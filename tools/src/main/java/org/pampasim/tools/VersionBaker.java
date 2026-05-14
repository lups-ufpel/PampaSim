package org.pampasim.tools;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionBaker {
    private static Path destFile = Path.of("sim/target/generated-sources/VersionBaker/org/pampasim/VersionInfo.java");
    private static String destPackage = "org.pampasim";
    public static void main(String[] args) throws IOException {
        String versionStr = null;
        try {
            File pomFile = new File("pom.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(pomFile);
            doc.getDocumentElement().normalize();

            NodeList versions = doc.getElementsByTagName("version");
            versionStr = versions.item(0).getTextContent();
        } catch (Exception e) {
            throw new RuntimeException("Error parsing pom.xml to get version info", e);
        }

        System.out.println(versionStr);
        assert versionStr != null && !versionStr.isEmpty();
        Pattern versionPattern = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+).*");
        Matcher versionMatcher = versionPattern.matcher(versionStr);
        boolean matches = versionMatcher.find();
        assert matches;
        int major = Integer.parseInt(versionMatcher.group(1));
        int minor = Integer.parseInt(versionMatcher.group(2));
        int patch = Integer.parseInt(versionMatcher.group(3));

        System.out.println("Baking version " + major + "." + minor + "." + patch);
        Files.createDirectories(destFile.getParent());
        Files.writeString(destFile, bakedSource(major, minor, patch));
    }
    private static String bakedSource(int major, int minor, int patch) {
        return "package " + destPackage +
                "; import lombok.Getter; public class VersionInfo { " +
                "@Getter " +
                "private static int major = " +
                major +
                ";" +
                "@Getter " +
                "private static int minor = " +
                minor +
                ";" +
                "@Getter " +
                "private static int patch = " +
                patch +
                ";}";
    }
}
