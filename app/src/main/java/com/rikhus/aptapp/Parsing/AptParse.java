package com.rikhus.aptapp.Parsing;

import android.text.format.Time;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rikhus.aptapp.UserType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AptParse {

    private static final String SUBJECT_NAME_HTML_CLASS = ".h5 .d-md-none.text-center";
    private static final String SUBJECT_AUDITORIUM_HTML_CLASS = ".text-truncate .h5 a";
    private static final String SUBJECT_TIME_HTML_CLASS = ".text-truncate .h4";
    private static final String SUBJECT_TEACHER_HTML_CLASS = ".Staff";

    private static final String SUBJECT_NAME_FOR_TEACHERS_HTML_CLASS = ".pl-2 .d-md-none.text-center.text-truncate";
    private static final String SUBJECT_GROUP_FOR_TEACHERS_HTML_CLASS = ".pl-2 .h5";
    private static final String SUBJECT_AUDITORIUM_FOR_TEACHERS_HTML_CLASS = ".pl-2 .h5 a";
    private static final String SUBJECT_SUBGROUP_FOR_TEACHERS_HTML_CLASS = ".rounded.h5";
    private static final String SUBJECT_TIME_FOR_TEACHERS_HTML_CLASS = ".pl-2.h4";



    // получение групп
    public static ArrayList<Group> getGroups(int courseNumber) throws IOException {
        // получение документа
        Document document = Jsoup.connect("http://almetpt.ru/2020/json/groups")
                .header("X-Requested-With", "XMLHttpRequest")
                .ignoreContentType(true).get();

        // преобразование строкового json в JsonObject
        JsonObject groupsJson = new Gson().fromJson(document.body().text(), JsonObject.class).getAsJsonObject("groups");

        ArrayList<Group> groups = new ArrayList<>();

        // получение айди соответсвующий по курсу групп и получение их названий
        for (JsonElement id : groupsJson.getAsJsonArray("dep_0_course_" + courseNumber)){
            Group group = new Group();
            group.setGroupId(id.getAsString().replace("\"", " "));
            group.setGroupName(groupsJson.getAsJsonObject(group.getGroupId())
                    .get("Name").getAsString());

            groups.add(group);
        }

        return groups;

    }

    // получение дат
    public static JsonObject getDates() throws IOException {
        // получение документа
        Document document = Jsoup.connect("http://almetpt.ru/2020/schedule/dates")
                .header("X-Requested-With", "XMLHttpRequest")
                .ignoreContentType(true).get();

        // преобразование строкового json в JsonObject
        return new Gson().fromJson(document.body().text(), JsonObject.class);
    }

    // получение преподавателей
    public static ArrayList<Teacher> getTeachers() throws  IOException{
        // получение документа
        Document document = Jsoup.connect("http://almetpt.ru/2020/json/staffs")
                .header("X-Requested-With", "XMLHttpRequest")
                .ignoreContentType(true).get();

        // преобразование строкового json в JsonObject
        JsonObject teachersJson = new Gson().fromJson(document.body().text(), JsonObject.class).getAsJsonObject("staffs");

        ArrayList<Teacher> teachers = new ArrayList<>();

        String firstName = "";
        String lastName = "";
        String patronymic = "";

        // получение айди учителей и их имен по этим айдишникам
        for (JsonElement teacherId : teachersJson.getAsJsonArray("teachers")){
            Teacher teacher = new Teacher();
            teacher.setId(teacherId.getAsString());
            firstName = teachersJson.getAsJsonObject(teacherId.getAsString()).get("Name").getAsString();
            lastName = teachersJson.getAsJsonObject(teacherId.getAsString()).get("Family").getAsString();
            patronymic = teachersJson.getAsJsonObject(teacherId.getAsString()).get("Father").getAsString();
            teacher.setName(lastName + " " + firstName + " " + patronymic);

            teachers.add(teacher);
        }

        return teachers;
    }

    // получение расписания для студентов
    public static ArrayList<Subject> getSchedule(String groupId, String date) throws IOException {
        // получение документа
        ArrayList<Subject> schedule = new ArrayList<>();
        Document document = Jsoup.connect("http://almetpt.ru/2020/site/schedule/group/" +
                groupId + "/" + date).get();
        Elements subjectsHTML = document.select(".card.myCard");
        try{
            // заполнение массива предметами(парами)
            for (Element subjectHTML : subjectsHTML){
                Subject subject = new Subject();

                // отсекаем консультации и прочее
                if(subjectHTML.select(".card-header").size() != 0 && subjectHTML.select(".card-header.text-center").size() == 0){
                    // если разделено по подгруппам и если пара есть для обоих подгрупп
                    if(subjectHTML.select(".subGroup1").size() != 0 && subjectHTML.select(".subGroup2").size() != 0){
                        // сначала выясняем пару для первой подгруппы
                        Element subjectHTML1 = subjectHTML.selectFirst(".subGroup1");
                        subject.setFirstSubgroupSubjectName("(1 п/гр) " + subjectHTML1.selectFirst(SUBJECT_NAME_HTML_CLASS).text());
                        subject.setFirstSubgroupSubjectAuditorium(subjectHTML1.selectFirst(SUBJECT_AUDITORIUM_HTML_CLASS).text());
                        subject.setFirstSubgroupSubjectTeacher(subjectHTML1.selectFirst(SUBJECT_TEACHER_HTML_CLASS).text());
                        // потом для второй
                        Element subjectHTML2 = subjectHTML.selectFirst(".subGroup2");
                        subject.setSecondSubgroupSubjectName("(2 п/гр) " + subjectHTML2.selectFirst(SUBJECT_NAME_HTML_CLASS).text());
                        subject.setSecondSubgroupSubjectAuditorium(subjectHTML2.selectFirst(SUBJECT_AUDITORIUM_HTML_CLASS).text());
                        subject.setSecondSubgroupSubjectTeacher(subjectHTML2.selectFirst(SUBJECT_TEACHER_HTML_CLASS).text());


                        // потом добавляем время
                        String subjectTime = subjectHTML.selectFirst(SUBJECT_TIME_HTML_CLASS).text();
                        subject.setSubjectTimeStart(
                                subjectTime.substring(0,2) + ":" +
                                        subjectTime.substring(2,5));
                        subject.setSubjectTimeEnd(
                                subjectTime.substring(7,9) + ":" +
                                        subjectTime.substring(9));
                        subject.setSubjectType(Subject.SubjectType.FOR_ALL_SUBGROUPS_SEPARATELY);

                    }
                    // если пара есть только для одной из подгрупп
                    else if(subjectHTML.select(".subGroup1").size() != 0 ^ subjectHTML.select(".subGroup2").size() != 0){
                        // если для первой вытаскиваем расписание для нее
                        if(subjectHTML.select(".subGroup1").size() != 0){
                            Element subjectHTML1 = subjectHTML.selectFirst(".subGroup1");
                            subject.setSubjectName("(1 п/гр) " + subjectHTML1.selectFirst(SUBJECT_NAME_HTML_CLASS).text());
                            subject.setSubjectAuditorium(subjectHTML1.selectFirst(SUBJECT_AUDITORIUM_HTML_CLASS).text());
                            subject.setSubjectType(Subject.SubjectType.FOR_FIRST_SUBGROUP_ONLY);
                            subject.setSubjectTeacher(subjectHTML1.selectFirst(SUBJECT_TEACHER_HTML_CLASS).text());
                        }
                        // если для второй, то для нее
                        if(subjectHTML.select(".subGroup2").size() != 0){
                            Element subjectHTML2 = subjectHTML.selectFirst(".subGroup2");
                            subject.setSubjectName("(2 п/гр) " + subjectHTML2.selectFirst(SUBJECT_NAME_HTML_CLASS).text());
                            subject.setSubjectAuditorium(subjectHTML2.selectFirst(SUBJECT_AUDITORIUM_HTML_CLASS).text());
                            subject.setSubjectType(Subject.SubjectType.FOR_SECOND_SUBGROUP_ONLY);
                            subject.setSubjectTeacher(subjectHTML2.selectFirst(SUBJECT_TEACHER_HTML_CLASS).text());
                        }
                        // и вытаскиваем время
                        String subjectTime = subjectHTML.selectFirst(SUBJECT_TIME_HTML_CLASS).text();
                        subject.setSubjectTimeStart(
                                subjectTime.substring(0,2) + ":" +
                                        subjectTime.substring(2,5));
                        subject.setSubjectTimeEnd(
                                subjectTime.substring(7,9) + ":" +
                                        subjectTime.substring(9));
                    }
                    // а тут если пара общая для всех
                    else{
                        // название и аудитория
                        subject.setSubjectName(subjectHTML.selectFirst(SUBJECT_NAME_HTML_CLASS).text());
                        subject.setSubjectAuditorium(subjectHTML.selectFirst(SUBJECT_AUDITORIUM_HTML_CLASS).text());
                        // вытаскиваем время
                        String subjectTime = subjectHTML.selectFirst(SUBJECT_TIME_HTML_CLASS).text();
                        subject.setSubjectTimeStart(
                                subjectTime.substring(0,2) + ":" +
                                        subjectTime.substring(2,5));
                        subject.setSubjectTimeEnd(
                                subjectTime.substring(7,9) + ":" +
                                        subjectTime.substring(9));
                        subject.setSubjectType(Subject.SubjectType.FOR_ALL_SUBGROUPS);
                        // добавляем преподавателя
                        subject.setSubjectTeacher(subjectHTML.selectFirst(SUBJECT_TEACHER_HTML_CLASS).text());
                    }
                    // довабляем элемент в список
                    schedule.add(subject);
                }
            }
        }
        catch(NullPointerException ex){
            // если возникла ошибка при парсинге
            System.out.println(ex.getMessage());
        }

        // если расписания на этот день нет
        if (schedule.size() == 0){
            Subject subjectNull = new Subject("", "", "", "", Subject.SubjectType.EMPTY, "");
            schedule.add(subjectNull);
        }
        return schedule;
    }

    // получение расписания для учителей
    public static ArrayList<Subject> getScheduleForTeacher(String teacherId, String date) throws IOException {
        // получение документа
        ArrayList<Subject> schedule = new ArrayList<>();
        Document document = Jsoup.connect("http://almetpt.ru/2020/site/schedule/staff/" +
                teacherId + "/" + date).get();
        Elements subjectsHTML = document.select(".card.myCard");

        try{
            // заполнение массива предметами(парами)
            for (Element subjectHTML : subjectsHTML){
                Subject subject = new Subject();

                // отсекаем консультации и прочее
                if(subjectHTML.select(".card-header").size() != 0 && subjectHTML.select(".card-header.text-center").size() == 0){

                    // если пара общая
                    if(subjectHTML.selectFirst(SUBJECT_SUBGROUP_FOR_TEACHERS_HTML_CLASS).text().equals("")){

                        // название, группа и аудитория
                        subject.setSubjectName(subjectHTML.selectFirst(SUBJECT_NAME_FOR_TEACHERS_HTML_CLASS).text());
                        subject.setSubjectAuditorium(subjectHTML.selectFirst(SUBJECT_AUDITORIUM_FOR_TEACHERS_HTML_CLASS).text());
                        subject.setSubjectTeacher(subjectHTML.select(SUBJECT_GROUP_FOR_TEACHERS_HTML_CLASS).last().text());
                        // вытаскиваем время
                        String subjectTime = subjectHTML.selectFirst(SUBJECT_TIME_FOR_TEACHERS_HTML_CLASS).text();
                        subject.setSubjectTimeStart(
                                subjectTime.substring(0,2) + ":" +
                                        subjectTime.substring(2,5));
                        subject.setSubjectTimeEnd(
                                subjectTime.substring(7,9) + ":" +
                                        subjectTime.substring(9));
                        subject.setSubjectType(Subject.SubjectType.FOR_ALL_SUBGROUPS);
                    }
                    // если для какой-либо подгруппы
                    else{
                        if(subjectHTML.selectFirst(SUBJECT_SUBGROUP_FOR_TEACHERS_HTML_CLASS).text().equals("1 п/гр.")){
                            subject.setSubjectType(Subject.SubjectType.FOR_FIRST_SUBGROUP_ONLY);
                            subject.setSubjectName("(1 п/гр.) " + subjectHTML.selectFirst(SUBJECT_NAME_FOR_TEACHERS_HTML_CLASS).text());

                        }
                        else{
                            subject.setSubjectType(Subject.SubjectType.FOR_SECOND_SUBGROUP_ONLY);
                            subject.setSubjectName("(2 п/гр.) " + subjectHTML.selectFirst(SUBJECT_NAME_FOR_TEACHERS_HTML_CLASS).text());
                        }

                        // группа и аудитория
                        subject.setSubjectAuditorium(subjectHTML.selectFirst(SUBJECT_AUDITORIUM_FOR_TEACHERS_HTML_CLASS).text());
                        subject.setSubjectTeacher(subjectHTML.select(SUBJECT_GROUP_FOR_TEACHERS_HTML_CLASS).last().text());
                        // вытаскиваем время
                        String subjectTime = subjectHTML.selectFirst(SUBJECT_TIME_FOR_TEACHERS_HTML_CLASS).text();
                        subject.setSubjectTimeStart(
                                subjectTime.substring(0,2) + ":" +
                                        subjectTime.substring(2,5));
                        subject.setSubjectTimeEnd(
                                subjectTime.substring(7,9) + ":" +
                                        subjectTime.substring(9));
                    }
                    // довабляем элемент в список
                    schedule.add(subject);
                }
            }
        }
        catch(NullPointerException ex){
            // если возникла ошибка при парсинге
            System.out.println(ex.getMessage());
        }

        // если расписания на этот день нет
        if (schedule.size() == 0){
            Subject subjectNull = new Subject("", "", "", "", Subject.SubjectType.EMPTY, "");
            schedule.add(subjectNull);
        }
        return schedule;
    }

    // получение расписания для студентов
    public static Date getPairsEndTime(String id, String date, UserType userType) throws IOException, ParseException {
        // получение документа
        ArrayList<Subject> schedule = new ArrayList<>();
        String userTypeUrl = "";
        if (userType == UserType.STUDENT){
            userTypeUrl = "group";
        }
        else{
            userTypeUrl = "staff";
        }
        Document document = Jsoup.connect("http://almetpt.ru/2020/site/schedule/" + userTypeUrl + "/" +
                id + "/" + date).get();
        Elements subjectsHTML = document.select(".card.myCard");
        Elements subjectsWithoutConsultationsHTML = new Elements();

        // отсекаем консультации и прочее
        for (Element subjectHTML : subjectsHTML){
            if(subjectHTML.select(".card-header").size() != 0 && subjectHTML.select(".card-header.text-center").size() == 0){
                subjectsWithoutConsultationsHTML.add(subjectHTML);
            }
        }
        Element lastSubjectHTML = subjectsWithoutConsultationsHTML.last();
        String endTimeString;

        // если пар нет, то выдаем полночь, чтоб сразу перескакивало на след день
        if(lastSubjectHTML == null){
            endTimeString = "00:00";
        }
        else {
            String timeString = lastSubjectHTML.selectFirst(SUBJECT_TIME_HTML_CLASS).text();
            endTimeString = timeString.substring(0, 2) + ":" +
                    timeString.substring(2, 5);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        Date pairsEndTime;
        try{
            pairsEndTime = sdf.parse(endTimeString);
        }
        catch(Exception ex){
            pairsEndTime = new Date();
        }

        Date currentDate = new Date();
        pairsEndTime.setDate(currentDate.getDate());
        pairsEndTime.setMonth(currentDate.getMonth());
        pairsEndTime.setYear(currentDate.getYear());
        return pairsEndTime;
    }
}
