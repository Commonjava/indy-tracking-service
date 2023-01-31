package org.commonjava.indy.service.tracking.controller;

import org.commonjava.indy.service.tracking.Constants;
import org.commonjava.indy.service.tracking.config.IndyTrackingConfiguration;
import org.commonjava.indy.service.tracking.data.cassandra.CassandraTrackingQuery;
import org.commonjava.indy.service.tracking.exception.ContentException;
import org.commonjava.indy.service.tracking.exception.IndyWorkflowException;
import org.commonjava.indy.service.tracking.model.TrackedContent;
import org.commonjava.indy.service.tracking.model.TrackedContentEntry;
import org.commonjava.indy.service.tracking.model.TrackingKey;
import org.commonjava.indy.service.tracking.model.dto.TrackedContentDTO;
import org.commonjava.indy.service.tracking.model.dto.TrackedContentEntryDTO;
import org.commonjava.indy.service.tracking.model.dto.TrackingIdsDTO;
import org.commonjava.indy.service.tracking.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.commonjava.indy.service.tracking.util.TrackingUtils.zipTrackedContent;

@ApplicationScoped
public class AdminController
{
    public static final String FOLO_DIR = "folo";

    public static final String FOLO_SEALED_ZIP = "folo-sealed.zip";

    private final Logger logger = LoggerFactory.getLogger( getClass() );

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

}
