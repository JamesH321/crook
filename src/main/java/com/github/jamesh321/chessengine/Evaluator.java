package com.github.jamesh321.chessengine;

public class Evaluator {
    public static final int PAWN_VALUE = 100;
    public static final int KNIGHT_VALUE = 300;
    public static final int BISHOP_VALUE = 300;
    public static final int ROOK_VALUE = 500;
    public static final int QUEEN_VALUE = 900;
    public static final int KING_VALUE = 10000;

    public static int evaluateBoard(Board board) {
        int score = 0;

        score += evaluateMaterial(board);

        return score;
    }

    public static int evaluateMaterial(Board board) {
        int score = 0;

        score += getWhiteMaterial(board);
        score -= getBlackMaterial(board);

        return score;
    }

    private static int getWhiteMaterial(Board board) {
        int score = 0;

        score += getPieceScore(Piece.WHITE_PAWN, PAWN_VALUE, board);
        score += getPieceScore(Piece.WHITE_KNIGHT, KNIGHT_VALUE, board);
        score += getPieceScore(Piece.WHITE_BISHOP, BISHOP_VALUE, board);
        score += getPieceScore(Piece.WHITE_ROOK, ROOK_VALUE, board);
        score += getPieceScore(Piece.WHITE_QUEEN, QUEEN_VALUE, board);

        return score;
    }

    private static int getBlackMaterial(Board board) {
        int score = 0;

        score += getPieceScore(Piece.BLACK_PAWN, PAWN_VALUE, board);
        score += getPieceScore(Piece.BLACK_KNIGHT, KNIGHT_VALUE, board);
        score += getPieceScore(Piece.BLACK_BISHOP, BISHOP_VALUE, board);
        score += getPieceScore(Piece.BLACK_ROOK, ROOK_VALUE, board);
        score += getPieceScore(Piece.BLACK_QUEEN, QUEEN_VALUE, board);

        return score;
    }

    private static int getPieceScore(Piece piece, int pieceValue, Board board) {
        return Long.bitCount(board.getBitboard(piece)) * pieceValue;
    }
}
