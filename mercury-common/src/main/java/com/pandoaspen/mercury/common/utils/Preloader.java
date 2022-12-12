package com.pandoaspen.mercury.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Preloader {
    private static final Logger LOGGER = Logger.getLogger(Preloader.class.getName());

    public static void preloadClasses(File file) {
        preloadClasses(LOGGER, file);
    }

    public static void preloadClasses(Logger logger, File file) {
        try (ZipInputStream zip = new ZipInputStream(new FileInputStream(file))) {
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.');
                    className = className.substring(0, className.length() - 6);
                    try {
                        Class.forName(className);
                        logger.finest("Preloaded class " + className);
                    } catch (Throwable ignored) {
                        logger.finest("Failed to load class " + className);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Preload done");
    }

    public static File findPluginJarFile(File pluginDirectory, String classNameToFind) {
        if (!pluginDirectory.isDirectory()) {
            return pluginDirectory;
        }

        for (File file : pluginDirectory.listFiles()) {
            if (file.isDirectory()) continue;
            if (!file.getName().endsWith(".jar")) return pluginDirectory;

            try {
                ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
                for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                    if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                        String className = entry.getName().replace('/', '.');
                        className = className.substring(0, className.length() - 6);

                        if (className.equals(classNameToFind)) {
                            return file;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
