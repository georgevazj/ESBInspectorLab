package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by jorge on 04/05/2016.
 */
public class DestinationTopicModel {

    private DestinationValueOfModel valueOfModel;

    @XmlElement(name = "value-of")
    public DestinationValueOfModel getValueOfModel() {
        return valueOfModel;
    }

    public void setValueOfModel(DestinationValueOfModel valueOfModel) {
        this.valueOfModel = valueOfModel;
    }

}
