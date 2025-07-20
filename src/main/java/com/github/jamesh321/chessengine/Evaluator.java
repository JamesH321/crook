package com.github.jamesh321.chessengine;

public class Evaluator {

    private Evaluator() {
        // private constructor to prevent instantiation of this utility class
    }

    public static final int PAWN_VALUE = 100;
    public static final int KNIGHT_VALUE = 300;
    public static final int BISHOP_VALUE = 300;
    public static final int ROOK_VALUE = 500;
    public static final int QUEEN_VALUE = 900;
    public static final int KING_VALUE = 10000;

    public static int evaluateBoard(Board board) {
        int score = 0;

        score += evaluateMaterial(board);
        score += evaluatePiecePositions(board);

        return score;
    }

    public static int evaluateMaterial(Board board) {
        int score = 0;

        score += getWhiteMaterial(board);
        score -= getBlackMaterial(board);

        return score;
    }

    public static int evaluatePiecePositions(Board board) {
        int score = 0;

        score += evaluateWhitePiecePositions(board);
        score -= evaluateBlackPiecePositions(board);

        return score;
    }

    private static int evaluateWhitePiecePositions(Board board) {
        int score = 0;

        score += evaluatePiecePositionScore(Piece.WHITE_PAWN, PieceSquareTables.WHITE_PAWN_PST, board);
        score += evaluatePiecePositionScore(Piece.WHITE_KNIGHT, PieceSquareTables.WHITE_KNIGHT_PST, board);
        score += evaluatePiecePositionScore(Piece.WHITE_BISHOP, PieceSquareTables.WHITE_BISHOP_PST, board);
        score += evaluatePiecePositionScore(Piece.WHITE_ROOK, PieceSquareTables.WHITE_ROOK_PST, board);
        score += evaluatePiecePositionScore(Piece.WHITE_QUEEN, PieceSquareTables.WHITE_QUEEN_PST, board);
        score += isEndGame(board)
                ? evaluatePiecePositionScore(Piece.WHITE_KING, PieceSquareTables.WHITE_KING_END_GAME_PST, board)
                : evaluatePiecePositionScore(Piece.WHITE_KING, PieceSquareTables.WHITE_KING_MIDDLE_GAME_PST, board);

        return score;
    }

    private static int evaluateBlackPiecePositions(Board board) {
        int score = 0;

        score += evaluatePiecePositionScore(Piece.BLACK_PAWN, PieceSquareTables.BLACK_PAWN_PST, board);
        score += evaluatePiecePositionScore(Piece.BLACK_KNIGHT, PieceSquareTables.BLACK_KNIGHT_PST, board);
        score += evaluatePiecePositionScore(Piece.BLACK_BISHOP, PieceSquareTables.BLACK_BISHOP_PST, board);
        score += evaluatePiecePositionScore(Piece.BLACK_ROOK, PieceSquareTables.BLACK_ROOK_PST, board);
        score += evaluatePiecePositionScore(Piece.BLACK_QUEEN, PieceSquareTables.BLACK_QUEEN_PST, board);
        score += isEndGame(board)
                ? evaluatePiecePositionScore(Piece.BLACK_KING, PieceSquareTables.BLACK_KING_END_GAME_PST, board)
                : evaluatePiecePositionScore(Piece.BLACK_KING, PieceSquareTables.BLACK_KING_MIDDLE_GAME_PST, board);

        return score;
    }

    private static int evaluatePiecePositionScore(Piece piece, int[] pieceSquareTable, Board board) {
        long pieceBitboard = board.getBitboard(piece);

        int score = 0;

        while (pieceBitboard != 0) {
            int from = 63 - Long.numberOfTrailingZeros(pieceBitboard);
            System.out.println(from);

            score += pieceSquareTable[from];

            pieceBitboard &= pieceBitboard - 1;
        }

        return score;
    }

    private static boolean isEndGame(Board board) {
        int pieceCount = 0;

        for (long pieceBitboard : board.getBitboards()) {
            pieceCount += Long.bitCount(pieceBitboard);

            if (pieceCount >= 16) {
                return false;
            }
        }

        return true;
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
