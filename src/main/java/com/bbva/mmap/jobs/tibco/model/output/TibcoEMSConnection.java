package com.bbva.mmap.jobs.tibco.model.output;

/**
 * Created by jorge on 03/05/2016.
 */
public class TibcoEMSConnection {

    private String url;
    private String user;


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
