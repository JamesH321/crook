package com.github.jamesh321.chessengine;

import java.util.ArrayList;
import java.util.Arrays;

public class MoveGenerator {
    public static ArrayList<Move> generateLegalMoves(Board board) {
        long kingBitboard = board.isWhiteTurn() ? board.getBitboard(5) : board.getBitboard(11);
        int kingSquare = kingBitboard != 0 ? 63 - Long.numberOfTrailingZeros(kingBitboard) : -1;
        long kingAttackers = getAttackers(kingSquare, board);
        boolean inCheck = kingAttackers != 0;
        ArrayList<Move> legalMoveList = new ArrayList<>();

        for (Move move : generatePseudoLegalMoves(board)) {
            if (move.getTo() == kingSquare) {
                continue;
            }

            boolean isLegal = true;
            if (move.getFrom() == kingSquare && isSquareAttacked(move.getTo(), board)) {
                isLegal = false;
            }

            if (inCheck) {
                if (move.getFlag() == Move.CASTLE) {
                    isLegal = false;
                } else if (!attackerTaken(move, kingAttackers, board) && !checkBlocked(move, kingSquare, board)
                        && move.getFrom() != kingSquare) {
                    isLegal = false;
                } else if (move.getFrom() != kingSquare && isPiecePinned(move, kingSquare, board)) {
                    isLegal = false;
                } else if (move.getFrom() == kingSquare && isSquareAttackedAfterMove(move, board)) {
                    isLegal = false;
                }
            } else {
                if (move.getFrom() != kingSquare && isPiecePinned(move, kingSquare, board)) {
                    isLegal = false;
                } else if (move.getFlag() == Move.CASTLE && !isLegalCastle(move, board)) {
                    isLegal = false;
                }
            }

            if (isLegal) {
                legalMoveList.add(move);
            }
        }
        return legalMoveList;
    }

    private static boolean isSquareAttackedAfterMove(Move move, Board board) {
        long occupied = board.getOccupiedSquares();
        occupied &= ~(1L << (63 - move.getFrom()));
        occupied |= 1L << (63 - move.getTo());
        board.setOccupiedSquares(occupied);
        boolean squareAttacked = isSquareAttacked(move.getTo(), board);
        board.updateOccupiedSquares();
        return squareAttacked;
    }

    private static boolean checkBlocked(Move move, int kingSquare, Board board) {
        long occupied = board.getOccupiedSquares();
        occupied &= ~(1L << (63 - move.getFrom()));
        occupied |= 1L << (63 - move.getTo());
        board.setOccupiedSquares(occupied);
        long attackers = getAttackers(kingSquare, board);

        if (attackers == 0) {
            board.updateOccupiedSquares();
            return true;
        }

        board.updateOccupiedSquares();
        return false;
    }

    private static boolean attackerTaken(Move move, long attackers, Board board) {
        if (Long.bitCount(attackers) == 1) {
            if (move.getFlag() != Move.EN_PASSANT) {
                if (move.getTo() == Long.numberOfLeadingZeros(attackers)) {
                    return true;
                }
            } else {
                int takenPiecequare = board.isWhiteTurn() ? board.getEnPassantSquare() + 8
                        : board.getEnPassantSquare() - 8;
                if (takenPiecequare == Long.numberOfLeadingZeros(attackers)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static long getAttackers(int square, Board board) {
        long attackers = 0L;
        long[] pieces = board.isWhiteTurn() ? Arrays.copyOfRange(board.getBitboards(), 6, 12)
                : Arrays.copyOfRange(board.getBitboards(), 0, 6);
        long occupied = board.getOccupiedSquares();

        // Pawns
        if (board.isWhiteTurn()) {
            attackers |= LookupTables.WHITE_PAWN_ATTACKS[square] & pieces[0];
        } else {
            attackers |= LookupTables.BLACK_PAWN_ATTACKS[square] & pieces[0];
        }

        // Knights
        attackers |= LookupTables.KNIGHT_MOVES[square] & pieces[1];

        // Bishops
        for (int direction = 0; direction < 4; direction++) {
            attackers |= getRay(LookupTables.DIAGONAL_RAYS[square][direction], occupied, square) & pieces[2];
        }

        // Rooks
        for (int direction = 0; direction < 4; direction++) {
            attackers |= getRay(LookupTables.STRAIGHT_RAYS[square][direction], occupied, square) & pieces[3];
        }

        // Queens
        for (int direction = 0; direction < 4; direction++) {
            attackers |= getRay(LookupTables.STRAIGHT_RAYS[square][direction], occupied, square) & pieces[4];
            attackers |= getRay(LookupTables.DIAGONAL_RAYS[square][direction], occupied, square) & pieces[4];

        }

        // King
        attackers |= LookupTables.KING_MOVES[square] & pieces[5];

        return attackers;
    }

    private static boolean isPiecePinned(Move move, int kingSquare, Board board) {
        long occupied = board.getOccupiedSquares();
        if (move.getFlag() == Move.EN_PASSANT) {
            int takenPiecequare = board.isWhiteTurn() ? board.getEnPassantSquare() + 8 : board.getEnPassantSquare() - 8;
            occupied &= ~(1L << (63 - takenPiecequare));
        }
        occupied &= ~(1L << (63 - move.getFrom()));
        occupied |= 1L << (63 - move.getTo());

        board.setOccupiedSquares(occupied);
        long attackers = getAttackers(kingSquare, board);

        if (attackers != 0 && !attackerTaken(move, attackers, board)) {
            board.updateOccupiedSquares();
            return true;
        }

        board.updateOccupiedSquares();
        return false;
    }

    public static boolean isSquareAttacked(int square, Board board) {
        return getAttackers(square, board) != 0;
    }

    private static boolean isLegalCastle(Move move, Board board) {
        int castleDirection = move.getTo() - move.getFrom();
        if (castleDirection == 2 && isSquareAttacked(move.getFrom() + 1, board)) {
            return false;
        } else if (castleDirection == -2 && isSquareAttacked(move.getFrom() - 1, board)) {
            return false;
        }

        return true;
    }

    public static ArrayList<Move> generatePseudoLegalMoves(Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        moveList.addAll(generatePawnMoves(board));
        moveList.addAll(generateKnightMoves(board));
        moveList.addAll(generateBishopMoves(board));
        moveList.addAll(generateRookMoves(board));
        moveList.addAll(generateQueenMoves(board));
        moveList.addAll(generateKingMoves(board));
        return moveList;
    }

    private static ArrayList<Move> getMoveList(long moves, int from, int flag) {
        ArrayList<Move> moveList = new ArrayList<>();
        while (moves != 0) {
            int to = 63 - Long.numberOfTrailingZeros(moves);
            moveList.add(new Move(from, to, flag));
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
        long knights = board.isWhiteTurn() ? board.getBitboard(1) : board.getBitboard(7);
        long movable = board.isWhiteTurn() ? (board.getEmptySquares() | board.getBlackPieces())
                : (board.getEmptySquares() | board.getWhitePieces());

        while (knights != 0) {
            int from = 63 - Long.numberOfTrailingZeros(knights);
            long moves = LookupTables.KNIGHT_MOVES[from] & movable;
            moveList.addAll(getMoveList(moves, from, Move.NORMAL));
            knights &= knights - 1;
        }
        return moveList;
    }

    public static ArrayList<Move> generateBishopMoves(Board board) {
        long bishops = board.isWhiteTurn() ? board.getBitboard(2) : board.getBitboard(8);
        long ownPieces = board.isWhiteTurn() ? board.getWhitePieces() : board.getBlackPieces();
        long occupied = board.getOccupiedSquares();

        return getSlidingMoves(LookupTables.DIAGONAL_RAYS, bishops, occupied, ownPieces, board);
    }

    public static ArrayList<Move> generateRookMoves(Board board) {
        long rooks = board.isWhiteTurn() ? board.getBitboard(3) : board.getBitboard(9);
        long ownPieces = board.isWhiteTurn() ? board.getWhitePieces() : board.getBlackPieces();
        long occupied = board.getOccupiedSquares();

        return getSlidingMoves(LookupTables.STRAIGHT_RAYS, rooks, occupied, ownPieces, board);
    }

    public static ArrayList<Move> generateQueenMoves(Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long queens = board.isWhiteTurn() ? board.getBitboard(4) : board.getBitboard(10);
        long ownPieces = board.isWhiteTurn() ? board.getWhitePieces() : board.getBlackPieces();
        long occupied = board.getOccupiedSquares();

        moveList.addAll(getSlidingMoves(LookupTables.STRAIGHT_RAYS, queens, occupied, ownPieces, board));
        moveList.addAll(getSlidingMoves(LookupTables.DIAGONAL_RAYS, queens, occupied, ownPieces, board));

        return moveList;
    }

    public static ArrayList<Move> generateKingMoves(Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long king = board.isWhiteTurn() ? board.getBitboard(5) : board.getBitboard(11);
        long empty = board.getEmptySquares();
        long movable = board.isWhiteTurn() ? empty | board.getBlackPieces()
                : empty | board.getWhitePieces();
        int from = 63 - Long.numberOfTrailingZeros(king);
        int castlingRights = board.isWhiteTurn() ? board.getCastlingRights() & 0b11 : board.getCastlingRights() >> 2;

        moveList.addAll(getMoveList(LookupTables.KING_MOVES[from] & movable, from, Move.NORMAL));
        moveList.addAll(getMoveList(getcastleMoves(king, empty, from, castlingRights), from, Move.CASTLE));

        return moveList;
    }

    private static long getcastleMoves(long king, long empty, int from, int castlingRights) {
        long QUEENSIDE_MASK = 0b01110000L << 60 - from;
        long KINGSIDE_MASK = 0b00000110L << 60 - from;
        long moves = 0L;

        moves |= ((QUEENSIDE_MASK & empty) == QUEENSIDE_MASK) && ((castlingRights & 0b10) == 0b10) ? king << 2 : 0;
        moves |= ((KINGSIDE_MASK & empty) == KINGSIDE_MASK) && ((castlingRights & 1) == 1) ? king >>> 2 : 0;
        return moves;
    }

    private static ArrayList<Move> getSlidingMoves(long[][] rayLookup, long piece, long occupied, long ownPieces,
            Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        while (piece != 0) {
            int from = 63 - Long.numberOfTrailingZeros(piece);
            long[] rays = rayLookup[from];
            long moves = 0L;
            for (int i = 0; i < 4; i++) {
                moves |= getRay(rays[i], occupied, from) & ~ownPieces;
            }
            moveList.addAll(getMoveList(moves, from, Move.NORMAL));
            piece &= piece - 1;
        }
        return moveList;
    }

    private static long getRay(long ray, long occupied, int from) {
        long blockers = ray & occupied;

        if (blockers == 0) {
            return ray;
        }

        long blockerMask;
        if (63 - Long.numberOfTrailingZeros(blockers) < from) {
            int blockerSquare = Long.numberOfTrailingZeros(blockers);
            blockerMask = ((1L << blockerSquare) - 1) | (1L << blockerSquare);
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
        long enPassantSquare = board.getEnPassantSquare() != -1 ? 1L << (63 - board.getEnPassantSquare()) : 0;

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
        long enPassantSquare = board.getEnPassantSquare() != -1 ? 1L << (63 - board.getEnPassantSquare()) : 0;

        moveList.addAll(generateBlackSinglePawnPushMoves(pawns, emptyNoPromotion, board));
        moveList.addAll(generateBlackDoublePawnPushMoves(pawns, emptyNoPromotion, board));
        moveList.addAll(
                generateBlackPawnCaptureMoves(pawns, emptyNoPromotion, whitePiecesNoPromotion, fileMask, board));
        moveList.addAll(generateBlackPawnEnPassantMoves(pawns, emptyNoPromotion, whitePiecesNoPromotion, fileMask,
                enPassantSquare, board));
        moveList.addAll(generateBlackPawnPromotionMoves(pawns, emptyPromotion, whitePiecesPromotion, fileMask, board));
        return moveList;
    }

    private static ArrayList<Move> getPawnMoveList(long moves, int shift, int flag) {
        ArrayList<Move> moveList = new ArrayList<>();
        while (moves != 0) {
            int to = 63 - Long.numberOfTrailingZeros(moves);
            moveList.add(new Move(to + shift, to, flag));
            moves &= moves - 1;
        }
        return moveList;
    }

    private static ArrayList<Move> generateWhiteSinglePawnPushMoves(long pawns, long emptyNoPromotion, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long singlePushMoves = (pawns << 8) & emptyNoPromotion;
        moveList.addAll(getPawnMoveList(singlePushMoves, 8, Move.NORMAL));
        return moveList;
    }

    private static ArrayList<Move> generateWhiteDoublePawnPushMoves(long pawns, long emptyNoPromotion, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long bothSquaresFree = (emptyNoPromotion >>> 16) & (emptyNoPromotion >>> 24) & 0b11111111;
        long doublePushMoves = (pawns << 16) & (bothSquaresFree << 24);
        moveList.addAll(getPawnMoveList(doublePushMoves, 16, Move.NORMAL));
        return moveList;
    }

    private static ArrayList<Move> generateWhitePawnCaptureMoves(long pawns, long emptyNoPromotion,
            long blackPiecesNoPromotion,
            long fileMask, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long leftCaptureMoves = (pawns << 9) & (blackPiecesNoPromotion & ~fileMask);
        long rightCaptureMoves = (pawns << 7) & (blackPiecesNoPromotion & ~(fileMask << 7));
        moveList.addAll(getPawnMoveList(leftCaptureMoves, 9, Move.NORMAL));
        moveList.addAll(getPawnMoveList(rightCaptureMoves, 7, Move.NORMAL));
        return moveList;
    }

    private static ArrayList<Move> generateWhitePawnEnPassantMoves(long pawns, long emptyNoPromotion,
            long blackPiecesNoPromotion,
            long fileMask, long enPassantSquare, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long leftEnPassantMoves = (pawns << 9) & (enPassantSquare & ~fileMask);
        long rightEnPassantMoves = (pawns << 7) & (enPassantSquare & ~(fileMask << 7));
        moveList.addAll(getPawnMoveList(leftEnPassantMoves, 9, Move.EN_PASSANT));
        moveList.addAll(getPawnMoveList(rightEnPassantMoves, 7, Move.EN_PASSANT));
        return moveList;
    }

    private static ArrayList<Move> generateWhitePawnPromotionMoves(long pawns, long emptyPromotion,
            long blackPiecesPromotion, long fileMask, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long singlePushMoves = (pawns << 8) & emptyPromotion;
        long leftCaptureMoves = (pawns << 9) & (blackPiecesPromotion & ~fileMask);
        long rightCaptureMoves = (pawns << 7) & (blackPiecesPromotion & ~(fileMask << 7));
        for (int i = 0; i < 4; i++) {
            moveList.addAll(getPawnMoveList(singlePushMoves, 8, 1 + (4 * i)));
            moveList.addAll(getPawnMoveList(leftCaptureMoves, 9, 1 + (4 * i)));
            moveList.addAll(getPawnMoveList(rightCaptureMoves, 7, 1 + (4 * i)));
        }
        return moveList;
    }

    private static ArrayList<Move> generateBlackSinglePawnPushMoves(long pawns, long emptyNoPromotion, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long singlePushMoves = (pawns >>> 8) & emptyNoPromotion;
        moveList.addAll(getPawnMoveList(singlePushMoves, -8, Move.NORMAL));
        return moveList;
    }

    private static ArrayList<Move> generateBlackDoublePawnPushMoves(long pawns, long emptyNoPromotion, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long bothSquaresFree = (emptyNoPromotion >>> 40) & (emptyNoPromotion >>> 32) & 0b11111111;
        long doublePushMoves = (pawns >>> 16) & (bothSquaresFree << 32);
        moveList.addAll(getPawnMoveList(doublePushMoves, -16, Move.NORMAL));
        return moveList;
    }

    private static ArrayList<Move> generateBlackPawnCaptureMoves(long pawns, long emptyNoPromotion,
            long blackPiecesNoPromotion,
            long fileMask, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long leftCaptureMoves = (pawns >>> 7) & (blackPiecesNoPromotion & ~fileMask);
        long rightCaptureMoves = (pawns >>> 9) & (blackPiecesNoPromotion & ~(fileMask << 7));
        moveList.addAll(getPawnMoveList(leftCaptureMoves, -7, Move.NORMAL));
        moveList.addAll(getPawnMoveList(rightCaptureMoves, -9, Move.NORMAL));
        return moveList;
    }

    private static ArrayList<Move> generateBlackPawnEnPassantMoves(long pawns, long emptyNoPromotion,
            long blackPiecesNoPromotion,
            long fileMask, long enPassantSquare, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long leftEnPassantMoves = (pawns >>> 7) & (enPassantSquare & ~fileMask);
        long rightEnPassantMoves = (pawns >>> 9) & (enPassantSquare & ~(fileMask << 7));
        moveList.addAll(getPawnMoveList(leftEnPassantMoves, -7, Move.EN_PASSANT));
        moveList.addAll(getPawnMoveList(rightEnPassantMoves, -9, Move.EN_PASSANT));
        return moveList;
    }

    private static ArrayList<Move> generateBlackPawnPromotionMoves(long pawns, long emptyPromotion,
            long blackPiecesPromotion, long fileMask, Board board) {
        ArrayList<Move> moveList = new ArrayList<>();
        long singlePushMoves = (pawns >>> 8) & emptyPromotion;
        long leftCaptureMoves = (pawns >>> 7) & (blackPiecesPromotion & ~fileMask);
        long rightCaptureMoves = (pawns >>> 9) & (blackPiecesPromotion & ~(fileMask << 7));
        for (int i = 0; i < 4; i++) {
            moveList.addAll(getPawnMoveList(singlePushMoves, -8, 1 + (4 * i)));
            moveList.addAll(getPawnMoveList(leftCaptureMoves, -7, 1 + (4 * i)));
            moveList.addAll(getPawnMoveList(rightCaptureMoves, -9, 1 + (4 * i)));
        }
        return moveList;
    }
}