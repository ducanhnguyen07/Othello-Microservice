package com.anhnd.othelloclient.model;

import java.util.Date;

public class Game {
    private String id;
    private Member player1;
    private Member player2;
    private Board board;
    private Date startTime;
    private Date endTime;
    private boolean isFinished;
    private int winnerId;
    private boolean isDraw;
    private int timeLimit;

    public Game() {
    }

    public Game(String id, Member player1, Member player2, Board board, Date startTime,
                Date endTime, boolean isFinished, int winnerId, boolean isDraw, int timeLimit) {
        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
        this.board = board;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isFinished = isFinished;
        this.winnerId = winnerId;
        this.isDraw = isDraw;
        this.timeLimit = timeLimit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Member getPlayer1() {
        return player1;
    }

    public void setPlayer1(Member player1) {
        this.player1 = player1;
    }

    public Member getPlayer2() {
        return player2;
    }

    public void setPlayer2(Member player2) {
        this.player2 = player2;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public int getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(int winnerId) {
        this.winnerId = winnerId;
    }

    public boolean isDraw() {
        return isDraw;
    }

    public void setDraw(boolean draw) {
        isDraw = draw;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }
}