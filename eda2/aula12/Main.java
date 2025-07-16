import java.io.*;
import java.util.*;

class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] parts = br.readLine().split(" ");
        int R = Integer.parseInt(parts[0]);
        int C = Integer.parseInt(parts[1]);

        Maze maze = new Maze(R, C);

        for (int i = 0; i < C; i++) {
            String line = br.readLine();
            if (line == null) break;
            parts = line.split(" ");
            int from = Integer.parseInt(parts[0]);
            int to = Integer.parseInt(parts[1]);
            char type = parts[2].charAt(0);
            int coins = Integer.parseInt(parts[3]);

            maze.addCorridor(from, to, type, coins);
        }

        boolean result = maze.contestantMayLoose(0, R - 1);
        System.out.println(result ? "yes" : "no");
    }
}

class Maze {
    private int rooms;
    private List<Edge> edges;
    private List<List<Integer>> adj;
    private List<List<Integer>> adjReverse;

    private static class Edge {
        int from, to, weight;
        public Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    public Maze(int rooms, int corridors) {
        this.rooms = rooms;
        this.edges = new ArrayList<>(corridors);
        this.adj = new ArrayList<>(rooms);
        this.adjReverse = new ArrayList<>(rooms);

        for (int i = 0; i < rooms; i++) {
            adj.add(new ArrayList<>());
            adjReverse.add(new ArrayList<>());
        }
    }

    public void addCorridor(int from, int to, char type, int coins) {
        int weight = (type == 'B') ? coins : -coins;
        edges.add(new Edge(from, to, weight));
        adj.get(from).add(to);
        adjReverse.get(to).add(from);
    }

    public boolean contestantMayLoose(int start, int end) {
        boolean[] reachableFromStart = bfs(start, adj);
        boolean[] canReachEnd = bfsReverse(end, adjReverse);

        if (!reachableFromStart[end]) return false;

        List<Edge> prunedEdges = new ArrayList<>();
        for (Edge e : edges) {
            if (reachableFromStart[e.from] && canReachEnd[e.to]) {
                prunedEdges.add(e);
            }
        }

        long[] dist = new long[rooms];
        Arrays.fill(dist, Long.MAX_VALUE);
        dist[start] = 0;

        Queue<Integer> queue = new LinkedList<>();
        boolean[] inQueue = new boolean[rooms];
        queue.add(start);
        inQueue[start] = true;

        int[] count = new int[rooms];
        boolean hasNegativeCycle = false;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            inQueue[u] = false;

            for (Edge e : prunedEdges) {
                if (e.from != u) continue;
                int v = e.to;

                if (dist[u] != Long.MAX_VALUE && dist[u] + e.weight < dist[v]) {
                    dist[v] = dist[u] + e.weight;
                    if (!inQueue[v]) {
                        queue.add(v);
                        inQueue[v] = true;
                        count[v]++;
                        if (count[v] >= rooms) {
                            hasNegativeCycle = true;
                            break;
                        }
                    }
                }
            }
            if (hasNegativeCycle) break;
        }

        if (dist[end] < 0) return true;

        return hasNegativeCycle;
    }

    private boolean[] bfs(int start, List<List<Integer>> adjacency) {
        boolean[] visited = new boolean[rooms];
        Queue<Integer> queue = new LinkedList<>();
        queue.add(start);
        visited[start] = true;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : adjacency.get(u)) {
                if (!visited[v]) {
                    visited[v] = true;
                    queue.add(v);
                }
            }
        }
        return visited;
    }

    private boolean[] bfsReverse(int end, List<List<Integer>> reverseAdj) {
        boolean[] visited = new boolean[rooms];
        Queue<Integer> queue = new LinkedList<>();
        queue.add(end);
        visited[end] = true;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : reverseAdj.get(u)) {
                if (!visited[v]) {
                    visited[v] = true;
                    queue.add(v);
                }
            }
        }
        return visited;
    }
}