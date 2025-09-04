package com.github.jamesh321.crook;

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

        bitboards[Piece.WHITE_PAWN.getIndex()] = 0x000000000000FF00L;
        bitboards[Piece.WHITE_KNIGHT.getIndex()] = 0x0000000000000042L;
        bitboards[Piece.WHITE_BISHOP.getIndex()] = 0x0000000000000024L;
        bitboards[Piece.WHITE_ROOK.getIndex()] = 0x0000000000000081L;
        bitboards[Piece.WHITE_QUEEN.getIndex()] = 0x0000000000000010L;
        bitboards[Piece.WHITE_KING.getIndex()] = 0x0000000000000008L;

        bitboards[Piece.BLACK_PAWN.getIndex()] = 0x00FF000000000000L;
        bitboards[Piece.BLACK_KNIGHT.getIndex()] = 0x4200000000000000L;
        bitboards[Piece.BLACK_BISHOP.getIndex()] = 0x2400000000000000L;
        bitboards[Piece.BLACK_ROOK.getIndex()] = 0x8100000000000000L;
        bitboards[Piece.BLACK_QUEEN.getIndex()] = 0x1000000000000000L;
        bitboards[Piece.BLACK_KING.getIndex()] = 0x0800000000000000L;

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

    /**
     * Gets the bitboard for a specific piece.
     * 
     * @param piece the piece to get the bitboard for
     * @return a long representing the bitboard for the piece
     */
    public long getBitboard(Piece piece) {
        return bitboards[piece.getIndex()];
    }

    /**
     * Sets the bitboard for a specific piece.
     * 
     * @param piece    the piece to set the bitboard for
     * @param bitboard a long representing the bitboard for the piece
     */
    public void setBitboard(Piece piece, long bitboard) {
        this.bitboards[piece.getIndex()] = bitboard;
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
     * @return the piece at that square, or null if the square is empty
     */
    public Piece getPieceAtSquare(int square) {
        long boardSquare = LookupTables.BITBOARD_SQUARES[square];
        for (Piece piece : Piece.values()) {
            if ((boardSquare & bitboards[piece.getIndex()]) != 0) {
                return piece;
            }
        }
        // Empty square
        return null;
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