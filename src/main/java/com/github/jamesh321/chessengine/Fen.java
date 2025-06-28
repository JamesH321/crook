package com.github.jamesh321.chessengine;

import java.util.Arrays;
import java.util.List;

public class Fen {
    private static final List<Character> PIECES = Arrays.asList('P', 'N', 'B', 'R', 'Q', 'K', 'p', 'n', 'b', 'r', 'q',
            'k');

    public static void load(Board board, String fen) {
        String[] fields = fen.split("[/ ]");
        loadBoard(fields, board);
        setTurn(fields[8], board);
        setCastlingRights(fields[9], board);
        setEnPassantSquare(fields[10], board);
        board.setHalfmoveClock(Integer.parseInt(fields[11]));
        board.setFullmoveNumber(Integer.parseInt(fields[12]));
    }

    private static void loadBoard(String[] fields, Board board) {
        for (int i = 0; i < 12; i++) {
            board.setBitboard(i, 0L);
        }
        int square = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < fields[i].length(); j++) {
                int piece = PIECES.indexOf(fields[i].charAt(j));
                if (piece > -1) {
                    board.setBitboard(piece, board.getBitboard(piece) | 1L << (63 - square));
                    square += 1;
                } else {
                    square += Character.getNumericValue(fields[i].charAt(j));
                }
            }
        }
    }

    private static void setTurn(String turn, Board board) {
        if (turn.equals("w")) {
            board.setWhiteTurn(true);
        } else {
            board.setWhiteTurn(false);
        }
    }

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