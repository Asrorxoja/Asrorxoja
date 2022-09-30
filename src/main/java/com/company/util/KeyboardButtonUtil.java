package com.company.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

import static com.company.util.KeyboardButtonConstants.*;

public class KeyboardButtonUtil {

    public static ReplyKeyboard getMenu() {
        List<KeyboardRow> rowList = getRowList(
                getRow(getButton(NEED_PARTNER), getButton(NEED_JOB)),
                getRow(getButton(NEED_EMPLOYEE), getButton(NEED_TEACHER)),
                getRow(getButton(NEED_APPRENTICE))
        );

        return getMarkup(rowList);
    }

    public static ReplyKeyboard getContactButton() {
        KeyboardButton button = getButton(CONTACT_DEMO);
        button.setRequestContact(true);

        return getMarkup(getRowList(getRow(button)));
    }

    public static ReplyKeyboard getYesOrNo(){
        return getMarkup(getRowList(getRow(getButton(YES), getButton(NO))));
    }

    private static KeyboardButton getButton(String demo) {
        return new KeyboardButton(demo);
    }

    private static KeyboardRow getRow(KeyboardButton... buttons) {
        return new KeyboardRow(List.of(buttons));
    }

    private static List<KeyboardRow> getRowList(KeyboardRow... rows) {
        return List.of(rows);
    }

    private static ReplyKeyboardMarkup getMarkup(List<KeyboardRow> rowList) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(rowList);
        markup.setResizeKeyboard(true);
        markup.setSelective(true);
        return markup;
    }
}
