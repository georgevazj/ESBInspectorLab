package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by jorge on 10/05/2016.
 */

@XmlRootElement(name = "returnBindings")
public class ReturnBindingsModel {

    private ActivityInputBindingsTargetServicesModel activityInputBindingsTargetServicesModel;

    @XmlElement(name = "TargetServices")
    public ActivityInputBindingsTargetServicesModel getActivityInputBindingsTargetServicesModel() {
        return activityInputBindingsTargetServicesModel;
    }

    public void setActivityInputBindingsTargetServicesModel(ActivityInputBindingsTargetServicesModel activityInputBindingsTargetServicesModel) {
        this.activityInputBindingsTargetServicesModel = activityInputBindingsTargetServicesModel;
    }
}
