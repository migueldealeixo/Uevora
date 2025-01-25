import java.sql.*;

public class conexaoBD implements java.io.Serializable{

    private Connection con = null;
    private Statement stmt = null;
    private String PG_HOST;
    private String PG_DB;
    private String USER;
    private String PWD;

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public Statement getStmt() {
        return stmt;
    }

    public void setStmt(Statement stmt) {
        this.stmt = stmt;
    }

    public conexaoBD() {
        PG_HOST = "localhost";
        PG_DB = "trabalho1";
        USER = "user1";
        PWD = "umaPas";
    }

    public conexaoBD(String host, String bd, String user, String pass) {
        PG_HOST = host;
        PG_DB = bd;
        USER = user;
        PWD = pass;
    }
    public void connect() {

        try {

            Class.forName("org.postgresql.Driver");

            // url = "jdbc:postgresql://host:port/database", psql trabalho1 -U user1 -h localhost
            con = DriverManager.getConnection("jdbc:postgresql://" + PG_HOST + ":5432/" + PG_DB, USER, PWD);
            stmt = con.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Problems setting the connection");

        }
    }

//Inserir codigo de inserção
    public int inserirCliente(cliente cliente){
        int clienteID = -1;
        String insertQuery = "insert into cliente (name, numerotele,clienteid) values(?,?,?)";
        try(PreparedStatement pstmt = con.prepareStatement(insertQuery)){
            pstmt.setString(1, cliente.getName());
            pstmt.setInt(2, cliente.getNumeroTele());
            pstmt.setInt(3, cliente.getClienteID());

            ResultSet resultSet = pstmt.executeQuery();
            if(resultSet.next()){
                clienteID = resultSet.getInt("id");
                cliente.setClienteID(clienteID);
            }
            System.out.println("Inserido cliente com id: " + clienteID);
        }catch(Exception e){
            e.printStackTrace();
            System.err.println("Problema a inserir cliente");
        }
        return clienteID;
    }

public int inserirVeiculo(veiculo veiculo){
    int veiculoID = -1;
    String insertQuery = "insert into veiculo(matricula,modelo,tipo,localizacao,estadoAluguer,estadoAdmin,veiculoID) values (?,?,?,?,?,?,?)";
    try (PreparedStatement pstmt = con.prepareStatement(insertQuery)){
        pstmt.setInt(1, veiculo.getMatricula());
        pstmt.setString(2, veiculo.getModelo());
        pstmt.setString(3, veiculo.getTipo());
        pstmt.setString(4, veiculo.getLocalizacao());
        pstmt.setString(5, veiculo.getEstadoAluguer());
        pstmt.setBoolean(6, veiculo.isEstadoAdmin());
        pstmt.setInt(7, veiculo.getVeiculoID());

        ResultSet resultSet = pstmt.executeQuery();
        if(resultSet.next()){
            veiculoID = resultSet.getInt("id");
            veiculo.setVeiculoID(veiculoID);
        }
        System.out.println("Inserido veiculo com id: " + veiculoID);
    }catch(Exception e){
       e.printStackTrace();
        System.err.println("Problema a inserir veiculo");
    }
    return veiculoID;
}

public int inserirAluguer(aluguer aluguer){
    int aluguerID = -1;
    String insertQuery = "insert into aluguer(veiculoID, clienteID,valor,datainicio,duracaoPrev,aluguerID) values (?,?,?,?,?,?)";
    try(PreparedStatement pstmt = con.prepareStatement(insertQuery)){
        pstmt.setInt(1, aluguer.getVeiculoID());
        pstmt.setInt(2, aluguer.getClienteID());
        pstmt.setFloat(3, aluguer.getValor());
        pstmt.setInt(4, aluguer.getDataInicio());
        pstmt.setInt(5, aluguer.getDuracaoPrev());
        pstmt.setInt(6, aluguer.getAluguerID());

        ResultSet resultSet = pstmt.executeQuery();

        if(resultSet.next()){
            aluguerID = resultSet.getInt("id");
            aluguer.setAluguerID(aluguerID);
        }
        System.out.println("Inserido aluguer com id: " + aluguerID);
    }catch(Exception e){
        e.printStackTrace();
        System.err.println("Problema a inserir aluguer");
    }
    return aluguerID;

}
    //Inserir codigos de consulta











    
    public void disconnect() { // importante: fechar a ligacao 'a BD
    try {
        stmt.close();
        con.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}



}