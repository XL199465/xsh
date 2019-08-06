package cn.ithcast.core.controller;

import cn.ithcast.core.common.utils.FastDFSClient;
import entity.Result;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {

    // 注入ip路径,用来返回路径的拼接
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    /**
     * 文件上传的方法
     */
    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file) {
        // 去待上传文件的全路径完整名称
        String originalFilename = file.getOriginalFilename();

        try {
            // 创建一个FastDFSClient对象
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");

            // 获取文件的字节数组
            byte[] fileBytes = file.getBytes();
            // 获取扩展名
            String ext = FilenameUtils.getExtension(originalFilename);
            // 执行文件上传
            String path = fastDFSClient.uploadFile(fileBytes, ext, null);

            return new Result(true, FILE_SERVER_URL + path);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "文件上传失败");
        }
    }
}
