package com.github.jamesh321.crook;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Search class implements chess position analysis algorithms to find the
 * best move
 * in a given position. It uses the negamax algorithm, a variant of minimax, to
 * evaluate chess positions and determine optimal moves. It also uses alpha-beta
 * pruning to improve search efficiency.
 */
public class Search {

    /**
     * The total nodes visited in this search.
     */
    private long nodes = 0;

    /**
     * The time taken for the search.
     */
    private long time;

    /**
     * The score for the given position.
     */
    private int score;

    /**
     * Finds the best move for the current player in the given position by searching
     * to the specified depth using the negamax algorithm with alpha-beta pruning.
     *
     * @param depth        the depth to search to (number of half-moves)
     * @param lastBestMove the previously found best move to prioritise in move
     *                     ordering
     * @param endTime      the timestamp at which the search should terminate
     * @param engine       the chess engine containing the current game state
     * @return the best move found, or null if no legal moves exist, depth is 0, or
     *         time has expired
     */
    public Move findBestMove(int depth, Move lastBestMove, long endTime, Engine engine) {
        long startTime = System.currentTimeMillis();

        if (depth == 0) {
            return null;
        }

        Move bestMove = null;
        int alpha = -100000;
        int beta = 100000;

        ArrayList<Move> moves = MoveGenerator.generateLegalMoves(engine.getBoard());

        if (moves.isEmpty() || engine.getBoard().getHalfmoveClock() == 100) {
            return null;
        }

        moves = orderMoves(moves, lastBestMove, engine.getBoard());

        for (Move move : moves) {
            if (Thread.currentThread().isInterrupted() || System.currentTimeMillis() >= endTime) {
                return null;
            }

            engine.makeMove(move);

            int score = -negaMax(depth - 1, -beta, -alpha, engine);

            engine.undoMove();

            if (score > alpha) {
                alpha = score;
                bestMove = move;
            }

            this.nodes += 1;
        }

        this.score = alpha;
        this.time = System.currentTimeMillis() - startTime;

        return bestMove;
    }

    /**
     * Implements the negamax algorithm with alpha-beta pruning for chess position
     * evaluation.
     * Negamax is a variant of minimax that relies on the zero-sum property of
     * chess,
     * simplifying the implementation by always maximizing from the current player's
     * perspective.
     *
     * @param depth  the remaining depth to search
     * @param alpha  the alpha value for alpha-beta pruning
     * @param beta   the beta value for alpha-beta pruning
     * @param engine the chess engine containing the current game state
     * @return the evaluation score from the perspective of the current player
     */
    public int negaMax(int depth, int alpha, int beta, Engine engine) {
        if (depth == 0) {
            return Evaluate.board(engine.getBoard());
        }

        ArrayList<Move> moves = MoveGenerator.generateLegalMoves(engine.getBoard());

        if (moves.isEmpty()) {
            if (inCheck(engine.getBoard())) {
                return -100000;
            } else {
                return 0;
            }
        }

        if (engine.getBoard().getHalfmoveClock() == 100) {
            return 0;
        }

        moves = orderMoves(moves, null, engine.getBoard());

        for (Move move : moves) {
            engine.makeMove(move);

            int score = -negaMax(depth - 1, -beta, -alpha, engine);

            engine.undoMove();

            this.nodes += 1;

            if (score >= beta) {
                return beta;
            }

            if (score > alpha) {
                alpha = score;
            }
        }

        return alpha;
    }

    /**
     * Determines if the current player is in check.
     *
     * @param board the chess board to analyze
     * @return true if the current player's king is in check, false otherwise
     */
    private boolean inCheck(Board board) {
        long kingBitboard = board.isWhiteTurn() ? board.getBitboard(Piece.WHITE_KING)
                : board.getBitboard(Piece.BLACK_KING);
        int kingSquare = Long.numberOfLeadingZeros(kingBitboard);

        return MoveGenerator.isSquareAttacked(kingSquare, board);
    }

    private ArrayList<Move> orderMoves(ArrayList<Move> moves, Move lastBestMove, Board board) {
        ArrayList<Move> orderedMoves = new ArrayList<>();

        orderedMoves = orderByMvvMinusLva(moves, board);

        if (lastBestMove != null) {
            orderedMoves.remove(lastBestMove);
            orderedMoves.addFirst(lastBestMove);
        }

        return orderedMoves;
    }

    private ArrayList<Move> orderByMvvMinusLva(ArrayList<Move> moves, Board board) {
        ArrayList<Move> orderedMoves = new ArrayList<>();

        ArrayList<Move> nonAttackingMoves = new ArrayList<>();
        HashMap<Move, Integer> captureMoveScores = new HashMap<>();

        long opponentSquares = board.isWhiteTurn() ? board.getBlackPieces() : board.getWhitePieces();

        for (Move move : moves) {
            if ((opponentSquares & LookupTables.BITBOARD_SQUARES[move.getTo()]) != 0) {
                captureMoveScores.put(move, mvvMinusLva(move, board));
            } else {
                nonAttackingMoves.add(move);
            }
        }

        ArrayList<Move> attackingMoves = new ArrayList<>(captureMoveScores.keySet());
        attackingMoves.sort((a, b) -> Integer.compare(captureMoveScores.get(b), captureMoveScores.get(a)));

        orderedMoves.addAll(attackingMoves);
        orderedMoves.addAll(nonAttackingMoves);

        return orderedMoves;
    }

    private int mvvMinusLva(Move move, Board board) {
        Piece attackerPiece = board.getPieceAtSquare(move.getFrom());
        Piece victimPiece = board.getPieceAtSquare(move.getTo());

        // Handle en passant capture
        if (move.getFlag() == Move.EN_PASSANT) {
            victimPiece = board.isWhiteTurn() ? Piece.BLACK_PAWN : Piece.WHITE_PAWN;
        }

        int attackerValue = getPieceValue(attackerPiece);
        int victimValue = getPieceValue(victimPiece);

        return victimValue - attackerValue;
    }

    private int getPieceValue(Piece piece) {
        switch (piece) {
            case WHITE_PAWN:
            case BLACK_PAWN:
                return Evaluate.PAWN_VALUE;
            case WHITE_KNIGHT:
            case BLACK_KNIGHT:
                return Evaluate.KNIGHT_VALUE;
            case WHITE_BISHOP:
            case BLACK_BISHOP:
                return Evaluate.BISHOP_VALUE;
            case WHITE_ROOK:
            case BLACK_ROOK:
                return Evaluate.ROOK_VALUE;
            case WHITE_QUEEN:
            case BLACK_QUEEN:
                return Evaluate.QUEEN_VALUE;
            case WHITE_KING:
            case BLACK_KING:
                return Evaluate.KING_VALUE;
            default:
                return 0;
        }
    }

    public long getNodes() {
        return nodes;
    }

    public long getTime() {
        return time;
    }

    public int getScore() {
        return score;
    }
}