import java.io.*;
import java.util.*;

public class Main{
    static final int INF = Integer.MAX_VALUE;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] firstLine = br.readLine().split(" ");
        int N = Integer.parseInt(firstLine[0]); // número de participantes
        int M = Integer.parseInt(firstLine[1]); // declarações de interesse

        int totalNodes = 2 * N + 2; // N à esquerda, N à direita + 1 source + 1 sinkk
        int source = 2 * N;
        int sink = 2 * N + 1;

        List<List<Integer>> adj = new ArrayList<>();
        int[][] capacity = new int[totalNodes][totalNodes]; // capacidade da aresta

        for (int i = 0; i < totalNodes; i++){
            adj.add(new ArrayList<>());
        }

        // lado esquerdo
        for (int i = 0; i < N; i++){
            adj.get(source).add(i);
            adj.get(i).add(source);
            capacity[source][i] = 1;
        }

        // lado direito
        for(int i = 0; i < N; i++){
            int receiver = i + N;
            adj.get(receiver).add(sink);
            adj.get(sink).add(receiver);
            capacity[receiver][sink] = 1;
        }

        for(int i = 0; i < M; i++){
            String[] line = br.readLine().split(" ");
            int a = Integer.parseInt(line[0]);
            int b = Integer.parseInt(line[1]);
            int from = a; // lado esquerdo
            int to = b + N; // lado direito
            adj.get(from).add(to);
            adj.get(to).add(from);
            capacity[from][to] = 1;
        }

        int maxFlow = edmondsKarp(source, sink, capacity, adj);
        System.out.println(maxFlow == N ? "YES" : "NO");

    }

    static int edmondsKarp(int source, int sink, int[][] capacity, List<List<Integer>> adj) {
        int flow = 0;
        int[] parent = new int[capacity.length];

        while (bfs(source, sink, parent, capacity, adj)) {
            // encontrar a capacidade minima
            int pathFlow = INF;
            int cur = sink;
            while (cur != source) {
                int prev = parent[cur];
                pathFlow = Math.min(pathFlow, capacity[prev][cur]);
                cur = prev;
            }

            // atualiza as capacidades
            cur = sink;
            while (cur != source) {
                int prev = parent[cur];
                capacity[prev][cur] -= pathFlow;
                capacity[cur][prev] += pathFlow;
                cur = prev;
            }

            flow += pathFlow;
        }

        return flow;
    }

    static boolean bfs(int source, int sink, int[] parent, int[][] capacity, List<List<Integer>> adj) {
        Arrays.fill(parent, -1);
        parent[source] = -2;
        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (int next : adj.get(current)) {
                if (parent[next] == -1 && capacity[current][next] > 0) {
                    parent[next] = current;
                    if (next == sink) {
                        return true;
                    }
                    queue.add(next);
                }
            }
        }

        return false;
    }
}
