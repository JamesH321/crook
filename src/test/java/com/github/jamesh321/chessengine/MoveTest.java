package com.github.jamesh321.chessengine;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MoveTest {
    @Test
    void generateMove_shouldReturnExpectedMove() {
        assertEquals(0b1101_000111_001111, Move.generateMove(15, 7, 0b1101));
    }

    @Test
    void getFrom_shouldReturnMoveFromSquare() {
        assertEquals(0b001111, Move.getFrom(0b1101_000111_001111));
    }

    @Test
    void getTo_shouldReturnMoveToSquare() {
        assertEquals(0b000111, Move.getTo(0b1101_000111_001111));
    }

    @Test
    void getFlag_shouldReturnCorrectMoveFlag() {
        assertEquals(0b1101, Move.getFlag(0b1101_000111_001111));
    }

    @Test
    void getPromotionPiece_shouldReturnThree() {
        assertEquals(3, Move.getPromotionPiece(0b1101_000111_001111));
    }

    @Test
    void getSpecialMove_shouldReturnOne() {
        assertEquals(1, Move.getSpecialMove(0b1101_000111_001111));
    }
}
