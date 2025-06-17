package com.github.jamesh321.chessengine;

public class BoardState {
    private long[] bitboards;
    private boolean whiteTurn;
    private int castlingRights;
    private int enPassantSquare;

    BoardState(Board board) {
        this.bitboards = board.getBitboards().clone();
        this.whiteTurn = board.isWhiteTurn();
        this.castlingRights = board.getCastlingRights();
        this.enPassantSquare = board.getEnPassantSquare();
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
}
