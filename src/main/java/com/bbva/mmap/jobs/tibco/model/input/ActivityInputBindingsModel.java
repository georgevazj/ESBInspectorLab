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
    private ActivityInputBindingsInputElement activityInputBindingsInputElement;
    private ActivityInputBindingsTargetServicesModel activityInputBindingsTargetServicesModel;
    private ActivityInputBindingsARCREQSModel activityInputBindingsARCREQSModel;


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

    @XmlElement(name = "Input")
    public ActivityInputBindingsInputElement getActivityInputBindingsInputElement() {
        return activityInputBindingsInputElement;
    }

    public void setActivityInputBindingsInputElement(ActivityInputBindingsInputElement activityInputBindingsInputElement) {
        this.activityInputBindingsInputElement = activityInputBindingsInputElement;
    }

    @XmlElement(name = "TargetServices")
    public ActivityInputBindingsTargetServicesModel getActivityInputBindingsTargetServicesModel() {
        return activityInputBindingsTargetServicesModel;
    }

    public void setActivityInputBindingsTargetServicesModel(ActivityInputBindingsTargetServicesModel activityInputBindingsTargetServicesModel) {
        this.activityInputBindingsTargetServicesModel = activityInputBindingsTargetServicesModel;
    }

    @XmlElement(name = "ARC_REQS_Main_IN")
    public ActivityInputBindingsARCREQSModel getActivityInputBindingsARCREQSModel() {
        return activityInputBindingsARCREQSModel;
    }

    public void setActivityInputBindingsARCREQSModel(ActivityInputBindingsARCREQSModel activityInputBindingsARCREQSModel) {
        this.activityInputBindingsARCREQSModel = activityInputBindingsARCREQSModel;
    }
}
