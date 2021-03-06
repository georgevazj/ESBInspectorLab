package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Jorge on 28/4/16.
 */
public class ActivityInputModel {

    private DestinationQueueModel destinationQueueModel;
    private DestinationTopicModel destinationTopicModel;
    private ActivityInputChooseModel activityInputChooseModel;

    @XmlElement(name = "destinationQueue")
    public DestinationQueueModel getDestinationQueueModel() {
        return destinationQueueModel;
    }

    public void setDestinationQueueModel(DestinationQueueModel destinationQueueModel) {
        this.destinationQueueModel = destinationQueueModel;
    }

    @XmlElement(name = "destinationTopic")
    public DestinationTopicModel getDestinationTopicModel() {
        return destinationTopicModel;
    }

    public void setDestinationTopicModel(DestinationTopicModel destinationTopicModel) {
        this.destinationTopicModel = destinationTopicModel;
    }

    @XmlElement(name = "choose")
    public ActivityInputChooseModel getActivityInputChooseModel() {
        return activityInputChooseModel;
    }

    public void setActivityInputChooseModel(ActivityInputChooseModel activityInputChooseModel) {
        this.activityInputChooseModel = activityInputChooseModel;
    }
}
