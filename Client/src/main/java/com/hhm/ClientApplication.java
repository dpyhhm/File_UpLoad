package com.hhm;

import com.hhm.Util.DateUtil;
import com.hhm.Util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.unit.DataUnit;

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

    @Value("${file.UpLoadCacheFile}")
    private String UpLoadCacheFile;

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

        List<File> allFilesInFolderReName = FileUtil.getAllFilesInFolder(folderPath);
        for (File file : allFilesInFolderReName) {
            System.out.println(file.getAbsolutePath());
            String newPath = file.getAbsoluteFile().toString().replace(UPLOAD_FILE_NAME, UpLoadCacheFile);
            String subFile = newPath.substring(0, newPath.indexOf(file.getName()));
            File subfile = new File(subFile);
            if (!subfile.exists()){
                subfile.mkdirs();
            }
            System.out.println(newPath);
            boolean b = file.renameTo(new File(newPath));
            if (!b){
                System.out.println(file.getAbsolutePath() + "文件被占用，本次无法上传/上一个文件未完成上传");
            }
        }
        List<File> allFilesInFolderUpLoad = FileUtil.getAllFilesInFolder(UpLoadCacheFile);
        for (File file : allFilesInFolderUpLoad) {
            try {
                FileUtil.uploadFile(URL, file, clientId + File.separator + DateUtil.getDate(), UpLoadCacheFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
