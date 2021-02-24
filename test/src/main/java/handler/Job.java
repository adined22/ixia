package handler;

import io.FileManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Job implements Callable<Integer> {
    private final Pattern pattern = Pattern.compile("[^;]*\\d+[^;]*");
    private String filePath;

    public Job(String filePath) {
        this.filePath = filePath;
    }

    public Integer call() {
        String threadName = Thread.currentThread().getName();
        Integer max = 0;

        try {
            List<String> fileContent = FileManager.getInstance().getFileContent(filePath);
            for (String line : fileContent) {
                Integer lineMax = findLineMax(line);
                max = max < lineMax ? lineMax : max;
            }
            System.out.println("[" + threadName + "]: max = " + max);
            return max;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private Integer findLineMax(String line) {
        Matcher m = pattern.matcher(line);
        Integer max = 0;
        while (m.find()) {
            try {
                Integer number = Integer.parseInt(m.group());
                max = max < number ? number : max;
            } catch (NumberFormatException e) {

            }
        }

        return max;
    }

}
