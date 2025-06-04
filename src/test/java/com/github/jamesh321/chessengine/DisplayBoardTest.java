package com.github.jamesh321.chessengine;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DisplayBoardTest {

    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void testDisplayBoardRuns() {
        Board board = new Board();
        DisplayBoard displayBoard = new DisplayBoard();
        displayBoard.displayBoard(board);
        assertTrue(true);
    }
}
