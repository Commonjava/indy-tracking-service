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
package org.commonjava.indy.service.tracking.ftests.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import org.commonjava.indy.service.tracking.client.content.BatchDeleteRequest;
import org.commonjava.indy.service.tracking.data.cassandra.CassandraConfiguration;
import org.commonjava.indy.service.tracking.exception.ContentException;
import org.commonjava.indy.service.tracking.exception.IndyWorkflowException;
import org.commonjava.indy.service.tracking.model.AccessChannel;
import org.commonjava.indy.service.tracking.model.StoreEffect;
import org.commonjava.indy.service.tracking.model.StoreKey;
import org.commonjava.indy.service.tracking.model.StoreType;
import org.commonjava.indy.service.tracking.model.TrackedContentEntry;
import org.commonjava.indy.service.tracking.model.TrackingKey;
import org.commonjava.indy.service.tracking.model.dto.TrackedContentDTO;
import org.commonjava.indy.service.tracking.model.dto.TrackedContentEntryDTO;
import org.commonjava.indy.service.tracking.model.pkg.PackageTypeConstants;
import org.commonjava.indy.service.tracking.profile.CassandraFunctionProfile;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestProfile( CassandraFunctionProfile.class )
@Testcontainers( disabledWithoutDocker = true )
public class AdminResourceTest
{
    private static volatile CassandraContainer<?> cassandraContainer;

    private final String TRACKING_ID = "tracking-id";

    private final String BASE_URL = "api/folo/admin/";

    @InjectMock
    CassandraConfiguration config;

    @Inject
    ObjectMapper mapper;

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
    void testRecalculateRecordSuccess()
    {
        String trackingId = "abc125";
        String expected_string =
                        "{\n" + "  \"key\" : {\n" + "    \"id\" : \"abc125\"\n" + "  },\n" + "  \"uploads\" : [ {\n"
                                        + "    \"storeKey\" : \"maven:remote:test\",\n"
                                        + "    \"accessChannel\" : \"GENERIC_PROXY\",\n"
                                        + "    \"path\" : \"/path/to/file\",\n"
                                        + "    \"originUrl\" : \"https://example.com/file\",\n"
                                        + "    \"localUrl\" : \"http://localhost:8081/api/content/maven/remote/test/path/to/file\",\n"
                                        + "    \"md5\" : \"md5hash124\",\n" + "    \"sha256\" : \"sha256hash124\",\n"
                                        + "    \"sha1\" : \"sha1hash124\"\n" + "  } ],\n" + "  \"downloads\" : [ {\n"
                                        + "    \"storeKey\" : \"maven:remote:test\",\n"
                                        + "    \"accessChannel\" : \"GENERIC_PROXY\",\n"
                                        + "    \"path\" : \"/path/to/file\",\n"
                                        + "    \"originUrl\" : \"https://example.com/file\",\n"
                                        + "    \"localUrl\" : \"http://localhost:8081/api/content/maven/remote/test/path/to/file\",\n"
                                        + "    \"md5\" : \"md5hash124\",\n" + "    \"sha256\" : \"sha256hash124\",\n"
                                        + "    \"sha1\" : \"sha1hash124\"\n" + "  } ]\n" + "}";
        given().when()
               .get( BASE_URL + trackingId + "/record/recalculate" )
               .then()
               .statusCode( 200 )
               .body( is( expected_string ) );
    }

    @Test
    void testRecalculateRecordNotFound()
    {
        given().when().get( BASE_URL + "random-id" + "/record/recalculate" ).then().statusCode( 404 );
    }

    @Test
    void testRecordArtifact()
    {
        String trackingId = "tracking-id";
        TrackedContentEntry entry = new TrackedContentEntry();
        entry.setTrackingKey( new TrackingKey( "new-tracking-id" ) );
        StoreKey storeKey = new StoreKey( PackageTypeConstants.PKG_TYPE_MAVEN, StoreType.remote, "test" );
        entry.setStoreKey( storeKey );
        entry.setSize( 123L );
        entry.setMd5( "md5123" );
        entry.setSha1( "sha112345" );
        entry.setSha256( "sha256123" );
        entry.setEffect( StoreEffect.UPLOAD );
        entry.setAccessChannel( AccessChannel.GENERIC_PROXY );
        entry.setOriginUrl( "uri://test/file" );
        Set<Long> timestamps = new HashSet<>();
        timestamps.add( 123L );
        timestamps.add( 1234L );
        entry.setTimestamps( timestamps );
        entry.setPath( "/path/to/file" );
        given().body( entry ).when().get( BASE_URL + trackingId + "/artifactRecord/test/path/abc?type=group&packageType=maven&name=tea" ).then().statusCode( 200 );
    }

    @Test
    public void testGetZipRepository()
    {
        given().when().get( BASE_URL + "abc123" + "/record/zip" ).then().statusCode( 200 ).body( is( "" ) );
    }

    @Test
    void testGetRecordReturnsOkResponse() throws IndyWorkflowException
    {
        String trackingId = "abc123";
        String expected_response =
                        "{\n" + "  \"key\" : {\n" + "    \"id\" : \"abc123\"\n" + "  },\n" + "  \"downloads\" : [ {\n"
                                        + "    \"storeKey\" : \"maven:remote:store_key_1\",\n"
                                        + "    \"accessChannel\" : \"GENERIC_PROXY\",\n"
                                        + "    \"path\" : \"/path/to/file\",\n"
                                        + "    \"originUrl\" : \"https://example.com/file\",\n"
                                        + "    \"localUrl\" : \"http://localhost:8081/api/content/maven/remote/store_key_1/path/to/file\",\n"
                                        + "    \"md5\" : \"md5hash123\",\n" + "    \"sha256\" : \"sha256hash123\",\n"
                                        + "    \"sha1\" : \"sha1hash123\",\n" + "    \"size\" : 1024,\n"
                                        + "    \"timestamps\" : [ 1647317221, 1647317231 ]\n" + "  } ]\n" + "}";

        given().when()
               .get( BASE_URL + trackingId + "/record" )
               .then()
               .statusCode( 200 )
               .body( is( expected_response ) );
    }

    @Test
    void testGetRecordReturnsNotFoundResponse() throws IndyWorkflowException
    {
        // when no existing tracking record is found a new tracking report is returned
        String expected_string = "{\n" + "  \"key\" : {\n" + "    \"id\" : \"lslsls\"\n" + "  }\n" + "}";
        given().when().get( BASE_URL + "lslsls" + "/record" ).then().statusCode( 200 ).body( is( expected_string ) );
    }

    @Test
    void testSealRecordSuccess()
    {
        String trackingId = "tracking-id";
        given().when().post( BASE_URL + trackingId + "/record" ).then().statusCode( 200 );
    }

    @Test
    void testSealRecordError()
    {
        given().when().post( BASE_URL + "random-id" + "/record" ).then().statusCode( 200 ).body( is( "" ) );
    }

    @Test
    public void testClearRecordSuccess() throws ContentException
    {
        // Mock the controller's behavior

        // Act
        // Call the function
        given().when().delete( BASE_URL + TRACKING_ID + "/record" ).then().statusCode( 204 );

        // Assert
    }

    @Test
    public void testGetRecordIdsSuccess()
    {
        // Set up mock response from adminController
        String expected_string1 = "{\n"
                        + "  \"sealed\" : [ \"abc123\", \"abc124\", \"abc125\", \"abc126\", \"abc127\", \"tracking-id\", \"abc128\" ]\n"
                        + "}";
        String expected_string2 = "{\n"
                        + "  \"sealed\" : [ \"abc123\", \"abc124\", \"abc125\", \"abc126\", \"abc127\", \"tracking-id\", \"abc128\" ]\n"
                        + "}";

        // Call getRecordIds() function
        given().when().get( BASE_URL + "report/ids/sealed" ).then().statusCode( 200 ).body( is( expected_string1 ) );

        given().when().get( BASE_URL + "report/ids/legacy" ).then().statusCode( 200 ).body( is( expected_string2 ) );

    }

    @Test
    public void testExportReportSuccess() throws IndyWorkflowException, IOException
    {
        // Set up mock response from adminController
        given().when().get( BASE_URL + "report/export" ).then().statusCode( 200 );
    }

    @Test
    public void testImportReportSuccess()
    {
        given().body( "test" ).when().put( BASE_URL + "report/import" ).then().statusCode( 201 );
    }

    @Test
    public void testDoDeleteSuccess() throws IndyWorkflowException, JsonProcessingException
    {
        StoreKey storeKey = new StoreKey( "maven", StoreType.remote, "test" );
        BatchDeleteRequest batchDeleteRequest = new BatchDeleteRequest();
        batchDeleteRequest.setStoreKey( storeKey );
        batchDeleteRequest.setTrackingID( TRACKING_ID );

        TrackedContentDTO trackedContentDTO = new TrackedContentDTO();
        TrackedContentEntryDTO entry = new TrackedContentEntryDTO();
        entry.setPath( "test" );
        Set<TrackedContentEntryDTO> entries = new HashSet<>();
        entries.add( entry );
        trackedContentDTO.setUploads( entries );
        trackedContentDTO.setKey( new TrackingKey( TRACKING_ID ) );

        given().header( "Content-type", "application/json" )
               .and()
               .body( mapper.writeValueAsString( batchDeleteRequest ) )
               .when()
               .post( BASE_URL + "batch/delete" )
               .then()
               .statusCode( 200 );
    }

    @Test
    public void testDoDeleteError() throws IndyWorkflowException, JsonProcessingException
    {
        StoreKey storeKey = new StoreKey( "maven", StoreType.remote, "test" );
        BatchDeleteRequest batchDeleteRequest1 = new BatchDeleteRequest();
        batchDeleteRequest1.setStoreKey( storeKey );
        batchDeleteRequest1.setTrackingID( TRACKING_ID );

        BatchDeleteRequest batchDeleteRequest2 = new BatchDeleteRequest();
        batchDeleteRequest2.setStoreKey( null );
        batchDeleteRequest2.setTrackingID( null );

        TrackedContentDTO trackedContentDTO = new TrackedContentDTO();
        TrackedContentEntryDTO entry = new TrackedContentEntryDTO();
        entry.setPath( "test" );
        Set<TrackedContentEntryDTO> entries = new HashSet<>();
        entries.add( entry );
        trackedContentDTO.setUploads( entries );
        trackedContentDTO.setKey( new TrackingKey( TRACKING_ID ) );
        given().header( "Content-type", "application/json" )
               .and()
               .body( mapper.writeValueAsString( batchDeleteRequest1 ) )
               .when()
               .post( BASE_URL + "batch/delete" )
               .then()
               .statusCode( 400 );
        given().header( "Content-type", "application/json" )
               .and()
               .body( mapper.writeValueAsString( batchDeleteRequest2 ) )
               .when()
               .post( BASE_URL + "batch/delete" )
               .then()
               .statusCode( 400 );

    }
}
