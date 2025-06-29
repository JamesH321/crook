package com.github.jamesh321.chessengine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

public class PerftTest {
    Engine engine;

    @BeforeEach
    void setUp() {
        engine = new Engine(new Board());
    }

    // Test the starting position
    @Test
    void testPosition1() {
        long[] expectedNodes = { 1, 20, 400, 8902, 197281, 4865609 };
        for (int depth = 0; depth < expectedNodes.length; depth++) {
            long actualNodes = perft(engine, depth);
            perftDivide(depth);
            assertEquals(expectedNodes[depth], actualNodes);
        }
    }

    // Test a position known as kiwipete
    @Test
    void testPosition2() {
        long[] expectedNodes = { 1, 48, 2039, 97862, 4085603 };
        Fen.load(engine.getBoard(), "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
        for (int depth = 0; depth < expectedNodes.length; depth++) {
            long actualNodes = perft(engine, depth);
            assertEquals(expectedNodes[depth], actualNodes);
        }
    }

    private long perft(Engine engine, int depth) {
        int total = 0;

        if (depth == 0) {
            return 1;
        }
        for (Move move : MoveGenerator.generateLegalMoves(engine.getBoard())) {
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

        List<Move> moves = MoveGenerator.generateLegalMoves(engine.getBoard());

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
