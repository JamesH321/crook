package com.github.jamesh321.chessengine;

import java.util.Random;

public class MagicBitboards {
    public static final long[] BISHOP_MAGICS = {};
    public static final long[] ROOK_MAGICS = {};

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