import java.io.*;
import java.util.*;

public class Main{

    static int N, D;
    static int[] numeros, sonhos;

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] prLine = br.readLine().split(" ");
        N = Integer.parseInt(prLine[0]);
        D = Integer.parseInt(prLine[1]);
        numeros = new int[N];
        String[] numberLine = br.readLine().split(" ");

        for (int i = 0; i < N; i++) {
            numeros[i] = Integer.parseInt(numberLine[i]);
        }
        sonhos = new int[D];
        for (int i = 0; i < D; i++) {
            sonhos[i] = Integer.parseInt(br.readLine().trim());
        }

        Arrays.sort(numeros);
        int result = minWaste();
        System.out.println(result);
    }
    static int minWaste() {
        int[] dp = new int[D + 1];
        Arrays.fill(dp, Integer.MAX_VALUE); 
        dp[0] = 0; 
        for (int i = 0; i < D; i++) {
            for (int num : numeros) {
                int totalSZA = 0;
                for (int j = i; j < D && totalSZA + sonhos[j] <= num; j++) {
                    totalSZA += sonhos[j];
                    dp[j + 1] = Math.min(dp[j + 1], num - totalSZA + dp[i]);
                }
            }
        }
        return dp[D];
    }
}
