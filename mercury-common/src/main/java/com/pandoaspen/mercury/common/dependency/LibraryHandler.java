/*
 * Copyright 2015 Demigods RPG
 * Copyright 2015 Alexander Chauncey
 * Copyright 2015 Alex Bennett
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pandoaspen.mercury.common.dependency;


import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class LibraryHandler {

    public static final String MAVEN_CENTRAL = "https://repo1.maven.org/maven2/";

    private static final int BYTE_SIZE = 1024;

    private final Logger logger;
    private final File libDirectory;
    private final ClassLoader classLoader;

    private final List<String> fileNames;

    // -- CONSTRUCTOR -- //

    public LibraryHandler(Logger logger, File libDirectory, ClassLoader classLoader) {
        this.logger = logger;
        this.libDirectory = libDirectory;
        this.classLoader = classLoader;
        this.fileNames = new ArrayList<>();
        checkDirectory();
    }

    // -- HELPER METHODS -- //

    public void addLibrary(String repo, String groupId, String artifactId, String version) {
        try {
            String fileName = artifactId + "-" + version + ".jar";
            loadLibrary(fileName, new URI(repo + groupId.replace(".", "/") + "/" + artifactId + "/" + version + "/" + fileName).toURL());
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }


    public void addLibrary(final String repo, final String dependencyStr) {
        String groupId = null;
        String artifactId = null;
        String version = null;

        try {
            String[] split = dependencyStr.split(":");
            groupId = split[0];
            artifactId = split[1];
            version = split[2];
        } catch (Exception e) {
            throw new RuntimeException("Invalid dependency string: " + dependencyStr);
        }

        addLibrary(repo, groupId, artifactId, version);
    }

    public void addLibraryDirect(String repo, String fileName) {
        try {
            loadLibrary(fileName, new URI(repo + fileName).toURL());
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    public void checkDirectory() {
        // If it exists and isn't a directory, throw an error
        if (libDirectory.exists() && !libDirectory.isDirectory()) {
            logger.severe("The library directory isn't a directory!");
            return;
        }
        // Otherwise, make the directory
        else if (!libDirectory.exists()) {
            libDirectory.mkdirs();
        }

        // Check if all libraries exist

        File[] filesArray = libDirectory.listFiles();
        List<File> files = Arrays.asList(filesArray != null ? filesArray : new File[]{});

        for (File file : files) {
            if (file.getName().endsWith(".jar")) {
                fileNames.add(file.getName());
            }
        }
    }

    public void loadLibrary(String fileName, URL url) {
        // Check if the files are found or not
        File libraryFile = null;
        if (fileNames.contains(fileName)) {
            libraryFile = new File(libDirectory + "/" + fileName);
        }

        // If they aren't found, download them
        if (libraryFile == null) {
            logger.warning("Downloading " + fileName + ".");
            try {
                libraryFile = downloadLibrary(fileName, url);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // Add the library to the classpath
        addToClasspath(libraryFile);
    }

    public void addToClasspath(File file) {
        try {
            ClassPathHack.addFile(file, classLoader);
        } catch (Exception oops) {
            logger.severe("Couldn't load " + (file != null ? file.getName() : "a required library") + ", " + "this may cause problems.");
            oops.printStackTrace();
        }
    }

    public File downloadLibrary(String libraryFileName, URL libraryUrl) throws IOException {
        // Get the file
        File libraryFile = new File(libDirectory.getPath() + "/" + libraryFileName);

        // Create the streams
        BufferedInputStream in = null;
        FileOutputStream fout = null;

        try {
            // Setup the streams
            in = new BufferedInputStream(libraryUrl.openStream());
            fout = new FileOutputStream(libraryFile);

            // Create variables for loop
            final byte[] data = new byte[BYTE_SIZE];
            int count;

            // Write the data to the file
            while ((count = in.read(data, 0, BYTE_SIZE)) != -1) {
                fout.write(data, 0, count);
            }

            logger.info("Download complete.");

            // Return the file
            return libraryFile;
        } catch (final Exception oops) {
            logger.severe("Download could not complete");
            throw oops;
        } finally {
            // Close the streams
            try {
                if (in != null) {
                    in.close();
                }
                if (fout != null) {
                    fout.close();
                }
            } catch (final Exception ignored) {
            }
        }
    }
}