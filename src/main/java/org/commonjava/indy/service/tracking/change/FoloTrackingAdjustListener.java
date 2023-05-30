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
import org.commonjava.event.promote.PathsPromoteCompleteEvent;
import org.commonjava.indy.service.tracking.config.IndyTrackingConfiguration;
import org.commonjava.indy.service.tracking.data.cassandra.CassandraTrackingQuery;
import org.commonjava.indy.service.tracking.model.StoreKey;
import org.commonjava.indy.service.tracking.model.StoreType;
import org.commonjava.indy.service.tracking.model.TrackedContent;
import org.commonjava.indy.service.tracking.model.TrackedContentEntry;
import org.commonjava.indy.service.tracking.model.TrackingKey;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Set;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class FoloTrackingAdjustListener
{

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Inject
    private IndyTrackingConfiguration trackingConfig;

    @Inject
    private CassandraTrackingQuery recordManager;

    @Blocking
    @Incoming( "promote-event-in" )
    public CompletionStage<Void> onPromoteComplete( Message<PathsPromoteCompleteEvent> message )
    {
        PathsPromoteCompleteEvent event = message.getPayload();
        logger.info( "Promote COMPLETE: {}", event );

        Set<String> paths = event.getCompletedPaths();
        if ( paths.isEmpty() )
        {
            logger.trace( "No completedPaths, skip adjust" );
            return message.ack();
        }

        StoreKey source = StoreKey.fromString( event.getSourceStore() );
        StoreKey target = StoreKey.fromString( event.getTargetStore() );

        TrackingKey trackingKey = getTrackingKey( source );
        if ( trackingKey == null )
        {
            logger.trace( "No tracking key found to: {}", source );
            return message.ack();
        }

        // Get the sealed record, client MUST seal the record before promote
        TrackedContent trackedContent = recordManager.get( trackingKey );
        if ( trackedContent == null )
        {
            logger.trace( "No sealed record found, trackingKey: {}", trackingKey );
            return message.ack();
        }

        adjustTrackedContent( trackedContent, target );

        recordManager.replaceTrackingRecord( trackedContent );
        return message.ack();
    }

    private void adjustTrackedContent( TrackedContent trackedContent, StoreKey target )
    {
        Set<TrackedContentEntry> uploads = trackedContent.getUploads();
        uploads.forEach( entry -> {
            entry.setStoreKey( target );
        } );
    }

    private TrackingKey getTrackingKey( StoreKey source )
    {
        if ( source.getType() == StoreType.hosted )
        {
            return new TrackingKey( source.getName() );
        }
        else
        {
            /* TODO: For remote, we can not get the tracking id by solely source repo name.
             * E.g., we promote ant from { "storeKey" : "maven:remote:central", "path" : "/ant/ant-launcher/1.6.5/ant-launcher-1.6.5.jar" }
             * into some hosted repo shared-imports, we really need to adjust all of those tracking records.
             *
             * One workaround is not to promote any remote repo artifact to hosted, or promote it with purse as false so the original
             * paths were still valid for a reproducer build.
             */
            return null;
        }
    }

}
