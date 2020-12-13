package Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPoolManager {

    public static List<Object> manage(List<Callable> threads) {
        List<Object> results = new ArrayList<>();
        int numThreads = threads.size();
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CompletionService completionService= new ExecutorCompletionService<>(executorService);

        List<Future<Boolean>> futures = new ArrayList<>();

        for (Callable<Object> thread : threads) {
            futures.add(completionService.submit(thread));
        }
        int counter = 0;
        Iterator<Future<Boolean>> sr = futures.iterator();

        try {
            while (sr.hasNext() && counter < numThreads) {
                counter++;
                results.add(completionService.take().get());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        try {
            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            //System.err.println("tasks interrupted");
        }
        finally {
            if (!executorService.isTerminated()) {
                //System.err.println("need to cancel non-finished tasks");
            }
            executorService.shutdownNow();
            //System.out.println("shutdown finished");
        }

        return results;
    }
}
