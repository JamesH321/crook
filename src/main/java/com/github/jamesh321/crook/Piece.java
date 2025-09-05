package com.github.jamesh321.crook;

/**
 * Represents a chess piece with its colour and type.
 */
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

    /**
     * Gets the index of the piece in the array.
     * 
     * @return the index of the piece
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Gets the piece from its index in the array.
     * 
     * @param index the index of the piece
     * @return the piece
     */
    public static Piece fromIndex(int index) {
        for (Piece piece : Piece.values()) {
            if (piece.getIndex() == index) {
                return piece;
            }
        }
        throw new IllegalArgumentException("No piece found with value: " + index);
    }
}