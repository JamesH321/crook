package com.github.jamesh321.chessengine;

public class Move {
    private int data;

    private final int TO_FROM_MASK = 0b111111;
    private final int FLAG_MASK = 0b1111;

    public Move(int from, int to, int flag) {
        this.data = from | (to << 6) | (flag << 12);
    }

    public int getFrom() {
        return data & TO_FROM_MASK;
    }

    public int getTo() {
        return (data >>> 6) & TO_FROM_MASK;
    }

    public int getFlag() {
        return (data >>> 12) & FLAG_MASK;
    }

    /*
     * 0 - Queen
     * 1 - Rook
     * 2 - Bishop
     * 3 - Knight
     */
    public int getPromotionPiece() {
        return (getFlag() >> 2) & 0b0011;
    }

    /*
     * 0 - None
     * 1 - Piece promotion
     * 2 - En passant
     * 3 - Castle
     */
    public int getSpecialMove() {
        return getFlag() & 0b0011;
    }
}
