/**
 * Copyright (C) 2022-2023 Red Hat, Inc. (https://github.com/Commonjava/indy-tracking-service)
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
package org.commonjava.indy.service.tracking.jaxrs;

import org.commonjava.indy.service.tracking.Constants;
import org.commonjava.indy.service.tracking.client.content.BatchDeleteRequest;
import org.commonjava.indy.service.tracking.client.content.MaintenanceService;
import org.commonjava.indy.service.tracking.config.IndyTrackingConfiguration;
import org.commonjava.indy.service.tracking.controller.AdminController;
import org.commonjava.indy.service.tracking.exception.ContentException;
import org.commonjava.indy.service.tracking.exception.IndyWorkflowException;
import org.commonjava.indy.service.tracking.model.AccessChannel;
import org.commonjava.indy.service.tracking.model.StoreEffect;
import org.commonjava.indy.service.tracking.model.StoreKey;
import org.commonjava.indy.service.tracking.model.StoreType;
import org.commonjava.indy.service.tracking.model.TrackedContentEntry;
import org.commonjava.indy.service.tracking.model.TrackingKey;
import org.commonjava.indy.service.tracking.model.dto.TrackedContentDTO;
import org.commonjava.indy.service.tracking.model.dto.TrackedContentEntryDTO;
import org.commonjava.indy.service.tracking.model.dto.TrackingIdsDTO;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.commonjava.indy.service.tracking.Constants.ALL;
import static org.commonjava.indy.service.tracking.Constants.LEGACY;
import static org.commonjava.indy.service.tracking.Constants.TRACKING_TYPE.IN_PROGRESS;
import static org.commonjava.indy.service.tracking.Constants.TRACKING_TYPE.SEALED;
import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.PATH;
import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;

@Tag( name = "Tracking Record Access", description = "Manages tracking records." )
@Path( "/api/folo/admin" )
@ApplicationScoped
public class AdminResource
{
    public static final String MEDIATYPE_APPLICATION_ZIP = "application/zip";

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Inject
    @RestClient
    MaintenanceService maintenanceService;

    @Inject
    private AdminController controller;

    @Inject
    private ResponseHelper responseHelper;

    @Inject
    private IndyTrackingConfiguration config;

    public AdminResource()
    {
    }

    @Operation( description = "Recalculate sizes and checksums for every file listed in a tracking record." )
    @APIResponse( responseCode = "200", description = "Recalculated tracking report" )
    @APIResponse( responseCode = "404", description = "No such tracking record found" )
    @Path( "/{id}/record/recalculate" )
    @GET
    public Response recalculateRecord(
                    @Parameter( description = "User-assigned tracking session key", in = PATH, required = true ) @PathParam( "id" ) final String id,
                    @Context final UriInfo uriInfo )
    {
        Response response;
        try
        {
            final String baseUrl = config.contentServiceURL();
            final TrackedContentDTO report = controller.recalculateRecord( id, baseUrl );

            if ( report == null )
            {
                response = Response.status( Response.Status.NOT_FOUND ).build();
            }
            else
            {
                response = responseHelper.formatOkResponseWithJsonEntity( report );
            }
        }
        catch ( final IndyWorkflowException e )
        {
            logger.error( String.format( "Failed to serialize tracking report for: %s. Reason: %s", id,
                                         e.getMessage() ), e );

            response = responseHelper.formatResponse( e );
        }

        return response;
    }

    @Operation( description = "Retrieve the content referenced in a tracking record as a ZIP-compressed Maven repository directory." )
    @APIResponse( responseCode = "200", content = @Content( schema = @Schema( implementation = File.class ) ), description = "ZIP repository content" )
    @APIResponse( responseCode = "404", description = "No such tracking record" )
    @Path( "/{id}/record/zip" )
    @GET
    @Produces( MEDIATYPE_APPLICATION_ZIP )
    public File getZipRepository(
                    @Parameter( description = "User-assigned tracking session key", in = PATH, required = true ) @PathParam( "id" ) final String id )
    {
        return controller.getZipRepository( id );
    }

    @Operation( description = "Alias of /{id}/record, returns the tracking record for the specified key" )
    @APIResponse( responseCode = "200", description = "Tracking record" )
    @APIResponse( responseCode = "404", description = "No such tracking record" )
    @Path( "/{id}/report" )
    @GET
    public Response getReport(
                    @Parameter( description = "User-assigned tracking session key", in = PATH, required = true ) @PathParam( "id" ) final String id,
                    @Context final UriInfo uriInfo )
    {
        return getRecord( id, uriInfo );
    }

    @Operation( description = "Explicitly setup a new tracking record for the specified key, to prevent 404 if the record is never used." )
    @APIResponse( responseCode = "201", description = "Tracking record was created" )
    @Path( "/{id}/record" )
    @PUT
    public Response initRecord(
            @Parameter( description = "User-assigned tracking session key", in = PATH, required = true )
            @PathParam( "id" ) final String id, @Context final UriInfo uriInfo )
    {
        Response.ResponseBuilder rb = Response.created( uriInfo.getRequestUri() );
        return rb.build();
    }

    @Operation( description = "Get record for single tracking content artifact" )
    @Parameters( value = {
            @Parameter( name = "packageType", in = QUERY, description = "The package type of the repository.",
                        example = "maven, npm, generic-http", required = true ),
            @Parameter( name = "type", in = QUERY, description = "The type of the repository.",
                        example = "group, remote, hosted", required = true ) } )
    @APIResponse( responseCode = "200", description = "Tracking record" )
    @APIResponse( responseCode = "404", description = "No such tracking record" )
    @Path( "/{id}/artifactRecord/{path: (.*)}" )
    @GET
    public Response recordArtifact( @Parameter( in = PATH, required = true ) @PathParam( "id" ) final String id,
                                    @Parameter( in = PATH, required = true ) @PathParam( "path" ) String path,
                                    @Parameter( in = QUERY, required = true ) @QueryParam( "packageType" )
                                    String packageType,
                                    @Parameter( in = QUERY, required = true ) @QueryParam( "type" ) String type,
                                    @Parameter( in = QUERY, required = true ) @QueryParam( "name" ) String name,
                                    @QueryParam( "originalUrl" ) String originalUrl, @QueryParam( "size" ) long size,
                                    @QueryParam( "md5" ) String md5, @QueryParam( "sha1" ) String sha1,
                                    @QueryParam( "sha256" ) String sha256, @Context final UriInfo uriInfo )
    {
        final StoreType st = StoreType.get( type );
        if ( null == packageType || null == type || null == st )
        {
            logger.error( "Unsupported package type: {} or unsupported repo type: {} for path: {}, tracking id: {}.",
                          packageType, type, path, id );
            return Response.status( Response.Status.NOT_FOUND ).build();
        }
        TrackedContentEntry contentEntry =
                new TrackedContentEntry( new TrackingKey( id ), new StoreKey( packageType, st, name ),
                                         AccessChannel.NATIVE, originalUrl, path, StoreEffect.DOWNLOAD, size, md5, sha1,
                                         sha256 );
        Boolean result = controller.recordArtifact( contentEntry );
        if ( result )
        {
            logger.debug( "Done for path {}, id {} artifact record.", path, id );
            return Response.ok().build();
        }
        return Response.status( Response.Status.NOT_FOUND ).build();
    }

    @Operation( description = "Seal the tracking record for the specified key, to prevent further content logging" )
    @APIResponse( responseCode = "200", description = "Tracking record" )
    @APIResponse( responseCode = "404", description = "No such tracking record" )
    @Path( "/{id}/record" )
    @POST
    public Response sealRecord(
                    @Parameter( description = "User-assigned tracking session key", in = PATH, required = true ) @PathParam( "id" ) final String id,
                    @Context final UriInfo uriInfo )
    {
        final String baseUrl = config.contentServiceURL();
        TrackedContentDTO record = controller.seal( id, baseUrl );
        if ( record == null )
        {
            return Response.status( Response.Status.NOT_FOUND ).build();
        }
        else
        {
            return Response.ok().build();
        }
    }

    @Operation( description = "Alias of /{id}/record, returns the tracking record for the specified key" )
    @APIResponse( responseCode = "200", description = "Tracking record" )
    @APIResponse( responseCode = "404", description = "No such tracking record" )
    @Path( "/{id}/record" )
    @GET
    public Response getRecord(
                    @Parameter( description = "User-assigned tracking session key", in = PATH, required = true ) @PathParam( "id" ) final String id,
                    @Context final UriInfo uriInfo )
    {
        Response response;
        try
        {
            final String baseUrl = config.contentServiceURL();
            TrackedContentDTO record = controller.getRecord( id, baseUrl );
            if ( record == null )
            {
                record = controller.getLegacyRecord( id, baseUrl ); // Try legacy record
            }
            if ( record == null )
            {
                // if not found, return an empty report
                record = new TrackedContentDTO( new TrackingKey( id ), emptySet(), emptySet() );
            }
            response = responseHelper.formatOkResponseWithJsonEntity( record );
        }
        catch ( final IndyWorkflowException e )
        {
            logger.error( String.format( "Failed to retrieve tracking report for: %s. Reason: %s", id, e.getMessage() ),
                          e );

            response = responseHelper.formatResponse( e );
        }

        return response;
    }

    @Operation( description = "Delete the tracking record for the specified key" )
    @Path( "/{id}/record" )
    @DELETE
    public Response clearRecord(
                    @Parameter( description = "User-assigned tracking session key", in = PATH, required = true ) @PathParam( "id" ) final String id )
    {
        Response response;
        try
        {
            controller.clearRecord( id );
            response = Response.status( Response.Status.NO_CONTENT ).build();
        }
        catch ( ContentException e )
        {
            response = responseHelper.formatResponse( e );
        }

        return response;
    }

    @Operation( description = "Retrieve tracking ids for records of given type." )
    @APIResponse( responseCode = "200", content = @Content( schema = @Schema( implementation = List.class ) ), description = "tracking ids with sealed or in_progress" )
    @APIResponse( responseCode = "404", description = "No ids found for type" )
    @Path( "/report/ids/{type}" )
    @GET
    public Response getRecordIds(
                    @Parameter( description = "Report type, should be in_progress|sealed|all|legacy", in = PATH, required = true ) @PathParam( "type" ) final String type )
    {
        Response response;
        TrackingIdsDTO ids;
        if ( LEGACY.equals( type ) )
        {
            ids = controller.getLegacyTrackingIds();
        }
        else
        {
            Set<Constants.TRACKING_TYPE> types = getRequiredTypes( type );
            ids = controller.getTrackingIds( types );
        }
        if ( ids != null )
        {
            response = responseHelper.formatOkResponseWithJsonEntity( ids );
        }
        else
        {
            response = Response.status( Response.Status.NOT_FOUND ).build();
        }

        return response;
    }

    private Set<Constants.TRACKING_TYPE> getRequiredTypes( String type )
    {
        Set<Constants.TRACKING_TYPE> types = new HashSet<>();

        if ( IN_PROGRESS.getValue().equals( type ) || ALL.equals( type ) )
        {
            types.add( IN_PROGRESS );
        }
        if ( SEALED.getValue().equals( type ) || ALL.equals( type ) )
        {
            types.add( SEALED );
        }
        return types;
    }

    @Operation( description = "Export the records as a ZIP file." )
    @APIResponse( responseCode = "200", description = "ZIP content" )
    @Path( "/report/export" )
    @GET
    @Produces( MEDIATYPE_APPLICATION_ZIP )
    public File exportReport()
    {
        try
        {
            return controller.renderReportZip();
        }
        catch ( IndyWorkflowException e )
        {
            responseHelper.throwError( e );
        }

        return null;
    }

    @Operation( description = "Import records from a ZIP file." )
    @APIResponse( responseCode = "201", description = "Import ZIP content" )
    @Path( "/report/import" )
    @PUT
    public Response importReport( final @Context UriInfo uriInfo, final @Context HttpServletRequest request )
    {
        try
        {
            controller.importRecordZip( request.getInputStream() );
        }
        catch ( IndyWorkflowException e )
        {
            responseHelper.throwError( e );
        }
        catch ( IOException e )
        {
            responseHelper.throwError( new IndyWorkflowException( "IO error", e ) );
        }

        return Response.created( uriInfo.getRequestUri() ).build();
    }

    @Operation( description = "Batch delete files uploaded through FOLO trackingID under the given storeKey." )
    @APIResponse( responseCode = "200", description = "Batch delete operation finished." )
    @RequestBody( description = "JSON object, specifying trackingID and storeKey, with other configuration options", name = "body", required = true )
    @Path( "/batch/delete" )
    @POST
    @Consumes( APPLICATION_JSON )
    @Produces( APPLICATION_JSON )
    public Response doDelete( @Context final UriInfo uriInfo, final BatchDeleteRequest request )
    {
        String trackingID = request.getTrackingID();

        if ( trackingID == null || request.getStoreKey() == null )
        {
            Response.ResponseBuilder builder = Response.status( 400 );
            return builder.build();
        }

        if ( !controller.deletionAdditionalGuardCheck(request) )
        {
            Response.ResponseBuilder builder = Response.status( 400 );
            return builder.build();
        }

        if ( request.getPaths() == null || request.getPaths().isEmpty() )
        {
            final String baseUrl = config.contentServiceURL();
            try
            {
                final TrackedContentDTO record = controller.getRecord( trackingID, baseUrl );
                if ( record == null || record.getUploads().isEmpty() )
                {
                    Response.ResponseBuilder builder = Response.status( 400 );
                    return builder.build();
                }

                Set<String> paths = new HashSet<>();
                for ( TrackedContentEntryDTO entry : record.getUploads() )
                {
                    paths.add( entry.getPath() );
                }
                request.setPaths( paths );
            }
            catch ( IndyWorkflowException e )
            {
                responseHelper.throwError( e );
            }
        }

        return maintenanceService.doDelete( request );
    }

}