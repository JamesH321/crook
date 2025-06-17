package com.github.jamesh321.chessengine;

import java.util.Stack;

public class Engine {
    private Board board;
    private Stack<BoardState> history;

    public Engine(Board board) {
        this.board = board;
        this.history = new Stack<>();
    }

    public void makeMove(Move move) {
        history.push(new BoardState(board));
        MoveExecutor.makeMove(board, move);
    }

    public void undoMove() {
        if (!history.isEmpty()) {
            board.restoreState(history.pop());
        }
    }
}
