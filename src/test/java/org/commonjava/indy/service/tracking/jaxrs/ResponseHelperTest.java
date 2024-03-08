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
package org.commonjava.indy.service.tracking.jaxrs;

import com.datastax.driver.core.SocketOptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import io.quarkus.test.junit.QuarkusTest;
import org.commonjava.indy.service.tracking.data.cassandra.ConfigurableRetryPolicy;
import org.commonjava.indy.service.tracking.data.cassandra.DtxTrackingRecord;
import org.commonjava.indy.service.tracking.data.metrics.TraceManager;
import org.commonjava.indy.service.tracking.model.AccessChannel;
import org.commonjava.indy.service.tracking.model.StoreEffect;
import org.commonjava.indy.service.tracking.model.StoreKey;
import org.commonjava.indy.service.tracking.model.StoreType;
import org.commonjava.indy.service.tracking.model.TrackedContent;
import org.commonjava.indy.service.tracking.model.TrackedContentEntry;
import org.commonjava.indy.service.tracking.model.TrackingKey;
import org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO;
import org.commonjava.indy.service.tracking.model.dto.TrackedContentDTO;
import org.commonjava.indy.service.tracking.model.dto.TrackedContentEntryDTO;
import org.commonjava.indy.service.tracking.model.dto.TrackingIdsDTO;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.commonjava.indy.service.tracking.model.pkg.PackageTypeConstants.PKG_TYPE_MAVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@QuarkusTest
public class ResponseHelperTest
{
    @Test
    public void test_formatResponse_0() throws Throwable
    {
        ResponseHelper responseHelper0 = new ResponseHelper();
        ResponseHelper responseHelper1 = new ResponseHelper();
        Exception exception2 = new Exception();
        Response response3 = responseHelper1.formatResponse( exception2 );

    }

    @Test
    public void test_formatResponse_1() throws Throwable
    {
        ResponseHelper responseHelper0 = new ResponseHelper();
        Throwable throwable1 = null;
        Response response2 = responseHelper0.formatResponse( throwable1 );

    }

    @Test
    public void test_formatOkResponseWithJsonEntity_0() throws Throwable
    {
        ResponseHelper responseHelper0 = new ResponseHelper();
        TrackedContent trackedContent1 = new TrackedContent();
        Response response2 = responseHelper0.formatOkResponseWithJsonEntity( trackedContent1 );

    }

    @Test
    public void test_formatOkResponseWithJsonEntity_1() throws Throwable
    {
        ResponseHelper responseHelper0 = new ResponseHelper();
        StoreKey storeKey2 = StoreKey.fromString( "hi!" );
        assertEquals( "hi!", storeKey2.getName() );
        assertEquals( "maven", storeKey2.getPackageType() );
        StoreType storeType3 = storeKey2.getType();
        StoreKey storeKey6 = new StoreKey( PKG_TYPE_MAVEN, storeType3, "maven:remote:hi!" );
        assertEquals( "maven:remote:hi!", storeKey6.getName() );
        assertEquals( PKG_TYPE_MAVEN, storeKey6.getPackageType() );
        StoreType storeType7 = storeKey6.getType();
        StoreKey storeKey10 = new StoreKey( PKG_TYPE_MAVEN, storeType7, "" );
        assertEquals( "", storeKey10.getName() );
        assertEquals( PKG_TYPE_MAVEN, storeKey10.getPackageType() );
        Response response11 = responseHelper0.formatOkResponseWithJsonEntity( storeKey10 );

    }

    @Test
    public void test_formatOkResponseWithJsonEntity_2() throws Throwable
    {
        ResponseHelper responseHelper0 = new ResponseHelper();
        StoreKey storeKey2 = StoreKey.fromString( "org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO" );
        assertEquals( "org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO", storeKey2.getName() );
        assertEquals( "maven", storeKey2.getPackageType() );
        TrackingKey trackingKey4 =
                        new TrackingKey( "org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO" );
        assertEquals( "org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO", trackingKey4.getId() );
        AccessChannel accessChannel5 = AccessChannel.GENERIC_PROXY;
        StoreEffect storeEffect6 = StoreEffect.DOWNLOAD;
        ContentTransferDTO contentTransferDTO9 =
                        new ContentTransferDTO( storeKey2, trackingKey4, accessChannel5, "/r{v0l5M#n}5r",
                                                "org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO",
                                                storeEffect6 );
        assertEquals( "/r{v0l5M#n}5r", contentTransferDTO9.getPath() );
        assertEquals( "org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO",
                      contentTransferDTO9.getOriginUrl() );
        Response response10 = responseHelper0.formatOkResponseWithJsonEntity( contentTransferDTO9 );

    }

    @Test
    public void test_formatOkResponseWithJsonEntity_3() throws Throwable
    {
        ResponseHelper responseHelper0 = new ResponseHelper();
        DtxTrackingRecord dtxTrackingRecord1 = new DtxTrackingRecord();
        Response response2 = responseHelper0.formatOkResponseWithJsonEntity( dtxTrackingRecord1 );

    }

    @Test
    public void test_formatOkResponseWithJsonEntity_4() throws Throwable
    {
        ResponseHelper responseHelper0 = new ResponseHelper();
        Object obj1 = new Object();
        Class<?> wildcardClass2 = obj1.getClass();
        assertEquals( "java.lang.Object", wildcardClass2.getName() );
        assertEquals( "java.lang", wildcardClass2.getPackageName() );
        ObjectMapperDeserializer<Object> objObjectMapperDeserializer3 =
                        new ObjectMapperDeserializer<Object>( (Class<Object>) wildcardClass2 );
        Class<?> wildcardClass4 = objObjectMapperDeserializer3.getClass();
        assertEquals( "io.quarkus.kafka.client.serialization.ObjectMapperDeserializer", wildcardClass4.getName() );
        assertEquals( "io.quarkus.kafka.client.serialization", wildcardClass4.getPackageName() );
        ObjectMapper objectMapper5 = null;
        ObjectMapperDeserializer<Object> objObjectMapperDeserializer6 =
                        new ObjectMapperDeserializer<Object>( (Class<Object>) wildcardClass4, objectMapper5 );
        Class<?> wildcardClass7 = objObjectMapperDeserializer6.getClass();
        assertEquals( "io.quarkus.kafka.client.serialization.ObjectMapperDeserializer", wildcardClass7.getName() );
        assertEquals( "io.quarkus.kafka.client.serialization", wildcardClass7.getPackageName() );
        ObjectMapper objectMapper8 = null;
        ObjectMapperDeserializer<Object> objObjectMapperDeserializer9 =
                        new ObjectMapperDeserializer<Object>( (Class<Object>) wildcardClass7, objectMapper8 );
        Response response10 = responseHelper0.formatOkResponseWithJsonEntity( objObjectMapperDeserializer9 );

    }

    @Test
    public void test_formatOkResponseWithJsonEntity_5() throws Throwable
    {
        ResponseHelper responseHelper0 = new ResponseHelper();
        TrackedContentDTO trackedContentDTO1 = new TrackedContentDTO();
        Response response2 = responseHelper0.formatOkResponseWithJsonEntity( trackedContentDTO1 );

    }

    @Test
    public void test_formatOkResponseWithJsonEntity_6() throws Throwable
    {
        ResponseHelper responseHelper0 = new ResponseHelper();
        TrackingIdsDTO trackingIdsDTO1 = new TrackingIdsDTO();
        Response response2 = responseHelper0.formatOkResponseWithJsonEntity( trackingIdsDTO1 );

    }

    @Test
    public void test_formatOkResponseWithJsonEntity_7() throws Throwable
    {
        ResponseHelper responseHelper0 = new ResponseHelper();
        ConfigurableRetryPolicy configurableRetryPolicy3 = new ConfigurableRetryPolicy( 10, 1 );
        Response response4 = responseHelper0.formatOkResponseWithJsonEntity( configurableRetryPolicy3 );

    }

    @Test
    public void test_formatOkResponseWithJsonEntity_8() throws Throwable
    {
        ResponseHelper responseHelper0 = new ResponseHelper();
        AtomicReference<Object> objAtomicReference1 = new AtomicReference<Object>();
        Response response2 = responseHelper0.formatOkResponseWithJsonEntity( objAtomicReference1 );

    }

    @Test
    public void test_formatOkResponseWithJsonEntity_9() throws Throwable
    {
        ResponseHelper responseHelper0 = new ResponseHelper();
        AccessChannel accessChannel1 = AccessChannel.GENERIC_PROXY;
        StoreType storeType2 = StoreType.hosted;
        StoreKey storeKey5 = new StoreKey( PKG_TYPE_MAVEN, storeType2, "mpv" );
        assertEquals( "mpv", storeKey5.getName() );
        assertEquals( PKG_TYPE_MAVEN, storeKey5.getPackageType() );
        TrackedContentEntryDTO trackedContentEntryDTO7 =
                        new TrackedContentEntryDTO( storeKey5, accessChannel1, "TrackedContentEntryDTO" );
        assertNull( trackedContentEntryDTO7.getMd5() );
        assertNull( trackedContentEntryDTO7.getSize() );
        assertNull( trackedContentEntryDTO7.getSha256() );
        assertEquals( "/TrackedContentEntryDTO", trackedContentEntryDTO7.getPath() );
        assertNull( trackedContentEntryDTO7.getOriginUrl() );
        assertNull( trackedContentEntryDTO7.getSha1() );
        assertNull( trackedContentEntryDTO7.getLocalUrl() );
        Response response8 = responseHelper0.formatOkResponseWithJsonEntity( trackedContentEntryDTO7 );

    }

    @Test
    public void test_formatOkResponseWithJsonEntity_10() throws Throwable
    {
        ResponseHelper responseHelper0 = new ResponseHelper();
        ResponseHelper responseHelper1 = new ResponseHelper();
        Response response2 = responseHelper0.formatOkResponseWithJsonEntity( responseHelper1 );

    }

    @Test
    public void test_formatOkResponseWithJsonEntity_11() throws Throwable
    {
        ResponseHelper responseHelper0 = new ResponseHelper();
        ObjectMapper objectMapper1 = null;
        ObjectMapper objectMapper2 = null;
        ObjectMapper objectMapper3 = null;
        ObjectMapper objectMapper4 = null;
        Object obj5 = new Object();
        TraceManager traceManager6 = null;
        DTOStreamingOutput dTOStreamingOutput7 = new DTOStreamingOutput( objectMapper4, obj5, traceManager6 );
        TraceManager traceManager8 = null;
        DTOStreamingOutput dTOStreamingOutput9 =
                        new DTOStreamingOutput( objectMapper3, dTOStreamingOutput7, traceManager8 );
        Class<?> wildcardClass10 = dTOStreamingOutput9.getClass();
        assertEquals( "org.commonjava.indy.service.tracking.jaxrs.DTOStreamingOutput", wildcardClass10.getName() );
        assertEquals( "org.commonjava.indy.service.tracking.jaxrs", wildcardClass10.getPackageName() );
        TraceManager traceManager11 = null;
        DTOStreamingOutput dTOStreamingOutput12 =
                        new DTOStreamingOutput( objectMapper2, wildcardClass10, traceManager11 );
        Class<?> wildcardClass13 = dTOStreamingOutput12.getClass();
        assertEquals( "org.commonjava.indy.service.tracking.jaxrs.DTOStreamingOutput", wildcardClass13.getName() );
        assertEquals( "org.commonjava.indy.service.tracking.jaxrs", wildcardClass13.getPackageName() );
        TraceManager traceManager14 = null;
        DTOStreamingOutput dTOStreamingOutput15 =
                        new DTOStreamingOutput( objectMapper1, wildcardClass13, traceManager14 );
        Response response16 = responseHelper0.formatOkResponseWithJsonEntity( dTOStreamingOutput15 );

    }

    @Test
    public void test_formatOkResponseWithJsonEntity_12() throws Throwable
    {
        ResponseHelper responseHelper0 = new ResponseHelper();
        SocketOptions socketOptions1 = new SocketOptions();
        Response response2 = responseHelper0.formatOkResponseWithJsonEntity( socketOptions1 );

    }

    @Test
    public void test_formatOkResponseWithJsonEntity_13() throws Throwable
    {
        ResponseHelper responseHelper0 = new ResponseHelper();
        TrackingKey trackingKey2 = new TrackingKey( "hi!" );
        assertEquals( "hi!", trackingKey2.getId() );
        Response response3 = responseHelper0.formatOkResponseWithJsonEntity( trackingKey2 );

    }

    @Test
    public void test_formatOkResponseWithJsonEntity_14() throws Throwable
    {
        ResponseHelper responseHelper0 = new ResponseHelper();
        AtomicBoolean atomicBoolean1 = new AtomicBoolean();
        Response response2 = responseHelper0.formatOkResponseWithJsonEntity( atomicBoolean1 );

    }

    @Test
    public void test_formatOkResponseWithJsonEntity_15() throws Throwable
    {
        ResponseHelper responseHelper0 = new ResponseHelper();
        TrackedContentEntry trackedContentEntry1 = new TrackedContentEntry();
        Response response2 = responseHelper0.formatOkResponseWithJsonEntity( trackedContentEntry1 );

    }

    @Test
    public void test_formatEntity_0() throws Throwable
    {
        ResponseHelper responseHelper0 = new ResponseHelper();
        ResponseHelper responseHelper1 = new ResponseHelper();
        Exception exception3 = new Exception();
        CharSequence charSequence5 = responseHelper1.formatEntity(
                        "IndyEvent{eventID=b020bdc3-8ade-4e45-b284-02c51d700ca2, eventMetadata=null}", exception3,
                        "IndyEvent{eventID=b020bdc3-8ade-4e45-b284-02c51d700ca2, eventMetadata=null}" );
        assertEquals( "Id: IndyEvent{eventID=b020bdc3-8ade-4e45-b284-02c51d700ca2, eventMetadata=null}\nMessage: IndyEvent{eventID=b020bdc3-8ade-4e45-b284-02c51d700ca2, eventMetadata=null}\nnull\n",
                      charSequence5 );

    }

    @Test
    public void test_formatEntity_1() throws Throwable
    {
        ResponseHelper responseHelper0 = new ResponseHelper();
        Throwable throwable1 = null;
        CharSequence charSequence4 = responseHelper0.formatEntity(
                        "Id: Id: \nMessage: hi!\n\nMessage: Id: Id: Id: hi!\nMessage: 0d6e4079e36703ebd37c00722f5891d28b0e2811dc114b129215123adcce3605\n\nMessage: Id: \nMessage: hi!\n\n\nMessage: Id: Id: hi!\nMessage: 0d6e4079e36703ebd37c00722f5891d28b0e2811dc114b129215123adcce3605\n\nMessage: Id: \nMessage: hi!\n\n\n\n",
                        throwable1,
                        "Id: Id: \nMessage: Id: \nMessage: hi!\n\n\nMessage: Id: hi!\nMessage: 0d6e4079e36703ebd37c00722f5891d28b0e2811dc114b129215123adcce3605\n\n" );
        assertEquals( "Id: Id: Id: \nMessage: hi!\n\nMessage: Id: Id: Id: hi!\nMessage: 0d6e4079e36703ebd37c00722f5891d28b0e2811dc114b129215123adcce3605\n\nMessage: Id: \nMessage: hi!\n\n\nMessage: Id: Id: hi!\nMessage: 0d6e4079e36703ebd37c00722f5891d28b0e2811dc114b129215123adcce3605\n\nMessage: Id: \nMessage: hi!\n\n\n\n\nMessage: Id: Id: \nMessage: Id: \nMessage: hi!\n\n\nMessage: Id: hi!\nMessage: 0d6e4079e36703ebd37c00722f5891d28b0e2811dc114b129215123adcce3605\n\n\n",
                      charSequence4 );

    }

    private Object getFieldValue( Object obj, String fieldName )
                    throws InvocationTargetException, SecurityException, IllegalArgumentException,
                    IllegalAccessException
    {
        try
        {
            Field field = obj.getClass().getField( fieldName );
            return field.get( obj );
        }
        catch ( NoSuchFieldException e )
        {
            for ( Method publicMethod : obj.getClass().getMethods() )
            {
                if ( publicMethod.getName().startsWith( "get" ) && publicMethod.getParameterCount() == 0
                                && publicMethod.getName().toLowerCase().equals( "get" + fieldName.toLowerCase() ) )
                {
                    return publicMethod.invoke( obj );
                }
            }
        }
        throw new IllegalArgumentException(
                        "Could not find field or getter " + fieldName + " for class " + obj.getClass().getName() );
    }
}
