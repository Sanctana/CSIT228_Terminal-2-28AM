package utilities;

import inventory.Item;
import maps.FirstFloorMap;
import maps.Map;
import maps.SecondFloorMap;
import maps.ThirdFloorMap;
import entity.player.Character;
import entity.player.CharacterType;
import main.GamePanel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

public class SaveManager {
    private static final Path SAVE_DIR = Paths.get("saves");
    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public static Path save(GamePanel gp) throws IOException {
        Files.createDirectories(SAVE_DIR);

        Character player = gp.player;
        if (player == null || gp.map == null) {
            throw new IOException("No active game to save.");
        }

        Properties properties = new Properties();
        properties.setProperty("characterType", player.getCharacterType().name());
        properties.setProperty("characterName", player.getName());
        properties.setProperty("mapName", gp.map.getMapName());
        properties.setProperty("worldX", Integer.toString(player.worldX));
        properties.setProperty("worldY", Integer.toString(player.worldY));
        properties.setProperty("heartRate", Integer.toString(player.heartRate));
        properties.setProperty("resistance", Double.toString(player.resistance));
        properties.setProperty("finalBossDefeated", Boolean.toString(gp.isFinalBossDefeated()));

        Item[] inventory = player.getInventory();
        for (int i = 0; i < inventory.length; i++) {
            properties.setProperty("inventory." + i + ".quantity", Integer.toString(inventory[i].getQuantity()));
        }

        String fileName = "save_" + LocalDateTime.now().format(FILE_TIMESTAMP) + ".properties";
        Path savePath = SAVE_DIR.resolve(fileName);

        try (OutputStream outputStream = Files.newOutputStream(savePath)) {
            properties.store(outputStream, "Terminal 2:28 AM Save Data");
        }

        return savePath;
    }

    public static List<Path> listSaves() throws IOException {
        if (!Files.exists(SAVE_DIR)) {
            return List.of();
        }

        try (Stream<Path> paths = Files.list(SAVE_DIR)) {
            return paths.filter(path -> path.getFileName().toString().endsWith(".properties"))
                    .sorted(Comparator.comparing(SaveManager::getLastModified).reversed())
                    .toList();
        }
    }

    public static void delete(Path savePath) throws IOException {
        if (savePath != null && savePath.normalize().startsWith(SAVE_DIR)) {
            Files.deleteIfExists(savePath);
        }
    }

    public static SaveData load(Path savePath) throws IOException {
        Properties properties = new Properties();

        try (InputStream inputStream = Files.newInputStream(savePath)) {
            properties.load(inputStream);
        }

        SaveData saveData = new SaveData();
        saveData.characterType = CharacterType.valueOf(properties.getProperty("characterType"));
        saveData.mapName = properties.getProperty("mapName", "3RD FLOOR");
        saveData.worldX = Integer.parseInt(properties.getProperty("worldX", "0"));
        saveData.worldY = Integer.parseInt(properties.getProperty("worldY", "0"));
        saveData.heartRate = Integer.parseInt(properties.getProperty("heartRate", "70"));
        saveData.resistance = Double.parseDouble(properties.getProperty("resistance", "0.1"));
        saveData.finalBossDefeated = Boolean.parseBoolean(properties.getProperty("finalBossDefeated", "false"));
        saveData.inventoryQuantities = new int[] {
                Integer.parseInt(properties.getProperty("inventory.0.quantity", "3")),
                Integer.parseInt(properties.getProperty("inventory.1.quantity", "9")),
                Integer.parseInt(properties.getProperty("inventory.2.quantity", "3"))
        };

        return saveData;
    }

    public static Map createMap(String mapName, GamePanel gp) {
        return switch (mapName) {
        case "1ST FLOOR" -> new FirstFloorMap(gp);
        case "2ND FLOOR" -> new SecondFloorMap(gp);
        default -> new ThirdFloorMap(gp);
        };
    }

    public static String getDisplayName(Path savePath) {
        String fileName = savePath.getFileName().toString();
        return fileName.replace(".properties", "").replace('_', ' ').toUpperCase();
    }

    private static long getLastModified(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException e) {
            return 0L;
        }
    }

    public static class SaveData {
        public CharacterType characterType;
        public String mapName;
        public int worldX;
        public int worldY;
        public int heartRate;
        public double resistance;
        public boolean finalBossDefeated;
        public int[] inventoryQuantities;
    }
}
