package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by jorge on 10/05/2016.
 */

@XmlRootElement(name = "ProcessDefinition")
public class ReturnBindingsModelRoot {

    private ReturnBindingsModel returnBindingsModel;

    @XmlElement(name = "returnBindings")
    public ReturnBindingsModel getReturnBindingsModel() {
        return returnBindingsModel;
    }

    public void setReturnBindingsModel(ReturnBindingsModel returnBindingsModel) {
        this.returnBindingsModel = returnBindingsModel;
    }
}
