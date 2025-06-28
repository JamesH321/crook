package com.github.jamesh321.chessengine;

public class BoardState {
    private long[] bitboards;
    private boolean whiteTurn;
    private int castlingRights;
    private int enPassantSquare;
    private int halfmoveClock;
    private int fullmoveCounter;

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