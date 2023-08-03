package com.hhm.Util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static List<File> getAllFilesInFolder(String folderPath) {
        List<File> fileList = new ArrayList<>();
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("指定的路径不是一个文件夹或文件夹不存在。");
            return fileList;
        }

        File[] files = folder.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isFile()) {
                    fileList.add(file);
                } else if (file.isDirectory()) {
                    fileList.addAll(getAllFilesInFolder(file.getAbsolutePath()));
                }
            }
        }
        return fileList;
    }

    public static void uploadFile(String serverUrl, File file, String ClientId, String UpLoadFileName) throws IOException {

        if (!file.exists() || !file.isFile()) {
            System.err.println("文件不存在或不是一个普通文件。");
            return;
        }


        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(serverUrl);

            ContentType contentType = ContentType.create("text/plain", StandardCharsets.UTF_8);

            // 创建MultipartEntityBuilder
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();


            entityBuilder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
            //String replaceAbsolutePath = file.getAbsoluteFile().toString().replace('\\', '/');
            //entityBuilder.addTextBody("AbsolutePath", replaceAbsolutePath, contentType);
            entityBuilder.addTextBody("AbsolutePath", file.getAbsolutePath(), contentType);
            entityBuilder.addTextBody("UpLoadFileName", UpLoadFileName, contentType);
            entityBuilder.addTextBody("ClientId", ClientId, contentType);


            // 构建HttpEntity
            HttpEntity httpEntity = entityBuilder.build();
            httpPost.setEntity(httpEntity);

            // 执行请求
            try(CloseableHttpResponse response = httpClient.execute(httpPost)){
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    System.out.println("文件上传成功。");
                    file.delete();
                } else {
                    System.out.println("文件上传失败，服务器返回：" + statusCode);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
