package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 14/04/2016.
 */

@XmlRootElement(name ="starter")
public class StarterModel {

    private String name;
    private String type;
    private List<StarterConfigModel> starterConfigModels = new ArrayList<StarterConfigModel>();

    public String getName() {
        return name;
    }

    @XmlAttribute(name ="name")
    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement(name = "config")
    public List<StarterConfigModel> getStarterConfigModels() {
        return starterConfigModels;
    }

    public void setStarterConfigModels(List<StarterConfigModel> starterConfigModels) {
        this.starterConfigModels = starterConfigModels;
    }
}
