/*!
 * Copyright 2018 Webdetails, a Hitachi Vantara company. All rights reserved.
 *
 * This software was developed by Webdetails and is provided under the terms
 * of the Mozilla Public License, Version 2.0, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */

package pt.webdetails.cda.push;

import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;
import io.reactivex.Observable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pentaho.platform.api.engine.IPentahoRequestContext;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoRequestContextHolder;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.StandaloneSession;
import org.pentaho.platform.engine.security.SecurityHelper;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import pt.webdetails.cda.CdaUtils;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Websocket implementation for CDA queries.
 * This Endpoint is registered in plugin.spring.xml file by means of the {@link org.pentaho.platform.web.websocket.EndpointConfig} bean.
 *
 * The endpoint will be created in ws://<server_host_and_port>/pentaho/<plugin_id>/websocket/<key>. The plugin_id is this plugin id (cda),
 * and the key is the key used for the map entry in plugin.spring.xml configuration.
 */
public class CdaPushQuery extends Endpoint {

  private static final Log logger = LogFactory.getLog( CdaPushQuery.class );

  private static Map<String, QueryHandler> runningQueries = Collections.synchronizedMap( new HashMap() );

  /**
   * This method is called whenever a client creates a new websocket connection to the URL registered to
   * this endpoint.
   *
   * @param session the websocket session.
   * @param endpointConfig the endpoint configuration.
   */
  @Override
  public void onOpen( Session session, EndpointConfig endpointConfig ) {
    IPentahoRequestContext requestContext = PentahoRequestContextHolder.getRequestContext();
    QueryHandler queryHandler = new QueryHandler( session, requestContext );
    session.addMessageHandler( queryHandler );
    runningQueries.put( session.getId(), queryHandler );
  }

  /**
   * Executed whenever a connected websocket connection is terminated or timeouts according
   * to the web server or container configurations (the connection is not kept alive forever
   * to better manage server resources, so it is disconnected after a defined period of time - timeout).
   *
   * @param session the websocket session.
   * @param closeReason the reason for closing the connection.
   */
  @Override public void onClose( Session session, CloseReason closeReason ) {
    runningQueries.remove( session.getId() );
    super.onClose( session, closeReason );
  }

  /**
   * Executed whenever there is an error associated with the websocket connection
   *
   * @param session the websocket session.
   * @param throwable the {@link Throwable} that occurred.
   */
  @Override public void onError( Session session, Throwable throwable ) {
    if ( logger.isDebugEnabled() ) {
      logger.debug( "Error occurred in session id " + session.getId(), throwable );
    }
    super.onError( session, throwable );
  }

  /**
   * This is the class that handles messages with queries to execute and then sends the results back to the
   * clients of the websocket server endpoint.
   */
  private class QueryHandler implements MessageHandler.Whole<String> {
    private Session session;
    private IPentahoRequestContext requestContext;
    private CdaUtils cdaUtils = new CdaUtils();

    public QueryHandler( Session session, IPentahoRequestContext requestContext ) {
      this.session = session;
      this.requestContext = requestContext;
    }

    /**
     * Place here all the initializations that should be done, whenever a new
     * message is received on the websocket.
     */
    private void initOnMessage() {
      PentahoRequestContextHolder.setRequestContext( requestContext );

      AbstractAuthenticationToken principal = (AbstractAuthenticationToken) session.getUserPrincipal();
      if ( principal.isAuthenticated() ) {
        IPentahoSession pentahoSession = new StandaloneSession( principal.getName() );
        PentahoSessionHolder.setSession( pentahoSession );
        SecurityHelper.getInstance().becomeUser( principal.getName() );
      }
    }

    /**
     * Place here all the wrapup code that should be executed when a message processing ends.
     */
    private void finnishOnMessage() throws IOException {

    }

    /**
     * This method executes the query sent by the client of this websocket.
     *
     * @param query The query to be executed, and sent over the websocket.
     */
    @Override
    public void onMessage( String query ) {
      try {
        initOnMessage();

        MultivaluedMap<String, String> params = new StringKeyIgnoreCaseMultivaluedMap<>();

        JSONObject jsonObject = new JSONObject( query );
        Iterator<?> keys = jsonObject.keys();

        while ( keys.hasNext() ) {
          String key = (String) keys.next();
          Object item = jsonObject.get( key );
          if ( item instanceof JSONArray ) {
            JSONArray itemArray = (JSONArray) item;
            for ( int i = 0; i < itemArray.length(); i++){
              params.add( key, itemArray.getString( i ) );
            }
          } else {
            params.add( key, jsonObject.getString( key ) );
          }
        }

        //TODO this is only simulating multiples queries being sent to the client, using the same websocket connection
        Observable.interval(500, TimeUnit.MILLISECONDS).timeInterval().
          doOnComplete( () -> finnishOnMessage() ).
          subscribe( longTimed -> {
            try{
              StreamingOutput streamingOutput = cdaUtils.doQuery( params, null );
              OutputStream outputStream = new ByteArrayOutputStream( );
              streamingOutput.write( outputStream );
              synchronized ( session ) {
                if ( session.isOpen() ) {
                  session.getBasicRemote().sendText( outputStream.toString() );
                }
              }
            } catch ( IOException e ) {
              logger.error( "Error processing result query message to then send it. Closing websocket...", e );
              try {
                session.getBasicRemote().sendText( e.getMessage() );
              } catch ( IOException ioException ) {
                logger.error( "Error writing to websocket", ioException );
              }
              session.close();
              throw new RuntimeException( e );
            }
          } );

//        StreamingOutput streamingOutput = cdaUtils.doQuery( params, null );
//        OutputStream outputStream = new ByteArrayOutputStream(  );
//        streamingOutput.write( outputStream );
//        session.getBasicRemote().sendText( outputStream.toString() );

      } catch ( JSONException e ) {
        logger.error( "Error processing JSON message. Closing websocket...", e );
        try {
          session.getBasicRemote().sendText( e.getMessage() );
          session.close();
        } catch ( IOException ioException ) {
          logger.error( "Error writing to websocket", ioException );
        }
      }
    }
  }
}
