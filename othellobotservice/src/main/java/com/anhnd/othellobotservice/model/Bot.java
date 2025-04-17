package com.anhnd.othellobotservice.model;


import java.util.List;
import java.util.Random;

public class Bot {
    private int id;
    private String urlModel;

    public Bot() {
    }

    public Bot(int id, String urlModel) {
        this.id = id;
        this.urlModel = urlModel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrlModel() {
        return urlModel;
    }

    public void setUrlModel(String urlModel) {
        this.urlModel = urlModel;
    }

    public Board makeMove(Board board) {
        // Get all valid moves for the current player (WHITE = 2)
        List<int[]> validMoves = board.getValidMoves(Board.WHITE);

        // If no valid moves, return board unchanged
        if (validMoves.isEmpty()) {
            return board;
        }

        // Choose a random move from valid moves
        Random random = new Random();
        int[] selectedMove = validMoves.get(random.nextInt(validMoves.size()));

        // Make the move
        board.makeMove(selectedMove[0], selectedMove[1], Board.WHITE);

        return board;
    }
}
