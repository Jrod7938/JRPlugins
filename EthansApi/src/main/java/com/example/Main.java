package com.example;

import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Main {

    // This is terrible lol, ChatGPT did most of this because I hate designing anything UI.
    // But it will help users installing and that's good enough for me.
    // Kinda funny how it decided making everything static was a fun idea

    private static JFrame root;

    public static void main(String[] args) throws IOException {
        root = new JFrame("");
        root.setLayout(new BorderLayout());
        root.setSize(200, 120);
        root.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        root.setResizable(false);
        root.getContentPane().setBackground(new Color(52, 53, 65));

        JLabel titleLabel = new JLabel("Piggy Plugins Installer");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Color.WHITE);
        root.getContentPane().add(titleLabel, BorderLayout.NORTH);

        JLabel label = new JLabel("Not Installed");
        root.getContentPane().add(label, BorderLayout.CENTER);

        JButton button = new JButton("Install sideloader");
        root.getContentPane().add(button, BorderLayout.SOUTH);

        button.addActionListener((e) -> {
            try {
                install();
            } catch (IOException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
        });

        if (jarExistsInDirectory() && configFileIsValid()) {
            label.setText("Already Installed");
            label.setForeground(Color.GREEN);
            button.setEnabled(false);
        } else {
            label.setText("Not Installed");
            label.setForeground(Color.RED);
            button.setEnabled(true);
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) ((screenSize.getWidth() - root.getWidth()) / 2);
        int centerY = (int) ((screenSize.getHeight() - root.getHeight()) / 2);
        root.setLocation(centerX, centerY);

        root.setVisible(true);
    }

    private static boolean jarExistsInDirectory() {
        String directoryPath;

        if (System.getProperty("os.name").contains("Mac OS X")) {
            directoryPath = "/Applications/RuneLite.app/Contents/Resources/";
        } else {
            directoryPath = System.getProperty("user.home") + "\\AppData\\Local\\RuneLite\\";
        }

        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.getName().equals("EthanVannInstaller.jar")) {
                    return true;
                }
            }
        }

        System.out.println("Cannot find EthanVannInstaller.jar, path: " + directoryPath);
        return false;
    }

    private static boolean configFileIsValid() throws IOException {
        String file;
        if (System.getProperty("os.name").contains("Mac OS X")) {
            file = "/Applications/RuneLite.app/Contents/Resources/config.json";
        } else {
            file = System.getProperty("user.home") + "\\AppData\\Local\\RuneLite\\config.json";
        }

        File configFile = new File(file);
        if (!configFile.exists()) {
            System.out.println("Config file does not exist.");
            return false;
        }

        try (InputStream inputStream = new FileInputStream(configFile)) {
            JSONTokener tokener = new JSONTokener(inputStream);
            JSONObject object = new JSONObject(tokener);

            return object.has("mainClass") && object.getString("mainClass").equals("ca.arnah.runelite.LauncherHijack");
        }
    }

    private static void install() throws IOException, URISyntaxException {
        ReadableByteChannel readableByteChannel = Channels.newChannel(new URL("https://github.com/Ethan-Vann/Installer/releases/download/1.0/RuneLiteHijack.jar").openStream());
        FileOutputStream fileOutputStream;

        if (System.getProperty("os.name").contains("Mac OS X")) {
            fileOutputStream = new FileOutputStream("/Applications/RuneLite.app/Contents/Resources/EthanVannInstaller.jar");
        } else {
            fileOutputStream = new FileOutputStream(System.getProperty("user.home") + "\\AppData\\Local\\RuneLite\\EthanVannInstaller.jar");
        }
        fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        String file;
        if (System.getProperty("os.name").contains("Mac OS X")) {
            file = "/Applications/RuneLite.app/Contents/Resources/config.json";
        } else {
            file = System.getProperty("user.home") + "\\AppData\\Local\\RuneLite\\config.json";
        }
        InputStream inputStream = new FileInputStream(file);
        JSONTokener tokener = new JSONTokener(inputStream);
        JSONObject object = new JSONObject(tokener);
        inputStream.close();
        object.remove("mainClass");
        object.put("mainClass", "ca.arnah.runelite.LauncherHijack");
        object.remove("classPath");
        object.append("classPath", "EthanVannInstaller.jar");
        object.append("classPath", "RuneLite.jar");
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(object.toString());
        fileWriter.flush();
        fileWriter.close();
        fileOutputStream.close();

        if (jarExistsInDirectory() && configFileIsValid()) {
            String baseDirectory = System.getProperty("user.home") + "/.runelite/";
            String externalPluginsDirectory = "externalplugins";
            Path externalPluginsPath = Paths.get(baseDirectory, externalPluginsDirectory);
            Files.createDirectories(externalPluginsPath);
            Path sourcePath = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path targetPath = Paths.get(externalPluginsPath.toString(), sourcePath.getFileName().toString());
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

            JOptionPane.showMessageDialog(root, "Successfully Installed!",
                    "Ethan Vann Installer", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
}
