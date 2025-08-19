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

        orderedMoves = mvvMinusLva(moves, board);

        if (lastBestMove != null) {
            orderedMoves.remove(lastBestMove);
            orderedMoves.addFirst(lastBestMove);
        }

        return orderedMoves;
    }

    private ArrayList<Move> mvvMinusLva(ArrayList<Move> moves, Board board) {
        ArrayList<Move> orderedMoves = new ArrayList<>();

        ArrayList<Move> attackingMoves = getAttackingMoves(moves, board);
        ArrayList<Integer> victimSquares = getVictimSquares(attackingMoves, board);

        HashMap<Move, Integer> captureMoveScores = new HashMap<>();

        for (int i = 0; i < attackingMoves.size(); i++) {
            int attackerValue = getPieceValue(attackingMoves.get(i).getFrom(), board);
            int victimValue = getPieceValue(victimSquares.get(i), board);
            int captureScore = victimValue - attackerValue;

            captureMoveScores.put(attackingMoves.get(i), captureScore);
        }

        attackingMoves.sort((a, b) -> Integer.compare(captureMoveScores.get(b), captureMoveScores.get(a)));
        orderedMoves.addAll(attackingMoves);

        moves.removeAll(attackingMoves);
        orderedMoves.addAll(moves);

        return orderedMoves;
    }

    private ArrayList<Move> getAttackingMoves(ArrayList<Move> moves, Board board) {
        ArrayList<Move> attackingMoves = new ArrayList<>();

        long opponentSquares = board.isWhiteTurn() ? board.getBlackPieces() : board.getWhitePieces();

        for (Move move : moves) {
            if ((opponentSquares & LookupTables.BITBOARD_SQUARES[move.getTo()]) != 0) {
                attackingMoves.add(move);
            }
        }

        return attackingMoves;
    }

    private ArrayList<Integer> getVictimSquares(ArrayList<Move> attackingMoves, Board board) {
        ArrayList<Integer> victimSquares = new ArrayList<>();

        for (Move move : attackingMoves) {
            victimSquares.add(move.getTo());
        }

        return victimSquares;
    }

    private int getPieceValue(int square, Board board) {
        int[] values = { 100, 300, 300, 500, 900, 10000 };

        for (Piece piece : Piece.values()) {
            if ((board.getBitboard(piece) & LookupTables.BITBOARD_SQUARES[square]) != 0) {
                return values[piece.getIndex() % 6];
            }
        }

        return -1;
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