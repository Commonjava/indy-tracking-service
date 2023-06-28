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
package org.commonjava.indy.service.tracking.controller;

import io.quarkus.test.junit.QuarkusTest;
import org.commonjava.indy.service.tracking.data.cassandra.CassandraTrackingQuery;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class AdminControllerTest
{
    @Test
    public void test_importRecordZip_0() throws Throwable
    {
        CassandraTrackingQuery cassandraTrackingQuery0 = null;
        AdminController adminController1 = new AdminController( cassandraTrackingQuery0 );
        assertEquals( "folo-sealed.zip", getFieldValue( adminController1, "FOLO_SEALED_ZIP" ) );
        assertEquals( "folo", getFieldValue( adminController1, "FOLO_DIR" ) );
        byte[] byteArray2 = new byte[] {};
        java.io.ByteArrayInputStream byteArrayInputStream3 = new java.io.ByteArrayInputStream( byteArray2 );
        adminController1.importRecordZip( byteArrayInputStream3 );
        assertEquals( "folo-sealed.zip", getFieldValue( adminController1, "FOLO_SEALED_ZIP" ) );
        assertEquals( "folo", getFieldValue( adminController1, "FOLO_DIR" ) );

    }

    @Test
    public void test_AdminController_0() throws Throwable
    {
        CassandraTrackingQuery cassandraTrackingQuery0 = null;
        AdminController adminController1 = new AdminController( cassandraTrackingQuery0 );
        assertEquals( "folo-sealed.zip", getFieldValue( adminController1, "FOLO_SEALED_ZIP" ) );
        assertEquals( "folo", getFieldValue( adminController1, "FOLO_DIR" ) );

    }

    private Object getFieldValue( Object obj, String fieldName )
                    throws java.lang.reflect.InvocationTargetException, SecurityException, IllegalArgumentException,
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
