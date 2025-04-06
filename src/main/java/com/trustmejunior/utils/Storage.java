package com.trustmejunior.utils;

/**
 * @author TrustMeJunior
 */

import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

public class Storage {
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static String storageUrl;
    private static String secretKey;

    private static final long MAX_FILE_SIZE_MB = 50;
    private static final long MAX_FILE_SIZE = MAX_FILE_SIZE_MB * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png");

    static {
        try {
            Properties props = new Properties();
            FileInputStream in = new FileInputStream("src/main/resources/com/trustmejunior/config/db.properties");
            props.load(in);
            in.close();

            storageUrl = props.getProperty("storage.url");
            secretKey = props.getProperty("storage.secretKey");
        } catch (IOException e) {
            System.err.println("Failed to load properties: " + e.getMessage());
        }
    }

    private Storage() {
        // Private constructor to prevent instantiation
    }

    private static void validateFile(File file) throws Exception {
        if (!file.exists())
            throw new Exception("File does not exist");
        if (file.length() > MAX_FILE_SIZE)
            throw new Exception("File size exceeds maximum limit of 50MB");
        if (!ALLOWED_EXTENSIONS.contains(getFileExtension(file.getName()).toLowerCase()))
            throw new Exception("File type not allowed. Allowed types: " + String.join(", ", ALLOWED_EXTENSIONS));
    }

    private static String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        return i > 0 ? fileName.substring(i + 1) : "";
    }

    private static String buildPath(String bucket, String path, String fileName, boolean isPublic) {
        String sanitizedPath = (path == null || path.trim().isEmpty()) ? ""
                : URLEncoder.encode(path.trim(), StandardCharsets.UTF_8) + "/";
        return String.format("%s/storage/v1/object%s/%s/%s%s",
                storageUrl,
                isPublic ? "/public" : "",
                URLEncoder.encode(bucket, StandardCharsets.UTF_8),
                sanitizedPath,
                URLEncoder.encode(fileName, StandardCharsets.UTF_8));
    }

    public static String uploadFile(File file, String bucket, String path) throws Exception {
        checkInitialization();
        validateFile(file);
        String fileName = UUID.randomUUID() + "-" + file.getName();
        String fullPath = buildPath(bucket, path, fileName, false);

        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(fullPath))
                        .header("Authorization", "Bearer " + secretKey)
                        .header("Content-Type", Files.probeContentType(file.toPath()))
                        .POST(HttpRequest.BodyPublishers.ofFile(file.toPath()))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
            throw new Exception("Failed to upload file: " + response.body());

        return fullPath.replace("/object/", "/object/public/");
    }

    public static HttpResponse<byte[]> downloadFile(String bucket, String path, String fileName) throws Exception {
        checkInitialization();
        String fullPath = buildPath(bucket, path, fileName, true);

        HttpResponse<byte[]> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(fullPath))
                        .header("Authorization", "Bearer " + secretKey)
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200)
            throw new Exception("Failed to download file: " + new String(response.body()));
        return response;
    }

    public static void deleteFile(String bucket, String path, String fileName) throws Exception {
        deleteFile(buildPath(bucket, path, fileName, false));
    }

    public static void deleteFile(String url) throws Exception {
        checkInitialization();
        String fullPath = url.replace("/object/public/", "/object/");

        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(fullPath))
                        .header("Authorization", "Bearer " + secretKey)
                        .DELETE()
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
            throw new Exception("Failed to delete file: " + response.body());
    }

    private static void checkInitialization() {
        if (storageUrl == null || secretKey == null)
            throw new IllegalStateException("Storage must be initialized with storageUrl and secretKey first");
    }
}