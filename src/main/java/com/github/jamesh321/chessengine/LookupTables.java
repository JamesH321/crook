package com.github.jamesh321.chessengine;

public class LookupTables {
    public static final long[][] DIAGONAL_RAYS = new long[64][4];
    public static final long[][] STRAIGHT_RAYS = new long[64][4];
    public static final long[] KNIGHT_MOVES = new long[64];

    public static final int N = 0;
    public static final int E = 1;
    public static final int S = 2;
    public static final int W = 3;

    public static final int NE = 0;
    public static final int NW = 1;
    public static final int SE = 2;
    public static final int SW = 3;

    // File, rank
    private static final int[][] DIAGONAL_DIRECTION = { { 1, -1 }, { -1, -1 }, { 1, 1 }, { -1, 1 } };
    private static final int[][] STRAIGHT_DIRECTION = { { 0, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 } };
    private static final int[][] KNIGHT_DIRECTION = { { 2, 1 }, { 1, 2 }, { -1, 2 }, { -2, 1 }, { -2, -1 }, { -1, -2 },
            { 1, -2 }, { 2, -1 } };

    static {
        initializeDiagonal();
        initializeHorizontal();
        initializeKnightAttacks();
    }

    private static void initializeKnightAttacks() {
        for (int square = 0; square < 64; square++) {
            int file = square % 8;
            int rank = square / 8;
            long moves = 0L;
            for (int direction = 0; direction < KNIGHT_DIRECTION.length; direction++) {
                int toFile = file + KNIGHT_DIRECTION[direction][0];
                int toRank = rank + KNIGHT_DIRECTION[direction][1];
                if (toFile < 0 || toFile > 7 || toRank < 0 || toRank > 7) {
                    continue;
                }
                moves |= 1L << (63 - (toRank * 8 + toFile));
            }
            KNIGHT_MOVES[square] = moves;
        }
    }

    private static void initializeDiagonal() {
        for (int square = 0; square < 64; square++) {
            DIAGONAL_RAYS[square][NE] = generateDiagonalRay(square, NE);
            DIAGONAL_RAYS[square][NW] = generateDiagonalRay(square, NW);
            DIAGONAL_RAYS[square][SE] = generateDiagonalRay(square, SE);
            DIAGONAL_RAYS[square][SW] = generateDiagonalRay(square, SW);
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

    public static void main(String[] args) {
        System.out.println(Long.toBinaryString(KNIGHT_MOVES[7]));
    }
}