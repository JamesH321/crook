package com.github.jamesh321.chessengine;

public class Board {
    private long[] bitboards = new long[12]; // 0-5 for white pieces, 6-11 for black pieces

    private boolean whiteTurn;
    private int castlingRights;
    private int enPassantSquare;

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
        enPassantSquare = -1;
    }

    public long[] getBitboards() {
        return bitboards;
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

    public void movePiece(int fromSquare, int toSquare) {
        long fromBit = 0x8000000000000000L >>> fromSquare;
        long toBit = 0x8000000000000000L >>> toSquare;
        int fromPiece = getPieceAtSquare(fromSquare);
        int toPiece = getPieceAtSquare(toSquare);
        bitboards[fromPiece] = (bitboards[fromPiece] & ~fromBit) | toBit;
        if (toPiece > -1) {
            bitboards[toPiece] &= ~toBit;
        }
    }

}