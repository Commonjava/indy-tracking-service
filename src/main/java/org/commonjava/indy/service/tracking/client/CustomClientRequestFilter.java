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
package org.commonjava.indy.service.tracking.client;

import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.Tokens;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Priority( Priorities.AUTHENTICATION )
public class CustomClientRequestFilter
                implements ClientRequestFilter
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Inject
    OidcClient client;

    @ConfigProperty( name = "indy_security.enabled" )
    boolean securityEnabled;

    private volatile Tokens tokens;

    @Override
    public void filter( ClientRequestContext requestContext ) throws IOException
    {
        if ( securityEnabled )
        {
            if ( tokens == null || tokens.isAccessTokenExpired() )
            {
                logger.debug( "Security enabled, get oidc Tokens" );
                tokens = client.getTokens().await().indefinitely();
            }
            requestContext.getHeaders().add( HttpHeaders.AUTHORIZATION, "Bearer " + tokens.getAccessToken() );
        }
    }
}
