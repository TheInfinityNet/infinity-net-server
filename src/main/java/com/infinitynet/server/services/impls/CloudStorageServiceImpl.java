package com.infinitynet.server.services.impls;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.common.collect.Iterables;
import com.google.firebase.cloud.StorageClient;
import com.infinitynet.server.exceptions.file_storage.FileStorageException;
import com.infinitynet.server.services.CloudStorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.infinitynet.server.Utils.isMedia;
import static com.infinitynet.server.exceptions.file_storage.FileStorageErrorCode.*;
import static java.util.concurrent.TimeUnit.HOURS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class CloudStorageServiceImpl implements CloudStorageService {


    @Override
    public void storeFile(MultipartFile file, String filePath) {
        if (file.isEmpty()) throw new FileStorageException(EMPTY_FILE, BAD_REQUEST);

        if (!isMedia(file)) throw new FileStorageException(INVALID_FILE_TYPE, BAD_REQUEST);

        float fileSizeInMegabytes = (float) file.getSize() / 1_000_000;

        if (fileSizeInMegabytes > 10.0f) throw new FileStorageException(FILE_TOO_LARGE, BAD_REQUEST);

        Bucket bucket = StorageClient.getInstance().bucket();
        InputStream fileStream;
        try {
            fileStream = file.getInputStream();

        } catch (IOException e) {
            throw new FileStorageException(CAN_NOT_STORE_FILE, BAD_REQUEST);
        }

        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        bucket.create(filePath + "." + fileExtension, fileStream, file.getContentType());
    }

    @Override
    public void storeFile(InputStream file, String contentType, String filePath) {
        Bucket bucket = StorageClient.getInstance().bucket();
        bucket.create(filePath, file, contentType);
    }

    @Override
    public String getFileUrl(String filePath) {
        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.get(filePath);

        if (blob == null) throw new FileStorageException(COULD_NOT_READ_FILE, BAD_REQUEST);

        URL url = blob.signUrl(1, HOURS);

        return url.toString();
    }

    @Override
    public List<String> loadAllFromFolder(String folderPath) {
        Bucket bucket = StorageClient.getInstance().bucket();
        // Liệt kê các tệp trong thư mục
        Iterable<Blob> blobs = bucket.list(Storage.BlobListOption.prefix(folderPath)).iterateAll();

        // Tạo một danh sách các URL cho các tệp
        List<String> urls = new ArrayList<>();
        for (Blob blob : blobs) {
            // Lấy URI của blob
            String url = blob.signUrl(1, HOURS).toString();
            urls.add(url);
        }

        return urls;
    }

    @Override
    public void deleteFile(String filePath) {
        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.get(filePath);

        if (blob != null) blob.delete();

        else throw new FileStorageException(FILE_NOT_FOUND, BAD_REQUEST);
    }

    @Override
    public void deleteFolder(String folderPath) {
        Bucket bucket = StorageClient.getInstance().bucket();
        Page<Blob> blobs = bucket.list(Storage.BlobListOption.prefix(folderPath + "/"));

        if (!blobs.iterateAll().iterator().hasNext()) throw new FileStorageException(FOLDER_NOT_FOUND, BAD_REQUEST);

        int size = Iterables.size(blobs.getValues());
        int count = 0;

        for (Blob blob : blobs.iterateAll()) {
            blob.delete();
            count++;
        }

        if (count != size) throw new FileStorageException(CAN_NOT_DELETE_FOLDER, BAD_REQUEST);
    }

}
