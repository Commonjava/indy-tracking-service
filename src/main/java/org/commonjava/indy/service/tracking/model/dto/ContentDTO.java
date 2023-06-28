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
package org.commonjava.indy.service.tracking.model.dto;

import org.commonjava.indy.service.tracking.model.TrackingKey;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import java.util.Set;

public class ContentDTO
                implements Externalizable
{

    private TrackingKey key;

    private Set<ContentEntryDTO> uploads;

    private Set<ContentEntryDTO> downloads;

    public ContentDTO()
    {
    }

    public ContentDTO( final TrackingKey key, final Set<ContentEntryDTO> uploads, final Set<ContentEntryDTO> downloads )
    {
        this.key = key;
        this.uploads = uploads;
        this.downloads = downloads;
    }

    public TrackingKey getKey()
    {
        return key;
    }

    public void setKey( TrackingKey key )
    {
        this.key = key;
    }

    public Set<ContentEntryDTO> getUploads()
    {
        return uploads;
    }

    public void setUploads( Set<ContentEntryDTO> uploads )
    {
        this.uploads = uploads;
    }

    public Set<ContentEntryDTO> getDownloads( Set<ContentEntryDTO> downloads )
    {
        return downloads;
    }

    public void setDownloads( Set<ContentEntryDTO> downloads )
    {
        this.downloads = downloads;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof TrackedContentDTO ) )
        {
            return false;
        }

        TrackedContentDTO that = (TrackedContentDTO) o;

        return getKey() != null ? getKey().equals( that.getKey() ) : that.getKey() == null;

    }

    @Override
    public int hashCode()
    {
        return getKey() != null ? getKey().hashCode() : 0;
    }

    @Override
    public void writeExternal( ObjectOutput objectOutput ) throws IOException
    {
        objectOutput.writeObject( key );
        objectOutput.writeObject( uploads );
        objectOutput.writeObject( downloads );
    }

    @Override
    public void readExternal( ObjectInput objectInput ) throws IOException, ClassNotFoundException
    {
        key = (TrackingKey) objectInput.readObject();
        Set<ContentEntryDTO> ups = (Set<ContentEntryDTO>) objectInput.readObject();
        uploads = ups == null ? new HashSet<>() : new HashSet<>( ups );

        Set<ContentEntryDTO> downs = (Set<ContentEntryDTO>) objectInput.readObject();
        downloads = downs == null ? new HashSet<>() : new HashSet<>( downs );
    }
}
