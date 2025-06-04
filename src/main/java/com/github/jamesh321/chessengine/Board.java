package com.github.jamesh321.chessengine;

public class Board {
    long whitePawns;
    long whiteKnights;
    long whiteBishops;
    long whiteRooks;
    long whiteQueens;
    long whiteKings;
    long blackPawns;
    long blackKnights;
    long blackBishops;
    long blackRooks;
    long blackQueens;
    long blackKings;

    boolean whiteTurn;
    int castlingRights;
    int enPassantSquare;

    public Board() {
        whitePawns = 0x000000000000FF00L;
        whiteKnights = 0x0000000000000042L;
        whiteBishops = 0x0000000000000024L;
        whiteRooks = 0x0000000000000081L;
        whiteQueens = 0x0000000000000010L;
        whiteKings = 0x0000000000000008L;

        blackPawns = 0x00FF000000000000L;
        blackKnights = 0x4200000000000000L;
        blackBishops = 0x2400000000000000L;
        blackRooks = 0x8100000000000000L;
        blackQueens = 0x1000000000000000L;
        blackKings = 0x0800000000000000L;

        whiteTurn = true;
        castlingRights = 0b1111; // BQ, BK, WQ, WK
        enPassantSquare = -1;
    }

    public long getWhitePawns() {
        return whitePawns;
    }

    public void setWhitePawns(long whitePawns) {
        this.whitePawns = whitePawns;
    }

    public long getWhiteKnights() {
        return whiteKnights;
    }

    public void setWhiteKnights(long whiteKnights) {
        this.whiteKnights = whiteKnights;
    }

    public long getWhiteBishops() {
        return whiteBishops;
    }

    public void setWhiteBishops(long whiteBishops) {
        this.whiteBishops = whiteBishops;
    }

    public long getWhiteRooks() {
        return whiteRooks;
    }

    public void setWhiteRooks(long whiteRooks) {
        this.whiteRooks = whiteRooks;
    }

    public long getWhiteQueens() {
        return whiteQueens;
    }

    public void setWhiteQueens(long whiteQueens) {
        this.whiteQueens = whiteQueens;
    }

    public long getWhiteKings() {
        return whiteKings;
    }

    public void setWhiteKings(long whiteKings) {
        this.whiteKings = whiteKings;
    }

    public long getBlackPawns() {
        return blackPawns;
    }

    public void setBlackPawns(long blackPawns) {
        this.blackPawns = blackPawns;
    }

    public long getBlackKnights() {
        return blackKnights;
    }

    public void setBlackKnights(long blackKnights) {
        this.blackKnights = blackKnights;
    }

    public long getBlackBishops() {
        return blackBishops;
    }

    public void setBlackBishops(long blackBishops) {
        this.blackBishops = blackBishops;
    }

    public long getBlackRooks() {
        return blackRooks;
    }

    public void setBlackRooks(long blackRooks) {
        this.blackRooks = blackRooks;
    }

    public long getBlackQueens() {
        return blackQueens;
    }

    public void setBlackQueens(long blackQueens) {
        this.blackQueens = blackQueens;
    }

    public long getBlackKings() {
        return blackKings;
    }

    public void setBlackKings(long blackKings) {
        this.blackKings = blackKings;
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public void setWhiteTurn(boolean whiteTurn) {
        this.whiteTurn = whiteTurn;
    }

    public int getCastlingRights() {
        return castlingRights;
    }

    public void setCastlingRights(int castlingRights) {
        this.castlingRights = castlingRights;
    }

    public int getEnPassantSquare() {
        return enPassantSquare;
    }

    public void setEnPassantSquare(int enPassantSquare) {
        this.enPassantSquare = enPassantSquare;
    }

    public long[] getAllPieces() {
        return new long[] {
                whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKings,
                blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKings
        };
    }

    public char getPieceAtSquare(int square) {
        
    }
