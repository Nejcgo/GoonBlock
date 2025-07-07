package com.github.nejcgo.goonblock.util;

import com.github.nejcgo.goonblock.classes.CustomSong;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomSongManager {
    private static final Map<String, CustomSong> loadedSongs = new HashMap<>();
    private static File songsDir;

    public static void initialize(File configDir) {
        songsDir = new File(new File(configDir, "goonblock"), "customSongs");
        if (!songsDir.exists()) {
            songsDir.mkdirs();
        }
        loadAllSongs();
    }

    public static void loadAllSongs() {
        loadedSongs.clear();
        Gson gson = new Gson();
        for (File file : Objects.requireNonNull(songsDir.listFiles((dir, name) -> name.endsWith(".json")))) {
            try (FileReader reader = new FileReader(file)) {
                CustomSong song = gson.fromJson(reader, CustomSong.class);
                if (song != null && song.id != null) {
                    loadedSongs.put(song.id, song);
                    System.out.println("Loaded custom song: " + song.name);
                }
            } catch (Exception e) {
                System.err.println("Failed to load custom song from " + file.getName());
                e.printStackTrace();
            }
        }
    }

    public static CustomSong getSongById(String id) {
        return loadedSongs.get(id);
    }

    // You could also add a method to get all loaded songs for a selection GUI
    public static Collection<CustomSong> getAllSongs() {
        return loadedSongs.values();
    }
}