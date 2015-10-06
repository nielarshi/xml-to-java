
package com.xmltojava.model;

import java.util.ArrayList;

public class Note {

    private java.lang.String body;
    private java.lang.String to;
    private ArrayList<String> headingList;
    private java.lang.String from;

    public java.lang.String getBody() {
        return body;
    }

    public void setBody(java.lang.String body) {
        this.body = body;
    }

    public java.lang.String getTo() {
        return to;
    }

    public void setTo(java.lang.String to) {
        this.to = to;
    }

    public ArrayList<String> getHeadingList() {
        return headingList;
    }

    public void setHeadingList(ArrayList<String> headingList) {
        this.headingList = headingList;
    }

    public java.lang.String getFrom() {
        return from;
    }

    public void setFrom(java.lang.String from) {
        this.from = from;
    }

}
