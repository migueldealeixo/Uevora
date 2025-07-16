
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

class Main {
  public static void main(String[] args) throws Exception
  {
    //leitura dos dados e controlo da execução do programa
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    int N = Integer.parseInt(br.readLine());
    int E = Integer.parseInt(br.readLine());
    int T = Integer.parseInt(br.readLine());
    int M = Integer.parseInt(br.readLine());

    MiceMaze mm = new MiceMaze(N);
    for(int i = 0; i <M ; i++){
        String[] partes = br.readLine().split(" ");
        int a = Integer.parseInt(partes[0]);
        int b = Integer.parseInt(partes[1]);
        int t = Integer.parseInt(partes[2]);
        mm.addConnection(b, a, t);

    }
    System.out.println(mm.miceOut(E, T));
  }
}

/*
  Descrição de um labirinto.
*/
class MiceMaze {
  //as variáveis necessárias...
  private List<List<int[]>> adj;
  
  /*
    Inicializa um labirinto com CELLS células.
  */
  public MiceMaze(int cells)
  {
    adj = new ArrayList<>(cells +1);
    for(int i = 0; i <= cells;i++){
        adj.add(new ArrayList<>());
    }

    }

  /*
    Acrescenta a ligação da célula SOURCE para a célula DESTINATION,
    que demora PENALTY unidades de tempo a ser atravessada.
  */
  public void addConnection(int source, int destination, int penalty)
  {
   adj.get(source).add(new int[]{destination,penalty});
    }

  /*
    Calcula e devolve o número de ratos que conseguem chegar à célula
    EXIT do labirinto em TIME unidades de tempo.
  */
  public int miceOut(int exit, int time)
  {
    int n = adj.size()-1;
    int[] dist = new int[n+1];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[exit] = 0;

    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
    pq.add(new int[]{exit,0});

    while(!pq.isEmpty()){
        int[] current = pq.poll();
        int u = current[0];
        int distCurr = current[1];

        if(distCurr > dist[u]) continue;

        for(int[] edge : adj.get(u)){
            int v = edge[0];
            int w = edge[1];
            if(dist[v]> distCurr +w){
                dist[v] = distCurr +w;
                pq.add(new int[]{v,dist[v]});
            }
        }
    }
    int count = 0;
    for(int i = 0; i <= n; i++){
        if(dist[i] <= time){
            count++;
        }
    }
    return count;
  }

}