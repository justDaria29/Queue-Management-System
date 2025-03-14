//package model;
//
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.Iterator;
//import java.util.logging.Logger;
//
//public class Server implements Runnable {
//    private static final Logger logger = Logger.getLogger(Server.class.getName());
//    private ArrayBlockingQueue<Task> tasks;
//    private AtomicInteger waitingPeriod;
//    private AtomicInteger totalServiceTime;
//    private AtomicInteger totalWaitingTime;
//    private boolean running;
//
//    public Server(int max) {
//        this.tasks = new ArrayBlockingQueue<>(max);
//        this.waitingPeriod = new AtomicInteger(0);
//        this.totalServiceTime = new AtomicInteger(0);
//        this.totalWaitingTime = new AtomicInteger(0);
//        this.running = true; // Initialize running to true to start the server
//    }
//
//    public synchronized void addTask(Task newTask) {
//        this.tasks.add(newTask);
//        this.waitingPeriod.addAndGet(newTask.getServiceTime());
//    }
//
//    @Override
//    public void run() {
//        while (this.running) {
//            Iterator<Task> iterator = this.tasks.iterator();
//
//            // Increment waiting time for all tasks
//            while (iterator.hasNext()) {
//                Task task = iterator.next();
//                task.incrementWaitingTime();
//            }
//
//            Task currentTask = this.tasks.peek();
//            if (currentTask != null) {
//                currentTask.decrementServiceTime();
//                this.waitingPeriod.getAndAdd(-1);
//
//                if (currentTask.getServiceTime() == 0) {
//                    this.totalWaitingTime.addAndGet(currentTask.getWaitingTime());
//                    this.totalServiceTime.addAndGet(currentTask.getOriginalServiceTime());
//                    this.tasks.poll(); // Use poll() instead of remove()
//                }
//            }
//
//            try {
//                Thread.sleep(1000L);
//            } catch (InterruptedException e) {
//                logger.warning("Server thread interrupted");
//                Thread.currentThread().interrupt();
//            }
//        }
//    }
//
//    public ArrayBlockingQueue<Task> getTasks() {
//        return this.tasks;
//    }
//
//    public int getWaitingPeriod() {
//        return waitingPeriod.get();
//    }
//
//    public void stopServer() {
//        this.running = false;
//    }
//
//    public double getTotalWaitingTime() {
//        return totalWaitingTime.get();
//    }
//
//    public double getTotalServiceTime() {
//        return totalServiceTime.get();
//    }
//}
//
//

package model;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Iterator;
import java.util.logging.Logger;

public class Server extends Thread {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;
    private AtomicInteger totalServiceTime;
    private AtomicInteger totalWaitingTime;
    private volatile boolean running;

    public Server(int max) {
        this.tasks = new ArrayBlockingQueue<>(max);
        this.waitingPeriod = new AtomicInteger(0);
        this.totalServiceTime = new AtomicInteger(0);
        this.totalWaitingTime = new AtomicInteger(0);
        this.running = true; // Initialize running to true to start the server
    }

    public synchronized void addTask(Task newTask) {
        this.tasks.add(newTask);
        this.waitingPeriod.addAndGet(newTask.getServiceTime());
    }

    @Override
    public void run() {
        while (this.running) {
            Task currentTask = null;

            synchronized (this) {
                Iterator<Task> iterator = this.tasks.iterator();
                while (iterator.hasNext()) {
                    Task task = iterator.next();
                    task.incrementWaitingTime();
                }

                currentTask = this.tasks.peek();
                if (currentTask != null) {
                    currentTask.decrementServiceTime();
                    this.waitingPeriod.decrementAndGet();
                }
            }

            if (currentTask != null && currentTask.getServiceTime() == 0) {
                synchronized (this) {
                    this.totalWaitingTime.addAndGet(currentTask.getWaitingTime());
                    this.totalServiceTime.addAndGet(currentTask.getOriginalServiceTime());
                    this.tasks.poll();
                }
            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                logger.warning("Server thread interrupted");
                Thread.currentThread().interrupt();
            }
        }
    }

    public BlockingQueue<Task> getTasks() {
        return this.tasks;
    }

    public int getWaitingPeriod() {
        return waitingPeriod.get();
    }

    public void stopServer() {
        this.running = false;
    }

    public double getTotalWaitingTime() {
        return totalWaitingTime.get();
    }

    public double getTotalServiceTime() {
        return totalServiceTime.get();
    }
}
