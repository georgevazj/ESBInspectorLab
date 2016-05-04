package com.bbva.mmap.jobs.tibco.model.input.ems;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Jorge on 28/4/16.
 */

@XmlRootElement(name = "BWSharedResource")
public class EMSResourceModel {

    private EMSConfigElementModel emsConfigElementModel;

    @XmlElement(name = "config")
    public EMSConfigElementModel getEmsConfigElementModel() {
        return emsConfigElementModel;
    }

    public void setEmsConfigElementModel(EMSConfigElementModel emsConfigElementModel) {
        this.emsConfigElementModel = emsConfigElementModel;
    }
}
