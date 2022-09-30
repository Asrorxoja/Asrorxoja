package com.company.controller;

import com.company.container.ComponentContainer;
import com.company.entity.Applicant;
import com.company.entity.Customer;
import com.company.enums.*;
import com.company.service.CustomerService;
import com.company.util.InlineKeyboardUtil;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.company.container.ComponentContainer.*;
import static com.company.util.KeyboardButtonConstants.*;
import static com.company.util.KeyboardButtonUtil.*;

public class MainController {
    public static void handleMessage(User user, Message message) {

        if (message.hasText()) {
            String text = message.getText();
            handleText(user, message, text);
        } else if (message.hasContact()) {
            Contact contact = message.getContact();
            handleContact(user, message, contact);
        }

    }

    private static void handleContact(User user, Message message, Contact contact) {
        String chatId = String.valueOf(message.getChatId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (contact.getPhoneNumber().contains("+998")) {
            sendMessage.setText("Botdan faqat O'zbekiston fuqorolari foydalana oladi");
        } else {
            Customer customer = CustomerService.getCustomerByChatId(chatId);

            if (customer == null) {
                CustomerService.addCustomer(chatId, contact);

                sendMessage.setText("Botdan muvaffiqiyatli ro'yxatdan o'tdingiz🙂");
            } else {
                sendMessage.setText("Bot qayta ishga tushdi😎");
            }

            sendMessage.setReplyMarkup(getMenu());
        }

        ComponentContainer.MY_BOT.sendMsg(sendMessage);
    }

    private static void handleText(User user, Message message, String text) {
        String chatId = String.valueOf(message.getChatId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);


        if (text.equals(YES)) {
            yerOrNo = true;
        }
        if (text.equals("/start")) {
            Customer customer = CustomerService.getCustomerByChatId(chatId);

            if (customer == null) {
                sendMessage.setText("""
                        Assalomu alaykum!

                        Botga xush kelibsiz🤗
                        Botdan to'liq foydalanish uchun telefon raqam yuborishingiz kerak""");

                sendMessage.setReplyMarkup(getContactButton());
            } else {
                sendMessage.setText("Bot qayta ishga tushirildi");
                sendMessage.setReplyMarkup(getMenu());
            }
        }

        else if (text.equals(NEED_PARTNER)) {
            String sb = "<b>Sherik topish uchun ariza berish</b>" +
                    "\n\n" +
                    getText();

            job.clear();
            employee.clear();
            partner.put(chatId, Status.START);

            sendMessage.setText(sb);
            sendMessage.setParseMode(ParseMode.HTML);
            sendMessage.setReplyMarkup(getYesOrNo());
        }

        else if (text.equals(NEED_JOB)) {
            String sb = "<b>Ish joyi topish uchun ariza berish</b>" +
                    "\n\n" +
                    getText();

            partner.clear();
            employee.clear();
            job.put(chatId, Status.START);

            sendMessage.setText(sb);
            sendMessage.setParseMode(ParseMode.HTML);
            sendMessage.setReplyMarkup(getYesOrNo());
        }

        else if (text.equals(NEED_EMPLOYEE)) {
            String sb = "<b>Xodim topish uchun ariza berish</b>" +
                    "\n\n" +
                    getText();

            partner.clear();
            job.clear();
            employee.put(chatId, Status.START);

            sendMessage.setText(sb);
            sendMessage.setParseMode(ParseMode.HTML);
            sendMessage.setReplyMarkup(getYesOrNo());
        }

        else if (ComponentContainer.yerOrNo) {
            if (!partner.isEmpty()) {

                if (partner.containsKey(chatId)) {
                    Status statusPartner = partner.get(chatId);

                    switch (statusPartner) {
                        case START -> {
                            partner.put(chatId, Status.ENTER_NAME);

                            applicantMap.put(chatId, new Applicant());

                            sendMessage.setText("<b>Ism, familiyangizni kiriting?</b>");
                            sendMessage.setParseMode(ParseMode.HTML);
                            sendMessage.setReplyMarkup(getMenu());
                        }
                        case ENTER_NAME -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                System.out.println("dwd");
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setName(text);

                                partner.put(chatId, Status.TYPE_TECHNOLOGY);


                                String str = """
                                        \uD83D\uDCDA <b>Texnologiya:</b>

                                        Talab qilinadigan texnologiyalarni kiriting?Texnologiya nomlarini vergul bilan ajrating. Masalan,\s
                                        Java, C++, C#""";

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                        case TYPE_TECHNOLOGY -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setTypeTechnology(text);

                                partner.put(chatId, Status.PHONE_NUMBER);

                                String str = """
                                        \uD83D\uDCDE <b>Aloqa</b>:

                                        Bog`lanish uchun raqamingizni kiriting?
                                        Masalan, +998 90 123 45 67""";

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                        case PHONE_NUMBER -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday telefon raqam mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setRegion(text);

                                partner.put(chatId, Status.REGION);

                                String str = getRegionText();

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                        case REGION -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setPhoneNumber(text);

                                partner.put(chatId, Status.PAYMENT);

                                String str = getPaymentText();

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                        case PAYMENT -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setPayment(text);

                                partner.put(chatId, Status.PROFESSION);

                                String str = getProfessionText();

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                        case PROFESSION -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setProfession(text);

                                partner.put(chatId, Status.TIME);

                                String str = getTimeText();

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                        case TIME -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setTime(text);

                                partner.put(chatId, Status.PURPOSE);

                                String str = getPurposeText();

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                        case PURPOSE -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setPurpose(text);
                                applicant.setUsername(user.getUserName());

                                partner.put(chatId, Status.CONFIRM_OR_CANCEL);

                                String str = getConfirmText("partner", applicant);

                                textMap.put(chatId, str);

                                sendMessage.setReplyMarkup(InlineKeyboardUtil.getConfirmOrCancelMenu());

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                    }
                }
            }

            else if (!job.isEmpty()) {

                if (job.containsKey(chatId)) {
                    Status sJob = job.get(chatId);

                    switch (sJob) {
                        case START -> {
                            job.put(chatId, Status.ENTER_NAME);

                            applicantMap.put(chatId, new Applicant());

                            sendMessage.setText("<b>Ism, familiyangizni kiriting?</b>");
                            sendMessage.setParseMode(ParseMode.HTML);
                            sendMessage.setReplyMarkup(getMenu());
                        }
                        case ENTER_NAME -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setName(text);

                                job.put(chatId, Status.AGE);


                                String str = """
                                        🕑 <b>Yosh:</b>

                                        Yoshingizni kiriting?
                                        Masalan, 19""";

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                        case AGE -> {
                            try {
                                int age = Integer.parseInt(text);

                                if (age < 15) {
                                    sendMessage.setText("Bunday yosh mumkin emas");
                                } else {
                                    Applicant applicant = applicantMap.get(chatId);
                                    applicant.setAge(age);

                                    job.put(chatId, Status.TYPE_TECHNOLOGY);


                                    String str = """
                                            \uD83D\uDCDA <b>Texnologiya:</b>

                                            Talab qilinadigan texnologiyalarni kiriting?Texnologiya nomlarini vergul bilan ajrating. Masalan,\s
                                            Java, C++, C#""";
                                    ;

                                    sendMessage.setText(str);
                                    sendMessage.setParseMode(ParseMode.HTML);
                                }
                            } catch (Exception e) {
                                sendMessage.setText("Bunday qiymat olinmaydi\nQaytdan urinib ko'ring");
                            }
                        }
                        case TYPE_TECHNOLOGY -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setTypeTechnology(text);

                                job.put(chatId, Status.PHONE_NUMBER);

                                String str = """
                                        \uD83D\uDCDE <b>Aloqa</b>:

                                        Bog`lanish uchun raqamingizni kiriting?
                                        Masalan, +998 90 123 45 67""";

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                        case PHONE_NUMBER -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday telefon raqam mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setRegion(text);

                                job.put(chatId, Status.REGION);

                                String str = getRegionText();

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                        case REGION -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setPhoneNumber(text);

                                job.put(chatId, Status.PAYMENT);

                                String str = getPaymentText();

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                        case PAYMENT -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setPayment(text);

                                job.put(chatId, Status.PROFESSION);

                                String str = getProfessionText();

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                        case PROFESSION -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setProfession(text);

                                job.put(chatId, Status.TIME);

                                String str = getTimeText();

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                        case TIME -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setTime(text);

                                job.put(chatId, Status.PURPOSE);

                                String str = getPurposeText();

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                        case PURPOSE -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setPurpose(text);
                                applicant.setUsername(user.getUserName());

                                job.put(chatId, Status.CONFIRM_OR_CANCEL);

                                String str = getConfirmText("job", applicant);

                                textMap.put(chatId, str);

                                sendMessage.setReplyMarkup(InlineKeyboardUtil.getConfirmOrCancelMenu());

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                    }
                }
            }

            else if (!employee.isEmpty()) {

                if (employee.containsKey(chatId)) {
                    Status sEmployee = employee.get(chatId);

                    switch (sEmployee) {
                        case START -> {
                            employee.put(chatId, Status.ENTER_OFFICE_NAME);

                            applicantMap.put(chatId, new Applicant());

                            sendMessage.setText("<b>\uD83C\uDF93 Idora nomi?</b>");
                            sendMessage.setParseMode(ParseMode.HTML);
                            sendMessage.setReplyMarkup(getMenu());
                        }
                        case ENTER_OFFICE_NAME -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setNameOffice(text);

                                employee.put(chatId, Status.TYPE_TECHNOLOGY);


                                String str = """
                                        \uD83D\uDCDA Texnologiya:

                                        Talab qilinadigan texnologiyalarni kiriting?
                                        Texnologiya nomlarini vergul bilan ajrating. Masalan,\s

                                        Java, C++, C#""";

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                        case TYPE_TECHNOLOGY -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setTypeTechnology(text);

                                employee.put(chatId, Status.REGION);

                                String str = getRegionText();

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                        case REGION -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setPhoneNumber(text);

                                employee.put(chatId, Status.NAME_CHARGE);

                                String str = getPaymentText();

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                        case NAME_CHARGE -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setRegion(text);

                                employee.put(chatId, Status.TIME);

                                String str = getNameChargeText();

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }

                        }
                        case TIME -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setTime(text);

                                employee.put(chatId, Status.JOB_TIME);

                                String str = getJobTimeText();

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                        case JOB_TIME -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setJobTime(text);

                                employee.put(chatId, Status.SALARY);

                                String str = getSalaryText();

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                        case SALARY -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setJobTime(text);

                                employee.put(chatId, Status.PURPOSE);

                                String str = getExtraText();

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                        case PURPOSE -> {
                            if (text.isBlank()) {
                                sendMessage.setText("Bunday nom mumkin emas");
                            } else {
                                Applicant applicant = applicantMap.get(chatId);
                                applicant.setPurpose(text);
                                applicant.setUsername(user.getUserName());

                                employee.put(chatId, Status.CONFIRM_OR_CANCEL);

                                String str = getConfirmText("employee", applicant);

                                textMap.put(chatId, str);

                                sendMessage.setReplyMarkup(InlineKeyboardUtil.getConfirmOrCancelMenu());

                                sendMessage.setText(str);
                                sendMessage.setParseMode(ParseMode.HTML);
                            }
                        }
                    }
                }
            }

        } else if (text.equals(NO)) {
            sendMessage.setText("Menu");
            sendMessage.setReplyMarkup(getMenu());
        }

        ComponentContainer.MY_BOT.sendMsg(sendMessage);
    }

    private static String getExtraText() {
        return "‼️ Qo`shimcha ma`lumotlar?";
    }

    public static void handleCallback(User user, Message message, String data) {
        String chatId = String.valueOf(message.getChatId());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
        ComponentContainer.MY_BOT.sendMsg(deleteMessage);

        if (partner.get(chatId).equals(Status.CONFIRM_OR_CANCEL)) {
            sendMessage = confirmCallBack(data, chatId);

            applicantMap.clear();
            partner.clear();
            yerOrNo = false;
        }
        if (job.get(chatId).equals(Status.CONFIRM_OR_CANCEL)) {
            sendMessage = confirmCallBack(data, chatId);

            applicantMap.clear();
            job.clear();
            yerOrNo = false;
        }
        else if (employee.get(chatId).equals(Status.CONFIRM_OR_CANCEL)) {
            sendMessage = confirmCallBack(data, chatId);

            applicantMap.clear();
            employee.clear();
            yerOrNo = false;
        }

        MY_BOT.sendMsg(sendMessage);
    }

    static SendMessage confirmCallBack(String data, String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (data.equals("_confirm_")) {
            SendMessage sendMessageAdmin = new SendMessage();
            sendMessageAdmin.setChatId(ADMIN_CHAT_ID);

            String text = textMap.get(chatId);
            sendMessageAdmin.setText(text);

            sendMessage.setText("Qabul qilindi");

            MY_BOT.sendMsg(sendMessageAdmin);
        } else if (data.equals("_cancel_")) {
            sendMessage.setText("Qabul qilinmadi");
        }

        return sendMessage;
    }

    static String getText() {
        return "Hozir sizga birnecha savollar beriladi. \n" +
                "Har biriga javob bering. \n" +
                "Oxirida agar hammasi to`g`ri bo`lsa, HA tugmasini bosing va arizangiz Adminga yuboriladi." +
                "\n\n" +
                "<b>Boshlaymizmi?</b>";
    }

    private static String getConfirmText(String text, Applicant applicant) {
        if (text.equals("partner")) {
            return "<b>Sherik kerak:</b>\n" +
                    "\n" +
                    "\uD83C\uDFC5 Sherik: <b>" + applicant.getName() + "</b>\n" +
                    "\uD83D\uDCDA Texnologiya: <b>" + applicant.getTypeTechnology() + "</b>\n" +
                    "\uD83C\uDDFA\uD83C\uDDFF Telegram: @" + applicant.getUsername() + "\n" +
                    "\uD83D\uDCDE Aloqa: " + applicant.getPhoneNumber() + "\n" +
                    "\uD83C\uDF10 Hudud: " + applicant.getRegion() + "\n" +
                    "\uD83D\uDCB0 Narxi: " + applicant.getPayment() + "\n" +
                    "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB Kasbi: " + applicant.getProfession() + "\n" +
                    "\uD83D\uDD70 Murojaat qilish vaqti: " + applicant.getTime() + "\n" +
                    "\uD83D\uDD0E Maqsad: " + applicant.getPurpose() + "\n" +
                    "\n" +
                    "#sherik";
        }
        else if (text.equalsIgnoreCase("job")) {
            return "<b>Ish joyi kerak:</b>\n" +
                    "\n" +
                    "\uD83D\uDC68\u200D\uD83D\uDCBC Xodim: <b>" + applicant.getName() + "</b>\n" +
                    "\uD83D\uDD51 Yosh: <b>" + applicant.getAge() + "</b>\n" +
                    "\uD83D\uDCDA Texnologiya: <b>" + applicant.getTypeTechnology() + "</b>\n" +
                    "\uD83C\uDDFA\uD83C\uDDFF Telegram: @" + applicant.getUsername() + "\n" +
                    "\uD83D\uDCDE Aloqa: " + applicant.getPhoneNumber() + "\n" +
                    "\uD83C\uDF10 Hudud: " + applicant.getRegion() + "\n" +
                    "\uD83D\uDCB0 Narxi: " + applicant.getPayment() + "\n" +
                    "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB Kasbi: " + applicant.getProfession() + "\n" +
                    "\uD83D\uDD70 Murojaat qilish vaqti: " + applicant.getTime() + "\n" +
                    "\uD83D\uDD0E Maqsad: " + applicant.getPurpose() + "\n" +
                    "\n" +
                    "#xodim";
        }
        else if (text.equals("employee")){
            return  "<b>Xodim kerak:</b>\n" +
                    "\n" +
                    "\uD83C\uDFE2 Idora: <b>" + applicant.getNameOffice() + "</b>\n" +
                    "\uD83D\uDCDA Texnologiya: <b>" + applicant.getTypeTechnology() + "</b>\n" +
                    "\uD83C\uDDFA\uD83C\uDDFF Telegram: @" + applicant.getUsername() + "\n" +
                    "\uD83D\uDCDE Aloqa: " + applicant.getPhoneNumber() + "\n" +
                    "\uD83C\uDF10 Hudud: " + applicant.getRegion() + "\n" +
                    "\uD83C\uDF10 Mas'ul: " + applicant.getNameCharge() + "\n" +
                    "\uD83D\uDD70 Murojaat vaqti: " + applicant.getTime() + "\n" +
                    "\uD83D\uDD70 Ish vaqti: " + applicant.getJobTime() + "\n" +
                    "\uD83D\uDCB0 Maosh: " + applicant.getSalary() + "\n" +
                    "‼️ Qo`shimcha: " + applicant.getPurpose() + "\n" +
                    "\n" +
                    "#ishJoyi";
        }
        return null;
    }

    private static String getSalaryText() {
        return "\uD83D\uDCB0 Maoshni kiriting?";
    }

    private static String getJobTimeText() {
        return "\uD83D\uDD70 Ish vaqtini kiriting?";
    }

    private static String getNameChargeText() {
        return "✍️Mas'ul ism sharifi?";
    }

    private static String getPurposeText() {
        return """
                \uD83D\uDD0E <b>Maqsad:</b>

                Maqsadingizni qisqacha yozib bering.""";
    }

    private static String getTimeText() {
        return """
                \uD83D\uDD70 <b>Murojaat qilish vaqti:</b>

                Qaysi vaqtda murojaat qilish mumkin?
                Masalan, 9:00 - 18:00""";
    }

    private static String getProfessionText() {
        return """
                \uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB <b>Kasbi:</b>

                Ishlaysizmi yoki o`qiysizmi?
                Masalan, Talaba""";
    }

    private static String getPaymentText() {
        return """
                \uD83D\uDCB0 <b>Narxi:</b>

                Tolov qilasizmi yoki Tekinmi?
                Kerak bo`lsa, Summani kiriting?""";
    }

    private static String getRegionText() {
        return """
                \uD83C\uDF10 <b>Hudud:</b>

                Qaysi hududdansiz?
                Viloyat nomi, Toshkent shahar yoki Respublikani kiriting.
                """;
    }
}

