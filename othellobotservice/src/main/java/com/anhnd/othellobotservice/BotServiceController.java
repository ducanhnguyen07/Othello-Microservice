package com.anhnd.othellobotservice;

import com.anhnd.othellobotservice.model.Board;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class BotServiceController {

    @PostMapping("/game/move")
    public Board makeMove(@RequestBody Board board, @RequestParam int botId) {
        return makeBotMove(board, botId);
    }


    public Board makeBotMove(Board board, int botId) {
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