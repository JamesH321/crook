package com.github.jamesh321.chessengine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class MoveGeneratorTest {
    Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void generateKnightMoves_shouldReturnCorrectNumberOfMoves() {
        assertEquals(4, MoveGenerator.generateKnightMoves(board).size());

    }

    @Test
    void generateBishopMoves_shouldReturnCorrectNumberOfMoves() {
        board.setBitboard(2, 0b00100010L << 24);
        board.updateCompositeBitboards();
        assertEquals(13, MoveGenerator.generateBishopMoves(board).size());
    }

    @Test
    void generateRookMoves_shouldReturnCorrectNumberOfMoves() {
        board.setBitboard(3, 0b00100001L << 32);
        board.updateCompositeBitboards();
        assertEquals(18, MoveGenerator.generateRookMoves(board).size());
    }

    @Test
    void generateQueenMoves_shouldReturnCorrectNumberOfMoves() {
        board.setBitboard(4, 0b00010000L << 32);
        board.updateCompositeBitboards();
        assertEquals(19, MoveGenerator.generateQueenMoves(board).size());
    }

    @Test
    void generateKingMoves_shouldReturnCorrectNumberOfMovesWithoutCastles() {
        board.setBitboard(5, 1L << 16);
        board.setCastlingRights(0);
        board.updateCompositeBitboards();
        assertEquals(3, MoveGenerator.generateKingMoves(board).size());
    }

    @Test
    void generateKingMoves_shouldReturnCorrectNumberOfMovesWithCastles() {
        for (int i = 1; i < 5; i++) {
            board.setBitboard(i, 0);
        }
        board.updateCompositeBitboards();
        assertEquals(4, MoveGenerator.generateKingMoves(board).size());
    }
}