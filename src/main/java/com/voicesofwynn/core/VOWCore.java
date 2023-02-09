package com.voicesofwynn.core;

import com.voicesofwynn.core.interfaces.IFunctionProvider;

import java.io.File;

public class VOWCore {

    private static IFunctionProvider functionProvider;
    private static boolean ready = false;

    /**
     * Initialize VOWCore
     * Run in the mod's constructor
     * @param provider Function provider which is used
     * @throws IllegalArgumentException if provider or root is null
     */
    public static void init(IFunctionProvider provider, File root) {
        if (provider == null) {
            throw new IllegalArgumentException("Provider should not be null. So why is it null?");
        }
        if (root == null) {
            throw new IllegalArgumentException("Root should not be null. So why is it null?");
        }

        functionProvider = provider;



        ready = true;
    }


    /**
     * Run this method every tick the player is in a world
     * @throws IllegalStateException if init was not run before
     */
    public static void tick() {
        if (!ready) {
            throw new IllegalStateException("tick() method was run before init()");
        }



    }

    public static IFunctionProvider getFunctionProvider() {
        return functionProvider;
    }
}