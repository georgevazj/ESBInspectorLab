package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by jorge on 09/05/2016.
 */

@XmlRootElement(name = "TargetServices")
public class ActivityInputBindingsTargetServicesModel {

    private ActivityInputBindingsServiceModel activityInputBindingsServiceModel;

    @XmlElement(name = "Service")
    public ActivityInputBindingsServiceModel getActivityInputBindingsServiceModel() {
        return activityInputBindingsServiceModel;
    }

    public void setActivityInputBindingsServiceModel(ActivityInputBindingsServiceModel activityInputBindingsServiceModel) {
        this.activityInputBindingsServiceModel = activityInputBindingsServiceModel;
    }
}
