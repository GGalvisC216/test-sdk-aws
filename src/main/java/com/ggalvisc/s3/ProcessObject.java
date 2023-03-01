package com.ggalvisc.s3;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ProcessObject {

    public static void convertS3Object (S3Client s3, String bucketName, String objectKey, String newObjectKey) {

        try {

            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            System.out.format("\nDownloading file '%s' from bucket '%s'\n",objectKey,bucketName);

            ResponseBytes<GetObjectResponse> responseBytes = s3.getObjectAsBytes(request);
            byte[] data = responseBytes.asByteArray();

            File localFile = new File("localFile.csv");
            OutputStream os = new FileOutputStream(localFile);
            os.write(data);
            os.close();

            System.out.format("\nObject '%s' downloaded from bucket '%s' is written to: %s\n", objectKey, bucketName, localFile.getAbsolutePath());

            System.out.format("\nConverting %s to json format...\n", objectKey);
            File jsonFile = new File(newObjectKey);

            try {
                CsvSchema csv = CsvSchema.emptySchema().withHeader();
                CsvMapper csvMapper = new CsvMapper();
                MappingIterator<Map<?,?>> mappingIterator = csvMapper.reader().forType(Map.class).with(csv).readValues(localFile);
                List<Map<?,?>> list = mappingIterator.readAll();
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(jsonFile, list);
                System.out.format("\nJSON File is written to: %s\n", jsonFile.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                UploadObject.putS3Object(s3, bucketName, newObjectKey, newObjectKey);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }

}
