package com.ggalvisc.s3;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class HostS3Website {

    public static String getPolicyFromFile (String policy) {
        StringBuilder text = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get(policy), StandardCharsets.UTF_8);
            for (String line: lines) {
                text.append(line);
            }
        } catch (IOException e) {
            System.out.format("Problem reading file: '%s' ", policy);
            System.exit(1);
        }

        try {
            final JsonParser parser = new ObjectMapper().getFactory().createParser(text.toString());
            while (parser.nextToken() != null) {}
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return text.toString();
    }

    public static void setPublicAccessPolicy(S3Client s3, String bucket, String policy) {

        try {
            String policyText = getPolicyFromFile(policy);

            System.out.println("Setting policy");
            System.out.println("--------------");
            System.out.println(policyText);
            System.out.println("--------------");
            System.out.format("On Amazon bucket '%s'\n",bucket);

            PutBucketPolicyRequest request = PutBucketPolicyRequest.builder()
                    .bucket(bucket)
                    .policy(policyText)
                    .build();

            s3.putBucketPolicy(request);
            System.out.println("Policy added successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public static void setWebsiteConfig (S3Client s3, String region, String bucketName, String indexPage, String errorPage) {

        try {

            WebsiteConfiguration websiteConfiguration = WebsiteConfiguration.builder()
                    .indexDocument(IndexDocument.builder().suffix(indexPage).build())
                    .errorDocument(ErrorDocument.builder().key(errorPage).build())
                    .build();

            PutBucketWebsiteRequest request = PutBucketWebsiteRequest.builder()
                    .bucket(bucketName)
                    .websiteConfiguration(websiteConfiguration)
                    .build();

            s3.putBucketWebsite(request);

            System.out.println("Website Configuration: ");

            GetBucketWebsiteRequest bucketWebsiteRequest = GetBucketWebsiteRequest.builder()
                    .bucket(bucketName)
                    .build();

            GetBucketWebsiteResponse bucketWebsiteResponse = s3.getBucketWebsite(bucketWebsiteRequest);

            System.out.format("Index page: %s\n", bucketWebsiteResponse.indexDocument());
            System.out.format("Error page: %s\n", bucketWebsiteResponse.errorDocument());
            System.out.format("Url to access your S3 website: \n\n");
            System.out.format("\thttp://%s.s3-website-%s.amazonaws.com",bucketName, region);

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

    }

}
