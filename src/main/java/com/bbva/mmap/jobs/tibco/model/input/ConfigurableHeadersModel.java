package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by jorge on 15/04/2016.
 */
public class ConfigurableHeadersModel {

    private String jmsDeliveryMode;
    private String jmsExpiration;
    private String jmsPriority;
    private String eventSource;

    @XmlElement(name ="JMSDeliveryMode")
    public String getJmsDeliveryMode() {
        return jmsDeliveryMode;
    }

    public void setJmsDeliveryMode(String jmsDeliveryMode) {
        this.jmsDeliveryMode = jmsDeliveryMode;
    }

    @XmlElement(name = "JMSExpiration")
    public String getJmsExpiration() {
        return jmsExpiration;
    }

    public void setJmsExpiration(String jmsExpiration) {
        this.jmsExpiration = jmsExpiration;
    }

    public String getJmsPriority() {
        return jmsPriority;
    }

    @XmlElement(name ="JMSPriority")
    public void setJmsPriority(String jmsPriority) {
        this.jmsPriority = jmsPriority;
    }

    @XmlElement(name = "EventSource")
    public String getEventSource() {
        return eventSource;
    }

    public void setEventSource(String eventSource) {
        this.eventSource = eventSource;
    }
}
