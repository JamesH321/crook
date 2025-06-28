package com.github.jamesh321.chessengine;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FenTest {
    @Test
    void load_ShouldSetInitialPosition() {
        Board board = new Board();
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Fen.load(board, fen);

        assertEquals(0x000000000000FF00L, board.getBitboard(0));
        assertEquals(0x00FF000000000000L, board.getBitboard(6));
        assertTrue(board.isWhiteTurn());
        assertEquals(0b1111, board.getCastlingRights());
        assertEquals(-1, board.getEnPassantSquare());
        assertEquals(0, board.getHalfmoveClock());
        assertEquals(1, board.getFullmoveNumber());
    }

    @Test
    void load_shouldSetPositionWithEnPassantAndCastling() {
        Board board = new Board();
        String fen = "rnbqkbnr/pppp1ppp/8/4p3/3P4/8/PPP1PPPP/RNBQKBNR b Kq d3 3 7";
        Fen.load(board, fen);

        assertFalse(board.isWhiteTurn());
        assertEquals(0b1001, board.getCastlingRights());
        assertEquals(43, board.getEnPassantSquare());
        assertEquals(3, board.getHalfmoveClock());
        assertEquals(7, board.getFullmoveNumber());
    }

    @Test
    void load_shouldSetPositionNoCastlingNoEnPassant() {
        Board board = new Board();
        String fen = "8/8/8/8/8/8/8/8 w - - 10 20";
        Fen.load(board, fen);

        for (int i = 0; i < 12; i++) {
            assertEquals(0L, board.getBitboard(i));
        }
        assertEquals(0, board.getCastlingRights());
        assertEquals(-1, board.getEnPassantSquare());
        assertEquals(10, board.getHalfmoveClock());
        assertEquals(20, board.getFullmoveNumber());
    }
}
