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
public final class Uci {

    private Uci() {
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
        String name = "Crook";
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
     * @param tokens the tokenized command string
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
     * Starts the search for the best move and outputs the result.
     * Currently uses a fixed search depth of 5.
     * 
     * @param engine the chess engine to use for finding the best move
     */
    private static void goCommand(String[] tokens, Engine engine) {
        HashMap<String, String> commands = processGoCommands(tokens);

        long timeForMove = calculateMsecForMove(commands, engine);

        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeForMove;

        Move lastBestMove = null;
        Move bestMove = null;

        for (int depth = 1; depth < 100; depth++) {
            System.out.println("info depth " + depth);

            bestMove = engine.findBestMove(depth, lastBestMove, endTime);

            if (bestMove == null) {
                break;
            }

            if (System.currentTimeMillis() >= endTime) {
                break;
            }

            lastBestMove = bestMove;
        }

        System.out.println("bestmove " + lastBestMove.toString());
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
     * @param tokens tokenized command string
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
     * @param tokens tokenized command string containing a FEN position
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

    private static long getLongValue(HashMap<String, String> map, String key, long defaultValue) {
        String value = map.get(key);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}