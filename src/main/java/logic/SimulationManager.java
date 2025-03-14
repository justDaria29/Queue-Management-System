package logic;

import model.Server;
import model.Task;
import GUI.Simulation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class SimulationManager implements Runnable {
    private static final Logger logger = Logger.getLogger(SimulationManager.class.getName());

    // Data read from UI
    private int timeLimit;
    private int maxProcessingTime;
    private int minProcessingTime;
    private int maxArrivalTime;
    private int minArrivalTime;
    private int numberOfServers;
    private int numberOfClients;
    private StrategyType selectionPolicy;

    // Entity responsible for queue management and client distribution
    private Scheduler scheduler;

    // Frame for displaying simulation
    private Simulation frame;

    // Pool of tasks (client shopping in the store)
    private List<Task> generatedTasks;
    private ExecutorService executorService;

    private int peakHour;
    private int peakClients;
    private double averageWaitingTime;
    private double averageServiceTime;

    private volatile boolean running = true;

    public SimulationManager(Simulation frame, int timeLimit, int maxProcessingTime, int minProcessingTime,int maxArrivalTime, int minArrivalTime,int numberOfServers, int numberOfClients, StrategyType selectionPolicy) {
        this.timeLimit = timeLimit;
        this.maxProcessingTime = maxProcessingTime;
        this.minProcessingTime = minProcessingTime;
        this.maxArrivalTime = maxArrivalTime;
        this.minArrivalTime = minArrivalTime;
        this.numberOfServers = numberOfServers;
        this.numberOfClients = numberOfClients;
        this.selectionPolicy = selectionPolicy;
        this.frame = frame;
        this.generatedTasks = new CopyOnWriteArrayList<>();
        this.scheduler = new Scheduler(numberOfServers, selectionPolicy);
        this.executorService = Executors.newFixedThreadPool(numberOfServers);

        generateRandomTasks();
        startServers();
    }

    private void generateRandomTasks() {
        Random rand = new Random();
        for (int i = 0; i < numberOfClients; i++) {
            int serviceTime = rand.nextInt((maxProcessingTime - minProcessingTime) + 1) + minProcessingTime;
            int arrivalTime = (int)(Math.random()*(maxArrivalTime - minArrivalTime)+minArrivalTime);
            Task task = new Task(i + 1, arrivalTime, serviceTime);
            generatedTasks.add(task);
        }
        generatedTasks.sort(Comparator.comparingInt(Task::getArrivalTime));
    }

    private void startServers() {
        for (Server server : scheduler.getServers()) {
            executorService.execute(server);
        }
    }

    @Override
    public void run() {
        int currentTime = 0;

        while (running && currentTime <= timeLimit) {
            for (Task task : new ArrayList<>(generatedTasks)) {
                if (task.getArrivalTime() == currentTime) {
                    scheduler.dispatchTask(task);
                    generatedTasks.remove(task);
                }
            }

            updateStatistics(currentTime);
            frame.updateCurrentTime(currentTime); // Update the current time in the UI
            frame.updateQueues(scheduler.getServers()); // Update the queues in the UI
            logToFile(currentTime);

            currentTime++;

            try {
                Thread.sleep(1000); // Wait an interval of 1 second
            } catch (InterruptedException e) {
                logger.warning("Simulation manager thread interrupted");
                Thread.currentThread().interrupt();
                break;
            }
        }

        stopServers();
        logFinalStatistics();
    }

    private void updateStatistics(int currentTime) {
        int currentClients = 0;

        for (Server server : scheduler.getServers()) {
            currentClients += server.getTasks().size();
        }

        if (currentClients > peakClients) {
            peakClients = currentClients;
            peakHour = currentTime;
        }

        // Update average times
        double totalWaitingTime = 0;
        double totalServiceTime = 0;
        for (Server server : scheduler.getServers()) {
            totalWaitingTime += server.getTotalWaitingTime();
            totalServiceTime += server.getTotalServiceTime();
        }
        averageWaitingTime = totalWaitingTime / numberOfClients;
        averageServiceTime = totalServiceTime / numberOfClients;

        frame.updateAverageWaitingTime(averageWaitingTime);
        frame.updateAverageServiceTime(averageServiceTime);
        frame.updatePeakHour(peakHour);
    }

    private void logToFile(int currentTime) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("simulation_log.txt", true))) {
            writer.write("Time " + currentTime + "\n");
            writer.write("Waiting Clients:\n");

            for (Task task : generatedTasks) {
                writer.write(task.toString() + "\n");
            }

            int i = 1;
            for (Server server : scheduler.getServers()) {
                writer.write("Queue " + i + ": ");
                for (Task task : server.getTasks()) {
                    writer.write(task.toString() + "; ");
                }
                writer.write("\n");
                i++;
            }

            writer.write("Average Waiting Time: " + averageWaitingTime + "\n");
            writer.write("Average Service Time: " + averageServiceTime + "\n");
            writer.write("Peak Hour: " + peakHour + "\n");
            writer.write("\n");
        } catch (IOException e) {
            logger.severe("Failed to write to log file: " + e.getMessage());
        }
    }

    private void logFinalStatistics() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("simulation_final_statistics.txt"))) {
            writer.write("Final Statistics\n");
            writer.write("Average Waiting Time: " + averageWaitingTime + "\n");
            writer.write("Average Service Time: " + averageServiceTime + "\n");
            writer.write("Peak Hour: " + peakHour + "\n");
        } catch (IOException e) {
            logger.severe("Failed to write final statistics to log file: " + e.getMessage());
        }
    }

    private void stopServers() {
        for (Server server : scheduler.getServers()) {
            server.stopServer();
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS))
                    logger.severe("Executor service did not terminate");
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void stopSimulation() {
        this.running = false;
    }
}
