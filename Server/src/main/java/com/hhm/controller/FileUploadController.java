package com.hhm.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
public class FileUploadController {
    @Value("${file.save.path}")
    private String SAVE_PATH;

//    @Value("${file.OriginalFilename}")
//    private String ORIGINAL_FILE_NAME;

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("AbsolutePath") String absolutePath,
        @RequestParam("ClientId") String clientId, @RequestParam("UpLoadFileName") String upLoadFileName) {
        try {
            //System.out.println(absolutePath);
            //System.out.println(fileName);
            //System.out.println(absolutePath.replaceAll("设备资料", "存储位置"));
            //System.out.println(absolutePath.replaceAll(ORIGINAL_FILE_NAME, SAVE_PATH));
            // 在这里处理文件上传逻辑
            //file.transferTo(new File(absolutePath.replaceAll(absolutePath.substring(0,absolutePath.indexOf(ORIGINAL_FILE_NAME)), "存储位置")));

            //String path = absolutePath.replaceAll(ORIGINAL_FILE_NAME, SAVE_PATH + "/" + clientId);
            String path = Paths.get(Paths.get(SAVE_PATH,clientId).toString(), absolutePath.substring(upLoadFileName.length() + 1)).toString();
            // 例如，保存文件到服务器或进行其他处理
            File file1 = new File(path);//创建多级目录
            if (!file1.exists()){
                file1.mkdirs();
            }
            file.transferTo(file1);
            return ResponseEntity.status(HttpStatus.OK).body("文件上传成功。");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("文件上传失败。");
        }
    }
}
