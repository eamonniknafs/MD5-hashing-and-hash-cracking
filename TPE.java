import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TPE extends ThreadPoolExecutor {
    private long timeout = -1;
    private TimeUnit time;

    

    public TPE(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit time, BlockingQueue<Runnable> workQueue, long timeout) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, time, workQueue);
        this.time = time;
        this.timeout = timeout;
    }

    /* Super constructors not shown */
    public void setTimeout(long timeout, TimeUnit time) {
        this.timeout = timeout;
        this.time = time;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return new FT<>(super.submit(task), timeout, time);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return new FT<>(super.submit(task, result), timeout, time);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return new FT<>(super.submit(task), timeout, time);
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}