package com.bbva.mmap.jobs.tibco.model.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ResourceAware;
import org.springframework.core.io.Resource;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jorge on 14/4/16.
 */

@XmlRootElement(name = "ProcessDefinition")
@XmlType(propOrder = {"applicationName","name","startName","starterModels","activityModels","groupModels"})
public class ProcessDefinitionModel implements ResourceAware{
    private static final Logger logger = LoggerFactory.getLogger(ProcessDefinitionModel.class);

    private String applicationName;
    private String name;
    private String startName;
    private List<StarterModel> starterModels = new ArrayList<StarterModel>();
    private List<ActivityModel> activityModels = new ArrayList<ActivityModel>();
    //private List<TransitionModel> transitionModels = new ArrayList<TransitionModel>();
    private List<GroupModel> groupModels = new ArrayList<GroupModel>();

    private File inputFile;

    @XmlAttribute(name ="application")
    public String getApplicationName() {
        return applicationName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    @XmlElement(name ="starter")
    public List<StarterModel> getStarterModels() {
        return starterModels;
    }

    public void setStarterModels(List<StarterModel> starterModels) {
        this.starterModels = starterModels;
    }

    @XmlElement(name ="activity")
    public List<ActivityModel> getActivityModels() {
        return activityModels;
    }

    public void setActivityModels(List<ActivityModel> activityModels) {
        this.activityModels = activityModels;
    }

    /**@XmlElement(name = "transition")
    public List<TransitionModel> getTransitionModels() {
        return transitionModels;
    }

    public void setTransitionModels(List<TransitionModel> transitionModels) {
        this.transitionModels = transitionModels;
    }**/

    @Override
    public void setResource(Resource resource) {
        String fileName = resource.getFilename();
        String[] fileNameSplit = fileName.split("__");
        this.applicationName = fileNameSplit[0];

        try {
            this.inputFile = resource.getFile();
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    @XmlElement(name = "group")
    public List<GroupModel> getGroupModels() {
        return groupModels;
    }

    public void setGroupModels(List<GroupModel> groupModels) {
        this.groupModels = groupModels;
    }

    public File getInputFile() {
        return inputFile;
    }
}
