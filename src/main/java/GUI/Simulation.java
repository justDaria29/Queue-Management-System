package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.Server;
import model.Task;
import logic.SimulationManager;
import logic.StrategyType;

public class Simulation extends JFrame {
    private JLabel currentTimeLabel;
    private JLabel averageWaitingTimeLabel;
    private JLabel averageServiceTimeLabel;
    private JLabel peakHourLabel;
    private JTextArea queuesArea;

    public Simulation(int timeLimit, int maxProcessingTime, int minProcessingTime,int maxArrivalTime, int minArrivalTime, int numberOfServers, int numberOfClients, StrategyType strategyType) {
        setTitle("Simulation");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        currentTimeLabel = new JLabel("Current Time: 0");
        averageWaitingTimeLabel = new JLabel("Average Waiting Time: 0");
        averageServiceTimeLabel = new JLabel("Average Service Time: 0");
        peakHourLabel = new JLabel("Peak Hour: 0");

        infoPanel.add(currentTimeLabel);
        infoPanel.add(averageWaitingTimeLabel);
        infoPanel.add(averageServiceTimeLabel);
        infoPanel.add(peakHourLabel);

        add(infoPanel, BorderLayout.NORTH);

        queuesArea = new JTextArea();
        queuesArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(queuesArea);
        add(scrollPane, BorderLayout.CENTER);

        // Initialize and start the simulation manager
        SimulationManager simulationManager = new SimulationManager(this, timeLimit, maxProcessingTime, minProcessingTime, minArrivalTime, maxArrivalTime,numberOfServers, numberOfClients, strategyType);
        Thread simulationThread = new Thread(simulationManager);
        simulationThread.start();
    }

    public void setVisibility(boolean isVisible) {
        setVisible(isVisible);
    }

    public void updateCurrentTime(int currentTime) {
        currentTimeLabel.setText("Current Time: " + currentTime);
    }

    public void updateAverageWaitingTime(double averageWaitingTime) {
        averageWaitingTimeLabel.setText("Average Waiting Time: " + averageWaitingTime);
    }

    public void updateAverageServiceTime(double averageServiceTime) {
        averageServiceTimeLabel.setText("Average Service Time: " + averageServiceTime);
    }

    public void updatePeakHour(int peakHour) {
        peakHourLabel.setText("Peak Hour: " + peakHour);
    }

    public void updateQueues(List<Server> servers) {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (Server server : servers) {
            sb.append("Queue ").append(i).append(": ");
            for (Task task : server.getTasks()) {
                sb.append(task.toString()).append("; ");
            }
            sb.append("\n");
            i++;
        }
        queuesArea.setText(sb.toString());
    }
}
