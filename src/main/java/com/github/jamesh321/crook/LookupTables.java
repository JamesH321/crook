package com.github.jamesh321.crook;

/**
 * Generates and stores tables for each piece that contain all the possible
 * moves from every square.
 */
public final class LookupTables {

    private LookupTables() {
        // private constructor to prevent instantiation of this utility class
    }

    /**
     * A lookup table for all the squares on the board.
     */
    public static final long[] BITBOARD_SQUARES = new long[64];
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
    public static final long[][] BISHOP_RAYS = new long[64][4];

    /**
     * A lookup table for diagonal rays with edge squares removed.
     * Used for magic bitboard attack mask generation. Edge squares are excluded
     * because they don't affect the attack pattern when occupied by blockers.
     */
    public static final long[][] BISHOP_RAYS_WITHOUT_EDGES = new long[64][4];
    /**
     * A lookup table for all possible straight rays.
     */
    public static final long[][] ROOK_RAYS = new long[64][4];

    /**
     * A lookup table for orthogonal rays with edge squares removed.
     * Used for magic bitboard attack mask generation. Edge squares are excluded
     * because they don't affect the attack pattern when occupied by blockers.
     */
    public static final long[][] ROOK_RAYS_WITHOUT_EDGES = new long[64][4];

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
        initialiseBitboardSquares();
        initialiseWhitePawnMoves();
        initialiseBlackPawnMoves();
        initialiseKnightMoves();
        initialiseBishop();
        initialiseBishopWithoutEdges();
        initialiseRook();
        initialiseRookWithoutEdges();
        initialiseKingMoves();
    }

    /**
     * Initialises the lookup table for every square on the board.
     */
    private static void initialiseBitboardSquares() {
        for (int square = 0; square < BITBOARD_SQUARES.length; square++) {
            BITBOARD_SQUARES[square] = 1L << 63 - square;
        }
    }

    /**
     * Initialises the lookup table for white pawn attack moves from every square.
     */
    private static void initialiseWhitePawnMoves() {
        for (int square = 0; square < 64; square++) {
            WHITE_PAWN_ATTACKS[square] = generateMovesForDirections(square, WHITE_PAWN_DIRECTION);
        }
    }

    /**
     * Initialises the lookup table for black pawn attack moves from every square.
     */
    private static void initialiseBlackPawnMoves() {
        for (int square = 0; square < 64; square++) {
            BLACK_PAWN_ATTACKS[square] = generateMovesForDirections(square, BLACK_PAWN_DIRECTION);
        }
    }

    /**
     * Initialises the lookup table for knight moves from every square.
     */
    private static void initialiseKnightMoves() {
        for (int square = 0; square < 64; square++) {
            KNIGHT_MOVES[square] = generateMovesForDirections(square, KNIGHT_DIRECTION);
        }
    }

    /**
     * Initialises the lookup table for king moves from every square.
     */
    private static void initialiseKingMoves() {
        for (int square = 0; square < 64; square++) {
            KING_MOVES[square] = generateMovesForDirections(square, KING_DIRECTION);
        }
    }

    /**
     * Initialises the lookup tables for diagonal rays (bishop and queen moves) from
     * every square in all four diagonal directions.
     */
    private static void initialiseBishop() {
        initialiseRays(BISHOP_RAYS, new int[] { NE, NW, SE, SW }, DIAGONAL_DIRECTION);
    }

    /**
     * Initialises the lookup tables for diagonal rays with edge squares removed.
     * This is used for magic bitboard attack mask generation.
     */
    private static void initialiseBishopWithoutEdges() {
        for (int square = 0; square < 64; square++) {
            BISHOP_RAYS_WITHOUT_EDGES[square][NE] = getBishopRayWithoutEdges(
                    generateRay(square, NE, DIAGONAL_DIRECTION));
            BISHOP_RAYS_WITHOUT_EDGES[square][NW] = getBishopRayWithoutEdges(
                    generateRay(square, NW, DIAGONAL_DIRECTION));
            BISHOP_RAYS_WITHOUT_EDGES[square][SE] = getBishopRayWithoutEdges(
                    generateRay(square, SE, DIAGONAL_DIRECTION));
            BISHOP_RAYS_WITHOUT_EDGES[square][SW] = getBishopRayWithoutEdges(
                    generateRay(square, SW, DIAGONAL_DIRECTION));
        }
    }

    /**
     * Initialises the lookup tables for straight rays (rook and queen moves) from
     * every square in all four straight directions.
     */
    private static void initialiseRook() {
        initialiseRays(ROOK_RAYS, new int[] { N, E, S, W }, STRAIGHT_DIRECTION);
    }

    /**
     * Initialises the lookup tables for straight rays with edge squares removed.
     * This is used for magic bitboard attack mask generation.
     */
    private static void initialiseRookWithoutEdges() {
        for (int square = 0; square < 64; square++) {
            ROOK_RAYS_WITHOUT_EDGES[square][N] = getRookRayWithoutEdges(square, N,
                    generateRay(square, N, STRAIGHT_DIRECTION));
            ROOK_RAYS_WITHOUT_EDGES[square][E] = getRookRayWithoutEdges(square, E,
                    generateRay(square, E, STRAIGHT_DIRECTION));
            ROOK_RAYS_WITHOUT_EDGES[square][S] = getRookRayWithoutEdges(square, S,
                    generateRay(square, S, STRAIGHT_DIRECTION));
            ROOK_RAYS_WITHOUT_EDGES[square][W] = getRookRayWithoutEdges(square, W,
                    generateRay(square, W, STRAIGHT_DIRECTION));
        }
    }

    private static void initialiseRays(long[][] raysToInitialise, int[] directions, int[][] directionArray) {
        for (int square = 0; square < 64; square++) {
            raysToInitialise[square] = new long[directions.length];
            for (int i = 0; i < directions.length; i++) {
                raysToInitialise[square][i] = generateRay(square, directions[i], directionArray);
            }
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
        for (int[] direction : directions) {
            int toFile = file + direction[0];
            int toRank = rank + direction[1];
            if (toFile < 0 || toFile > 7 || toRank < 0 || toRank > 7) {
                continue;
            }
            moves |= BITBOARD_SQUARES[toRank * 8 + toFile];
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
            ray |= BITBOARD_SQUARES[rank * 8 + file];
        }

        return ray;
    }

    /**
     * Removes edge squares from a bishop ray for magic bitboard generation.
     * Edge squares don't affect attack patterns when occupied by blockers,
     * so they can be excluded from the attack mask.
     *
     * @param ray The original bishop ray.
     * @return The ray with edge squares removed.
     */
    private static long getBishopRayWithoutEdges(long ray) {
        long noEdgeMask = 0x7E7E7E7E7E7E00L;

        return ray & noEdgeMask;
    }

    /**
     * Removes the edge square from a rook ray for magic bitboard generation.
     * For rook rays, only the end square of each ray needs to be removed
     * since it doesn't affect the attack pattern when occupied.
     *
     * @param square    The starting square of the ray.
     * @param direction The direction of the ray.
     * @param ray       The original rook ray.
     * @return The ray with the edge square removed.
     */
    private static long getRookRayWithoutEdges(int square, int direction, long ray) {
        long rayWithoutEdges = ray;

        int squareToRemove = -1;
        squareToRemove = switch (direction) {
            case LookupTables.N -> (square % 8);
            case LookupTables.E -> square + (7 - (square % 8));
            case LookupTables.S -> 56 + (square % 8);
            case LookupTables.W -> square - (square % 8);
            default -> squareToRemove;
        };

        rayWithoutEdges &= ~LookupTables.BITBOARD_SQUARES[squareToRemove];

        return rayWithoutEdges;
    }
}