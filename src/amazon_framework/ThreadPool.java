package amazon_framework;

import java.util.concurrent.*;

public class ThreadPool extends ThreadPoolExecutor {
    @SuppressWarnings("rawtypes")
    public Future[] future;

    public ThreadPool(int corePoolSize, int maxQueSize, LinkedBlockingQueue<Runnable> que) {
        super(corePoolSize, corePoolSize, 0, TimeUnit.SECONDS, que);
        future = new Future[maxQueSize];
    }

    public void join_all() {
        for (int i = 0; i < future.length; i++) {
            if (future[i] == null) {
                return;
            }
            try {
                future[i].get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}