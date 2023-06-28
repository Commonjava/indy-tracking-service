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
package org.commonjava.indy.service.tracking.ftests.data.cassandra;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.commonjava.indy.service.tracking.data.cassandra.CassandraConfiguration;
import org.commonjava.indy.service.tracking.data.cassandra.CassandraTrackingQuery;
import org.commonjava.indy.service.tracking.data.cassandra.DtxTrackingRecord;
import org.commonjava.indy.service.tracking.model.StoreKey;
import org.commonjava.indy.service.tracking.model.StoreType;
import org.commonjava.indy.service.tracking.model.TrackingKey;
import org.commonjava.indy.service.tracking.model.pkg.PackageTypeConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.inject.Inject;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.when;

@QuarkusTest
@Testcontainers( disabledWithoutDocker = true )
public class CassandraTrackingRecordTest
{
    @InjectMock
    CassandraConfiguration config;

    @Inject
    CassandraTrackingQuery trackingQuery;

    private volatile CassandraContainer<?> cassandraContainer;

    @BeforeEach
    public void start() throws Exception
    {
        this.cassandraContainer = (CassandraContainer) ( new CassandraContainer() );
        String initScript = "cassandra_init_script.cql";
        URL resource = Thread.currentThread().getContextClassLoader().getResource( initScript );
        if ( resource != null )
        {
            this.cassandraContainer.withInitScript( initScript );
        }
        this.cassandraContainer.start();
        String host = this.cassandraContainer.getHost();
        int port = this.cassandraContainer.getMappedPort( CassandraContainer.CQL_PORT );
        when( config.getCassandraHost() ).thenReturn( host );
        when( config.getCassandraPort() ).thenReturn( port );
        when( config.getCassandraUser() ).thenReturn( "cassandra" );
        when( config.getCassandraPass() ).thenReturn( "cassandra" );
        when( config.getKeyspace() ).thenReturn( "folo" );
        when( config.getKeyspaceReplicas() ).thenReturn( 1 );
        when( config.isEnabled() ).thenReturn( true );
    }

    @Test
    public void testQuery()
    {
        DtxTrackingRecord trackingRecord = createTestStore();
        trackingQuery.get( new TrackingKey( "test" ) );
        trackingQuery.getSealedTrackingKey();
        trackingQuery.getLegacyTrackingKeys();
        trackingQuery.getSealed();

    }

    private DtxTrackingRecord createTestStore()
    {
        DtxTrackingRecord trackingRecord = new DtxTrackingRecord();

        trackingRecord.setTrackingKey( "test" );
        trackingRecord.setState( true );
        StoreKey storeKey = new StoreKey( PackageTypeConstants.PKG_TYPE_MAVEN, StoreType.remote, "test" );
        trackingRecord.setStoreKey( storeKey.toString() );
        trackingRecord.setPath( "testPath" );
        trackingRecord.setOriginUrl( "test" );
        trackingRecord.setLocalUrl( "test" );
        trackingRecord.setStoreEffect( "UPLOAD" );
        trackingRecord.setMd5( "12345" );
        trackingRecord.setSha256( "12345" );
        trackingRecord.setSha1( "12345" );
        trackingRecord.setSize( 12345L );
        trackingRecord.setStarted( 12345L );
        Set<Long> timestamps = new HashSet<>();
        timestamps.add( 12L );
        timestamps.add( 123L );
        timestamps.add( 1234L );
        timestamps.add( 12345L );
        trackingRecord.setTimestamps( timestamps );

        trackingQuery.createDtxTrackingRecord( trackingRecord );

        return trackingRecord;
    }

    @AfterEach
    public void stop()
    {
        if ( this.cassandraContainer != null && this.cassandraContainer.isRunning() )
        {
            this.cassandraContainer.stop();
        }
    }

}
