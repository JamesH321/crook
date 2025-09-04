package com.github.jamesh321.crook;

import java.util.Random;

/**
 * Implementation of magic bitboards for efficient sliding piece move
 * generation.
 * Magic bitboards use pre-computed magic numbers to quickly calculate bishop
 * and rook
 * attacks by hashing the relevant blocker patterns to attack lookup tables.
 * This provides very fast move generation for bishops, rooks, and queens.
 */
public class MagicBitboards {
    /**
     * Pre-computed magic numbers for bishop attack calculations.
     * Each magic number is specifically chosen to create a perfect hash
     * for all possible blocker patterns on bishop diagonal rays.
     */
    public static final long[] BISHOP_MAGICS = { 0x14180A1412420200L, 0x4180A024301E10D2L, 0x2C46E4CE0844901L,
            0x441004020024410L, 0x210020A024A09802L, 0x520198876B08082CL, 0x10E0911382012051L, 0x42508200808440C0L,
            0x2201B2101011503L, 0x459850100A830086L, 0x80400A9007120260L, 0x180A60A104240400L, 0x1E18800084041030L,
            0x3191708348080418L, 0x4420108B804044CL, 0xC311D0820046544L, 0x70884D010E0111A7L, 0x21044808410448D0L,
            0x423E888102010101L, 0x49A7412012021503L, 0xB600346013001807L, 0x8042E01150104800L, 0x140A08228CEBD922L,
            0x494A012C4009E00BL, 0x2325E70200410084L, 0xC044040886440990L, 0x8921022200808802L, 0x32900880440020L,
            0x8444120080980080L, 0x8101240300100112L, 0x4CAE066004701102L, 0x2238051640900AL, 0x2261850246104A0AL,
            0x9211822403051020L, 0x80A0028410088L, 0x602002022008141L, 0x10C0110026004900L, 0x1808082B021020L,
            0x8021220204C0423L, 0x4010112040246310L, 0x800F02841280388L, 0x8820C06988045001L, 0x10200A502920100L,
            0x3284804C00A01070L, 0x1140108240410B0L, 0x814409202040100L, 0xC110133204250408L, 0x2E20039021024189L,
            0x94E034848C01A000L, 0x8020852C1006280CL, 0x2402210120130304L, 0x80D6091140000000L, 0x40CA82184A10830L,
            0x4EC10450808002L, 0x425A8248210100L, 0xF4185010304302D2L, 0x2404402C88201000L, 0x11080842120001DL,
            0x1C46282088010108L, 0xE540C608C200208L, 0xB959040B08940200L, 0x889540102013010L, 0xA860118410808041L,
            0x88A18020404022AL };

    /**
     * Pre-computed magic numbers for rook attack calculations.
     * Each magic number is specifically chosen to create a perfect hash
     * for all possible blocker patterns on rook straight rays.
     */
    public static final long[] ROOK_MAGICS = { 0x4602D109A4814402L, 0x101948101201209CL, 0xA01004400080A09L,
            0x600305805204AL, 0x9001001000080521L, 0x2511402860010091L, 0x250400193028021L, 0x15038108E2004032L,
            0x8000840B05509200L, 0x1841D00601080400L, 0x243020004008080L, 0x6C11000801C500L, 0x81CCC80010068080L,
            0x209410012A00700L, 0x4004820301C0EA00L, 0x380042194400080L, 0x38000402A44A0009L, 0x840C1380A040010L,
            0xA04A042040180110L, 0x2820310008010044L, 0xE518A200407A0010L, 0x2528821200420020L, 0x820005004A44000L,
            0x80303180C0008002L, 0x442940942000889L, 0x881821584001810L, 0x29B2009822004450L, 0xE108110501001800L,
            0xF173805001800800L, 0x110842000801000L, 0x4D8200040C01000L, 0x340C8009C1002101L, 0x645488200054401L,
            0xCAC3126400108108L, 0xB282008200100804L, 0x9408012900100500L, 0x211E200084200L, 0x11420200188060L,
            0xA0840008020048CL, 0x10681A08000C012L, 0x4000A0001095194L, 0x28C240070122D28L, 0xC84101000C00484EL,
            0x1013250028010070L, 0x2250010048306100L, 0x6609420022008251L, 0x5420848020004001L, 0xB0C1608000400485L,
            0x24028003C3001080L, 0x2822000802004584L, 0xD0200101826004CL, 0x5B9D001100280006L, 0x82D3001000230058L,
            0x42A002180B60140L, 0x2119002040010880L, 0x90C080012180400EL, 0x80068003422100L, 0xA00075200080284L,
            0x15000900A80C0006L, 0x320018220044E010L, 0x3D00046100500028L, 0x280200008801000L, 0xA4C000D002A00042L,
            0x8000C002908820L };

    /**
     * Pre-computed attack lookup tables for bishops.
     * Indexed by [square][magic_index] where magic_index is computed
     * using the blocker configuration and magic number.
     */
    public static final long[][] BISHOP_ATTACKS = new long[64][];

    /**
     * Pre-computed attack lookup tables for rooks.
     * Indexed by [square][magic_index] where magic_index is computed
     * using the blocker configuration and magic number.
     */
    public static final long[][] ROOK_ATTACKS = new long[64][];

    static {
        initialiseAttacks(true, BISHOP_ATTACKS);
        initialiseAttacks(false, ROOK_ATTACKS);
    }

    /**
     * Finds and prints magic numbers for both bishops and rooks.
     * This method is used for generating new magic numbers if needed.
     * The generated magic numbers should be copied into the constants above.
     */
    private static void findMagics() {
        long[] bishopMagics = new long[64];
        long[] rookMagics = new long[64];

        for (int square = 0; square < 64; square++) {
            bishopMagics[square] = findMagic(square, true, LookupTables.BISHOP_RAYS_WITHOUT_EDGES[square]);
            rookMagics[square] = findMagic(square, false, LookupTables.ROOK_RAYS_WITHOUT_EDGES[square]);
        }

        printMagics("bishop", bishopMagics);
        printMagics("rook", rookMagics);
    }

    /**
     * Finds a magic number for a specific square and piece type.
     * Uses a brute-force approach with random number generation to find
     * a magic number that creates a perfect hash for all blocker patterns.
     *
     * @param square   The square to find a magic number for (0-63).
     * @param isBishop True for bishop, false for rook.
     * @param rays     The attack rays for the piece from this square.
     * @return A magic number that works for this square and piece type.
     */
    private static long findMagic(int square, boolean isBishop, long[] rays) {
        long attackMask = getAttackMask(rays);
        int shift = 64 - Long.bitCount(attackMask);

        long[] blockerCombinations = generateBlockerCombinations(attackMask);
        long[] blockerAttacks = getBlockerAttacks(square, isBishop, blockerCombinations);

        Random random = new Random();

        while (true) {
            long[] attackTable = new long[blockerCombinations.length];

            long potentialMagic = random.nextLong() & random.nextLong();

            if (Long.bitCount(potentialMagic) < 6)
                continue;

            boolean isMagic = true;
            for (int i = 0; i < blockerCombinations.length; i++) {
                int index = (int) ((blockerCombinations[i] * potentialMagic) >>> shift);

                if (attackTable[index] == 0L) {
                    attackTable[index] = blockerAttacks[i];
                } else if (attackTable[index] != blockerAttacks[i]) {
                    isMagic = false;
                    break;
                }
            }

            if (isMagic) {
                return potentialMagic;
            }
        }
    }

    /**
     * Initialises the attack lookup tables using the pre-computed magic numbers.
     * This method populates the BISHOP_ATTACKS and ROOK_ATTACKS arrays with
     * attack patterns for all possible blocker configurations.
     *
     * @param isBishop            True for bishop initialization, false for rook.
     * @param slidingPieceAttacks The attack table to initialize.
     */
    private static void initialiseAttacks(boolean isBishop, long[][] slidingPieceAttacks) {
        for (int square = 0; square < 64; square++) {
            long magicNumber = isBishop ? BISHOP_MAGICS[square] : ROOK_MAGICS[square];

            long[] rays = isBishop ? LookupTables.BISHOP_RAYS_WITHOUT_EDGES[square]
                    : LookupTables.ROOK_RAYS_WITHOUT_EDGES[square];

            long attackMask = getAttackMask(rays);
            int shift = 64 - Long.bitCount(attackMask);

            long[] blockerCombinations = generateBlockerCombinations(attackMask);
            long[] blockerAttacks = getBlockerAttacks(square, isBishop, blockerCombinations);

            long[] attacks = new long[blockerCombinations.length];
            for (int j = 0; j < blockerCombinations.length; j++) {
                int index = (int) ((blockerCombinations[j] * magicNumber) >>> shift);

                attacks[index] = blockerAttacks[j];
            }

            slidingPieceAttacks[square] = attacks;
        }
    }

    /**
     * Formats and prints magic numbers.
     *
     * @param piece        The piece type name for the output ("bishop" or "rook").
     * @param magicNumbers Array of magic numbers to print.
     */
    private static void printMagics(String piece, long[] magicNumbers) {
        System.out.printf("Magic numbers for %s:\n", piece);
        System.out.print("{ ");

        for (int i = 0; i < magicNumbers.length; i++) {
            System.out.printf("0x%XL", magicNumbers[i]);
            if (i != 63)
                System.out.print(", ");
        }

        System.out.print(" }\n\n");
    }

    /**
     * Calculates the attack patterns for each blocker combination on a square.
     * For each possible arrangement of blocking pieces, determines which squares
     * the sliding piece can attack before being blocked.
     *
     * @param square              The square the piece is on.
     * @param isBishop            True for bishop, false for rook.
     * @param blockerCombinations All possible blocker arrangements.
     * @return Array of attack bitboards corresponding to each blocker combination.
     */
    private static long[] getBlockerAttacks(int square, boolean isBishop, long[] blockerCombinations) {
        long[][] attackRays = isBishop ? LookupTables.BISHOP_RAYS : LookupTables.ROOK_RAYS;
        long[] blockerAttacks = new long[blockerCombinations.length];

        for (int i = 0; i < blockerCombinations.length; i++) {
            long blockerAttack = 0L;

            for (int j = 0; j < 4; j++) {
                long blockers = blockerCombinations[i] & attackRays[square][j];

                if (blockers == 0) {
                    blockerAttack |= attackRays[square][j];
                    continue;
                }

                long blockerMask;
                if (63 - Long.numberOfTrailingZeros(blockers) < square) {
                    int blockerSquare = 63 - Long.numberOfTrailingZeros(blockers);

                    blockerMask = (LookupTables.BITBOARD_SQUARES[blockerSquare] - 1)
                            | LookupTables.BITBOARD_SQUARES[blockerSquare];
                } else {
                    int blockerSquare = Long.numberOfLeadingZeros(blockers);

                    blockerMask = -LookupTables.BITBOARD_SQUARES[blockerSquare];
                }

                blockerAttack |= attackRays[square][j] & blockerMask;
            }

            blockerAttacks[i] = blockerAttack;
        }

        return blockerAttacks;
    }

    /**
     * Generates all possible combinations of blocker pieces for a given attack
     * mask.
     * Each combination represents a different way pieces can be arranged on the
     * relevant squares that could block the sliding piece's movement.
     *
     * @param attackMask Bitboard representing all squares that could contain
     *                   blockers.
     * @return Array of all possible blocker combinations.
     */
    private static long[] generateBlockerCombinations(long attackMask) {
        int possibleCombinations = (int) Math.pow(2, Long.bitCount(attackMask));
        long[] blockerCombinations = new long[possibleCombinations];

        int[] possibleBlockerSquares = getPossibleBlockerSquares(attackMask);

        for (int i = 0; i < possibleCombinations; i++) {
            long currentCombination = 0L;

            for (int j = 0; j < possibleBlockerSquares.length; j++) {

                if ((i & (1 << j)) != 0) {
                    currentCombination |= (1L << possibleBlockerSquares[j]);
                }
            }

            blockerCombinations[i] = currentCombination;
        }

        return blockerCombinations;
    }

    /**
     * Creates an attack mask by combining all attack rays for a piece.
     * The attack mask represents all squares that are relevant for
     * determining the piece's attack pattern.
     *
     * @param rays   Array of attack rays in all directions.
     * @return Combined attack mask covering all relevant squares.
     */
    private static long getAttackMask(long[] rays) {
        long attackMask = 0;

        for (int i = 0; i < 4; i++) {
            attackMask |= rays[i];
        }

        return attackMask;
    }

    /**
     * Extracts the individual square indices from an attack mask.
     * Converts a bitboard representation into an array of square numbers
     * for easier iteration over the relevant squares.
     *
     * @param attackMask Bitboard representing the relevant squares.
     * @return Array of square indices (0-63) for each set bit in the mask.
     */
    private static int[] getPossibleBlockerSquares(long attackMask) {
        int numOfBlockerSquares = Long.bitCount(attackMask);
        int[] possibleBlockerSquares = new int[numOfBlockerSquares];

        long tempAttackMask = attackMask;

        for (int i = 0; i < possibleBlockerSquares.length; i++) {
            int possibleBlocker = Long.numberOfTrailingZeros(tempAttackMask);

            possibleBlockerSquares[i] = possibleBlocker;

            tempAttackMask &= tempAttackMask - 1;
        }

        return possibleBlockerSquares;
    }

    /**
     * Main method for generating and displaying magic numbers.
     * Run this method to generate new magic numbers if needed.
     *
     * @param args Command line arguments (unused).
     */
    public static void main(String[] args) {
        findMagics();
    }
}