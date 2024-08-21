package com.infinitynet.server.services.impls;

import com.infinitynet.server.exceptions.file_storage.FileStorageException;
import com.infinitynet.server.services.LocalStorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import static com.infinitynet.server.Constants.LOCAL_STORAGE_ROOT_FOLDER;
import static com.infinitynet.server.Utils.isMedia;
import static com.infinitynet.server.exceptions.file_storage.FileStorageErrorCode.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class LocalStorageServiceImpl implements LocalStorageService {

    public LocalStorageServiceImpl() {
        createNewFolder(Paths.get(LOCAL_STORAGE_ROOT_FOLDER));
    }

    @Override
    public String storeFile(MultipartFile file, String filePath) {
        String[] fileNameParts = filePath.split("/");
        String fileName = fileNameParts[fileNameParts.length - 1];
        String pathToFile = (fileNameParts.length > 1)
                ? "/" + filePath.replace("/" + fileName, "") : "";
        Path pathToNewFolder = Paths.get(LOCAL_STORAGE_ROOT_FOLDER + pathToFile);
        createNewFolder(pathToNewFolder);

        if (file.isEmpty()) throw new FileStorageException(EMPTY_FILE, BAD_REQUEST);

        if (!isMedia(file)) throw new FileStorageException(INVALID_FILE_TYPE, BAD_REQUEST);

        float fileSizeInMegabytes = (float) file.getSize() / 1_000_000;

        if (fileSizeInMegabytes > 10.0f) throw new FileStorageException(FILE_TOO_LARGE, BAD_REQUEST);

        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        Path destinationFilePath = pathToNewFolder
                .resolve(Paths.get(fileName + "." + fileExtension))
                .normalize()
                .toAbsolutePath();

        if (!destinationFilePath.getParent().equals(pathToNewFolder.toAbsolutePath()))
            throw new FileStorageException(CAN_NOT_STORE_FILE, BAD_REQUEST);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFilePath, REPLACE_EXISTING);

        } catch (IOException e) {
            throw new FileStorageException(CAN_NOT_STORE_FILE, BAD_REQUEST);
        }

        return (pathToFile + "/" + fileName + "." + fileExtension).substring(1);
    }

    @Override
    public InputStream readFile(String filePath) {
        Path file = Paths.get(LOCAL_STORAGE_ROOT_FOLDER + "/" + filePath);
        Resource resource;
        try {
            resource = new UrlResource(file.toUri());

        } catch (MalformedURLException e) {
            throw new FileStorageException(INVALID_FILE_PROVIDED, BAD_REQUEST);
        }

        if (resource.exists() || resource.isReadable()) {
            try {
                return resource.getInputStream();

            } catch (IOException e) {
                FileStorageException fileStorageException = new FileStorageException(COULD_NOT_READ_FILE, BAD_REQUEST);
                fileStorageException.setMoreInfo(new Object[]{ filePath });
                throw fileStorageException;
            }

        } else throw new FileStorageException(FILE_NOT_FOUND, BAD_REQUEST);

    }

    private void createNewFolder(Path folderPath) {
        try {
            Files.createDirectories(folderPath);

        } catch (IOException e) {
            throw new FileStorageException(CAN_NOT_INIT_BACKUP_FOLDER, BAD_REQUEST);
        }
    }

    @Override
    public Stream<Path> loadAllFromFolder(String folderPath) {
        Path pathToFolder = Paths.get(folderPath);

        try(Stream<Path> paths = Files.walk(pathToFolder, 1)) {
            return paths.filter(path -> !path.equals(pathToFolder) && !path.toString().contains("._"))
                    .map(pathToFolder::relativize);

        } catch (IOException e) {
            throw new FileStorageException(FOLDER_NOT_FOUND, BAD_REQUEST);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        boolean isDeleted;
        try {
            isDeleted = Files.deleteIfExists(Paths.get(LOCAL_STORAGE_ROOT_FOLDER + "/" + filePath));

        } catch (IOException e) {
            throw new FileStorageException(FILE_NOT_FOUND, BAD_REQUEST);
        }

        if (!isDeleted) throw new FileStorageException(CAN_NOT_DELETE_FILE, BAD_REQUEST);
    }

    @Override
    public void deleteFolder(String folderPath) {
        Path pathToFolder = Paths.get(LOCAL_STORAGE_ROOT_FOLDER + "/" + folderPath);

        if (!Files.exists(pathToFolder)) throw new FileStorageException(FOLDER_NOT_FOUND, BAD_REQUEST);

        try(Stream<Path> paths = Files.walk(pathToFolder)) {
                paths.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);

                        } catch (IOException e) {
                            throw new FileStorageException(CAN_NOT_DELETE_FOLDER, BAD_REQUEST);
                        }
                    });

        } catch (IOException e) {
            throw new FileStorageException(CAN_NOT_DELETE_FOLDER, BAD_REQUEST);
        }
    }

}
