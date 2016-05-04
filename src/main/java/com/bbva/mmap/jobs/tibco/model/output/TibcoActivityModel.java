package com.bbva.mmap.jobs.tibco.model.output;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by jorge on 18/04/2016.
 */

@XmlRootElement(name = "activity")
public class TibcoActivityModel {

    private String name;
    private String type;
    private TibcoEMSConnection tibcoEMSConnection;
    private String destination;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement(name = "connection")
    public TibcoEMSConnection getTibcoEMSConnection() {
        return tibcoEMSConnection;
    }

    public void setTibcoEMSConnection(TibcoEMSConnection tibcoEMSConnection) {
        this.tibcoEMSConnection = tibcoEMSConnection;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
