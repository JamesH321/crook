package com.github.jamesh321.chessengine;

public class DisplayBoard {
    private static final char[] PIECES = { 'P', 'N', 'B', 'R', 'Q', 'K', 'p', 'n', 'b', 'r', 'q', 'k' };
    private static final char DARK_SQUARE = '■';

    public static void displayBoard(Board board) {
        System.out.println("     a  b  c  d  e  f  g  h");
        System.out.println("    ========================");
        System.out.println(convertBoard(board));
        System.out.println("    ========================");
        System.out.println("     a  b  c  d  e  f  g  h");
    }

    public static String convertBoard(Board board) {
        String boardString = "";
        long startingSquare = 0x8000000000000000L;
        long[] allPieces = board.getBitboards();
        boolean isEmptySquare;
        for (int i = 0; i < 64; i++) {
            isEmptySquare = true;
            int row = 8 - (i / 8);
            if (i % 8 == 0) {
                boardString += String.format("%d   |", row);
            }
            for (int j = 0; j < 12; j++) {
                if ((startingSquare & allPieces[j]) != 0) {
                    boardString += PIECES[j];
                    isEmptySquare = false;
                    break;
                }
            }
            if (isEmptySquare) {
                if (isLightSquare(i)) {
                    boardString += ' ';
                } else {
                    boardString += DARK_SQUARE;
                }
            }
            if (i % 8 == 7) {
                boardString += String.format("|   %d", row);
                if (i != 63) {
                    boardString += "\n";
                }
            } else {
                boardString += "  ";
            }
            startingSquare >>>= 1;
        }
        return boardString;
    }

    public static boolean isLightSquare(int square) {
        int row = square / 8;
        int column = square % 8;
        return (row + column) % 2 == 0;
    }
}