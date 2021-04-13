package com.ajikartiko.gowisuda.utils;

import com.ajikartiko.gowisuda.model.Message;

import java.util.List;

public interface MessagesListener {
    void onSuccess(List<Message> messages);
}
