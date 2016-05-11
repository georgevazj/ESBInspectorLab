package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by jorge on 11/05/2016.
 */

@XmlRootElement(name = "ResquestServiceParameters")
public class ActivityInputBindingsResquestServiceParameters {

    private ActivityInputBindingsServiceIdModel activityInputBindingsServiceIdModel;

    @XmlElement(name = "ServiceId")
    public ActivityInputBindingsServiceIdModel getActivityInputBindingsServiceIdModel() {
        return activityInputBindingsServiceIdModel;
    }

    public void setActivityInputBindingsServiceIdModel(ActivityInputBindingsServiceIdModel activityInputBindingsServiceIdModel) {
        this.activityInputBindingsServiceIdModel = activityInputBindingsServiceIdModel;
    }
}
