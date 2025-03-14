package logic;
import model.*;
import java.util.List;

public interface Strategy {
    public void addTask(List<Server> servers, Task task);
}
