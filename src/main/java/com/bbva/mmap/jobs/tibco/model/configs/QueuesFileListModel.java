package com.bbva.mmap.jobs.tibco.model.configs;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 06/05/2016.
 */

@XmlRootElement(name = "QueuesCacheList")
public class QueuesFileListModel {

    List<String> destination = new ArrayList<String>();

    @XmlElement(name = "Destination")
    public List<String> getDestination() {
        return destination;
    }

    public void setDestination(List<String> destination) {
        this.destination = destination;
    }
}
