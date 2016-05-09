package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by jorge on 09/05/2016.
 */

@XmlRootElement(name = "Input")
public class ActivityInputBindingsInputElement {

    private ActivityInputBindingsFilterAdapterElement activityInputBindingsFilterAdapterElement;

    @XmlElement(name = "FilterAdapterParameters")
    public ActivityInputBindingsFilterAdapterElement getActivityInputBindingsFilterAdapterElement() {
        return activityInputBindingsFilterAdapterElement;
    }

    public void setActivityInputBindingsFilterAdapterElement(ActivityInputBindingsFilterAdapterElement activityInputBindingsFilterAdapterElement) {
        this.activityInputBindingsFilterAdapterElement = activityInputBindingsFilterAdapterElement;
    }
}
