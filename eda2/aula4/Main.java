
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;

class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int A = Integer.parseInt(st.nextToken());
        int B = Integer.parseInt(st.nextToken());
        int E = Integer.parseInt(st.nextToken());
        int P = Integer.parseInt(st.nextToken());

        Organisation org = new Organisation(E);

        for (int i = 0; i < P; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            org.setOutperformed(x, y);
        }

        Promotions result = org.promotions(A, B);
        System.out.println(result);
    }
}
  
  /*
    Promotions é um objecto que contém o número de funcionários que
    serão certamente promovidos nos limites de um intervalo de possíveis
    promoções e o número de funcionários que não serão promovidos mesmo
    no limite superior do intervalo.
  */
  class Promotions {
    int certainlyMin, certainlyMax, impossible;

    public Promotions(int certainlyMin, int certainlyMax, int impossible){
        this.certainlyMax = certainlyMax;
        this.certainlyMin = certainlyMin;
        this.impossible = impossible;
    }
    
    @Override
    public java.lang.String toString() {
        return certainlyMin + "\n" + certainlyMax + "\n" + impossible;
    }
    
  }
  
  @SuppressWarnings("unchecked")
  class Organisation {
    int employees;
    List<Integer>[] graph;
    List<Integer>[] reverseGraph;

    public Organisation(int employees) {
        this.employees = employees;
        graph = new ArrayList[employees];
        reverseGraph = new ArrayList[employees];
        for (int i = 0; i < employees; i++) {
            graph[i] = new ArrayList<>();
            reverseGraph[i] = new ArrayList<>();
        }
    }

  

    /*
      Estabelece que o funcionário EMPLOYEE teve melhor desempenho do que
      o funcionário OTHER.
    */
    public void setOutperformed(int employee, int other)
    {
      graph[employee].add(other);
      reverseGraph[other].add(employee);
    }
    private int countReachable(List<Integer>[] g, int start) {
        boolean[] visited = new boolean[employees];
        Queue<Integer> queue = new LinkedList<>();
        queue.add(start);
        visited[start] = true;

        int count = 1; // inclui ele próprio

        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (int neighbor : g[current]) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    count++;
                    queue.add(neighbor);
                }
            }
        }

        return count;
    }

    /*
      Calcula e devolve o número de funcionários que serão certamente
      promovidos se houver MIN e se houver MAX promoções, e o número de
      funcionários que não serão promovidos mesmo que haja MAX promoções.
    */
    public Promotions promotions(int min, int max) {
        int certainlyMin = 0;
        int certainlyMax = 0;
        int impossible = 0;

        for (int i = 0; i < employees; i++) {
            int before = countReachable(reverseGraph, i); // pessoas que o precedem (inclusive)
            int after = countReachable(graph, i);         // pessoas que ele precede (inclusive)

            if (before > max) {
                impossible++;
            }
            if ((employees - after) < min) {
                certainlyMin++;
            }
            if ((employees - after) < max) {
                certainlyMax++;
            }
        }

        return new Promotions(certainlyMin, certainlyMax, impossible);
    }
}

  