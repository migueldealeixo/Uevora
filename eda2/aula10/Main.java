import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int E = Integer.parseInt(br.readLine());
        Organisation org = new Organisation(E);
        for (int i = 0; i < E; i++) {
            String[] parts = br.readLine().split(" ");
            int N = Integer.parseInt(parts[0]);
            for (int j = 1; j <= N; j++) {
                int friend = Integer.parseInt(parts[j]);
                org.addFriend(i, friend);
            }
        }
        int T = Integer.parseInt(br.readLine());
        for (int t = 0; t < T; t++) {
            int source = Integer.parseInt(br.readLine());
            Boom boom = org.firstMaxBoom(source);
            if (boom.getSize() == 0) {
                System.out.println(0);
            } else {
                System.out.println(boom.getSize() + " " + boom.getDay());
            }
        }
    }
}

class Boom {
    private int size;
    private int day;

    public Boom(int size, int day) {
        this.size = size;
        this.day = day;
    }

    public int getSize() {
        return size;
    }

    public int getDay() {
        return day;
    }
}

class Organisation {
    private List<List<Integer>> adj;

    public Organisation(int employees) {
        adj = new ArrayList<>(employees);
        for (int i = 0; i < employees; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addFriend(int employee, int friend) {
        adj.get(employee).add(friend);
    }

    public Boom firstMaxBoom(int source) {
        int E = adj.size();
        boolean[] visited = new boolean[E];
        Queue<Integer> queue = new LinkedList<>();
        int maxSize = 0;
        int maxDay = 0;

        visited[source] = true;
        queue.add(source);

        int currentDay = 0;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            currentDay++;
            int count = 0;

            for (int i = 0; i < levelSize; i++) {
                int current = queue.poll();

                for (int friend : adj.get(current)) {
                    if (!visited[friend]) {
                        visited[friend] = true;
                        queue.add(friend);
                        count++;
                    }
                }
            }

            if (count > 0) {
                if (count > maxSize) {
                    maxSize = count;
                    maxDay = currentDay;
                }
            }
        }

        return maxSize == 0 ? new Boom(0, 0) : new Boom(maxSize, maxDay);
    }
}