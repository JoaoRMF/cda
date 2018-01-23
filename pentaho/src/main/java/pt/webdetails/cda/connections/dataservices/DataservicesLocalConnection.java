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

/*
 * Copyright 2018 Hitachi Vantara. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */
package pt.webdetails.cda.connections.dataservices;

import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.DriverConnectionProvider;

public class DataservicesLocalConnection implements IDataservicesLocalConnection {

  public DriverConnectionProvider getDriverConnectionProvider(){
    final DriverConnectionProvider connectionProvider = new DataservicesDriverLocalConnectionProvider();
    //fix the server port and app context in the url below - this are the "default" values
    connectionProvider.setDriver( "org.pentaho.di.trans.dataservice.jdbc.ThinDriver" );
    connectionProvider.setUrl( "jdbc:pdi://localhost:8080/pentaho/kettle?local=true" );

    return connectionProvider;
  }
}
