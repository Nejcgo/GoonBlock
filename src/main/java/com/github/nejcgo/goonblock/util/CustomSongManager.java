package com.github.nejcgo.goonblock.util; // Your package may vary

import com.github.nejcgo.goonblock.classes.CustomSong;
import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomSongManager {

    public static final Map<String, CustomSong> loadedSongs = new HashMap<>();

    // The name here must match the filename in resources (without .json).
    private static final List<String> INTERNAL_SONG_IDS = Arrays.asList(
            "mesmerizer",
            "campfireLimbo",
            "freedom"
    );

    /**
     * Initializes the song manager and loads all predefined internal songs.
     */
    public static void initialize() {
        loadAllSongs();
    }

    /**
     * Loads songs from the mod's internal resources based on the INTERNAL_SONG_IDS list.
     */
    public static void loadAllSongs() {
        loadedSongs.clear();
        Gson gson = new Gson();
        IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();

        for (String songId : INTERNAL_SONG_IDS) {
            ResourceLocation songLocation = new ResourceLocation("goonblock", "custom_songs/" + songId + ".json");

            System.out.println("GoonBlock: Attempting to load internal song: " + songLocation);

            try {
                // Use the resource manager to get the resource as a stream
                IResource resource = resourceManager.getResource(songLocation);
                try (InputStream inputStream = resource.getInputStream();
                     InputStreamReader reader = new InputStreamReader(inputStream)) {

                    CustomSong song = gson.fromJson(reader, CustomSong.class);
                    if (song != null && song.id != null) {
                        loadedSongs.put(song.id, song);
                        System.out.println("GoonBlock: Successfully loaded custom song: " + song.name);
                    }
                }
            } catch (Exception e) {
                System.err.println("GoonBlock: CRITICAL - Failed to load internal song from " + songLocation);
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets a loaded song by its unique ID.
     * @param id The ID of the song (e.g., "my_first_song").
     * @return The CustomSong object, or null if not found.
     */
    public static CustomSong getSongById(String id) {
        return loadedSongs.get(id);
    }

    /**
     * Gets a collection of all successfully loaded songs.
     * @return A collection of CustomSong objects.
     */
    public static Collection<CustomSong> getAllSongs() {
        return loadedSongs.values();
    }
}