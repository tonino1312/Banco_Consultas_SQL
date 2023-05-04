/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package bancobbdd;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;
import javax.swing.JOptionPane;

public class BancoBBDD {

    private static BancoBBDD instance = null;

    public Connection conn1;
    public Connection conn2;
    public Connection conn;

    public String userNIF;
    public boolean isAdmin = false;
    public boolean isDisabled = false;

    private BancoBBDD() {
        try {
            // Definimos la cadena de conexión a la base de datos "discografica"
            String urll = "jdbc:mysql://localhost:3306/bancobbdd?serverTimezone=UTC";
            // Definimos el nombre de usuario utilizado para conectarse a la base de datos
            String user = "root";
            // Definimos la contraseña utilizada para conectarse a la base de datos
            String password = "Carlos1234";

            String url2 = "jdbc:mysql://bancobbdd.xavifortes.com:5169/bancobbdd?serverTimezone=UTC";
            String user2 = "externosBancoBBDD";
            String password2 = "BancoBBDD**00!!";

            // Intentamos establecer una conexión a la base de datos utilizando la clase "DriverManager" y los detalles de conexión especificados anteriormente
            try {
                conn2 = DriverManager.getConnection(url2, user2, password2);
                conn1 = DriverManager.getConnection(urll, user, password);
            } catch (SQLException ex1) {
                // Si ocurre una excepción de tipo "SQLException" durante la conexión a la base de datos, se imprimirá un mensaje de error en la consola y se mostrará una traza de pila en caso de que se necesite una depuración adicional
                if (conn2 == null) {
                    System.out.println("ERROR in the external DB connection");
                    showErrorDialog("Error in external DB");
                } else {
                    System.out.println("ERROR in the local DB connection");
                }

                // En caso de fallo de la conexión conn1, intentamos establecer una conexión con conn2
            }
            conn = (conn2 != null) ? conn2 : conn1;
        } catch (Exception e) {
            showErrorDialog(e.getMessage());
            e.printStackTrace();
        }
    }

    public static BancoBBDD getInstance() {
        if (instance == null) {
            instance = new BancoBBDD();
        }
        return instance;
    }

    public void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void CerrarConexion() {
        try {
            conn1.close();
            conn2.close();
        } catch (SQLException ex) {
            System.out.println("ERROR closing connection");
            showErrorDialog("ERROR closing connection");
        }
    }

    public ResultSet getUser(String NIF) {
        ResultSet result = null;
        try {
            // Crear una sentencia SQL para seleccionar todas las filas de la tabla
            String sql = "SELECT * FROM users WHERE NIF='" + NIF + "';";

            // Crear un objeto Statement y ejecutar la consulta
            Statement stmt;
            stmt = conn.createStatement();

            result = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog(e.getMessage());
        }
        return result;
    }
     public ResultSet getIban(String NIF) {
        ResultSet result = null;
        try {
            // Crear una sentencia SQL para seleccionar todas las filas de la tabla
            String sql = "SELECT account_iban FROM users WHERE NIF='" + NIF + "';";

            // Crear un objeto Statement y ejecutar la consulta
            Statement stmt;
            stmt = conn.createStatement();

            result = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ResultSet getAccounts(String NIF) {
        ResultSet result = null;
        try {
            // Crear una sentencia SQL para seleccionar todas las filas de la tabla
            String sql = """
                         SELECT a.account_iban, a.account_type, opening_date, a.balance
                         FROM accounts a
                         INNER JOIN users_account ua ON a.account_iban = ua.acc_iban
                         INNER JOIN users u ON ua.NIF = u.NIF
                         WHERE u.NIF = '""" + NIF + "';";

            // Crear un objeto Statement y ejecutar la consulta
            Statement stmt;
            stmt = conn.createStatement();

            result = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog(e.getMessage());
        }
        return result;
    }

    public ResultSet getTransactionsUser(String NIF) {
        ResultSet result = null;
        try {
            // Crear una sentencia SQL para seleccionar todas las filas de la tabla
            String sql = """
                SELECT t.description,
                    CASE WHEN t.dest_account = a.account_iban THEN CONCAT('-', t.amount)
                        ELSE t.amount
                    END AS amount,
                    CASE WHEN t.dest_account = a.account_iban THEN t.source_account
                        ELSE t.dest_account
                        END AS other_account,
                        a.account_iban
                FROM transactions t
                INNER JOIN accounts a ON t.source_account = a.account_iban OR t.dest_account = a.account_iban
                INNER JOIN users_account ua ON a.account_iban = ua.acc_iban
                INNER JOIN users u ON ua.NIF = u.NIF
                WHERE u.NIF = '""" + NIF + "';";

            // Crear un objeto Statement y ejecutar la consulta
            Statement stmt;
            stmt = conn.createStatement();

            result = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog(e.getMessage());
        }
        return result;
    }

    public ResultSet getTransactionsFinalUser(String NIF) {
        ResultSet result = null;
        try {
            // Crear una sentencia SQL para seleccionar todas las filas de la tabla
            String sql = """
                     SELECT  t.amount, t.source_account, t.dest_account, t.description, tr.time, tr.date
                     FROM transactions t
                     LEFT JOIN accounts a ON t.source_account = a.account_iban OR t.dest_account = a.account_iban
                     LEFT JOIN users_account ua ON a.account_iban = ua.acc_iban
                     LEFT JOIN users u ON ua.NIF = u.NIF
                     LEFT JOIN transactions tr ON t.transaction_id = tr.transaction_id
                     WHERE u.NIF = '""" + NIF + "';";

            // Crear un objeto Statement y ejecutar la consulta
            Statement stmt;
            stmt = conn.createStatement();

            result = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog(e.getMessage());
        }
        return result;
    }

    public String generateHash(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(hash);
    }

    public boolean checkPassword(String NIF, String password) {
        String hashedPassword = null;
        boolean matches = false;
        try {
            String sql = "SELECT password, admin, disabled FROM users WHERE NIF = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, NIF);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                hashedPassword = rs.getString("password");
                isAdmin = rs.getBoolean("admin");
                isDisabled = rs.getBoolean("disabled");
                String hashedInputPassword = generateHash(password);
                matches = hashedPassword.equals(hashedInputPassword);
                if (matches) {
                    userNIF = NIF;
                    System.out.println(NIF);
                }
            }
        } catch (NoSuchAlgorithmException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
            showErrorDialog(e.getMessage());
        }
        return matches;
    }

    public void modificarFila(String NIF, String firstname, String lastname, String email, String phonenumber,
            String address) {
        // Guardar los cambios en la base de datos
        try {
            Statement stmt = conn.createStatement();

            String updateQuery = "UPDATE users SET ";
            String[] columns = {"first_name", "last_name", "email", "phone_number", "address"};
            String[] values = {firstname, lastname, email, phonenumber, address};

            for (int i = 0; i < columns.length; i++) {
                String columnValue = values[i].trim();
                if (!columnValue.isEmpty()) {
                    updateQuery += columns[i] + "='" + columnValue + "'";
                    if (i < columns.length - 1) {
                        updateQuery += ", ";
                    }
                }
            }
            updateQuery += " WHERE NIF='" + NIF + "'";

            stmt.executeUpdate(updateQuery);

            System.out.println("Row succesfully updated");
        } catch (SQLException e) {
            System.out.println("Error updating row: " + e.getMessage());
            showErrorDialog("Error updating row: \n" + e.getMessage());
        }
    }

    public void toggleDisableUser(String NIF, int disable) {
        // Guardar los cambios en la base de datos
        try {
            Statement stmt = conn.createStatement();

            String updateQuery = "UPDATE users SET ";
            updateQuery += "disabled=b'" + disable + "'";
            updateQuery += " WHERE NIF='" + NIF + "'";

            stmt.executeUpdate(updateQuery);

            System.out.println("Row succesfully updated");
        } catch (SQLException e) {
            System.out.println("Error al actualizar la fila: " + e.getMessage());
            showErrorDialog("Error updating row: \n" + e.getMessage());
        }
    }

    public ResultSet getTransactionsAccount(String IBAN) {
        ResultSet result = null;
        try {
            // Crear una sentencia SQL para seleccionar todas las filas de la tabla
            String sql = """
             SELECT t.transaction_id, 
                    CASE WHEN t.dest_account = '""" + IBAN + """
                    ' THEN t.amount ELSE -t.amount END AS amount,
                    t.source_account, 
                    t.dest_account, 
                    t.description, 
                    a.account_iban
             FROM transactions t
             INNER JOIN accounts a ON t.source_account = a.account_iban OR t.dest_account = a.account_iban
             WHERE a.account_iban = '""" + IBAN + "';";

            // Crear un objeto Statement y ejecutar la consulta
            Statement stmt;
            stmt = conn.createStatement();

            result = stmt.executeQuery(sql);
        } catch (SQLException e) {
            showErrorDialog(e.getMessage());
        }
        return result;
    }

}
