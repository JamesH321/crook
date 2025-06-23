package com.github.jamesh321.chessengine;

import java.util.ArrayList;

public class MoveGenerator {
    public static ArrayList<Move> generatePseudoLegalMoves(Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        moveList.addAll(generatePawnMoves(board));
        moveList.addAll(generateKnightMoves(board));
        moveList.addAll(generateBishopMoves(board));
        return moveList;
    }

    private static ArrayList<Move> getMoveList(long moves, int from, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        while (moves != 0) {
            int to = 63 - Long.numberOfTrailingZeros(moves);
            if (board.getPieceAtSquare(to) != 5 || board.getPieceAtSquare(to) != 11) {
                moveList.add(new Move(from, to, Move.NORMAL));
            }
            moves &= moves - 1;
        }
        return moveList;
    }

    public static ArrayList<Move> generatePawnMoves(Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        if (board.isWhiteTurn()) {
            moveList.addAll(generateWhitePawnMoves(board));
        } else {
            moveList.addAll(generateBlackPawnMoves(board));
        }
        return moveList;
    }

    public static ArrayList<Move> generateKnightMoves(Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long NOT_A_MASK = 0xFEFEFEFEFEFEFEFEL;
        long NOT_H_MASK = 0x7F7F7F7F7F7F7F7FL;
        long NOT_AB_MASK = 0xFCFCFCFCFCFCFCFCL;
        long NOT_GH_MASK = 0x3F3F3F3F3F3F3F3FL;
        long knights = board.isWhiteTurn() ? board.getBitboard(1) : board.getBitboard(7);
        long movable = board.isWhiteTurn() ? (board.getEmptySquares() | board.getBlackPieces())
                : (board.getEmptySquares() | board.getWhitePieces());

        while (knights != 0) {
            int from = 63 - Long.numberOfTrailingZeros(knights);
            long fromBitboard = 1L << Long.numberOfTrailingZeros(knights);
            long moves = 0L;
            moves |= (fromBitboard << 17) & NOT_A_MASK;
            moves |= (fromBitboard << 15) & NOT_H_MASK;
            moves |= (fromBitboard << 10) & NOT_AB_MASK;
            moves |= (fromBitboard << 6) & NOT_GH_MASK;
            moves |= (fromBitboard >>> 6) & NOT_AB_MASK;
            moves |= (fromBitboard >>> 10) & NOT_GH_MASK;
            moves |= (fromBitboard >>> 15) & NOT_A_MASK;
            moves |= (fromBitboard >>> 17) & NOT_H_MASK;
            moves &= movable;
            moveList.addAll(getMoveList(moves, from, board));
            knights &= knights - 1;
        }
        return moveList;
    }

    public static ArrayList<Move> generateBishopMoves(Board board) {
        long bishops = board.isWhiteTurn() ? board.getBitboard(2) : board.getBitboard(8);
        long ownPieces = board.isWhiteTurn() ? board.getWhitePieces() : board.getBlackPieces();
        long occupied = board.getOccupiedSquares();

        return getDiagonalMoves(bishops, occupied, ownPieces, board);
    }

    private static ArrayList<Move> getDiagonalMoves(long piece, long occupied, long ownPieces, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        while (piece != 0) {
            int from = 63 - Long.numberOfTrailingZeros(piece);
            long[] rays = RayLookup.DIAGONAL_RAYS[from];
            long moves = 0L;
            for (int i = 0; i < 4; i++) {
                moves |= getDiagonalRay(rays[i], occupied, from) & ~ownPieces;
            }
            moveList.addAll(getMoveList(moves, from, board));
            piece &= piece - 1;
        }
        return moveList;
    }

    private static long getDiagonalRay(long ray, long occupied, int from) {
        long blockers = ray & occupied;

        if (blockers == 0) {
            return ray;
        }

        long blockerMask;
        if (63 - Long.numberOfTrailingZeros(blockers) < from) {
            int blockerSquare = Long.numberOfTrailingZeros(blockers);
            blockerMask = (1L << blockerSquare + 1) - 1;
        } else {
            int blockerSquare = 63 - Long.numberOfLeadingZeros(blockers);
            blockerMask = ~((1L << blockerSquare) - 1);
        }

        return ray & blockerMask;
    }

    public static ArrayList<Move> generateWhitePawnMoves(Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long pawns = board.getBitboard(0);
        long emptyNoPromotion = board.getEmptySquares() & ~(0b11111111L << 56);
        long emptyPromotion = board.getEmptySquares() & (0b11111111L << 56);
        long blackPiecesNoPromotion = board.getBlackPieces() & ~(0b11111111L << 56);
        long blackPiecesPromotion = board.getBlackPieces() & (0b11111111L << 56);
        long fileMask = 0x101010101010101L;
        long enPassantSquare = 1L << (63 - board.getEnPassantSquare());

        moveList.addAll(generateWhiteSinglePawnPushMoves(pawns, emptyNoPromotion, board));
        moveList.addAll(generateWhiteDoublePawnPushMoves(pawns, emptyNoPromotion, board));
        moveList.addAll(
                generateWhitePawnCaptureMoves(pawns, emptyNoPromotion, blackPiecesNoPromotion, fileMask, board));
        moveList.addAll(generateWhitePawnEnPassantMoves(pawns, emptyNoPromotion, blackPiecesNoPromotion, fileMask,
                enPassantSquare, board));
        moveList.addAll(generateWhitePawnPromotionMoves(pawns, emptyPromotion, blackPiecesPromotion, fileMask, board));
        return moveList;
    }

    public static ArrayList<Move> generateBlackPawnMoves(Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long pawns = board.getBitboard(6);
        long emptyNoPromotion = board.getEmptySquares() & ~0b11111111;
        long emptyPromotion = board.getEmptySquares() & 0b11111111;
        long whitePiecesNoPromotion = board.getWhitePieces() & ~0b11111111;
        long whitePiecesPromotion = board.getWhitePieces() & 0b11111111;
        long fileMask = 0x101010101010101L;
        long enPassantSquare = 1L << (63 - board.getEnPassantSquare());

        moveList.addAll(generateBlackSinglePawnPushMoves(pawns, emptyNoPromotion, board));
        moveList.addAll(generateBlackDoublePawnPushMoves(pawns, emptyNoPromotion, board));
        moveList.addAll(
                generateBlackPawnCaptureMoves(pawns, emptyNoPromotion, whitePiecesNoPromotion, fileMask, board));
        moveList.addAll(generateBlackPawnEnPassantMoves(pawns, emptyNoPromotion, whitePiecesNoPromotion, fileMask,
                enPassantSquare, board));
        moveList.addAll(generateBlackPawnPromotionMoves(pawns, emptyPromotion, whitePiecesPromotion, fileMask, board));
        return moveList;
    }

    private static ArrayList<Move> getPawnMoveList(long moves, int shift, int flag, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        while (moves != 0) {
            int to = 63 - Long.numberOfTrailingZeros(moves);
            if (board.getPieceAtSquare(to) != 5 || board.getPieceAtSquare(to) != 11) {
                moveList.add(new Move(to + shift, to, flag));
            }
            moves &= moves - 1;
        }
        return moveList;
    }

    private static ArrayList<Move> generateWhiteSinglePawnPushMoves(long pawns, long emptyNoPromotion, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long singlePushMoves = (pawns << 8) & emptyNoPromotion;
        moveList.addAll(getPawnMoveList(singlePushMoves, 8, Move.NORMAL, board));
        return moveList;
    }

    private static ArrayList<Move> generateWhiteDoublePawnPushMoves(long pawns, long emptyNoPromotion, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long bothSquaresFree = (emptyNoPromotion >>> 16) & (emptyNoPromotion >>> 24) & 0b11111111;
        long doublePushMoves = (pawns << 16) & (bothSquaresFree << 24);
        moveList.addAll(getPawnMoveList(doublePushMoves, 16, Move.NORMAL, board));
        return moveList;
    }

    private static ArrayList<Move> generateWhitePawnCaptureMoves(long pawns, long emptyNoPromotion,
            long blackPiecesNoPromotion,
            long fileMask, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long leftCaptureMoves = (pawns << 9) & (blackPiecesNoPromotion & ~fileMask);
        long rightCaptureMoves = (pawns << 7) & (blackPiecesNoPromotion & ~(fileMask << 7));
        moveList.addAll(getPawnMoveList(leftCaptureMoves, 9, Move.NORMAL, board));
        moveList.addAll(getPawnMoveList(rightCaptureMoves, 7, Move.NORMAL, board));
        return moveList;
    }

    private static ArrayList<Move> generateWhitePawnEnPassantMoves(long pawns, long emptyNoPromotion,
            long blackPiecesNoPromotion,
            long fileMask, long enPassantSquare, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long leftEnPassantMoves = (pawns << 9) & (enPassantSquare & ~fileMask);
        long rightEnPassantMoves = (pawns << 7) & (enPassantSquare & ~(fileMask << 7));
        moveList.addAll(getPawnMoveList(leftEnPassantMoves, 9, Move.EN_PASSANT, board));
        moveList.addAll(getPawnMoveList(rightEnPassantMoves, 7, Move.EN_PASSANT, board));
        return moveList;
    }

    private static ArrayList<Move> generateWhitePawnPromotionMoves(long pawns, long emptyPromotion,
            long blackPiecesPromotion, long fileMask, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long singlePushMoves = (pawns << 8) & emptyPromotion;
        long leftCaptureMoves = (pawns << 9) & (blackPiecesPromotion & ~fileMask);
        long rightCaptureMoves = (pawns << 7) & (blackPiecesPromotion & ~(fileMask << 7));
        for (int i = 0; i < 4; i++) {
            moveList.addAll(getPawnMoveList(singlePushMoves, 8, 1 + (4 * i), board));
            moveList.addAll(getPawnMoveList(leftCaptureMoves, 9, 1 + (4 * i), board));
            moveList.addAll(getPawnMoveList(rightCaptureMoves, 7, 1 + (4 * i), board));
        }
        return moveList;
    }

    private static ArrayList<Move> generateBlackSinglePawnPushMoves(long pawns, long emptyNoPromotion, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long singlePushMoves = (pawns >>> 8) & emptyNoPromotion;
        moveList.addAll(getPawnMoveList(singlePushMoves, -8, Move.NORMAL, board));
        return moveList;
    }

    private static ArrayList<Move> generateBlackDoublePawnPushMoves(long pawns, long emptyNoPromotion, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long bothSquaresFree = (emptyNoPromotion >>> 40) & (emptyNoPromotion >>> 32) & 0b11111111;
        long doublePushMoves = (pawns >>> 16) & (bothSquaresFree << 32);
        moveList.addAll(getPawnMoveList(doublePushMoves, -16, Move.NORMAL, board));
        return moveList;
    }

    private static ArrayList<Move> generateBlackPawnCaptureMoves(long pawns, long emptyNoPromotion,
            long blackPiecesNoPromotion,
            long fileMask, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long leftCaptureMoves = (pawns >>> 7) & (blackPiecesNoPromotion & ~fileMask);
        long rightCaptureMoves = (pawns >>> 9) & (blackPiecesNoPromotion & ~(fileMask << 7));
        moveList.addAll(getPawnMoveList(leftCaptureMoves, -7, Move.NORMAL, board));
        moveList.addAll(getPawnMoveList(rightCaptureMoves, -9, Move.NORMAL, board));
        return moveList;
    }

    private static ArrayList<Move> generateBlackPawnEnPassantMoves(long pawns, long emptyNoPromotion,
            long blackPiecesNoPromotion,
            long fileMask, long enPassantSquare, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long leftEnPassantMoves = (pawns >>> 7) & (enPassantSquare & ~fileMask);
        long rightEnPassantMoves = (pawns >>> 9) & (enPassantSquare & ~(fileMask << 7));
        moveList.addAll(getPawnMoveList(leftEnPassantMoves, -7, Move.EN_PASSANT, board));
        moveList.addAll(getPawnMoveList(rightEnPassantMoves, -9, Move.EN_PASSANT, board));
        return moveList;
    }

    private static ArrayList<Move> generateBlackPawnPromotionMoves(long pawns, long emptyPromotion,
            long blackPiecesPromotion, long fileMask, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long singlePushMoves = (pawns >>> 8) & emptyPromotion;
        long leftCaptureMoves = (pawns >>> 7) & (blackPiecesPromotion & ~fileMask);
        long rightCaptureMoves = (pawns >>> 9) & (blackPiecesPromotion & ~(fileMask << 7));
        for (int i = 0; i < 4; i++) {
            moveList.addAll(getPawnMoveList(singlePushMoves, -8, 1 + (4 * i), board));
            moveList.addAll(getPawnMoveList(leftCaptureMoves, -7, 1 + (4 * i), board));
            moveList.addAll(getPawnMoveList(rightCaptureMoves, -9, 1 + (4 * i), board));
        }
        return moveList;
    }
}