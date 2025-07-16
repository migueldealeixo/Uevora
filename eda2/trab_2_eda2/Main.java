
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;


public class Main {
    long strat = System.nanoTime();
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());
        if(n == 1){
            System.out.println(0);
            return;
        }
        List<List<Integer>> adj = new ArrayList<>();
        for(int i=0;i<=n;i++){
            adj.add(new ArrayList<>());
        }
         for (int i = 0; i < n - 1; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            int a = Integer.parseInt(st.nextToken());
            int b = Integer.parseInt(st.nextToken());
            adj.get(a).add(b);
            adj.get(b).add(a);
        }
        int[] BFS_1 = bfs(1, adj, n);
        int u = BFS_1[0];
        int[] BFS_2 = bfs(u, adj, n);
        System.out.println(BFS_2[1]);
    }

  public static int[] bfs(int start, List<List<Integer>> adj, int n) {
        int[] visited = new int[n + 1];
        Arrays.fill(visited, -1);
        Queue<Integer> queue = new LinkedList<>();
        queue.add(start);
        visited[start] = 0;
        int maxDist = 0;
        int farthestNode = start;
        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (int neighbor : adj.get(current)) {
                if (visited[neighbor] == -1) {
                    visited[neighbor] = visited[current] + 1;
                    queue.add(neighbor);
                    if (visited[neighbor] > maxDist) {
                        maxDist = visited[neighbor];
                        farthestNode = neighbor;
                    }
                }
            }
        }
        return new int[]{farthestNode, maxDist};
    }
    long end = System.nanoTime();
    long total = end - strat;
}