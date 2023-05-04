/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package bancobbdd;

import static bancobbdd.NewUser.generateRandomIban;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.sql.ResultSet;
import java.sql.*;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class EndUser extends javax.swing.JFrame {

    /**
     * Creates new form NewJFrame
     */
    BancoBBDD bancobbdd = BancoBBDD.getInstance();
    String NIF = bancobbdd.userNIF;
    Connection conn = bancobbdd.conn;
    int xx = 0;
    int yy = 0;

    public EndUser() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/layout/logo.png"));

        // Establece la imagen como icono del sistema
        setIconImage(icon.getImage());
        setSize(600, 600);
        setLocationRelativeTo(null);
        initComponents();
        initListeners();
        refreshData();
        actualizarComboBox();
        jFieldTransparente();

    }
    
    private void initListeners() {
        tableAccounts.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tableAccounts.getSelectedRow();
                String accountIban = tableAccounts.getValueAt(selectedRow, 0).toString();
                System.out.println(accountIban);
                ResultSet rsTrans = bancobbdd.getTransactionsAccount(accountIban);
                tableTransactions.setModel(getTableData(rsTrans));
            }
        });
    }

    //Metodo para volver los jFieldTransparentes
    private void jFieldTransparente() {
        // Crear un borde personalizado con bordes blancos de 1 píxeles
        Border borde = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)
        );

        // Establecer el borde personalizado en los campos de texto
        jTextFieldDestination1.setBorder(borde);
        jTextFieldAmount1.setBorder(borde);
        jTextFieldDescription1.setBorder(borde);
        tfNewAccount.setBorder(borde);

        jTextFieldDestination1.setBackground(new java.awt.Color(0, 0, 0, 0));
        jTextFieldAmount1.setBackground(new java.awt.Color(0, 0, 0, 0));
        jTextFieldDescription1.setBackground(new java.awt.Color(0, 0, 0, 0));
        tfNewAccount.setBackground(new java.awt.Color(0, 0, 0, 0));

    }

    private DefaultTableModel getTableData(ResultSet rs) {
        DefaultTableModel model = new DefaultTableModel();
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                model.addColumn(metaData.getColumnName(columnIndex));
            }
            while (rs.next()) {
                // Creamos un array de objetos para almacenar los datos de una fila
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    // Obtenemos el valor de cada celda de la fila actual y lo almacenamos en el array
                    row[i] = rs.getObject(i + 1);
                }
                // Añadimos la fila al modelo
                model.addRow(row);
            }
        } catch (SQLException e) {
            bancobbdd.showErrorDialog(e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }
        return model;
    }

    public void actualizarComboBox() {
        ArrayList<String> nifs = new ArrayList<String>();
        for (int r = 0; r < tableAccounts.getRowCount(); r++) {
            String nif = tableAccounts.getValueAt(r, 0).toString();
            if (!nifs.contains(nif)) {
                nifs.add(nif);
            }
        }
        Collections.sort(nifs);
        jComboBoxMisCuentas1.removeAllItems();
        for (String nif : nifs) {
            jComboBoxMisCuentas1.addItem(nif);
        }
    }

    private void refreshData() {
        ResultSet rsAccount = bancobbdd.getAccounts(NIF);
        ResultSet rsTrans = bancobbdd.getTransactionsUser(NIF);
        tableAccounts.setModel(getTableData(rsAccount));
        tableTransactions.setModel(getTableData(rsTrans));

        float money = 0;
        int balanceIndex = -1;
        for (int i = 0; i < tableAccounts.getColumnCount(); i++) {
            if (tableAccounts.getColumnName(i).equals("balance")) {
                balanceIndex = i;
                break;
            }
        }
        if (balanceIndex != -1) {
            for (int r = 0; r < tableAccounts.getRowCount(); r++) {
                money += Float.parseFloat(tableAccounts.getValueAt(r, balanceIndex).toString());
            }
        }
        if (money < 0.0) {
            labelUserMoney.setForeground(Color.red);
        } else {
            labelUserMoney.setForeground(Color.white);
        }
        labelUserMoney.setText("Total Money: " + money);

    }

    public void insertTransaction(String sourceAccount, String destAccount, double amount, String description) throws SQLException {
        // Paso 1: Obtener la fecha y hora actuales
        LocalDateTime dateTime = LocalDateTime.now();

        // Paso 2: Crear un objeto Timestamp a partir de la fecha y hora actuales
        Timestamp timestamp = Timestamp.valueOf(dateTime);

        // Paso 3: Crear una consulta SQL para insertar una nueva transacción
        String sql = "INSERT INTO transactions (source_account, dest_account, amount, date, time, description) VALUES (?, ?, ?, ?, ?, ?)";

        try ( // Paso 4: Obtener un objeto PreparedStatement a partir de la conexión
                 PreparedStatement pstmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, sourceAccount);
            pstmt.setString(2, destAccount);
            pstmt.setDouble(3, amount);
            pstmt.setDate(4, new java.sql.Date(timestamp.getTime()));
            pstmt.setTime(5, new Time(timestamp.getTime()));
            pstmt.setString(6, description);
            // Paso 5: Ejecutar la consulta SQL para insertar la nueva transacción
            int affectedRows = pstmt.executeUpdate();
            // Paso 6: Obtener el ID de la nueva transacción generada automáticamente
            try (final ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int transactionId = generatedKeys.getInt(1);
                    System.out.println("The transfer has been created with ID " + transactionId + " affecting " + affectedRows + " rows");
                } else {
                    bancobbdd.showErrorDialog("The transfer has failed.");
                    throw new SQLException("The transfer has failed.");
                }
            }
        } // Paso 7: Cerrar el objeto PreparedStatement (y la conexión)
    }
    
    // Método que crea una nueva cuenta para un usuario ya existente
public void createNewAccount(String nif, String accountType, Connection conn) {
    try {
        // Verificación de que el usuario existe
        String query = "SELECT * FROM users WHERE NIF = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, nif);
        ResultSet result = statement.executeQuery();

        if (result.next()) {
            // Generación del nuevo account_iban
            String newIban = generateRandomIban();

            // Inserción de los datos de la nueva cuenta en la tabla accounts
            query = "INSERT INTO accounts (account_iban, account_type, opening_date, balance) VALUES (?, ?, NOW(), 0)";
            statement = conn.prepareStatement(query);
            statement.setString(1, newIban);
            statement.setString(2, accountType);
            int rows = statement.executeUpdate();

            if (rows > 0) {
                // Inserción de la relación entre usuario y cuenta en la tabla users_account
                query = "INSERT INTO users_account (NIF, acc_iban) VALUES (?, ?)";
                statement = conn.prepareStatement(query);
                statement.setString(1, nif);
                statement.setString(2, newIban);
                rows = statement.executeUpdate();

                if (rows > 0) {
                    // Si la inserción es exitosa, se devuelve un mensaje al usuario
                    System.out.println("account created for NIF " + nif);
                } else {
                    System.out.println("Error creating the new account");
                    bancobbdd.showErrorDialog("Error creating the new account");
                }
            } else {
                System.out.println("Error creating the new account");
                bancobbdd.showErrorDialog("Error creating the new account");
            }
        } else {
            System.out.println("User couldn't be found with NIF " + nif);
            bancobbdd.showErrorDialog("User couldn't be found with NIF " + nif);
        }

    } catch (SQLException e) {
        bancobbdd.showErrorDialog(e.getMessage());
        e.printStackTrace();
    }
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Fondo = new javax.swing.JPanel(){

            public void paintComponent(Graphics g){
                ImageIcon im = new ImageIcon(getClass().getResource("/layout/fondo.gif"));
                Image i = im.getImage();
                g.drawImage(i, 0, 0, this.getSize().width, this.getSize().height, this);
            }
        };
        Close = new javax.swing.JPanel(){

            public void paintComponent(Graphics g){
                ImageIcon im = new ImageIcon(getClass().getResource("/imagenes/close.png"));
                Image i = im.getImage();
                g.drawImage(i, 0, 0, this.getSize().width, this.getSize().height, this);
            }
        };
        Minimize = new javax.swing.JPanel(){

            public void paintComponent(Graphics g){
                ImageIcon im = new ImageIcon(getClass().getResource("/imagenes/minimize.png"));
                Image i = im.getImage();
                g.drawImage(i, 0, 0, this.getSize().width, this.getSize().height, this);
            }
        };
        DragDrop = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldDescription1 = new javax.swing.JTextField();
        jTextFieldAmount1 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jComboBoxMisCuentas1 = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jTextFieldDestination1 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableAccounts = new javax.swing.JTable(){
            // Agrega aquí el código para cambiar la fuente del encabezado
            {
                getTableHeader().setFont(new Font("Rockwell", Font.BOLD, 12));

                // Centrar el contenido del header
                ((DefaultTableCellRenderer) getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

                // Centrar el contenido de todos los campos de la tabla
                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment( JLabel.CENTER );
                setDefaultRenderer(Object.class, centerRenderer);

                getTableHeader().setForeground(new Color(28, 74, 106));
            }

        };
        labelUserMoney = new javax.swing.JLabel();
        LastestTransactions = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableTransactions = new javax.swing.JTable(){
            // Agrega aquí el código para cambiar la fuente del encabezado
            {
                getTableHeader().setFont(new Font("Rockwell", Font.BOLD, 12));

                // Centrar el contenido del header
                ((DefaultTableCellRenderer) getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

                // Centrar el contenido de todos los campos de la tabla
                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment( JLabel.CENTER );
                setDefaultRenderer(Object.class, centerRenderer);

                getTableHeader().setForeground(new Color(28, 74, 106));
            }

        };
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel(){

            public void paintComponent(Graphics g){
                ImageIcon im = new ImageIcon(getClass().getResource("/imagenes/IconoTransferencia.png"));
                Image i = im.getImage();
                g.drawImage(i, 0, 0, this.getSize().width, this.getSize().height, this);
            }
        };
        IconoDni1 = new javax.swing.JPanel(){

            public void paintComponent(Graphics g){
                ImageIcon im = new ImageIcon(getClass().getResource("/imagenes/atras2.png"));
                Image i = im.getImage();
                g.drawImage(i, 0, 0, this.getSize().width, this.getSize().height, this);
            }
        };
        BtnNewAccount = new javax.swing.JButton();
        tfNewAccount = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        Close.setPreferredSize(new java.awt.Dimension(18, 18));
        Close.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CloseMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                CloseMouseEntered(evt);
            }
        });
        Close.setLayout(new java.awt.BorderLayout());

        Minimize.setPreferredSize(new java.awt.Dimension(18, 18));
        Minimize.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MinimizeMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                MinimizeMouseEntered(evt);
            }
        });
        Minimize.setLayout(new java.awt.BorderLayout());

        DragDrop.setOpaque(false);
        DragDrop.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                DragDropMouseDragged(evt);
            }
        });
        DragDrop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                DragDropMousePressed(evt);
            }
        });
        DragDrop.setLayout(new java.awt.BorderLayout());

        jLabel7.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("DESCRIPTION");

        jLabel8.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("AMOUNT");

        jTextFieldDescription1.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jTextFieldDescription1.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldDescription1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldDescription1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldDescription1ActionPerformed(evt);
            }
        });

        jTextFieldAmount1.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jTextFieldAmount1.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldAmount1.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel9.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("MY ACCOUNTS");

        jComboBoxMisCuentas1.setBackground(new java.awt.Color(43, 0, 95));
        jComboBoxMisCuentas1.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jComboBoxMisCuentas1.setForeground(new java.awt.Color(255, 255, 255));

        jLabel10.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("DESTINATION");

        jTextFieldDestination1.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jTextFieldDestination1.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldDestination1.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        tableAccounts.setBackground(new java.awt.Color(147, 85, 180));
        tableAccounts.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        tableAccounts.setForeground(new java.awt.Color(255, 255, 255));
        tableAccounts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tableAccounts.setSelectionBackground(new java.awt.Color(102, 0, 102));
        jScrollPane2.setViewportView(tableAccounts);

        labelUserMoney.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        labelUserMoney.setText("Total money: 0");

        LastestTransactions.setFont(new java.awt.Font("Rockwell", 1, 18)); // NOI18N
        LastestTransactions.setForeground(new java.awt.Color(255, 255, 255));
        LastestTransactions.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LastestTransactions.setText("Lastes Transactions");
        LastestTransactions.setToolTipText("");

        tableTransactions.setBackground(new java.awt.Color(147, 85, 180));
        tableTransactions.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        tableTransactions.setForeground(new java.awt.Color(255, 255, 255));
        tableTransactions.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tableTransactions.setSelectionBackground(new java.awt.Color(102, 0, 102));
        jScrollPane3.setViewportView(tableTransactions);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Rockwell", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Accounts of user");

        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 209, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 62, Short.MAX_VALUE)
        );

        IconoDni1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                IconoDni1MouseClicked(evt);
            }
        });
        IconoDni1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BtnNewAccount.setBackground(new java.awt.Color(91, 25, 199));
        BtnNewAccount.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        BtnNewAccount.setForeground(new java.awt.Color(255, 255, 255));
        BtnNewAccount.setText("NEW ACCOUNT");
        BtnNewAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnNewAccountActionPerformed(evt);
            }
        });

        tfNewAccount.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        tfNewAccount.setForeground(new java.awt.Color(255, 255, 255));
        tfNewAccount.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout FondoLayout = new javax.swing.GroupLayout(Fondo);
        Fondo.setLayout(FondoLayout);
        FondoLayout.setHorizontalGroup(
            FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FondoLayout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxMisCuentas1, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addComponent(jTextFieldDestination1, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldAmount1, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(138, 138, 138)
                        .addComponent(jLabel8)
                        .addGap(66, 66, 66)))
                .addGap(18, 18, 18)
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addComponent(jTextFieldDescription1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(63, 63, 63))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FondoLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(103, 103, 103))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FondoLayout.createSequentialGroup()
                                .addComponent(LastestTransactions, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(384, 384, 384))
                            .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1016, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(labelUserMoney, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1016, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(FondoLayout.createSequentialGroup()
                                .addComponent(IconoDni1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(166, 166, 166)
                                .addComponent(tfNewAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(BtnNewAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(FondoLayout.createSequentialGroup()
                                .addComponent(DragDrop, javax.swing.GroupLayout.DEFAULT_SIZE, 998, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Minimize, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Close, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(14, 14, 14))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FondoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(399, 399, 399))
        );
        FondoLayout.setVerticalGroup(
            FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FondoLayout.createSequentialGroup()
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(DragDrop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(FondoLayout.createSequentialGroup()
                                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(Minimize, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(Close, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(0, 11, Short.MAX_VALUE)))
                        .addGap(24, 24, 24)
                        .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(tfNewAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BtnNewAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(IconoDni1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelUserMoney)
                .addGap(28, 28, 28)
                .addComponent(LastestTransactions)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldDestination1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldAmount1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextFieldDescription1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jComboBoxMisCuentas1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Fondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Fondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void CloseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CloseMouseClicked
        System.exit(0);
    }//GEN-LAST:event_CloseMouseClicked

    private void CloseMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CloseMouseEntered
        Close.setToolTipText("Close"); //Mensaje al pasar cursor por encima

    }//GEN-LAST:event_CloseMouseEntered

    private void MinimizeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MinimizeMouseClicked
        this.setState(JFrame.ICONIFIED);
    }//GEN-LAST:event_MinimizeMouseClicked

    private void MinimizeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MinimizeMouseEntered
        Minimize.setToolTipText("Minimize");  //Mensaje al pasar cursor por encima

    }//GEN-LAST:event_MinimizeMouseEntered

    private void DragDropMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DragDropMouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x - xx, y - yy);
    }//GEN-LAST:event_DragDropMouseDragged

    private void DragDropMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DragDropMousePressed
        xx = evt.getX();
        yy = evt.getY();
    }//GEN-LAST:event_DragDropMousePressed

    private void jTextFieldDescription1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldDescription1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldDescription1ActionPerformed

    private void jPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseClicked
        String sourceAccount = (String) jComboBoxMisCuentas1.getSelectedItem();
        String destAccount = jTextFieldDestination1.getText();
        double amount = Double.parseDouble(jTextFieldAmount1.getText());
        String description = jTextFieldDescription1.getText();

        try {
            insertTransaction(sourceAccount, destAccount, amount, description);
            JOptionPane.showMessageDialog(null, "Transfer succesful!");
        } catch (SQLException e) {
            bancobbdd.showErrorDialog("Error doing the transfer: " + e.getMessage());
        }
        refreshData();
    }//GEN-LAST:event_jPanel1MouseClicked

    private void IconoDni1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_IconoDni1MouseClicked
        int respuesta = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "BYNAY BANK", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            Login ib = new Login();
            ib.setVisible(true);
            dispose();
        }
    }//GEN-LAST:event_IconoDni1MouseClicked

    private void BtnNewAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnNewAccountActionPerformed
        String tipoCuenta = tfNewAccount.getText();
        createNewAccount(NIF,tipoCuenta, conn );
        refreshData();
    }//GEN-LAST:event_BtnNewAccountActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EndUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EndUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EndUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EndUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EndUser().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnNewAccount;
    private javax.swing.JPanel Close;
    private javax.swing.JPanel DragDrop;
    private javax.swing.JPanel Fondo;
    private javax.swing.JPanel IconoDni1;
    private javax.swing.JLabel LastestTransactions;
    private javax.swing.JPanel Minimize;
    private javax.swing.JComboBox<String> jComboBoxMisCuentas1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextFieldAmount1;
    private javax.swing.JTextField jTextFieldDescription1;
    private javax.swing.JTextField jTextFieldDestination1;
    private javax.swing.JLabel labelUserMoney;
    private javax.swing.JTable tableAccounts;
    private javax.swing.JTable tableTransactions;
    private javax.swing.JTextField tfNewAccount;
    // End of variables declaration//GEN-END:variables
}
