package com.github.jamesh321.chessengine;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MoveGeneratorTest {
    @Test
    void generateWhitePawnMoves_shouldReturnCorrectNumberOfMoves() {
        Board board = new Board();
        assertEquals(16, MoveGenerator.generateWhitePawnMoves(board).size());
    }

    @Test
    void generateBlackPawnMoves_shouldReturnCorrectNumberOfMoves() {
        Board board = new Board();
        assertEquals(16, MoveGenerator.generateBlackPawnMoves(board).size());
    }
}
