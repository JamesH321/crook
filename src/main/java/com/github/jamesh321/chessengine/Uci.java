package com.github.jamesh321.chessengine;

import java.util.Scanner;
import java.util.Arrays;

public final class Uci {

    private Uci() {
    }

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
                goCommand(engine);
                break;
            default:
                break;
        }
    }

    private static void uciCommand() {
        String name = "Crook";
        String author = "James Hickson";

        System.out.printf("id name %s\n", name);
        System.out.printf("id author %s\n", author);
        System.out.println("uciok");
    }

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

    private static void goCommand(Engine engine) {
        Move bestMove = engine.findBestMove(5);
        System.out.println("bestmove " + bestMove.toString());
    }

    private static void loadMoves(String[] moves, Engine engine) {
        for (String moveString : moves) {
            engine.makeMove(new Move(moveString, engine.getBoard()));
        }
    }

    private static int getMoveIndex(String[] tokens) {
        for (int i = 2; i < tokens.length; i++) {
            if (tokens[i].equals("moves")) {
                return i;
            }
        }

        return -1;
    }

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
}