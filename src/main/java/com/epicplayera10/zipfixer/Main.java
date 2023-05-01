package com.epicplayera10.zipfixer;

import sun.misc.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: java -jar zipfixer.jar [input zip path] [output zip path]");
            return;
        }

        String inputZip = args[0];
        String outputZip = args[1];

        System.out.println("Fixing zip...");

        File file = new File(inputZip);

        ZipFile zip = new ZipFile(file);

        FileOutputStream fos = new FileOutputStream(outputZip);
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        Enumeration<? extends ZipEntry> enumeration = zip.entries();

        while (enumeration.hasMoreElements()) {
            ZipEntry zipEntry = enumeration.nextElement();

            zipOut.putNextEntry(new ZipEntry(zipEntry.getName()));

            if (!zipEntry.isDirectory()) {
                InputStream stream = zip.getInputStream(zipEntry);

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                byte[] data;

                if (stream.available() > 100_000_000) {
                    data = new byte[1024];
                } else {
                    data = new byte[stream.available()];
                }

                int nRead;
                while ((nRead = stream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();

                zipOut.write(buffer.toByteArray());
            }

            zipOut.closeEntry();
        }

        zipOut.close();

        System.out.println("Fixed zip!");
    }
}