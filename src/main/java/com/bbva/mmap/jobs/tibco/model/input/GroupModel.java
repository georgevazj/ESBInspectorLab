package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by jorge on 04/05/2016.
 */

@XmlRootElement(name = "group")
public class GroupModel {

    private String name;
    private List<ActivityModel> activityModels;

    @XmlElement(name = "activity")
    public List<ActivityModel> getActivityModels() {
        return activityModels;
    }

    public void setActivityModels(List<ActivityModel> activityModels) {
        this.activityModels = activityModels;
    }

    @XmlAttribute(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
