package com.github.jamesh321.chessengine;

/**
 * The Evaluator class provides static methods for evaluating a chess board
 * position.
 * It calculates a score based on material balance and piece-square tables.
 * A positive score indicates an advantage for White, while a negative score
 * indicates an advantage for Black.
 */
public final class Evaluate {

    private Evaluate() {
        // private constructor to prevent instantiation of this utility class
    }

    public static final int PAWN_VALUE = 100;
    public static final int KNIGHT_VALUE = 300;
    public static final int BISHOP_VALUE = 300;
    public static final int ROOK_VALUE = 500;
    public static final int QUEEN_VALUE = 900;
    public static final int KING_VALUE = 10000;

    /**
     * Evaluates the given board and returns a score representing the position.
     * The evaluation is based on material and piece positions.
     *
     * @param board the board to evaluate
     * @return the score of the board position. Positive for white's advantage,
     *         negative for black's
     */
    public static int board(Board board) {
        int score = 0;

        score += material(board);
        score += piecePositions(board);

        return score;
    }

    /**
     * Calculates the material balance of the board.
     * It sums the values of white's pieces and subtracts the values of black's
     * pieces.
     *
     * @param board the board to evaluate
     * @return the material score
     */
    public static int material(Board board) {
        int score = 0;

        score += getWhiteMaterial(board);
        score -= getBlackMaterial(board);

        return score;
    }

    /**
     * Evaluates the positions of the pieces on the board using Piece-Square Tables.
     *
     * @param board the board to evaluate
     * @return the positional score
     */
    public static int piecePositions(Board board) {
        int score = 0;

        score += whitePiecePositions(board);
        score -= blackPiecePositions(board);

        return score;
    }

    /**
     * Evaluates the positions of white's pieces.
     *
     * @param board the board to evaluate
     * @return the positional score for white's pieces
     */
    private static int whitePiecePositions(Board board) {
        int score = 0;

        score += piecePositionScore(Piece.WHITE_PAWN, PieceSquareTables.WHITE_PAWN_PST, board);
        score += piecePositionScore(Piece.WHITE_KNIGHT, PieceSquareTables.WHITE_KNIGHT_PST, board);
        score += piecePositionScore(Piece.WHITE_BISHOP, PieceSquareTables.WHITE_BISHOP_PST, board);
        score += piecePositionScore(Piece.WHITE_ROOK, PieceSquareTables.WHITE_ROOK_PST, board);
        score += piecePositionScore(Piece.WHITE_QUEEN, PieceSquareTables.WHITE_QUEEN_PST, board);
        score += isEndGame(board)
                ? piecePositionScore(Piece.WHITE_KING, PieceSquareTables.WHITE_KING_END_GAME_PST, board)
                : piecePositionScore(Piece.WHITE_KING, PieceSquareTables.WHITE_KING_MIDDLE_GAME_PST, board);

        return score;
    }

    /**
     * Evaluates the positions of black's pieces.
     *
     * @param board the board to evaluate
     * @return the positional score for black's pieces
     */
    private static int blackPiecePositions(Board board) {
        int score = 0;

        score += piecePositionScore(Piece.BLACK_PAWN, PieceSquareTables.BLACK_PAWN_PST, board);
        score += piecePositionScore(Piece.BLACK_KNIGHT, PieceSquareTables.BLACK_KNIGHT_PST, board);
        score += piecePositionScore(Piece.BLACK_BISHOP, PieceSquareTables.BLACK_BISHOP_PST, board);
        score += piecePositionScore(Piece.BLACK_ROOK, PieceSquareTables.BLACK_ROOK_PST, board);
        score += piecePositionScore(Piece.BLACK_QUEEN, PieceSquareTables.BLACK_QUEEN_PST, board);
        score += isEndGame(board)
                ? piecePositionScore(Piece.BLACK_KING, PieceSquareTables.BLACK_KING_END_GAME_PST, board)
                : piecePositionScore(Piece.BLACK_KING, PieceSquareTables.BLACK_KING_MIDDLE_GAME_PST, board);

        return score;
    }

    /**
     * Calculates the positional score for a given piece type using its Piece-Square
     * Table.
     *
     * @param piece            the piece type to evaluate
     * @param pieceSquareTable the corresponding Piece-Square Table
     * @param board            the board
     * @return the total positional score for the given piece type
     */
    private static int piecePositionScore(Piece piece, int[] pieceSquareTable, Board board) {
        long pieceBitboard = board.getBitboard(piece);

        int score = 0;

        while (pieceBitboard != 0) {
            int from = 63 - Long.numberOfTrailingZeros(pieceBitboard);

            score += pieceSquareTable[from];

            pieceBitboard &= pieceBitboard - 1;
        }

        return score;
    }

    /**
     * Determines if the game is in the endgame phase.
     * If the total number of pieces on the board is less than 16,
     * it's considered the endgame.
     *
     * @param board the board to check
     * @return true if the game is in the endgame, false otherwise
     */
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

    /**
     * Calculates the total material value for white's pieces.
     *
     * @param board the board
     * @return the material score for white
     */
    private static int getWhiteMaterial(Board board) {
        int score = 0;

        score += getPieceScore(Piece.WHITE_PAWN, PAWN_VALUE, board);
        score += getPieceScore(Piece.WHITE_KNIGHT, KNIGHT_VALUE, board);
        score += getPieceScore(Piece.WHITE_BISHOP, BISHOP_VALUE, board);
        score += getPieceScore(Piece.WHITE_ROOK, ROOK_VALUE, board);
        score += getPieceScore(Piece.WHITE_QUEEN, QUEEN_VALUE, board);

        return score;
    }

    /**
     * Calculates the total material value for black's pieces.
     *
     * @param board the board
     * @return the material score for black
     */
    private static int getBlackMaterial(Board board) {
        int score = 0;

        score += getPieceScore(Piece.BLACK_PAWN, PAWN_VALUE, board);
        score += getPieceScore(Piece.BLACK_KNIGHT, KNIGHT_VALUE, board);
        score += getPieceScore(Piece.BLACK_BISHOP, BISHOP_VALUE, board);
        score += getPieceScore(Piece.BLACK_ROOK, ROOK_VALUE, board);
        score += getPieceScore(Piece.BLACK_QUEEN, QUEEN_VALUE, board);

        return score;
    }

    /**
     * Calculates the score for a specific piece type based on its count and value.
     *
     * @param piece      the piece type
     * @param pieceValue the value of the piece
     * @param board      the board
     * @return the total score for the given piece type
     */
    private static int getPieceScore(Piece piece, int pieceValue, Board board) {
        return Long.bitCount(board.getBitboard(piece)) * pieceValue;
    }
}
