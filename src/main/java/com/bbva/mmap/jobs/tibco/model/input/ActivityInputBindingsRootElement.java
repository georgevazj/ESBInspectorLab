package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by jorge on 06/05/2016.
 */

@XmlRootElement(name = "Root")
public class ActivityInputBindingsRootElement {

    private ActivityInputBindingsRootAdapterChannel activityInputBindingsRootAdapterChannel;

    @XmlElement(name = "AdapterChannelParameters")
    public ActivityInputBindingsRootAdapterChannel getActivityInputBindingsRootAdapterChannel() {
        return activityInputBindingsRootAdapterChannel;
    }

    public void setActivityInputBindingsRootAdapterChannel(ActivityInputBindingsRootAdapterChannel activityInputBindingsRootAdapterChannel) {
        this.activityInputBindingsRootAdapterChannel = activityInputBindingsRootAdapterChannel;
    }
}
