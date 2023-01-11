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
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
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

import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.PATH;

@Tag(name = "FOLO Tracked Content Access and Storage. Tracks retrieval and management of file/artifact content.")
@Path("/api/folo/track/{id}/maven/{type: (hosted|group|remote)}/{name}")
@ApplicationScoped
public class MavenContentAccessResource {
    public static final String CHECK_CACHE_ONLY = "cache-only";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Operation(description = "Store and track file/artifact content under the given artifact store (type/name) and path.")
    @APIResponse(responseCode = "201", description = "Content was stored successfully")
    @APIResponse(responseCode = "400", description = "No appropriate storage location was found in the specified store (this store, or a member if a group is specified).")
    @Path("/{path: (.*)}")
    @PUT
    public Response doCreate(@Parameter(description = "User-assigned tracking session key", in = PATH, required = true) final @PathParam("id") String id,
                             final @Parameter(in = PATH,
                                     schema = @Schema(
                                             enumeration = {
                                                     "hosted",
                                                     "group",
                                                     "remote"}),
                                     required = true) @PathParam("type") String type,
                             @Parameter(in = PATH, required = true) @PathParam("name") final String name,
                             @PathParam("path") final String path, @Context final HttpServletRequest request,
                             @Context final UriInfo uriInfo) {
        Response response;

        logger.info("tracking id: {} type: {} name: {} path: {}", id, type, name, path);

        response = Response.ok().build();
        return response;
    }

    @Operation(description = "Store and track file/artifact content under the given artifact store (type/name) and path.")
    @APIResponse(responseCode = "200", description = "Header metadata for content")
    @APIResponse(responseCode = "404", description = "Content is not available")
    @Path("/{path: (.*)}")
    @HEAD
    public Response doHead(@Parameter(description = "User-assigned tracking session key", in = PATH, required = true) final @PathParam("id") String id,
                           final @Parameter(in = PATH,
                                   schema = @Schema(
                                           enumeration = {
                                                   "hosted",
                                                   "group",
                                                   "remote"}),
                                   required = true) @PathParam("type") String type,
                           @Parameter(in = PATH, required = true) @PathParam("name") final String name,
                           @PathParam("path") final String path,
                           @QueryParam(CHECK_CACHE_ONLY) final Boolean cacheOnly,
                           @Context final HttpServletRequest request,
                           @Context final UriInfo uriInfo) {
        Response response;

        logger.info("tracking id: {} type: {} name: {} path: {}", id, type, name, path);

        response = Response.ok().build();
        return response;
    }

    @Operation(description = "Retrieve and track file/artifact content under the given artifact store (type/name) and path.")
    @APIResponse(responseCode = "200", description = "Content stream")
    @APIResponse(responseCode = "404", description = "Content is not available")
    @Path("/{path: (.*)}")
    @GET
    public Response doGet(@Parameter(description = "User-assigned tracking session key", in = PATH, required = true) final @PathParam("id") String id,
                          final @Parameter(in = PATH,
                                  schema = @Schema(
                                          enumeration = {
                                                  "hosted",
                                                  "group",
                                                  "remote"}),
                                  required = true) @PathParam("type") String type,
                          @Parameter(in = PATH, required = true) @PathParam("name") final String name,
                          @PathParam("path") final String path,
                          @Context final HttpServletRequest request,
                          @Context final UriInfo uriInfo) {
        logger.info("tracking id: {} type: {} name: {} path: {}", id, type, name, path);

        return Response.created(uriInfo.getRequestUri()).build();
    }

}
