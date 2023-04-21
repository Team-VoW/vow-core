package com.voicesofwynn.core.sourcemanager;

public class Sources {
    private final String[] sources;

    public Sources (String[] sources) {
        for (int i = 0 ; i < sources.length ; i++) {
            if (!sources[i].endsWith("/")) {
                sources[i] += "/";
            }
        }
        this.sources = sources;
    }

    public String[] getSources () {
        return sources.clone();
    }

    public void moveUp(String source) {
        int i = 0;
        for (String src : sources) {
            if (src.equals(source)) {
                break;
            }
            i++;
        }
        sources[i] = sources[0];
        sources[0] = source;

    }


}
