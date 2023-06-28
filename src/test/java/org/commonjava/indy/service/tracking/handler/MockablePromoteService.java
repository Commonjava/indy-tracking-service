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
package org.commonjava.indy.service.tracking.handler;

import io.quarkus.test.Mock;
import org.commonjava.indy.service.tracking.client.promote.PathsPromoteTrackingRecords;
import org.commonjava.indy.service.tracking.client.promote.PromoteService;
import org.commonjava.indy.service.tracking.jaxrs.DTOStreamingOutput;
import org.commonjava.indy.service.tracking.jaxrs.ResponseHelper;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Mock
@RestClient
public class MockablePromoteService
                implements PromoteService
{
    @Inject
    ResponseHelper helper;
    @Override
    public Response getPromoteRecords(String trackingId) throws Exception
    {
/*
        PathsPromoteTrackingRecords records = new PathsPromoteTrackingRecords();
        records.setTrackingId(trackingId);
        Map<String, PathsPromoteTrackingRecords.PathsPromoteResult> resultMap = new HashMap<>();
        PathsPromoteTrackingRecords.PathsPromoteResult result = new PathsPromoteTrackingRecords.PathsPromoteResult();
        PathsPromoteTrackingRecords.PathsPromoteRequest request = new PathsPromoteTrackingRecords.PathsPromoteRequest();
        request.setSourceStore("maven:remote:src");
        request.setTargetStore("maven:remote:test");
        result.setRequest(request);
        Set<String> completedPaths = new HashSet<>();
        completedPaths.add("a/b/c");
        result.setCompletedPaths(completedPaths);
        resultMap.put("uuid-1", result);
        records.setResultMap(resultMap);
        return helper.formatOkResponseWithJsonEntity( records );
*/
        try (InputStream stream = this.getClass().getClassLoader()
                .getResourceAsStream( "promote-tracking-records-test.json" ))
        {
            Response.ResponseBuilder builder = Response.ok( stream, APPLICATION_JSON );
            return builder.build();
        }
    }
}
