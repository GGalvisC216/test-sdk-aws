package com.ggalvisc;

import com.ggalvisc.s3.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class Main {

    static Input s3Config = new Input();

    public static void main(String[] args) throws Exception {
        testS3Operations();
    }

    public static void testS3Operations() throws Exception {
        String bucketName = s3Config.getBucketName();
        Region region = Region.of(s3Config.getRegion());

        S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.builder().profileName("developer").build())
                .build();

        if (!CreateBucket.checkBucketExisting(s3, bucketName)) {
            CreateBucket.createBucket(s3, bucketName);
        }

        String objectKey = s3Config.getObjectName();
        String filePath = s3Config.getFile();

        UploadObject.putS3Object(s3, bucketName, objectKey, filePath);

        String newObjectKey = s3Config.getNewObjectName();

        ProcessObject.convertS3Object(s3, bucketName, objectKey, newObjectKey);

        String path = "html/";
        String policy = "policy.json";

        HostS3Website.setPublicAccessPolicy(s3, bucketName, path + policy);

        String index = "index.html";
        String error = "error.html";
        String file404 = "404.png";
        String header = "header.png";
        String style = "style.css";

        UploadObject.putS3Object(s3, bucketName, index, path + index);
        UploadObject.putS3Object(s3, bucketName, error, path + error);
        UploadObject.putS3Object(s3, bucketName, file404, path + file404);
        UploadObject.putS3Object(s3, bucketName, header, path + header);
        UploadObject.putS3Object(s3, bucketName, style, path + style);

        HostS3Website.setWebsiteConfig(s3, region.toString(), bucketName, index, error);

        s3.close();
    }
}