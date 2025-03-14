package logic;
import model.*;
import java.util.List;
public class ShortestQueueStrategy implements Strategy{
    @Override
    public void addTask(List<Server> servers, Task task) {
        int min=Integer.MAX_VALUE;
        Server minServer=servers.get(1);
        for(Server server:servers)
        {
            if(server.getTasks().size() < min){
                min=server.getTasks().size();
                minServer = server;
            }
        }
        minServer.addTask(task);
    }
}
