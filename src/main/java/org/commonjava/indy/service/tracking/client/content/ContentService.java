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
package org.commonjava.indy.service.tracking.client.content;

import org.commonjava.indy.service.tracking.client.CustomClientRequestFilter;
import org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path( "/api/admin/content" )
@RegisterRestClient( configKey = "content-service-api" )
@RegisterProvider( CustomClientRequestFilter.class )
public interface ContentService
{
    @POST
    @Path( "/tracking/recalculate" )
    Response recalculateEntrySet( final Set<ContentTransferDTO> entries );

}
