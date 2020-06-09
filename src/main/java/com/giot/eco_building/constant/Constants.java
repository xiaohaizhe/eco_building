package com.giot.eco_building.constant;

/**
 * @Author: pyt
 * @Date: 2020/6/9 14:14
 * @Description:
 */
public class Constants {
    public static final String RESPONSE_CODE_KEY = "code"; //返回对象里的code的key名称
    public static final String RESPONSE_MSG_KEY = "msg"; //返回对象里的msg的key名称
    public static final String RESPONSE_DATA_KEY = "data"; //返回对象里的data的key名称
    public static final String RESPONSE_SIZE_KEY = "size"; //返回对象里的size的key名称

    /**
     * 删除标志位
     */
    public enum DelStatus {
        /**
         * 有效，0
         */
        NORMAL(false),
        /**
         * 无效，1
         */
        DELETE(true);
        private final boolean value;

        DelStatus(boolean value) {
            this.value = value;
        }

        public boolean isValue() {
            return value;
        }

    }

    /**
     * 用户权限
     */
    public enum Authority{
        /**
         * 普通用户
         */
        USER("USER"),
        /**
         * 管理员
         */
        ADMIN("ADMIN");
        private final String value;

        Authority(String value) {
            this.value = value;
        }

        public String getValue(){
            return value;
        }
    }
}
