package com.voicesofwynn.core.generator;

import com.voicesofwynn.core.loadmanager.LoadManager;
import com.voicesofwynn.core.utils.ByteUtils;
import com.voicesofwynn.core.utils.VOWLog;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.zip.CRC32;

public class Generator {

    public static void main(String[] args) throws IOException {
        File file = new File(args.length > 0 ? args[0] : ".");
        generate(file, args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[] {});
    }
    /**
     * The generate function
     * <br><br>
     * Will generate output in the 'out folder' which can be used as a preset source. <p>
     * Requires 'presets.yml' in the in folder.
     *
     * @param base base project folder
     * @param in the files/folders to generate (if non, then it will just generate everything) <br>
     * in general in parameter should only be specified when you are working with large amount of configs and want to check your config file
     */
    public static void generate(File base, String[] in) throws IOException {
        File settings = new File(base, "settings.yml");

        LoadManager loadManager = new LoadManager();

        Map<String, Object> settingsInfo;
        if (!settings.isFile()) {
            VOWLog.error("Unable to find settings.yml, stopping.");
            return;
        } else {
            Yaml yaml = new Yaml();
            settingsInfo = yaml.load(Files.newInputStream(settings.toPath()));
            settingsInfo.putIfAbsent("in", "src");
            settingsInfo.putIfAbsent("out", "vow-core-out");
        }

        if (in == null || in.length == 0) {
            in = new String[] {"."};
        } else {
            VOWLog.warn("Make sure to not to use the current build for production.");
        }

        File baseOutForFile = new File(base, (String)settingsInfo.get("out"));
        baseOutForFile = new File(baseOutForFile, "src");
        File baseInForFile = new File(base, (String)settingsInfo.get("in"));

        if (baseOutForFile.exists()) {
            recursivelyDelete(baseOutForFile);
        }

        for (String filePath : in) {
            File file = new File(baseInForFile, filePath);

            if (file.isDirectory()) {
                buildDir(file, new File(baseOutForFile, filePath), baseInForFile);
            } else if (file.exists()) {
                loadManager.build(file,
                        new File(baseOutForFile, filePath.replace(".yml", "")), baseInForFile);
            } else {
                VOWLog.warn("File " + filePath + " not found");
            }
        }
        File baseOutForLists = new File(base, (String)settingsInfo.get("out"));
        baseOutForLists = new File(baseOutForLists, "lists");
        createDirLists(baseOutForFile, baseOutForLists, "base");

    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public static long createDirLists(File folder, File out, String path) throws IOException {

        File list = new File(out, path);
        if (list.exists()) {
            list.delete();
        }
        list.getParentFile().mkdirs();
        list.createNewFile();
        FileOutputStream output = new FileOutputStream(list);

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    CRC32 crc = new CRC32();
                    crc.update(Files.readAllBytes(file.toPath()));

                    output.write(ByteUtils.encodeString(file.getName()));
                    output.write(0);
                    output.write(ByteUtils.encodeLong(crc.getValue()));
                } else if (file.isDirectory()) {
                    output.write(ByteUtils.encodeString(file.getName()));
                    output.write(1);
                    output.write(ByteUtils.encodeLong(createDirLists(file, out, path + "$" + file.getName())));
                }
            }
        }
        output.close();

        CRC32 crc = new CRC32();
        crc.update(Files.readAllBytes(list.toPath()));
        return crc.getValue();
    }

    public static void buildDir(File folder, File outBase, File rootIn) throws IOException {
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                buildDir(file, new File(outBase, file.getName()), rootIn);
            } else if (file.exists() && file.getName().endsWith(".yml")) {
                LoadManager.getInstance().build(file,
                        new File(outBase, file.getName().replace(".yml", ".vow-config")), rootIn);
            } else {
                File out = new File(outBase, file.getName());
                out.getParentFile().mkdirs();
                Files.copy(file.toPath(), out.toPath());
            }
        }
    }
    public static void recursivelyDelete(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                recursivelyDelete(f);
            }
        }
        dir.delete();
    }
}