import java.io.*;

public class clienteServer {
    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static remoteObject obj;
    public static void main(String[] args) {
        String regHost = "localhost";
        String regPort = "9000";

        if (args.length != 2) {
            System.out.println("Faltam argumentos!");
            System.exit(1);
        }

        regHost = args[0];
        regPort = args[1];
        try {
            obj = (remoteObject) java.rmi.Naming.lookup("rmi://" + regHost + ":" + regPort + "/remoteobject");

            System.out.println("Bem vindo ao Sistema de Artistas de Rua do Alentejo!\n");

        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}