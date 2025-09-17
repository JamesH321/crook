package com.github.jamesh321.crook;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class MoveExecutorTest {
    Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void testNormalMove() {
        Move move = new Move(55, 39, Move.NORMAL);
        MoveExecutor.makeMove(board, move);
        assertEquals(Piece.WHITE_PAWN, board.getPieceAtSquare(39));
        assertNull(board.getPieceAtSquare(55));
    }

    @Test
    void testTakePiece() {
        board.setBitboard(Piece.BLACK_PAWN, board.getBitboard(Piece.BLACK_PAWN) | (1L >>> 63 - 36));
        Move move = new Move(51, 36, Move.NORMAL);
        MoveExecutor.makeMove(board, move);
        assertEquals(Piece.WHITE_PAWN, board.getPieceAtSquare(36));
        assertNull(board.getPieceAtSquare(51));
    }

    @Test
    void testPromotion() {
        board.setBitboard(Piece.WHITE_PAWN, 1L >>> 63 - 8);
        Move move = new Move(8, 0, Move.ROOK_PROMOTION);
        MoveExecutor.makeMove(board, move);
        assertEquals(Piece.WHITE_ROOK, board.getPieceAtSquare(0));
        assertNull(board.getPieceAtSquare(8));
    }

    @Test
    void testEnPassant() {
        Move move1 = new Move(55, 31, Move.NORMAL);
        MoveExecutor.makeMove(board, move1);
        board.setWhiteTurn(false);
        Move move2 = new Move(14, 30, Move.NORMAL);
        MoveExecutor.makeMove(board, move2);
        board.setWhiteTurn(true);
        Move move3 = new Move(31, 22, Move.EN_PASSANT);
        MoveExecutor.makeMove(board, move3);
        assertEquals(Piece.WHITE_PAWN, board.getPieceAtSquare(22));
        assertNull(board.getPieceAtSquare(30));
    }

    @Test
    void testCastlingKingside() {
        Move move1 = new Move(61, 45, Move.NORMAL);
        MoveExecutor.makeMove(board, move1);
        Move move2 = new Move(62, 46, Move.NORMAL);
        MoveExecutor.makeMove(board, move2);
        Move move3 = new Move(60, 62, Move.CASTLE);
        MoveExecutor.makeMove(board, move3);
        assertEquals(Piece.WHITE_KING, board.getPieceAtSquare(62));
        assertEquals(Piece.WHITE_ROOK, board.getPieceAtSquare(61));
        assertNull(board.getPieceAtSquare(60));
        assertNull(board.getPieceAtSquare(63));
        assertEquals(0b1100, board.getCastlingRights());
    }

    @Test
    void testCastlingQueenside() {
        Move move1 = new Move(59, 45, Move.NORMAL);
        MoveExecutor.makeMove(board, move1);
        board.setWhiteTurn(true);
        Move move2 = new Move(58, 46, Move.NORMAL);
        MoveExecutor.makeMove(board, move2);
        board.setWhiteTurn(true);
        Move move3 = new Move(57, 47, Move.NORMAL);
        MoveExecutor.makeMove(board, move3);
        board.setWhiteTurn(true);
        Move move4 = new Move(60, 58, Move.CASTLE);
        MoveExecutor.makeMove(board, move4);
        board.setWhiteTurn(true);
        assertEquals(Piece.WHITE_KING, board.getPieceAtSquare(58));
        assertEquals(Piece.WHITE_ROOK, board.getPieceAtSquare(59));
        assertNull(board.getPieceAtSquare(60));
        assertNull(board.getPieceAtSquare(56));
        assertEquals(0b1100, board.getCastlingRights());
    }

    @Test
    void testSetEnPassantSquare() {
        Move move = new Move(52, 36, 0);
        MoveExecutor.makeMove(board, move);
        assertEquals(44, board.getEnPassantSquare());
    }

    @Test
    void testSetCastlingRightsKingMove() {
        Move move = new Move(60, 61, 0);
        MoveExecutor.makeMove(board, move);
        assertEquals(0b1100, board.getCastlingRights());
    }

    @Test
    void testSetCastlingRightsRookMove() {
        Move move = new Move(63, 39, 0);
        MoveExecutor.makeMove(board, move);
        assertEquals(0b1110, board.getCastlingRights());
    }
}