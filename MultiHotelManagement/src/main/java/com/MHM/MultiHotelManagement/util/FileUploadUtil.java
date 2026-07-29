package com.MHM.MultiHotelManagement.util;

import com.MHM.MultiHotelManagement.exception.BadRequestException;

import java.nio.file.Paths;
import java.util.Set;

public final class FileUploadUtil {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".avif", ".bmp", ".jfif", ".pdf"
    );

    private FileUploadUtil() {
    }

    // Strips path separators, traversal sequences, and anything outside a safe
    // charset from a user-supplied string before it's used to build a file name.
    public static String sanitizeBaseName(String input, String fallback) {
        if (input == null || input.isBlank()) {
            return fallback;
        }
        String cleaned = input.trim()
                .replaceAll("\\s+", "_")
                .replace("..", "_")
                .replaceAll("[^a-zA-Z0-9._-]", "");
        return cleaned.isBlank() ? fallback : cleaned;
    }

    // Extracts the extension from just the file's own name (ignoring any path
    // segments the client might have sent) and validates it against a whitelist.
    public static String safeExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BadRequestException("File name is required");
        }
        String nameOnly = Paths.get(originalFilename).getFileName().toString();
        int dot = nameOnly.lastIndexOf('.');
        if (dot < 0) {
            throw new BadRequestException("File must have an extension");
        }
        String ext = nameOnly.substring(dot).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new BadRequestException("Unsupported file type: " + ext);
        }
        return ext;
    }
}
