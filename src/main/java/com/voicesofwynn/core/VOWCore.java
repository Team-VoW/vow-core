package com.voicesofwynn.core;

import com.voicesofwynn.core.interfaces.IFunctionProvider;
import com.voicesofwynn.core.sourcemanager.SourceManager;

import java.io.File;

public class VOWCore {

    private static IFunctionProvider functionProvider;
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

        new SourceManager();

    }

    public static File getRootFolder() {
        return rootFolder;
    }

    public static IFunctionProvider getFunctionProvider() {
        return functionProvider;
    }
}