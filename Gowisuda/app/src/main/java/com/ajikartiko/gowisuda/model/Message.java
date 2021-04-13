package com.ajikartiko.gowisuda.model;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.ajikartiko.gowisuda.utils.MessageType;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Message implements Serializable {
    private String recipientId;
    private String senderId;
    private Date time;
    private String content;
    private MessageType messageType;

    public Message() {

    }

    public Message(String recipientId, String senderId, Date time, String content, MessageType messageType) {
        this.recipientId = recipientId;
        this.senderId = senderId;
        this.time = time;
        this.content = content;
        this.messageType = messageType;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public static final DiffUtil.ItemCallback<Message> DIFF_CALLBACK = new DiffUtil.ItemCallback<Message>() {
        @Override
        public boolean areItemsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
            return oldItem.content.equals(newItem.content);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message car = (Message) o;
        return Objects.equals(content, car.content) && Objects.equals(time, car.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, time);
    }

    @Override
    public String toString() {
        return "Message{" +
                "recipientId='" + recipientId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", time=" + time +
                ", content='" + content + '\'' +
                ", messageType=" + messageType +
                '}';
    }
}
