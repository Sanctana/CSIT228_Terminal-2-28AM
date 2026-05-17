package utilities;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class LeaderboardManager {
    private static final Path SAVE_DIR = Paths.get("saves");
    private static final Path LB_FILE = SAVE_DIR.resolve("leaderboard.properties");
    private static final int MAX_ENTRIES = 10;

    public static class Entry {
        public String name;
        public int score;
        public int killCount;
        public int bossKillCount;
        public long totalPlayedMs;

        public Entry(String name, int score, int killCount, int bossKillCount, long totalPlayedMs) {
            this.name = name;
            this.score = score;
            this.killCount = killCount;
            this.bossKillCount = bossKillCount;
            this.totalPlayedMs = totalPlayedMs;
        }
    }

    public static List<Entry> load() {
        List<Entry> entries = new ArrayList<>();
        if (!Files.exists(LB_FILE)) return entries;

        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(LB_FILE)) {
            p.load(in);
        } catch (IOException e) {
            return entries;
        }

        int count = Integer.parseInt(p.getProperty("count", "0"));
        for (int i = 0; i < count; i++) {
            String name = p.getProperty("entry." + i + ".name", "Unknown");
            int score = Integer.parseInt(p.getProperty("entry." + i + ".score", "0"));
            int kills = Integer.parseInt(p.getProperty("entry." + i + ".kills", "0"));
            int bosses = Integer.parseInt(p.getProperty("entry." + i + ".bosses", "0"));
            long ms = Long.parseLong(p.getProperty("entry." + i + ".ms", "0"));
            entries.add(new Entry(name, score, kills, bosses, ms));
        }
        return entries;
    }

    public static void save(List<Entry> entries) {
        try {
            Files.createDirectories(SAVE_DIR);
            Properties p = new Properties();
            p.setProperty("count", String.valueOf(entries.size()));
            for (int i = 0; i < entries.size(); i++) {
                Entry e = entries.get(i);
                p.setProperty("entry." + i + ".name", e.name);
                p.setProperty("entry." + i + ".score", String.valueOf(e.score));
                p.setProperty("entry." + i + ".kills", String.valueOf(e.killCount));
                p.setProperty("entry." + i + ".bosses", String.valueOf(e.bossKillCount));
                p.setProperty("entry." + i + ".ms", String.valueOf(e.totalPlayedMs));
            }
            try (OutputStream out = Files.newOutputStream(LB_FILE)) {
                p.store(out, "Terminal 2:28 AM Leaderboard");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addEntry(Entry newEntry) {
        List<Entry> entries = load();
        entries.add(newEntry);
        entries.sort((a, b) -> Integer.compare(b.score, a.score));
        if (entries.size() > MAX_ENTRIES) {
            entries = entries.subList(0, MAX_ENTRIES);
        }
        save(entries);
    }

    public static void updateEntryName(int index, String newName) {
        List<Entry> entries = load();
        if (index >= 0 && index < entries.size()) {
            entries.get(index).name = newName;
            save(entries);
        }
    }
}