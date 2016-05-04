package com.bbva.mmap.jobs.tibco.model.input;

import com.bbva.mmap.jobs.tibco.model.input.ems.EMSResourceModel;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 14/04/2016.
 */

@XmlRootElement(name ="config")
public class StarterConfigModel {

    private String connectionReference;
    private List<SessionAttributesModel> sessionAttributesModels = new ArrayList<SessionAttributesModel>();
    private EMSResourceModel emsResourceModel;

    @XmlElement(name ="SessionAttributes")
    public List<SessionAttributesModel> getSessionAttributesModels() {
        return sessionAttributesModels;
    }

    public void setSessionAttributesModels(List<SessionAttributesModel> sessionAttributesModels) {
        this.sessionAttributesModels = sessionAttributesModels;
    }

    @XmlElement(name = "ConnectionReference")
    public String getConnectionReference() {
        return connectionReference;
    }

    public void setConnectionReference(String connectionReference) {
        this.connectionReference = connectionReference;
    }

    @XmlElement(name = "EMSConnection")
    public EMSResourceModel getEmsResourceModel() {
        return emsResourceModel;
    }

    public void setEmsResourceModel(EMSResourceModel emsResourceModel) {
        this.emsResourceModel = emsResourceModel;
    }
}
