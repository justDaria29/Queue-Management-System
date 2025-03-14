package logic;

import model.Server;
import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scheduler {

    private final ArrayList<Server> servers;
    private final int max;
    private Strategy strategy;
    private final ExecutorService executor;

    public Scheduler(int max, StrategyType type) {
        this.max = max;
        this.servers = new ArrayList<>();
        this.executor = Executors.newFixedThreadPool(max);

        for (int i = 0; i < max; i++) {
            Server server = new Server(10); // Assuming each server can handle 10 tasks at a time
            servers.add(server);
            executor.execute(server); // Start the server
        }

        setStrategy(type);
    }

    public void setStrategy(StrategyType type) {
        switch (type) {
            case SHORTEST_QUEUE:
                this.strategy = new ShortestQueueStrategy();
                break;
            case SHORTEST_TIME:
                this.strategy = new TimeStrategy();
                break;
            default:
                throw new IllegalArgumentException("Invalid strategy type");
        }
    }

    public void dispatchTask(Task task) {
        strategy.addTask(servers, task);
    }

    public List<Server> getServers() {
        return servers;
    }

    public void stopAllServers() {
        for (Server server : servers) {
            server.stopServer();
        }
        executor.shutdown();
    }
}
