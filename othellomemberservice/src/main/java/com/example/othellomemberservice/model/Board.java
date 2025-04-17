package com.example.othellomemberservice.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board implements Serializable {

    private static final long serialVersionUID = 1L;

    // Constants
    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;
    public static final int BOARD_SIZE = 8;

    // Directions (8 hướng)
    private static final int[][] DIRECTIONS = {
            {-1, -1}, {-1, 0}, {-1, 1}, {0, -1},
            {0, 1}, {1, -1}, {1, 0}, {1, 1}
    };

    // Board state
    private int[][] cells;
    private int currentPlayer;
    private int blackCount;
    private int whiteCount;
    private boolean gameOver;
    private int winner;

    public Board() {
        cells = new int[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();
        currentPlayer = BLACK; // Người chơi đi trước (quân đen)
        updateScores();
        gameOver = false;
        winner = EMPTY;
    }

    /**
     * Khởi tạo bàn cờ với 4 quân ở vị trí trung tâm
     */
    private void initializeBoard() {
        // Đặt tất cả các ô là trống
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                cells[i][j] = EMPTY;
            }
        }

        // Đặt 4 quân ban đầu
        cells[3][3] = WHITE;
        cells[3][4] = BLACK;
        cells[4][3] = BLACK;
        cells[4][4] = WHITE;
    }

    /**
     * Cập nhật số quân của mỗi người chơi
     */
    public void updateScores() {
        blackCount = 0;
        whiteCount = 0;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (cells[i][j] == BLACK) {
                    blackCount++;
                } else if (cells[i][j] == WHITE) {
                    whiteCount++;
                }
            }
        }
    }

    /**
     * Kiểm tra nước đi có hợp lệ không
     */
    public boolean isValidMove(int row, int col, int player) {
        // Nếu ô đã có quân, không hợp lệ
        if (cells[row][col] != EMPTY) {
            return false;
        }

        int opponent = (player == BLACK) ? WHITE : BLACK;

        for (int[] dir : DIRECTIONS) {
            int dx = dir[0];
            int dy = dir[1];
            int r = row + dx;
            int c = col + dy;
            boolean hasOpponent = false;

            while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE) {
                if (cells[r][c] == EMPTY) {
                    break;
                }

                if (cells[r][c] == opponent) {
                    hasOpponent = true;
                } else if (cells[r][c] == player) {
                    if (hasOpponent) {
                        return true;
                    }
                    break;
                }

                r += dx;
                c += dy;
            }
        }

        return false;
    }

    /**
     * Thực hiện nước đi và lật các quân bị kẹp
     */
    public void makeMove(int row, int col, int player) {
        if (!isValidMove(row, col, player)) {
            return;
        }

        cells[row][col] = player;
        flipPieces(row, col, player);
        updateScores();

        // Chuyển lượt chơi
        switchPlayer();

        // Kiểm tra trò chơi kết thúc
        checkGameOver();
    }

    /**
     * Lật các quân bị kẹp sau khi đặt quân
     */
    private void flipPieces(int row, int col, int player) {
        int opponent = (player == BLACK) ? WHITE : BLACK;

        for (int[] dir : DIRECTIONS) {
            int dx = dir[0];
            int dy = dir[1];
            int r = row + dx;
            int c = col + dy;
            List<int[]> piecesToFlip = new ArrayList<>();

            while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE) {
                if (cells[r][c] == EMPTY) {
                    break;
                }

                if (cells[r][c] == opponent) {
                    piecesToFlip.add(new int[]{r, c});
                } else if (cells[r][c] == player) {
                    // Lật tất cả quân bị kẹp
                    for (int[] piece : piecesToFlip) {
                        cells[piece[0]][piece[1]] = player;
                    }
                    break;
                }

                r += dx;
                c += dy;
            }
        }
    }

    /**
     * Lấy danh sách các nước đi hợp lệ cho người chơi
     */
    public List<int[]> getValidMoves(int player) {
        List<int[]> validMoves = new ArrayList<>();

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (cells[row][col] == EMPTY && isValidMove(row, col, player)) {
                    validMoves.add(new int[]{row, col});
                }
            }
        }

        return validMoves;
    }

    /**
     * Kiểm tra người chơi có thể đi được không
     */
    public boolean canMove(int player) {
        return !getValidMoves(player).isEmpty();
    }

    /**
     * Chuyển lượt chơi
     */
    public void switchPlayer() {
        currentPlayer = (currentPlayer == BLACK) ? WHITE : BLACK;

        // Nếu người chơi hiện tại không thể đi
        if (!canMove(currentPlayer)) {
            // Chuyển lại lượt nếu người chơi kia vẫn có thể đi
            if (canMove((currentPlayer == BLACK) ? WHITE : BLACK)) {
                currentPlayer = (currentPlayer == BLACK) ? WHITE : BLACK;
            } else {
                // Nếu cả hai không thể đi, trò chơi kết thúc
                gameOver = true;
            }
        }
    }

    /**
     * Kiểm tra trò chơi kết thúc chưa
     */
    public void checkGameOver() {
        if (!canMove(BLACK) && !canMove(WHITE)) {
            gameOver = true;

            // Xác định người thắng
            if (blackCount > whiteCount) {
                winner = BLACK;
            } else if (whiteCount > blackCount) {
                winner = WHITE;
            } else {
                winner = EMPTY; // Hòa
            }
        }
    }

    // Getters and setters

    public int[][] getCells() {
        return cells;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getBlackCount() {
        return blackCount;
    }

    public int getWhiteCount() {
        return whiteCount;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getWinner() {
        return winner;
    }

    @Override
    public String toString() {
        return "Board{" +
                "cells=" + Arrays.toString(cells) +
                ", currentPlayer=" + currentPlayer +
                ", blackCount=" + blackCount +
                ", whiteCount=" + whiteCount +
                ", gameOver=" + gameOver +
                ", winner=" + winner +
                '}';
    }
}