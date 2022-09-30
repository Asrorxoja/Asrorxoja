package com.company.container;

import com.company.bot.SearchTeacherOrApprenticeBot;
import com.company.entity.Applicant;
import com.company.enums.Status;

import java.util.HashMap;
import java.util.Map;

public class ComponentContainer {
    public static SearchTeacherOrApprenticeBot MY_BOT = null;
    public static String BOT_USERNAME = "t.me/teacherApprenticeBot.";
    public static String BOT_TOKEN = "5548020336:AAGqBwIJPylMb2KPwyKeW0ERCvHqoTmEm1k";
    public static String ADMIN_CHAT_ID = "";

    // adminChatId, AdminStatus
    public static Map<String, Status> partner = new HashMap<>();
    public static Map<String, Status> job = new HashMap<>();
    public static Map<String, Status> employee = new HashMap<>();

    public static Map<String, Applicant> applicantMap = new HashMap<>();
    public static Map<String, String> textMap = new HashMap<>();
    public static boolean yerOrNo = false;
}
