package org.commonjava.indy.service.tracking.controller;

import org.commonjava.indy.service.tracking.data.cassandra.CassandraTrackingQuery;
import org.commonjava.indy.service.tracking.exception.IndyWorkflowException;
import org.commonjava.indy.service.tracking.model.TrackedContent;
import org.commonjava.indy.service.tracking.model.TrackedContentEntry;
import org.commonjava.indy.service.tracking.model.TrackingKey;
import org.commonjava.indy.service.tracking.model.dto.TrackedContentDTO;
import org.commonjava.indy.service.tracking.model.dto.TrackedContentEntryDTO;
import org.commonjava.indy.service.tracking.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.MalformedURLException;
import java.util.Set;
import java.util.TreeSet;

@ApplicationScoped
public class AdminController
{

    private final Logger logger = LoggerFactory.getLogger( getClass() );

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

}
