package handler;

import io.FileManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class JobManager {

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
    private Integer globalMax = 0;
    private String file;

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public Integer getGlobalMax() {
        return globalMax;
    }

    public void setGlobalMax(Integer globalMax) {
        this.globalMax = globalMax;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public static void main(String[] args) {
        JobManager jobManager = new JobManager();

        try {
            List<String> inputFilePaths = FileManager.getInstance().getInputFilePaths();
            inputFilePaths.forEach(filePath -> {
                Job job = new Job(filePath);
                Future<Integer> future = jobManager.getExecutor().submit(job);
                try {
                    Integer fileMax = future.get();

                    synchronized (jobManager.getGlobalMax()) {
                        if (jobManager.getGlobalMax() < fileMax) {
                            jobManager.setGlobalMax(fileMax);
                            jobManager.setFile(filePath);
                        }
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                }

            });

            FileManager.getInstance().writeOutput(jobManager.getGlobalMax(), jobManager.getFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            jobManager.getExecutor().shutdown();
        }
    }

}



