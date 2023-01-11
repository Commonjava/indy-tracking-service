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
package org.commonjava.indy.service.tracking.jaxrs;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.PATH;

@Tag(name = "Tracking Record Access", description = "Manages tracking records.")
@Path("/api/folo/admin")
@ApplicationScoped
public class AdminResource {
    public static final String MEDIATYPE_APPLICATION_ZIP = "application/zip";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AdminResource() {
    }

    @Operation(description = "Recalculate sizes and checksums for every file listed in a tracking record.")
    @APIResponse(responseCode = "200", description = "Recalculated tracking report")
    @APIResponse(responseCode = "404", description = "No such tracking record found")
    @Path("/{id}/record/recalculate")
    @GET
    public Response recalculateRecord(@Parameter(description = "User-assigned tracking session key", in = PATH, required = true) @PathParam("id") final String id) {
        Response response;

        logger.info("id is: {}", id);

        response = Response.ok().build();
        return response;
    }

    @Operation(description = "Retrieve the content referenced in a tracking record as a ZIP-compressed Maven repository directory.")
    @APIResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = File.class)),
            description = "ZIP repository content")
    @APIResponse(responseCode = "404", description = "No such tracking record")
    @Path("/{id}/record/zip")
    @GET
    @Produces(MEDIATYPE_APPLICATION_ZIP)
    public Response getZipRepository(@Parameter(description = "User-assigned tracking session key", in = PATH, required = true) @PathParam("id") final String id) {
        Response response;

        logger.info("id is: {}", id);

        response = Response.ok().build();
        return response;
    }

    @Operation(description = "Alias of /{id}/record, returns the tracking record for the specified key")
    @APIResponse(responseCode = "200", description = "Tracking record")
    @APIResponse(responseCode = "404", description = "No such tracking record")
    @Path("/{id}/report")
    @GET
    public Response getReport(@Parameter(description = "User-assigned tracking session key", in = PATH, required = true) @PathParam("id") final String id) {
        Response response;

        logger.info("id is: {}", id);

        response = Response.ok().build();
        return response;
    }

    @Operation(description = "Explicitly setup a new tracking record for the specified key, to prevent 404 if the record is never used.")
    @APIResponse(responseCode = "201", description = "Tracking record was created")
    @Path("/{id}/record")
    @PUT
    public Response initRecord(@Parameter(description = "User-assigned tracking session key", in = PATH, required = true) @PathParam("id") final String id) {
        Response response;

        logger.info("id is: {}", id);

        response = Response.ok().build();
        return response;
    }

    @Operation(description = "Seal the tracking record for the specified key, to prevent further content logging")
    @APIResponse(responseCode = "200", description = "Tracking record")
    @APIResponse(responseCode = "404", description = "No such tracking record")
    @Path("/{id}/record")
    @POST
    public Response sealRecord(@Parameter(description = "User-assigned tracking session key", in = PATH, required = true) @PathParam("id") final String id) {
        Response response;

        logger.info("id is: {}", id);

        response = Response.ok().build();
        return response;
    }

    @Operation(description = "Alias of /{id}/record, returns the tracking record for the specified key")
    @APIResponse(responseCode = "200", description = "Tracking record")
    @APIResponse(responseCode = "404", description = "No such tracking record")
    @Path("/{id}/record")
    @GET
    public Response getRecord(@Parameter(description = "User-assigned tracking session key", in = PATH, required = true) @PathParam("id") final String id) {
        Response response;

        logger.info("id is: {}", id);

        response = Response.ok().build();
        return response;
    }

    @Operation(description = "Delete the tracking record for the specified key")
    @Path("/{id}/record")
    @DELETE
    public Response clearRecord(@Parameter(description = "User-assigned tracking session key", in = PATH, required = true) @PathParam("id") final String id) {
        Response response;

        logger.info("id is: {}", id);

        response = Response.ok().build();
        return response;
    }

    @Operation(description = "Retrieve tracking ids for records of given type.")
    @APIResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = List.class)),
            description = "tracking ids with sealed or in_progress")
    @APIResponse(responseCode = "404", description = "No ids found for type")
    @Path("/report/ids/{type}")
    @GET
    public Response getRecordIds(@Parameter(description = "Report type, should be in_progress|sealed|all|legacy", in = PATH, required = true) @PathParam("type") final String type) {
        Response response;

        logger.info("type is: {}", type);

        response = Response.ok().build();
        return response;
    }

    @Operation(description = "Export the records as a ZIP file.")
    @APIResponse(responseCode = "200", description = "ZIP content")
    @Path("/report/export")
    @GET
    @Produces(MEDIATYPE_APPLICATION_ZIP)
    public Response exportReport() {
        Response response;

        response = Response.ok().build();
        return response;
    }

    @Operation(description = "Import records from a ZIP file.")
    @APIResponse(responseCode = "201", description = "Import ZIP content")
    @Path("/report/export")
    @PUT
    public Response importReport(final @Context UriInfo uriInfo, final @Context HttpServletRequest request) {
        return Response.created(uriInfo.getRequestUri()).build();
    }

    @Operation(description = "Batch delete files uploaded through FOLO trackingID under the given storeKey.")
    @APIResponse(responseCode = "200", description = "Batch delete operation finished.")
    @RequestBody(description = "JSON object, specifying trackingID and storeKey, with other configuration options",
            name = "body", required = true)
    @Path("/batch/delete")
    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response doDelete(@Context final UriInfo uriInfo, @Context final HttpServletRequest request) {
        return Response.created(uriInfo.getRequestUri()).build();
    }

    @Operation(description = "Import folo from ISPN cache to Cassandra.")
    @APIResponse(responseCode = "201", description = "Import folo from ISPN cache to Cassandra.")
    @Path("/report/importToCassandra")
    @PUT
    public Response importFoloToCassandra(final @Context UriInfo uriInfo, final @Context HttpServletRequest request) {
        return Response.created(uriInfo.getRequestUri()).build();
    }

}