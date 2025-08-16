package com.github.jamesh321.crook;

/**
 * This class contains Piece-Square Tables (PSTs) for position evaluation.
 * <p>
 * PSTs are used to evaluate the positional value of pieces based on their
 * location on the board.
 * Each square on the board is assigned a value for each type of piece. A higher
 * value means the square is more
 * advantageous for that piece.
 * <p>
 * The tables are defined from White's perspective. The tables for Black pieces
 * are the reverse of the White tables.
 * The indices of the array correspond to squares on the board, from square 0
 * (A8) to 63 (H1).
 */
public class PieceSquareTables {
    /**
     * Piece-Square Table for white pawns.
     */
    public static final int[] WHITE_PAWN_PST = {
            0, 0, 0, 0, 0, 0, 0, 0,
            50, 50, 50, 50, 50, 50, 50, 50,
            10, 10, 20, 30, 30, 20, 10, 10,
            5, 5, 10, 25, 25, 10, 5, 5,
            0, 0, 0, 20, 20, 0, 0, 0,
            5, -5, -10, 0, 0, -10, -5, 5,
            5, 10, 10, -20, -20, 10, 10, 5,
            0, 0, 0, 0, 0, 0, 0, 0
    };

    /**
     * Piece-Square Table for white knights.
     */
    public static final int[] WHITE_KNIGHT_PST = {
            -50, -40, -30, -30, -30, -30, -40, -50,
            -40, -20, 0, 0, 0, 0, -20, -40,
            -30, 0, 10, 15, 15, 10, 0, -30,
            -30, 5, 15, 20, 20, 15, 5, -30,
            -30, 0, 15, 20, 20, 15, 0, -30,
            -30, 5, 10, 15, 15, 10, 5, -30,
            -40, -20, 0, 5, 5, 0, -20, -40,
            -50, -40, -30, -30, -30, -30, -40, -50
    };

    /**
     * Piece-Square Table for white bishops.
     */
    public static final int[] WHITE_BISHOP_PST = {
            -20, -10, -10, -10, -10, -10, -10, -20,
            -10, 0, 0, 0, 0, 0, 0, -10,
            -10, 0, 5, 10, 10, 5, 0, -10,
            -10, 5, 5, 10, 10, 5, 5, -10,
            -10, 0, 10, 10, 10, 10, 0, -10,
            -10, 10, 10, 10, 10, 10, 10, -10,
            -10, 5, 0, 0, 0, 0, 5, -10,
            -20, -10, -10, -10, -10, -10, -10, -20
    };

    /**
     * Piece-Square Table for white rooks.
     */
    public static final int[] WHITE_ROOK_PST = {
            0, 0, 0, 0, 0, 0, 0, 0,
            5, 10, 10, 10, 10, 10, 10, 5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            0, 0, 0, 5, 5, 0, 0, 0
    };

    /**
     * Piece-Square Table for white queens.
     */
    public static final int[] WHITE_QUEEN_PST = {
            -20, -10, -10, -5, -5, -10, -10, -20,
            -10, 0, 0, 0, 0, 0, 0, -10,
            -10, 0, 5, 5, 5, 5, 0, -10,
            -5, 0, 5, 5, 5, 5, 0, -5,
            0, 0, 5, 5, 5, 5, 0, -5,
            -10, 5, 5, 5, 5, 5, 0, -10,
            -10, 0, 5, 0, 0, 0, 0, -10,
            -20, -10, -10, -5, -5, -10, -10, -20
    };

    /**
     * Piece-Square Table for the white king during the middle phase of the game.
     */
    public static final int[] WHITE_KING_MIDDLE_GAME_PST = {
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -20, -30, -30, -40, -40, -30, -30, -20,
            -10, -20, -20, -20, -20, -20, -20, -10,
            20, 20, 0, 0, 0, 0, 20, 20,
            20, 30, 10, 0, 0, 10, 30, 20
    };

    /**
     * Piece-Square Table for the white king during the end phase of the game.
     */
    public static final int[] WHITE_KING_END_GAME_PST = {
            -50, -40, -30, -20, -20, -30, -40, -50,
            -30, -20, -10, 0, 0, -10, -20, -30,
            -30, -10, 20, 30, 30, 20, -10, -30,
            -30, -10, 30, 40, 40, 30, -10, -30,
            -30, -10, 30, 40, 40, 30, -10, -30,
            -30, -10, 20, 30, 30, 20, -10, -30,
            -30, -30, 0, 0, 0, 0, -30, -30,
            -50, -30, -30, -30, -30, -30, -30, -50
    };

    /**
     * Piece-Square Table for black pawns.
     */
    public static final int[] BLACK_PAWN_PST = new int[64];

    /**
     * Piece-Square Table for black knights.
     */
    public static final int[] BLACK_KNIGHT_PST = new int[64];

    /**
     * Piece-Square Table for black bishops.
     */
    public static final int[] BLACK_BISHOP_PST = new int[64];

    /**
     * Piece-Square Table for black rooks.
     */
    public static final int[] BLACK_ROOK_PST = new int[64];

    /**
     * Piece-Square Table for black queens.
     */
    public static final int[] BLACK_QUEEN_PST = new int[64];

    /**
     * Piece-Square Table for the black king during the middle phase of the game.
     */
    public static final int[] BLACK_KING_MIDDLE_GAME_PST = new int[64];

    /**
     * Piece-Square Table for the black king during the end phase of the game.
     */
    public static final int[] BLACK_KING_END_GAME_PST = new int[64];

    static {
        initialiseBlackPST(WHITE_PAWN_PST, BLACK_PAWN_PST);
        initialiseBlackPST(WHITE_KNIGHT_PST, BLACK_KNIGHT_PST);
        initialiseBlackPST(WHITE_BISHOP_PST, BLACK_BISHOP_PST);
        initialiseBlackPST(WHITE_ROOK_PST, BLACK_ROOK_PST);
        initialiseBlackPST(WHITE_QUEEN_PST, BLACK_QUEEN_PST);
        initialiseBlackPST(WHITE_KING_MIDDLE_GAME_PST, BLACK_KING_MIDDLE_GAME_PST);
        initialiseBlackPST(WHITE_KING_END_GAME_PST, BLACK_KING_END_GAME_PST);
    }

    /**
     * Initialises a Piece-Square Table for a black piece by flipping the
     * corresponding white piece's table.
     *
     * @param whitePST the Piece-Square Table for the white piece
     * @param blackPST the array to be filled with the Piece-Square Table for the
     *                 black piece
     */
    public static void initialiseBlackPST(int[] whitePST, int[] blackPST) {
        for (int square = 0; square < 64; square++) {
            blackPST[square] = whitePST[63 - square];
        }
    }
}