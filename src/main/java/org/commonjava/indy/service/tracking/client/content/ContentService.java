package org.commonjava.indy.service.tracking.client.content;

import org.commonjava.indy.service.tracking.client.CustomClientRequestFilter;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path( "/api/content/admin" )
@RegisterRestClient( configKey = "content-service-api" )
@RegisterProvider( CustomClientRequestFilter.class )
public interface ContentService
{
    @GET
    @Path( "/{id}/record/recalculate" )
    Response recalculateRecord( final @PathParam( "id" ) String id );

}