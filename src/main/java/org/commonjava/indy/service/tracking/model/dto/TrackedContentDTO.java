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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.commonjava.indy.service.tracking.model.TrackingKey;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Set;

@Schema( type = SchemaType.OBJECT, discriminatorProperty = "type", description = "Representation of a simple boolean result of query, like if the stores data is empty" )
public class TrackedContentDTO
{

    @JsonProperty
    @Schema( description = "The description for this boolean result" )
    private TrackingKey key;

    private Set<TrackedContentEntryDTO> uploads;

    private Set<TrackedContentEntryDTO> downloads;

    public TrackedContentDTO()
    {
    }

    public TrackedContentDTO( final TrackingKey key, final Set<TrackedContentEntryDTO> uploads,
                              final Set<TrackedContentEntryDTO> downloads )
    {
        this.key = key;
        this.uploads = uploads;
        this.downloads = downloads;
    }

    public TrackingKey getKey()
    {
        return key;
    }

    public void setKey( final TrackingKey key )
    {
        this.key = key;
    }

    public Set<TrackedContentEntryDTO> getUploads()
    {
        return uploads;
    }

    public void setUploads( final Set<TrackedContentEntryDTO> uploads )
    {
        this.uploads = uploads;
    }

    public Set<TrackedContentEntryDTO> getDownloads()
    {
        return downloads;
    }

    public void setDownloads( final Set<TrackedContentEntryDTO> downloads )
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
}