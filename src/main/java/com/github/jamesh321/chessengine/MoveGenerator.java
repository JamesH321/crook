package com.github.jamesh321.chessengine;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Generates legal and pseudo-legal moves for a position in a chess game.
 * It provides methods to generate all possible moves from a given board state
 * and to filter them down to only the legal moves (those that do not leave the
 * king in check).
 */
public final class MoveGenerator {

    private MoveGenerator() {
        // private constructor to prevent instantiation of this utility class
    }

    /**
     * Generates a list of all legal moves for the current board state.
     * A legal move is a pseudo-legal move that does not leave the king in check.
     *
     * @param board The current board state.
     * @return An ArrayList of legal moves.
     */
    public static ArrayList<Move> generateLegalMoves(Board board) {
        ArrayList<Move> legalMoveList = new ArrayList<>();

        long kingBitboard = board.isWhiteTurn() ? board.getBitboard(Piece.WHITE_KING)
                : board.getBitboard(Piece.BLACK_KING);
        int kingSquare = Long.numberOfLeadingZeros(kingBitboard);
        long kingAttackers = getAttackers(kingSquare, board);

        boolean inCheck = kingAttackers != 0;

        for (Move move : generatePseudoLegalMoves(board)) {

            if (isLegalMove(move, kingSquare, kingAttackers, inCheck, board)) {
                legalMoveList.add(move);
            }
        }

        return legalMoveList;
    }

    /**
     * Generates a list of all pseudo-legal moves for the current board state.
     * Pseudo-legal moves are moves that are valid based on the piece type,
     * but may leave the king in check.
     *
     * @param board The current board state.
     * @return An ArrayList of pseudo-legal moves.
     */
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

    /**
     * Generates pseudo-legal pawn moves for the current board state.
     * Includes single pushes, double pushes, captures, and en passant.
     *
     * @param board The current board state.
     * @return An ArrayList of pseudo-legal pawn moves.
     */
    public static ArrayList<Move> generatePawnMoves(Board board) {
        ArrayList<Move> moveList = new ArrayList<>();

        boolean whiteTurn = board.isWhiteTurn();
        long pawns = whiteTurn ? board.getBitboard(Piece.WHITE_PAWN) : board.getBitboard(Piece.BLACK_PAWN);
        long emptySquares = board.getEmptySquares();
        long enemySquares = whiteTurn ? board.getBlackPieces() : board.getWhitePieces();
        long enPassantSquare = 1L << 63 - board.getEnPassantSquare();
        int secondToLastRank = whiteTurn ? 1 : 6;

        while (pawns != 0) {
            int from = 63 - Long.numberOfTrailingZeros(pawns);
            int rank = from / 8;

            long[] moves = generatePawnMovesBitboard(whiteTurn, from, emptySquares, enemySquares, enPassantSquare);

            if (rank != secondToLastRank) {
                moveList.addAll(getMoveList(moves[0], from, Move.NORMAL));
                moveList.addAll(getMoveList(moves[1], from, Move.EN_PASSANT));
            } else {
                moveList.addAll(getMoveList(moves[0], from, Move.QUEEN_PROMOTION));
                moveList.addAll(getMoveList(moves[0], from, Move.ROOK_PROMOTION));
                moveList.addAll(getMoveList(moves[0], from, Move.BISHOP_PROMOTION));
                moveList.addAll(getMoveList(moves[0], from, Move.KNIGHT_PROMOTION));
            }

            pawns &= pawns - 1;
        }

        return moveList;
    }

    /**
     * Generates pseudo-legal knight moves for the current board state.
     *
     * @param board The current board state.
     * @return An ArrayList of pseudo-legal knight moves.
     */
    public static ArrayList<Move> generateKnightMoves(Board board) {
        ArrayList<Move> moveList = new ArrayList<>();

        long knights = board.isWhiteTurn() ? board.getBitboard(Piece.WHITE_KNIGHT)
                : board.getBitboard(Piece.BLACK_KNIGHT);
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

    /**
     * Generates pseudo-legal bishop moves for the current board state.
     *
     * @param board The current board state.
     * @return An ArrayList of pseudo-legal bishop moves.
     */
    public static ArrayList<Move> generateBishopMoves(Board board) {
        long bishops = board.isWhiteTurn() ? board.getBitboard(Piece.WHITE_BISHOP)
                : board.getBitboard(Piece.BLACK_BISHOP);
        long ownPieces = board.isWhiteTurn() ? board.getWhitePieces() : board.getBlackPieces();
        long occupied = board.getOccupiedSquares();

        return getSlidingMoves(LookupTables.DIAGONAL_RAYS, bishops, occupied, ownPieces, board);
    }

    /**
     * Generates pseudo-legal rook moves for the current board state.
     *
     * @param board The current board state.
     * @return An ArrayList of pseudo-legal rook moves.
     */
    public static ArrayList<Move> generateRookMoves(Board board) {
        long rooks = board.isWhiteTurn() ? board.getBitboard(Piece.WHITE_ROOK) : board.getBitboard(Piece.BLACK_ROOK);
        long ownPieces = board.isWhiteTurn() ? board.getWhitePieces() : board.getBlackPieces();
        long occupied = board.getOccupiedSquares();

        return getSlidingMoves(LookupTables.STRAIGHT_RAYS, rooks, occupied, ownPieces, board);
    }

    /**
     * Generates pseudo-legal queen moves for the current board state.
     *
     * @param board The current board state.
     * @return An ArrayList of pseudo-legal queen moves.
     */
    public static ArrayList<Move> generateQueenMoves(Board board) {
        ArrayList<Move> moveList = new ArrayList<>();

        long queens = board.isWhiteTurn() ? board.getBitboard(Piece.WHITE_QUEEN) : board.getBitboard(Piece.BLACK_QUEEN);
        long ownPieces = board.isWhiteTurn() ? board.getWhitePieces() : board.getBlackPieces();
        long occupied = board.getOccupiedSquares();

        moveList.addAll(getSlidingMoves(LookupTables.STRAIGHT_RAYS, queens, occupied, ownPieces, board));
        moveList.addAll(getSlidingMoves(LookupTables.DIAGONAL_RAYS, queens, occupied, ownPieces, board));

        return moveList;
    }

    /**
     * Generates pseudo-legal king moves for the current board state.
     * Includes normal moves and castling.
     *
     * @param board The current board state.
     * @return An ArrayList of pseudo-legal king moves.
     */
    public static ArrayList<Move> generateKingMoves(Board board) {
        ArrayList<Move> moveList = new ArrayList<>();

        long kingBitboard = board.isWhiteTurn() ? board.getBitboard(Piece.WHITE_KING)
                : board.getBitboard(Piece.BLACK_KING);
        long emptySquares = board.getEmptySquares();
        long movable = board.isWhiteTurn() ? emptySquares | board.getBlackPieces()
                : emptySquares | board.getWhitePieces();
        int from = 63 - Long.numberOfTrailingZeros(kingBitboard);
        int castlingRights = board.isWhiteTurn() ? board.getCastlingRights() & 0b11 : board.getCastlingRights() >> 2;

        moveList.addAll(getMoveList(LookupTables.KING_MOVES[from] & movable, from, Move.NORMAL));
        moveList.addAll(
                getMoveList(getcastleMoves(kingBitboard, emptySquares, from, castlingRights), from, Move.CASTLE));

        return moveList;
    }

    /**
     * Checks if a pseudo-legal move is legal.
     * A move is legal if it does not leave the king in check.
     *
     * @param move          The move to check.
     * @param kingSquare    The square the king is on.
     * @param kingAttackers A bitboard of pieces attacking the king.
     * @param inCheck       Whether the king is currently in check.
     * @param board         The current board state.
     * @return True if the move is legal, false otherwise.
     */
    private static boolean isLegalMove(Move move, int kingSquare, long kingAttackers, boolean inCheck, Board board) {
        if (move.getTo() == kingSquare) {
            return false;
        }

        if (isKingAttackedAfterMove(move, kingSquare, board)) {
            return false;
        }

        if (isPiecePinned(move, kingSquare, board)) {
            return false;
        }

        if (inCheck) {
            if (move.getFlag() == Move.CASTLE) {
                return false;
            }

            if (move.getFrom() != kingSquare && !attackerTaken(move, kingAttackers, board)
                    && !checkBlocked(move, kingSquare, board)) {
                return false;
            }
        }

        if (!isLegalCastle(move, board)) {
            return false;
        }

        return true;

    }

    /**
     * Checks if the king is attacked after a specific move is made.
     * This is used to determine if a move is legal.
     *
     * @param move       The move to simulate.
     * @param kingSquare The square the king is on.
     * @param board      The current board state.
     * @return True if the king is attacked after the move, false otherwise.
     */
    private static boolean isKingAttackedAfterMove(Move move, int kingSquare, Board board) {
        if (move.getFrom() == kingSquare) {

            long occupied = board.getOccupiedSquares();

            occupied &= ~(1L << (63 - move.getFrom()));
            occupied |= 1L << (63 - move.getTo());

            board.setOccupiedSquares(occupied);

            boolean squareAttacked = isSquareAttacked(move.getTo(), board);

            board.updateOccupiedSquares();

            return squareAttacked;
        }

        return false;
    }

    /**
     * Checks if the piece being moved is pinned to the king.
     * A pinned piece cannot move if it would expose the king to an attack.
     *
     * @param move       The move to check.
     * @param kingSquare The square the king is on.
     * @param board      The current board state.
     * @return True if the piece is pinned, false otherwise.
     */
    private static boolean isPiecePinned(Move move, int kingSquare, Board board) {
        if (move.getFrom() != kingSquare) {

            long occupied = board.getOccupiedSquares();

            if (move.getFlag() == Move.EN_PASSANT) {
                int takenPiecequare = board.isWhiteTurn() ? board.getEnPassantSquare() + 8
                        : board.getEnPassantSquare() - 8;
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
        }

        return false;
    }

    /**
     * Checks if a castling move is legal.
     * Castling is illegal if the king is in check, or if the king or rook
     * has moved previously, or if any squares the king moves across or to
     * are under attack.
     *
     * @param move  The castling move to check.
     * @param board The current board state.
     * @return True if the castling move is legal, false otherwise.
     */
    private static boolean isLegalCastle(Move move, Board board) {
        if (move.getFlag() == Move.CASTLE) {

            int castleDirection = move.getTo() - move.getFrom();

            if (castleDirection == 2 && isSquareAttacked(move.getFrom() + 1, board)) {
                return false;
            } else if (castleDirection == -2 && isSquareAttacked(move.getFrom() - 1, board)) {
                return false;
            }

            return true;
        }

        return true;
    }

    /**
     * Checks if a given square is attacked by the opposing side.
     *
     * @param square The square to check.
     * @param board  The current board state.
     * @return True if the square is attacked, false otherwise.
     */
    public static boolean isSquareAttacked(int square, Board board) {
        return getAttackers(square, board) != 0;
    }

    /**
     * Checks if a move blocks a check.
     *
     * @param move       The move to check.
     * @param kingSquare The square the king is on.
     * @param board      The current board state.
     * @return True if the move blocks a check, false otherwise.
     */
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

    /**
     * Checks if the move captures the piece that is attacking the king.
     *
     * @param move      The move to check.
     * @param attackers A bitboard of pieces attacking the king.
     * @param board     The current board state.
     * @return True if the move captures an attacker, false otherwise.
     */
    private static boolean attackerTaken(Move move, long attackers, Board board) {
        if (Long.bitCount(attackers) != 1) {
            return false;
        }

        int attackerSquare = Long.numberOfLeadingZeros(attackers);

        if (move.getFlag() != Move.EN_PASSANT) {
            return move.getTo() == attackerSquare;
        } else {
            int takenPieceSquare = board.isWhiteTurn() ? board.getEnPassantSquare() + 8
                    : board.getEnPassantSquare() - 8;
            return takenPieceSquare == attackerSquare;
        }
    }

    /**
     * Generates a bitboard of possible pawn moves (pushes, captures, and en
     * passant) from a
     * given square.
     *
     * @param whiteTurn       Whether it is white's turn.
     * @param from            The square the pawn is on.
     * @param emptySquares    A bitboard of empty squares.
     * @param enemySquares    A bitboard of enemy occupied squares.
     * @param enPassantSquare The square where an en passant capture is possible.
     * @return A long array where the first element is a bitboard of normal moves
     *         and captures,
     *         and the second element is a bitboard of en passant moves.
     */
    private static long[] generatePawnMovesBitboard(boolean whiteTurn, int from, long emptySquares, long enemySquares,
            long enPassantSquare) {
        // 0 = normal moves and captures
        // 1 = en passant
        long[] moves = new long[2];

        if (whiteTurn) {
            // Single push
            moves[0] |= (1L << (63 - (from - 8))) & emptySquares;

            // Double push
            if (from >= 48 && from <= 55) {
                boolean singleEmpty = ((1L << (63 - (from - 8))) & emptySquares) != 0;
                boolean doubleEmpty = ((1L << (63 - (from - 16))) & emptySquares) != 0;

                if (singleEmpty && doubleEmpty) {
                    moves[0] |= (1L << (63 - (from - 16)));
                }
            }

            // Captures and en passant
            moves[0] |= LookupTables.WHITE_PAWN_ATTACKS[from] & enemySquares;
            moves[1] = LookupTables.WHITE_PAWN_ATTACKS[from] & enPassantSquare;
        } else {
            // Single push
            moves[0] |= (1L << (63 - (from + 8))) & emptySquares;

            // Double push
            if (from >= 8 && from <= 15) {
                boolean singleEmpty = ((1L << (63 - (from + 8))) & emptySquares) != 0;
                boolean doubleEmpty = ((1L << (63 - (from + 16))) & emptySquares) != 0;

                if (singleEmpty && doubleEmpty) {
                    moves[0] |= (1L << (63 - (from + 16)));
                }
            }

            // Captures and en passant
            moves[0] |= LookupTables.BLACK_PAWN_ATTACKS[from] & enemySquares;
            moves[1] |= LookupTables.BLACK_PAWN_ATTACKS[from] & enPassantSquare;
        }

        return moves;
    }

    /**
     * Generates a bitboard of possible castling moves for the king.
     *
     * @param kingBitboard   The bitboard of the king.
     * @param emptySquares   A bitboard of the empty squares.
     * @param from           The square the king is on.
     * @param castlingRights The castling rights for the current side.
     * @return A bitboard of possible castling moves.
     */
    private static long getcastleMoves(long kingBitboard, long emptySquares, int from, int castlingRights) {
        long QUEENSIDE_MASK = 0b01110000L << 60 - from;
        long KINGSIDE_MASK = 0b00000110L << 60 - from;
        long moves = 0L;

        moves |= ((QUEENSIDE_MASK & emptySquares) == QUEENSIDE_MASK) && ((castlingRights & 0b10) == 0b10)
                ? kingBitboard << 2
                : 0;
        moves |= ((KINGSIDE_MASK & emptySquares) == KINGSIDE_MASK) && ((castlingRights & 1) == 1) ? kingBitboard >>> 2
                : 0;

        return moves;
    }

    /**
     * Generates pseudo-legal moves for sliding pieces (Bishops, Rooks, Queens).
     *
     * @param rayLookup The lookup table for the piece type's rays.
     * @param piece     The bitboard of the piece type.
     * @param occupied  A bitboard of all occupied squares.
     * @param ownPieces A bitboard of the current side's pieces.
     * @param board     The current board state.
     * @return An ArrayList of pseudo-legal sliding moves.
     */
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

    /**
     * Calculates the ray for a sliding piece up until it is blocked by another
     * piece.
     *
     * @param ray      The bitboard representing the ray.
     * @param occupied A bitboard of all occupied squares.
     * @param from     The starting square of the ray.
     * @return A bitboard of the squares along the ray that are not blocked.
     */
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

    /**
     * Converts a bitboard of destination squares into a list of Move objects.
     *
     * @param moves A bitboard of destination squares.
     * @param from  The starting square of the moves.
     * @param flag  The flag for the move type (e.g., normal, capture, promotion).
     * @return An ArrayList of Move objects.
     */
    private static ArrayList<Move> getMoveList(long moves, int from, int flag) {
        ArrayList<Move> moveList = new ArrayList<>();

        while (moves != 0) {
            int to = 63 - Long.numberOfTrailingZeros(moves);

            moveList.add(new Move(from, to, flag));

            moves &= moves - 1;
        }

        return moveList;
    }

    /**
     * Gets a bitboard of all pieces attacking a given square.
     *
     * @param square The square to check for attackers.
     * @param board  The current board state.
     * @return A bitboard of pieces attacking the square.
     */
    private static long getAttackers(int square, Board board) {
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
}