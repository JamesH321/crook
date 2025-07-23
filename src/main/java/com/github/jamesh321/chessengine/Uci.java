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

        for (int token = 0; token < tokens.length; token++) {
            switch (tokens[token]) {
                case "uci":
                    uciCommand();
                    break;
                case "isready":
                    System.out.println("readyok");
                    break;
                case "position":
                    positionCommand(Arrays.copyOfRange(tokens, token + 1, tokens.length), engine);
                    break;
                case "go":
                    goCommand(engine);
                    break;
                default:
                    break;
            }
        }

    }

    private static void uciCommand() {
        String name = "Crook";
        String author = "James Hickson";

        System.out.printf("id name %s\n", name);
        System.out.printf("id author %s\n", author);

        System.out.println("uciok");
    }

    private static void positionCommand(String[] position, Engine engine) {
        switch (position[0]) {
            case "startpos":
                engine.setBoard(new Board());
                loadMoves(Arrays.copyOfRange(position, 1, position.length), engine);
                break;
            case "fen":
                String fen = String.join(" ", Arrays.copyOfRange(position, 1, 7));
                Fen.load(fen, engine.getBoard());
                loadMoves(Arrays.copyOfRange(position, 2, position.length), engine);
                break;
        }
    }

    private static void goCommand(Engine engine) {
        Move bestMove = engine.findBestMove(5);
        System.out.println("bestmove " + bestMove.toString());
    }

    private static void loadMoves(String[] position, Engine engine) {
        if (position[0].equals("moves")) {
            for (String movesString : position) {
                engine.makeMove(new Move(movesString, engine.getBoard()));
            }
        }
    }
}
