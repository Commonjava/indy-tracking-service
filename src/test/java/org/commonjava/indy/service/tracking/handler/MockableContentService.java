package org.commonjava.indy.service.tracking.handler;

import io.quarkus.test.Mock;
import org.apache.http.HttpStatus;
import org.commonjava.indy.service.tracking.client.content.ContentService;
import org.commonjava.indy.service.tracking.model.TrackedContentEntry;
import org.commonjava.indy.service.tracking.model.dto.TransferDTO;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Set;

@Mock
@RestClient
public class MockableContentService
                implements ContentService
{

    public final Logger logger = LoggerFactory.getLogger( getClass() );

    @Override
    public Response recalculateEntrySet( final Set<TransferDTO> entries )
    {
        Set<TrackedContentEntry> newEntries = new HashSet<>();
        newEntries.add( new TrackedContentEntry() );
        return Response.status( HttpStatus.SC_OK ).entity( newEntries ).build();
    }

}
