
import java.io.*;
import java.util.*;

public class Main {
    
    static int N, D;
    static int[] numeros, sonhos,memo;

    public static void main(String[] args) throws IOException{
        long StartTime = System.nanoTime();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] prLine = br.readLine().split(" ");
        N = Integer.parseInt(prLine[0]);
        D = Integer.parseInt(prLine[1]);
        numeros = new int[N];
        String[] numberLine = br.readLine().split(" ");
        
        for (int i = 0; i < N; i++){
            numeros[i] = Integer.parseInt(numberLine[i]);
        }
        sonhos = new int[D];
        for (int i = 0; i < D; i++){
            sonhos[i] = Integer.parseInt(br.readLine().trim());
        }

        Arrays.sort(numeros);

        memo = new int[D + 1];
        Arrays.fill(memo, -1);
        int result = minWaste(0); // <--- primeiro index dos sonhos
        System.out.println(result);
        long endTime = System.nanoTime();
        System.out.println(StartTime);
        System.out.println(endTime);
        long time = (endTime-StartTime) / 100000000;
        System.out.println(time);
        
        
    }

     /*
          Recursiva
    */
    static int minWaste(int C){

        if(C >= sonhos.length){
          return 0;  
        }

        if(memo[C] != -1){
            return memo[C];
        }

        int waste = Integer.MAX_VALUE;


        for(int num : numeros){
            int totalSZA = 0;
            int b = C;

            while (b < sonhos.length && totalSZA + sonhos[b] <= num){
                totalSZA += sonhos[b];
                int dp = num - totalSZA + minWaste(b + 1);
                waste = Math.min(waste, dp);
                b++;
            }
        }
        System.out.println("C: " + C + " | Waste: " + waste);
        memo[C] = waste;
        return waste;


        
    }

    
}
