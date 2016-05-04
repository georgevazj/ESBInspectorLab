package com.bbva.mmap.jobs.tibco.model.input;

import javax.xml.bind.annotation.XmlType;

/**
 * Created by jorge on 14/04/2016.
 */

@XmlType(propOrder = {"from","to","conditionType"})
public class TransitionModel {

    private String from;
    private String to;
    private String conditionType;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }
}
