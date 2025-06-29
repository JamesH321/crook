package com.github.jamesh321.chessengine;

public class MoveExecutor {
    public static void makeMove(Board board, Move move) {
        int from = move.getFrom();
        int to = move.getTo();
        long fromMask = 0x8000000000000000L >>> from;
        long toMask = 0x8000000000000000L >>> to;
        int fromPiece = board.getPieceAtSquare(from);
        int toPiece = board.getPieceAtSquare(to);

        switch (move.getSpecialMove()) {
            case 0: // None
                movePiece(board, fromPiece, fromMask, toMask);
                takePiece(board, toPiece, toMask);
                break;
            case 1: // Piece promotion
                int promotionPiece = getPieceIndex(move.getPromotionPiece(), board.isWhiteTurn());
                movePiece(board, promotionPiece, fromMask, toMask);
                takePiece(board, toPiece, toMask);
                takePiece(board, fromPiece, fromMask);
                break;
            case 2: // En passant
                movePiece(board, fromPiece, fromMask, toMask);
                takeEnPassantPiece(board, toMask);
                break;
            case 3: // Castling
                castle(board, to, fromPiece, fromMask, toMask);
                break;
        }

        setCastlingRights(board, fromPiece, from);
        setEnPassantSquare(board, fromPiece, from, to);
        setHalfmoveClock(board, fromPiece, toPiece);
        incrementFullmoveCounter(board);
        board.updateCompositeBitboards();
        board.setWhiteTurn(!board.isWhiteTurn());
    }

    public static void movePiece(Board board, int fromPiece, long fromMask, long toMask) {
        long newBitboard = (board.getBitboard(fromPiece) & ~fromMask) | toMask;
        board.setBitboard(fromPiece, newBitboard);
    }

    public static void takePiece(Board board, int toPiece, long toMask) {
        if (toPiece > -1) {
            long newBitboard = board.getBitboard(toPiece) & ~toMask;
            board.setBitboard(toPiece, newBitboard);
        }
    }

    public static int getPieceIndex(int promotionPiece, boolean whiteTurn) {
        int pieceIndex = 4 - promotionPiece;
        if (!whiteTurn) {
            pieceIndex += 6;
        }
        return pieceIndex;
    }

    public static void takeEnPassantPiece(Board board, long toMask) {
        int toPiece;
        if (board.isWhiteTurn()) {
            toPiece = 6;
            toMask >>>= 8;
        } else {
            toPiece = 0;
            toMask <<= 8;
        }
        takePiece(board, toPiece, toMask);
    }

    public static void castle(Board board, int to, int fromPiece, long fromMask, long toMask) {
        int rook = 3;
        if (!board.isWhiteTurn()) {
            rook = 9;
        }
        movePiece(board, fromPiece, fromMask, toMask);

        if (to == 58 || to == 2) {
            movePiece(board, rook, fromMask << 4, toMask >>> 1);
        } else {
            movePiece(board, rook, fromMask >>> 3, toMask << 1);
        }

        board.setCastlingRights(0);
    }

    public static void setEnPassantSquare(Board board, int fromPiece, int from, int to) {
        int enPassantSquare = -1;

        if (fromPiece == 0 && from - to == 16) {
            enPassantSquare = to + 8;
        } else if (fromPiece == 6 && from - to == -16) {
            enPassantSquare = to - 8;
        }
        board.setEnPassantSquare(enPassantSquare);
    }

    public static void setCastlingRights(Board board, int fromPiece, int from) {
        int castlingRights = board.getCastlingRights();
        switch (fromPiece) {
            case 3:
            case 9:
                castlingRights &= from / 8 == 0 ? ~(fromPiece - 1) : ~((fromPiece - 1) >> 1);
                break;
            case 5:
                castlingRights &= ~0b0011;
                break;
            case 11:
                castlingRights &= ~0b1100;
                break;
        }
        board.setCastlingRights(castlingRights);
    }

    public static void setHalfmoveClock(Board board, int fromPiece, int toPiece) {
        if (fromPiece == 0 || fromPiece == 6 || toPiece != -1) {
            board.setHalfmoveClock(0);
        } else {
            board.setHalfmoveClock(board.getHalfmoveClock() + 1);
        }
    }

    public static void incrementFullmoveCounter(Board board) {
        if (!board.isWhiteTurn()) {
            board.setFullmoveNumber(board.getFullmoveNumber() + 1);
        }
    }
}