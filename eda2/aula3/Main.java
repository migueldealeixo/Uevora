
import java.util.Scanner;


public class Main {

    static int R;
    static long[][] dp;
    static boolean[][][] blocked; 

    public static void main(String[] args) {
     Scanner sc = new Scanner(System.in);
     int C = sc.nextInt();

        
        while(C--> 0){
            R = sc.nextInt();
            int Sx = sc.nextInt();
            int Sy = sc.nextInt();
            int Ex = sc.nextInt();
            int Ey = sc.nextInt();
            int B = sc.nextInt();

            dp = new long[R+1][R+1];
            blocked = new boolean[R+1][R+1][2];

            for(int i = 0; i <B ; i++){
                int Px = sc.nextInt();
                int Py = sc.nextInt();
                char D = sc.next().charAt(0);
                if (D == 'N') blocked[Px][Py][0] = true;
                if (D == 'E') blocked[Px][Py][1] = true;
                if (D == 'S' && Py > 1) blocked[Px][Py - 1][0] = true;
                if (D == 'W' && Px > 1) blocked[Px - 1][Py][1] = true;
            }
            dp[Sx][Sy] = 1;
            for (int x = Sx; x <= Ex; x++) {
                for (int y = Sy; y <= Ey; y++) {
                    if (x > Sx && !blocked[x - 1][y][1]) {
                        dp[x][y] += dp[x - 1][y];
                    }
                    if (y > Sy && !blocked[x][y - 1][0]) {
                        dp[x][y] += dp[x][y - 1];
                    }
                }
        }
        System.out.println(dp[Ex][Ey]);



    }



    }
}
