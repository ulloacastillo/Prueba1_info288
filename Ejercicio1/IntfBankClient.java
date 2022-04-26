import java.rmi.*;
import java.util.ArrayList;

public interface IntfBankClient extends Remote {

    public void setClient(IntfBankClient client) throws RemoteException;

    public IntfBankClient getClient() throws RemoteException;

    public String getMessage() throws RemoteException;

    public void setMessage(String msg) throws RemoteException;

    public Boolean getIsLogged() throws RemoteException;

    public void setIsLooged(Boolean b) throws RemoteException;

    public void setUsers(ArrayList<ArrayList<String>> users) throws RemoteException;

    public ArrayList<ArrayList<String>> getUsers() throws RemoteException;

    public String getName() throws RemoteException;

    public void setName(String name) throws RemoteException;

    public int getType() throws RemoteException;

    public void setType(int type) throws RemoteException;

    public int getId() throws RemoteException;

    public void setId(int id) throws RemoteException;

    public String getPass() throws RemoteException;

    public void setPass(String pass) throws RemoteException;

    public int getFunds() throws RemoteException;

    public void setFunds(int amount) throws RemoteException;

    public int getLastMove() throws RemoteException;

    public void setLastMove(int amount) throws RemoteException;

    public String getiDLastMove() throws RemoteException;

    public void setiDLastMove(String id) throws RemoteException;

    public void checkFunds() throws RemoteException;

    public String deposit(int amount) throws RemoteException;

    public String withdraw(int amount) throws RemoteException;

    public int[] login(String name, String password, int id) throws RemoteException;

    public void setAllClient(IntfBankClient client, int idx) throws RemoteException;

    public void send(String msg) throws RemoteException;

}