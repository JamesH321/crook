package com.github.jamesh321.chessengine;

import java.util.ArrayList;

public class MoveGenerator {
    public static ArrayList<Move> generateWhitePawnMoves(Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long pawns = board.getBitboard(0);
        long emptyNoPromotion = board.getEmptySquares() & ~(0b11111111L << 56);
        long emptyPromotion = board.getEmptySquares() & (0b11111111L << 56);
        long blackPiecesNoPromotion = board.getBlackPieces() & ~(0b11111111L << 56);
        long blackPiecesPromotion = board.getBlackPieces() & (0b11111111L << 56);
        long fileMask = 0x101010101010101L;
        long enPassantSquare = 1L << (63 - board.getEnPassantSquare());

        moveList.addAll(generateWhiteSinglePawnPushMoves(pawns, emptyNoPromotion));
        moveList.addAll(generateWhiteDoublePawnPushMoves(pawns, emptyNoPromotion));
        moveList.addAll(generateWhitePawnCaptureMoves(pawns, emptyNoPromotion, blackPiecesNoPromotion, fileMask));
        moveList.addAll(generateWhitePawnEnPassantMoves(pawns, emptyNoPromotion, blackPiecesNoPromotion, fileMask,
                enPassantSquare));
        moveList.addAll(generateWhitePawnPromotionMoves(pawns, emptyPromotion, blackPiecesPromotion, fileMask));

        return moveList;
    }

    public static ArrayList<Move> generateWhiteSinglePawnPushMoves(long pawns, long emptyNoPromotion) {
        ArrayList<Move> moveList = new ArrayList<>();
        long singlePushMoves = (pawns << 8) & emptyNoPromotion;
        moveList.addAll(getPawnMoveList(singlePushMoves, 8, Move.NORMAL));
        return moveList;
    }

    public static ArrayList<Move> generateWhiteDoublePawnPushMoves(long pawns, long emptyNoPromotion) {
        ArrayList<Move> moveList = new ArrayList<>();
        long bothSquaresFree = (emptyNoPromotion >>> 16) & (emptyNoPromotion >>> 24) & 0b11111111;
        long doublePushMoves = (pawns << 16) & (bothSquaresFree << 24);
        moveList.addAll(getPawnMoveList(doublePushMoves, 16, Move.NORMAL));
        return moveList;
    }

    public static ArrayList<Move> generateWhitePawnCaptureMoves(long pawns, long emptyNoPromotion,
            long blackPiecesNoPromotion,
            long fileMask) {
        ArrayList<Move> moveList = new ArrayList<>();
        long leftCaptureMoves = (pawns << 9) & (blackPiecesNoPromotion & ~fileMask);
        long rightCaptureMoves = (pawns << 7) & (blackPiecesNoPromotion & ~(fileMask << 8));
        moveList.addAll(getPawnMoveList(leftCaptureMoves, 9, Move.NORMAL));
        moveList.addAll(getPawnMoveList(rightCaptureMoves, 7, Move.NORMAL));
        return moveList;
    }

    public static ArrayList<Move> generateWhitePawnEnPassantMoves(long pawns, long emptyNoPromotion,
            long blackPiecesNoPromotion,
            long fileMask, long enPassantSquare) {
        ArrayList<Move> moveList = new ArrayList<>();
        long leftEnPassantMoves = (pawns << 9) & (enPassantSquare & ~fileMask);
        long rightEnPassantMoves = (pawns << 7) & (enPassantSquare & ~(fileMask << 8));
        moveList.addAll(getPawnMoveList(leftEnPassantMoves, 9, Move.EN_PASSANT));
        moveList.addAll(getPawnMoveList(rightEnPassantMoves, 7, Move.EN_PASSANT));
        return moveList;
    }

    public static ArrayList<Move> generateWhitePawnPromotionMoves(long pawns, long emptyPromotion,
            long blackPiecesPromotion, long fileMask) {
        ArrayList<Move> moveList = new ArrayList<>();
        long singlePushMoves = (pawns << 8) & emptyPromotion;
        long leftCaptureMoves = (pawns << 9) & (blackPiecesPromotion & ~fileMask);
        long rightCaptureMoves = (pawns << 7) & (blackPiecesPromotion & ~(fileMask << 8));
        for (int i = 0; i < 4; i++) {
            moveList.addAll(getPawnMoveList(singlePushMoves, 8, 1 + (4 * i)));
            moveList.addAll(getPawnMoveList(leftCaptureMoves, 9, 1 + (4 * i)));
            moveList.addAll(getPawnMoveList(rightCaptureMoves, 7, 1 + (4 * i)));
        }
        return moveList;
    }

    public static ArrayList<Move> generateBlackPawnMoves(Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long pawns = board.getBitboard(6);
        long emptyNoPromotion = board.getEmptySquares() & ~0b11111111;
        long emptyPromotion = board.getEmptySquares() & 0b11111111;
        long whitePiecesNoPromotion = board.getBlackPieces() & ~0b11111111;
        long whitePiecesPromotion = board.getBlackPieces() & 0b11111111;
        long fileMask = 0x101010101010101L;
        long enPassantSquare = 1L << (63 - board.getEnPassantSquare());

        moveList.addAll(generateBlackSinglePawnPushMoves(pawns, emptyNoPromotion));
        moveList.addAll(generateBlackDoublePawnPushMoves(pawns, emptyNoPromotion));
        moveList.addAll(generateBlackPawnCaptureMoves(pawns, emptyNoPromotion, whitePiecesNoPromotion, fileMask));
        moveList.addAll(generateBlackPawnEnPassantMoves(pawns, emptyNoPromotion, whitePiecesNoPromotion, fileMask,
                enPassantSquare));
        moveList.addAll(generateBlackPawnPromotionMoves(pawns, emptyPromotion, whitePiecesPromotion, fileMask));

        return moveList;
    }

    public static ArrayList<Move> generateBlackSinglePawnPushMoves(long pawns, long emptyNoPromotion) {
        ArrayList<Move> moveList = new ArrayList<>();
        long singlePushMoves = (pawns >>> 8) & emptyNoPromotion;
        moveList.addAll(getPawnMoveList(singlePushMoves, -8, Move.NORMAL));
        return moveList;
    }

    public static ArrayList<Move> generateBlackDoublePawnPushMoves(long pawns, long emptyNoPromotion) {
        ArrayList<Move> moveList = new ArrayList<>();
        long bothSquaresFree = (emptyNoPromotion >>> 40) & (emptyNoPromotion >>> 32) & 0b11111111;
        long doublePushMoves = (pawns >>> 16) & (bothSquaresFree << 32);
        moveList.addAll(getPawnMoveList(doublePushMoves, -16, Move.NORMAL));
        return moveList;
    }

    public static ArrayList<Move> generateBlackPawnCaptureMoves(long pawns, long emptyNoPromotion,
            long blackPiecesNoPromotion,
            long fileMask) {
        ArrayList<Move> moveList = new ArrayList<>();
        long leftCaptureMoves = (pawns >>> 7) & (blackPiecesNoPromotion & ~fileMask);
        long rightCaptureMoves = (pawns >>> 9) & (blackPiecesNoPromotion & ~(fileMask << 8));
        moveList.addAll(getPawnMoveList(leftCaptureMoves, -7, Move.NORMAL));
        moveList.addAll(getPawnMoveList(rightCaptureMoves, -9, Move.NORMAL));
        return moveList;
    }

    public static ArrayList<Move> generateBlackPawnEnPassantMoves(long pawns, long emptyNoPromotion,
            long blackPiecesNoPromotion,
            long fileMask, long enPassantSquare) {
        ArrayList<Move> moveList = new ArrayList<>();
        long leftEnPassantMoves = (pawns >>> 7) & (enPassantSquare & ~fileMask);
        long rightEnPassantMoves = (pawns >>> 9) & (enPassantSquare & ~(fileMask << 8));
        moveList.addAll(getPawnMoveList(leftEnPassantMoves, -7, Move.EN_PASSANT));
        moveList.addAll(getPawnMoveList(rightEnPassantMoves, -9, Move.EN_PASSANT));
        return moveList;
    }

    public static ArrayList<Move> generateBlackPawnPromotionMoves(long pawns, long emptyPromotion,
            long blackPiecesPromotion, long fileMask) {
        ArrayList<Move> moveList = new ArrayList<>();
        long singlePushMoves = (pawns >>> 8) & emptyPromotion;
        long leftCaptureMoves = (pawns >>> 7) & (blackPiecesPromotion & ~fileMask);
        long rightCaptureMoves = (pawns >>> 9) & (blackPiecesPromotion & ~(fileMask << 8));
        for (int i = 0; i < 4; i++) {
            moveList.addAll(getPawnMoveList(singlePushMoves, -8, 1 + (4 * i)));
            moveList.addAll(getPawnMoveList(leftCaptureMoves, -7, 1 + (4 * i)));
            moveList.addAll(getPawnMoveList(rightCaptureMoves, -9, 1 + (4 * i)));
        }
        return moveList;
    }

    public static ArrayList<Move> getPawnMoveList(long moves, int shift, int flag) {
        ArrayList<Move> moveList = new ArrayList<>();
        while (moves != 0) {
            int to = 63 - Long.numberOfTrailingZeros(moves);
            moveList.add(new Move(to + shift, to, flag));
            moves &= moves - 1;
        }
        return moveList;
    }
}