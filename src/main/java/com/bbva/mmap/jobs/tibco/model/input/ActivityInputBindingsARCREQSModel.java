package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by jorge on 11/05/2016.
 */

@XmlRootElement(name = "ARC_REQS_Main_IN")
public class ActivityInputBindingsARCREQSModel {

    private ActivityInputBindingsResquestServiceParameters activityInputBindingsResquestServiceParameters;

    @XmlElement(name = "ResquestServiceParameters")
    public ActivityInputBindingsResquestServiceParameters getActivityInputBindingsResquestServiceParameters() {
        return activityInputBindingsResquestServiceParameters;
    }

    public void setActivityInputBindingsResquestServiceParameters(ActivityInputBindingsResquestServiceParameters activityInputBindingsResquestServiceParameters) {
        this.activityInputBindingsResquestServiceParameters = activityInputBindingsResquestServiceParameters;
    }
}
