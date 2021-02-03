package com.example.aptapp.Parsing;

public class Subject {
    public String subjectName = "";
    public String subjectAuditorium = "";
    public String subjectTimeStart = "";
    public String subjectTimeEnd = "";

    public static enum SubjectType{
        FOR_ALL_SUBGROUPS,
        FOR_FIRST_SUBGROUP_ONLY,
        FOR_SECOND_SUBGROUP_ONLY,
        FOR_ALL_SUBGROUPS_SEPARATELY
    }
    public SubjectType subjectType;

    public String firstSubgroupSubjectName = "";
    public String firstSubgroupSubjectAuditorium = "";

    public String secondSubgroupSubjectName = "";
    public String secondSubgroupSubjectAuditorium = "";

    public Subject(String subjectName, String subjectAuditorium, String subjectTimeStart, String subjectTimeEnd, SubjectType subjectType) {
        this.subjectName = subjectName;
        this.subjectAuditorium = subjectAuditorium;
        this.subjectTimeStart = subjectTimeStart;
        this.subjectTimeEnd = subjectTimeEnd;
        this.subjectType = subjectType;
    }

    Subject(){

    }
}
