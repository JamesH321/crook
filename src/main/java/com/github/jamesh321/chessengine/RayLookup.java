package com.github.jamesh321.chessengine;

public class RayLookup {
    public static final long[][] DIAGONAL_RAYS = new long[64][4];
    public static final long[][] DIAGONAL_MASKS = new long[64][4];

    public static final long[][] STRAIGHT_RAYS = new long[64][4];
    public static final long[][] STRAIGHT_MASKS = new long[64][4];

    public static final int NE = 0;
    public static final int NW = 1;
    public static final int SE = 2;
    public static final int SW = 3;

    public static final int N = 0;
    public static final int E = 1;
    public static final int S = 2;
    public static final int W = 3;

    private static final int[][] DIAGONAL_DIRECTION = { { 1, -1 }, { -1, -1 }, { 1, 1 }, { -1, 1 } }; // File, rank
    private static final int[][] STRAIGHT_DIRECTION = { { 0, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 } }; // File, rank

    static {
        initializeDiagonal();
        initializeHorizontal();
    }

    private static void initializeDiagonal() {
        for (int square = 0; square < 64; square++) {
            DIAGONAL_RAYS[square][NE] = generateDiagonalRay(square, NE);
            DIAGONAL_RAYS[square][NW] = generateDiagonalRay(square, NW);
            DIAGONAL_RAYS[square][SE] = generateDiagonalRay(square, SE);
            DIAGONAL_RAYS[square][SW] = generateDiagonalRay(square, SW);

            DIAGONAL_MASKS[square][NE] = ~DIAGONAL_RAYS[square][NE];
            DIAGONAL_MASKS[square][NW] = ~DIAGONAL_RAYS[square][NW];
            DIAGONAL_MASKS[square][SE] = ~DIAGONAL_RAYS[square][SE];
            DIAGONAL_MASKS[square][SW] = ~DIAGONAL_RAYS[square][SW];
        }
    }

    private static long generateDiagonalRay(int square, int direction) {
        int file = square % 8;
        int rank = square / 8;
        long ray = 0L;
        while (true) {
            file += DIAGONAL_DIRECTION[direction][0];
            rank += DIAGONAL_DIRECTION[direction][1];
            if (file < 0 || file > 7 || rank < 0 || rank > 7) {
                break;
            }
            ray |= 1L << (63 - (rank * 8 + file));
        }
        return ray;
    }

    private static void initializeHorizontal() {
        for (int square = 0; square < 64; square++) {
            STRAIGHT_RAYS[square][N] = generateStraightRay(square, N);
            STRAIGHT_RAYS[square][E] = generateStraightRay(square, E);
            STRAIGHT_RAYS[square][S] = generateStraightRay(square, S);
            STRAIGHT_RAYS[square][W] = generateStraightRay(square, W);

            STRAIGHT_MASKS[square][N] = ~STRAIGHT_RAYS[square][N];
            STRAIGHT_MASKS[square][E] = ~STRAIGHT_RAYS[square][E];
            STRAIGHT_MASKS[square][S] = ~STRAIGHT_RAYS[square][S];
            STRAIGHT_MASKS[square][W] = ~STRAIGHT_RAYS[square][W];
        }
    }

    private static long generateStraightRay(int square, int direction) {
        int file = square % 8;
        int rank = square / 8;
        long ray = 0L;
        while (true) {
            file += STRAIGHT_DIRECTION[direction][0];
            rank += STRAIGHT_DIRECTION[direction][1];
            if (file < 0 || file > 7 || rank < 0 || rank > 7) {
                break;
            }
            ray |= 1L << (63 - (rank * 8 + file));
        }
        return ray;
    }
}