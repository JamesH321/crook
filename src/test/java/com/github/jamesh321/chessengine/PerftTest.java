package com.github.jamesh321.chessengine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

public class PerftTest {
    Engine engine;

    @BeforeEach
    void setUp() {
        engine = new Engine(new Board());
    }

    // Test starting position
    @Test
    void testPosition1() {
        long expectedNodes = 3195901860L;
        long actualNodes = perft(engine, 7);
        assertEquals(expectedNodes, actualNodes);
    }

    // Test position known as kiwipete
    @Test
    void testPosition2() {
        long expectedNodes = 8031647685L;
        Fen.load(engine.getBoard(), "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");

        long actualNodes = perft(engine, 6);
        assertEquals(expectedNodes, actualNodes);
    }

    @Test
    void testPosition3() {
        long expectedNodes = 3009794393L;
        Fen.load(engine.getBoard(), "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1");
        long actualNodes = perft(engine, 8);
        assertEquals(expectedNodes, actualNodes);
    }

    @Test
    void testPosition4() {
        long expectedNodes = 706045033L;
        Fen.load(engine.getBoard(), "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
        long actualNodes = perft(engine, 6);
        assertEquals(expectedNodes, actualNodes);
    }

    @Test
    void testPosition5() {
        long expectedNodes = 89941194L;
        Fen.load(engine.getBoard(), "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
        long actualNodes = perft(engine, 5);
        assertEquals(expectedNodes, actualNodes);
    }

    @Test
    void testPosition6() {
        long expectedNodes = 6923051137L;
        Fen.load(engine.getBoard(), "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10");
        long actualNodes = perft(engine, 6);
        assertEquals(expectedNodes, actualNodes);
    }

    private long perft(Engine engine, int depth) {
        if (depth == 0) {
            return 1;
        }
        ArrayList<Move> moves = MoveGenerator.generateLegalMoves(engine.getBoard());
        if (depth == 1) {
            return moves.size();
        }
        long total = 0;
        for (Move move : moves) {
            engine.makeMove(move);
            total += perft(engine, depth - 1);
            engine.undoMove();
        }
        return total;
    }

    private void perftDivide(int depth) {
        if (depth <= 0) {
            System.out.println("Depth must be greater than 0 for divide.");
            return;
        }

        System.out.println("Running Perft Divide for depth: " + depth);
        long totalNodes = 0;
        ArrayList<Move> moves = MoveGenerator.generateLegalMoves(engine.getBoard());

        for (Move move : moves) {
            engine.makeMove(move);
            long nodes = perft(engine, depth - 1);
            totalNodes += nodes;
            System.out.println(move.toString() + ": " + nodes);
            engine.undoMove();
        }

        System.out.println("\nTotal Nodes: " + totalNodes);
    }
}