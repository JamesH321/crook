package com.github.jamesh321.chessengine;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MoveExecutorTest {

    @Test
    void testNormalMove() {
        Board board = new Board();
        Move move = new Move(55, 39, 0);
        MoveExecutor.makeMove(board, move);
        assertEquals(0, board.getPieceAtSquare(39));
        assertEquals(-1, board.getPieceAtSquare(55));
    }

    @Test
    void testTakePiece() {
        Board board = new Board();
        board.setBitboard(6, board.getBitboard(6) | (0x8000000000000000L >>> 36));
        Move move = new Move(51, 36, 0);
        MoveExecutor.makeMove(board, move);
        assertEquals(0, board.getPieceAtSquare(36));
        assertEquals(-1, board.getPieceAtSquare(51));
        assertFalse((board.getBitboard(6) & (0x8000000000000000L >>> 36)) != 0);
    }

    @Test
    void testPromotion() {
        Board board = new Board();
        board.setBitboard(0, 0x0100000000000000L >>> 8);
        Move move = new Move(8, 0, 0b0101);
        MoveExecutor.makeMove(board, move);
        assertEquals(3, board.getPieceAtSquare(0));
        assertEquals(-1, board.getPieceAtSquare(8));
    }

    @Test
    void testEnPassant() {
        Board board = new Board();
        Move move1 = new Move(55, 31, 0);
        MoveExecutor.makeMove(board, move1);
        board.setWhiteTurn(false);
        Move move2 = new Move(14, 30, 0);
        MoveExecutor.makeMove(board, move2);
        board.setWhiteTurn(true);
        Move move3 = new Move(31, 22, 0b0010);
        MoveExecutor.makeMove(board, move3);
        assertEquals(0, board.getPieceAtSquare(22));
        assertEquals(-1, board.getPieceAtSquare(30));
    }

    @Test
    void testCastlingKingside() {
        Board board = new Board();
        Move move1 = new Move(61, 45, 0);
        MoveExecutor.makeMove(board, move1);
        Move move2 = new Move(62, 46, 0);
        MoveExecutor.makeMove(board, move2);
        Move move3 = new Move(60, 62, 0b0011);
        MoveExecutor.makeMove(board, move3);
        assertEquals(5, board.getPieceAtSquare(62));
        assertEquals(3, board.getPieceAtSquare(61));
        assertEquals(-1, board.getPieceAtSquare(60));
        assertEquals(-1, board.getPieceAtSquare(63));
        assertEquals(0, board.getCastlingRights());
    }

    @Test
    void testCastlingQueenside() {
        Board board = new Board();
        Move move1 = new Move(59, 45, 0);
        MoveExecutor.makeMove(board, move1);
        Move move2 = new Move(58, 46, 0);
        MoveExecutor.makeMove(board, move2);
        Move move3 = new Move(57, 47, 0);
        MoveExecutor.makeMove(board, move3);
        Move move4 = new Move(60, 58, 0b0011);
        MoveExecutor.makeMove(board, move4);
        assertEquals(5, board.getPieceAtSquare(58));
        assertEquals(3, board.getPieceAtSquare(59));
        assertEquals(-1, board.getPieceAtSquare(60));
        assertEquals(-1, board.getPieceAtSquare(56));
        assertEquals(0, board.getCastlingRights());
    }

    @Test
    void testSetEnPassantSquare() {
        Board board = new Board();
        Move move = new Move(52, 36, 0);
        MoveExecutor.makeMove(board, move);
        assertEquals(44, board.getEnPassantSquare());
    }

    @Test
    void testSetCastlingRightsKingMove() {
        Board board = new Board();
        Move move = new Move(60, 61, 0);
        MoveExecutor.makeMove(board, move);
        assertEquals(0b1100, board.getCastlingRights());
    }

    @Test
    void testSetCastlingRightsRookMove() {
        Board board = new Board();
        Move move = new Move(63, 39, 0);
        MoveExecutor.makeMove(board, move);
        assertEquals(0b1110, board.getCastlingRights());
    }
}