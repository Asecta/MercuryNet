package com.pandoaspen.mercury.common.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.logging.Logger;

public class ConfigLoader {

    private static final Gson GSON = new Gson();

    private static final Logger LOGGER = Logger.getLogger(ConfigLoader.class.getName());

    private static Class getClassCaller() {
        String thisClassName = ConfigLoader.class.getName();
        StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < traceElements.length; i++) {
            StackTraceElement stackTraceElement = traceElements[i];
            if (thisClassName.equals(stackTraceElement.getClassName())) continue;
            try {
                return Class.forName(stackTraceElement.getClassName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Cannot find class caller...");
    }

    public static <T> T load(File dataFolder, String file, Class<T> clazz) {
        return load(new File(dataFolder, file), clazz);
    }

    public static <T> T load(File file, Class<T> clazz) {
        return load(getClassCaller(), LOGGER, GSON, file, clazz);
    }

    public static byte[] loadRaw(Class caller, Logger logger, String resource, File destination) {
        saveResource(caller, resource, destination, false);
        try {
            return Files.readAllBytes(destination.toPath());
        } catch (Exception e) {
            logger.severe("The resource couldn't be loaded!");
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] loadRaw(Logger logger, String resource, File destination) {
        return loadRaw(getClassCaller(), logger, resource, destination);
    }

    public static void saveResource(Class caller, String resource, File destination, boolean replace) {
        if (destination.exists() && !replace) return;

        try {
            if (!destination.exists()) {
                destination.getParentFile().mkdirs();
                destination.createNewFile();
            }

            InputStream inputStream = caller.getClassLoader().getResourceAsStream(resource);
            OutputStream out = new FileOutputStream(destination);
            byte[] buf = new byte[1024];

            int len;
            while ((len = inputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            out.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T load(Class caller, Logger logger, Gson gson, File file, Class<T> clazz) {
        saveResource(caller, file.getName(), file, false);

        try (InputStream inputStream = new FileInputStream(file)) {
            Yaml yaml = new Yaml();
            Object obj = yaml.load(inputStream);
            JsonElement jsonElement = gson.toJsonTree(obj);
            T configObject = gson.fromJson(jsonElement, clazz);
            logger.fine("Config loaded: \"" + file.getAbsolutePath() + "\" as \"" + clazz.toString() + "\"");
            return configObject;
        } catch (Exception e) {
            logger.severe("The configuration couldn't be loaded!");
            e.printStackTrace();
        }
        return null;
    }


}