/**
 * Copyright (C) 2023 Red Hat, Inc. (https://github.com/Commonjava/indy-tracking-service)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.commonjava.indy.service.tracking.controller;

import org.commonjava.indy.service.tracking.Constants;
import org.commonjava.indy.service.tracking.client.content.ContentService;
import org.commonjava.indy.service.tracking.config.IndyTrackingConfiguration;
import org.commonjava.indy.service.tracking.data.cassandra.CassandraTrackingQuery;
import org.commonjava.indy.service.tracking.exception.ContentException;
import org.commonjava.indy.service.tracking.exception.IndyWorkflowException;
import org.commonjava.indy.service.tracking.model.TrackedContent;
import org.commonjava.indy.service.tracking.model.TrackedContentEntry;
import org.commonjava.indy.service.tracking.model.TrackingKey;
import org.commonjava.indy.service.tracking.model.dto.TrackedContentDTO;
import org.commonjava.indy.service.tracking.model.dto.TrackedContentEntryDTO;
import org.commonjava.indy.service.tracking.model.dto.TrackedContentEntrySetDTO;
import org.commonjava.indy.service.tracking.model.dto.TrackingIdsDTO;
import org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO;
import org.commonjava.indy.service.tracking.util.UrlUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.commonjava.indy.service.tracking.util.TrackingUtils.readZipInputStreamAnd;
import static org.commonjava.indy.service.tracking.util.TrackingUtils.zipTrackedContent;

@ApplicationScoped
public class AdminController
{
    public static final String FOLO_DIR = "folo";

    public static final String FOLO_SEALED_ZIP = "folo-sealed.zip";

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Inject
    @RestClient
    ContentService contentService;

    @Inject
    private IndyTrackingConfiguration config;

    @Inject
    private CassandraTrackingQuery recordManager;

    protected AdminController()
    {
    }

    public AdminController( final CassandraTrackingQuery recordManager )
    {
        this.recordManager = recordManager;
    }

    public TrackedContentDTO seal( final String id, final String baseUrl )
    {
        TrackingKey tk = new TrackingKey( id );
        return constructContentDTO( recordManager.seal( tk ), baseUrl );
    }

    public void importRecordZip( InputStream stream ) throws IndyWorkflowException
    {
        try
        {
            int count = readZipInputStreamAnd( stream, ( record ) -> recordManager.addSealedRecord( record ) );
            logger.debug( "Import records done, size: {}", count );
        }
        catch ( Exception e )
        {
            throw new IndyWorkflowException( "Failed to import zip file", e );
        }
    }

    public File renderReportZip() throws IndyWorkflowException
    {
        Set<TrackedContent> sealed = recordManager.getSealed(); // only care about sealed records
        try
        {
            File file = Paths.get( config.baseDir().getAbsolutePath(), FOLO_DIR, FOLO_SEALED_ZIP ).toFile();
            if ( file.exists() )
            {
                file.delete();
            }
            file.getParentFile().mkdirs(); // make dirs if not exist

            zipTrackedContent( file, sealed );

            return file;
        }
        catch ( IOException e )
        {
            throw new IndyWorkflowException( "Failed to create zip file", e );
        }
    }

    public TrackedContentDTO getRecord( final String id, String baseUrl ) throws IndyWorkflowException
    {
        final TrackingKey tk = new TrackingKey( id );
        return constructContentDTO( recordManager.get( tk ), baseUrl );
    }

    public TrackedContentDTO getLegacyRecord( final String id, String baseUrl ) throws IndyWorkflowException
    {
        final TrackingKey tk = new TrackingKey( id );
        return constructContentDTO( recordManager.getLegacy( tk ), baseUrl );
    }

    public void clearRecord( final String id ) throws ContentException
    {
        final TrackingKey tk = new TrackingKey( id );
        recordManager.delete( tk );
    }

    private TrackedContentDTO constructContentDTO( final TrackedContent content, final String baseUrl )
    {
        if ( content == null )
        {
            return null;
        }
        final Set<TrackedContentEntryDTO> uploads = new TreeSet<>();
        for ( TrackedContentEntry entry : content.getUploads() )
        {
            uploads.add( constructContentEntryDTO( entry, baseUrl ) );
        }

        final Set<TrackedContentEntryDTO> downloads = new TreeSet<>();
        for ( TrackedContentEntry entry : content.getDownloads() )
        {
            downloads.add( constructContentEntryDTO( entry, baseUrl ) );
        }
        return new TrackedContentDTO( content.getKey(), uploads, downloads );
    }

    private TrackedContentEntryDTO constructContentEntryDTO( final TrackedContentEntry entry, String apiBaseUrl )
    {
        if ( entry == null )
        {
            return null;
        }
        TrackedContentEntryDTO entryDTO =
                        new TrackedContentEntryDTO( entry.getStoreKey(), entry.getAccessChannel(), entry.getPath() );

        try
        {
            entryDTO.setLocalUrl( UrlUtils.buildUrl( apiBaseUrl, "content", entryDTO.getStoreKey().getPackageType(),
                                                     entryDTO.getStoreKey().getType().singularEndpointName(),
                                                     entryDTO.getStoreKey().getName(), entryDTO.getPath() ) );
        }
        catch ( MalformedURLException e )
        {
            logger.warn( String.format( "Cannot formulate local URL!\n  Base URL: %s"
                                                        + "\n  Store: %s\n  Path: %s\n  Record: %s\n  Reason: %s",
                                        apiBaseUrl, entry.getStoreKey(), entry.getPath(), entry.getTrackingKey(),
                                        e.getMessage() ), e );
        }

        entryDTO.setOriginUrl( entry.getOriginUrl() );
        entryDTO.setMd5( entry.getMd5() );
        entryDTO.setSha1( entry.getSha1() );
        entryDTO.setSha256( entry.getSha256() );
        entryDTO.setSize( entry.getSize() );
        entryDTO.setTimestamps( entry.getTimestamps() );
        return entryDTO;
    }

    private Set<ContentTransferDTO> constructTransferDTOSet( final Set<TrackedContentEntry> entries )
    {
        if ( entries == null )
        {
            return null;
        }
        Set<ContentTransferDTO> cut_entries = new HashSet<>();
        for ( TrackedContentEntry entry : entries )
        {
            ContentTransferDTO cut_entry =
                            new ContentTransferDTO( entry.getStoreKey(), entry.getTrackingKey(), entry.getAccessChannel(),
                                                    entry.getPath(), entry.getOriginUrl(), entry.getEffect() );
            cut_entries.add( cut_entry );
        }

        return cut_entries;
    }

    public TrackingIdsDTO getLegacyTrackingIds()
    {
        logger.info( "Get legacy folo ids" );
        TrackingIdsDTO ret = null;
        Set<String> sealed = recordManager.getLegacyTrackingKeys()
                                          .stream()
                                          .map( TrackingKey::getId )
                                          .collect( Collectors.toSet() );
        if ( sealed != null )
        {
            ret = new TrackingIdsDTO();
            ret.setSealed( sealed );
        }
        return ret;
    }

    public TrackingIdsDTO getTrackingIds( final Set<Constants.TRACKING_TYPE> types )
    {

        Set<String> inProgress = null;
        if ( types.contains( Constants.TRACKING_TYPE.IN_PROGRESS ) )
        {
            inProgress = recordManager.getInProgressTrackingKey()
                                      .stream()
                                      .map( TrackingKey::getId )
                                      .collect( Collectors.toSet() );
        }

        Set<String> sealed = null;
        if ( types.contains( Constants.TRACKING_TYPE.SEALED ) )
        {
            sealed = recordManager.getSealedTrackingKey()
                                  .stream()
                                  .map( TrackingKey::getId )
                                  .collect( Collectors.toSet() );
        }

        if ( ( inProgress != null && !inProgress.isEmpty() ) || ( sealed != null && !sealed.isEmpty() ) )
        {
            return new TrackingIdsDTO( inProgress, sealed );
        }
        return null;
    }

    public TrackedContentDTO recalculateRecord( final String id, final String baseUrl ) throws IndyWorkflowException
    {
        TrackingKey trackingKey = new TrackingKey( id );
        TrackedContent record = recordManager.get( trackingKey );

        AtomicBoolean failed = new AtomicBoolean( false );

        Set<TrackedContentEntry> recalculatedUploads = recalculateEntrySet( record.getUploads(), failed );
        Set<TrackedContentEntry> recalculatedDownloads = null;
        if ( !failed.get() )
        {
            recalculatedDownloads = recalculateEntrySet( record.getDownloads(), failed );
        }

        if ( failed.get() )
        {
            throw new IndyWorkflowException(
                            "Failed to recalculate tracking record: %s. See Indy logs for more information", id );
        }

        TrackedContent recalculated = new TrackedContent( record.getKey(), recalculatedUploads, recalculatedDownloads );
        recordManager.replaceTrackingRecord( recalculated );

        return constructContentDTO( recalculated, baseUrl );
    }

    private Set<TrackedContentEntry> recalculateEntrySet( final Set<TrackedContentEntry> entries,
                                                          final AtomicBoolean failed )
    {
        Set<ContentTransferDTO> transfer_entries = constructTransferDTOSet( entries );
        try (Response response = contentService.recalculateEntrySet( transfer_entries ))
        {
            return response.readEntity( TrackedContentEntrySetDTO.class ).entries;
        }
        catch ( Exception e )
        {
            failed.set( true );
            return null;
        }
    }

}
