package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by jorge on 06/05/2016.
 */

@XmlRootElement(name = "AdapterChannelParameters")
public class ActivityInputBindingsRootAdapterChannel {

    private TargetServiceModelIDModel targetServiceModelIDModel;

    @XmlElement(name = "TargetServiceId")
    public TargetServiceModelIDModel getTargetServiceModelIDModel() {
        return targetServiceModelIDModel;
    }

    public void setTargetServiceModelIDModel(TargetServiceModelIDModel targetServiceModelIDModel) {
        this.targetServiceModelIDModel = targetServiceModelIDModel;
    }
}
