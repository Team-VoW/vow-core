package com.voicesofwynn.core.loadmanager.subtypes;

import com.voicesofwynn.core.VOWCore;
import com.voicesofwynn.core.utils.ByteUtils;
import com.voicesofwynn.core.wrappers.VOWLocation;
import com.voicesofwynn.core.wrappers.VOWLocationProvider;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class LocationSubType {

    public static void writeLocation(FileOutputStream out, Object part) {

    }

    /**
     * in case of name being null it will instead write player position based location
     */
    public static void writeNpcNameLocation(FileOutputStream out, String name) throws IOException {
        if (name ==  null) {
            out.write(2);
            return;
        }

        out.write(1);
        out.write(ByteUtils.encodeString(name));
    }

    public static VOWLocationProvider readLocation(FileInputStream in) throws IOException {

        // https://github.com/Team-VoW/vow-core/wiki/Types-in-code#location
        switch (ByteUtils.readByte(in)) {
            case 0:
                float x = ByteUtils.readFloat(in);
                float y = ByteUtils.readFloat(in);
                float z = ByteUtils.readFloat(in);
                return () -> new VOWLocation(x, y, z);
            case 1:
                String name = ByteUtils.readString(in);
                return () -> VOWCore.getFunctionProvider().getNpcLocationFromName(name);
            case 2:
                return () -> VOWCore.getFunctionProvider().getPlayerLocation();
        }

        return null;
    }

}
