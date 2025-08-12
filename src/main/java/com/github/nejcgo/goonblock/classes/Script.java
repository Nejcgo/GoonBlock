package com.github.nejcgo.goonblock.classes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Script {
    private final List<ScriptPage> pages;

    // The constructor takes the list of pages that have been parsed
    public Script(List<ScriptPage> pages) {
        this.pages = pages;
    }

    public List<ScriptPage> getPages() {
        return this.pages;
    }

    /**
     * The core parsing logic. This static method takes the content of a JSON file
     * and uses Gson to turn it into a list of ScriptPage objects.
     * @param jsonContent The raw string content from the .json file.
     * @return A new Script object containing all the pages.
     */
    public static Script fromJson(String jsonContent) {
        Gson gson = new Gson();

        // Because the JSON is a list of objects, we need to give Gson a special "Type Token"
        // to tell it that we want an ArrayList of ScriptPage.
        Type listType = new TypeToken<ArrayList<ScriptPage>>(){}.getType();

        List<ScriptPage> parsedPages = gson.fromJson(jsonContent, listType);
        return new Script(parsedPages);
    }

    /**
     * A helper method to load a script directly from your mod's resources.
     * @param resourcePath The path to the file inside your resources folder (e.g., "/assets/goonblock/scripts/welcome.json")
     * @return A fully loaded Script object.
     * @throws IOException if the file cannot be found or read.
     */
    public static Script loadFromFile(String resourcePath) throws IOException {
        InputStream inputStream = Script.class.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("Cannot find resource: " + resourcePath);
        }

        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        return fromJson(stringBuilder.toString());
    }
}
