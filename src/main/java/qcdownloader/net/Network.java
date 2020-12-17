package qcdownloader.net;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import qcdownloader.Config;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Random;

public class Network {
    private static String ENDPOINT = "https://www.qualitycontrol.cc/php/services.php";
    private static String BuildURL (String function, String... params) {
        StringBuilder b = new StringBuilder(ENDPOINT);
        b.append("?function=");
        b.append(function);
        for (int i = 0; i < params.length; i++) {
            b.append("&param");
            b.append(i+1);
            b.append("=");
            b.append(params[i]);
        }
        return b.toString();
    }


    public static String GetURL (String function, String... params) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet req = new HttpGet(BuildURL(function,params));
            req.setHeader("User-Agent",Config.USER_AGENT);
            req.setHeader("Authorization","Basic "+ Base64.getEncoder().encodeToString((Config.USERNAME+":"+Config.PASSWORD).getBytes()));
            return client.execute(req, httpResponse -> EntityUtils.toString(httpResponse.getEntity()));
        } catch (IOException ex) {
            System.err.println("Error connecting to the website!");
            ex.printStackTrace();
            System.exit(-1);
        }
        return null; //this wil never run
    }
    public static void DownloadFile (String url, File location) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet req = new HttpGet(url);
            req.setHeader("User-Agent",Config.USER_AGENT);
            req.setHeader("Authorization","Basic "+ Base64.getEncoder().encodeToString((Config.USERNAME+":"+Config.PASSWORD).getBytes()));
            try (CloseableHttpResponse response = client.execute(req)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try (FileOutputStream outstream = new FileOutputStream(location)) {
                        entity.writeTo(outstream);
                    } catch (IOException ex) {
                        System.err.println("Error writing to file");
                        ex.printStackTrace();
                        System.exit(-1);
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println("Error connecting to the website!");
            ex.printStackTrace();
            System.exit(-1);
        }
    }
}
