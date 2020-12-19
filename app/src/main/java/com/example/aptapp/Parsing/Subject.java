package com.example.aptapp.Parsing;

public class Subject {
    public String subjectName = "";
    public String subjectTeacher = "";
    public String subjectAuditorium = "";
    public String subjectNumber = "";
    public String subjectTime = "";

    Subject(String _subjectName, String _subjectTeacher, String _subjectAuditorium, String _subjectNumber, String _subjectTime){
        subjectName = _subjectName;
        subjectTeacher = _subjectTeacher;
        subjectAuditorium = _subjectAuditorium;
        subjectNumber = _subjectNumber;
        subjectTime = _subjectTime;
    }

    Subject(){

    }
}
