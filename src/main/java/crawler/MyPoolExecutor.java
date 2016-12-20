package crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Simple pool executor
 */
class MyPoolExecutor<T extends Task> {

    private final int poolCapacity;
    ConcurrentMap<T, Boolean> tasks;
    List<Thread> threads;

    private volatile int tasksInQueue = 0;

    public MyPoolExecutor(int poolCapacity) {
        this.poolCapacity = poolCapacity;

        tasks = new ConcurrentHashMap<>();
        threads = new ArrayList<>();

        for (int i = 0; i < poolCapacity; ++i) {
            Thread thread = new Thread(new TaskRunner<>(this));
            thread.setDaemon(true);
            thread.start();
            threads.add(thread);
        }
    }

    public int getPoolCapacity() {
        return poolCapacity;
    }

    public synchronized boolean execute(T task) {
        if (tasks.containsKey(task)) {
            return false;
        }

        tasks.put(task, false);
        ++tasksInQueue;

        notifyAll();
        return true;
    }

    private synchronized T getTask() throws InterruptedException {

        while (tasksInQueue == 0) {
            wait();
        }

        for (Map.Entry<T, Boolean> entry : tasks.entrySet()) {
            if (!entry.getValue()) {

                entry.setValue(true);
                --tasksInQueue;
                return entry.getKey();
            }
        }

        throw new InterruptedException();
    }

    private synchronized void endTask(T task) {
        tasks.remove(task);
        notifyAll();
    }

    public synchronized void stop() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    private static class TaskRunner<T extends Task> implements Runnable {

        MyPoolExecutor<T> executor;

        public TaskRunner(MyPoolExecutor<T> executor) {
            this.executor = executor;
        }

        @Override
        public void run() {
            try {
                while (true) {

                    T task = executor.getTask();
                    task.doWork();
                    executor.endTask(task);
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

