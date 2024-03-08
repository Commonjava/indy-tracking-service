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
import org.apache.http.HttpStatus;
import org.commonjava.indy.service.tracking.client.content.BatchDeleteRequest;
import org.commonjava.indy.service.tracking.client.content.MaintenanceService;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.ws.rs.core.Response;

@Mock
@RestClient
public class MockableMaintenanceService
                implements MaintenanceService
{
    @Override
    public Response doDelete( BatchDeleteRequest request )
    {
        return Response.status( HttpStatus.SC_OK ).build();
    }
}
