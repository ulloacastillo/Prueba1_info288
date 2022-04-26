import java.rmi.*;
import java.rmi.server.*;
import java.util.Scanner;
import java.io.*;
import java.lang.Character.Subset;
import java.util.*;

public class Server {
    public static void main(String args[]) {

        ArrayList<ArrayList<String>> users = new ArrayList<ArrayList<String>>();
        Scanner scan = new Scanner(System.in);

        try {
            Scanner sc = new Scanner(
                    new File("./datos_banco.csv"));
            sc.useDelimiter("\n");

            while (sc.hasNext()) {
                // We recieve the string of each line line and we split it to get an array
                String a_ = sc.next();
                // We transform this array to a List and we add it to another list of users
                users.add(new ArrayList<String>(Arrays.asList(a_.split(","))));
            }

            sc.close();
        } catch (FileNotFoundException ex) {
            System.out.println("no se ha encontrado archivo");
        }

        try {

            IntfBankClient server = new IntfBankClientRemote(users);

            Naming.rebind("rmi://localhost:3000/test", server);
            System.out.println("Servidor esta arriba");
            String message;

            while (true) {

                if (server.getClient() != null) {

                    IntfBankClient client = server.getClient();
                    // we check the message sent by the client
                    // there are 2 options for this messages:
                    // 1) The message is "salir" or "exit"
                    // 2) Client sent the data (operation_number + amount) so that the server could
                    // do the deposit/withdraw
                    message = client.getMessage();

                    if (message != null) {

                        if (message.equals("EXIT")) {
                            System.out.println("EXIT");
                            break;
                        }
                        if (message.toUpperCase().equals("SALIR")) {
                            System.out.println("Gracias por usar el banco");
                            break;
                        }

                        // System.out.println(">>>>>>" + client.getMessage());
                        String[] datos = client.getMessage().split("=");
                        // System.out.println(">>>>>>" + datos);

                        if (client.getIsLogged()) {
                            switch (datos[0]) {
                                case "1":
                                    client.checkFunds();
                                    break;
                                case "2":
                                    client.send(client.deposit(Integer.parseInt(datos[1])));
                                    break;

                                case "3":
                                    client.send(client.withdraw(Integer.parseInt(datos[1])));
                                    break;
                                default:
                                    break;
                            }
                        } else {

                            if (server.login(datos[0], datos[1], Integer.parseInt(datos[2]))[0] == 1) {
                                System.out.println("Acceso exitoso");
                                client.setIsLooged(true);
                                server.setAllClient(client,
                                        server.login(datos[0], datos[1], Integer.parseInt(datos[2]))[1]);
                            }
                        }
                        client.setMessage(null);

                    }

                }
            }
            scan.close();
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Fallo de servidor: " + e);
        }
    }
}
