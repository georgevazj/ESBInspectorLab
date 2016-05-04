package com.bbva.mmap.jobs.tibco.model.input;

import com.bbva.mmap.jobs.tibco.model.input.ems.EMSResourceModel;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 14/04/2016.
 */

public class ActivityConfigModel {

    private String processName;
    //private String permittedMessageType;
    private String connectionReference;
    //private List<ConfigurableHeadersModel> configurableHeadersModels;
    private List<SessionAttributesModel> sessionAttributesModels = new ArrayList<SessionAttributesModel>();

    private EMSResourceModel emsResourceModel;

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    /**@XmlElement(name = "PermittedMessageType")
    public String getPermittedMessageType() {
        return permittedMessageType;
    }

    public void setPermittedMessageType(String permittedMessageType) {
        this.permittedMessageType = permittedMessageType;
    }**/

    @XmlElement(name = "SessionAttributes")
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

    /**@XmlElement(name = "ConfigurableHeaders")
    public List<ConfigurableHeadersModel> getConfigurableHeadersModels() {
        return configurableHeadersModels;
    }

    public void setConfigurableHeadersModels(List<ConfigurableHeadersModel> configurableHeadersModels) {
        this.configurableHeadersModels = configurableHeadersModels;
    }**/
}
