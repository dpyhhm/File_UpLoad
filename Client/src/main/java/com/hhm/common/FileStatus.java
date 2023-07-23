package com.hhm.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileStatus implements ApplicationRunner {

    @Value("${file.UpLoadCacheFile}")
    private String UpLoadCacheFile;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        File file = new File(UpLoadCacheFile);
        if (!file.exists()){
            if (file.mkdirs()){
                System.out.println("文件位置存在");
            }else {
                System.out.println("同步中转文件不存在/创建失败");
            }
        }
    }
}
