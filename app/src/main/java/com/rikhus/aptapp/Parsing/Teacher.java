package com.rikhus.aptapp.Parsing;

public class Teacher {
    private String Name;
    private String Id;

    public String getName() {return Name;}
    public void setName(String name) {Name = name;}

    public String getId() {return Id;}
    public void setId(String id) {Id = id;}

    public Teacher(String name, String id) {
        Name = name;
        Id = id;
    }

    public Teacher() {
    }
}
