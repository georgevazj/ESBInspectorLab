package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by jorge on 04/05/2016.
 */
public class ActivityInputChooseWhenModel {

    private DestinationQueueModel destinationQueueModel;

    @XmlElement(name = "destinationQueue")
    public DestinationQueueModel getDestinationQueueModel() {
        return destinationQueueModel;
    }

    public void setDestinationQueueModel(DestinationQueueModel destinationQueueModel) {
        this.destinationQueueModel = destinationQueueModel;
    }
}
