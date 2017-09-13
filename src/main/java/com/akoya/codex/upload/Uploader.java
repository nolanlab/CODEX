/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.upload;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.file.CloudFile;
import com.microsoft.azure.storage.file.CloudFileClient;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.CloudFileShare;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Nikolay Samusik
 * 
 */
public class Uploader {

    AtomicInteger ai;
    ExecutorService es;
    URL url;

    public Uploader(URL URL, int numThreads) {
        BlockingQueue<Runnable> q = new LinkedBlockingQueue<>();
        this.url = URL;
        es = new ThreadPoolExecutor(numThreads, numThreads, 1, TimeUnit.SECONDS, q);
        ai = new AtomicInteger(0);
        // Define the path to a local file.

    }

    public String uploadFilesMultith(File file, FileShareAccess fsa, int region, int tile, String token, int attempt) {
        try {
            final String acctName = fsa.fileShare.split("[/\\.]+")[1];
            final String shareRef = fsa.fileShare.split("[/]+")[2];
            final String storageConnectionString
                    = "DefaultEndpointsProtocol=https;"
                    + "AccountName=" + acctName + ";"
                    + "AccountKey=" + fsa.key;
            frmMain.log("Scheduling upload:" + file.getName());
            es.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
                        CloudFileClient fileClient = storageAccount.createCloudFileClient();
                        CloudFileShare share = fileClient.getShareReference(shareRef);
                        CloudFileDirectory rootDir = share.getRootDirectoryReference();
                        CloudFile cloudFile = rootDir.getFileReference(file.getName());
                        int idx = ai.addAndGet(1);
                        frmMain.log("uploading file #" + idx + ": " + file.getName());
                        cloudFile.uploadFromFile(file.getAbsolutePath());
                        frmMain.log("file uploaded: #" + idx + ": " + file.getName());
                        Uploader.this.notifyUploadComplete(fsa, file.getName(), token, 1);
                    } catch (Exception e) {
                        logger.showException(e);
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            frmMain.log("Upload failed: " + file.getAbsolutePath() + ", trying attempt #" + attempt);
            return uploadFilesMultith(file, fsa, region, tile, token, attempt++);
        }
        return "Done";
    }

    public static void main(String[] args) {

    }

    public String sendAuthRequest(String username, String password) throws Exception {
        String httpsURL = url + (url.toString().endsWith("/")?"":"/") + "/Token";

        String query
                = "grant_type=" + URLEncoder.encode("password", "UTF-8")
                + "&password=" + URLEncoder.encode(password, "UTF-8")
                + "&username=" + URLEncoder.encode(username, "UTF-8");

        URL myurl = new URL(httpsURL);
        HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
        con.setRequestMethod("POST");

        con.setRequestProperty("Content-length", String.valueOf(query.length()));
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
        con.setDoOutput(true);
        con.setDoInput(true);

        DataOutputStream output = new DataOutputStream(con.getOutputStream());

        output.writeBytes(query);

        output.close();

        DataInputStream input = new DataInputStream(con.getInputStream());
        JsonFactory factory = new JsonFactory();
        // configure, if necessary:
        factory.enable(JsonParser.Feature.ALLOW_COMMENTS);

        JsonParser jp = factory.createParser(input);
        String accessToken = null;
        while (jp.nextToken() != null) {
            if (jp.getCurrentName() == null) {
                continue;
            }
            if (jp.getCurrentName().equals("access_token")) {
                accessToken = jp.getText();
            }
        }
        frmMain.log("access_token:" + accessToken);
        frmMain.log("Resp Code:" + con.getResponseCode());
        frmMain.log("Resp Message:" + con.getResponseMessage());
        return accessToken;
    }

    public class FileShareAccess {

        int id;
        String fileShare;
        String key;

        public FileShareAccess(int id, String fileShare, String key) {
            this.id = id;
            this.fileShare = fileShare;
            this.key = key;
        }

    }

    public void notifyUploadComplete(FileShareAccess fsa, String filename, String token, int i) throws Exception {
        try {
            String httpsURL = url + (url.toString().endsWith("/")?"":"/") + "/api/uploads";
            String query = "{\"id\":" + fsa.id + ",\"TifName\":\"" + filename + "\"}";
            URL myurl = new URL(httpsURL);
            HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-length", String.valueOf(query.length()));
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
            con.setRequestProperty("authorization", "Bearer " + token);
            con.setDoOutput(true);
            con.setDoInput(true);
            frmMain.log("Notifying upload complete: " + query);
            DataOutputStream output = new DataOutputStream(con.getOutputStream());

            output.writeBytes(query);
            output.close();
            frmMain.log("Resp Code:" + con.getResponseCode());
            frmMain.log("Resp Message:" + con.getResponseMessage());
        } catch (UnknownHostException e) {
            frmMain.log("Notification failed: " + filename + ", trying attempt #" + i);
            notifyUploadComplete(fsa, filename, token, (i++));
        }
    }

    public FileShareAccess sendExpCreateRequest(String token, String expJson) throws Exception {
        String httpsURL = url + (url.toString().endsWith("/")?"":"/") +"api/experiments";
        
        String query = expJson;

        URL myurl = new URL(httpsURL);
        HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
        con.setRequestMethod("POST");

        con.setRequestProperty("Content-length", String.valueOf(query.length()));
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
        con.setRequestProperty("authorization", "Bearer " + token);
        con.setDoOutput(true);
        con.setDoInput(true);

        DataOutputStream output = new DataOutputStream(con.getOutputStream());

        output.writeBytes(query);

        output.close();

        DataInputStream input = new DataInputStream(con.getInputStream());
        JsonFactory factory = new JsonFactory();
        // configure, if necessary:
        factory.enable(JsonParser.Feature.ALLOW_COMMENTS);

        JsonParser jp = factory.createParser(input);
        String id = "null";
        String fileShare = "null";
        String key = "null";
        while (jp.nextToken() != null) {
            if (jp.getCurrentName() == null) {
                continue;
            }
            if (jp.getCurrentName().equals("id")) {
                id = jp.getText();
            }
            if (jp.getCurrentName().equals("fileShare")) {
                fileShare = jp.getText();
            }
            if (jp.getCurrentName().equals("key")) {
                key = jp.getText();
            }
        }
        frmMain.log("Resp Code:" + con.getResponseCode());
        frmMain.log("Resp Message:" + con.getResponseMessage());
        frmMain.log("");
        frmMain.log("id:" + id);
        frmMain.log("fileShare:" + fileShare);
        frmMain.log("Key:" + key);

        return new FileShareAccess(Integer.parseInt(id), fileShare, key);
    }
}
