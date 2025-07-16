import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args){
        Scanner s = new Scanner(System.in);
        int N = s.nextInt();
        int[] moedas = new int[N];
        for(int i = 0; i < N; i++){
            moedas[i] = s.nextInt();
        }
        int P = s.nextInt();
        int[] quantias = new int[P];
        for(int j = 0; j < P; j++){
            quantias[j] = s.nextInt();
        }
        s.close();
        for(int q : quantias){
            int minCoins = minimoMoedas(moedas,q);
            System.out.println(q + ": [" + minCoins + "]");
        }
    }

    public static int minimoMoedas(int[] moedas, int quantias){
        int[] dp = new int[quantias +1];
        Arrays.fill(dp,Integer.MAX_VALUE);
        dp[0] = 0;

        for(int moeda : moedas){
            for(int j = moeda; j <= quantias; j++){
                if (dp[j - moeda] != Integer.MAX_VALUE) {
                    dp[j] = Math.min(dp[j], dp[j - moeda] + 1);
                }
            }
        }
        return dp[quantias] == Integer.MAX_VALUE ? -1 : dp[quantias];
    }
}
