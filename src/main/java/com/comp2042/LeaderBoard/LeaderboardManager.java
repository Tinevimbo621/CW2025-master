
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
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    public LeaderboardManager() {
        // Prevent instantiation
    }

    /** Path to the leaderboard file. */
    private static final String FILE_PATH = "leaderboard.txt";
    private static final int MAX_ENTRIES = 10;

    /**
     * Saves a new score entry to the leaderboard file.
     * Each entry is appended in the format:
     * {@code playerName;score;gameMode;timestamp}.
     * Keeps only the top MAX_ENTRIES scores.
     * @param entry the score entry to save
     * @throws IOException if writing to the file fails
     */

    public void saveEntry(ScoreEntry entry) throws IOException {
        List<ScoreEntry> entries = loadEntries(); // Load existing scores
        entries.add(entry);

        // Sort descending by score
        entries.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());

        // Keep only top MAX_ENTRIES
        if (entries.size() > MAX_ENTRIES) {
            entries = new ArrayList<>(entries.subList(0, MAX_ENTRIES));
        }
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
     * @return  only the top MAX_ENTRIES scores sorted by score.
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

        // Sort descending and trim to MAX_ENTRIES
        entries.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());
        if (entries.size() > MAX_ENTRIES) {
            entries = new ArrayList<>(entries.subList(0, MAX_ENTRIES));
        }

        return entries;
    }
}
