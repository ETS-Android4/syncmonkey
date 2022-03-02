package com.chesapeaketechnology.syncmonkey.fileupload;

import android.util.Log;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Facilitates uploading of files to Azure Blob storage by using a SAS URL to an Azure Blob container
 * with Write and List permissions. The list permission is needed to check if local file name exists
 * on the blob storage account and if so the upload is skipped.
 *
 * @since 1.2.0
 */
public class AzureBlob
{
    private static final String LOG_TAG = AzureBlob.class.getSimpleName();
    private CloudBlobContainer container = null;

    public AzureBlob(String sasUrl)
    {
        try
        {
            container = new CloudBlobContainer(URI.create(sasUrl));
        } catch (Throwable t)
        {
            Log.e(LOG_TAG, "Could not access cloud account.", t);
        }
    }

    /**
     * Uploads the provided file or folder to azure blob storage container.
     *
     * @param fileOrFolderToUpload The file(s) to upload to Azure Blob storage
     * @param destinationPath The Blob Storage path to store the files (i.e. blobs) under
     */
    public void uploadFile(String fileOrFolderToUpload, String destinationPath)
    {
        try (Stream<Path> walk = Files.walk(Paths.get(fileOrFolderToUpload)))
        {
            walk.filter(Files::isRegularFile).forEach(file -> {
                try
                {
                    boolean blobAlreadyUploaded = false;
                    CloudBlockBlob blob = container.getBlockBlobReference(destinationPath + file.getFileName());
                    for (ListBlobItem blobItem : container.listBlobs(destinationPath))
                    {
                        // If the item is a blob, not a virtual directory
                        if (blobItem instanceof CloudBlockBlob)
                        {
                            CloudBlockBlob retrievedBlob = (CloudBlockBlob) blobItem;
                            if (retrievedBlob.getName().equals(destinationPath.substring(1) + file.getFileName()))
                            {
                                if (Log.isLoggable(LOG_TAG, Log.INFO))
                                {
                                    Log.i(LOG_TAG, "Blob " + retrievedBlob.getName() + " already present in blob storage - skipping upload.");
                                }
                                blobAlreadyUploaded = true;
                            }
                        }
                    }
                    if (!blobAlreadyUploaded)
                    {
                        blob.upload(new FileInputStream(file.toFile()), file.toFile().length());
                    }
                } catch (URISyntaxException e)
                {
                    Log.e(LOG_TAG, "Bad URI.", e);
                } catch (StorageException e)
                {
                    Log.e(LOG_TAG, "Could not access blob storage.", e);
                } catch (FileNotFoundException e)
                {
                    Log.e(LOG_TAG, "File could not be uploaded", e);
                } catch (IOException e)
                {
                    Log.e(LOG_TAG, "Error while processing file " + file.getFileName(), e);
                }
            });
        } catch (IOException e)
        {
            Log.e(LOG_TAG, "Upload to Azure blob storage failed.", e);
        }
    }
}
