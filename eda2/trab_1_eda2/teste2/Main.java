
package teste2;

import java.io.*;
import java.util.*;

public class Main {

    static int N, D;
    static int[] numbers, dreams;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] firstLine = br.readLine().split(" ");
        N = Integer.parseInt(firstLine[0]);
        D = Integer.parseInt(firstLine[1]);

        numbers = new int[N];
        String[] numberLine = br.readLine().split(" ");
        for (int i = 0; i < N; i++) {
            numbers[i] = Integer.parseInt(numberLine[i]);
        }

        dreams = new int[D];
        for (int i = 0; i < D; i++) {
            dreams[i] = Integer.parseInt(br.readLine().trim());
        }

        Arrays.sort(numbers);
        int result = minWaste();
        System.out.println(result);
    }

    static int minWaste() {
        int[] dp = new int[D + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        for (int i = 0; i < D; i++) {
            if (dp[i] == Integer.MAX_VALUE) continue; 

            for (int num : numbers) {
                int totalSize = 0;
                for (int j = i; j < D && totalSize + dreams[j] <= num; j++) {
                    totalSize += dreams[j];
                    dp[j + 1] = Math.min(dp[j + 1], dp[i] + (num - totalSize));
                }
            }
        }
        return dp[D];
    }
}