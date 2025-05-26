package quang_ly_bi_da;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class QuanLyBiDa extends JFrame {
    private ArrayList<BilliardTable> tables;
    private JTable tableDisplay;
    private JTextField nameField, priceField, statusField, searchField;
    private JButton addButton, updateButton, deleteButton, searchButton, saveButton, loadButton;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private static final String DATA_FILE = "billiard_data.dat";

    public QuanLyBiDa() {
        tables = new ArrayList<>();
        initializeUI();
        loadData(); // Tự động load dữ liệu khi khởi động
    }

    private void initializeUI() {
        setTitle("Quản Lý Bàn Bi Da");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin bàn bi da"));
        inputPanel.setBackground(new Color(240, 240, 240));

        inputPanel.add(new JLabel("Tên bàn:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Giá (VNĐ/giờ):"));
        priceField = new JTextField();
        inputPanel.add(priceField);

        inputPanel.add(new JLabel("Trạng thái:"));
        statusField = new JTextField();
        inputPanel.add(statusField);

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm"));
        searchField = new JTextField(20);
        searchButton = new JButton("Tìm kiếm");
        searchPanel.add(new JLabel("Tên bàn:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        addButton = createStyledButton("Thêm", new Color(46, 204, 113));
        updateButton = createStyledButton("Cập nhật", new Color(52, 152, 219));
        deleteButton = createStyledButton("Xóa", new Color(231, 76, 60));
        saveButton = createStyledButton("Lưu", new Color(155, 89, 182));
        loadButton = createStyledButton("Đọc", new Color(241, 196, 15));

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        // Bảng hiển thị
        String[] columns = {"Tên bàn", "Giá (VNĐ/giờ)", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableDisplay = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        tableDisplay.setRowSorter(sorter);
        
        // Tùy chỉnh giao diện bảng
        tableDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableDisplay.getTableHeader().setReorderingAllowed(false);
        tableDisplay.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(tableDisplay);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách bàn bi da"));

        // Thêm các panel vào main panel
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        // Thêm sự kiện cho các nút
        addButton.addActionListener(e -> addTable());
        updateButton.addActionListener(e -> updateTable());
        deleteButton.addActionListener(e -> deleteTable());
        searchButton.addActionListener(e -> searchTable());
        saveButton.addActionListener(e -> saveData());
        loadButton.addActionListener(e -> loadData());

        // Thêm sự kiện cho bảng
        tableDisplay.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tableDisplay.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = tableDisplay.convertRowIndexToModel(selectedRow);
                    BilliardTable table = tables.get(modelRow);
                    nameField.setText(table.getName());
                    priceField.setText(String.valueOf(table.getPrice()));
                    statusField.setText(table.getStatus());
                }
            }
        });

        add(mainPanel);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        return button;
    }

    private void addTable() {
        if (!validateInput()) return;

        try {
            String name = nameField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            String status = statusField.getText().trim();

            BilliardTable table = new BilliardTable(name, price, status);
            tables.add(table);
            updateTableDisplay();
            clearFields();
            JOptionPane.showMessageDialog(this, "Thêm bàn bi da thành công!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giá hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable() {
        int selectedRow = tableDisplay.getSelectedRow();
        if (selectedRow >= 0 && validateInput()) {
            try {
                int modelRow = tableDisplay.convertRowIndexToModel(selectedRow);
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                String status = statusField.getText().trim();

                BilliardTable table = tables.get(modelRow);
                table.setName(name);
                table.setPrice(price);
                table.setStatus(status);

                updateTableDisplay();
                clearFields();
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập giá hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn cần cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteTable() {
        int selectedRow = tableDisplay.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa bàn này?", 
                "Xác nhận xóa", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                int modelRow = tableDisplay.convertRowIndexToModel(selectedRow);
                tables.remove(modelRow);
                updateTableDisplay();
                clearFields();
                JOptionPane.showMessageDialog(this, "Xóa bàn bi da thành công!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void searchTable() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 0));
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(tables);
            JOptionPane.showMessageDialog(this, "Lưu dữ liệu thành công!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu: " + ex.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                tables = (ArrayList<BilliardTable>) ois.readObject();
                updateTableDisplay();
                JOptionPane.showMessageDialog(this, "Đọc dữ liệu thành công!");
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi đọc dữ liệu: " + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên bàn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (priceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giá!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (statusField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập trạng thái!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void updateTableDisplay() {
        tableModel.setRowCount(0);
        for (BilliardTable table : tables) {
            Object[] row = {table.getName(), table.getPrice(), table.getStatus()};
            tableModel.addRow(row);
        }
    }

    private void clearFields() {
        nameField.setText("");
        priceField.setText("");
        statusField.setText("");
        tableDisplay.clearSelection();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new QuanLyBiDa().setVisible(true);
        });
    }
}

class BilliardTable implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private double price;
    private String status;

    public BilliardTable(String name, double price, String status) {
        this.name = name;
        this.price = price;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 