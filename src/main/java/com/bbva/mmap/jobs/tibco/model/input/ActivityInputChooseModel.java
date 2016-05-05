package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by jorge on 04/05/2016.
 */
public class ActivityInputChooseModel {

    private ActivityInputChooseWhenModel activityInputChooseWhenModel;
    private ActivityInputChooseOtherWiseModel activityInputChooseOtherWiseModel;

    @XmlElement(name = "when")
    public ActivityInputChooseWhenModel getActivityInputChooseWhenModel() {
        return activityInputChooseWhenModel;
    }

    public void setActivityInputChooseWhenModel(ActivityInputChooseWhenModel activityInputChooseWhenModel) {
        this.activityInputChooseWhenModel = activityInputChooseWhenModel;
    }

    @XmlElement(name = "otherwise")
    public ActivityInputChooseOtherWiseModel getActivityInputChooseOtherWiseModel() {
        return activityInputChooseOtherWiseModel;
    }

    public void setActivityInputChooseOtherWiseModel(ActivityInputChooseOtherWiseModel activityInputChooseOtherWiseModel) {
        this.activityInputChooseOtherWiseModel = activityInputChooseOtherWiseModel;
    }
}
