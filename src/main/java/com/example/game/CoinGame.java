package com.example.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoinGame {
    public static void main(String[] args) {
        // Example array of coins
        int[] coins = {4, 15, 7, 3, 8, 9};
        int n = coins.length;

        // Create DP table
        int[][] dp = new int[n][n];
        int[][] moves = new int[n][n]; // To track moves

        // Fill base cases
        for (int i = 0; i < n; i++) {
            dp[i][i] = coins[i];
        }

        for (int len = 2; len <= n; len++) { // len is the interval length
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;

                int pickStart = coins[i] + Math.min(getValue(dp, i + 2, j), getValue(dp, i + 1, j - 1));
                int pickEnd = coins[j] + Math.min(getValue(dp, i + 1, j - 1), getValue(dp, i, j - 2));

                if (pickStart >= pickEnd) {
                    dp[i][j] = pickStart;
                    moves[i][j] = i; // Start coin picked
                } else {
                    dp[i][j] = pickEnd;
                    moves[i][j] = j; // End coin picked
                }
            }
        }

        // Simulate the game
        List<Integer> player1Choices = new ArrayList<>();
        List<Integer> player2Choices = new ArrayList<>();
        simulateGame(coins, moves, dp, player1Choices, player2Choices);

        // Output the results
        System.out.println("Maximum coins Player 1 can guarantee: " + dp[0][n - 1]);
        System.out.println("DP Table:");
        printTable(dp);

        System.out.println("Player 1 choices: " + player1Choices);
        System.out.println("Player 2 choices: " + player2Choices);

        int player1Total = player1Choices.stream().mapToInt(Integer::intValue).sum();
        int player2Total = player2Choices.stream().mapToInt(Integer::intValue).sum();
        System.out.println("Player 1 total: " + player1Total);
        System.out.println("Player 2 total: " + player2Total);
    }

    // Simulate the game to track choices for both players
    private static void simulateGame(int[] coins, int[][] moves, int[][] dp, List<Integer> player1Choices, List<Integer> player2Choices) {
        int i = 0, j = coins.length - 1;
        boolean isPlayer1Turn = true;

        while (i <= j) {
            int pick;
            if (isPlayer1Turn) {
                // Player 1 picks optimally based on dp[i][j]
                pick = (dp[i][j] == coins[i] + Math.min(getValue(dp, i + 2, j), getValue(dp, i + 1, j - 1))) ? i : j;
                player1Choices.add(coins[pick]);
            } else {
                // Player 2 also picks optimally based on dp[i][j]
                pick = (dp[i][j] == coins[i] + Math.min(getValue(dp, i + 2, j), getValue(dp, i + 1, j - 1))) ? i : j;
                player2Choices.add(coins[pick]);
            }

            // Move indices based on the coin picked
            if (pick == i) {
                i++;
            } else {
                j--;
            }
            isPlayer1Turn = !isPlayer1Turn; // Alternate turns
        }
    }

    // Utility to safely get DP table values (out-of-bound checks)
    private static int getValue(int[][] dp, int i, int j) {
        if (i > j) return 0; // No coins left in this range
        return dp[i][j];
    }

    // Utility to print a 2D table
    private static void printTable(int[][] table) {
        for (int[] row : table) {
            System.out.println(Arrays.toString(row));
        }
    }
}
