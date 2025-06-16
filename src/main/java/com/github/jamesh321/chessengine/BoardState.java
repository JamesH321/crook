package com.github.jamesh321.chessengine;

public class BoardState {
    long[] bitboards;
    boolean whiteTurn;
    int castlingRights;
    int enPassantSquare;

    BoardState(Board board) {
        this.bitboards = board.getBitboards().clone();
        this.whiteTurn = board.isWhiteTurn();
        this.castlingRights = board.getCastlingRights();
        this.enPassantSquare = board.getEnPassantSquare();
    }
}
