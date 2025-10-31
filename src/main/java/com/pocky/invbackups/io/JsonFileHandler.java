package com.pocky.invbackups.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pocky.invbackups.InventoryBackupsMod;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonFileHandler<T extends Serializable> {

    private static final Path DIR = Path.of("InventoryLog");

    private final T obj;

    public JsonFileHandler(T obj) {
        this.obj = obj;
    }

    public void save(String dir, String fileName) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(this.obj);
        Path path = DIR.resolve(Path.of(dir).resolve(fileName + ".json"));

        try {
            // Создаем директорию, если ее нет
            Files.createDirectories(path.getParent());

            // Создаем файл, если его нет
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            // Записываем JSON в файл
            FileWriter writer = new FileWriter(path.toFile());
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T load(String dir, String fileName, Class<T> clazz) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Path path = DIR.resolve(Path.of(dir).resolve(fileName + ".json"));

        try {
            // Создаем директорию, если ее нет
            Files.createDirectories(path.getParent());
            // Читаем JSON из файла и возвращаем объект класса clazz
            try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
                return gson.fromJson(reader, clazz);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
