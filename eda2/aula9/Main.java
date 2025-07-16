
import java.io.BufferedReader;
import java.io.InputStreamReader;

class Main {
    public static void main(String[] args) throws Exception
    {
      //leitura dos dados e controlo da execução do programa
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int S = Integer.parseInt(br.readLine());
        PolygonPhobia pp = new PolygonPhobia(S);
        for (int i = 0; i < S; i++) {
            String[] parts = br.readLine().split(" ");
            int x1 = Integer.parseInt(parts[0]);
            int y1 = Integer.parseInt(parts[1]);
            int x2 = Integer.parseInt(parts[2]);
            int y2 = Integer.parseInt(parts[3]);
            pp.addSegment(x1, y1, x2, y2);
        }
        System.out.println(pp.drawableSegments());
    }
  }
  
  /*
    Os segmentos de recta a desenhar.
  */
  class PolygonPhobia {
    //as variáveis necessárias...
    private int[] parent;
    private int[] rank;
    private int count;
    /*
      Inicializa um conjunto de NSEGMENTS segmentos.
    */
    public PolygonPhobia(int nSegments)
    {
        int size = 1000 * 1000;
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
        count = 0;
    }
    private int getKey(int x, int y) {
        return x * 1000 + y;
    }
    /*
      Acrescenta o segmento entre o ponto (X1,Y1) e o ponto (X2,Y2).
    */
    
    public void addSegment(int x1, int y1, int x2, int y2) {
        int u = getKey(x1, y1);
        int v = getKey(x2, y2);
        int rootU = find(u);
        int rootV = find(v);
        if (rootU != rootV) {
            if (rank[rootU] > rank[rootV]) {
                parent[rootV] = rootU;
            } else if (rank[rootU] < rank[rootV]) {
                parent[rootU] = rootV;
            } else {
                parent[rootV] = rootU;
                rank[rootU]++;
            }
            count++;
        }
    }    
    
    private int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    /*
      Calcula e devolve o número de segmentos de recta que é possível
      desenhar sem desenhar um polígono (fechado).
    */
    public int drawableSegments()
    {
      return count;
    }
  }
  