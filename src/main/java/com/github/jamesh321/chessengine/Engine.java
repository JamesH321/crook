package com.github.jamesh321.chessengine;

import java.util.Stack;

/**
 * The Engine class manages the state of a chess game, including move execution
 * and undo functionality.
 * <p>
 * It maintains a history of board states to allow moves to be undone and
 * provides access to the current board.
 */
public class Engine {
    private Board board;
    private Stack<BoardState> history;

    /**
     * Constructs an Engine with the specified board.
     *
     * @param board the initial board state
     */
    public Engine(Board board) {
        this.board = board;
        this.history = new Stack<>();
    }

    /**
     * Returns the current board.
     *
     * @return the current {@link Board}
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Executes the given move and saves the previous board state to history.
     *
     * @param move the {@link Move} to execute
     */
    public void makeMove(Move move) {
        history.push(new BoardState(board));
        MoveExecutor.makeMove(board, move);
    }

    /**
     * Undoes the last move, restoring the previous board state if available.
     */
    public void undoMove() {
        if (!history.isEmpty()) {
            board.restoreState(history.pop());
        }
    }

    public Move findBestMove(int depth) {
        return Search.findBestMove(depth, this);
    }
}
