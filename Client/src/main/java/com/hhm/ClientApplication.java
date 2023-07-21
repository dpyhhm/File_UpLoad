package com.hhm;

import com.hhm.fileUtil.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class ClientApplication {
    @Value("${post.url}")
    private String URL;

    @Value("${file.UpLoadFileName}")
    private String UPLOAD_FILE_NAME;  // 文件夹路径

    @Value("${client.id}")
    private String clientId;

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class,args);
    }

    @Scheduled(fixedDelayString = "${upload.time}") // 每完整执行一次后隔9秒执行一次定时任务
    public void uploadFilesScheduledTask() {
        String folderPath = UPLOAD_FILE_NAME; // 文件夹路径

        File folder = new File(folderPath);
        //if (!folder.exists() || !folder.isDirectory()||!folder.isFile()) {
        if (!folder.exists()){
            System.err.println("指定的文件路径不存在");
            return;
        }

        List<File> allFilesInFolder = FileUtil.getAllFilesInFolder(folderPath);
        for (File file : allFilesInFolder) {
            System.out.println(file.getAbsolutePath());
            try {
                FileUtil.uploadFile(URL, file.getAbsolutePath(), clientId, UPLOAD_FILE_NAME);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
