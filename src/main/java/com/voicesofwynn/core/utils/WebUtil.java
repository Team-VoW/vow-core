package com.voicesofwynn.core.utils;

import com.voicesofwynn.core.sourcemanager.Sources;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ConcurrentModificationException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class WebUtil {

    public static final int THREAD_AMOUNT = 8;
    private final ThreadPoolExecutor es;

    public WebUtil() {
        es = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_AMOUNT);
    }


    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        final int bufLen = 1024;
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
                outputStream.write(buf, 0, readLen);

            return outputStream.toByteArray();
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null) inputStream.close();
            else try {
                inputStream.close();
            } catch (IOException e) {
                exception.addSuppressed(e);
            }
        }
    }

    public static InputStream getHttpStream(String address, Sources sources) throws IOException {
        address = address.replace(" ", "%20"); //Replace spaces in the filename
        InputStream stream = null;
        try {
            int i = 0;
            for (String source : sources.getSources()) {
                try {
                    URL url = new URL(source + address);
                    
                    HttpsURLConnection con = null;

                    con = (HttpsURLConnection) url.openConnection();
                    

                    con.setConnectTimeout(20000);
                    

                    // sort out redirection response, send a second request to the new location
                    String redirect = con.getHeaderField("Location");
                    if (redirect != null) {
                        con = (HttpsURLConnection) new URL(redirect).openConnection();
                        con.setConnectTimeout(2000);
                    }

                    

                    String re = con.getResponseMessage();
                    if (re.equals("OK")) {
                        stream = con.getInputStream();

                        if (i != 0) {
                            sources.moveUp(source);
                        }
                    } else {
                        throw new IOException();
                    }
                    break; // no need to try other sources
                } catch (IOException e) {
                    e.printStackTrace();
                }
                i++;
            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
            stream = getHttpStream(address, sources);
        }

        return stream;
    }

    public static boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.getDesktop();
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public int finished() {
        return (int) es.getCompletedTaskCount();
    }

    public void getRemoteFile(String path, remoteFileGot rfg, Sources sources) {
        es.submit(
                () -> {
                    try {
                        InputStream s = getHttpStream(path, sources);
                        rfg.run(s);
                    } catch (Exception e) {
                        rfg.run(null);
                    }
                }
        );
    }

    public interface remoteFileGot {
        void run(InputStream contents);
    }

    public static class remoteJar {
        public String recommendedFileName_, id_;
        public remoteJar(String recommendedFileName, String id) {
            this.recommendedFileName_ = recommendedFileName;
            this.id_ = id;
        }

        public String recommendedFileName() {
            return recommendedFileName_;
        }

        public String id() {
            return id_;
        }
    }

}
