import java.util.*;

/**
 * Q5: Comparator Correctness, Generics Reusability, and Faulty Comparator Demonstration
 */
public class ComparatorAnalysis {

    static class Player {
        final String name;
        final int score;
        final int penalties;

        Player(String name, int score, int penalties) {
            this.name = name;
            this.score = score;
            this.penalties = penalties;
        }

        @Override
        public String toString() {
            return name + "(Score:" + score + ",Penalties:" + penalties + ")";
        }
    }

    public static void main(String[] args) {
        System.out.println("--- 1. Generic Reuse of Leaderboard for Quiz Players ---");
        Comparator<Player> playerRank = Comparator
                .comparingInt((Player p) -> p.score).reversed()
                .thenComparingInt(p -> p.penalties) // lower penalties is better
                .thenComparing(p -> p.name);

        Leaderboard<Player> quizBoard = new Leaderboard<>(playerRank);
        quizBoard.add(new Player("Alice", 100, 2));
        quizBoard.add(new Player("Bob", 100, 1));
        quizBoard.add(new Player("Charlie", 85, 0));
        System.out.println("Quiz Leaderboard: " + quizBoard.ranking());

        System.out.println("\n--- 2. Subtraction Bug in Faulty Comparator Demonstration ---");
        // Faulty comparator using naive integer subtraction (t2.points - t1.points)
        // With extreme values near Integer.MIN_VALUE or Integer.MAX_VALUE:
        int valA = Integer.MAX_VALUE - 5;
        int valB = -100;
        int diff = valB - valA; // Underflow occurs!
        System.out.println("valA = " + valA + ", valB = " + valB);
        System.out.println("Naive (valB - valA) = " + diff + " (Positive! Wrongly asserts valB > valA due to overflow)");
        System.out.println("Integer.compare(valA, valB) = " + Integer.compare(valA, valB) + " (Correctly avoids overflow)");
    }
}
