package edu.icesi.emprendimientos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadsRoot;

    public FileStorageService(@Value("${app.uploads.dir:uploads}") String dir) {
        this.uploadsRoot = Paths.get(dir).toAbsolutePath().normalize();
    }

    /**
     * Guarda un archivo en uploads/<subcarpeta>/ y devuelve la URL pública /uploads/<subcarpeta>/<uuid>.<ext>
     * Devuelve null si el archivo está vacío.
     */
    public String guardar(MultipartFile file, String subcarpeta) throws IOException {
        if (file == null || file.isEmpty()) return null;

        Path subDir = uploadsRoot.resolve(subcarpeta);
        Files.createDirectories(subDir);

        String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) ext = original.substring(dot).toLowerCase();

        String filename = UUID.randomUUID().toString() + ext;
        Path dest = subDir.resolve(filename);
        file.transferTo(dest.toFile());

        return "/uploads/" + subcarpeta + "/" + filename;
    }

    /**
     * Guarda una imagen recibida como data URL base64 (ej. "data:image/png;base64,...")
     * y devuelve la URL pública /uploads/<subcarpeta>/<uuid>.png
     */
    public String guardarBase64(String dataUrl, String subcarpeta) throws IOException {
        // Separar encabezado y datos: "data:image/png;base64,<datos>"
        String[] partes = dataUrl.split(",", 2);
        String header = partes[0];                          // "data:image/png;base64"
        byte[] bytes = Base64.getDecoder().decode(partes[1]);

        // Extraer extensión del mime type
        String ext = "jpg";
        if (header.contains("png"))       ext = "png";
        else if (header.contains("webp")) ext = "webp";

        Path subDir = uploadsRoot.resolve(subcarpeta);
        Files.createDirectories(subDir);

        String filename = UUID.randomUUID().toString() + "." + ext;
        Files.write(subDir.resolve(filename), bytes);

        return "/uploads/" + subcarpeta + "/" + filename;
    }
}
