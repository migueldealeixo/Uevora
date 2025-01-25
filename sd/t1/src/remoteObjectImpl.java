import java.rmi.server.UnicastRemoteObject;

public class remoteObjectImpl extends  UnicastRemoteObject implements  remoteObject{
    private conexaoBD cbd;
    public remoteObjectImpl(conexaoBD cbd) throws java.rmi.RemoteException{
        this.cbd = cbd;
        //super();
    }
    public int sendCliente(cliente cliente) throws java.rmi.RemoteException{
        return cbd.inserirCliente(cliente);
    }

    public int sendAluguer(aluguer aluguer) throws java.rmi.RemoteException{
        return cbd.inserirAluguer(aluguer);
    }


    public int sendVeiculo(veiculo veiculo) throws java.rmi.RemoteException{
        return cbd.inserirVeiculo(veiculo);
    }
}