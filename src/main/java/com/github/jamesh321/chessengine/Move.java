package com.github.jamesh321.chessengine;

/**
 * Represents a chess move encoded as a 16 bit integer with the following
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
    private int data;

    private final int TO_FROM_MASK = 0b111111;
    private final int FLAG_MASK = 0b1111;

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
     * @param from the square the move is from
     * @param to   the square the move is to
     * @param flag special moves like en passant, castling, and promotion
     */
    public Move(int from, int to, int flag) {
        this.data = from | (to << 6) | (flag << 12);
    }

    public Move(String move, Board board) {
        this.data = getMoveFromString(move, board);
    }

    public int getData() {
        return data;
    }

    public int getFrom() {
        return data & TO_FROM_MASK;
    }

    public int getTo() {
        return (data >>> 6) & TO_FROM_MASK;
    }

    public int getFlag() {
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

    private void validateMoveString(String moveString) {
        if (moveString == null || moveString.length() < 4) {
            throw new IllegalArgumentException("Invalid move format: " + moveString);
        }
    }

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
        if (isEnPassantMove(from, to, piece, board)) {
            flag = EN_PASSANT;
        }

        return flag;
    }

    private int getPromotionFlag(char promotionChar) {
        switch (promotionChar) {
            case 'q':
                return QUEEN_PROMOTION;
            case 'r':
                return ROOK_PROMOTION;
            case 'b':
                return BISHOP_PROMOTION;
            case 'n':
                return KNIGHT_PROMOTION;
            default:
                throw new IllegalArgumentException("Invalid promotion piece: " + promotionChar);
        }
    }

    private boolean isCastlingMove(Piece piece, int fromFile, int toFile) {
        return (piece == Piece.WHITE_KING || piece == Piece.BLACK_KING) &&
                Math.abs(fromFile - toFile) == 2;
    }

    private boolean isEnPassantMove(int from, int to, Piece piece, Board board) {
        return to == board.getEnPassantSquare() &&
                (piece == Piece.WHITE_PAWN || piece == Piece.BLACK_PAWN);
    }

    private int encodeMove(int from, int to, int flag) {
        return from | (to << 6) | (flag << 12);
    }

    /**
     * Returns the move in standard algebraic notation (e.g., e2e4, e7e8q for
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
        String promotionPiece;
        switch (getFlag()) {
            case 1:
                promotionPiece = "q";
                break;
            case 5:
                promotionPiece = "r";
                break;
            case 9:
                promotionPiece = "b";
                break;
            case 13:
                promotionPiece = "n";
                break;
            default:
                promotionPiece = "";
                break;
        }

        return fromFile + fromRank + toFile + toRank + promotionPiece;
    }
}
