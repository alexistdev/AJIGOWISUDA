package com.ajikartiko.go_wisuda_dosen.model;

import java.util.ArrayList;
import java.util.List;

public class ChatChannel {
    private List<String> userIds = new ArrayList<>();

    public ChatChannel() {
    }

    public ChatChannel(List<String> userIds) {
        this.userIds = userIds;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
}
