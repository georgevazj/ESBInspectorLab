package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 14/04/2016.
 */

@XmlRootElement(name = "activity")
public class ActivityModel {

    private String name;
    private String type;
    private List<ActivityConfigModel> activityConfigModels = new ArrayList<ActivityConfigModel>();
    private ActivityInputBindingsModel activityInputBindingsModel;


    @XmlAttribute(name = "name")
    public String getName() {
        return name;
    }

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
    public List<ActivityConfigModel> getActivityConfigModels() {
        return activityConfigModels;
    }

    public void setActivityConfigModels(List<ActivityConfigModel> activityConfigModels) {
        this.activityConfigModels = activityConfigModels;
    }

    @XmlElement(name = "inputBindings")
    public ActivityInputBindingsModel getActivityInputBindingsModel() {
        return activityInputBindingsModel;
    }

    public void setActivityInputBindingsModel(ActivityInputBindingsModel activityInputBindingsModel) {
        this.activityInputBindingsModel = activityInputBindingsModel;
    }
}
