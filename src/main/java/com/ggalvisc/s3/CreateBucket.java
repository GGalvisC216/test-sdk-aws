package com.ggalvisc.s3;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

public class CreateBucket {

    public static Boolean checkBucketExisting(S3Client s3, String bucketName) {

        boolean exists = true;

        try {

            HeadBucketRequest request = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            HeadBucketResponse response = s3.headBucket(request);

            if (response.sdkHttpResponse().statusCode() == HttpStatusCode.OK) {
                System.out.format("Bucket '%s' already exists.\n", bucketName);
            }

        } catch (AwsServiceException awsEx) {
            switch (awsEx.statusCode()) {
                case HttpStatusCode.NOT_FOUND:
                    System.out.format("Bucket '%s' doesn't exist.\n", bucketName);
                    exists = false;
                    break;
                case HttpStatusCode.BAD_REQUEST:
                    System.out.println("You are trying to access a bucket from a different region");
                    break;
                case HttpStatusCode.FORBIDDEN:
                    System.out.println("You don't have permission to access the bucket");
                    break;
            }

        }
        return exists;
    }

    public static void createBucket (S3Client s3, String bucketName) {

        System.out.format("\nCreating bucket '%s'\n\n", bucketName);

        try {
            S3Waiter waiter = s3.waiter();

            CreateBucketRequest request = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3.createBucket(request);

            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            System.out.format("Waiting for bucket creation: '%s'\n", bucketName);
            WaiterResponse<HeadBucketResponse> waiterResponse = waiter.waitUntilBucketExists(headBucketRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);
            System.out.format("The bucket '%s' was created.\n", bucketName);
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

}
