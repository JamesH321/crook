package com.github.jamesh321.crook;

/**
 * Represents a chess move encoded as a 16-bit integer with the following
 * fields:
 * <ul>
 * <li>The 6 least significant bits (0-5) represent the source square
 * (0-63).</li>
 * <li>The next 6 bits (6-11) represent the destination square (0-63).</li>
 * <li>The highest 4 bits (12-15) encode special move flags, such as castling,
 * en passant, and promotions.</li>
 * </ul>
 * This compact encoding allows efficient storage and manipulation of moves
 * within the engine.
 */
public class Move {
    private final int data;

    private final int TO_FROM_MASK = 0b111111;

    /**
     * A flag for a normal move.
     */
    public static final int NORMAL = 0;
    /**
     * A flag for an en passant move.
     */
    public static final int EN_PASSANT = 2;
    /**
     * A flag for a castling move.
     */
    public static final int CASTLE = 3;
    /**
     * A flag for a queen promotion.
     */
    public static final int QUEEN_PROMOTION = 1;
    /**
     * A flag for a rook promotion.
     */
    public static final int ROOK_PROMOTION = 5;
    /**
     * A flag for a bishop promotion.
     */
    public static final int BISHOP_PROMOTION = 9;
    /**
     * A flag for a knight promotion.
     */
    public static final int KNIGHT_PROMOTION = 13;

    /**
     * Constructs a Move object using square indices and a flag.
     *
     * @param from the square the move is from (0-63 board index)
     * @param to   the square the move is to (0-63 board index)
     * @param flag special moves like en passant, castling, and promotion
     */
    public Move(int from, int to, int flag) {
        this.data = encodeMove(from, to, flag);
    }

    /**
     * Constructs a Move object from a string representation of a chess move.
     * The string should be in the format of source square + destination square,
     * such as "e2e4", plus an optional promotion piece indicator (q, r, b, n).
     * 
     * @param move  the string representation of the move (e.g. "e2e4" or "e7e8q"
     *              for promotion)
     * @param board the current chess board state, needed to identify special moves
     * @throws IllegalArgumentException if the move string is invalid or has
     *                                  incorrect coordinates
     */
    public Move(String move, Board board) {
        this.data = getMoveFromString(move, board);
    }

    public int getFrom() {
        return data & TO_FROM_MASK;
    }

    public int getTo() {
        return (data >>> 6) & TO_FROM_MASK;
    }

    public int getFlag() {
        int FLAG_MASK = 0b1111;
        return (data >>> 12) & FLAG_MASK;
    }

    /*
     * 0 - Queen
     * 1 - Rook
     * 2 - Bishop
     * 3 - Knight
     */
    public int getPromotionPiece() {
        return (getFlag() >> 2) & 0b0011;
    }

    /*
     * 0 - None
     * 1 - Piece promotion
     * 2 - En passant
     * 3 - Castle
     */
    public int getSpecialMove() {
        return getFlag() & 0b0011;
    }

    /**
     * Converts a string representation of a chess move into the encoded integer
     * format.
     * Processes a move string such as "e2e4" and returns the encoded move as an
     * integer.
     * 
     * @param moveString the string representation of the move (e.g. "e2e4" or
     *                   "e7e8q" for promotion)
     * @param board      the current chess board state, needed to identify special
     *                   moves
     * @return the encoded move as an integer
     * @throws IllegalArgumentException if the move string is invalid or has
     *                                  incorrect coordinates
     */
    public int getMoveFromString(String moveString, Board board) {
        validateMoveString(moveString);

        int[] coords = parseCoordinates(moveString);
        int from = coords[0];
        int to = coords[1];
        int fromFile = coords[2];
        int toFile = coords[3];

        int flag = determineFlag(moveString, from, to, fromFile, toFile, board);

        return encodeMove(from, to, flag);
    }

    /**
     * Validates that the move string has the correct format.
     * The string must not be null and must be at least 4 characters long.
     * 
     * @param moveString the string representation of the move to validate
     * @throws IllegalArgumentException if the move string is null or shorter than 4
     *                                  characters
     */
    private void validateMoveString(String moveString) {
        if (moveString == null || moveString.length() < 4) {
            throw new IllegalArgumentException("Invalid move format: " + moveString);
        }
    }

    /**
     * Parses the algebraic notation coordinates from a move string.
     * Converts notation like "e2e4" into array of indices:
     * [fromSquare, toSquare, fromFile, toFile] where:
     * - fromSquare and toSquare are 0-63 board indices
     * - fromFile and toFile are 0-7 file indices (a-h)
     * 
     * @param moveString the string representation of the move to parse
     * @return an array containing [fromSquare, toSquare, fromFile, toFile]
     * @throws IllegalArgumentException if any coordinate is outside the valid range
     */
    private int[] parseCoordinates(String moveString) {
        int fromFile = moveString.charAt(0) - 'a';
        int fromRank = 8 - Character.getNumericValue(moveString.charAt(1));
        int toFile = moveString.charAt(2) - 'a';
        int toRank = 8 - Character.getNumericValue(moveString.charAt(3));

        if (fromFile < 0 || fromFile > 7 || fromRank < 0 || fromRank > 7 ||
                toFile < 0 || toFile > 7 || toRank < 0 || toRank > 7) {
            throw new IllegalArgumentException("Invalid move coordinates: " + moveString);
        }

        int from = fromFile + (fromRank * 8);
        int to = toFile + (toRank * 8);

        return new int[] { from, to, fromFile, toFile };
    }

    /**
     * Determines the special flag associated with the move.
     * Checks for special moves such as promotion, castling, and en passant,
     * and returns the appropriate flag.
     * 
     * @param moveString the string representation of the move
     * @param from       the source square index (0-63)
     * @param to         the destination square index (0-63)
     * @param fromFile   the source file index (0-7, representing a-h)
     * @param toFile     the destination file index (0-7, representing a-h)
     * @param board      the current chess board state
     * @return the flag value representing the move type
     */
    private int determineFlag(String moveString, int from, int to, int fromFile, int toFile, Board board) {
        int flag = NORMAL;

        // Check for promotion
        if (moveString.length() == 5) {
            flag = getPromotionFlag(moveString.charAt(4));
        }

        Piece piece = board.getPieceAtSquare(from);

        // Check for castling
        if (isCastlingMove(piece, fromFile, toFile)) {
            flag = CASTLE;
        }

        // Check for en passant
        if (isEnPassantMove(to, piece, board)) {
            flag = EN_PASSANT;
        }

        return flag;
    }

    /**
     * Converts a promotion piece character to the corresponding flag.
     * The character is expected to be one of: q (queen), r (rook), b (bishop), or n
     * (knight).
     * 
     * @param promotionChar the character representing the promotion piece
     * @return the flag value for the specified promotion piece
     * @throws IllegalArgumentException if the character is not a valid promotion
     *                                  piece
     */
    private int getPromotionFlag(char promotionChar) {
        return switch (promotionChar) {
            case 'q' -> QUEEN_PROMOTION;
            case 'r' -> ROOK_PROMOTION;
            case 'b' -> BISHOP_PROMOTION;
            case 'n' -> KNIGHT_PROMOTION;
            default -> throw new IllegalArgumentException("Invalid promotion piece: " + promotionChar);
        };
    }

    /**
     * Determines if the move is a castling move.
     * A castling move is identified by a king moving two squares horizontally.
     * 
     * @param piece    the piece being moved
     * @param fromFile the source file index (0-7, representing a-h)
     * @param toFile   the destination file index (0-7, representing a-h)
     * @return true if the move is a castling move, false otherwise
     */
    private boolean isCastlingMove(Piece piece, int fromFile, int toFile) {
        return (piece == Piece.WHITE_KING || piece == Piece.BLACK_KING) &&
                Math.abs(fromFile - toFile) == 2;
    }

    /**
     * Determines if the move is an en passant capture.
     * An en passant move occurs when a pawn moves to the en passant square.
     * 
     * @param to    the destination square index (0-63)
     * @param piece the piece being moved
     * @param board the current chess board state
     * @return true if the move is an en passant capture, false otherwise
     */
    private boolean isEnPassantMove(int to, Piece piece, Board board) {
        return to == board.getEnPassantSquare() &&
                (piece == Piece.WHITE_PAWN || piece == Piece.BLACK_PAWN);
    }

    /**
     * Encodes a move into a 16-bit integer.
     * The encoding format is:
     * - bits 0-5: source square (0-63)
     * - bits 6-11: destination square (0-63)
     * - bits 12-15: move flag (special move type)
     * 
     * @param from the source square index (0-63)
     * @param to   the destination square index (0-63)
     * @param flag the move flag representing the move type
     * @return the encoded move as an integer
     */
    private int encodeMove(int from, int to, int flag) {
        return from | (to << 6) | (flag << 12);
    }

    /**
     * Returns the move in standard algebraic notation (e.g. e2e4, e7e8q for
     * promotion).
     * The format is: from-square + to-square + promotion piece (if applicable).
     *
     * @return the move as a string in algebraic notation
     */
    @Override
    public String toString() {
        String fromFile = Character.toString((char) (getFrom() % 8) + 'a');
        String fromRank = Integer.toString(8 - (getFrom() / 8));
        String toFile = Character.toString((char) (getTo() % 8) + 'a');
        String toRank = Integer.toString(8 - (getTo() / 8));
        String promotionPiece = switch (getFlag()) {
            case 1 -> "q";
            case 5 -> "r";
            case 9 -> "b";
            case 13 -> "n";
            default -> "";
        };

        return fromFile + fromRank + toFile + toRank + promotionPiece;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Move move = (Move) o;
        return data == move.data;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(data);
    }
}
