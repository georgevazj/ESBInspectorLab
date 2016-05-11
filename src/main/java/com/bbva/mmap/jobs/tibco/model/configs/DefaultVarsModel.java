package com.bbva.mmap.jobs.tibco.model.configs;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by jorge on 06/05/2016.
 */

@XmlRootElement(name = "repository")
public class DefaultVarsModel {

    private GlobalVariablesModel globalVariablesModel;

    @XmlElement(name = "globalVariables")
    public GlobalVariablesModel getGlobalVariablesModel() {
        return globalVariablesModel;
    }

    public void setGlobalVariablesModel(GlobalVariablesModel globalVariablesModel) {
        this.globalVariablesModel = globalVariablesModel;
    }
}
