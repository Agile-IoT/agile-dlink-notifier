package org.eclipse.agail.microservice.dlinknotifier.rest;

import static spark.Spark.*;
import org.eclipse.agail.microservice.dlinknotifier.db.NotificationsDb;
import org.eclipse.agail.microservice.dlinknotifier.rest.JsonTransformer;

public class RestServer {

    public RestServer() {
        get("/notifications", "application/json", (req, res) -> {
            return NotificationsDb.getInstance().getAll();
        }, new JsonTransformer());

        get("/clear", "application/json", (req, res) -> {
           NotificationsDb.getInstance().clearDb();
           return "{\"result\":\"ok\"}";
        });
    }

}