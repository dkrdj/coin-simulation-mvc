package com.mvc.coinsimulation.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * AWS S3로의 파일 업로드 및 관련 유틸리티 클래스
 *
 * @Author 이상현
 * @Version 1.0.0
 * @See None
 */
@Component
public class S3Utils {
    private final AmazonS3Client amazonS3Client;
    private final String bucket;

    public S3Utils(AmazonS3Client amazonS3Client,
                   @Value("${cloud.aws.s3.bucket}") String bucket) {
        this.amazonS3Client = amazonS3Client;
        this.bucket = bucket;
    }

    /**
     * MultipartFile을 받아서 S3에 업로드하는 메서드
     *
     * @param multipartFile 업로드할 파일
     * @param userId        유저 PK
     * @return S3에 업로드된 파일의 URL
     * @throws IOException 파일 변환 또는 S3 업로드 중 발생한 예외
     */
    public String uploadFromFile(MultipartFile multipartFile, Long userId) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));
        return upload(uploadFile, userId);
    }

    /**
     * URL로부터 이미지를 다운로드하여 S3에 업로드하는 메서드
     *
     * @param url    다운로드할 이미지의 URL
     * @param userId 유저 PK
     * @return S3에 업로드된 파일의 URL
     * @throws IOException 파일 변환 또는 S3 업로드 중 발생한 예외
     */
    public String uploadFromUrl(String url, Long userId) throws IOException {
        URL profile = new URL(url);
        String fileName = userId + "/" + Math.random() * 1000 + "_socialprofile";
        String ext = url.substring(url.lastIndexOf('.') + 1);
        BufferedImage img = ImageIO.read(profile);
        File uploadFile = new File("upload/" + fileName + "." + ext);
        if (!uploadFile.exists()) {
            uploadFile.mkdirs();
        }
        ImageIO.write(img, ext, uploadFile);
        String uploadImageUrl = putS3(uploadFile, fileName);
        uploadFile.delete();
        return uploadImageUrl;
    }

    /**
     * 파일을 S3에 업로드하는 메서드
     *
     * @param uploadFile 업로드할 파일
     * @param userId     유저 PK
     * @return S3에 업로드된 파일의 URL
     */
    private String upload(File uploadFile, Long userId) {
        String fileName = userId + "/" + Math.random() * 1000 + "_" + LocalDate.now() + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);
        uploadFile.delete();
        return uploadImageUrl;
    }

    /**
     * S3에 파일을 업로드하는 메서드
     *
     * @param uploadFile 업로드할 파일
     * @param fileName   S3에 저장될 파일 이름
     * @return S3에 업로드된 파일의 URL
     */
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(
                CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    /**
     * MultipartFile을 File로 변환하는 메서드
     *
     * @param file 변환할 MultipartFile
     * @return Optional<File>
     * @throws IOException 변환 중 발생한 예외
     */
    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }
}
