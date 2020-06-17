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
    public enum Authority {
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

        public String getValue() {
            return value;
        }
    }

    /**
     * 建筑类型：
     * 办公
     * 商住两用
     * 文化教育
     * 商场
     * 宾馆
     * 医院
     * 工业
     * 商业
     * 其他
     */
    public enum ArchitecturalType {
        //        COMMERCE_AT_THE_BOTTOM("底商"),
        OFFICE("办公"),
        RESIDENTIAL_AND_COMMERCIAL_COMPLEX("商住两用"),
        CULTURE_AND_EDUCATION("文化教育"),
        MALL("商场"),
        HOTEL("宾馆"),
        HOSPITAL("医院"),
//        INDUSTRY("工业"),
        COMMERCE("商业"),
        OTHER("其他");
        private String value;

        ArchitecturalType(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    /**
     * 用户操作类型：
     * 登入
     * 登出
     * 添加用户
     * 操作项目
     */
    public enum ActionType {
        LOGIN("登录", 0),
        LOGOUT("登出",1),
        ADD_USER("添加用户",2),

        ;
        private String value;
        private Integer code;

        ActionType(String value, Integer code) {
            this.value = value;
            this.code = code;
        }

        public Integer getCode() {
            return this.code;
        }

        public String getValue() {
            return value;
        }
    }
}
