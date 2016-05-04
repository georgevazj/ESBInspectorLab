package com.bbva.mmap.jobs.tibco.model.output;

import com.bbva.mmap.jobs.tibco.model.input.ActivityModel;
import com.bbva.mmap.jobs.tibco.model.input.StarterModel;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 18/04/2016.
 */

@XmlRootElement(name = "service")
@XmlType(propOrder = {"service","application","domain","uuaa","starterModel","activityModels"})
public class TibcoDataModel {

    private String uuaa;
    private String domain;
    private String application;
    private String service;
    private StarterModel starterModel;
    private List<ActivityModel> activityModels = new ArrayList<ActivityModel>();

    @XmlAttribute(name = "uuaa")
    public String getUuaa() {
        return uuaa;
    }

    public void setUuaa(String uuaa) {
        this.uuaa = uuaa;
    }

    @XmlAttribute(name = "domain")
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @XmlAttribute(name = "application")
    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    @XmlAttribute(name = "name")
    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }


    @XmlElement(name = "starter")
    public StarterModel getStarterModel() {
        return starterModel;
    }

    public void setStarterModel(StarterModel starterModel) {
        this.starterModel = starterModel;
    }

    @XmlElement(name = "activity")
    public List<ActivityModel> getActivityModels() {
        return activityModels;
    }

    public void setActivityModels(List<ActivityModel> activityModels) {
        this.activityModels = activityModels;
    }
}
