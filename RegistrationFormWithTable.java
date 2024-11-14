import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegistrationFormWithTable extends JFrame {
    private JTextField idField, nameField, addressField, contactField;
    private JRadioButton maleRadio, femaleRadio;
    private JCheckBox termsCheck;
    private JTable userTable;
    private DefaultTableModel tableModel;

    public RegistrationFormWithTable() {
        setTitle("Registration Form");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Left side form panel
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel idLabel = new JLabel("ID:");
        idField = new JTextField();

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();

        JLabel genderLabel = new JLabel("Gender:");
        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();  // <-- Declaration for genderGroup
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);

        JLabel addressLabel = new JLabel("Address:");
        addressField = new JTextField();

        JLabel contactLabel = new JLabel("Contact:");
        contactField = new JTextField();

        termsCheck = new JCheckBox("Accept Terms and Conditions");

        JButton registerButton = new JButton("Register");
        JButton exitButton = new JButton("Exit");

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Adding components to the form panel
        formPanel.add(idLabel);
        formPanel.add(idField);
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(genderLabel);
        formPanel.add(maleRadio);
        formPanel.add(new JLabel()); // empty placeholder
        formPanel.add(femaleRadio);
        formPanel.add(addressLabel);
        formPanel.add(addressField);
        formPanel.add(contactLabel);
        formPanel.add(contactField);
        formPanel.add(termsCheck);
        formPanel.add(new JLabel()); // empty placeholder
        formPanel.add(exitButton);
        formPanel.add(registerButton);

        // Right side table panel
        JPanel tablePanel = new JPanel();
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Gender", "Address", "Contact"}, 0);
        userTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(userTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 300));
        tablePanel.add(tableScrollPane);

        // Adding panels to the frame
        add(formPanel, BorderLayout.WEST);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void registerUser() {
        String id = idField.getText();
        String name = nameField.getText();
        String gender = maleRadio.isSelected() ? "Male" : femaleRadio.isSelected() ? "Female" : "";
        String address = addressField.getText();
        String contact = contactField.getText();
        boolean termsAccepted = termsCheck.isSelected();

        if (id.isEmpty() || name.isEmpty() || gender.isEmpty() || address.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }

        if (!termsAccepted) {
            JOptionPane.showMessageDialog(this, "Please accept terms and conditions");
            return;
        }

        // Insert data into the database and add to the table
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/yourDatabase", "username", "password")) {
            String query = "INSERT INTO users (id, name, gender, address, contact) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setString(3, gender);
            stmt.setString(4, address);
            stmt.setString(5, contact);
            stmt.executeUpdate();

            // Add row to the JTable
            tableModel.addRow(new Object[]{id, name, gender, address, contact});
            JOptionPane.showMessageDialog(this, "Registration Successful");

            // Clear fields after registration
            idField.setText("");
            nameField.setText("");
            genderGroup.clearSelection();  // Clear gender selection
            addressField.setText("");
            contactField.setText("");
            termsCheck.setSelected(false);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to database");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistrationFormWithTable().setVisible(true));
    }
}
