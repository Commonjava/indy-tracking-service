package org.commonjava.indy.service.tracking.jaxrs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import org.commonjava.indy.service.tracking.Constants;
import org.commonjava.indy.service.tracking.client.content.BatchDeleteRequest;
import org.commonjava.indy.service.tracking.controller.AdminController;
import org.commonjava.indy.service.tracking.exception.ContentException;
import org.commonjava.indy.service.tracking.exception.IndyWorkflowException;
import org.commonjava.indy.service.tracking.model.StoreKey;
import org.commonjava.indy.service.tracking.model.StoreType;
import org.commonjava.indy.service.tracking.model.TrackingKey;
import org.commonjava.indy.service.tracking.model.dto.TrackedContentDTO;
import org.commonjava.indy.service.tracking.model.dto.TrackedContentEntryDTO;
import org.commonjava.indy.service.tracking.model.dto.TrackingIdsDTO;
import org.commonjava.indy.service.tracking.profile.CassandraFunctionProfile;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestProfile( CassandraFunctionProfile.class )
public class AdminResourceTest
{
    private final String TRACKING_ID = "tracking-id";

    private final String BASE_URL = "api/folo/admin/";

    @InjectMock
    AdminController adminController;

    @Inject
    ObjectMapper mapper;

    @Inject
    private ResponseHelper responseHelper;

    @Test
    void testRecalculateRecordSuccess() throws IndyWorkflowException
    {
        TrackedContentDTO trackedContentDTO = new TrackedContentDTO();
        when( adminController.recalculateRecord( anyString(), anyString() ) ).thenReturn( trackedContentDTO );
        given().when()
               .get( BASE_URL + TRACKING_ID + "/record/recalculate" )
               .then()
               .statusCode( 200 )
               .body( is( responseHelper.formatOkResponseWithJsonEntity( trackedContentDTO ).getEntity().toString() ) );
    }

    @Test
    void testRecalculateRecordNotFound() throws IndyWorkflowException
    {
        when( adminController.recalculateRecord( TRACKING_ID, "" ) ).thenReturn( null );
        given().when().get( BASE_URL + TRACKING_ID + "/record/recalculate" ).then().statusCode( 404 );
    }

    @Test
    void testRecalculateRecordFailed() throws IndyWorkflowException
    {
        when( adminController.recalculateRecord( TRACKING_ID, "" ) ).thenThrow( new IndyWorkflowException( "" ) );
        given().when().get( BASE_URL + TRACKING_ID + "/record/recalculate" ).then().statusCode( 404 );
    }

    @Test
    public void testGetZipRepository() throws IOException
    {
        File file = File.createTempFile( "test", ".zip" );
        when( adminController.getZipRepository( TRACKING_ID ) ).thenReturn( file );

        given().when().get( BASE_URL + TRACKING_ID + "/record/zip" ).then().statusCode( 200 ).body( is( "" ) );
    }

    @Test
    void testGetRecordReturnsOkResponse() throws IndyWorkflowException
    {
        // given
        TrackedContentDTO trackedContentDTO = new TrackedContentDTO();
        trackedContentDTO.setKey( new TrackingKey( TRACKING_ID ) );
        when( adminController.getRecord( anyString(), anyString() ) ).thenReturn( trackedContentDTO, null );
        when( adminController.getLegacyRecord( anyString(), anyString() ) ).thenReturn( trackedContentDTO );

        given().when()
               .get( BASE_URL + TRACKING_ID + "/record" )
               .then()
               .statusCode( 200 )
               .body( is( responseHelper.formatOkResponseWithJsonEntity( trackedContentDTO ).getEntity().toString() ) );

        given().when()
               .get( BASE_URL + TRACKING_ID + "/record" )
               .then()
               .statusCode( 200 )
               .body( is( responseHelper.formatOkResponseWithJsonEntity( trackedContentDTO ).getEntity().toString() ) );

        verify( adminController, times( 2 ) ).getRecord( anyString(), anyString() );
        verify( adminController, times( 1 ) ).getLegacyRecord( anyString(), anyString() );
    }

    @Test
    void testGetRecordReturnsNotFoundResponse() throws IndyWorkflowException
    {
        // given
        when( adminController.getRecord( anyString(), anyString() ) ).thenThrow( new IndyWorkflowException( "test" ) );

        // when
        given().when().get( BASE_URL + TRACKING_ID + "/record" ).then().statusCode( 500 );
    }

    @Test
    void testSealRecordSuccess()
    {
        // Mock controller methods
        when( adminController.seal( anyString(), anyString() ) ).thenReturn( new TrackedContentDTO() );

        // Call the function
        given().when().post( BASE_URL + TRACKING_ID + "/record" ).then().statusCode( 200 );
    }

    @Test
    void testSealRecordError()
    {
        // Mock controller methods
        final String errorMessage = "Failed to seal tracking record.";
        when( adminController.seal( anyString(), anyString() ) ).thenThrow( new RuntimeException( errorMessage ) );

        // Call the function
        given().when().post( BASE_URL + TRACKING_ID + "/record" ).then().statusCode( 500 );
    }

    @Test
    public void testClearRecordSuccess() throws ContentException
    {
        // Mock the controller's behavior
        doNothing().when( adminController ).clearRecord( TRACKING_ID );

        // Act
        // Call the function
        given().when().delete( BASE_URL + TRACKING_ID + "/record" ).then().statusCode( 204 );

        // Assert
        verify( adminController ).clearRecord( TRACKING_ID );
    }

    @Test
    public void testClearRecordError() throws ContentException
    {
        String errorMessage = "An error occurred while clearing the record.";

        // Mock the controller's behavior
        doThrow( new ContentException( errorMessage ) ).when( adminController ).clearRecord( TRACKING_ID );

        // Act
        given().when().delete( BASE_URL + TRACKING_ID + "/record" ).then().statusCode( 500 );

        // Assert
        verify( adminController ).clearRecord( TRACKING_ID );
    }

    @Test
    public void testGetRecordIdsSuccess()
    {
        // Set up mock response from adminController
        Set<Constants.TRACKING_TYPE> types = new HashSet<>();
        types.add( Constants.TRACKING_TYPE.SEALED );
        String expected_string = "{ }";

        when( adminController.getLegacyTrackingIds() ).thenReturn( new TrackingIdsDTO() );
        when( adminController.getTrackingIds( types ) ).thenReturn( new TrackingIdsDTO() );

        // Call getRecordIds() function
        given().when().get( BASE_URL + "report/ids/sealed" ).then().statusCode( 200 ).body( is( expected_string ) );

        given().when().get( BASE_URL + "report/ids/legacy" ).then().statusCode( 200 ).body( is( expected_string ) );

        // Verify that the getAllTrackingIds() function was called on the adminController
        verify( adminController, times( 1 ) ).getLegacyTrackingIds();
        verify( adminController, times( 1 ) ).getTrackingIds( types );
    }

    @Test
    public void testGetRecordIdsNotFound()
    {
        // Set up mock response from adminController
        when( adminController.getLegacyTrackingIds() ).thenReturn( null );

        // Call getRecordIds() function
        given().when().get( BASE_URL + "report/ids/legacy" ).then().statusCode( 404 );

        // Verify that the getAllTrackingIds() function was called on the adminController
        verify( adminController, times( 1 ) ).getLegacyTrackingIds();
    }

    @Test
    public void testExportReportSuccess() throws IndyWorkflowException, IOException
    {
        File file = File.createTempFile( "test", ".zip" );
        // Set up mock response from adminController
        when( adminController.renderReportZip() ).thenReturn( file );
        given().when().get( BASE_URL + "report/export" ).then().statusCode( 200 ).body( is( "" ) );
    }

    @Test
    public void testExportReportError() throws IndyWorkflowException
    {
        // Set up mock response from adminController
        when( adminController.renderReportZip() ).thenThrow( new IndyWorkflowException( "test" ) );
        given().when().get( BASE_URL + "report/export" ).then().statusCode( 500 );
    }

    @Test
    public void testImportReportSuccess() throws IndyWorkflowException
    {
        doNothing().when( adminController ).importRecordZip( any() );
        given().when().put( BASE_URL + "report/import" ).then().statusCode( 201 );
    }

    @Test
    public void testImportReportError() throws IndyWorkflowException, IOException
    {
        // Set up mock response from adminController
        doThrow( new IndyWorkflowException( "test" ) ).when( adminController ).importRecordZip( any() );
        given().when().put( BASE_URL + "report/import" ).then().statusCode( 500 );
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

        when( adminController.deletionAdditionalGuardCheck( any() ) ).thenReturn( true );
        when( adminController.getRecord( anyString(), anyString() ) ).thenReturn( trackedContentDTO, null );
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
        when( adminController.deletionAdditionalGuardCheck( any() ) ).thenReturn( true );
        when( adminController.getRecord( anyString(), anyString() ) ).thenReturn( null );
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

        verify( adminController, times( 1 ) ).getRecord( anyString(), anyString() );
    }
}
