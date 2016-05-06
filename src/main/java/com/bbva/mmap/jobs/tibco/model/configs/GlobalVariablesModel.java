package com.bbva.mmap.jobs.tibco.model.configs;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by jorge on 06/05/2016.
 */

@XmlRootElement(name = "globalVariables")
public class GlobalVariablesModel {

    private List<GlobalVariableModel> globalVariableModels;

    @XmlElement(name = "globalVariable")
    public List<GlobalVariableModel> getGlobalVariableModels() {
        return globalVariableModels;
    }

    public void setGlobalVariableModels(List<GlobalVariableModel> globalVariableModels) {
        this.globalVariableModels = globalVariableModels;
    }
}
