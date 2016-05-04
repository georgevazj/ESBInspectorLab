package com.bbva.mmap.jobs.tibco.model.input.ems;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Jorge on 28/4/16.
 */

@XmlRootElement(name = "config")
public class EMSConfigElementModel {

    private NamingEnvironmentModel namingEnvironmentModel;
    private ConnectionAttributesModel connectionAttributesModel;

    @XmlElement(name = "NamingEnvironment")
    public NamingEnvironmentModel getNamingEnvironmentModel() {
        return namingEnvironmentModel;
    }

    public void setNamingEnvironmentModel(NamingEnvironmentModel namingEnvironmentModel) {
        this.namingEnvironmentModel = namingEnvironmentModel;
    }

    @XmlElement(name = "ConnectionAttributes")
    public ConnectionAttributesModel getConnectionAttributesModel() {
        return connectionAttributesModel;
    }

    public void setConnectionAttributesModel(ConnectionAttributesModel connectionAttributesModel) {
        this.connectionAttributesModel = connectionAttributesModel;
    }

}
