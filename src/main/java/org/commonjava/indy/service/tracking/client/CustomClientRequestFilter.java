package org.commonjava.indy.service.tracking.client;

import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.Tokens;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
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
