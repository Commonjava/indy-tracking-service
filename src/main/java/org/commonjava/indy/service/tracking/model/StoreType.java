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
package org.commonjava.indy.service.tracking.model;

import java.util.HashSet;
import java.util.Set;

public enum StoreType
{
    group( false, "group", "groups", "g" ), remote( false, "remote", "remotes", "repository", "repositories",
                                                    "r" ), hosted( true, "hosted", "hosted", "deploy", "deploys",
                                                                   "deploy_point", "h", "d" );

    private final boolean writable;

    private final String singular;

    private final String plural;

    private final Set<String> aliases;

    StoreType( final boolean writable, final String singular, final String plural, final String... aliases )
    {
        this.writable = writable;
        this.singular = singular;
        this.plural = plural;

        final Set<String> a = new HashSet<String>();
        a.add( singular );
        a.add( plural );
        for ( final String alias : aliases )
        {
            a.add( alias.toLowerCase() );
        }

        this.aliases = a;
    }

    public static StoreType get( final String typeStr )
    {
        if ( typeStr == null )
        {
            return null;
        }

        final String type = typeStr.trim().toLowerCase();
        if ( type.length() < 1 )
        {
            return null;
        }

        for ( final StoreType st : values() )
        {
            //            logger.info( "Checking '{}' vs name: '{}' and aliases: {}", type, st.name(), join( st.aliases, ", " ) );
            if ( st.name().equalsIgnoreCase( type ) || st.aliases.contains( type ) )
            {
                return st;
            }
        }

        return null;
    }

    public String pluralEndpointName()
    {
        return plural;
    }

    public String singularEndpointName()
    {
        return singular;
    }

    public String getName()
    {
        return singularEndpointName();
    }

    public boolean isWritable()
    {
        return writable;
    }

}
