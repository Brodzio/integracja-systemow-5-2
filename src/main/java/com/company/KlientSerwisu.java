package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class KlientSerwisu extends JFrame{

    public static LaptopsInterface laptopsInterface;
    public static Object[][] data = new Object[0][15];
    public static String[] producers;
    public static KlientSerwisu klientSerwisu = new KlientSerwisu();
    public static JScrollPane scrollPane;
    public static JPanel itemsPanel;

    public static String[] column = {"Producent", "Przekątna", "Rozdzielczość", "Powierzchnia ekranu", "Dotykowy ekran", "CPU",
            "Liczba rdzeni", "Taktowanie [MHz]", "Ilość RAM", "Pojemność dysku", "Rodzaj dysku", "GPU", "Pamięć GPU",
            "OS", "Napęd ODD"};

    public static String[] proportion = {"5:4","4:3","3:2","16:9","16:10","2:1","21:9"};

    public static String[] matrixType = {"matowa", "blyszczaca"};

    private static JButton producersBtn = new JButton("Liczba laptopów producenta");
    private static JButton matrixTypeBtn = new JButton("Lista laptopów z określonym rodzajem matrycy");
    private static JButton matrixProportionBtn = new JButton("Liczba laptopów z matrycami o określonych proporcjach");
    private static JTextArea producersTxTArea = new JTextArea();
    private static JTextArea matrixProportionTxtArea = new JTextArea();
    private static JTable jTable;

    public KlientSerwisu() {
        setTitle("Integracja Systemów - Bartłomiej Brodawka");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(100, 100);
        setLayout(new BorderLayout());
        setResizable(true);
    }

    public static void connection() {
        URL url = null;
        try {
            url = new URL("http://localhost:8888/laptops?wsdl");
            QName qName = new QName("http://company.com/", "LaptopBeanService");
            Service service = Service.create(url, qName);
            laptopsInterface = service.getPort(LaptopsInterface.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        connection();

        producers = laptopsInterface.listOfProducers();

        JComboBox<String> dropDownProducersList = new JComboBox<>(producers);
        producersTxTArea.setText("Liczba laptopów danego producenta: ");
        producersTxTArea.setFocusable(false);

        JComboBox<String> dropDownListMatrixType = new JComboBox<>(matrixType);

        JComboBox<String> dropDownListMatrixProportion = new JComboBox<>(proportion);
        matrixProportionTxtArea.setText("Liczba laptopow z matrycami \no określonych proporcjach: ");
        matrixProportionTxtArea.setFocusable(false);

        itemsPanel = new JPanel();
        itemsPanel.add(dropDownProducersList);
        itemsPanel.add(producersBtn);
        itemsPanel.add(producersTxTArea);
        itemsPanel.add(dropDownListMatrixType);
        itemsPanel.add(matrixTypeBtn);
        itemsPanel.add(dropDownListMatrixProportion);
        itemsPanel.add(matrixProportionBtn);
        itemsPanel.add(matrixProportionTxtArea);

        klientSerwisu.add(itemsPanel, BorderLayout.PAGE_START);

        loadTable();
        scrollPane = new JScrollPane(jTable);
        klientSerwisu.add(scrollPane, BorderLayout.CENTER);

        producersBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                producersTxTArea.setText("Liczba laptopów danego producenta: " + laptopsInterface.numberOfLaptopsByProducer(Objects.requireNonNull(dropDownProducersList.getSelectedItem()).toString()));
            }
        });

        matrixTypeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                data = laptopsInterface.listOfLaptopsByMatrix(Objects.requireNonNull(dropDownListMatrixType.getSelectedItem()).toString());
                jTable = new JTable(data, column) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                setPreferredSizeOfColumns();
                klientSerwisu.remove(scrollPane);
                klientSerwisu.scrollPane = new JScrollPane(jTable);
                klientSerwisu.add(scrollPane, BorderLayout.CENTER);
                klientSerwisu.invalidate();
                klientSerwisu.validate();
                klientSerwisu.repaint();
            }
        });

        matrixProportionBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                matrixProportionTxtArea.setText("Liczba laptopow z matrycami o określonych proporcjach: " + laptopsInterface.numberOfLaptopsByMatrixSize(Objects.requireNonNull(dropDownListMatrixProportion.getSelectedItem()).toString()));
            }
        });

        klientSerwisu.pack();
        klientSerwisu.setVisible(true);
    }

    public static void loadTable() {
        jTable = new JTable(data, column) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        setPreferredSizeOfColumns();
    }

    public static void setPreferredSizeOfColumns() {
        TableColumn columns = null;
        for(int i = 0; i < column.length; i++) {
            columns = jTable.getColumnModel().getColumn(i);
            if(i == 2 || i == 3 || i == 4 || i == 7 || i == 9 ) {
                columns.setPreferredWidth(120);
            } else if (i == 11 || i == 13) {
                columns.setPreferredWidth(200);
            } else if(i == 5) {
                columns.setPreferredWidth(50);
            } else {
                columns.setPreferredWidth(100);
            }
        }
        jTable.setPreferredScrollableViewportSize(new Dimension(jTable.getPreferredSize().width, 500));
    }
}
