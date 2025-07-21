package com.github.jamesh321.chessengine;

import java.util.ArrayList;

public class Search {
    private Search() {

    }

    public static Move findBestMove(int depth, Engine engine) {
        if (depth == 0) {
            return null;
        }

        Move bestMove = null;
        int max = Integer.MIN_VALUE;

        ArrayList<Move> moves = MoveGenerator.generateLegalMoves(engine.getBoard());

        if (moves.isEmpty()) {
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

    public static int negaMax(int depth, Engine engine) {
        if (depth == 0) {
            return Evaluate.board(engine.getBoard());
        }

        int max = Integer.MIN_VALUE;

        ArrayList<Move> moves = MoveGenerator.generateLegalMoves(engine.getBoard());

        if (moves.isEmpty()) {
            if (isCheckmate(engine.getBoard())) {
                return Integer.MIN_VALUE;
            } else {
                return 0;
            }
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

    private static boolean isCheckmate(Board board) {
        long kingBitboard = board.isWhiteTurn() ? board.getBitboard(Piece.WHITE_KING)
                : board.getBitboard(Piece.BLACK_KING);
        int kingSquare = Long.numberOfLeadingZeros(kingBitboard);

        if (MoveGenerator.isSquareAttacked(kingSquare, board)) {
            return true;
        }

        return false;
    }
}