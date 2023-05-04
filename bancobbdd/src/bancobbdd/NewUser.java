/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package bancobbdd;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.Instant;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.border.Border;

/**
 *
 * @author carlo
 */
public class NewUser extends javax.swing.JDialog {

    BancoBBDD bancobbdd = BancoBBDD.getInstance();
    Connection conn1 = bancobbdd.conn;
    //Drag and Drop en la interfaz
    int xx = 0;
    int yy = 0;

    /**
     * Creates new form NewUser
     */
    public NewUser(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        ImageIcon icon = new ImageIcon(getClass().getResource("/layout/logo.png"));

        // Establece la imagen como icono del sistema
        setIconImage(icon.getImage());
        setSize(600, 600);
        setLocationRelativeTo(null);
        initComponents();
        jFieldTransparente();
    }

    private void jFieldTransparente() {
        // Crear un borde personalizado con bordes blancos de 1 píxeles
        Border borde = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)
        );

        // Establecer el borde personalizado en los campos de texto
        jTextFieldPassword.setBorder(borde);
        jTextFieldPassword2.setBorder(borde);
        jTextFieldLastName.setBorder(borde);
        jTextFieldFirstName.setBorder(borde);
        jTextFieldPhoneNumber.setBorder(borde);
        jTextFieldEmail.setBorder(borde);
        jTextFieldAddress.setBorder(borde);
        jTextFieldNif.setBorder(borde);

        jTextFieldPassword.setBackground(new java.awt.Color(0, 0, 0, 0));
        jTextFieldPassword2.setBackground(new java.awt.Color(0, 0, 0, 0));
        jTextFieldLastName.setBackground(new java.awt.Color(0, 0, 0, 0));
        jTextFieldFirstName.setBackground(new java.awt.Color(0, 0, 0, 0));
        jTextFieldPhoneNumber.setBackground(new java.awt.Color(0, 0, 0, 0));
        jTextFieldEmail.setBackground(new java.awt.Color(0, 0, 0, 0));
        jTextFieldAddress.setBackground(new java.awt.Color(0, 0, 0, 0));
        jTextFieldNif.setBackground(new java.awt.Color(0, 0, 0, 0));

    }

public static boolean createUser(Connection conn, String nif, String firstName, String lastName,
        String email, String phoneNumber, String address, String password, boolean createAccount) {
    try {
        // Crear una declaración SQL para insertar el nuevo usuario
        String sql = "INSERT INTO users (NIF, password, first_name, last_name, email, phone_number, address) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, nif);
        statement.setString(2, password);
        statement.setString(3, firstName);
        statement.setString(4, lastName);
        statement.setString(5, email);
        statement.setString(6, phoneNumber);
        statement.setString(7, address);

        String generatedIban = null;
        
        int rowsInserted = statement.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("El nuevo usuario ha sido creado correctamente.");

        // Verificar si se debe crear la cuenta y la relación usuario-cuenta
        if (createAccount) {
            generatedIban = generateRandomIban();

            // Crear una declaración SQL para insertar la nueva cuenta en la tabla "accounts"
            String accountSql = "INSERT INTO accounts (account_iban, account_type, opening_date, closing_date, balance) "
                    + "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement accountStatement = conn.prepareStatement(accountSql, Statement.RETURN_GENERATED_KEYS);
            accountStatement.setString(1, generatedIban);
            accountStatement.setString(2, "ahorros");
            accountStatement.setTimestamp(3, Timestamp.from(Instant.now()));
            accountStatement.setNull(4, Types.TIMESTAMP);
            accountStatement.setBigDecimal(5, BigDecimal.ZERO);

            // Ejecutar la declaración SQL y obtener el número de filas afectadas
            int accountRowsInserted = accountStatement.executeUpdate();
            if (accountRowsInserted <= 0) {
                System.err.println("Error al crear la cuenta del nuevo usuario.");
                return false;
            }

            // Crear una declaración SQL para insertar la nueva relación usuario-cuenta en la tabla "users_account"
            String userAccountSql = "INSERT INTO users_account (NIF, acc_iban) VALUES (?, ?)";
            PreparedStatement userAccountStatement = conn.prepareStatement(userAccountSql);
            userAccountStatement.setString(1, nif);
            userAccountStatement.setString(2, generatedIban);

            // Ejecutar la declaración SQL y obtener el número de filas afectadas
            int userAccountRowsInserted = userAccountStatement.executeUpdate();
            if (userAccountRowsInserted <= 0) {
                System.err.println("Error al crear la relación entre el nuevo usuario y su cuenta.");
                return false;
            }
        }

        // Ejecutar la declaración SQL para insertar el nuevo usuario y obtener el número de filas afectadas
        

            // Si se creó la cuenta, imprimir el IBAN generado
            if (createAccount) {
                System.out.println("IBAN: " + generatedIban);
            }

            return true;
        }
    } catch (SQLException ex) {
        System.err.println("Error al crear el nuevo usuario: " + ex.getMessage());
        ex.getErrorCode();
    }

    return false;
}


    public static String generateRandomIban() {
        Random random = new Random();
        String countryCode = "ES";
        String checkDigits = "00";
        String bankCode = String.format("%04d", random.nextInt(10000));
        String officeCode = String.format("%04d", random.nextInt(10000));
        String accountNumber = String.format("%010d", random.nextInt(1000000000));
        return countryCode + checkDigits + bankCode + officeCode + accountNumber;
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
                ImageIcon im = new ImageIcon(getClass().getResource("/layout/fondo2.gif"));
                Image i = im.getImage();
                g.drawImage(i, 0, 0, this.getSize().width, this.getSize().height, this);
            }
        };
        jTextFieldNif = new javax.swing.JTextField();
        jTextFieldFirstName = new javax.swing.JTextField();
        jTextFieldLastName = new javax.swing.JTextField();
        jTextFieldEmail = new javax.swing.JTextField();
        jTextFieldPhoneNumber = new javax.swing.JTextField();
        jTextFieldAddress = new javax.swing.JTextField();
        Nif = new javax.swing.JLabel();
        FirstName = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        Address = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldPassword = new javax.swing.JPasswordField();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldPassword2 = new javax.swing.JPasswordField();
        RepeatPassword = new javax.swing.JLabel();
        signUp = new javax.swing.JPanel(){

            public void paintComponent(Graphics g){
                ImageIcon im = new ImageIcon(getClass().getResource("/imagenes/SingUp.png"));
                Image i = im.getImage();
                g.drawImage(i, 0, 0, this.getSize().width, this.getSize().height, this);
            }
        };
        DragDrop = new javax.swing.JPanel();
        Close = new javax.swing.JPanel(){

            public void paintComponent(Graphics g){
                ImageIcon im = new ImageIcon(getClass().getResource("/imagenes/close.png"));
                Image i = im.getImage();
                g.drawImage(i, 0, 0, this.getSize().width, this.getSize().height, this);
            }
        };
        botonatras = new javax.swing.JPanel(){

            public void paintComponent(Graphics g){
                ImageIcon im = new ImageIcon(getClass().getResource("/imagenes/atras.png"));
                Image i = im.getImage();
                g.drawImage(i, 0, 0, this.getSize().width, this.getSize().height, this);
            }
        };
        jCheckBoxCrearcuenta = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setSize(new java.awt.Dimension(652, 484));

        jTextFieldNif.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jTextFieldNif.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldNif.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jTextFieldFirstName.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jTextFieldFirstName.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldFirstName.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jTextFieldLastName.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jTextFieldLastName.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldLastName.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jTextFieldEmail.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jTextFieldEmail.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldEmail.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jTextFieldPhoneNumber.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jTextFieldPhoneNumber.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldPhoneNumber.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldPhoneNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPhoneNumberActionPerformed(evt);
            }
        });

        jTextFieldAddress.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jTextFieldAddress.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldAddress.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldAddressActionPerformed(evt);
            }
        });

        Nif.setFont(new java.awt.Font("Rockwell", 1, 14)); // NOI18N
        Nif.setForeground(new java.awt.Color(255, 255, 255));
        Nif.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Nif.setText("NIF");

        FirstName.setFont(new java.awt.Font("Rockwell", 1, 14)); // NOI18N
        FirstName.setForeground(new java.awt.Color(255, 255, 255));
        FirstName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        FirstName.setText("FIRST NAME");

        jLabel3.setFont(new java.awt.Font("Rockwell", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("LAST NAME");

        jLabel4.setFont(new java.awt.Font("Rockwell", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("EMAIL");

        jLabel5.setFont(new java.awt.Font("Rockwell", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("PHONE NUMBER");

        Address.setFont(new java.awt.Font("Rockwell", 1, 14)); // NOI18N
        Address.setForeground(new java.awt.Color(255, 255, 255));
        Address.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Address.setText("ADDRESS");

        jLabel7.setText("PASSWORD");

        jTextFieldPassword.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jTextFieldPassword.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldPassword.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldPassword.setToolTipText("Password");
        jTextFieldPassword.setEchoChar('*');
        jTextFieldPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPasswordActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Rockwell", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("PASSWORD");

        jTextFieldPassword2.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jTextFieldPassword2.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldPassword2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldPassword2.setToolTipText("Repeat password");

        RepeatPassword.setFont(new java.awt.Font("Rockwell", 1, 14)); // NOI18N
        RepeatPassword.setForeground(new java.awt.Color(255, 255, 255));
        RepeatPassword.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        RepeatPassword.setText("REPEAT PASSWORD");

        signUp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                signUpMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout signUpLayout = new javax.swing.GroupLayout(signUp);
        signUp.setLayout(signUpLayout);
        signUpLayout.setHorizontalGroup(
            signUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 154, Short.MAX_VALUE)
        );
        signUpLayout.setVerticalGroup(
            signUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 47, Short.MAX_VALUE)
        );

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

        javax.swing.GroupLayout DragDropLayout = new javax.swing.GroupLayout(DragDrop);
        DragDrop.setLayout(DragDropLayout);
        DragDropLayout.setHorizontalGroup(
            DragDropLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 574, Short.MAX_VALUE)
        );
        DragDropLayout.setVerticalGroup(
            DragDropLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

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

        botonatras.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                botonatrasMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout botonatrasLayout = new javax.swing.GroupLayout(botonatras);
        botonatras.setLayout(botonatrasLayout);
        botonatrasLayout.setHorizontalGroup(
            botonatrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 12, Short.MAX_VALUE)
        );
        botonatrasLayout.setVerticalGroup(
            botonatrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 11, Short.MAX_VALUE)
        );

        jCheckBoxCrearcuenta.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxCrearcuenta.setText("Crear cuenta");

        javax.swing.GroupLayout FondoLayout = new javax.swing.GroupLayout(Fondo);
        Fondo.setLayout(FondoLayout);
        FondoLayout.setHorizontalGroup(
            FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FondoLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldPassword2)
                    .addComponent(RepeatPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                    .addComponent(jTextFieldLastName)
                    .addComponent(jTextFieldFirstName)
                    .addComponent(jTextFieldPassword, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(FirstName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Address, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextFieldAddress, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)))
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, FondoLayout.createSequentialGroup()
                                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jTextFieldEmail, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                                        .addComponent(Nif, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jTextFieldPhoneNumber, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jTextFieldNif, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12, 12, 12)))))
                .addGap(0, 12, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FondoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(signUp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(84, 84, 84)
                .addComponent(jCheckBoxCrearcuenta)
                .addGap(70, 70, 70))
            .addGroup(FondoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(botonatras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(DragDrop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Close, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(FondoLayout.createSequentialGroup()
                    .addGap(0, 189, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 463, Short.MAX_VALUE)))
        );
        FondoLayout.setVerticalGroup(
            FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FondoLayout.createSequentialGroup()
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Close, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(DragDrop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonatras, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addComponent(Address)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldAddress))
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addComponent(FirstName)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jTextFieldPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldEmail))
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(RepeatPassword)
                    .addComponent(Nif))
                .addGap(18, 18, 18)
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldPassword2, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                    .addComponent(jTextFieldNif))
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(signUp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(jCheckBoxCrearcuenta)))
                .addGap(53, 53, 53))
            .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(FondoLayout.createSequentialGroup()
                    .addGap(0, 126, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 354, Short.MAX_VALUE)))
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

    private void jTextFieldPhoneNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPhoneNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPhoneNumberActionPerformed

    private void jTextFieldAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldAddressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldAddressActionPerformed

    private void jTextFieldPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPasswordActionPerformed

    private void signUpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_signUpMouseClicked
        // Definir los detalles del nuevo usuario
        String nif = jTextFieldNif.getText();
        String password1 = new String(jTextFieldPassword.getPassword());
        String password2 = new String(jTextFieldPassword2.getPassword());
        String firstName = jTextFieldFirstName.getText();
        String lastName = jTextFieldLastName.getText();
        String email = jTextFieldEmail.getText();
        String phoneNumber = jTextFieldPhoneNumber.getText();
        String address = jTextFieldAddress.getText();
        boolean crearCuenta = jCheckBoxCrearcuenta.isSelected();

        // Verificar campos vacíos
        if (nif.isEmpty() || password1.isEmpty() || password2.isEmpty() || firstName.isEmpty()
                || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || address.isEmpty()) {

            JOptionPane.showMessageDialog(null, "Please fill in all required fields.", "BYNARY BANK", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String hashedPassword;
        try {
            hashedPassword = bancobbdd.generateHash(password1);
        } catch (NoSuchAlgorithmException e) {
            JOptionPane.showMessageDialog(null, "Error encriptando la contraseña, contacta un administrador.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("Error: " + e.getMessage());
            return;
        }

        if (!password1.equals(password2)) {
            JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
// Llamar al método createUser y pasar los parámetros correspondientes
        boolean success = createUser(conn1, nif, firstName, lastName, email, phoneNumber, address, hashedPassword,crearCuenta);

        if (success) {
            JOptionPane.showMessageDialog(null, "El nuevo usuario ha sido creado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            Principal principal = new Principal();
            principal.setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(null, "No se ha podido crear el nuevo usuario.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_signUpMouseClicked

    private void DragDropMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DragDropMouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x - xx, y - yy);
    }//GEN-LAST:event_DragDropMouseDragged

    private void DragDropMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DragDropMousePressed
        xx = evt.getX();
        yy = evt.getY();
    }//GEN-LAST:event_DragDropMousePressed

    private void CloseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CloseMouseClicked
        Principal principal = new Principal();
        principal.setVisible(true);
        dispose();
        //System.exit(0);
    }//GEN-LAST:event_CloseMouseClicked

    private void CloseMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CloseMouseEntered
        Close.setToolTipText("Cerrar"); //Mensaje al pasar cursor por encima
    }//GEN-LAST:event_CloseMouseEntered

    private void botonatrasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonatrasMouseClicked
        // TODO add your handling code here:
        Principal principal = new Principal();
        principal.setVisible(true);
        dispose();
    }//GEN-LAST:event_botonatrasMouseClicked

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
            java.util.logging.Logger.getLogger(NewUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NewUser dialog = new NewUser(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Address;
    private javax.swing.JPanel Close;
    private javax.swing.JPanel DragDrop;
    private javax.swing.JLabel FirstName;
    private javax.swing.JPanel Fondo;
    private javax.swing.JLabel Nif;
    private javax.swing.JLabel RepeatPassword;
    private javax.swing.JPanel botonatras;
    private javax.swing.JCheckBox jCheckBoxCrearcuenta;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JTextField jTextFieldAddress;
    private javax.swing.JTextField jTextFieldEmail;
    private javax.swing.JTextField jTextFieldFirstName;
    private javax.swing.JTextField jTextFieldLastName;
    private javax.swing.JTextField jTextFieldNif;
    private javax.swing.JPasswordField jTextFieldPassword;
    private javax.swing.JPasswordField jTextFieldPassword2;
    private javax.swing.JTextField jTextFieldPhoneNumber;
    private javax.swing.JPanel signUp;
    // End of variables declaration//GEN-END:variables

}
