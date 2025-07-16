package com.github.jamesh321.chessengine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class MoveTest {

    Move move;

    @BeforeEach
    void setUp() {
        move = new Move(0b001111, 0b000111, 0b1101);
    }

    @Test
    void getFrom_shouldReturnMoveFromSquare() {
        assertEquals(0b001111, move.getFrom());
    }

    @Test
    void getTo_shouldReturnMoveToSquare() {
        assertEquals(0b000111, move.getTo());
    }

    @Test
    void getFlag_shouldReturnCorrectMoveFlag() {
        assertEquals(0b1101, move.getFlag());
    }

    @Test
    void getPromotionPiece_shouldReturnThree() {
        assertEquals(3, move.getPromotionPiece());
    }

    @Test
    void getSpecialMove_shouldReturnOne() {
        assertEquals(1, move.getSpecialMove());
    }
}
