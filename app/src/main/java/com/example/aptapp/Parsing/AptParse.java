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
                    // если разделено по подгруппам
                    // первая п.гр
                    if(subjectHTML.select(".subGroup1").size() != 0 || subjectHTML.select(".subGroup2").size() != 0){
                        if(subjectHTML.select(".subGroup1").size() != 0){
                            Element subjectHTML1 = subjectHTML.selectFirst(".subGroup1");
                            Subject subject1 = new Subject();
                            subject1.subjectTeacher = subjectHTML1.selectFirst(".d-md-block").text();
                            subject1.subjectName = "(1 п/гр) " + subjectHTML1.selectFirst(".h5 .d-none.d-md-block").text();
                            subject1.subjectAuditorium = subjectHTML1.selectFirst(".text-truncate .h5 a").text();
                            subject1.subjectNumber = subjectHTML.selectFirst(".text-truncate .h3").text();
                            // для оформления времени
                            String subjectTime = subjectHTML.selectFirst(".text-truncate .h4").text();
                            subject1.subjectTime =
                                    subjectTime.substring(0,2) + ":" +
                                            subjectTime.substring(2,9) + ":" +
                                            subjectTime.substring(9);
                            schedule.add(subject1);
                        }
                        if(subjectHTML.select(".subGroup2").size() != 0){
                            Element subjectHTML2 = subjectHTML.selectFirst(".subGroup2");
                            Subject subject2 = new Subject();
                            subject2.subjectTeacher = subjectHTML2.selectFirst(".d-md-block").text();
                            subject2.subjectName = "(2 п/гр) " + subjectHTML2.selectFirst(".h5 .d-none.d-md-block").text();
                            subject2.subjectAuditorium = subjectHTML2.selectFirst(".text-truncate .h5 a").text();
                            subject2.subjectNumber = subjectHTML.selectFirst(".text-truncate .h3").text();
                            // для оформления времени
                            String subjectTime = subjectHTML.selectFirst(".text-truncate .h4").text();
                            subject2.subjectTime =
                                    subjectTime.substring(0,2) + ":" +
                                            subjectTime.substring(2,9) + ":" +
                                            subjectTime.substring(9);
                            schedule.add(subject2);
                        }

                    }
                    else{
                        subject.subjectTeacher = subjectHTML.selectFirst(".d-md-block").text();
                        subject.subjectName = subjectHTML.selectFirst(".h5 .d-none.d-md-block").text();
                        subject.subjectAuditorium = subjectHTML.selectFirst(".text-truncate .h5 a").text();
                        subject.subjectNumber = subjectHTML.selectFirst(".text-truncate .h3").text();
                        // для оформления времени
                        String subjectTime = subjectHTML.selectFirst(".text-truncate .h4").text();
                        subject.subjectTime =
                                subjectTime.substring(0,2) + ":" +
                                        subjectTime.substring(2,9) + ":" +
                                        subjectTime.substring(9);
                        schedule.add(subject);
                    }

                }
            }
        }
        catch(NullPointerException ex){
            // если возникла ошибка при парсинге
            System.out.println(ex.getMessage());
        }

        // если расписания на этот день нет
        if (schedule.size() == 0){
            Subject subjectNull = new Subject("На этот день нет расписания", "", "", "", "");
            schedule.add(subjectNull);
        }
        return schedule;
    }
}
