package com.voicesofwynn.core;

import com.voicesofwynn.core.interfaces.IFunctionProvider;

import java.io.File;

public class VOWCore {

    private static IFunctionProvider functionProvider;
    private static boolean ready = false;

    private static File rootFolder;

    /**
     * Initialize VOWCore
     * Run in the mod's constructor
     * @param provider Function provider which is used
     * @param root root of VOW folder
     */
    public static void init(IFunctionProvider provider, File root) {

        functionProvider = provider;
        rootFolder = root;


        ready = true;
    }


    /**
     * Run this method every tick the player is in a world.
     * <br><br>
     * This method will crash if run before init()
     */
    public static void tick() { // position based sounds and etc



    }

    public static File getRootFolder() {
        return rootFolder;
    }

    public static IFunctionProvider getFunctionProvider() {
        return functionProvider;
    }
}