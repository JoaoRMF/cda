/*
 *
 *  * Copyright 2018 Hitachi Vantara. All rights reserved.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *
 */
package pt.webdetails.cda.connections.dataservices;

import org.dom4j.Element;
import pt.webdetails.cda.connections.AbstractConnection;
import pt.webdetails.cda.connections.ConnectionCatalog;
import pt.webdetails.cda.connections.InvalidConnectionException;
import pt.webdetails.cda.dataaccess.PropertyDescriptor;

import java.util.ArrayList;

public class DataServicesConnection extends AbstractConnection {

  private DataServicesConnectionInfo connectionInfo;

  public DataServicesConnection( final Element connection ) throws InvalidConnectionException {
    super( connection );
  }

  public DataServicesConnection() {
  }

  public ConnectionCatalog.ConnectionType getGenericType() {
    return ConnectionCatalog.ConnectionType.DATASERVICES;
  }

  protected void initializeConnection( final Element connection ) throws InvalidConnectionException {
    connectionInfo = new DataServicesConnectionInfo( connection );
  }

  public String getType() {
    return "dataservices";
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final DataServicesConnection
      that = (DataServicesConnection) o;

    if ( connectionInfo != null ? !connectionInfo.equals( that.connectionInfo ) : that.connectionInfo != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return connectionInfo != null ? connectionInfo.hashCode() : 0;
  }

  @Override
  public ArrayList<PropertyDescriptor> getProperties() {
    ArrayList<PropertyDescriptor> properties = new ArrayList<PropertyDescriptor>();
    properties.add( new PropertyDescriptor( "connectionString", PropertyDescriptor.Type.STRING,
      PropertyDescriptor.Placement.CHILD ) );
    return properties;
  }

  public String getTypeForFile() {
    return "dataservices.DataServices";
  }

  public DataServicesConnectionInfo getConnectionInfo() {

    return connectionInfo;
  }
}
