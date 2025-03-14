package model;

public class Task {
    private int ID;
    private int arrivalTime;
    private int serviceTime;
    private int waitingTime;
    private int originalServiceTime;

    public Task(int ID, int arrivalTime, int serviceTime) {
        this.ID = ID;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.waitingTime = 0;
        this.originalServiceTime = serviceTime; // Store the original service time
    }

    public int getID() {
        return this.ID;
    }

    public int getArrivalTime() {
        return this.arrivalTime;
    }

    public int getServiceTime() {
        return this.serviceTime;
    }

    public int getWaitingTime() {
        return this.waitingTime;
    }

    public String toString() {
        return "Task{" +
                "arrivalTime=" + arrivalTime +
                ", serviceTime=" + serviceTime +
                '}';
    }

    public synchronized void decrementServiceTime() {
        if (this.serviceTime > 0) {
            this.serviceTime--;
        }
    }

    public synchronized void incrementWaitingTime() {
        this.waitingTime++;
    }

    public int getOriginalServiceTime() {
        return this.originalServiceTime;
    }
}
