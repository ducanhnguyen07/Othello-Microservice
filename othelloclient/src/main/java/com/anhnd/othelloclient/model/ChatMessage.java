package com.anhnd.othelloclient.model;

import java.util.Date;

public class ChatMessage {
    private Long id;
    private String gameId;
    private Member sender;
    private String message;
    private Date timestamp;

    public ChatMessage() {
    }

    public ChatMessage(Long id, String gameId, Member sender, String message, Date timestamp) {
        this.id = id;
        this.gameId = gameId;
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public Member getSender() {
        return sender;
    }

    public void setSender(Member sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}