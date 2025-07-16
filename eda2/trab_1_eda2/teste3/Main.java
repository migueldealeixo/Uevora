import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int N = scanner.nextInt(); 
        int D = scanner.nextInt(); 

        int[] numeros = new int[N];  
        for (int i = 0; i < N; i++) {
            numeros[i] = scanner.nextInt();
        }

        int[] sonhos = new int[D];   
        for (int i = 0; i < D; i++) {
            sonhos[i] = scanner.nextInt();
        }

        scanner.close();

        Arrays.sort(numeros); 
        Arrays.sort(sonhos);  

        int result = minimoDesperdicio(numeros, sonhos, D);
        
        System.out.println(result);
    }

    private static int minimoDesperdicio(int[] numeros, int[] sonhos, int D) {
        int[] prefixSum = new int[D + 1]; 
        for (int i = 1; i <= D; i++) {
            prefixSum[i] = prefixSum[i - 1] + sonhos[i - 1];
        }

        final int INF = 1000000; 

        int[] pd = new int[D + 1]; 
        Arrays.fill(pd, INF); 
        pd[0] = 0; 

        for (int num : numeros) { 
            for (int j = D; j > 0; j--) { 
                for (int k = j; k >= 0; k--) { 
                    int sum = prefixSum[j] - prefixSum[k]; 
                    if (sum > num) break; 
                    
                    int desperdicio = num - sum; 
                    pd[j] = Math.min(pd[j], pd[k] + desperdicio); 
                }
            }
        }

        return pd[D] == INF ? -1 : pd[D]; 
    }
}
