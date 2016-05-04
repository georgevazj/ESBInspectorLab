package com.bbva.mmap.jobs.tibco.model.input.ems;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Jorge on 28/4/16.
 */

public class NamingEnvironmentModel {

    private String providerUrl;

    @XmlElement(name ="ProviderURL")
    public String getProviderUrl() {
        return providerUrl;
    }

    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }

}
