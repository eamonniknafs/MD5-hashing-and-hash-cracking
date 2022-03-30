import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FT<V> implements Future<V> {
    private Future<V> task;
    private long timeout;
    private TimeUnit time;

    public FT(Future<V> task, long timeout, TimeUnit time) {
        this.task = task;
        this.timeout = timeout;
        this.time = time;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return task.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return task.isCancelled();
    }

    @Override
    public boolean isDone() {
        return task.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        try {
            if (timeout > 0) {
                return task.get(timeout, time);
            }
            return task.get();
        } catch (TimeoutException e) {
            this.cancel(true);
            throw new ExecutionException(
                    "Forced timeout after " + timeout + " " + time.name(), null);
        }
    }

    @Override
    public V get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        try {
            return task.get(timeout, unit);
        } catch (TimeoutException e) {
            this.cancel(true);
            throw new ExecutionException(
                    "Timeout after " + timeout + " " + unit.name(), null);
        }
    }
}