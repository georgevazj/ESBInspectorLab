package com.bbva.mmap.jobs.tibco.model.output;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 04/05/2016.
 */

@XmlRootElement(name = "service")
public class TibcoOutput {

    private List<TibcoOutputActivity> tibcoOutputActivities = new ArrayList<TibcoOutputActivity>();

    @XmlElement(name = "activity")
    public List<TibcoOutputActivity> getTibcoOutputActivities() {
        return tibcoOutputActivities;
    }

    public void setTibcoOutputActivities(List<TibcoOutputActivity> tibcoOutputActivities) {
        this.tibcoOutputActivities = tibcoOutputActivities;
    }
}
