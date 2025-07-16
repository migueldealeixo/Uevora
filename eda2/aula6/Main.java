import java.io.*;
import java.util.*;

public class Main {
    static int[][] dp;
    static int L, P, TOP;
    static String texto, transcricao;
    static Map<Character, int[]> teclado = new HashMap<>();

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int N = Integer.parseInt(br.readLine());

        for (int i = 0; i < N; i++) {
            String linha = br.readLine();
            for (int j = 0; j < linha.length(); j++) {
                teclado.put(linha.charAt(j), new int[]{i, j});
            }
        }

        texto = br.readLine();
        transcricao = br.readLine();
        L = texto.length();
        P = transcricao.length();

        TOP = calcularTOP();

        dp = new int[L + 1][P + 1];

        for (int i = 0; i <= L; i++) {
            Arrays.fill(dp[i], Integer.MIN_VALUE);
        }

        dp[0][0] = 0;

        for (int i = 0; i <= L; i++) {
            for (int j = 0; j <= P; j++) {
                if (i > 0 && dp[i - 1][j] != Integer.MIN_VALUE) {
                    dp[i][j] = Math.max(dp[i][j], dp[i - 1][j] - (TOP + 1));
                }
                if (i > 0 && j > 0 && dp[i - 1][j - 1] != Integer.MIN_VALUE) {
                    char c1 = texto.charAt(i - 1);
                    char c2 = transcricao.charAt(j - 1);
                    int ganho = (c1 == c2) ? TOP + 1 : TOP + 1 - distancia(c1, c2);
                    dp[i][j] = Math.max(dp[i][j], dp[i - 1][j - 1] + ganho);
                }
                
        }
    }
        System.out.println(dp[L][P]);
    }

    static int distancia(char a, char b) {
        int[] pa = teclado.get(a);
        int[] pb = teclado.get(b);
        return Math.max(Math.abs(pa[0] - pb[0]), Math.abs(pa[1] - pb[1]));
    }

    static int calcularTOP() {
        int max = 0;
        for (char a : teclado.keySet()) {
            for (char b : teclado.keySet()) {
                max = Math.max(max, distancia(a, b));
            }
        }
        return max;
    }
}
