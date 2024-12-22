package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

public class AgeCalculatorApp2 {
    private static final String DATA_FILE = "age_data.txt";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AgeCalculatorApp2::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Age Calculator App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2, 10, 10));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();

        JLabel dobLabel = new JLabel("Date of Birth (YYYY-MM-DD):");
        JTextField dobField = new JTextField();

        JButton calculateButton = new JButton("Calculate Age");
        JLabel ageLabel = new JLabel("Age:");

        JTextArea dataArea = new JTextArea(10, 30);
        dataArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(dataArea);

        JButton saveButton = new JButton("Save Record");
        JButton loadButton = new JButton("Load Records");
        JButton updateButton = new JButton("Update Record");
        JButton deleteButton = new JButton("Delete Record");

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(dobLabel);
        panel.add(dobField);
        panel.add(calculateButton);
        panel.add(ageLabel);
        panel.add(saveButton);
        panel.add(loadButton);
        panel.add(updateButton);
        panel.add(deleteButton);

        frame.getContentPane().add(panel, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Event handling
        calculateButton.addActionListener(e -> {
            String dobInput = dobField.getText();
            try {
                LocalDate dob = LocalDate.parse(dobInput, DateTimeFormatter.ISO_DATE);
                int age = Period.between(dob, LocalDate.now()).getYears();
                ageLabel.setText("Age: " + age);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid date format! Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        saveButton.addActionListener(e -> {
            String name = nameField.getText();
            String dob = dobField.getText();
            try {
                if (name.isEmpty() || dob.isEmpty()) {
                    throw new IllegalArgumentException("Name or DOB cannot be empty.");
                }
                saveRecord(name, dob);
                JOptionPane.showMessageDialog(frame, "Record saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loadButton.addActionListener(e -> {
            try {
                List<String> records = loadRecords();
                dataArea.setText(String.join("\n", records));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error loading records!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateButton.addActionListener(e -> {
            String name = nameField.getText();
            String dob = dobField.getText();
            try {
                if (name.isEmpty() || dob.isEmpty()) {
                    throw new IllegalArgumentException("Name or DOB cannot be empty.");
                }
                updateRecord(name, dob);
                JOptionPane.showMessageDialog(frame, "Record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            String name = nameField.getText();
            try {
                deleteRecord(name);
                JOptionPane.showMessageDialog(frame, "Record deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    private static void saveRecord(String name, String dob) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(DATA_FILE), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(name + "," + dob);
            writer.newLine();
        }
    }

    private static List<String> loadRecords() throws IOException {
        if (!Files.exists(Paths.get(DATA_FILE))) {
            return Collections.emptyList();
        }
        return Files.readAllLines(Paths.get(DATA_FILE));
    }

    private static void updateRecord(String name, String dob) throws IOException {
        List<String> records = loadRecords();
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(DATA_FILE), StandardOpenOption.TRUNCATE_EXISTING)) {
            boolean found = false;
            for (String record : records) {
                if (record.startsWith(name + ",")) {
                    writer.write(name + "," + dob);
                    found = true;
                } else {
                    writer.write(record);
                }
                writer.newLine();
            }
            if (!found) {
                throw new IllegalArgumentException("Record not found.");
            }
        }
    }

    private static void deleteRecord(String name) throws IOException {
        List<String> records = loadRecords();
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(DATA_FILE), StandardOpenOption.TRUNCATE_EXISTING)) {
            boolean found = false;
            for (String record : records) {
                if (!record.startsWith(name + ",")) {
                    writer.write(record);
                    writer.newLine();
                } else {
                    found = true;
                }
            }
            if (!found) {
                throw new IllegalArgumentException("Record not found.");
            }
        }
    }
}