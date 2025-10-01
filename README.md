# Crook Chess Engine

A chess engine written in Java that is compatible with the [Universal Chess Interface (UCI)](https://backscattering.de/chess/uci/).

## Features

*   **UCI Compatible**, allowing it to be used with a variety of chess GUIs
*   **Bitboards** to represent the board
*   **Move Generation:**
    * Pre-computed lookup tables for non-sliding piece move generation
    * **Magic bitboards** for sliding piece move generation
    * Provides code for generating magic numbers
*   **Search:**
    *   Uses **negamax** algorithm, which is a variation of the minimax algorithm
    *   **Iterative deepening**
*   **Evaluation:**
    *  **Material balance** score
    * Basic **piece-square tables** for each piece and separate king tables for the middle and end phases of the game

## Installation

1.  **Prerequisites:**
    *   Java 17 or higher
    *   Apache Maven

2.  **Build:**
    *   Clone the repository.
    *   Open a terminal in the project's root directory and run the following command:
        ```bash
        mvn clean install
        ```
    *   This will create a JAR file in the `target` directory.

## Usage

Once the project is built, you can run the engine from the terminal with the following command:

```bash
java -jar target/crook-1.0.jar
```

The engine can also be used with any UCI-compatible chess GUI, such as [Arena](http://www.playwitharena.de/).
