package analyzer;


import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

class Executor<T extends Task> {

    private ArrayList<T> taskArray;
    private ArrayList<T> taskExecute;
    private ArrayList<RunThread> threadArray;
    private final Integer threadNumber;

    Executor(Integer threadNumber) {

        if (threadNumber < 1) {
            Logger logger = Logger.getLogger("Executor");
            logger.setLevel(Level.INFO);
            logger.fine( "ThreadNumber must be >=1 ");
            threadNumber = 1;
        }

        this.threadNumber = threadNumber;
        taskArray = new ArrayList<T>();
        taskExecute = new  ArrayList<T>();

        threadArray = new ArrayList<RunThread>();
        for (int i = 0; i < threadNumber; ++i) {
            threadArray.add(new RunThread());
        }
    }

    void start() {
        for (RunThread runThread: threadArray) {
            runThread.start();
        }
    }

    synchronized void interrupt() {

        for (RunThread runThread: threadArray) {
            runThread.interrupt();
        }
    }

    synchronized void interruptSoft() {

        try {

            while (taskExecute.size() != 0) {
                wait(1000);
            }
            interrupt();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    boolean execute(T task) {

        if (taskArray.contains(task) && taskExecute.contains(task)) {
            return false;
        }

        synchronized(taskArray) {
            taskArray.add(task);
            taskArray.notifyAll();
        }
        return true;
    }

    private synchronized Task getTask() {
        Task currentTask = taskArray.get(0);
        taskArray.remove(0);
        addExecuteTask((T) currentTask);

        return currentTask;
    }

    private synchronized void deleteExecutedTask(Task task) {
        taskExecute.remove(task);
    }

    private synchronized void addExecuteTask(T task) {
        taskExecute.add(task);
    }

    class RunThread extends Thread {
        @Override
        public void run() {

            synchronized (taskArray) {
                try {
                    while (true) {

                        while (taskArray.size() == 0) {
                            taskArray.wait();
                        }

                        Task currentTask = getTask();
                        currentTask.doWork();
                        deleteExecutedTask(currentTask);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        }
    }
}