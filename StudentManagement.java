import java.awt.*;
import java.sql.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentManagement extends JFrame {

    JTextField nameField, rollField, courseField, marksField;
    JTable table;
    DefaultTableModel model;

    public StudentManagement() {

        setTitle("Star Glass Student Dashboard");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        StarPanel background = new StarPanel();
        background.setLayout(null);
        setContentPane(background);

        JPanel card = new JPanel(new BorderLayout());
        card.setBounds(150, 80, 700, 450);
        card.setOpaque(false);
        card.setBorder(BorderFactory.createLineBorder(
                new Color(255,255,255,150), 2));
        background.add(card);

        JPanel formPanel = new JPanel(new GridLayout(2,4,10,10));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        nameField = createField();
        rollField = createField();
        courseField = createField();
        marksField = createField();

        formPanel.add(createLabel("Name"));
        formPanel.add(createLabel("Roll No"));
        formPanel.add(createLabel("Course"));
        formPanel.add(createLabel("Marks"));

        formPanel.add(nameField);
        formPanel.add(rollField);
        formPanel.add(courseField);
        formPanel.add(marksField);

        card.add(formPanel, BorderLayout.NORTH);

        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Roll No");
        model.addColumn("Course");
        model.addColumn("Marks");

        table = new JTable(model);
        table.setRowHeight(25);

        JScrollPane scroll = new JScrollPane(table);
        card.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);

        JButton addBtn = new JButton("Add");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");

        btnPanel.add(addBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        card.add(btnPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        refreshBtn.addActionListener(e -> viewStudents());

        viewStudents();
        setVisible(true);
    }

    JTextField createField() {
        JTextField field = new JTextField();
        return field;
    }

    JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        return label;
    }

    void addStudent() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO students(name, roll_no, course, marks) VALUES (?, ?, ?, ?)"
            );

            ps.setString(1, nameField.getText());
            ps.setString(2, rollField.getText());
            ps.setString(3, courseField.getText());
            ps.setInt(4, Integer.parseInt(marksField.getText()));

            ps.executeUpdate();
            con.close();
            viewStudents();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void deleteStudent() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "DELETE FROM students WHERE roll_no=?"
            );

            ps.setString(1, rollField.getText());
            ps.executeUpdate();
            con.close();
            viewStudents();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void viewStudents() {
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM students");

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("roll_no"),
                        rs.getString("course"),
                        rs.getInt("marks")
                });
            }
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class StarPanel extends JPanel {

        int[] x = new int[100];
        int[] y = new int[100];
        Random rand = new Random();

        public StarPanel() {
            for(int i=0;i<100;i++){
                x[i] = rand.nextInt(1000);
                y[i] = rand.nextInt(600);
            }

            Timer timer = new Timer(40, e -> {
                for(int i=0;i<100;i++){
                    y[i] += 1;
                    if(y[i] > getHeight()){
                        y[i] = 0;
                        x[i] = rand.nextInt(getWidth());
                    }
                }
                repaint();
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            GradientPaint gp = new GradientPaint(
                0,0,new Color(10,10,40),
                0,getHeight(),new Color(40,0,80)
            );
            g2.setPaint(gp);
            g2.fillRect(0,0,getWidth(),getHeight());

            g2.setColor(Color.WHITE);
            for(int i=0;i<100;i++){
                g2.fillOval(x[i], y[i], 2, 2);
            }
        }
    }

    public static void main(String[] args) {
        StudentManagement frame = new StudentManagement();
    }
}