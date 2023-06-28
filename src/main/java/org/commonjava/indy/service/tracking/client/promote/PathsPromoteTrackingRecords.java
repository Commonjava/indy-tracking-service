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
package org.commonjava.indy.service.tracking.client.promote;

import org.commonjava.indy.service.tracking.model.StoreKey;

import java.util.Map;
import java.util.Set;

public class PathsPromoteTrackingRecords
{
    private String trackingId; // user specified tracking id

    private Map<String, PathsPromoteResult> resultMap; // promotion uuid -> result

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public Map<String, PathsPromoteResult> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<String, PathsPromoteResult> resultMap) {
        this.resultMap = resultMap;
    }

    public static class PathsPromoteResult {

        private PathsPromoteRequest request;

        private Set<String> completedPaths;

        public PathsPromoteRequest getRequest() {
            return request;
        }

        public void setRequest(PathsPromoteRequest request) {
            this.request = request;
        }

        public Set<String> getCompletedPaths() {
            return completedPaths;
        }

        public void setCompletedPaths(Set<String> completedPaths) {
            this.completedPaths = completedPaths;
        }
    }

    public static class PathsPromoteRequest {
        private StoreKey source;

        private StoreKey target;

        public StoreKey getSource() {
            return source;
        }

        public void setSource(StoreKey source) {
            this.source = source;
        }

        public StoreKey getTarget() {
            return target;
        }

        public void setTarget(StoreKey target) {
            this.target = target;
        }
    }
}
