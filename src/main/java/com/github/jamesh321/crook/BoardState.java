package com.github.jamesh321.crook;

/**
 * Represents the state of a board.
 * Used to save a board position so that it can be restored later when undoing
 * moves.
 * Doesn't save unnecessary information from a board like composite bitboards.
 */
public class BoardState {
    private final long[] bitboards;
    private final boolean whiteTurn;
    private final int castlingRights;
    private final int enPassantSquare;
    private final int halfmoveClock;
    private final int fullmoveCounter;

    /**
     * Copies the essential information from a board to save a position.
     * 
     * @param board the board to copy the state from
     */
    BoardState(Board board) {
        this.bitboards = board.getBitboards().clone();
        this.whiteTurn = board.isWhiteTurn();
        this.castlingRights = board.getCastlingRights();
        this.enPassantSquare = board.getEnPassantSquare();
        this.halfmoveClock = board.getHalfmoveClock();
        this.fullmoveCounter = board.getFullmoveNumber();
    }

    public long[] getBitboards() {
        return bitboards;
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public int getCastlingRights() {
        return castlingRights;
    }

    public int getEnPassantSquare() {
        return enPassantSquare;
    }

    public int getHalfmoveClock() {
        return halfmoveClock;
    }

    public int getFullmoveCounter() {
        return fullmoveCounter;
    }
}