package com.github.jamesh321.crook;

import java.util.Arrays;
import java.util.List;

/**
 * Provides functionality to load a board position from an FEN (Forsyth-Edwards
 * Notation) string.
 * <p>
 * FEN is a standard notation for describing a particular board position of a
 * chess game.
 * A FEN string consists of six fields separated by spaces:
 * <ul>
 * <li><b>Piece placement</b>: Describes the position of all pieces on the
 * board, from rank 8 to rank 1. Numbers are used to represent the number of
 * empty spaces, and each rank is separated by a '/'.</li>
 * <li><b>Active colour</b>: 'w' if White is to move, 'b' if Black is to
 * move.</li>
 * <li><b>Castling availability</b>: Letters indicating available castling
 * rights (KQkq). Upper case letters are for white and lower case for black. '-'
 * if none.</li>
 * <li><b>En passant target square</b>: The square where en passant capture is
 * possible, or '-' if not applicable.</li>
 * <li><b>Halfmove clock</b>: The number of half-moves since the last pawn
 * advance or capture (for the fifty-move rule).</li>
 * <li><b>Fullmove number</b>: The number of the full moves. It starts at 1 and
 * increments after Black's move.</li>
 * </ul>
 */
public final class Fen {

    private Fen() {
        // private constructor to prevent instantiation of this utility class
    }

    private static final List<Character> PIECES = Arrays.asList('P', 'N', 'B', 'R', 'Q', 'K', 'p', 'n', 'b', 'r', 'q',
            'k');

    /**
     * Loads the position and game rules from the FEN string to the specified board.
     * 
     * @param fen   the FEN string for the board position that is being loaded
     * @param board the board to load the position to
     */
    public static void load(String fen, Board board) {
        String[] fields = fen.split(" ");
        loadBoard(fields[0], board);
        setTurn(fields[1], board);
        setCastlingRights(fields[2], board);
        setEnPassantSquare(fields[3], board);
        board.setHalfmoveClock(Integer.parseInt(fields[4]));
        board.setFullmoveNumber(Integer.parseInt(fields[5]));
        board.updateCompositeBitboards();
    }

    /**
     * Loads the pieces specified in the first field onto a board. The old board is
     * cleared before loading.
     * 
     * @param boardPosition the position of the pieces on the board
     * @param board         the board to load the pieces onto
     */
    private static void loadBoard(String boardPosition, Board board) {
        String[] ranks = boardPosition.split("/");

        // Clear board
        for (Piece piece : Piece.values()) {
            board.setBitboard(piece, 0L);
        }

        // Iterate over squares in each rank and load pieces to square
        int square = 0;
        for (int rank = 0; rank < 8; rank++) {
            for (int j = 0; j < ranks[rank].length(); j++) {

                Piece piece;
                try {
                    piece = Piece.fromIndex(PIECES.indexOf(ranks[rank].charAt(j)));
                } catch (IllegalArgumentException e) {
                    piece = null;
                }

                if (piece != null) {
                    board.setBitboard(piece, board.getBitboard(piece) | LookupTables.BITBOARD_SQUARES[square]);
                    square += 1;
                } else {
                    square += Character.getNumericValue(ranks[rank].charAt(j));
                }
            }
        }
    }

    /**
     * Sets the turn to white or black.
     * 
     * @param turn  either "w" or "b" for white or blacks turn
     * @param board the board to load the turn to
     */
    private static void setTurn(String turn, Board board) {
        board.setWhiteTurn(turn.equals("w"));
    }

    /**
     * Sets the castling rights of each side.
     * 
     * @param castlingAvailability string with letters showing the castling rights
     *                             for each side (KQkq) or "-" for none with upper
     *                             case for white and lower case for black
     * 
     * @param board                the board to load the castling rights to
     */
    private static void setCastlingRights(String castlingAvailability, Board board) {
        int castlingRights = 0;
        if (!castlingAvailability.equals("-")) {
            for (char c : castlingAvailability.toCharArray()) {
                switch (c) {
                    case 'K':
                        castlingRights |= 0b0001;
                        break;
                    case 'Q':
                        castlingRights |= 0b0010;
                        break;
                    case 'k':
                        castlingRights |= 0b0100;
                        break;
                    case 'q':
                        castlingRights |= 0b1000;
                        break;
                }
            }
        }
        board.setCastlingRights(castlingRights);
    }

    /**
     * Sets the square that an en passant capture is possible on. It is set to -1 if
     * there is no possible en passant capture.
     * 
     * @param targetSquare the square where an en passant capture is possible (0-63)
     *                     or "-" for none
     * @param board        the board to set the en passant square to
     */
    private static void setEnPassantSquare(String targetSquare, Board board) {
        if (!targetSquare.equals("-")) {
            int file = targetSquare.toCharArray()[0] - 'a';
            int rank = 8 - (targetSquare.toCharArray()[1] - '0');
            int enPassantSquare = rank * 8 + file;
            board.setEnPassantSquare(enPassantSquare);
        } else {
            board.setEnPassantSquare(-1);
        }
    }
}