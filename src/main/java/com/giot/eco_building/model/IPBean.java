package com.giot.eco_building.model;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-09-20
 * Time: 16:36
 */
@Data
public class IPBean {
    public static final int TYPE_HTTP = 0;
    public static final int TYPE_HTTPS = 1;

    private String ip;
    private int port;
    private int type;
}
