package com.rikhus.aptapp.Parsing;

public class Subject {
    private String subjectNumber = "";
    private String subjectName = "";
    private String subjectAuditorium = "";
    private String subjectTimeStart = "";
    private String subjectTimeEnd = "";
    private String subjectTeacher = "";

    public static enum SubjectType{
        FOR_ALL_SUBGROUPS,
        FOR_FIRST_SUBGROUP_ONLY,
        FOR_SECOND_SUBGROUP_ONLY,
        FOR_ALL_SUBGROUPS_SEPARATELY,
        EMPTY
    }
    private SubjectType subjectType;

    private String firstSubgroupSubjectName = "";
    private String firstSubgroupSubjectAuditorium = "";
    private String firstSubgroupSubjectTeacher = "";
    private String secondSubgroupSubjectName = "";
    private String secondSubgroupSubjectAuditorium = "";
    private String secondSubgroupSubjectTeacher = "";



    // геттеры и сеттеры для всего
    public String getSubjectNumber() {return subjectNumber;}
    public void setSubjectNumber(String subjectNumber) {this.subjectNumber = subjectNumber;}
    public String getSubjectName() {return subjectName;}
    public void setSubjectName(String subjectName) {this.subjectName = subjectName;}
    public String getSubjectAuditorium() {return subjectAuditorium;}
    public void setSubjectAuditorium(String subjectAuditorium) {this.subjectAuditorium = subjectAuditorium;}
    public String getSubjectTimeStart() {return subjectTimeStart;}
    public void setSubjectTimeStart(String subjectTimeStart) {this.subjectTimeStart = subjectTimeStart;}
    public String getSubjectTimeEnd() {return subjectTimeEnd;}
    public void setSubjectTimeEnd(String subjectTimeEnd) {this.subjectTimeEnd = subjectTimeEnd;}
    public SubjectType getSubjectType() {return subjectType;}
    public void setSubjectType(SubjectType subjectType) {this.subjectType = subjectType;}
    public String getSubjectTeacher() {return subjectTeacher;}
    public void setSubjectTeacher(String subjectTeacher) {this.subjectTeacher = subjectTeacher;}

    public String getFirstSubgroupSubjectName() {return firstSubgroupSubjectName;}
    public void setFirstSubgroupSubjectName(String firstSubgroupSubjectName) {this.firstSubgroupSubjectName = firstSubgroupSubjectName;}
    public String getFirstSubgroupSubjectAuditorium() {return firstSubgroupSubjectAuditorium;}
    public void setFirstSubgroupSubjectAuditorium(String firstSubgroupSubjectAuditorium) {this.firstSubgroupSubjectAuditorium = firstSubgroupSubjectAuditorium;}
    public String getFirstSubgroupSubjectTeacher() {return firstSubgroupSubjectTeacher;}
    public void setFirstSubgroupSubjectTeacher(String firstSubgroupSubjectTeacher) {this.firstSubgroupSubjectTeacher = firstSubgroupSubjectTeacher;}

    public String getSecondSubgroupSubjectName() {return secondSubgroupSubjectName;}
    public void setSecondSubgroupSubjectName(String secondSubgroupSubjectName) {this.secondSubgroupSubjectName = secondSubgroupSubjectName;}
    public String getSecondSubgroupSubjectAuditorium() {return secondSubgroupSubjectAuditorium;}
    public void setSecondSubgroupSubjectAuditorium(String secondSubgroupSubjectAuditorium) {this.secondSubgroupSubjectAuditorium = secondSubgroupSubjectAuditorium;}
    public String getSecondSubgroupSubjectTeacher() {return secondSubgroupSubjectTeacher;}
    public void setSecondSubgroupSubjectTeacher(String secondSubgroupSubjectTeacher) {this.secondSubgroupSubjectTeacher = secondSubgroupSubjectTeacher;}

    public Subject(String subjectNumber, String subjectName, String subjectAuditorium, String subjectTimeStart, String subjectTimeEnd, SubjectType subjectType, String subjectTeacher) {
        this.subjectNumber = subjectNumber;
        this.subjectName = subjectName;
        this.subjectAuditorium = subjectAuditorium;
        this.subjectTimeStart = subjectTimeStart;
        this.subjectTimeEnd = subjectTimeEnd;
        this.subjectType = subjectType;
        this.subjectTeacher = subjectTeacher;
    }

    Subject(){

    }
}
