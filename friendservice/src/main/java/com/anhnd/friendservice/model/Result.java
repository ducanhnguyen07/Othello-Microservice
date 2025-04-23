package com.anhnd.friendservice.model;

public class Result {
    private int id;
    private Member memberA;
    private Member memberB;
    private Bot bot;
    private boolean resAtoB;
    private boolean resAtoBot;

    public Result() {
    }

    public Result(int id, Member memberA, Member memberB, Bot bot, boolean resAtoB, boolean resAtoBot) {
        this.id = id;
        this.memberA = memberA;
        this.memberB = memberB;
        this.bot = bot;
        this.resAtoB = resAtoB;
        this.resAtoBot = resAtoBot;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMemberA() {
        return memberA.getId();
    }

    public void setMemberA(Member memberA) {
        this.memberA = memberA;
    }

    public int getMemberB() {
        return memberB.getId();
    }

    public void setMemberB(Member memberB) {
        this.memberB = memberB;
    }

    public int getBot() {
        return bot.getId();
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public boolean isResAtoB() {
        return resAtoB;
    }

    public void setResAtoB(boolean resAtoB) {
        this.resAtoB = resAtoB;
    }

    public boolean isResAtoBot() {
        return resAtoBot;
    }

    public void setResAtoBot(boolean resAtoBot) {
        this.resAtoBot = resAtoBot;
    }
}
