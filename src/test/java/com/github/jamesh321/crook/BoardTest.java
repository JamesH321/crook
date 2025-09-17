package com.github.jamesh321.crook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void getPieceAtSquare_shouldReturnWhitePawn() {
        assertEquals(Piece.WHITE_PAWN, board.getPieceAtSquare(48));
        assertEquals(Piece.WHITE_PAWN, board.getPieceAtSquare(52));
        assertEquals(Piece.WHITE_PAWN, board.getPieceAtSquare(55));
    }

    @Test
    void getPieceAtSquare_shouldReturnEmptySquare() {
        assertNull(board.getPieceAtSquare(24));
        assertNull(board.getPieceAtSquare(28));
        assertNull(board.getPieceAtSquare(31));
    }
}
