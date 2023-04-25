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
