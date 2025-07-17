package com.github.jamesh321.chessengine;

public enum Piece {
    WHITE_PAWN(0),
    WHITE_KNIGHT(1),
    WHITE_BISHOP(2),
    WHITE_ROOK(3),
    WHITE_QUEEN(4),
    WHITE_KING(5),
    BLACK_PAWN(6),
    BLACK_KNIGHT(7),
    BLACK_BISHOP(8),
    BLACK_ROOK(9),
    BLACK_QUEEN(10),
    BLACK_KING(11);

    private final int index;

    Piece(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }
}