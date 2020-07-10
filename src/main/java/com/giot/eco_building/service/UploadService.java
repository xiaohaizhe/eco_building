package com.giot.eco_building.service;

import java.io.IOException;

/**
 * @Author: pyt
 * @Date: 2020/7/9 17:48
 * @Description:
 */
public interface UploadService {
    String uploadProjectImg(byte[] photo) throws IOException;
}
