
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Main{
    static final int INF = Integer.MAX_VALUE;
    static int N, M;
    static List<Integer>[] adj;
    static int[][] capacity;
    static int[] parent;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] first = br.readLine().split(" ");
        N = Integer.parseInt(first[0]);
        M = Integer.parseInt(first[1]);

        int totalNodes = 2*N +2;
        int source = 2*N;
        int sink = 2*N+1;

        capacity = new int[totalNodes][totalNodes];
        adj = new ArrayList[totalNodes];
        for(int i = 0; i < totalNodes; i++) adj[i] = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            adj[source].add(i);
            adj[i].add(source);
            capacity[source][i] = 1;
        }

        for (int i = 0; i < N; i++) {
            int targetNode = N + i;
            adj[targetNode].add(sink);
            adj[sink].add(targetNode);
            capacity[targetNode][sink] = 1;
        }

        for (int i = 0; i < M; i++) {
            String[] parts = br.readLine().split(" ");
            int a = Integer.parseInt(parts[0]);
            int b = Integer.parseInt(parts[1]);

            int from = a;
            int to = N + b; // b is on the "target" side

            adj[from].add(to);
            adj[to].add(from);
            capacity[from][to] = 1;
        }

        int flow = edmondsKarp(source, sink, totalNodes);
        System.out.println(flow == N ? "YES" : "NO");
    }

    static int edmondsKarp(int source, int sink,int totalNodes){
        int flow = 0;
        parent = new int[totalNodes];


        while (true) {
            Arrays.fill(parent, -1);
            parent[source] = -2;
            Queue<int[]> q = new LinkedList<>();
            q.add(new int[]{source, INF});

            while (!q.isEmpty()) {
                int[] cur = q.poll();
                int node = cur[0], flowSoFar = cur[1];

                for (int next : adj[node]) {
                    if (parent[next] == -1 && capacity[node][next] > 0) {
                        parent[next] = node;
                        int newFlow = Math.min(flowSoFar, capacity[node][next]);
                        if (next == sink) {
                            flow += newFlow;
                            updateCap(next, source, newFlow);
                            break;
                        }
                        q.add(new int[]{next, newFlow});
                    }
                }
            }

            if (parent[sink] == -1) break; // no more paths
        }

        return flow;
    }

    static void updateCap(int cur, int source, int flow) {
        while (cur != source) {
            int prev = parent[cur];
            capacity[prev][cur] -= flow;
            capacity[cur][prev] += flow;
            cur = prev;
        }
    }
    }

  


    





