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

package pt.webdetails.cda.dataaccess;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import pt.webdetails.cda.connections.ConnectionCatalog.ConnectionType;
import pt.webdetails.cda.connections.InvalidConnectionException;
import pt.webdetails.cda.connections.dataservices.DataservicesConnection;
import pt.webdetails.cda.settings.UnknownConnectionException;

/**
 * Todo: Document me!
 */
public class DataservicesDataAccess extends SimpleDataAccess {

  private static final Log logger = LogFactory.getLog( DataservicesDataAccess.class );

  public DataservicesDataAccess( final Element element ) {
    super( element );
  }

  public DataservicesDataAccess() {
  }

  public DataFactory getDataFactory() throws UnknownConnectionException, InvalidConnectionException {
    final DataservicesConnection connection = (DataservicesConnection) getCdaSettings().getConnection( getConnectionId() );

//    final XPathDataFactory dataFactory = new XPathDataFactory();
//    dataFactory.setXqueryDataFile( connection.get );

    // incompatible versions of setQuery in 4.x and 5.x
//    legacyFallbackInvoke( dataFactory, "setQuery",
//      new Class<?>[] { String.class, String.class }, new Object[] { "query", getQuery() },
//      new Class<?>[] { String.class, String.class, boolean.class }, new Object[] { "query", getQuery(), true } );
//
//    return dataFactory;
    return null;
  }

//  private static boolean legacyFallbackInvoke(
//    Object object, String methodName,
//    Class<?>[] argTypes, Object[] args,
//    Class<?>[] argTypesFallback, Object[] argsFallback ) {
//    Method method = null;
//    try {
//      try {
//        method = object.getClass().getMethod( methodName, argTypes );
//      } catch ( NoSuchMethodException e1 ) {
//        logger.debug(
//          String.format( "failed to find %s(%s): ", methodName, ArrayUtils.toString( argTypes ),
//            e1.getLocalizedMessage() ) );
//        try {
//          method = object.getClass().getMethod( methodName, argTypesFallback );
//          args = argsFallback;
//        } catch ( NoSuchMethodException e2 ) {
//          logger.error(
//            String.format( "failed to find %1$s(%2$s) or %1$s(%3$s) ",
//              methodName,
//              ArrayUtils.toString( argTypes ),
//              ArrayUtils.toString( argTypesFallback ) ) );
//          throw e2;
//        }
//      }
//      method.invoke( object, args );
//      return true;
//    } catch ( Exception e ) {
//      logger.error( String.format( "%s call failed ", methodName ), e );
//    }
//    return false;
//  }

  public String getType() {
    return "dataservices";
  }

  @Override
  public ConnectionType getConnectionType() {
    return ConnectionType.DATASERVICES;
  }

  // this change allows xPath parameters parsing
  @Override
  protected IDataSourceQuery performRawQuery( ParameterDataRow parameterDataRow ) throws QueryException {
//    String origQuery = query;
//
//    CdaPropertyLookupParser lookupParser = new CdaPropertyLookupParser( parameterDataRow );
//    query = lookupParser.translateAndLookup( query, parameterDataRow );
//    IDataSourceQuery dataSourceQuery = super.performRawQuery( parameterDataRow );
//    query = origQuery;
//    return ( dataSourceQuery );
    return null;
  }
}
