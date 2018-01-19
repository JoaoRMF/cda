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

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import java.util.List;
import java.util.Properties;

public class DataservicesConnectionInfo {

  private String driver;
  private String url;
  private Properties properties;

  public DataservicesConnectionInfo( final Element connection ) {

    final String driver = (String) connection.selectObject( "string(./Driver)" );
    final String url = (String) connection.selectObject( "string(./Url)" );

    if ( StringUtils.isEmpty( driver ) ) {
      throw new IllegalStateException( "A driver is mandatory" );
    }
    if ( StringUtils.isEmpty( url ) ) {
      throw new IllegalStateException( "A url is mandatory" );
    }

    setDriver( driver );
    setUrl( url );

    properties = new Properties();

    final List<?> list = connection.elements( "Property" );
    for ( int i = 0; i < list.size(); i++ ) {
      final Element childElement = (Element) list.get( i );
      final String name = childElement.attributeValue( "name" );
      final String text = childElement.getText();
      properties.put( name, text );
    }
  }

  public Properties getProperties() {
    return properties;
  }

  public String getDriver() {
    return driver;
  }

  public void setDriver( final String driver ) {
    this.driver = driver;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl( final String url ) {
    this.url = url;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final DataservicesConnectionInfo that = (DataservicesConnectionInfo) o;

    if ( driver != null ? !driver.equals( that.driver ) : that.driver != null ) {
      return false;
    }
    if ( url != null ? !url.equals( that.url ) : that.url != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = driver != null ? driver.hashCode() : 0;
    result = 31 * result + ( url != null ? url.hashCode() : 0 );
    return result;
  }
}
