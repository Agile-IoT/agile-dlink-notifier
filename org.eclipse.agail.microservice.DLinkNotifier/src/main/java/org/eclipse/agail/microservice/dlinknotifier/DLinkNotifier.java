package org.eclipse.agail.microservice.dlinknotifier;

import org.eclipse.agail.microservice.dlinknotifier.rest.RestServer;
import org.eclipse.agail.microservice.dlinknotifier.db.Notification;
import org.eclipse.agail.microservice.dlinknotifier.db.NotificationsDb;

import java.net.HttpCookie;
import java.net.CookieManager;
import java.net.CookieHandler;

import com.google.gson.JsonElement;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.NullLogger;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.transport.WebsocketTransport;
import microsoft.aspnet.signalr.client.StateChangedCallback;
import microsoft.aspnet.signalr.client.ConnectionState;

import org.eclipse.agail.microservice.dlinknotifier.webapi.SStackWebApiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DLinkNotifier {

  private final Logger logger = LoggerFactory.getLogger(DLinkNotifier.class);

  private HubConnection con;
  private HubProxy eventHub;

  private static final String API_URL = "https://encontrol.io";
  private static final String USER_ID = "[ENCONTROL USER ID HERE]";
  private static final String GW_API_HEADER = "enControl-Api-Key";
  private static final String GW_API_KEY = "ENCONTROL API KEY HERE";
  private static final String JOIN_GROUP = "joinGroup";
  private static final String NEW_EVENT = "receiveNewEvent";

  private SStackWebApiConsumer client;
  private RestServer restServer;

  public DLinkNotifier() {
    System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
    System.setProperty("org.eclipse.jetty.LEVEL", "OFF");

    new RestServer();

    con = new HubConnection(API_URL, "", true, new NullLogger());

    CookieManager cmanager = new CookieManager();
    CookieHandler.setDefault(cmanager);

    client = new SStackWebApiConsumer();

    client.authenticate();

    for(HttpCookie cookie: cmanager.getCookieStore().getCookies()){
      con.addHeader("X-" + cookie.getName(), cookie.getValue());
    }

    con.addHeader(GW_API_HEADER, GW_API_KEY);

    con.stateChanged(new StateChangedCallback() {
      @Override
      public void stateChanged(ConnectionState oldState, ConnectionState newState){
        logger.debug("Connection changed from {} to {}", oldState, newState);
      }
    });

    con.connected(new Runnable() {

      @Override
      public void run(){
        eventHub.invoke(JOIN_GROUP, USER_ID);
        eventHub.subscribe(NEW_EVENT).addReceivedHandler(new Action<JsonElement[]>() {
          @Override
          public void run(JsonElement[] params) throws Exception {
            try {
              if(params[1].getAsString().equals("custom")){
                logger.debug("New event received");
                logger.debug("Event Id -> {}", params[0].getAsString());
                logger.debug("Type -> {}", params[1].getAsString());
                logger.debug("Subtype -> {}", params[2].getAsString());
                logger.debug("Installation Name -> {}", params[3].getAsString());
                logger.debug("Installation Id -> {}", params[4].getAsString());
                logger.debug("Description -> {}", params[5].getAsString());
                logger.debug("Pending -> {}", params[6].getAsString());
                logger.debug("Date -> {}", params[7].getAsString());
                logger.debug("Camera Trigger -> {}", params[8].getAsString());

                Notification n = new Notification();
                n.setId(params[0].getAsString());
                n.setType(params[1].getAsString());
                n.setSubtype(params[2].getAsString());
                n.setInstallation(params[3].getAsString());
                n.setInstallationId(params[4].getAsString());
                n.setDescription(params[5].getAsString());
                n.setPending(params[6].getAsBoolean());
                n.setDate(params[7].getAsString());
                n.setCameraTrigger(params[8].getAsString());

                NotificationsDb.getInstance().save(n);
              }
            } catch (Exception e){
              logger.debug("Exception", e);
            }
          }
        });
      }

    });

    eventHub = con.createHubProxy("eventHub");

    con.start(new WebsocketTransport(new NullLogger()));
  }

  public static void main(String[] args) {
    new DLinkNotifier();
  }

 }
