package com.bbva.mmap.jobs.tibco.model.configs;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by jorge on 06/05/2016.
 */

@XmlRootElement(name = "Destination")
public class QueuesFileModel {

    private String serviceId;

    @XmlAttribute(name = "ServiceID")
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
