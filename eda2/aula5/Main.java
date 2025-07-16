
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

class Main {
    public static void main(String[] args) throws Exception
    {
     // leitura dos dados e controlo da execução do programa
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String[] line = br.readLine().split(" ");
        int N = Integer.parseInt(line[0]);
        int M = Integer.parseInt(line[1]);

        Project project = new Project(N);

        for(int i = 0; i < M; i++){
            String[] parts = br.readLine().split(" ");
            int[] rule = new int[parts.length];
            for(int j = 0; j < parts.length;j++){
                rule[j] = Integer.parseInt(parts[j]);
            }
            project.addDependencies(rule);
        }

        int[] order = project.computeOrder();
        StringBuilder sb = new StringBuilder();
for (int i = 0; i < order.length; i++) {
    sb.append(order[i]);
    if (i < order.length - 1) {
        sb.append(" ");
    }
}
System.out.println(sb.toString());

        }
  }
  
  class Project {
   // as variáveis necessárias...
    private final List<List<Integer>> graph;
    private final int[] inDegree;
    private final int numTasks;

    /*
      Cria um projecto com TASKS tarefas.
    */
    public Project(int tasks)
    {
     this.numTasks = tasks;
     this.graph = new ArrayList<>();
     for(int i= 0; i <= tasks; i++){
        graph.add(new ArrayList<>());
     }
     this.inDegree = new int[tasks+1];
    }
  
    /*
      Acrescenta a tarefa PRECEDENT às tarefas de que a tarefa TASK
      depende.
    */
    public void addDependency(int task, int precedent)
    {
      graph.get(precedent).add(task);
      inDegree[task]++;
    }
  
    /*
      Acrescenta as dependências indicadas na regra RULE.
  
      [Em alternativa ou como complemento do método anterior.]
    */
    public void addDependencies(int[] rule)
    {
        int task = rule[0];
        int k = rule[1];
        for (int i = 0; i < k; i++) {
            int precedent = rule[2 + i];
            addDependency(task, precedent);
        }
    }
  
    /*
      Calcula uma ordem possível de execução das tarefas do projecto e
      devolve um array que contém as tarefas por essa ordem.
  
      Na ordem calculada, sempre que há duas ou mais tarefas que é
      possível executar, a com menor número aparece primeiro.
    */
    public int[] computeOrder() {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        List<Integer> result = new ArrayList<>();

        // Adicionar tarefas sem dependências (grau de entrada 0)
        for (int i = 1; i <= numTasks; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        // Kahn’s Algorithm com PriorityQueue
        while (!queue.isEmpty()) {
            int current = queue.poll();
            result.add(current);

            for (int neighbor : graph.get(current)) {
                inDegree[neighbor]--;
                if (inDegree[neighbor] == 0) {
                    queue.add(neighbor);
                }
            }
        }

        // Converter para array
        int[] order = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            order[i] = result.get(i);
        }

        return order;
    }
  }