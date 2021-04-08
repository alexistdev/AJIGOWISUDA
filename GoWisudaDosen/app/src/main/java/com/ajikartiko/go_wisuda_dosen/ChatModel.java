package com.ajikartiko.go_wisuda_dosen;

public class ChatModel {

    String sender, id_receiver, receiver, message;

    public ChatModel() {
    }

    public ChatModel(String sender, String id_receiver, String receiver, String message) {
        this.sender = sender;
        this.id_receiver = id_receiver;
        this.receiver = receiver;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getId_receiver() {
        return id_receiver;
    }

    public void setId_receiver(String id_receiver) {
        this.id_receiver = id_receiver;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}