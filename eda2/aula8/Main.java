import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception
    {
     BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
     String[] firstLine = br.readLine().trim().split("\\s+");
     int n = Integer.parseInt(firstLine[0]);
     int m = Integer.parseInt(firstLine[1]);
     SocialNetwork sn = new SocialNetwork(n);

      for(int i= 0; i < m; i++){
        String[] line = br.readLine().trim().split("\\s+");
        int u = Integer.parseInt(line[0]);
        int v = Integer.parseInt(line[1]);
        int w = Integer.parseInt(line[2]);
        sn.addRelation(u, v, w);
      }
      System.out.println(sn.maximumBackboneWeight());
    }
  }
  
  /*
    Pessoas numa rede social e a idade das suas relações (em dias).
  */
  class SocialNetwork {
   // as variáveis necessárias...
    private int nPersons;
    private List<Edge> edges;

    /*
      Inicializa uma rede social com NPERSONS elementos.
    */
    public SocialNetwork(int nPersons)
    {
      this.nPersons = nPersons;
      this.edges = new ArrayList<>();
    }
  

    static class Edge implements Comparable<Edge>{
      int u,v,weight;

       public Edge(int u,int v, int weight){
        this.u = u;
        this.v = v;
        this.weight = weight;
      }

      @Override
      public int compareTo(Edge other){
        return Integer.compare(other.weight, this.weight);
      }
    }
    /*
      Estabelece a existência de uma relação entre PERSON1 e PERSON2, cuja
      idade é DAYS dias.
    */
    public void addRelation(int p1, int p2, int dias)
    {
      edges.add(new Edge(p1, p2, dias));
    }
    
    private static class DSU{
      int[] pais;
      int[] rank;
      public DSU(int size){
        pais = new int[size+1];
        rank = new int[size+1];
        for(int i = 1; i <= size; i++){
          pais[i] = i;
          rank[i] = 1;
        }
      }

      public int find(int x){
        if(pais[x]!= x){
          pais[x] = find(pais[x]);
        }
        return pais[x];
      }
      public void union(int x, int y){
        int xRaiz = find(x);
        int yRaiz = find(y);
        if(xRaiz == yRaiz) return;

        if(rank[xRaiz]<rank[yRaiz]){
          pais[xRaiz] = yRaiz;
        }else{
          pais[yRaiz] = xRaiz;
          if(rank[xRaiz]== rank[yRaiz]){
            rank[xRaiz]++;
          }
        }
      }
    }
    /*
      Calcula e devolve o peso de um backbone máximo da rede.
    */
    public int maximumBackboneWeight()
    {
      Collections.sort(edges);
      DSU dsu = new DSU(nPersons);
      int total = 0;
      int count = 0;

      for(Edge edge :edges){
        if(count == nPersons-1) break;
        int uRaiz = dsu.find(edge.u);
        int vRaiz = dsu.find(edge.v);
        if(uRaiz != vRaiz){
          dsu.union(edge.u, edge.v);
          total+=edge.weight;
          count++;
        }
      }
      return total;
    }
  } 
    

