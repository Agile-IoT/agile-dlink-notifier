package org.eclipse.agail.microservice.dlinknotifier.db;

import org.eclipse.agail.microservice.dlinknotifier.db.Notification;
import java.util.List;

import io.jsondb.JsonDBTemplate;
import java.io.File;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationsDb {

    private static final Logger logger = LoggerFactory.getLogger(NotificationsDb.class);

    private static NotificationsDb instance;
    private JsonDBTemplate jsonDBTemplate;

    private NotificationsDb() {
        try {
            String dbFilesLocation = "/usr/src/app/.dlink";
            String baseScanPackage = "org.eclipse.agail.microservice.dlinknotifier.db";

            File dir = new File(dbFilesLocation);

            if(!dir.exists()){
                dir.mkdirs();
            }

            jsonDBTemplate = new JsonDBTemplate(dbFilesLocation, baseScanPackage);

            if(!jsonDBTemplate.collectionExists(Notification.class)) {
                jsonDBTemplate.createCollection(Notification.class);
            }
        } catch (Exception e) {
            logger.debug("Exception creating NotificationsDB", e);
        }
    }

    public static synchronized NotificationsDb getInstance(){
        if(instance == null) {
            instance = new NotificationsDb();
        }

        return instance;
    }

    public void save(Notification n){
        jsonDBTemplate.insert(n);
    }

    public List<Notification> getAll() {
        return jsonDBTemplate.findAll(Notification.class);
    }

    public void clearDb() {
        jsonDBTemplate.dropCollection(Notification.class);
        jsonDBTemplate.createCollection(Notification.class);
    }
}