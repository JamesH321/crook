package com.github.jamesh321.chessengine;

public class Board {
    private long[] bitboards = new long[12];

    private boolean whiteTurn;
    private int castlingRights;
    private int enPassantSquare;
    private int halfmoveClock;
    private int fullmoveCounter;

    private long whitePieces;
    private long blackPieces;
    private long occupiedSquares;
    private long emptySquares;

    public Board() {
        // White pieces (indices 0-5)
        bitboards[0] = 0x000000000000FF00L; // Pawns
        bitboards[1] = 0x0000000000000042L; // Knights
        bitboards[2] = 0x0000000000000024L; // Bishops
        bitboards[3] = 0x0000000000000081L; // Rooks
        bitboards[4] = 0x0000000000000010L; // Queens
        bitboards[5] = 0x0000000000000008L; // Kings

        // Black pieces (indices 6-11)
        bitboards[6] = 0x00FF000000000000L; // Pawns
        bitboards[7] = 0x4200000000000000L; // Knights
        bitboards[8] = 0x2400000000000000L; // Bishops
        bitboards[9] = 0x8100000000000000L; // Rooks
        bitboards[10] = 0x1000000000000000L; // Queens
        bitboards[11] = 0x0800000000000000L; // Kings

        whiteTurn = true;
        castlingRights = 0b1111; // BQ, BK, WQ, WK
        enPassantSquare = -1; // 0-63
        halfmoveClock = 0;
        fullmoveCounter = 1;

        updateCompositeBitboards();
    }

    public long[] getBitboards() {
        return bitboards;
    }

    public void setBitboards(long[] bitboards) {
        this.bitboards = bitboards;
    }

    public long getBitboard(int piece) {
        return bitboards[piece];
    }

    public void setBitboard(int piece, long bitboard) {
        this.bitboards[piece] = bitboard;
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

    public int getHalfmoveClock() {
        return halfmoveClock;
    }

    public void setHalfmoveClock(int halfmoveClock) {
        this.halfmoveClock = halfmoveClock;
    }

    public int getFullmoveNumber() {
        return fullmoveCounter;
    }

    public void setFullmoveNumber(int fullmoveCounter) {
        this.fullmoveCounter = fullmoveCounter;
    }

    public long getWhitePieces() {
        return whitePieces;
    }

    public void updateWhitePieces() {
        this.whitePieces = 0L;
        for (int i = 0; i < 6; i++) {
            this.whitePieces |= bitboards[i];
        }
    }

    public void updateBlackPieces() {
        this.blackPieces = 0L;
        for (int i = 6; i < 12; i++) {
            this.blackPieces |= bitboards[i];
        }
    }

    public long getBlackPieces() {
        return blackPieces;
    }

    public void updateOccupiedSquares() {
        this.occupiedSquares = 0L;
        for (int i = 0; i < 12; i++) {
            this.occupiedSquares |= bitboards[i];
        }
    }

    public long getOccupiedSquares() {
        return occupiedSquares;
    }

    public void updateEmptySquares() {
        this.emptySquares = ~occupiedSquares;
    }

    public long getEmptySquares() {
        return emptySquares;
    }

    public void updateCompositeBitboards() {
        updateWhitePieces();
        updateBlackPieces();
        updateOccupiedSquares();
        updateEmptySquares();
    }

    public int getPieceAtSquare(int square) {
        long pieceBit = 0x8000000000000000L >>> square;
        for (int i = 0; i < 12; i++) {
            if ((pieceBit & bitboards[i]) != 0) {
                return i;
            }
        }
        // Empty square
        return -1;
    }

    public void restoreState(BoardState previousState) {
        this.bitboards = previousState.getBitboards().clone();
        this.whiteTurn = previousState.isWhiteTurn();
        this.castlingRights = previousState.getCastlingRights();
        this.enPassantSquare = previousState.getEnPassantSquare();
        this.halfmoveClock = previousState.getFullmoveCounter();
        this.fullmoveCounter = previousState.getFullmoveCounter();
        updateCompositeBitboards();
    }
}