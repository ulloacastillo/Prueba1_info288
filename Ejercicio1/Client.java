import java.rmi.*;
import java.rmi.server.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Client {
    public static void main(String args[]) {
        try {
            Scanner scan = new Scanner(System.in);

            IntfBankClient server = (IntfBankClient) Naming.lookup("rmi://localhost:3000/test");

            Boolean log = false;
            int op = 2;

            System.out.print("Ingrese su nombre: ");
            String name = scan.nextLine().trim();

            System.out.print("Ingrese su contraseña: ");
            String password = scan.nextLine().trim();

            System.out.print("Ingrese su ID: ");
            int id = Integer.parseInt(scan.nextLine().trim());
            String message = name + "=" + password + "=" + id;
            IntfBankClient client = new IntfBankClientRemote(message);

            server.setClient(client);

            TimeUnit.SECONDS.sleep(1);

            while (true) {

                if (!client.getIsLogged() && op > 0) {
                    System.out.print("Ingrese su nombre: ");
                    name = scan.nextLine().trim();

                    System.out.print("Ingrese su contraseña: ");
                    password = scan.nextLine().trim();

                    System.out.print("Ingrese su ID: ");
                    id = Integer.parseInt(scan.nextLine().trim());
                    message = name + "=" + password + "=" + id;
                    client.setMessage(message);

                    server.setClient(client);

                    // wait
                    TimeUnit.SECONDS.sleep(1);
                    if (!client.getIsLogged()) {
                        op--;
                    }

                }

                else {
                    System.out.println("==============================================");
                    System.out.println("\nBienvenido " + client.getName());
                    System.out.println("Opciones a realizar: ");
                    System.out.println("1) Consultar saldo");
                    System.out.println("2) Depositar dinero");
                    System.out.println("3) Retirar dinero");
                    System.out.println("Escriba SALIR para salir del cajero");
                    System.out.print("Ingrese el numero de la operacion a realizar: ");
                    String operation = scan.nextLine().trim();
                    int ope;
                    if (operation.toUpperCase().equals("SALIR")) {
                        System.out.println("Gracias por usar el banco");
                        server.setClient(null);
                        System.exit(0);
                        break;
                    }

                    else {
                        try {
                            ope = Integer.parseInt(operation);
                        } catch (Exception e) {
                            ope = 0;
                        }

                    }

                    if (ope == 2 || ope == 3) {
                        String amount;
                        while (true) {
                            System.out.print("Ingrese el monto a Depositar/Retirar: ");
                            amount = scan.nextLine().trim();
                            try {
                                int aux = Integer.parseInt(amount);
                                break;
                            }

                            catch (Exception e) {
                                continue;
                            }
                        }

                        message = ope + "=" + amount;
                    }

                    else {
                        message = ope + "";
                    }

                    client.setMessage(message);
                    server.setClient(client);
                    TimeUnit.SECONDS.sleep(1);

                }

                if (op == 0) {
                    System.out.println("EXIT");
                    server.setClient(null);
                    System.exit(0);
                    break;
                }

            }
            scan.close();

        } catch (Exception e) {
            System.out.println("Fallo de servidor: " + e);
        }
    }
}
