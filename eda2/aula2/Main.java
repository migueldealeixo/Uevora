import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static List<String> minCoins(int[] coins, int[] queries) {
        int maxQ = Arrays.stream(queries).max().orElse(0);
        int[] dp = new int[maxQ + 1];
        int[][] usedCoins = new int[maxQ + 1][coins.length];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int i = 1; i <= maxQ; i++) {
            for (int j = 0; j < coins.length; j++) {
                if (coins[j] <= i && dp[i - coins[j]] != Integer.MAX_VALUE) {
                    if (dp[i - coins[j]] + 1 < dp[i]) {
                        dp[i] = dp[i - coins[j]] + 1;
                        usedCoins[i] = Arrays.copyOf(usedCoins[i - coins[j]], coins.length);
                        usedCoins[i][j]++;
                    }
                }
            }
        }

        List<String> results = new ArrayList<>();
        for (int q : queries) {
            int count = dp[q];
            List<Integer> coinsUsed = new ArrayList<>();
            for (int i = 0; i < coins.length; i++) {
                for (int j = 0; j < usedCoins[q][i]; j++) {
                    coinsUsed.add(coins[i]);
                }
            }
            Collections.sort(coinsUsed);
            results.add(q + ": [" + count + "] " + coinsUsed.stream().map(String::valueOf).collect(Collectors.joining(" ")));
        }

        return results;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();
        int[] coins = new int[N];
        for (int i = 0; i < N; i++) {
            coins[i] = scanner.nextInt();
        }
        int P = scanner.nextInt();
        int[] queries = new int[P];
        for (int i = 0; i < P; i++) {
            queries[i] = scanner.nextInt();
        }
        scanner.close();

        for (String result : minCoins(coins, queries)) {
            System.out.println(result);
        }
    }
}
