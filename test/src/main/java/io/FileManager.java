package io;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileManager {

    private static String rootPath = System.getProperty("user.dir");
    private static String configPath = Paths.get(rootPath, "max.conf").toString();
    private static String inputDirPath;
    private static String outputPath;

    private static FileManager instance;

    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }

        return instance;
    }

    private FileManager() {
        try {
            List<String> config = getFileContent(configPath);
            inputDirPath = getConfigValue(config.get(0));
            outputPath = getConfigValue(config.get(1));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public List<String> getInputFilePaths() throws IOException {
        try (Stream<Path> stream = Files.walk(Paths.get(inputDirPath), 1)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Get file content line by line
     *
     * @param filePath
     * @return
     */
    public List<String> getFileContent(String filePath) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            return stream.collect(Collectors.toList());
        }
    }

    public void writeOutput(Integer max, String filePath) throws IOException {
        PrintWriter printWriter = new PrintWriter(new FileWriter(outputPath));
        printWriter.printf("Maximum: %d\n", max);
        printWriter.printf("Location: %s", filePath);
        printWriter.close();
    }

    private String getConfigValue(String configLine) {
        int indexOf = configLine.indexOf('=');
        return configLine.substring(indexOf + 1);
    }
}
