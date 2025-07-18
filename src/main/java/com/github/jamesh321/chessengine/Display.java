package com.github.jamesh321.chessengine;

/**
 * A utility class for displaying the chess board in the console.
 */
public class Display {
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
        String boardString = "";
        for (int square = 0; square < 64; square++) {
            int file = square % 8;
            int rank = 8 - (square / 8);
            int pieceIndex = board.getPieceAtSquare(square).getIndex();

            if (file == 0) {
                boardString += String.format("%d   |", rank);
            }

            boardString += pieceIndex != -1 ? String.format(" %c |", PIECES[pieceIndex]) : "   |";

            if (file == 7 && rank != 1) {
                boardString += String.format("   %d\n", rank);
                boardString += "    +---+---+---+---+---+---+---+---+\n";
            }
        }
        return boardString;
    }
}