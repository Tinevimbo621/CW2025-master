
package com.comp2042.LeaderBoard;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
/**Class to get the txt file with the score details, save a new score entry, load score entries and sort them in descending order*/
public class LeaderboardManager {

    private static final String FILE_PATH = "leaderboard.txt";

    // Save a new score entry
    public void saveEntry(ScoreEntry entry) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(entry.getPlayerName() + ";" +
                    entry.getScore() + ";" +
                    entry.getGameMode() + ";" +
                    entry.getTimestamp().toString());
            writer.newLine();
        }
    }

    // Load all score entries
    public List<ScoreEntry> loadEntries() throws IOException {
        List<ScoreEntry> entries = new ArrayList<>();

        if (!Files.exists(Paths.get(FILE_PATH)))
            return entries;  // No file yet

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length != 4) continue;

                String name = parts[0];
                int score = Integer.parseInt(parts[1]);
                String mode = parts[2];
                LocalDateTime timestamp = LocalDateTime.parse(parts[3]);

                ScoreEntry entry = new ScoreEntry(name, score, mode);
                entry.setTimestamp(timestamp); // overwrite auto-now()
                entries.add(entry);
            }
        }

        // Sort by score descending
        entries.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        return entries;
    }
}
