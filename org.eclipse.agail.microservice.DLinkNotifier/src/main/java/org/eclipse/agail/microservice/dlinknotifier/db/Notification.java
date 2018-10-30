package org.eclipse.agail.microservice.dlinknotifier.db;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "notifications", schemaVersion= "1.0")
public class Notification {
    @Id
    private String id;
    private String type;
    private String subtype;
    private String installation;
    private String installationId;
    private String description;
    private Boolean pending;
    private String date;
    private String cameraTrigger;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getInstallation() {
        return installation;
    }

    public void setInstallation(String installation) {
        this.installation = installation;
    }

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getPending() {
        return pending;
    }

    public void setPending(Boolean pending) {
        this.pending = pending;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCameraTrigger() {
        return cameraTrigger;
    }

    public void setCameraTrigger(String cameraTrigger) {
        this.cameraTrigger = cameraTrigger;
    }
}