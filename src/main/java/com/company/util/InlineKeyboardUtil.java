package com.company.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class InlineKeyboardUtil {
    public static InlineKeyboardMarkup getConfirmOrCancelMenu() {
        InlineKeyboardButton confirm = new InlineKeyboardButton(InlineButtonConstants.CONFIRM_DEMO);
        confirm.setCallbackData(InlineButtonConstants.CONFIRM_CALL_BACK);

        InlineKeyboardButton cancel = new InlineKeyboardButton(InlineButtonConstants.CANCEL_DEMO);
        cancel.setCallbackData(InlineButtonConstants.CANCEL_CALL_BACK);

        return new InlineKeyboardMarkup(List.of(
                List.of(confirm, cancel)
        ));
    }
}
