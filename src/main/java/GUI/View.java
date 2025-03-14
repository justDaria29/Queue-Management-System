package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import logic.StrategyType;

public class View extends JFrame {
    JLabel panel1 = new JLabel();
    public JLabel titleLabel = new JLabel("Queue Management System");
    public JLabel clientsLabel = new JLabel("No. of clients:");
    public JLabel queueLabel = new JLabel("No. of queues:");
    public JLabel simulationLabel = new JLabel("Simulation time:");
    public JLabel arrLabel = new JLabel("Min arrival time:");
    public JLabel arrivalLabel = new JLabel("Max arrival time:");
    public JLabel serLabel = new JLabel("Min service time:");
    public JLabel serviceLabel = new JLabel("Max service time:");
    public JLabel strategyLabel = new JLabel("Strategy:");
    private JTextField clients = new JTextField();
    private JTextField queues = new JTextField();
    private JTextField simulationTime = new JTextField();
    private JTextField arrivalMin = new JTextField();
    private JTextField arrivalMax = new JTextField();
    private JTextField serviceMin = new JTextField();
    private JTextField serviceMax = new JTextField();
    private JComboBox<String> strategy = new JComboBox<>();
    private JButton start = new JButton("Start");

    JFrame frame = new JFrame();

    public View() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
        titleLabel.setBounds(80, 20, 350, 40);

        clientsLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        clientsLabel.setBounds(10, 70, 150, 25);

        queueLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        queueLabel.setBounds(10, 110, 150, 25);

        simulationLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        simulationLabel.setBounds(10, 150, 150, 25);

        arrLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        arrLabel.setBounds(10, 190, 150, 25);

        arrivalLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        arrivalLabel.setBounds(10, 230, 150, 25);

        serLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        serLabel.setBounds(10, 270, 150, 25);

        serviceLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        serviceLabel.setBounds(10, 310, 150, 25);

        strategyLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        strategyLabel.setBounds(10, 350, 150, 25);

        start.setFont(new Font("Tahoma", Font.PLAIN, 15));
        start.setBounds(80, 400, 300, 30);

        clients.setHorizontalAlignment(SwingConstants.CENTER);
        clients.setFont(new Font("Tahoma", Font.PLAIN, 14));
        clients.setBounds(160, 70, 300, 25);

        queues.setHorizontalAlignment(SwingConstants.CENTER);
        queues.setFont(new Font("Tahoma", Font.PLAIN, 14));
        queues.setBounds(160, 110, 300, 25);

        simulationTime.setHorizontalAlignment(SwingConstants.CENTER);
        simulationTime.setFont(new Font("Tahoma", Font.PLAIN, 14));
        simulationTime.setBounds(160, 150, 300, 25);

        arrivalMin.setHorizontalAlignment(SwingConstants.CENTER);
        arrivalMin.setFont(new Font("Tahoma", Font.PLAIN, 14));
        arrivalMin.setBounds(160, 190, 300, 25);

        arrivalMax.setHorizontalAlignment(SwingConstants.CENTER);
        arrivalMax.setFont(new Font("Tahoma", Font.PLAIN, 14));
        arrivalMax.setBounds(160, 230, 300, 25);

        serviceMin.setHorizontalAlignment(SwingConstants.CENTER);
        serviceMin.setFont(new Font("Tahoma", Font.PLAIN, 14));
        serviceMin.setBounds(160, 270, 300, 25);

        serviceMax.setHorizontalAlignment(SwingConstants.CENTER);
        serviceMax.setFont(new Font("Tahoma", Font.PLAIN, 14));
        serviceMax.setBounds(160, 310, 300, 25);

        strategy.setFont(new Font("Tahoma", Font.PLAIN, 14));
        strategy.setBounds(160, 350, 300, 25);

        // Populate JComboBox with StrategyType values
        for (StrategyType type : StrategyType.values()) {
            strategy.addItem(type.name());
        }

        panel1.add(titleLabel);
        panel1.add(clientsLabel);
        panel1.add(queueLabel);
        panel1.add(simulationLabel);
        panel1.add(arrLabel);
        panel1.add(arrivalLabel);
        panel1.add(serLabel);
        panel1.add(serviceLabel);
        panel1.add(strategyLabel);
        panel1.add(clients);
        panel1.add(queues);
        panel1.add(simulationTime);
        panel1.add(arrivalMin);
        panel1.add(arrivalMax);
        panel1.add(serviceMin);
        panel1.add(serviceMax);
        panel1.add(strategy);
        panel1.add(start);

        frame.setContentPane(panel1);

        // Add action listener to start button
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int timeLimit = Integer.parseInt(simulationTime.getText());
                    int maxProcessingTime = Integer.parseInt(serviceMax.getText());
                    int minProcessingTime = Integer.parseInt(serviceMin.getText());
                    int maxArrivalTime = Integer.parseInt(arrivalMax.getText());
                    int minArrivalTime = Integer.parseInt(arrivalMin.getText());
                    int numberOfServers = Integer.parseInt(queues.getText());
                    int numberOfClients = Integer.parseInt(clients.getText());
                    String strategyTypeString = (String) strategy.getSelectedItem();

                    // Check if a strategy is selected
                    if (strategyTypeString == null) {
                        JOptionPane.showMessageDialog(frame, "Please select a strategy.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    StrategyType strategyType = StrategyType.valueOf(strategyTypeString);

                    Simulation simulationFrame = new Simulation(timeLimit, maxProcessingTime, minProcessingTime, maxArrivalTime, minArrivalTime,numberOfServers, numberOfClients, strategyType);
                    simulationFrame.setVisibility(true);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter valid numerical values.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid strategy selected.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public void setVisibility(boolean isVisible) {
        frame.setVisible(isVisible);
    }
}
