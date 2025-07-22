package com.github.jamesh321.chessengine;

import java.util.ArrayList;

/**
 * The Search class implements chess position analysis algorithms to find the
 * best move
 * in a given position. It uses the negamax algorithm, a variant of minimax, to
 * evaluate
 * chess positions and determine optimal moves.
 */
public class Search {
    private Search() {

    }

    /**
     * Finds the best move for the current player in the given position by searching
     * to the specified depth using the negamax algorithm.
     *
     * @param depth  the depth to search to (number of half-moves)
     * @param engine the chess engine containing the current game state
     * @return the best move found, or null if no legal moves exist or depth is 0
     */
    public static Move findBestMove(int depth, Engine engine) {
        if (depth == 0) {
            return null;
        }

        Move bestMove = null;
        int max = -100000;

        ArrayList<Move> moves = MoveGenerator.generateLegalMoves(engine.getBoard());

        if (moves.isEmpty()) {
            return null;
        }

        if (engine.getBoard().getHalfmoveClock() == 100) {
            return null;
        }

        for (Move move : moves) {
            engine.makeMove(move);

            int score = -negaMax(depth - 1, engine);

            engine.undoMove();

            if (score > max) {
                max = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    /**
     * Implements the negamax algorithm for chess position evaluation.
     * Negamax is a variant of minimax that relies on the zero-sum property of
     * chess,
     * simplifying the implementation by always maximizing from the current player's
     * perspective.
     *
     * @param depth  the remaining depth to search
     * @param engine the chess engine containing the current game state
     * @return the evaluation score from the perspective of the current player
     */
    public static int negaMax(int depth, Engine engine) {
        if (depth == 0) {
            return Evaluate.board(engine.getBoard());
        }

        int max = -100000;

        ArrayList<Move> moves = MoveGenerator.generateLegalMoves(engine.getBoard());

        if (moves.isEmpty()) {
            if (inCheck(engine.getBoard())) {
                return -100000;
            } else {
                return 0;
            }
        }

        if (engine.getBoard().getHalfmoveClock() == 100) {
            return 0;
        }

        for (Move move : moves) {
            engine.makeMove(move);

            int score = -negaMax(depth - 1, engine);

            engine.undoMove();

            if (score > max) {
                max = score;
            }
        }

        return max;
    }

    /**
     * Determines if the current player is in check.
     *
     * @param board the chess board to analyze
     * @return true if the current player's king is in check, false otherwise
     */
    private static boolean inCheck(Board board) {
        long kingBitboard = board.isWhiteTurn() ? board.getBitboard(Piece.WHITE_KING)
                : board.getBitboard(Piece.BLACK_KING);
        int kingSquare = Long.numberOfLeadingZeros(kingBitboard);

        if (MoveGenerator.isSquareAttacked(kingSquare, board)) {
            return true;
        }

        return false;
    }
}