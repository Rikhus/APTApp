package com.example.aptapp.Parsing;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class AptParse {
    public static JsonObject getGroups() throws IOException {
        // получение документа
        Document document = Jsoup.connect("http://almetpt.ru/2020/json/groups")
                .header("X-Requested-With", "XMLHttpRequest")
                .ignoreContentType(true).get();

        // преобразование строкового json в JsonObject
        return new Gson().fromJson(document.body().text(), JsonObject.class);
    }

    public static JsonObject getDates() throws IOException {
        // получение документа
        Document document = Jsoup.connect("http://almetpt.ru/2020/schedule/dates")
                .header("X-Requested-With", "XMLHttpRequest")
                .ignoreContentType(true).get();

        // преобразование строкового json в JsonObject
        return new Gson().fromJson(document.body().text(), JsonObject.class);
    }

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
                        subject.firstSubgroupSubjectName = "(1 п/гр) " + subjectHTML1.selectFirst(".h5 .d-md-none.text-center").text();
                        subject.firstSubgroupSubjectAuditorium = subjectHTML1.selectFirst(".text-truncate .h5 a").text();
                        // потом для второй
                        Element subjectHTML2 = subjectHTML.selectFirst(".subGroup2");
                        subject.secondSubgroupSubjectName = "(2 п/гр) " + subjectHTML2.selectFirst(".h5 .d-md-none.text-center").text();
                        subject.secondSubgroupSubjectAuditorium = subjectHTML2.selectFirst(".text-truncate .h5 a").text();

                        // потом добавляем время
                        String subjectTime = subjectHTML.selectFirst(".text-truncate .h4").text();
                        subject.subjectTimeStart =
                                subjectTime.substring(0,2) + ":" +
                                        subjectTime.substring(2,5);
                        subject.subjectTimeEnd =
                                subjectTime.substring(7,9) + ":" +
                                        subjectTime.substring(9);
                        subject.subjectType = Subject.SubjectType.FOR_ALL_SUBGROUPS_SEPARATELY;

                    }
                    // если пара есть только для одной из подгрупп
                    else if(subjectHTML.select(".subGroup1").size() != 0 ^ subjectHTML.select(".subGroup2").size() != 0){
                        // если для первой вытаскиваем расписание для нее
                        if(subjectHTML.select(".subGroup1").size() != 0){
                            Element subjectHTML1 = subjectHTML.selectFirst(".subGroup1");
                            subject.subjectName = "(1 п/гр) " + subjectHTML1.selectFirst(".h5 .d-md-none.text-center").text();
                            subject.subjectAuditorium = subjectHTML1.selectFirst(".text-truncate .h5 a").text();
                            subject.subjectType = Subject.SubjectType.FOR_FIRST_SUBGROUP_ONLY;
                        }
                        // если для второй, то для нее
                        if(subjectHTML.select(".subGroup2").size() != 0){
                            Element subjectHTML2 = subjectHTML.selectFirst(".subGroup2");
                            subject.subjectName = "(2 п/гр) " + subjectHTML2.selectFirst(".h5 .d-md-none.text-center").text();
                            subject.subjectAuditorium = subjectHTML2.selectFirst(".text-truncate .h5 a").text();
                            subject.subjectType = Subject.SubjectType.FOR_SECOND_SUBGROUP_ONLY;
                        }
                        // и вытаскиваем время
                        String subjectTime = subjectHTML.selectFirst(".text-truncate .h4").text();
                        subject.subjectTimeStart =
                                subjectTime.substring(0,2) + ":" +
                                        subjectTime.substring(2,5);
                        subject.subjectTimeEnd =
                                subjectTime.substring(7,9) + ":" +
                                        subjectTime.substring(9);
                    }
                    // а тут если пара общая для всех
                    else{
                        // название и аудитория
                        subject.subjectName = subjectHTML.selectFirst(".h5 .d-md-none.text-center").text();
                        subject.subjectAuditorium = subjectHTML.selectFirst(".text-truncate .h5 a").text();
                        // вытаскиваем время
                        String subjectTime = subjectHTML.selectFirst(".text-truncate .h4").text();
                        subject.subjectTimeStart =
                                subjectTime.substring(0,2) + ":" +
                                        subjectTime.substring(2,5);
                        subject.subjectTimeEnd =
                                subjectTime.substring(7,9) + ":" +
                                        subjectTime.substring(9);
                        subject.subjectType = Subject.SubjectType.FOR_ALL_SUBGROUPS;
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
            Subject subjectNull = new Subject("На этот день нет расписания", "", "", "", Subject.SubjectType.FOR_ALL_SUBGROUPS);
            schedule.add(subjectNull);
        }
        return schedule;
    }
}
