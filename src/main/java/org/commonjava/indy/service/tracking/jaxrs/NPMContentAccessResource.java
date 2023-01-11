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
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import static org.commonjava.indy.service.tracking.jaxrs.MavenContentAccessResource.CHECK_CACHE_ONLY;
import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.PATH;

@Tag(name = "FOLO Tracked Content Access and Storage For NPM related artifacts. Tracks retrieval and management of file/artifact content.")
@Path("/api/folo/track/{id}/npm/{type: (hosted|group|remote)}/{name}")
@ApplicationScoped
public class NPMContentAccessResource {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Operation(description = "Store and track NPM file/artifact content under the given artifact store (type/name) and path.")
    @APIResponse(responseCode = "201", description = "Content was stored successfully")
    @APIResponse(responseCode = "400", description = "No appropriate storage location was found in the specified store (this store, or a member if a group is specified).")
    @Path("/{packageName}")
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
                             @PathParam("packageName") final String packageName,
                             @Context final HttpServletRequest request, @Context final UriInfo uriInfo) {
        Response response;

        logger.info("tracking id: {} type: {} name: {} path: {}", id, type, name, packageName);

        response = Response.ok().build();
        return response;
    }

    @Operation(description = "Store NPM artifact content under the given artifact store (type/name), packageName and versionTarball (/version or /-/tarball).")
    @APIResponse(responseCode = "201", description = "Content was stored successfully")
    @APIResponse(responseCode = "400", description = "No appropriate storage location was found in the specified store (this store, or a member if a group is specified).")
    @Path("/{packageName}/{versionTarball: (.*)}")
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
                             @PathParam("packageName") final String packageName,
                             final @PathParam("versionTarball") String versionTarball,
                             @Context final HttpServletRequest request, @Context final UriInfo uriInfo) {
        Response response;

        logger.info("tracking id: {} type: {} name: {} path: {} version tarball: {}", id, type, name, packageName, versionTarball);

        response = Response.ok().build();
        return response;
    }

    @Operation(description = "Store and track file/artifact content under the given artifact store (type/name) and path.")
    @APIResponse(responseCode = "200", description = "Header metadata for content (or rendered listing when path ends with '/index.html' or '/'")
    @APIResponse(responseCode = "404", description = "Content is not available")
    @Path("/{packageName}")
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
                           final @PathParam("packageName") String packageName,
                           final @QueryParam(CHECK_CACHE_ONLY) Boolean cacheOnly, final @Context UriInfo uriInfo,
                           final @Context HttpServletRequest request) {
        Response response;

        logger.info("tracking id: {} type: {} name: {} path: {}", id, type, name, packageName);

        response = Response.ok().build();
        return response;
    }

    @Operation(description = "Store and track file/artifact content under the given artifact store (type/name) and path.")
    @APIResponse(responseCode = "200", description = "Header metadata for content (or rendered listing when path ends with '/index.html' or '/'")
    @APIResponse(responseCode = "404", description = "Content is not available")
    @Path("/{packageName}/{versionTarball: (.*)}")
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
                           final @PathParam("packageName") String packageName,
                           final @PathParam("versionTarball") String versionTarball,
                           final @QueryParam(CHECK_CACHE_ONLY) Boolean cacheOnly, final @Context UriInfo uriInfo,
                           final @Context HttpServletRequest request) {
        Response response;

        logger.info("tracking id: {} type: {} name: {} path: {} versionTarball: {}", id, type, name, packageName, versionTarball);

        response = Response.ok().build();
        return response;
    }

    @Operation(description = "Retrieve and track NPM file/artifact content under the given artifact store (type/name) and path.")
    @APIResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = String.class)), description = "Rendered content listing (when path ends with '/index.html' or '/')")
    @APIResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = StreamingOutput.class)), description = "Content stream")
    @APIResponse(responseCode = "404", description = "Content is not available")
    @Path("/{packageName}")
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
                          @PathParam("packageName") final String packageName,
                          @Context final HttpServletRequest request,
                          @Context final UriInfo uriInfo) {
        logger.info("tracking id: {} type: {} name: {} packageName: {}", id, type, name, packageName);

        return Response.created(uriInfo.getRequestUri()).build();
    }

    @Operation(description = "Retrieve and track NPM file/artifact content under the given artifact store (type/name) and path.")
    @APIResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = String.class)), description = "Rendered content listing (when path ends with '/index.html' or '/')")
    @APIResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = StreamingOutput.class)), description = "Content stream")
    @APIResponse(responseCode = "404", description = "Content is not available")
    @Path("/{packageName}/{versionTarball: (.*)}")
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
                          @PathParam("packageName") final String packageName,
                          final @PathParam("versionTarball") String versionTarball,
                          @Context final HttpServletRequest request,
                          @Context final UriInfo uriInfo) {
        logger.info("tracking id: {} type: {} name: {} packageName: {} versionTarball: {}", id, type, name, packageName, versionTarball);

        return Response.created(uriInfo.getRequestUri()).build();
    }

}
