package com.github.jamesh321.chessengine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class MoveGeneratorTest {
    Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void generateWhitePawnMoves_shouldReturnCorrectNumberOfMoves() {
        board.setBitboard(0, 0b10101111L << 8);
        board.setBitboard(6, 0b10000000_00100000L << 16);
        board.updateCompositeBitboards();
        ArrayList<Move> moves = MoveGenerator.generateWhitePawnMoves(board);
        assertEquals(9, moves.size());
        for (Move move : moves) {
            assertEquals(Move.NORMAL, move.getFlag());
        }
    }

    @Test
    void generateBlackPawnMoves_shouldReturnCorrectNumberOfNormalMoves() {
        board.setBitboard(6, 0b10101111L << 48);
        board.setBitboard(0, 0b00100000_10000000L << 32);
        board.updateCompositeBitboards();
        ArrayList<Move> moves = MoveGenerator.generateBlackPawnMoves(board);
        assertEquals(9, moves.size());
        for (Move move : moves) {
            assertEquals(Move.NORMAL, move.getFlag());
        }
    }

    @Test
    void generateWhitePawnMoves_shoudlReturnCorrectNumberOfCaptureMoves() {
        board.setBitboard(0, 0b00010000L << 8);
        board.setBitboard(6, 0b00111000L << 16);
        board.updateCompositeBitboards();
        ArrayList<Move> moves = MoveGenerator.generateWhitePawnMoves(board);
        assertEquals(2, moves.size());
        for (Move move : moves) {
            assertEquals(Move.NORMAL, move.getFlag());
        }
    }

    @Test
    void generateBlackPawnMoves_shoudlReturnCorrectNumberOfCaptureMoves() {
        board.setBitboard(6, 0b00010000L << 16);
        board.setBitboard(0, 0b00111000L << 8);
        board.updateCompositeBitboards();
        ArrayList<Move> moves = MoveGenerator.generateBlackPawnMoves(board);
        assertEquals(2, moves.size());
        for (Move move : moves) {
            assertEquals(Move.NORMAL, move.getFlag());
        }
    }

    @Test
    void generateWhitePawnMoves_shoudlReturnCorrectNumberOfEnPassantMoves() {
        board.setBitboard(0, 0b00010000L << 32);
        board.setBitboard(6, (0b00010000L << 40) | (0b00001000L << 48));
        MoveExecutor.makeMove(board, new Move(12, 28, Move.NORMAL));
        board.updateCompositeBitboards();
        ArrayList<Move> moves = MoveGenerator.generateWhitePawnMoves(board);
        assertEquals(1, moves.size());
        assertEquals(Move.EN_PASSANT, moves.get(0).getFlag());
    }

    @Test
    void generateBlackPawnMoves_shoudlReturnCorrectNumberOfEnPassantMoves() {
        board.setBitboard(6, 0b00010000L << 24);
        board.setBitboard(0, (0b00010000L << 16) | (0b00001000L << 8));
        MoveExecutor.makeMove(board, new Move(52, 36, Move.NORMAL));
        board.updateCompositeBitboards();
        ArrayList<Move> moves = MoveGenerator.generateBlackPawnMoves(board);
        assertEquals(1, moves.size());
        assertEquals(Move.EN_PASSANT, moves.get(0).getFlag());
    }

    @Test
    void generateWhitePawnMoves_shouldReturnCorrectNumberOfPromotionMoves() {
        for (int i = 6; i < 12; i++) {
            board.setBitboard(i, 0);
        }
        board.setBitboard(0, 1L << 48);
        ArrayList<Move> moves = MoveGenerator.generateWhitePawnMoves(board);
        assertEquals(4, moves.size());
        assertEquals(Move.QUEEN_PROMOTION, moves.get(0).getFlag());
        assertEquals(Move.ROOK_PROMOTION, moves.get(1).getFlag());
        assertEquals(Move.BISHOP_PROMOTION, moves.get(2).getFlag());
        assertEquals(Move.KNIGHT_PROMOTION, moves.get(3).getFlag());
    }

    @Test
    void generateBlackPawnMoves_shouldReturnCorrectNumberOfPromotionMoves() {
        for (int i = 0; i < 6; i++) {
            board.setBitboard(i, 0);
        }
        board.setBitboard(6, 1L << 8);
        ArrayList<Move> moves = MoveGenerator.generateBlackPawnMoves(board);
        assertEquals(4, moves.size());
        assertEquals(Move.QUEEN_PROMOTION, moves.get(0).getFlag());
        assertEquals(Move.ROOK_PROMOTION, moves.get(1).getFlag());
        assertEquals(Move.BISHOP_PROMOTION, moves.get(2).getFlag());
        assertEquals(Move.KNIGHT_PROMOTION, moves.get(3).getFlag());
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
}