package com.anhnd.othelloclient.model;

import java.util.Date;

public class Challenge {
    private String id;
    private Member challenger;
    private Member challenged;
    private Date createdAt;
    private Date expiresAt;
    private String status; // "PENDING", "ACCEPTED", "REJECTED", "EXPIRED"
    private String gameId;

    public Challenge() {
    }

    public Challenge(String id, Member challenger, Member challenged, Date createdAt,
                     Date expiresAt, String status, String gameId) {
        this.id = id;
        this.challenger = challenger;
        this.challenged = challenged;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.status = status;
        this.gameId = gameId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Member getChallenger() {
        return challenger;
    }

    public void setChallenger(Member challenger) {
        this.challenger = challenger;
    }

    public Member getChallenged() {
        return challenged;
    }

    public void setChallenged(Member challenged) {
        this.challenged = challenged;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}