package com.github.jamesh321.chessengine;

import java.util.Stack;
import java.util.HashMap;

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

    public void setBoard(Board board) {
        this.board = board;
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

    /**
     * Finds the best move for the current player by running a search.
     *
     * @param depth        the depth to search to (number of half-moves)
     * @param lastBestMove the previously found best move to prioritise in move
     *                     ordering
     * @param endTime      the timestamp at which the search should terminate
     * @return the best move found, or null if no legal moves exist, depth is 0, or
     *         time has expired
     */
    public HashMap<String, Object> findBestMove(int depth, Move lastBestMove) {
        Search search = new Search();

        Move bestMove = search.findBestMove(depth, lastBestMove, this);
        long nodes = search.getNodes();
        long time = search.getTime();
        long nps;
        try {
            nps = (long) (nodes / (time / 1000.0));
        } catch (Exception e) {
            nps = 0;
        }
        int score = search.getScore();

        HashMap<String, Object> moveInfo = new HashMap<>();
        moveInfo.put("best move", bestMove);
        moveInfo.put("nodes", nodes);
        moveInfo.put("time", time);
        moveInfo.put("nps", nps);
        moveInfo.put("score", score);

        return moveInfo;
    }
}
