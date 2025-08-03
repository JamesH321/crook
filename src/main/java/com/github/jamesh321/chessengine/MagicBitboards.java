package com.github.jamesh321.chessengine;

import java.util.Random;

public class MagicBitboards {
    public static final long[] BISHOP_MAGICS = { 0x4200415286100L, 0x84101C4848085280L, 0x90509A30B0821820L,
            0x144946408210906L, 0x810C856402E28804L, 0x2030108742319000L, 0x14940A0489A41006L, 0x1E28508808080409L,
            0x8450100B01206654L, 0x1C088CB21161001L, 0x2326852080E05C5L, 0x6983791212021010L, 0x204040108C80084L,
            0x8BE20DA00900034L, 0x4260802188120L, 0x89A60E10042D0480L, 0x2A9014054DC64082L, 0x448105380884620L,
            0x6020010202208A03L, 0x4CC490522000C00L, 0x922018420182A800L, 0x806500A101041000L, 0x53440C9670256821L,
            0x1012D2006382036L, 0xA014424A05508089L, 0x481C0810D0020113L, 0x2E200801C0208042L, 0x7884200A0820080L,
            0x4019940400080120L, 0xA224020110280848L, 0x10880802A12A04L, 0x710402204088A060L, 0x52208A003C50840L,
            0xA2141409C104822EL, 0x910CA005B010148L, 0xCD3B01001B904008L, 0x60A040028009220L, 0x6294680070094141L,
            0x12C820064D840882L, 0xD186060C02D0308L, 0x220B8088828800L, 0xD0004426A8041060L, 0x30A3019201C10442L,
            0x500C010086A01211L, 0xA480074024038A0L, 0x10D008450400A040L, 0x29901544304E8F00L, 0x120838200A146805L,
            0x4900404048443030L, 0x6840630482104000L, 0x80001040240008EL, 0x548A845040224100L, 0x83C2091810260C0L,
            0xB85180810C88242L, 0x9988433002021640L, 0x5210692014040260L, 0xCA08D40218020800L, 0x10004F242060820FL,
            0x167110081C5A6A28L, 0xE13410281854C882L, 0x21410420428101BL, 0x5B80240C2030133L, 0xC184A0424042C48L,
            0x510541010806305L };

    public static final long[] ROOK_MAGICS = { 0x1D8990180254402L, 0x5104031802009004L, 0x522200100401681AL,
            0x800E011020983402L, 0x7002A10002045L, 0x4811200C280204AL, 0x811058038604001L, 0xC8261008601C152L,
            0xA3F084400A90200L, 0x207481012832400L, 0x5E11004C00285B00L, 0x42A4580080540180L, 0x181180030068080L,
            0x829025708202C200L, 0x4A0400220008080L, 0x140F098000324100L, 0x9430005C10920001L, 0x25214A0004010100L,
            0x442A011004020048L, 0x3042004418220010L, 0x83120040220008L, 0x911204200820030L, 0x8150042013C04006L,
            0xE12814008298004L, 0x526A088142000413L, 0x211F08632C000250L, 0x24CA204038011014L, 0x6842002012002468L,
            0x48000880805002L, 0x8A1702001004100L, 0xD8D0004004402011L, 0x2B01224008800291L, 0x1442C10200248D44L,
            0x141417040010081AL, 0x8492000600102894L, 0x8241080080140080L, 0x41000900213002L, 0x440330100422000L,
            0x81210200420A80L, 0x2AC0013180014480L, 0x20905A00008C0059L, 0x102C0011088210L, 0x4102280120403024L,
            0xB01032001A000A20L, 0x4081010024300028L, 0x4902820040A20012L, 0x3EC10100400A80A3L, 0x1104AC8004804009L,
            0xA51080010011C080L, 0xE445000445000A00L, 0x9980800401220080L, 0x4041001100842800L, 0x3080803004804800L,
            0x205001041002000L, 0x237C400160100240L, 0x623800040059122L, 0x8300048A00C06100L, 0xC00118214050810L,
            0x1200231802004410L, 0x200020130842018L, 0x5480040800100280L, 0x801A2000805000L, 0x8900132100844000L,
            0x780002440088010L };

    public static final long[][] BISHOP_ATTACKS = new long[64][];
    public static final long[][] ROOK_ATTACKS = new long[64][];

    public static void findMagics() {
        long[] bishopMagics = new long[64];
        long[] rookMagics = new long[64];

        for (int square = 0; square < 64; square++) {
            bishopMagics[square] = findMagic(square, true, LookupTables.DIAGONAL_RAYS[square]);
            rookMagics[square] = findMagic(square, false, LookupTables.STRAIGHT_RAYS[square]);
        }

        printMagics("bishop", bishopMagics);
        printMagics("rook", rookMagics);
    }

    public static long findMagic(int square, boolean isBishop, long[] rays) {
        long attackMask = getAttackMask(isBishop, square, rays);
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

    private static long[] getBlockerAttacks(int square, boolean isBishop, long[] blockerCombinations) {
        long[][] attackRays = isBishop ? LookupTables.DIAGONAL_RAYS : LookupTables.STRAIGHT_RAYS;
        long[] blockerAttacks = new long[blockerCombinations.length];

        for (int i = 0; i < blockerCombinations.length; i++) {
            long blockerAttack = 0L;

            for (int j = 0; j < 4; j++) {
                long blockers = blockerCombinations[i];

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

                    blockerMask = ~(LookupTables.BITBOARD_SQUARES[blockerSquare] - 1);
                }

                blockerAttack |= attackRays[square][j] & blockerMask;
            }

            blockerAttacks[i] = blockerAttack;
        }

        return blockerAttacks;
    }

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

    private static long getAttackMask(boolean isbishop, int square, long[] rays) {
        long attackMask = 0;

        for (int i = 0; i < 4; i++) {
            attackMask |= isbishop ? getBishopRayWithoutEdges(rays[i]) : getRookRayWithoutEdges(square, i, rays[i]);
        }

        return attackMask;
    }

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

    private static long getBishopRayWithoutEdges(long ray) {
        long noEdgeMask = 0x7E7E7E7E7E7E00L;

        return ray & noEdgeMask;
    }

    private static long getRookRayWithoutEdges(int square, int direction, long ray) {
        long rayWithoutEdges = ray;

        int squareToRemove = -1;
        switch (direction) {
            case LookupTables.N:
                squareToRemove = (square % 8);
                break;
            case LookupTables.E:
                squareToRemove = square + (7 - (square % 8));
                break;
            case LookupTables.S:
                squareToRemove = 56 + (square % 8);
                break;
            case LookupTables.W:
                squareToRemove = square - (square % 8);
                break;
        }

        rayWithoutEdges &= ~LookupTables.BITBOARD_SQUARES[squareToRemove];

        return rayWithoutEdges;
    }

    public static void main(String[] args) {
        findMagics();
    }
}