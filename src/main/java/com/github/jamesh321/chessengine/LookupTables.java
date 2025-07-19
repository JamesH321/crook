package com.github.jamesh321.chessengine;

/**
 * Generates and stores tables for each piece that contain all the possible
 * moves from every square.
 */
public final class LookupTables {

    private LookupTables() {
        // private constructor to prevent instantiation of this utility class
    }

    /**
     * A lookup table for all possible white pawn attacks.
     */
    public static final long[] WHITE_PAWN_ATTACKS = new long[64];
    /**
     * A lookup table for all possible black pawn attacks.
     */
    public static final long[] BLACK_PAWN_ATTACKS = new long[64];
    /**
     * A lookup table for all possible knight moves.
     */
    public static final long[] KNIGHT_MOVES = new long[64];
    /**
     * A lookup table for all possible king moves.
     */
    public static final long[] KING_MOVES = new long[64];
    /**
     * A lookup table for all possible diagonal rays.
     */
    public static final long[][] DIAGONAL_RAYS = new long[64][4];
    /**
     * A lookup table for all possible straight rays.
     */
    public static final long[][] STRAIGHT_RAYS = new long[64][4];

    public static final int N = 0;
    public static final int E = 1;
    public static final int S = 2;
    public static final int W = 3;

    public static final int NE = 0;
    public static final int NW = 1;
    public static final int SE = 2;
    public static final int SW = 3;

    // File, rank
    private static final int[][] WHITE_PAWN_DIRECTION = { { -1, -1 }, { 1, -1 } };
    private static final int[][] BLACK_PAWN_DIRECTION = { { -1, 1 }, { 1, 1 } };
    private static final int[][] KNIGHT_DIRECTION = { { 2, 1 }, { 1, 2 }, { -1, 2 }, { -2, 1 }, { -2, -1 }, { -1, -2 },
            { 1, -2 }, { 2, -1 } };
    private static final int[][] KING_DIRECTION = { { 0, -1 }, { 1, -1 }, { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 1 },
            { -1, 0 }, { -1, -1 } };
    private static final int[][] DIAGONAL_DIRECTION = { { 1, -1 }, { -1, -1 }, { 1, 1 }, { -1, 1 } };
    private static final int[][] STRAIGHT_DIRECTION = { { 0, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 } };

    static {
        initializeWhitePawnMoves();
        initializeBlackPawnMoves();
        initializeKnightMoves();
        initializeDiagonal();
        initializeHorizontal();
        initializeKingMoves();
    }

    /**
     * Initializes the lookup table for white pawn attack moves from every square.
     */
    private static void initializeWhitePawnMoves() {
        for (int square = 0; square < 64; square++) {
            WHITE_PAWN_ATTACKS[square] = generateMovesForDirections(square, WHITE_PAWN_DIRECTION);
        }
    }

    /**
     * Initializes the lookup table for black pawn attack moves from every square.
     */
    private static void initializeBlackPawnMoves() {
        for (int square = 0; square < 64; square++) {
            BLACK_PAWN_ATTACKS[square] = generateMovesForDirections(square, BLACK_PAWN_DIRECTION);
        }
    }

    /**
     * Initializes the lookup table for knight moves from every square.
     */
    private static void initializeKnightMoves() {
        for (int square = 0; square < 64; square++) {
            KNIGHT_MOVES[square] = generateMovesForDirections(square, KNIGHT_DIRECTION);
        }
    }

    /**
     * Initializes the lookup table for king moves from every square.
     */
    private static void initializeKingMoves() {
        for (int square = 0; square < 64; square++) {
            KING_MOVES[square] = generateMovesForDirections(square, KING_DIRECTION);
        }
    }

    /**
     * Initializes the lookup tables for diagonal rays (bishop and queen moves) from
     * every square in all four diagonal directions.
     */
    private static void initializeDiagonal() {
        for (int square = 0; square < 64; square++) {
            DIAGONAL_RAYS[square][NE] = generateRay(square, NE, DIAGONAL_DIRECTION);
            DIAGONAL_RAYS[square][NW] = generateRay(square, NW, DIAGONAL_DIRECTION);
            DIAGONAL_RAYS[square][SE] = generateRay(square, SE, DIAGONAL_DIRECTION);
            DIAGONAL_RAYS[square][SW] = generateRay(square, SW, DIAGONAL_DIRECTION);
        }
    }

    /**
     * Initializes the lookup tables for straight rays (rook and queen moves) from
     * every square in all four straight directions.
     */
    private static void initializeHorizontal() {
        for (int square = 0; square < 64; square++) {
            STRAIGHT_RAYS[square][N] = generateRay(square, N, STRAIGHT_DIRECTION);
            STRAIGHT_RAYS[square][E] = generateRay(square, E, STRAIGHT_DIRECTION);
            STRAIGHT_RAYS[square][S] = generateRay(square, S, STRAIGHT_DIRECTION);
            STRAIGHT_RAYS[square][W] = generateRay(square, W, STRAIGHT_DIRECTION);
        }
    }

    /**
     * Generates all of the moves for a piece based in a given direction.
     * 
     * @param square     the square the moves are from
     * @param directions 2d array with the directions of each possible move
     * @return bitboard with all of the possible moves from the given square
     */
    private static long generateMovesForDirections(int square, int[][] directions) {
        int file = square % 8;
        int rank = square / 8;
        long moves = 0L;
        for (int direction = 0; direction < directions.length; direction++) {
            int toFile = file + directions[direction][0];
            int toRank = rank + directions[direction][1];
            if (toFile < 0 || toFile > 7 || toRank < 0 || toRank > 7) {
                continue;
            }
            moves |= 1L << (63 - (toRank * 8 + toFile));
        }
        return moves;
    }

    /**
     * Generates a ray of moves in a given direction.
     * 
     * @param square         the square the moves are from
     * @param direction      the direction the ray is in
     * @param directionArray 2d array with the directions of each possible move
     * @return bitboard with all of the possible moves from the given square
     */
    private static long generateRay(int square, int direction, int[][] directionArray) {
        int file = square % 8;
        int rank = square / 8;
        long ray = 0L;
        while (true) {
            file += directionArray[direction][0];
            rank += directionArray[direction][1];
            if (file < 0 || file > 7 || rank < 0 || rank > 7) {
                break;
            }
            ray |= 1L << (63 - (rank * 8 + file));
        }
        return ray;
    }
}