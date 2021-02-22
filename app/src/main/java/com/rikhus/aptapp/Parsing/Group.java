package com.rikhus.aptapp.Parsing;

public class Group {
    private String groupName;
    private String groupId;

    public String getGroupName() {return groupName;}
    public void setGroupName(String groupName) {this.groupName = groupName;}

    public String getGroupId() {return groupId;}
    public void setGroupId(String groupId) {this.groupId = groupId;}

    public Group(String groupName, String groupId) {
        this.groupName = groupName;
        this.groupId = groupId;
    }

    public Group() {
    }
}
