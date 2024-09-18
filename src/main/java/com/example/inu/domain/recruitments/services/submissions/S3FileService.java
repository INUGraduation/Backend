package com.example.inu.domain.recruitments.services.submissions;



import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;

import com.example.inu.global.s3.ErrorCode;
import com.example.inu.global.s3.S3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3FileService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public String upload(MultipartFile file){
        if(file.isEmpty() || Objects.isNull(file.getOriginalFilename())){
            throw new S3Exception(ErrorCode.EMPTY_FILE_EXCEPTION);
        }
        return this.uploadFile(file);
    }

    private String uploadFile(MultipartFile file){
        this.validateFileExtension(file.getOriginalFilename());
        try {
            return this.uploadFileToS3(file);
        }catch (IOException e){
            throw new S3Exception(ErrorCode.IO_EXCEPTION_ON_FILE_UPLOAD);
        }
    }

    private void validateFileExtension(String filename){
        int lastDotIndex = filename.lastIndexOf(".");
        if(lastDotIndex == -1){
            throw new S3Exception(ErrorCode.NO_FILE_EXTENSION);
        }

        String extention= filename.substring(lastDotIndex+1).toLowerCase();
        List<String> allowedDocumentExtensions = Arrays.asList("txt","pdf","hwp","ppyx");

        if(!allowedDocumentExtensions.contains(extention)){
            throw new S3Exception(ErrorCode.INVALID_DOCUMENT_EXTENSION);//올바른 파일 형식이 아닌경우
        }
    }

    private String uploadFileToS3(MultipartFile file) throws IOException{
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")+1).toLowerCase();

        String s3FileName= UUID.randomUUID().toString().substring(0,10) + originalFilename;

        InputStream is =file.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(this.getContentType(extension));
        metadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try{
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName,s3FileName,byteArrayInputStream,metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest);
        }catch (Exception e){
            throw new S3Exception(ErrorCode.PUT_OBJECT_EXCEPTION);
        }finally {
            byteArrayInputStream.close();
            is.close();
        }
        return amazonS3.getUrl(bucketName,s3FileName).toString();
    }

    private String getContentType(String extentsion){
        switch (extentsion){
            case "txt":
                return "text/plain";
            case "pdf":
                return "application/pdf";
            case "hwp":
                return "application/hwp";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            default:
                return "application/octet-stream";
        }
    }
}