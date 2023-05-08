/**
 * Copyright (C) 2023 Red Hat, Inc. (https://github.com/Commonjava/indy-tracking-service)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.commonjava.indy.service.tracking.change;

import io.smallrye.common.annotation.Blocking;
import org.apache.commons.lang3.StringUtils;
import org.commonjava.event.common.EventMetadata;
import org.commonjava.event.file.FileEvent;
import org.commonjava.event.file.FileEventType;
import org.commonjava.indy.service.tracking.Constants;
import org.commonjava.indy.service.tracking.config.IndyTrackingConfiguration;
import org.commonjava.indy.service.tracking.data.cassandra.CassandraTrackingQuery;
import org.commonjava.indy.service.tracking.exception.ContentException;
import org.commonjava.indy.service.tracking.exception.IndyWorkflowException;
import org.commonjava.indy.service.tracking.model.AccessChannel;
import org.commonjava.indy.service.tracking.model.StoreKey;
import org.commonjava.indy.service.tracking.model.TrackedContentEntry;
import org.commonjava.indy.service.tracking.model.TrackingKey;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Objects;

import static org.commonjava.indy.service.tracking.Constants.ORIGIN_PATH;
import static org.commonjava.indy.service.tracking.model.StoreEffect.DOWNLOAD;
import static org.commonjava.indy.service.tracking.model.StoreEffect.UPLOAD;
import static org.commonjava.indy.service.tracking.model.StoreType.group;

@ApplicationScoped
public class FoloTrackingListener
{

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Inject
    private IndyTrackingConfiguration trackingConfig;

    @Inject
    private CassandraTrackingQuery recordManager;

    public void handleFileAccessEvent( final FileEvent event )
    {
        logger.trace( "FILE ACCESS: {}", event );

        EventMetadata metadata = event.getEventMetadata();

        final String originPath = (String) metadata.get( ORIGIN_PATH );
        /*
         * If a build makes a request to indy-admin service, pnc sends it through generic proxy
         * where it gets tracked once and then it gets tracked second time on the indy-admin service.
         * We should avoid the tracker if it sends request through generic proxy and the target is
         * indy instance,
         */
        if ( originPath != null && originPath.contains( "api/folo/track" ) )
        {
            logger.trace( "NOT tracking content requests from indy itself, path: {}", originPath );
            return;
        }

        String trackingId = event.getSessionId();

        final TrackingKey trackingKey = StringUtils.isNotBlank( trackingId ) ? new TrackingKey( trackingId ) : null;
        if ( trackingKey == null )
        {
            logger.trace( "No tracking key for access to: {}", event.getTargetPath() );
            return;
        }
        final AccessChannel accessChannel = AccessChannel.valueOf( (String) metadata.get( Constants.ACCESS_CHANNEL ) );

        try
        {
            StoreKey storeKey = StoreKey.fromString( event.getStoreKey() );
            if ( !trackingConfig.trackGroupContent() && storeKey.getType() == group )
            {
                logger.trace( "NOT tracking content stored directly in group: {}. This content is generally aggregated metadata, and can be recalculated. Groups may not be stable in some build environments",
                              storeKey );
                return;
            }

            final String trackingPath = originPath == null ? event.getTargetPath() : originPath;

            logger.trace( "Tracking report: {} += {} in {} (DOWNLOAD)", trackingKey, trackingPath, storeKey );

            //Here we need to think about npm metadata retrieving case. As almost all npm metadata retrieving is through
            // /$pkg from remote, but we use STORAGE_PATH in EventMetadata with /$pkg/package.json to store this metadata,
            //so the real path for this transfer should be /$pkg but its current path is /$pkg/package.json. We need to
            //think about if need to do the replacement here, especially for the originalUrl.
            TrackedContentEntry entry =
                            new TrackedContentEntry( trackingKey, storeKey, accessChannel, event.getSourceLocation(),
                                                     trackingPath, DOWNLOAD, event.getSize(), event.getMd5(),
                                                     event.getSha1(), event.getChecksum() );
            recordManager.recordArtifact( entry );
        }
        catch ( final ContentException | IndyWorkflowException e )
        {
            logger.error( String.format( "Failed to record download: %s. Reason: %s", event.getSourcePath(),
                                         e.getMessage() ), e );
        }
    }

    public void handleFileStorageEvent( final FileEvent event )
    {
        logger.trace( "FILE STORAGE: {}", event );

        EventMetadata metadata = event.getEventMetadata();

        logger.warn( ">>> Metadata: " + metadata );

        String trackingId = event.getSessionId();

        final TrackingKey trackingKey = StringUtils.isNotBlank( trackingId ) ? new TrackingKey( trackingId ) : null;
        if ( trackingKey == null )
        {
            logger.trace( "No tracking key. Not recording." );
            return;
        }
        final AccessChannel accessChannel = AccessChannel.valueOf( (String) metadata.get( Constants.ACCESS_CHANNEL ) );
        StoreKey storeKey = StoreKey.fromString( event.getStoreKey() );

        if ( !trackingConfig.trackGroupContent() && storeKey.getType() == group )
        {
            logger.trace( "NOT tracking content stored directly in group: {}. This content is generally aggregated metadata, and can be recalculated. Groups may not be stable in some build environments",
                          storeKey );
            return;
        }

        try
        {

            logger.trace( "Tracking report: {} += {} in {} ({})", trackingKey, event.getTargetPath(), storeKey,
                          UPLOAD );

            TrackedContentEntry entry =
                            new TrackedContentEntry( trackingKey, storeKey, accessChannel, event.getSourceLocation(),
                                                     event.getTargetPath(), DOWNLOAD, event.getSize(), event.getMd5(),
                                                     event.getSha1(), event.getChecksum() );

            recordManager.recordArtifact( entry );
        }
        catch ( final ContentException | IndyWorkflowException e )
        {
            logger.error( String.format( "Failed to record download: %s. Reason: %s", event.getSourcePath(),
                                         e.getMessage() ), e );
        }
    }

    @Blocking
    @Incoming( "file-event-in" )
    public void handleFileEvent( FileEvent event )
    {
        if ( event.getEventType().equals( FileEventType.ACCESS ) )
        {
            handleFileAccessEvent( event );
        }
        else if ( event.getEventType().equals( FileEventType.STORAGE ) )
        {
            handleFileStorageEvent( event );
        }

    }

}
