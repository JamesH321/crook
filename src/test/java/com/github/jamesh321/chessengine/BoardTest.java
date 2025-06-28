package com.github.jamesh321.chessengine;

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
        assertEquals(0, board.getPieceAtSquare(48));
        assertEquals(0, board.getPieceAtSquare(52));
        assertEquals(0, board.getPieceAtSquare(55));
    }

    @Test
    void getPieceAtSquare_shouldReturnEmptySquare() {
        assertEquals(-1, board.getPieceAtSquare(24));
        assertEquals(-1, board.getPieceAtSquare(28));
        assertEquals(-1, board.getPieceAtSquare(31));
    }
}
