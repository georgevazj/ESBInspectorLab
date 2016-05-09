package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by jorge on 09/05/2016.
 */

@XmlRootElement(name = "Service")
public class ActivityInputBindingsServiceModel {

    private DestinationValueOfModel destinationValueOfModel;

    @XmlElement(name = "value-of")
    public DestinationValueOfModel getDestinationValueOfModel() {
        return destinationValueOfModel;
    }

    public void setDestinationValueOfModel(DestinationValueOfModel destinationValueOfModel) {
        this.destinationValueOfModel = destinationValueOfModel;
    }
}
