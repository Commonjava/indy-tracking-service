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
import org.commonjava.indy.service.tracking.client.content.ContentService;
import org.commonjava.indy.service.tracking.jaxrs.ResponseHelper;
import org.commonjava.indy.service.tracking.model.AccessChannel;
import org.commonjava.indy.service.tracking.model.StoreEffect;
import org.commonjava.indy.service.tracking.model.StoreKey;
import org.commonjava.indy.service.tracking.model.StoreType;
import org.commonjava.indy.service.tracking.model.TrackedContentEntry;
import org.commonjava.indy.service.tracking.model.TrackingKey;
import org.commonjava.indy.service.tracking.model.dto.ContentDTO;
import org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO;
import org.commonjava.indy.service.tracking.model.pkg.PackageTypeConstants;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Mock
@RestClient
public class MockableContentService
                implements ContentService
{
    @Inject
    ResponseHelper helper;

    @Override
    public Response recalculateEntrySet( final Set<ContentTransferDTO> entries )
    {
        Set<TrackedContentEntry> newEntries = new HashSet<>();
        TrackedContentEntry entry = new TrackedContentEntry();
        entry.setPath( "/path/to/file" );
        entry.setTrackingKey( new TrackingKey( "tracking-id" ) );
        entry.setStoreKey( new StoreKey( PackageTypeConstants.PKG_TYPE_MAVEN, StoreType.remote, "test" ) );
        entry.setAccessChannel( AccessChannel.GENERIC_PROXY );
        entry.setOriginUrl( "https://example.com/file" );
        entry.setEffect( StoreEffect.UPLOAD );
        entry.setMd5( "md5hash124" );
        entry.setSha1( "sha1hash124" );
        entry.setSha256( "sha256hash124" );
        newEntries.add( entry );
        return helper.formatOkResponseWithJsonEntity( newEntries );
    }

    @Override
    public File getZipRepository( ContentDTO record )
    {
        File file;
        try
        {
            file = File.createTempFile( "test", ".zip" );
        }
        catch ( IOException e )
        {
            return null;
        }
        return file;
    }

}
