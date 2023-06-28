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

import io.quarkus.test.junit.QuarkusTest;
import org.commonjava.indy.service.tracking.model.AccessChannel;
import org.commonjava.indy.service.tracking.model.StoreEffect;
import org.commonjava.indy.service.tracking.model.StoreKey;
import org.commonjava.indy.service.tracking.model.StoreType;
import org.commonjava.indy.service.tracking.model.TrackingKey;
import org.junit.jupiter.api.Test;

import static org.commonjava.indy.service.tracking.model.pkg.PackageTypeConstants.PKG_TYPE_MAVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class ContentTransferDTOTest
{
    @Test
    public void test_setStoreKey_0() throws Throwable
    {
        ContentTransferDTO contentTransferDTO0 = new ContentTransferDTO();
        StoreKey storeKey2 = StoreKey.fromString( "/MELe*A>,zm\"M+%] \"" );
        assertEquals( "/MELe*A>,zm\"M+%] \"", storeKey2.getName() );
        assertEquals( "maven", storeKey2.getPackageType() );
        contentTransferDTO0.setStoreKey( storeKey2 );
        assertNull( contentTransferDTO0.getPath() );
        assertNull( contentTransferDTO0.getOriginUrl() );

    }

    @Test
    public void test_compareTo_0() throws Throwable
    {
        StoreKey storeKey1 = StoreKey.fromString( "org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO" );
        assertEquals( "org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO", storeKey1.getName() );
        assertEquals( "maven", storeKey1.getPackageType() );
        TrackingKey trackingKey3 =
                        new TrackingKey( "org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO" );
        assertEquals( "org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO", trackingKey3.getId() );
        AccessChannel accessChannel4 = AccessChannel.GENERIC_PROXY;
        StoreEffect storeEffect5 = StoreEffect.DOWNLOAD;
        ContentTransferDTO contentTransferDTO8 =
                        new ContentTransferDTO( storeKey1, trackingKey3, accessChannel4, "/r{v0l5M#n}5r",
                                                "org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO",
                                                storeEffect5 );
        assertEquals( "/r{v0l5M#n}5r", contentTransferDTO8.getPath() );
        assertEquals( "org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO",
                      contentTransferDTO8.getOriginUrl() );
        int int9 = contentTransferDTO8.compareTo( contentTransferDTO8 );
        assertEquals( 0, int9 );

    }

    @Test
    public void test_equals_0() throws Throwable
    {
        StoreKey storeKey1 = StoreKey.fromString( "(MnYGZy4" );
        assertEquals( "(MnYGZy4", storeKey1.getName() );
        assertEquals( "maven", storeKey1.getPackageType() );
        StoreEffect storeEffect2 = StoreEffect.UPLOAD;
        TrackingKey trackingKey3 = new TrackingKey();
        AccessChannel accessChannel4 = AccessChannel.NATIVE;
        ContentTransferDTO contentTransferDTO7 =
                        new ContentTransferDTO( storeKey1, trackingKey3, accessChannel4, "Q4{X4`v!O{dt4)u/!", "27;t",
                                                storeEffect2 );
        assertEquals( "/Q4{X4`v!O{dt4)u/!", contentTransferDTO7.getPath() );
        assertEquals( "27;t", contentTransferDTO7.getOriginUrl() );
        org.commonjava.indy.service.tracking.model.TrackedContent trackedContent8 =
                        new org.commonjava.indy.service.tracking.model.TrackedContent();
        boolean boolean9 = contentTransferDTO7.equals( trackedContent8 );
        assertFalse( boolean9 );

    }

    @Test
    public void test_equals_1() throws Throwable
    {
        StoreKey storeKey1 = StoreKey.fromString( "(MnYGZy4" );
        assertEquals( "(MnYGZy4", storeKey1.getName() );
        assertEquals( "maven", storeKey1.getPackageType() );
        StoreEffect storeEffect2 = StoreEffect.UPLOAD;
        TrackingKey trackingKey3 = new TrackingKey();
        AccessChannel accessChannel4 = AccessChannel.NATIVE;
        ContentTransferDTO contentTransferDTO7 =
                        new ContentTransferDTO( storeKey1, trackingKey3, accessChannel4, "Q4{X4`v!O{dt4)u/!", "27;t",
                                                storeEffect2 );
        assertEquals( "/Q4{X4`v!O{dt4)u/!", contentTransferDTO7.getPath() );
        assertEquals( "27;t", contentTransferDTO7.getOriginUrl() );
        StoreKey storeKey9 = StoreKey.fromString( "hi!" );
        assertEquals( "hi!", storeKey9.getName() );
        assertEquals( "maven", storeKey9.getPackageType() );
        StoreType storeType10 = storeKey9.getType();
        StoreKey storeKey13 = new StoreKey( PKG_TYPE_MAVEN, storeType10, "maven:remote:hi!" );
        assertEquals( "maven:remote:hi!", storeKey13.getName() );
        assertEquals( PKG_TYPE_MAVEN, storeKey13.getPackageType() );
        StoreType storeType14 = storeKey13.getType();
        StoreKey storeKey17 = new StoreKey( PKG_TYPE_MAVEN, storeType14, "" );
        assertEquals( "", storeKey17.getName() );
        assertEquals( PKG_TYPE_MAVEN, storeKey17.getPackageType() );
        boolean boolean18 = contentTransferDTO7.equals( storeKey17 );
        assertFalse( boolean18 );

    }

    @Test
    public void test_equals_2() throws Throwable
    {
        StoreKey storeKey1 = StoreKey.fromString( "(MnYGZy4" );
        assertEquals( "(MnYGZy4", storeKey1.getName() );
        assertEquals( "maven", storeKey1.getPackageType() );
        StoreEffect storeEffect2 = StoreEffect.UPLOAD;
        TrackingKey trackingKey3 = new TrackingKey();
        AccessChannel accessChannel4 = AccessChannel.NATIVE;
        ContentTransferDTO contentTransferDTO7 =
                        new ContentTransferDTO( storeKey1, trackingKey3, accessChannel4, "Q4{X4`v!O{dt4)u/!", "27;t",
                                                storeEffect2 );
        assertEquals( "/Q4{X4`v!O{dt4)u/!", contentTransferDTO7.getPath() );
        assertEquals( "27;t", contentTransferDTO7.getOriginUrl() );
        StoreKey storeKey9 = StoreKey.fromString( "org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO" );
        assertEquals( "org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO", storeKey9.getName() );
        assertEquals( "maven", storeKey9.getPackageType() );
        TrackingKey trackingKey11 =
                        new TrackingKey( "org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO" );
        assertEquals( "org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO", trackingKey11.getId() );
        AccessChannel accessChannel12 = AccessChannel.GENERIC_PROXY;
        StoreEffect storeEffect13 = StoreEffect.DOWNLOAD;
        ContentTransferDTO contentTransferDTO16 =
                        new ContentTransferDTO( storeKey9, trackingKey11, accessChannel12, "/r{v0l5M#n}5r",
                                                "org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO",
                                                storeEffect13 );
        assertEquals( "/r{v0l5M#n}5r", contentTransferDTO16.getPath() );
        assertEquals( "org.commonjava.indy.service.tracking.model.dto.ContentTransferDTO",
                      contentTransferDTO16.getOriginUrl() );
        boolean boolean17 = contentTransferDTO7.equals( contentTransferDTO16 );
        assertFalse( boolean17 );

    }

    @Test
    public void test_equals_3() throws Throwable
    {
        StoreKey storeKey1 = StoreKey.fromString( "(MnYGZy4" );
        assertEquals( "(MnYGZy4", storeKey1.getName() );
        assertEquals( "maven", storeKey1.getPackageType() );
        StoreEffect storeEffect2 = StoreEffect.UPLOAD;
        TrackingKey trackingKey3 = new TrackingKey();
        AccessChannel accessChannel4 = AccessChannel.NATIVE;
        ContentTransferDTO contentTransferDTO7 =
                        new ContentTransferDTO( storeKey1, trackingKey3, accessChannel4, "Q4{X4`v!O{dt4)u/!", "27;t",
                                                storeEffect2 );
        assertEquals( "/Q4{X4`v!O{dt4)u/!", contentTransferDTO7.getPath() );
        assertEquals( "27;t", contentTransferDTO7.getOriginUrl() );
        org.commonjava.indy.service.tracking.data.cassandra.DtxTrackingRecord dtxTrackingRecord8 =
                        new org.commonjava.indy.service.tracking.data.cassandra.DtxTrackingRecord();
        boolean boolean9 = contentTransferDTO7.equals( dtxTrackingRecord8 );
        assertFalse( boolean9 );

    }

    @Test
    public void test_equals_4() throws Throwable
    {
        StoreKey storeKey1 = StoreKey.fromString( "(MnYGZy4" );
        assertEquals( "(MnYGZy4", storeKey1.getName() );
        assertEquals( "maven", storeKey1.getPackageType() );
        StoreEffect storeEffect2 = StoreEffect.UPLOAD;
        TrackingKey trackingKey3 = new TrackingKey();
        AccessChannel accessChannel4 = AccessChannel.NATIVE;
        ContentTransferDTO contentTransferDTO7 =
                        new ContentTransferDTO( storeKey1, trackingKey3, accessChannel4, "Q4{X4`v!O{dt4)u/!", "27;t",
                                                storeEffect2 );
        assertEquals( "/Q4{X4`v!O{dt4)u/!", contentTransferDTO7.getPath() );
        assertEquals( "27;t", contentTransferDTO7.getOriginUrl() );
        Object obj8 = new Object();
        Class<?> wildcardClass9 = obj8.getClass();
        assertEquals( "java.lang.Object", wildcardClass9.getName() );
        assertEquals( "java.lang", wildcardClass9.getPackageName() );
        io.quarkus.kafka.client.serialization.ObjectMapperDeserializer<Object> objObjectMapperDeserializer10 =
                        new io.quarkus.kafka.client.serialization.ObjectMapperDeserializer<Object>(
                                        (Class<Object>) wildcardClass9 );
        Class<?> wildcardClass11 = objObjectMapperDeserializer10.getClass();
        assertEquals( "io.quarkus.kafka.client.serialization.ObjectMapperDeserializer", wildcardClass11.getName() );
        assertEquals( "io.quarkus.kafka.client.serialization", wildcardClass11.getPackageName() );
        com.fasterxml.jackson.databind.ObjectMapper objectMapper12 = null;
        io.quarkus.kafka.client.serialization.ObjectMapperDeserializer<Object> objObjectMapperDeserializer13 =
                        new io.quarkus.kafka.client.serialization.ObjectMapperDeserializer<Object>(
                                        (Class<Object>) wildcardClass11, objectMapper12 );
        Class<?> wildcardClass14 = objObjectMapperDeserializer13.getClass();
        assertEquals( "io.quarkus.kafka.client.serialization.ObjectMapperDeserializer", wildcardClass14.getName() );
        assertEquals( "io.quarkus.kafka.client.serialization", wildcardClass14.getPackageName() );
        com.fasterxml.jackson.databind.ObjectMapper objectMapper15 = null;
        io.quarkus.kafka.client.serialization.ObjectMapperDeserializer<Object> objObjectMapperDeserializer16 =
                        new io.quarkus.kafka.client.serialization.ObjectMapperDeserializer<Object>(
                                        (Class<Object>) wildcardClass14, objectMapper15 );
        boolean boolean17 = contentTransferDTO7.equals( objObjectMapperDeserializer16 );
        assertFalse( boolean17 );

    }

    @Test
    public void test_equals_5() throws Throwable
    {
        StoreKey storeKey1 = StoreKey.fromString( "(MnYGZy4" );
        assertEquals( "(MnYGZy4", storeKey1.getName() );
        assertEquals( "maven", storeKey1.getPackageType() );
        StoreEffect storeEffect2 = StoreEffect.UPLOAD;
        TrackingKey trackingKey3 = new TrackingKey();
        AccessChannel accessChannel4 = AccessChannel.NATIVE;
        ContentTransferDTO contentTransferDTO7 =
                        new ContentTransferDTO( storeKey1, trackingKey3, accessChannel4, "Q4{X4`v!O{dt4)u/!", "27;t",
                                                storeEffect2 );
        assertEquals( "/Q4{X4`v!O{dt4)u/!", contentTransferDTO7.getPath() );
        assertEquals( "27;t", contentTransferDTO7.getOriginUrl() );
        TrackedContentDTO trackedContentDTO8 = new TrackedContentDTO();
        boolean boolean9 = contentTransferDTO7.equals( trackedContentDTO8 );
        assertFalse( boolean9 );

    }

    @Test
    public void test_equals_6() throws Throwable
    {
        StoreKey storeKey1 = StoreKey.fromString( "(MnYGZy4" );
        assertEquals( "(MnYGZy4", storeKey1.getName() );
        assertEquals( "maven", storeKey1.getPackageType() );
        StoreEffect storeEffect2 = StoreEffect.UPLOAD;
        TrackingKey trackingKey3 = new TrackingKey();
        AccessChannel accessChannel4 = AccessChannel.NATIVE;
        ContentTransferDTO contentTransferDTO7 =
                        new ContentTransferDTO( storeKey1, trackingKey3, accessChannel4, "Q4{X4`v!O{dt4)u/!", "27;t",
                                                storeEffect2 );
        assertEquals( "/Q4{X4`v!O{dt4)u/!", contentTransferDTO7.getPath() );
        assertEquals( "27;t", contentTransferDTO7.getOriginUrl() );
        TrackingIdsDTO trackingIdsDTO8 = new TrackingIdsDTO();
        boolean boolean9 = contentTransferDTO7.equals( trackingIdsDTO8 );
        assertFalse( boolean9 );

    }

    @Test
    public void test_equals_7() throws Throwable
    {
        StoreKey storeKey1 = StoreKey.fromString( "(MnYGZy4" );
        assertEquals( "(MnYGZy4", storeKey1.getName() );
        assertEquals( "maven", storeKey1.getPackageType() );
        StoreEffect storeEffect2 = StoreEffect.UPLOAD;
        TrackingKey trackingKey3 = new TrackingKey();
        AccessChannel accessChannel4 = AccessChannel.NATIVE;
        ContentTransferDTO contentTransferDTO7 =
                        new ContentTransferDTO( storeKey1, trackingKey3, accessChannel4, "Q4{X4`v!O{dt4)u/!", "27;t",
                                                storeEffect2 );
        assertEquals( "/Q4{X4`v!O{dt4)u/!", contentTransferDTO7.getPath() );
        assertEquals( "27;t", contentTransferDTO7.getOriginUrl() );
        org.commonjava.indy.service.tracking.data.cassandra.ConfigurableRetryPolicy configurableRetryPolicy10 =
                        new org.commonjava.indy.service.tracking.data.cassandra.ConfigurableRetryPolicy( 10, 1 );
        boolean boolean11 = contentTransferDTO7.equals( configurableRetryPolicy10 );
        assertFalse( boolean11 );

    }

    @Test
    public void test_equals_8() throws Throwable
    {
        StoreKey storeKey1 = StoreKey.fromString( "(MnYGZy4" );
        assertEquals( "(MnYGZy4", storeKey1.getName() );
        assertEquals( "maven", storeKey1.getPackageType() );
        StoreEffect storeEffect2 = StoreEffect.UPLOAD;
        TrackingKey trackingKey3 = new TrackingKey();
        AccessChannel accessChannel4 = AccessChannel.NATIVE;
        ContentTransferDTO contentTransferDTO7 =
                        new ContentTransferDTO( storeKey1, trackingKey3, accessChannel4, "Q4{X4`v!O{dt4)u/!", "27;t",
                                                storeEffect2 );
        assertEquals( "/Q4{X4`v!O{dt4)u/!", contentTransferDTO7.getPath() );
        assertEquals( "27;t", contentTransferDTO7.getOriginUrl() );
        java.util.concurrent.atomic.AtomicReference<Object> objAtomicReference8 =
                        new java.util.concurrent.atomic.AtomicReference<Object>();
        boolean boolean9 = contentTransferDTO7.equals( objAtomicReference8 );
        assertFalse( boolean9 );

    }

    @Test
    public void test_equals_9() throws Throwable
    {
        StoreKey storeKey1 = StoreKey.fromString( "(MnYGZy4" );
        assertEquals( "(MnYGZy4", storeKey1.getName() );
        assertEquals( "maven", storeKey1.getPackageType() );
        StoreEffect storeEffect2 = StoreEffect.UPLOAD;
        TrackingKey trackingKey3 = new TrackingKey();
        AccessChannel accessChannel4 = AccessChannel.NATIVE;
        ContentTransferDTO contentTransferDTO7 =
                        new ContentTransferDTO( storeKey1, trackingKey3, accessChannel4, "Q4{X4`v!O{dt4)u/!", "27;t",
                                                storeEffect2 );
        assertEquals( "/Q4{X4`v!O{dt4)u/!", contentTransferDTO7.getPath() );
        assertEquals( "27;t", contentTransferDTO7.getOriginUrl() );
        AccessChannel accessChannel8 = AccessChannel.GENERIC_PROXY;
        StoreType storeType9 = StoreType.hosted;
        StoreKey storeKey12 = new StoreKey( PKG_TYPE_MAVEN, storeType9, "mpv" );
        assertEquals( "mpv", storeKey12.getName() );
        assertEquals( PKG_TYPE_MAVEN, storeKey12.getPackageType() );
        TrackedContentEntryDTO trackedContentEntryDTO14 = new TrackedContentEntryDTO( storeKey12, accessChannel8,
                                                                                      "org.commonjava.indy.service.tracking.model.dto.TrackedContentEntryDTO" );
        assertNull( trackedContentEntryDTO14.getMd5() );
        assertNull( trackedContentEntryDTO14.getSize() );
        assertNull( trackedContentEntryDTO14.getSha256() );
        assertEquals( "/org.commonjava.indy.service.tracking.model.dto.TrackedContentEntryDTO",
                      trackedContentEntryDTO14.getPath() );
        assertNull( trackedContentEntryDTO14.getOriginUrl() );
        assertNull( trackedContentEntryDTO14.getSha1() );
        assertNull( trackedContentEntryDTO14.getLocalUrl() );
        boolean boolean15 = contentTransferDTO7.equals( trackedContentEntryDTO14 );
        assertFalse( boolean15 );

    }

    @Test
    public void test_equals_10() throws Throwable
    {
        ContentTransferDTO contentTransferDTO0 = new ContentTransferDTO();
        boolean boolean1 = contentTransferDTO0.equals( contentTransferDTO0 );
        assertTrue( boolean1 );

    }

    @Test
    public void test_equals_11() throws Throwable
    {
        StoreKey storeKey1 = StoreKey.fromString( "(MnYGZy4" );
        assertEquals( "(MnYGZy4", storeKey1.getName() );
        assertEquals( "maven", storeKey1.getPackageType() );
        StoreEffect storeEffect2 = StoreEffect.UPLOAD;
        TrackingKey trackingKey3 = new TrackingKey();
        AccessChannel accessChannel4 = AccessChannel.NATIVE;
        ContentTransferDTO contentTransferDTO7 =
                        new ContentTransferDTO( storeKey1, trackingKey3, accessChannel4, "Q4{X4`v!O{dt4)u/!", "27;t",
                                                storeEffect2 );
        assertEquals( "/Q4{X4`v!O{dt4)u/!", contentTransferDTO7.getPath() );
        assertEquals( "27;t", contentTransferDTO7.getOriginUrl() );
        com.fasterxml.jackson.databind.ObjectMapper objectMapper8 = null;
        com.fasterxml.jackson.databind.ObjectMapper objectMapper9 = null;
        com.fasterxml.jackson.databind.ObjectMapper objectMapper10 = null;
        com.fasterxml.jackson.databind.ObjectMapper objectMapper11 = null;
        Object obj12 = new Object();
        org.commonjava.indy.service.tracking.data.metrics.TraceManager traceManager13 = null;
        org.commonjava.indy.service.tracking.jaxrs.DTOStreamingOutput dTOStreamingOutput14 =
                        new org.commonjava.indy.service.tracking.jaxrs.DTOStreamingOutput( objectMapper11, obj12,
                                                                                           traceManager13 );
        org.commonjava.indy.service.tracking.data.metrics.TraceManager traceManager15 = null;
        org.commonjava.indy.service.tracking.jaxrs.DTOStreamingOutput dTOStreamingOutput16 =
                        new org.commonjava.indy.service.tracking.jaxrs.DTOStreamingOutput( objectMapper10,
                                                                                           dTOStreamingOutput14,
                                                                                           traceManager15 );
        Class<?> wildcardClass17 = dTOStreamingOutput16.getClass();
        assertEquals( "org.commonjava.indy.service.tracking.jaxrs.DTOStreamingOutput", wildcardClass17.getName() );
        assertEquals( "org.commonjava.indy.service.tracking.jaxrs", wildcardClass17.getPackageName() );
        org.commonjava.indy.service.tracking.data.metrics.TraceManager traceManager18 = null;
        org.commonjava.indy.service.tracking.jaxrs.DTOStreamingOutput dTOStreamingOutput19 =
                        new org.commonjava.indy.service.tracking.jaxrs.DTOStreamingOutput( objectMapper9,
                                                                                           wildcardClass17,
                                                                                           traceManager18 );
        Class<?> wildcardClass20 = dTOStreamingOutput19.getClass();
        assertEquals( "org.commonjava.indy.service.tracking.jaxrs.DTOStreamingOutput", wildcardClass20.getName() );
        assertEquals( "org.commonjava.indy.service.tracking.jaxrs", wildcardClass20.getPackageName() );
        org.commonjava.indy.service.tracking.data.metrics.TraceManager traceManager21 = null;
        org.commonjava.indy.service.tracking.jaxrs.DTOStreamingOutput dTOStreamingOutput22 =
                        new org.commonjava.indy.service.tracking.jaxrs.DTOStreamingOutput( objectMapper8,
                                                                                           wildcardClass20,
                                                                                           traceManager21 );
        boolean boolean23 = contentTransferDTO7.equals( dTOStreamingOutput22 );
        assertFalse( boolean23 );

    }

    @Test
    public void test_equals_12() throws Throwable
    {
        StoreKey storeKey1 = StoreKey.fromString( "(MnYGZy4" );
        assertEquals( "(MnYGZy4", storeKey1.getName() );
        assertEquals( "maven", storeKey1.getPackageType() );
        StoreEffect storeEffect2 = StoreEffect.UPLOAD;
        TrackingKey trackingKey3 = new TrackingKey();
        AccessChannel accessChannel4 = AccessChannel.NATIVE;
        ContentTransferDTO contentTransferDTO7 =
                        new ContentTransferDTO( storeKey1, trackingKey3, accessChannel4, "Q4{X4`v!O{dt4)u/!", "27;t",
                                                storeEffect2 );
        assertEquals( "/Q4{X4`v!O{dt4)u/!", contentTransferDTO7.getPath() );
        assertEquals( "27;t", contentTransferDTO7.getOriginUrl() );
        com.datastax.driver.core.SocketOptions socketOptions8 = new com.datastax.driver.core.SocketOptions();
        boolean boolean9 = contentTransferDTO7.equals( socketOptions8 );
        assertFalse( boolean9 );

    }

    @Test
    public void test_equals_13() throws Throwable
    {
        StoreKey storeKey1 = StoreKey.fromString( "(MnYGZy4" );
        assertEquals( "(MnYGZy4", storeKey1.getName() );
        assertEquals( "maven", storeKey1.getPackageType() );
        StoreEffect storeEffect2 = StoreEffect.UPLOAD;
        TrackingKey trackingKey3 = new TrackingKey();
        AccessChannel accessChannel4 = AccessChannel.NATIVE;
        ContentTransferDTO contentTransferDTO7 =
                        new ContentTransferDTO( storeKey1, trackingKey3, accessChannel4, "Q4{X4`v!O{dt4)u/!", "27;t",
                                                storeEffect2 );
        assertEquals( "/Q4{X4`v!O{dt4)u/!", contentTransferDTO7.getPath() );
        assertEquals( "27;t", contentTransferDTO7.getOriginUrl() );
        TrackingKey trackingKey9 = new TrackingKey( "hi!" );
        assertEquals( "hi!", trackingKey9.getId() );
        boolean boolean10 = contentTransferDTO7.equals( trackingKey9 );
        assertFalse( boolean10 );

    }

    @Test
    public void test_equals_14() throws Throwable
    {
        StoreKey storeKey1 = StoreKey.fromString( "(MnYGZy4" );
        assertEquals( "(MnYGZy4", storeKey1.getName() );
        assertEquals( "maven", storeKey1.getPackageType() );
        StoreEffect storeEffect2 = StoreEffect.UPLOAD;
        TrackingKey trackingKey3 = new TrackingKey();
        AccessChannel accessChannel4 = AccessChannel.NATIVE;
        ContentTransferDTO contentTransferDTO7 =
                        new ContentTransferDTO( storeKey1, trackingKey3, accessChannel4, "Q4{X4`v!O{dt4)u/!", "27;t",
                                                storeEffect2 );
        assertEquals( "/Q4{X4`v!O{dt4)u/!", contentTransferDTO7.getPath() );
        assertEquals( "27;t", contentTransferDTO7.getOriginUrl() );
        java.util.concurrent.atomic.AtomicBoolean atomicBoolean8 = new java.util.concurrent.atomic.AtomicBoolean();
        boolean boolean9 = contentTransferDTO7.equals( atomicBoolean8 );
        assertFalse( boolean9 );

    }

    @Test
    public void test_equals_15() throws Throwable
    {
        StoreKey storeKey1 = StoreKey.fromString( "(MnYGZy4" );
        assertEquals( "(MnYGZy4", storeKey1.getName() );
        assertEquals( "maven", storeKey1.getPackageType() );
        StoreEffect storeEffect2 = StoreEffect.UPLOAD;
        TrackingKey trackingKey3 = new TrackingKey();
        AccessChannel accessChannel4 = AccessChannel.NATIVE;
        ContentTransferDTO contentTransferDTO7 =
                        new ContentTransferDTO( storeKey1, trackingKey3, accessChannel4, "Q4{X4`v!O{dt4)u/!", "27;t",
                                                storeEffect2 );
        assertEquals( "/Q4{X4`v!O{dt4)u/!", contentTransferDTO7.getPath() );
        assertEquals( "27;t", contentTransferDTO7.getOriginUrl() );
        org.commonjava.indy.service.tracking.model.TrackedContentEntry trackedContentEntry8 =
                        new org.commonjava.indy.service.tracking.model.TrackedContentEntry();
        boolean boolean9 = contentTransferDTO7.equals( trackedContentEntry8 );
        assertFalse( boolean9 );

    }

    @Test
    public void test_setPath_0() throws Throwable
    {
        ContentTransferDTO contentTransferDTO0 = new ContentTransferDTO();
        contentTransferDTO0.setPath( "vi" );
        assertEquals( "/vi", contentTransferDTO0.getPath() );
        assertNull( contentTransferDTO0.getOriginUrl() );

    }

    @Test
    public void test_setEffect_0() throws Throwable
    {
        ContentTransferDTO contentTransferDTO0 = new ContentTransferDTO();
        StoreEffect storeEffect1 = StoreEffect.UPLOAD;
        contentTransferDTO0.setEffect( storeEffect1 );
        assertNull( contentTransferDTO0.getPath() );
        assertNull( contentTransferDTO0.getOriginUrl() );

    }

    @Test
    public void test_setOriginUrl_0() throws Throwable
    {
        ContentTransferDTO contentTransferDTO0 = new ContentTransferDTO();
        contentTransferDTO0.setOriginUrl( "/MELe*A>,zm\"M+%] \"" );
        assertNull( contentTransferDTO0.getPath() );
        assertEquals( "/MELe*A>,zm\"M+%] \"", contentTransferDTO0.getOriginUrl() );

    }

    @Test
    public void test_setTrackingKey_0() throws Throwable
    {
        ContentTransferDTO contentTransferDTO0 = new ContentTransferDTO();
        TrackingKey trackingKey1 = null;
        contentTransferDTO0.setTrackingKey( trackingKey1 );
        assertNull( contentTransferDTO0.getPath() );
        assertNull( contentTransferDTO0.getOriginUrl() );

    }

    @Test
    public void test_ContentTransferDTO_0() throws Throwable
    {
        StoreKey storeKey0 = new StoreKey();
        TrackingKey trackingKey2 = new TrackingKey( "-9." );
        assertEquals( "-9.", trackingKey2.getId() );
        AccessChannel accessChannel3 = AccessChannel.NATIVE;
        StoreEffect storeEffect4 = StoreEffect.UPLOAD;
        ContentTransferDTO contentTransferDTO7 =
                        new ContentTransferDTO( storeKey0, trackingKey2, accessChannel3, "key_f46", "vi",
                                                storeEffect4 );
        assertEquals( "/key_f46", contentTransferDTO7.getPath() );
        assertEquals( "vi", contentTransferDTO7.getOriginUrl() );

    }

    @Test
    public void test_setAccessChannel_0() throws Throwable
    {
        ContentTransferDTO contentTransferDTO0 = new ContentTransferDTO();
        ContentTransferDTO contentTransferDTO1 = new ContentTransferDTO();
        AccessChannel accessChannel2 = AccessChannel.GENERIC_PROXY;
        contentTransferDTO1.setAccessChannel( accessChannel2 );
        assertNull( contentTransferDTO1.getPath() );
        assertNull( contentTransferDTO1.getOriginUrl() );

    }

    private Object getFieldValue( Object obj, String fieldName )
                    throws java.lang.reflect.InvocationTargetException, SecurityException, IllegalArgumentException,
                    IllegalAccessException
    {
        try
        {
            java.lang.reflect.Field field = obj.getClass().getField( fieldName );
            return field.get( obj );
        }
        catch ( NoSuchFieldException e )
        {
            for ( java.lang.reflect.Method publicMethod : obj.getClass().getMethods() )
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
