package com.github.jamesh321.chessengine;

import java.util.Scanner;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Implementation of the Universal Chess Interface (UCI) protocol.
 * This class handles communication between a chess GUI and the engine
 * following the UCI protocol specification.
 * https://backscattering.de/chess/uci/
 * 
 */
public final class Uci implements Runnable {

    public static Thread searchThread = null;

    private Uci() {
    }

    public void run() {
    }

    /**
     * The entry point of the chess engine.
     * Listens for UCI commands from standard input and responds accordingly.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        Engine engine = new Engine(new Board());
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();

            if (input.equals("quit")) {
                break;
            }

            processCommand(input, engine);
        }

        scanner.close();
    }

    /**
     * Processes an incoming UCI command responds accordingly.
     * 
     * @param input  the raw command input string
     * @param engine the chess engine instance to handle the command
     */
    private static void processCommand(String input, Engine engine) {
        String[] tokens = input.split("\\s+");
        String command = tokens[0];

        switch (command) {
            case "uci":
                uciCommand();
                break;
            case "isready":
                System.out.println("readyok");
                break;
            case "position":
                positionCommand(tokens, engine);
                break;
            case "go":
                goCommand(tokens, engine);
                break;
            case "stop":
                stopCommand();
                break;
            default:
                break;
        }
    }

    /**
     * Handles the 'uci' command.
     * Identifies the engine and author to the GUI and signals that the engine
     * supports UCI.
     */
    private static void uciCommand() {
        String name = "crook";
        String author = "James Hickson";

        System.out.printf("id name %s\n", name);
        System.out.printf("id author %s\n", author);
        System.out.println("uciok");
    }

    /**
     * Handles the 'position' command.
     * Sets up the board position according to the given parameters,
     * either from the starting position or from a FEN string,
     * and then makes any provided moves.
     * 
     * @param tokens the tokenised command string
     * @param engine the chess engine instance to update
     */
    private static void positionCommand(String[] tokens, Engine engine) {
        if (tokens.length > 1) {
            if (tokens[1].equals("startpos")) {
                engine.setBoard(new Board());

                if (tokens.length > 2 && tokens[2].equals("moves")) {
                    loadMoves(Arrays.copyOfRange(tokens, 3, tokens.length), engine);
                }

            } else if (tokens[1].equals("fen")) {
                loadFen(tokens, engine);
            }
        }
    }

    /**
     * Handles the 'go' command.
     * Starts the search for the best move and outputs the result using iterative
     * deepening.
     * 
     * @param tokens the tokenised command string containing search parameters
     * @param engine the chess engine to use for finding the best move
     */
    private static void goCommand(String[] tokens, Engine engine) {
        if (searchThread != null && searchThread.isAlive()) {
            return;
        }

        searchThread = new Thread(() -> {
            HashMap<String, String> commands = processGoCommands(tokens);

            long timeForMove = calculateMsecForMove(commands, engine);

            long startTime = System.currentTimeMillis();
            long endTime = startTime + timeForMove;

            Move lastBestMove = null;
            Move bestMove = null;

            for (int depth = 1; depth < 100; depth++) {
                HashMap<String, Object> bestMoveInfo = engine.findBestMove(depth, lastBestMove, endTime);

                bestMove = (Move) bestMoveInfo.get("best move");
                long nodes = (long) bestMoveInfo.get("nodes");
                long time = (long) bestMoveInfo.get("time");
                long nps = (long) bestMoveInfo.get("nps");
                int score = (int) bestMoveInfo.get("score");

                if (bestMove == null || System.currentTimeMillis() >= endTime) {
                    break;
                }

                lastBestMove = bestMove;

                System.out.printf("info depth %d nodes %d time %d nps %d score cp %d\n", depth, nodes, time, nps,
                        score);
            }

            System.out.println("bestmove " + lastBestMove.toString());

            searchThread = null;
        });

        searchThread.start();
    }

    private static void stopCommand() {
        if (searchThread != null && searchThread.isAlive()) {
            searchThread.interrupt();
        }
    }

    /**
     * Applies a sequence of moves to the current board position.
     * 
     * @param moves  array of move strings in UCI format
     * @param engine the chess engine to apply moves to
     */
    private static void loadMoves(String[] moves, Engine engine) {
        for (String moveString : moves) {
            engine.makeMove(new Move(moveString, engine.getBoard()));
        }
    }

    /**
     * Finds the index of the "moves" keyword in a position command.
     * Used to separate the FEN string from the moves list in a position command.
     * 
     * @param tokens tokenised command string
     * @return the index of the "moves" token, or -1 if not found
     */
    private static int getMoveIndex(String[] tokens) {
        for (int i = 2; i < tokens.length; i++) {
            if (tokens[i].equals("moves")) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Processes a FEN string from a position command and optionally applies
     * subsequent moves.
     * Handles both formats:
     * - "position fen [fen string]"
     * - "position fen [fen string] moves [move1] [move2] ..."
     * 
     * @param tokens tokenised command string containing a FEN position
     * @param engine the chess engine to update with the position
     */
    private static void loadFen(String[] tokens, Engine engine) {
        int movesIndex = getMoveIndex(tokens);

        String fen;
        if (movesIndex != -1) {
            fen = String.join(" ", Arrays.copyOfRange(tokens, 2, movesIndex));
            Fen.load(fen, engine.getBoard());

            loadMoves(Arrays.copyOfRange(tokens, movesIndex + 1, tokens.length), engine);
        } else {
            fen = String.join(" ", Arrays.copyOfRange(tokens, 2, tokens.length));
            Fen.load(fen, engine.getBoard());
        }
    }

    /**
     * Processes the 'go' command parameters and returns them as a map.
     * Handles parameters like wtime, btime, winc, binc, movetime, etc.
     * 
     * @param tokens the tokenised command string
     * @return a map of parameter names to their values
     */
    private static HashMap<String, String> processGoCommands(String[] tokens) {
        HashMap<String, String> goCommands = new HashMap<>();

        for (int i = 1; i < tokens.length; i++) {
            switch (tokens[i]) {
                case "wtime":
                case "btime":
                case "winc":
                case "binc":
                case "movetime":
                case "movestogo":
                case "depth":
                case "nodes":
                case "mate":
                    if (i + 1 < tokens.length) {
                        goCommands.put(tokens[i], tokens[i + 1]);
                        i++;
                    }
                    break;
                case "infinite":
                case "ponder":
                    goCommands.put(tokens[i], "true");
                    break;
            }
        }

        return goCommands;
    }

    /**
     * Calculates the time in milliseconds to allocate for the next move.
     * Takes into account the remaining time, increment, and moves to go.
     * 
     * @param commands the map of go command parameters
     * @param engine   the chess engine instance to check the current turn
     * @return the time in milliseconds to use for the next move
     */
    private static long calculateMsecForMove(HashMap<String, String> commands, Engine engine) {
        if (commands.containsKey("movetime")) {
            return Long.parseLong(commands.get("movetime"));
        }

        if (commands.containsKey("infinite")) {
            return Integer.MAX_VALUE;
        }

        long whiteTime = getLongValue(commands, "wtime", 0);
        long blackTime = getLongValue(commands, "btime", 0);
        long whiteIncrement = getLongValue(commands, "winc", 0);
        long blackIncrement = getLongValue(commands, "binc", 0);
        long movesRemaining = getLongValue(commands, "movestogo", 40);

        boolean isWhiteTurn = engine.getBoard().isWhiteTurn();
        long msecRemaining = isWhiteTurn ? whiteTime : blackTime;
        long msecIncrement = isWhiteTurn ? whiteIncrement : blackIncrement;

        if (msecRemaining == 0) {
            return 2000;
        }

        long timeForMove = (msecRemaining / movesRemaining) + msecIncrement;
        timeForMove = (long) (timeForMove * 0.95);

        return timeForMove;
    }

    /**
     * Safely gets a long value from a map with a default fallback.
     * 
     * @param map          the map containing parameter values
     * @param key          the key to look up in the map
     * @param defaultValue the default value to return if the key is missing or
     *                     invalid
     * @return the value from the map as a long, or defaultValue if not found or
     *         invalid
     */
    private static long getLongValue(HashMap<String, String> map, String key, long defaultValue) {
        String value = map.get(key);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}