package takenoko.utils;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

public class StatsHandler {

    private static Path DEFAULT_PATH = Path.of("src", "main", "resources", "stats");
    private static String DEFAULT_FILE_NAME = "gamestats.csv";

    public boolean write(List<Integer> stats) {

        // check if folder exists
        File folder = new File(DEFAULT_PATH.toString());
        if (!folder.exists() && !folder.mkdir()) {
            return false;
        }

        // check if file exists
        File file = new File(DEFAULT_PATH.toString(), DEFAULT_FILE_NAME);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        // write stats to file
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write("");
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
