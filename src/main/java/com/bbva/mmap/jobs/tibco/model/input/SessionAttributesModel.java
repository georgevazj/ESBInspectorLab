package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by jorge on 14/04/2016.
 */

@XmlRootElement(name = "SessionAttributes")
public class SessionAttributesModel {

    private String destination;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
