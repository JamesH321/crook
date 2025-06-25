package com.github.jamesh321.chessengine;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PerftTest {

    @Test
    void testStartingPositionDepth1() {
        Engine engine = new Engine(new Board());
        int depth = 1;
        long expectedNodes = 20;

        long actualNodes = perft(engine, depth);

        assertEquals(expectedNodes, actualNodes);
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
}
