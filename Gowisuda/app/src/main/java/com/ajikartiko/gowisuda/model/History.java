package com.ajikartiko.gowisuda.model;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.ajikartiko.gowisuda.utils.Utils;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class History implements Serializable, Comparable<History> {
    private String chatId;
    private String day;
    private List<Message> messages;

    public History() {
    }

    public History(String chatId, String day, List<Message> messages) {
        this.chatId = chatId;
        this.day = day;
        this.messages = messages;
    }

    public String getChatId() {
        return chatId;
    }

    public String getDay() {
        return day;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public static final DiffUtil.ItemCallback<History> DIFF_CALLBACK = new DiffUtil.ItemCallback<History>() {
        @Override
        public boolean areItemsTheSame(@NonNull History oldItem, @NonNull History newItem) {
            return oldItem.day.equals(newItem.day);
        }

        @Override
        public boolean areContentsTheSame(@NonNull History oldItem, @NonNull History newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        History history = (History) o;
        return Objects.equals(day, history.day) && Objects.equals(messages, history.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, messages);
    }

    @Override
    public int compareTo(History o) {
        if (getDay()==null ||o.getDay()==null) return 0;
        return Utils.stringToDate(getDay(),"EEEE, dd-MMMM-yyyy").compareTo(Utils.stringToDate(o.getDay(),"EEEE, dd-MMMM-yyyy"));
    }
}
