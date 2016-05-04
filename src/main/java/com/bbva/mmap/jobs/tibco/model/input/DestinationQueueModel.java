package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Jorge on 28/4/16.
 */
public class DestinationQueueModel {

    private DestinationValueOfModel valueOfModel;

    @XmlElement(name = "value-of")
    public DestinationValueOfModel getValueOfModel() {
        return valueOfModel;
    }

    public void setValueOfModel(DestinationValueOfModel valueOfModel) {
        this.valueOfModel = valueOfModel;
    }
}
