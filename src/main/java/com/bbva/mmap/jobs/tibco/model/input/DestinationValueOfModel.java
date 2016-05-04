package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Jorge on 28/4/16.
 */
@XmlRootElement(name = "value-of")
public class DestinationValueOfModel {

    private String select;

    @XmlAttribute(name = "select")
    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }
}
