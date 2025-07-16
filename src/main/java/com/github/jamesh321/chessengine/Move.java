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

    public static final int NORMAL = 0;
    public static final int EN_PASSANT = 2;
    public static final int CASTLE = 3;
    public static final int QUEEN_PROMOTION = 1;
    public static final int ROOK_PROMOTION = 5;
    public static final int BISHOP_PROMOTION = 9;
    public static final int KNIGHT_PROMOTION = 13;

    /**
     * 
     * @param from the square the move is from
     * @param to   the square the move is to
     * @param flag special moves like en passant, castling, and promotion
     */
    public Move(int from, int to, int flag) {
        this.data = from | (to << 6) | (flag << 12);
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
