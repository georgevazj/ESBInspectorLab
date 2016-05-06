package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Jorge on 28/4/16.
 */

@XmlRootElement(name = "inputBindings")
public class ActivityInputBindingsModel {

    private ActivityInputModel activityInputModel;
    private ActivityInputBindingsRootElement activityInputBindingsRootElement;

    @XmlElement(name = "ActivityInput")
    public ActivityInputModel getActivityInputModel() {
        return activityInputModel;
    }

    public void setActivityInputModel(ActivityInputModel activityInputModel) {
        this.activityInputModel = activityInputModel;
    }

    @XmlElement(name = "Root")
    public ActivityInputBindingsRootElement getActivityInputBindingsRootElement() {
        return activityInputBindingsRootElement;
    }

    public void setActivityInputBindingsRootElement(ActivityInputBindingsRootElement activityInputBindingsRootElement) {
        this.activityInputBindingsRootElement = activityInputBindingsRootElement;
    }
}
