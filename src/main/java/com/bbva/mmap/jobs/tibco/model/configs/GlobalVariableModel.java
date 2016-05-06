package com.bbva.mmap.jobs.tibco.model.configs;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by jorge on 06/05/2016.
 */

@XmlRootElement(name = "globalVariable")
public class GlobalVariableModel {

    private String name;
    private String value;

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
