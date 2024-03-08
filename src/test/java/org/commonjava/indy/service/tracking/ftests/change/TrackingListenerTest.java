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
package org.commonjava.indy.service.tracking.ftests.change;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import org.commonjava.event.common.EventMetadata;
import org.commonjava.event.file.FileEvent;
import org.commonjava.event.file.FileEventType;
import org.commonjava.event.file.TransferOperation;
import org.commonjava.event.promote.PathsPromoteCompleteEvent;
import org.commonjava.indy.service.tracking.Constants;
import org.commonjava.indy.service.tracking.data.cassandra.CassandraConfiguration;
import org.commonjava.indy.service.tracking.data.cassandra.CassandraTrackingQuery;
import org.commonjava.indy.service.tracking.model.AccessChannel;
import org.commonjava.indy.service.tracking.model.StoreKey;
import org.commonjava.indy.service.tracking.model.StoreType;
import org.commonjava.indy.service.tracking.model.TrackedContent;
import org.commonjava.indy.service.tracking.model.TrackingKey;
import org.commonjava.indy.service.tracking.model.pkg.PackageTypeConstants;
import org.commonjava.indy.service.tracking.profile.KafkaEventProfile;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Thread.sleep;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestProfile( KafkaEventProfile.class )
@Testcontainers( disabledWithoutDocker = true )
public class TrackingListenerTest
{
    private static volatile CassandraContainer<?> cassandraContainer;

    @Inject
    @Any
    InMemoryConnector connector;

    @InjectMock
    CassandraConfiguration config;

    @Inject
    CassandraTrackingQuery trackingQuery;

    @BeforeAll
    public static void init()
    {
        cassandraContainer = (CassandraContainer) ( new CassandraContainer() );
        String initScript = "folo_init_script.cql";
        URL resource = Thread.currentThread().getContextClassLoader().getResource( initScript );
        if ( resource != null )
        {
            cassandraContainer.withInitScript( initScript );
        }
        cassandraContainer.start();
    }

    @AfterAll
    public static void stop()
    {
        cassandraContainer.stop();
    }

    @BeforeEach
    public void start()
    {
        String host = cassandraContainer.getHost();
        int port = cassandraContainer.getMappedPort( CassandraContainer.CQL_PORT );
        when( config.getCassandraHost() ).thenReturn( host );
        when( config.getCassandraPort() ).thenReturn( port );
        when( config.getCassandraUser() ).thenReturn( "cassandra" );
        when( config.getCassandraPass() ).thenReturn( "cassandra" );
        when( config.getKeyspace() ).thenReturn( "folo" );
        when( config.getKeyspaceReplicas() ).thenReturn( 1 );
        when( config.isEnabled() ).thenReturn( true );
    }

    @Test
    void TestHandleFileAccessEvent() throws InterruptedException
    {
        InMemorySource<FileEvent> fileEvents = connector.source( "file-event-in" );
        FileEvent event = new FileEvent( FileEventType.ACCESS );
        EventMetadata metadata = new EventMetadata();
        metadata.set( Constants.ACCESS_CHANNEL, AccessChannel.GENERIC_PROXY.toString() );
        event.setEventMetadata( metadata );
        String trackingId = "access-tracking-id";
        event.setSessionId( trackingId );
        StoreKey storeKey = new StoreKey( PackageTypeConstants.PKG_TYPE_MAVEN, StoreType.remote, "test" );
        event.setStoreKey( storeKey.toString() );
        event.setTargetPath( "path/to/file" );
        event.setSourceLocation( "file://path/to/sourcefile" );
        event.setSize( 123L );
        event.setMd5( "md5123" );
        event.setSha1( "sha112345" );
        event.setChecksum( "sha256123" );
        event.setSourcePath( "/path/to/sourcefile" );
        fileEvents.send( event );
        sleep( 10000 );
        TrackedContent content = trackingQuery.get( new TrackingKey( trackingId ) );
        assert content != null;
    }

    @Test
    void TestHandleFileStorageEvent() throws InterruptedException
    {
        InMemorySource<FileEvent> fileEvents = connector.source( "file-event-in" );
        FileEvent event = new FileEvent( FileEventType.STORAGE );
        event.setOperation( TransferOperation.UPLOAD );
        EventMetadata metadata = new EventMetadata();
        metadata.set( Constants.ACCESS_CHANNEL, AccessChannel.GENERIC_PROXY.toString() );
        event.setEventMetadata( metadata );
        String trackingId = "access-tracking-id1";
        event.setSessionId( trackingId );
        StoreKey storeKey = new StoreKey( PackageTypeConstants.PKG_TYPE_MAVEN, StoreType.remote, "test" );
        event.setStoreKey( storeKey.toString() );
        event.setTargetPath( "path/to/file" );
        event.setSourceLocation( "file://path/to/sourcefile" );
        event.setSize( 123L );
        event.setMd5( "md5123" );
        event.setSha1( "sha112345" );
        event.setChecksum( "sha256123" );
        event.setSourcePath( "/path/to/sourcefile" );
        fileEvents.send( event );
        sleep( 10000 );
        TrackedContent content = trackingQuery.get( new TrackingKey( trackingId ) );
        assert content != null;
    }
    
}
