package com.github.jamesh321.crook;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EvaluateTest {
    Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void testMaterial_startingPosition() {
        assertEquals(0, Evaluate.material(board));
    }

    @Test
    void testMaterial_fenPosition1() {
        Fen.load("8/7N/2R5/4r1p1/2P4k/1P6/P3b1PP/4R1K1 b - - 0 38", board);
        assertEquals(900, Evaluate.material(board));
    }

    @Test
    void testMaterial_fenPosition2() {
        Fen.load("r1b1k1nr/ppp2ppp/8/8/4P3/2P5/PP3KPP/RN1q1B1R w kq - 0 11", board);
        assertEquals(-900, Evaluate.material(board));
    }

    @Test
    void testMaterial_fenPosition3() {
        Fen.load("7k/7P/7K/5N2/8/8/8/8 b - - 2 59", board);
        assertEquals(400, Evaluate.material(board));
    }

    @Test
    void testPiecePositions_startingPosition() {
        assertEquals(0, Evaluate.piecePositions(board));
    }

    @Test
    void testPiecePositions_fenPosition1() {
        Fen.load("8/7N/2R5/4r1p1/2P4k/1P6/P3b1PP/4R1K1 b - - 0 38", board);
        assertEquals(-20, Evaluate.piecePositions(board));
    }

    @Test
    void testPiecePositions_fenPosition2() {
        Fen.load("r1b1k1nr/ppp2ppp/8/8/4P3/2P5/PP3KPP/RN1q1B1R w kq - 0 11", board);
        assertEquals(-5, Evaluate.piecePositions(board));
    }

    @Test
    void testPiecePositions_fenPosition3() {
        Fen.load("7k/7P/7K/5N2/8/8/8/8 b - - 2 59", board);
        assertEquals(85, Evaluate.piecePositions(board));
    }

}
