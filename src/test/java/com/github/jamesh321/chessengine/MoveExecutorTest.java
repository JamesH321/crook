package com.github.jamesh321.chessengine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class MoveExecutorTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void movePiece_shouldMovePieceToCorrectSquare() {
        MoveExecutor.movePiece(board, 0, 0x100L, 0x1000000L);
        assertEquals(0x100FE00L, board.getBitboard(0));
    }

    @Test
    void takePiece_shouldRemoveCorrectPiece() {
        MoveExecutor.takePiece(board, 0, 0x100L);
        assertEquals(0xFE00L, board.getBitboard(0));
    }

    @Test
    void getPieceIndex_shouldReturnCorrectIndex() {
        assertEquals(3, MoveExecutor.getPieceIndex(1, true));
        assertEquals(9, MoveExecutor.getPieceIndex(1, false));
    }

    @Test
    void takeEnPassantPiece_shouldRemoveCorrectPawn() {
        MoveExecutor.takeEnPassantPiece(board, 1L << 63);
        assertEquals(0b01111111L << 48, board.getBitboard(6));
    }

    @Test
    void castle_shouldMoveKingAndRookCorrectlyOnKingside() {
        MoveExecutor.castle(board, 62, 5, 0b1000, 0b0010);
        assertEquals(0b10, board.getBitboard(5));
        assertEquals(0b10000100, board.getBitboard(3));
    }

    @Test
    void castle_shouldMoveKingAndRookCorrectlyOnQueenside() {
        MoveExecutor.castle(board, 58, 5, 0b1000, 0b00100000);
        assertEquals(0b00100000, board.getBitboard(5));
        assertEquals(0b00010001, board.getBitboard(3));
    }

    @Test
    void setEnPassantSquare_shouldSetWhenWhitePawnMovesTwoSquares() {
        MoveExecutor.setEnPassantSquare(board, 0, 55, 39);
        assertEquals(47, board.getEnPassantSquare());
    }

    @Test
    void setEnPassantSquare_shouldSetWhenBlackPawnMovesTwoSquares() {
        MoveExecutor.setEnPassantSquare(board, 0, 15, 31);
        assertEquals(23, board.getEnPassantSquare());
    }

    @Test
    void setEnPassantSquare_shouldClearWhenNoEnPassantPossible() {
        MoveExecutor.setEnPassantSquare(board, 0, 15, 23);
        assertEquals(-1, board.getEnPassantSquare());
    }

    @Test
    void setCastlingRights_shouldUpdateCastlingRightsWhenKingMoved() {
        MoveExecutor.setCastlingRights(board, 11, 4);
        assertEquals(0b0011, board.getCastlingRights());
    }

    @Test
    void setCastlingRights_shouldUpdateCastlingRightsWhenRookMoved() {
        MoveExecutor.setCastlingRights(board, 9, 0);
        assertEquals(0b0111, board.getCastlingRights());
    }

    @Test
    void makeMove_shouldDoNormalMoveCorrectly() {
        MoveExecutor.makeMove(board, 0b0000_100100_110100); // from 52, to 36
        assertEquals(0x800F700L, board.getBitboard(0));
        assertEquals(0b1111, board.getCastlingRights());
        assertEquals(44, board.getEnPassantSquare());
    }

    @Test
    void makeMove_shouldDoQueenPromotionMoveCorrectly() {
        MoveExecutor.makeMove(board, 0b0001_000111_110111);
        assertEquals(0b11111110 << 8, board.getBitboard(0));
        assertEquals(0x100000000000010L, board.getBitboard(4));
        assertEquals(0b1111, board.getCastlingRights());
        assertEquals(-1, board.getEnPassantSquare());
    }
}