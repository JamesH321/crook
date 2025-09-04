package com.github.jamesh321.crook;

/**
 * A utility class for displaying the chess board in the console.
 */
public final class Display {

    private Display() {
        // private constructor to prevent instantiation of this utility class
    }

    private static final char[] PIECES = { 'P', 'N', 'B', 'R', 'Q', 'K', 'p', 'n', 'b', 'r', 'q', 'k' };

    /**
     * Prints the current state of the board to the console.
     *
     * @param board The board to display.
     */
    public static void board(Board board) {
        System.out.println("      a   b   c   d   e   f   g   h");
        System.out.println("    +---+---+---+---+---+---+---+---+");
        System.out.println(formatBoard(board));
        System.out.println("    +---+---+---+---+---+---+---+---+");
        System.out.println("      a   b   c   d   e   f   g   h");
    }

    /**
     * Formats the board into a string representation.
     *
     * @param board The board to format.
     * @return A string representing the board.
     */
    private static String formatBoard(Board board) {
        StringBuilder boardString = new StringBuilder();
        for (int square = 0; square < 64; square++) {
            int file = square % 8;
            int rank = 8 - (square / 8);

            int pieceIndex;
            try {
                pieceIndex = board.getPieceAtSquare(square).getIndex();
            } catch (Exception e) {
                pieceIndex = -1;
            }

            if (file == 0) {
                boardString.append(String.format("%d   |", rank));
            }

            boardString.append(pieceIndex != -1 ? String.format(" %c |", PIECES[pieceIndex]) : "   |");

            if (file == 7 && rank != 1) {
                boardString.append(String.format("   %d\n", rank));
                boardString.append("    +---+---+---+---+---+---+---+---+\n");
            }
        }
        return boardString.toString();
    }
}