package com.ajikartiko.go_wisuda_dosen.utils;

import com.ajikartiko.go_wisuda_dosen.model.Message;

import java.util.List;

public interface MessagesListener {
    void onSuccess(List<Message> messages);
}
