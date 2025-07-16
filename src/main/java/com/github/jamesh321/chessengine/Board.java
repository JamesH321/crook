package com.github.jamesh321.chessengine;

/**
 * Represents a chess board and manages all game rules and state.
 * Uses bitboards for efficient piece representation and provides methods
 * for updating and querying the board state.
 */
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

    /**
     * Initialises the board and game rules to the standard starting position.
     */
    public Board() {
        // White pieces
        bitboards[0] = 0x000000000000FF00L; // Pawns
        bitboards[1] = 0x0000000000000042L; // Knights
        bitboards[2] = 0x0000000000000024L; // Bishops
        bitboards[3] = 0x0000000000000081L; // Rooks
        bitboards[4] = 0x0000000000000010L; // Queens
        bitboards[5] = 0x0000000000000008L; // Kings

        // Black pieces
        bitboards[6] = 0x00FF000000000000L; // Pawns
        bitboards[7] = 0x4200000000000000L; // Knights
        bitboards[8] = 0x2400000000000000L; // Bishops
        bitboards[9] = 0x8100000000000000L; // Rooks
        bitboards[10] = 0x1000000000000000L; // Queens
        bitboards[11] = 0x0800000000000000L; // Kings

        whiteTurn = true;
        castlingRights = 0b1111; // BQ, BK, WQ, WK
        enPassantSquare = -1; // 0-63 or -1 for no square
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

    /**
     * Updates the bitboard representing all white pieces on the board.
     */
    public void updateWhitePieces() {
        this.whitePieces = 0L;
        for (int i = 0; i < 6; i++) {
            this.whitePieces |= bitboards[i];
        }
    }

    /**
     * Updates the bitboard representing all black pieces on the board.
     */
    public void updateBlackPieces() {
        this.blackPieces = 0L;
        for (int i = 6; i < 12; i++) {
            this.blackPieces |= bitboards[i];
        }
    }

    public long getBlackPieces() {
        return blackPieces;
    }

    /**
     * Updates the bitboard representing all occupied squares on the board.
     */
    public void updateOccupiedSquares() {
        this.occupiedSquares = 0L;
        for (int i = 0; i < 12; i++) {
            this.occupiedSquares |= bitboards[i];
        }
    }

    public long getOccupiedSquares() {
        return occupiedSquares;
    }

    public void setOccupiedSquares(long occupiedSquares) {
        this.occupiedSquares = occupiedSquares;
    }

    /**
     * Updates the bitboard representing all empty squares on the board.
     */
    public void updateEmptySquares() {
        this.emptySquares = ~occupiedSquares;
    }

    public long getEmptySquares() {
        return emptySquares;
    }

    /**
     * Updates the bitboards for white pieces, black pieces, occupied squares and
     * empty squares.
     */
    public void updateCompositeBitboards() {
        updateWhitePieces();
        updateBlackPieces();
        updateOccupiedSquares();
        updateEmptySquares();
    }

    /**
     * Gets the piece that is on the specified square.
     *
     * @param square the square on the board (0-63)
     * @return the index in the bitboards array of the piece at that square, or -1
     *         if the square is empty
     */
    public int getPieceAtSquare(int square) {
        long piecePosition = 1L << 63 - square;
        for (int i = 0; i < 12; i++) {
            if ((piecePosition & bitboards[i]) != 0) {
                return i;
            }
        }
        // Empty square
        return -1;
    }

    /**
     * Reverts the board to a previous state and updates all composite bitboards.
     *
     * @param previousState the board state that is being reverted to
     */
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