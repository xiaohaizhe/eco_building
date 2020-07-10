package com.giot.eco_building.service.impl;

import com.giot.eco_building.service.UploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * @Author: pyt
 * @Date: 2020/7/9 17:49
 * @Description:
 */
@Service
public class BaseUploadService implements UploadService {
    @Value("${upload.picture.path}")
    private String path;
    @Value("${upload.picture.url}")
    private String url;

    @Override
    public String uploadProjectImg(byte[] photo) throws IOException {
        String name = UUID.randomUUID() + ".png";
        OutputStream os = new FileOutputStream(path + "" + name);
        os.write(photo, 0, photo.length);
        os.flush();
        os.close();
        String imgUrl = url + name;
        return imgUrl;
    }
}
