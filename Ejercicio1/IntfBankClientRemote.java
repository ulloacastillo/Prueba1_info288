import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;

public class IntfBankClientRemote extends UnicastRemoteObject implements IntfBankClient {
    public String name;
    public int type;
    public int id;
    public String pass;
    public int funds;
    public int lastMove;
    public String iDLastMove;
    public IntfBankClient client = null;

    // List that contains a list for each user and this list contains the data of a
    // user
    public ArrayList<ArrayList<String>> users;

    // Atribute used to the client can send messages(data) to the server and so the
    // server can process the requests
    public String message = null;
    public Boolean isLogged = false;

    public IntfBankClientRemote(String name, String pass, int id) throws RemoteException {
        this.name = name;
        this.pass = pass;
        this.id = id;
    }

    public IntfBankClientRemote(ArrayList<ArrayList<String>> users) throws RemoteException {
        this.users = users;
    }

    public IntfBankClientRemote(String data) throws RemoteException {
        this.message = data;
    }

    // setters and getters

    public void setClient(IntfBankClient client) throws RemoteException {
        this.client = client;
    }

    public IntfBankClient getClient() throws RemoteException {
        return this.client;
    }

    public Boolean getIsLogged() throws RemoteException {
        return this.isLogged;
    }

    public void setIsLooged(Boolean b) throws RemoteException {
        this.isLogged = b;
    }

    public void setMessage(String msg) throws RemoteException {
        this.message = msg;
    }

    public String getMessage() throws RemoteException {
        return this.message;
    }

    public ArrayList<ArrayList<String>> getUsers() throws RemoteException {
        return this.users;
    }

    public void setUsers(ArrayList<ArrayList<String>> users) throws RemoteException {
        this.users = users;
    }

    public String getName() throws RemoteException {
        return this.name;
    }

    public void setName(String name) throws RemoteException {
        this.name = name;
    }

    public int getType() throws RemoteException {
        return this.type;
    }

    public void setType(int type) throws RemoteException {
        this.type = type;
    }

    public int getId() throws RemoteException {
        return this.id;
    }

    public void setId(int id) throws RemoteException {
        this.id = id;
    }

    public String getPass() throws RemoteException {
        return this.pass;
    }

    public void setPass(String pass) throws RemoteException {
        this.pass = pass;
    }

    public int getFunds() throws RemoteException {

        return this.funds;
    }

    public void setFunds(int amount) throws RemoteException {
        this.funds = amount;
    }

    public int getLastMove() throws RemoteException {
        return this.lastMove;
    }

    public void setLastMove(int amount) throws RemoteException {
        this.lastMove = amount;
    }

    public String getiDLastMove() throws RemoteException {
        return this.iDLastMove;
    }

    public void setiDLastMove(String id) throws RemoteException {
        this.iDLastMove = id;
    }

    public void checkFunds() throws RemoteException {
        System.out.println("\n==============================================");
        System.out.println("Hola " + this.name + " Usted posee en su cuenta: $ " + this.funds);
        System.out.print(this.name + " su ultimo movimiento fue de : $ " + this.lastMove);
        System.out.print(" y consistio en " + this.iDLastMove);
        System.out.println("\n");
    }

    public String deposit(int amount) throws RemoteException {
        if (this.type == 1) {

            if (amount > 50000 || this.funds + amount > 500000) {
                return "Excede el maximo permitido";
            } else {
                this.funds += amount;
                this.lastMove = amount;
                this.iDLastMove = "Abono";
                return "Deposito exitoso";
            }
        }

        else {
            if (amount > 500000 || this.funds + amount > 6500000) {
                return "Excede el maximo permitido";
            } else {
                this.funds += amount;
                this.lastMove = amount;
                this.iDLastMove = "Abono";
                return "Deposito exitoso";
            }
        }
    }

    public String withdraw(int amount) throws RemoteException {
        if (this.type == 1) {
            if (amount > 50000) {
                return "Exede retiro Maximo";
            } else {
                if (this.funds - amount < -100000) {
                    return "Excede Deuda Maxima";
                } else {
                    this.funds -= amount;
                    this.lastMove = -1 * amount;
                    this.iDLastMove = "Cargo";
                    return "Retiro exitoso";
                }
            }
        }

        else {
            if (amount > 500000) {

                return "Exede retiro Maximo";
            } else {
                if (this.funds - amount < -1000000) {

                    return "Excede deuda Maxima";
                } else {
                    this.funds -= amount;
                    this.lastMove = -1 * amount;
                    this.iDLastMove = "Cargo";
                    return "Retiro exitoso";
                }
            }
        }
    }

    public void send(String msg) throws RemoteException {
        System.out.println(msg);
    }

    /*
     * It set all the atributes of the client `client. To do this, the method
     * use the data located in users[idx]
     * 
     */
    public void setAllClient(IntfBankClient client, int idx) throws RemoteException {
        client.setName(this.users.get(idx).get(0));

        client.setId(Integer.parseInt(this.users.get(idx).get(2)));
        client.setPass(this.users.get(idx).get(3));
        client.setFunds(Integer.parseInt(this.users.get(idx).get(4)));
        client.setLastMove(Integer.parseInt(this.users.get(idx).get(5)));
        client.setiDLastMove(this.users.get(idx).get(6));

        if (this.users.get(idx).get(1).equals("Empresa")) {
            client.setType(2);
        } else {
            client.setType(1);
        }

    }

    /*
     * It check if the data entered by the user is equual to any client registered
     * in the DB
     * 
     * return an array that contains in its 1st position if the user was found and
     * in the 2nd position the index of the list user where the client was found
     */
    public int[] login(String name, String password, int id) throws RemoteException {
        int[] res = { 0, -1 };
        for (int i = 0; i < this.users.size(); i++) {

            if (Integer.parseInt(this.users.get(i).get(2)) == id) {

                if (this.users.get(i).get(3).equals(password)) {

                    if (this.users.get(i).get(0).equals(name)) {
                        System.out.println(">>> " + name + " Conectado");
                        res[0] = 1;
                        res[1] = i;
                    }
                }
            }
        }

        return res;
    }

}