package com.anhnd.othelloclient.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GameRoom {
    private String id;
    private String name;
    private String roomCode;
    private Member host;
    private List<Member> players;
    private boolean isPrivate;
    private Date createdAt;
    private int timeLimit;
    private String status; // "WAITING", "PLAYING", "FINISHED"

    public GameRoom() {
        this.players = new ArrayList<>();
    }

    public GameRoom(String id, String name, String roomCode, Member host, boolean isPrivate,
                    Date createdAt, int timeLimit, String status) {
        this.id = id;
        this.name = name;
        this.roomCode = roomCode;
        this.host = host;
        this.players = new ArrayList<>();
        if (host != null) {
            this.players.add(host);
        }
        this.isPrivate = isPrivate;
        this.createdAt = createdAt;
        this.timeLimit = timeLimit;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public Member getHost() {
        return host;
    }

    public void setHost(Member host) {
        this.host = host;
    }

    public List<Member> getPlayers() {
        return players;
    }

    public void setPlayers(List<Member> players) {
        this.players = players;
    }

    public void addPlayer(Member player) {
        if (this.players == null) {
            this.players = new ArrayList<>();
        }
        this.players.add(player);
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}