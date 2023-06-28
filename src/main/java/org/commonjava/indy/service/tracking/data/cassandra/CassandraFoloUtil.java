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
package org.commonjava.indy.service.tracking.data.cassandra;

public class CassandraFoloUtil
{

    public static final String TABLE_FOLO = "records2";

    public static final String TABLE_FOLO_LEGACY = "records";

    public static String getSchemaCreateTableFolo( String keySpace )
    {
        return "CREATE TABLE IF NOT EXISTS " + keySpace + "." + TABLE_FOLO + " (" + "tracking_key text,"
                        + "sealed boolean," + "store_key text," + "access_channel text," + "path text,"
                        + "origin_url text," + "local_url text," + "store_effect text," + "md5 text," + "sha256 text,"
                        + "sha1 text," + "size bigint," + "started bigint," // started timestamp *
                        + "timestamps set<bigint>," + "PRIMARY KEY ((tracking_key),store_key,path,store_effect)" + ");";
    }

    public static String getSchemaCreateTableFoloLegacy( String keySpace )
    {
        return "CREATE TABLE IF NOT EXISTS " + keySpace + "." + TABLE_FOLO_LEGACY + " (" + "tracking_key text,"
                        + "sealed boolean," + "store_key text," + "access_channel text," + "path text,"
                        + "origin_url text," + "local_url text," + "store_effect text," + "md5 text," + "sha256 text,"
                        + "sha1 text," + "size bigint," + "started bigint," // started timestamp *
                        + "timestamps set<bigint>," + "PRIMARY KEY ((tracking_key),store_key,path,store_effect)" + ");";
    }

}
