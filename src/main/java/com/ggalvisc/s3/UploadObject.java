package com.ggalvisc.s3;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class UploadObject {

    public static void putS3Object(S3Client s3, String bucketName, String objectKey, String filePath) {

        try {

            Map<String, String> metadata = new HashMap<>();
            metadata.put("x-amz-meta-myVal2", "testing-uploadS3Object");

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .metadata(metadata)
                    .build();

            System.out.format("\nUploading file from '%s' \n", new File(filePath).getAbsolutePath());

            PutObjectResponse response = s3.putObject(request,
                    RequestBody.fromFile(Paths.get(filePath)));

            System.out.format("\nUpload completed.\n Tag: %s\n", response.eTag());
        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }

}
