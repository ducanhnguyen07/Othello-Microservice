package com.example.othellomemberservice.model;

import java.util.Date;

public class FriendInvitation {
    private int id;
    private int requestId;
    private int receiveId;
    private String status;
    private Date timeRequest;
    private Date timeUpdate;

    public FriendInvitation() {
    }

    public FriendInvitation(int id, int requestId, int receiveId, String status, Date timeRequest, Date timeUpdate) {
        this.id = id;
        this.requestId = requestId;
        this.receiveId = receiveId;
        this.status = status;
        this.timeRequest = timeRequest;
        this.timeUpdate = timeUpdate;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(int receiveId) {
        this.receiveId = receiveId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTimeRequest() {
        return timeRequest;
    }

    public void setTimeRequest(Date timeRequest) {
        this.timeRequest = timeRequest;
    }

    public Date getTimeUpdate() {
        return timeUpdate;
    }

    public void setTimeUpdate(Date timeUpdate) {
        this.timeUpdate = timeUpdate;
    }
}