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
     * 商场
     * 文化教育
     * 餐饮
     * 医院
     * 酒店
     * 其他
     */
    public enum ArchitecturalType {
        //        COMMERCE_AT_THE_BOTTOM("底商"),
        OFFICE("办公", 0),
        MALL("商场", 1),
        CULTURE_AND_EDUCATION("文化教育", 2),
        CATERING("餐饮", 3),
        HOSPITAL("医院", 4),
        HOTEL("酒店", 5),
        //        RESIDENTIAL_AND_COMMERCIAL_COMPLEX("商住两用"),
//        HOTEL("宾馆"),
//        INDUSTRY("工业"),
//        COMMERCE("商业"),
        OTHER("其他", 6);
        private String value;
        private Integer code;

        ArchitecturalType(String value, Integer code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return this.value;
        }

        public Integer getCode() {
            return code;
        }
    }

    /**
     * 用户操作类型：
     * 登录
     * 登出
     * 添加用户
     * 编辑用户
     * 删除用户
     * 上传
     */
    public enum ActionType {
        LOGIN("登入", 0),
        LOGOUT("登出", 0),

        ADD_USER("添加用户", 1),
        UPDATE_USER("编辑用户", 1),
        DELETE_USER("删除用户", 2),

        IMPORT_EXCEL("上传", 3),
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

    /**
     * 项目数据类型：
     * 水
     * 电
     * 气
     */
    public enum DataType {
        WATER("水", 0),
        ELECTRICITY("电", 1),
        GAS("气", 2);
        private String value;
        private Integer code;

        DataType(String value, Integer code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public Integer getCode() {
            return code;
        }
    }

    /**
     * 绿建等级：
     * 0星
     * 1星
     * 2星
     * 3星
     * 未知
     */
    public enum GBES {
        ZERO("0星", 0),
        ONE("1星", 1),
        TWO("2星", 2),
        THREE("3星", 3),
        UNKNOWN("未知", 4),
        ;
        private String value;
        private Integer code;

        GBES(String value, Integer code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public Integer getCode() {
            return code;
        }
    }

    /**
     * 节能标准：
     * 不执行节能标准
     * 50%
     * 65%
     * 75%以上
     * 未知
     */
    public enum EnergySavingStandard {
        NON("不执行节能标准", 0),
        FIFTY("50%", 1),
        SIXTY_FIVE("65%", 2),
        SEVENTY_FIVE("75%以上", 3),
        UNKNOW("未知", 4),
        ;
        private String value;
        private Integer code;

        EnergySavingStandard(String value, Integer code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public Integer getCode() {
            return code;
        }
    }


    /**
     * 是否经过节能改造：
     * 是
     * 否
     * 未知
     */
    public enum EnergySavingTransformationOrNot {
        YES("是", 0),
        NO("否", 1),
        UNKNOW("未知", 2),
        ;
        private String value;
        private Integer code;

        EnergySavingTransformationOrNot(String value, Integer code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public Integer getCode() {
            return code;
        }
    }

    /**
     * 供暖方式：
     * 集中供暖
     * 分户供暖
     * 无供暖
     * 未知
     */
    public enum HeatingMode {
        COLLECTIVE("集中供暖", 0),
        HOUSEHOLD("分户供暖", 1),
        NO_HEATING("无供暖", 2),
        UNKNOW("未知", 3),
        ;
        private String value;
        private Integer code;

        HeatingMode(String value, Integer code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public Integer getCode() {
            return code;
        }
    }

    /**
     * 供冷方式：
     * 集中供冷
     * 分户供冷
     * 无供冷
     * 未知
     */
    public enum CoolingMode {
        COLLECTIVE("集中供冷", 0),
        HOUSEHOLD("分户供冷", 1),
        NO_COOLING("无供冷", 2),
        UNKNOW("未知", 3),
        ;
        private String value;
        private Integer code;

        CoolingMode(String value, Integer code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public Integer getCode() {
            return code;
        }
    }

    /**
     * 是否利用可再生资源：
     * 否
     * 太阳能
     * 浅层地热能
     * 未知
     */
    public enum WhetherToUseRenewableResources {
        NO("否", 0),
        SOLAR_ENERGY("太阳能", 1),
        SHALLOW_GEOTHERMAL_ENERGY("浅层地热能", 2),
        UNKNOW("未知", 3),
        ;
        private String value;
        private Integer code;

        WhetherToUseRenewableResources(String value, Integer code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public Integer getCode() {
            return code;
        }
    }
}
