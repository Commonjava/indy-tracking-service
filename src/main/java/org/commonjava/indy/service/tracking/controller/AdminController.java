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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.commonjava.indy.service.tracking.Constants;
import org.commonjava.indy.service.tracking.client.content.BatchDeleteRequest;
import org.commonjava.indy.service.tracking.client.content.ContentService;
import org.commonjava.indy.service.tracking.client.promote.PathsPromoteTrackingRecords;
import org.commonjava.indy.service.tracking.client.promote.PromoteService;
import org.commonjava.indy.service.tracking.config.IndyTrackingConfiguration;
import org.commonjava.indy.service.tracking.data.cassandra.CassandraTrackingQuery;
import org.commonjava.indy.service.tracking.exception.ContentException;
import org.commonjava.indy.service.tracking.exception.IndyWorkflowException;
import org.commonjava.indy.service.tracking.jaxrs.DTOStreamingOutput;
import org.commonjava.indy.service.tracking.model.TrackedContent;
import org.commonjava.indy.service.tracking.model.TrackedContentEntry;
import org.commonjava.indy.service.tracking.model.TrackingKey;
import org.commonjava.indy.service.tracking.model.dto.ContentDTO;
import org.commonjava.indy.service.tracking.model.dto.ContentEntryDTO;
import org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO;
import org.commonjava.indy.service.tracking.model.dto.TrackedContentDTO;
import org.commonjava.indy.service.tracking.model.dto.TrackedContentEntryDTO;
import org.commonjava.indy.service.tracking.model.dto.TrackingIdsDTO;
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
import java.util.Map;
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
    @RestClient
    PromoteService promoteService;

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
            ContentTransferDTO cut_entry = new ContentTransferDTO( entry.getStoreKey(), entry.getTrackingKey(),
                                                                   entry.getAccessChannel(), entry.getPath(),
                                                                   entry.getOriginUrl(), entry.getEffect() );
            cut_entries.add( cut_entry );
        }

        return cut_entries;
    }

    private ContentDTO convertToContentDTO( final TrackedContent record )
    {
        ContentDTO dto = new ContentDTO();
        dto.setKey( record.getKey() );
        Set<ContentEntryDTO> uploads = new HashSet<>();
        Set<ContentEntryDTO> downloads = new HashSet<>();
        for ( TrackedContentEntry entry : record.getUploads() )
        {
            uploads.add( convertToCOntentEntryDTO( entry ) );
        }
        for ( TrackedContentEntry entry : record.getDownloads() )
        {
            downloads.add( convertToCOntentEntryDTO( entry ) );
        }
        dto.setUploads( uploads );
        dto.getDownloads( downloads );
        return dto;
    }

    private ContentEntryDTO convertToCOntentEntryDTO( TrackedContentEntry entry )
    {
        ContentEntryDTO entryDTO = new ContentEntryDTO();
        entryDTO.setStoreKey( entry.getStoreKey() );
        entryDTO.setPath( entry.getPath() );
        return entryDTO;
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

        if ( record == null )
        {
            return null;
        }

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
            return (Set<TrackedContentEntry>) response.readEntity( DTOStreamingOutput.class ).getDto();
        }
        catch ( Exception e )
        {
            failed.set( true );
            return null;
        }
    }

    public File getZipRepository( String id )
    {
        final TrackingKey tk = new TrackingKey( id );
        final TrackedContent record = recordManager.get( tk );
        ContentDTO dto = convertToContentDTO( record );
        return contentService.getZipRepository( dto );
    }

    public boolean recordArtifact( InputStream stream )
    {
        TrackedContentEntry entry;
        ObjectMapper mapper = new ObjectMapper();
        boolean isRecorded = false;
        try
        {
            entry = mapper.readValue( stream, TrackedContentEntry.class );
            if ( entry != null )
            {
                isRecorded = recordManager.recordArtifact( entry );
            }
        }
        catch ( IOException e )
        {
            logger.error( String.format( "Failed to record tracked entry. Reason: %s", e.getMessage() ), e );
        }
        catch ( final ContentException | IndyWorkflowException e )
        {
            logger.error( "Failed to read TrackedContentEntry from event payload.", e );
        }
        return isRecorded;
    }

    /**
     * Additional check for batch deletion. It retrieves the promotion record by the trackingID
     * to find the target store associated with the promotion. If the target store does not match given store,
     * return failed delete validation result.
     */
    public boolean deletionAdditionalGuardCheck(BatchDeleteRequest deleteRequest )
    {
        if ( !config.deletionAdditionalGuardCheck() )
        {
            return true; // as passed if guard check is not enabled
        }

        String trackingID = deleteRequest.getTrackingID();
        final String givenStore = deleteRequest.getStoreKey().toString();
        final AtomicBoolean isOk = new AtomicBoolean(false);
        try
        {
            Response resp = promoteService.getPromoteRecords( trackingID );
            if (!isSuccess(resp))
            {
                logger.warn( "Deletion guard check failed, status:" + resp.getStatus() );
                return false;
            }
            PathsPromoteTrackingRecords promoteTrackingRecords = resp.readEntity(PathsPromoteTrackingRecords.class);
            Map<String, PathsPromoteTrackingRecords.PathsPromoteResult> resultMap = promoteTrackingRecords.getResultMap();
            if ( resultMap != null )
            {
                resultMap.forEach( (k,v) -> {
                    if (v.getRequest().getTargetStore().equals(givenStore))
                    {
                        isOk.set(true); // set true if any match found
                    }
                });
            }
        }
        catch (Exception e)
        {
            logger.warn("Deletion guard check failed", e);
            return false;
        }
        logger.info("Deletion guard check, trackingID: {}, passed: {}", trackingID, isOk.get());
        return isOk.get();
    }

    private boolean isSuccess(Response resp) {
        return Response.Status.fromStatusCode(resp.getStatus()).getFamily()
                == Response.Status.Family.SUCCESSFUL;
    }
}
