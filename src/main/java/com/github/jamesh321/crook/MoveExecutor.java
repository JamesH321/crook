package com.github.jamesh321.crook;

/**
 * Provides static methods to execute chess moves on a {@link Board} object.
 * <p>
 * The MoveExecutor class handles all aspects of move execution, including:
 * <ul>
 * <li>Moving pieces between squares</li>
 * <li>Capturing pieces</li>
 * <li>Handling special moves such as castling, en passant, and promotions</li>
 * <li>Updating castling rights, en passant squares, halfmove clock, and
 * fullmove counter</li>
 * <li>Maintaining board state consistency after each move</li>
 * </ul>
 * All methods are static and operate directly on the provided {@link Board}
 * instance.
 */
public final class MoveExecutor {

    private MoveExecutor() {
        // private constructor to prevent instantiation of this utility class
    }

    /**
     * Executes the provided move. Handles special moves and updates game rules.
     *
     * @param board the board on which to make the move
     * @param move  the move to execute
     */
    public static void makeMove(Board board, Move move) {
        int from = move.getFrom();
        int to = move.getTo();
        long fromMask = LookupTables.BITBOARD_SQUARES[from];
        long toMask = LookupTables.BITBOARD_SQUARES[to];
        Piece fromPiece = board.getPieceAtSquare(from);
        Piece toPiece = board.getPieceAtSquare(to);

        switch (move.getSpecialMove()) {
            case Move.NORMAL:
                movePiece(board, fromPiece, fromMask, toMask);
                takePiece(board, toPiece, toMask);
                break;
            case Move.QUEEN_PROMOTION: // Handles all promotions, not just queen.
                Piece promotionPiece = getPromotionPiece(move.getPromotionPiece(), board.isWhiteTurn());
                movePiece(board, promotionPiece, fromMask, toMask);
                takePiece(board, toPiece, toMask);
                takePiece(board, fromPiece, fromMask);
                break;
            case Move.EN_PASSANT:
                movePiece(board, fromPiece, fromMask, toMask);
                takeEnPassantPiece(board, toMask);
                break;
            case Move.CASTLE:
                castle(board, to, fromPiece, fromMask, toMask);
                break;
            default:
                break;
        }

        setCastlingRights(board, fromPiece, toPiece, from, to);
        setEnPassantSquare(board, fromPiece, from, to);
        setHalfmoveClock(board, fromPiece, toPiece);
        incrementFullmoveCounter(board);
        board.updateCompositeBitboards();
        board.setWhiteTurn(!board.isWhiteTurn());
    }

    /**
     * Adds the piece to the bitboard on the square it moves to and removes it from
     * the square it was on.
     *
     * @param board     the board on which to make the move
     * @param fromPiece the piece being moved
     * @param fromMask  the bitboard mask for the square the piece was on
     * @param toMask    the bitboard mask for the square the piece is moving to
     */
    public static void movePiece(Board board, Piece fromPiece, long fromMask, long toMask) {
        long newBitboard = (board.getBitboard(fromPiece) & ~fromMask) | toMask;
        board.setBitboard(fromPiece, newBitboard);
    }

    /**
     * Removes a piece from its bitboard if the piece being moved lands on its
     * square.
     *
     * @param board   the board on which the piece is being captured
     * @param toPiece the piece being captured
     * @param toMask  the bitboard mask for the piece being captured
     */
    public static void takePiece(Board board, Piece toPiece, long toMask) {
        if (toPiece != null) {
            long newBitboard = board.getBitboard(toPiece) & ~toMask;
            board.setBitboard(toPiece, newBitboard);
        }
    }

    /**
     * Gets the index of the piece to which a pawn is being promoted.
     *
     * @param promotionPiece the piece specified in the move to promote to
     * @param isWhiteTurn    true if it is white's turn, false if it is black's
     * @return the piece the pawn is being promoted to
     */
    public static Piece getPromotionPiece(int promotionPiece, boolean isWhiteTurn) {
        int pieceIndex = isWhiteTurn ? 4 - promotionPiece : 10 - promotionPiece;

        return Piece.fromIndex(pieceIndex);

    }

    /**
     * Removes a pawn from its bitboard when performing an en passant capture.
     *
     * @param board  the board on which the pawn is being captured
     * @param toMask the bitboard mask for the piece being captured
     */
    public static void takeEnPassantPiece(Board board, long toMask) {
        Piece toPiece;

        if (board.isWhiteTurn()) {
            toPiece = Piece.BLACK_PAWN;
            toMask >>>= 8;
        } else {
            toPiece = Piece.WHITE_PAWN;
            toMask <<= 8;
        }

        takePiece(board, toPiece, toMask);
    }

    /**
     * Moves the king and rook to the correct positions when a castling move is
     * made.
     *
     * @param board     the board on which castling is performed
     * @param to        the square to which the king is moving
     * @param fromPiece the king piece
     * @param fromMask  the bitboard mask for the square the king is moving from
     * @param toMask    the bitboard mask for the square the king is moving to
     */
    public static void castle(Board board, int to, Piece fromPiece, long fromMask, long toMask) {
        Piece rook = board.isWhiteTurn() ? Piece.WHITE_ROOK : Piece.BLACK_ROOK;

        movePiece(board, fromPiece, fromMask, toMask);

        if (to == 58 || to == 2) {
            movePiece(board, rook, fromMask << 4, toMask >>> 1);
        } else {
            movePiece(board, rook, fromMask >>> 3, toMask << 1);
        }

        if (board.isWhiteTurn()) {
            board.setCastlingRights(board.getCastlingRights() & 0b1100);
        } else {
            board.setCastlingRights(board.getCastlingRights() & 0b11);
        }
    }

    /**
     * Sets the en passant square if a double pawn push is made.
     *
     * @param board     the board on which the en passant square is set
     * @param fromPiece the piece being moved
     * @param from      the square from which the piece is being moved
     * @param to        the square to which the piece is being moved
     */
    public static void setEnPassantSquare(Board board, Piece fromPiece, int from, int to) {
        int enPassantSquare = -1;

        if (fromPiece == Piece.WHITE_PAWN && from - to == 16) {
            enPassantSquare = to + 8;
        } else if (fromPiece == Piece.BLACK_PAWN && from - to == -16) {
            enPassantSquare = to - 8;
        }

        board.setEnPassantSquare(enPassantSquare);
    }

    /**
     * Updates castling rights if the king or rook is moved or captured.
     *
     * @param board     the board on which castling rights are set
     * @param fromPiece the piece being moved
     * @param toPiece   the piece on the destination square
     * @param from      the square from which the move is made
     * @param to        the square to which the move is made
     */
    public static void setCastlingRights(Board board, Piece fromPiece, Piece toPiece, int from, int to) {
        int castlingRights = board.getCastlingRights();

        if (fromPiece != null) {
            switch (fromPiece) {
                case WHITE_ROOK:
                    if (from == 56) {
                        castlingRights &= 0b1101;
                    } else if (from == 63) {
                        castlingRights &= 0b1110;
                    }
                    break;
                case BLACK_ROOK:
                    if (from == 0) {
                        castlingRights &= 0b0111;
                    } else if (from == 7) {
                        castlingRights &= 0b1011;
                    }
                    break;
                case WHITE_KING:
                    castlingRights &= 0b1100;
                    break;
                case BLACK_KING:
                    castlingRights &= 0b0011;
                    break;
                default:
                    break;
            }
        }

        if (toPiece != null) {
            switch (toPiece) {
                case WHITE_ROOK:
                    if (to == 56) {
                        castlingRights &= 0b1101;
                    } else if (to == 63) {
                        castlingRights &= 0b1110;
                    }
                    break;
                case BLACK_ROOK:
                    if (to == 0) {
                        castlingRights &= 0b0111;
                    } else if (to == 7) {
                        castlingRights &= 0b1011;
                    }
                    break;
                default:
                    break;
            }
        }

        board.setCastlingRights(castlingRights);
    }

    /**
     * Increments the halfmove clock whenever a pawn move or capture is not made.
     * Resets it to 0 whenever a pawn move or capture is made.
     *
     * @param board     the board on which the halfmove clock is set
     * @param fromPiece the piece being moved
     * @param toPiece   the piece on the destination square
     */
    public static void setHalfmoveClock(Board board, Piece fromPiece, Piece toPiece) {
        if (fromPiece == Piece.WHITE_PAWN || fromPiece == Piece.BLACK_PAWN || toPiece != null) {
            board.setHalfmoveClock(0);
        } else {
            board.setHalfmoveClock(board.getHalfmoveClock() + 1);
        }
    }

    /**
     * Increments the fullmove counter after black's move.
     *
     * @param board the board on which the fullmove counter is set
     */
    public static void incrementFullmoveCounter(Board board) {
        if (!board.isWhiteTurn()) {
            board.setFullmoveNumber(board.getFullmoveNumber() + 1);
        }
    }
}