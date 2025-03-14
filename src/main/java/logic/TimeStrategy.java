package logic;
import model.*;
import java.util.List;
public class TimeStrategy implements Strategy{
    @Override
    public void addTask(List<Server> servers, Task task) {
        int min=Integer.MAX_VALUE;
        Server minServer=null;
        for (Server server : servers) {
            if (min > server.getWaitingPeriod()) {
                min = server.getWaitingPeriod();
                minServer = server;
            }
        }
        assert minServer != null;
        minServer.addTask(task);
    }
}
