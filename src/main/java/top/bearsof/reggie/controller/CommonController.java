package top.bearsof.reggie.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.bearsof.reggie.common.R;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    //文件保存的路径
    public static String path = new File(Objects.requireNonNull(CommonController.class.getResource("")).getPath()).getParent() + "\\img";

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        File baseFile = new File(path);
        String fileFix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //新的文件名
        originalFilename = UUID.randomUUID()+ fileFix;
        log.info(originalFilename);
        //目录不存在，则创建目录
        if (!baseFile.exists()) {
            baseFile.mkdir();
        }
        try {
            file.transferTo(new File(baseFile.getAbsolutePath() + "\\" + originalFilename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(originalFilename);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        FileInputStream fileInputStream;
        ServletOutputStream outputStream;
        try {
            fileInputStream = new FileInputStream(this.path + "\\" + name);
            response.setContentType("image/jpeg");
            outputStream = response.getOutputStream();
            byte[] part = new byte[1024];
            int len;
            while ((len = fileInputStream.read(part)) != -1) {
                outputStream.write(part,0,len);
            }
            fileInputStream.close();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
