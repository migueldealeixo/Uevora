package aula1;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws IOException {

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        int max = Integer.MIN_VALUE;
        int lines = Integer.parseInt(input.readLine());

        for (int i = 0; i < lines; i++) {

            String[] children;
            children = input.readLine().split(" ");
            int sticks = Integer.parseInt(children[0]);

            for (int j = 1; j <= sticks; j++) {

                int value = Integer.parseInt(children[j]);
                if (value > max) {
                    max = value;
                }

            }

        }
        System.out.println(max);
    }

}