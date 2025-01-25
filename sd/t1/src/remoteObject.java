import java.util.*;

public interface remoteObject extends java.rmi.Remote {
    public int sendCliente(cliente cliente) throws java.rmi.RemoteException;

    public int sendVeiculo(veiculo veiculo) throws java.rmi.RemoteException;

    public int sendAluguer(aluguer aluguer) throws java.rmi.RemoteException;
     
}