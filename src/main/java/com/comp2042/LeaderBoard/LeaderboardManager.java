
package com.comp2042.LeaderBoard;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
/**
 * Manages leaderboard entries by saving scores to a text file,
 * loading them back into memory, and sorting them in descending order.
 * <p>
 * The leaderboard is persisted in a simple text file where each entry
 * is stored as: {@code playerName;score;gameMode;timestamp}.
 * </p>
 */
public class LeaderboardManager {
    /** Path to the leaderboard file. */
    private static final String FILE_PATH = "leaderboard.txt";

    /**
     * Saves a new score entry to the leaderboard file.
     * Each entry is appended in the format:
     * {@code playerName;score;gameMode;timestamp}.
     *
     * @param entry the score entry to save
     * @throws IOException if writing to the file fails
     */

    public void saveEntry(ScoreEntry entry) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(entry.getPlayerName() + ";" +
                    entry.getScore() + ";" +
                    entry.getGameMode() + ";" +
                    entry.getTimestamp().toString());
            writer.newLine();
        }
    }

    /**
     * Loads all score entries from the leaderboard file.
     * <p>
     * If the file does not exist, an empty list is returned.
     * Entries are parsed and sorted in descending order by score.
     * </p>
     *
     * @return a list of score entries sorted by score (highest first)
     * @throws IOException if reading from the file fails
     */

    public List<ScoreEntry> loadEntries() throws IOException {
        List<ScoreEntry> entries = new ArrayList<>();

        if (!Files.exists(Paths.get(FILE_PATH)))
            return entries;

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
                entry.setTimestamp(timestamp);
                entries.add(entry);
            }
        }

        entries.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());

        return entries;
    }
}
