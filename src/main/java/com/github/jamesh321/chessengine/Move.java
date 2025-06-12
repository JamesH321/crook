package com.github.jamesh321.chessengine;

public class Move {
    private static final int TO_FROM_MASK = 0b111111;
    private static final int FLAG_MASK = 0b1111;

    public static int generateMove(int from, int to, int flags) {
        return from | (to << 6) | (flags << 12);
    }

    public static int getFrom(int move) {
        return move & TO_FROM_MASK;
    }

    public static int getTo(int move) {
        return (move >>> 6) & TO_FROM_MASK;
    }

    public static int getFlag(int move) {
        return (move >>> 12) & FLAG_MASK;
    }

    public static int getPromotionPiece(int move) {
        return getFlag(move) & 0b0011;
    }

    public static int getSpecialMove(int move) {
        return getFlag(move) & 0b1100;
    }
}
