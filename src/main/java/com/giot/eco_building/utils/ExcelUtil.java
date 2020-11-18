package com.giot.eco_building.utils;

import com.giot.eco_building.constant.Constants;
import com.giot.eco_building.entity.Project;
import com.giot.eco_building.model.ProjectData;
import com.giot.eco_building.repository.ProjectRepository;
import com.giot.eco_building.service.UploadService;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: pyt
 * @Date: 2020/6/12 11:31
 * @Description:
 */
@Component
public class ExcelUtil {
    private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);
    public static String[] columNames = {
            "编号", "项目名称", "工程名称", "建设单位", "面积", "楼栋数", "竣工日期", "建筑主要类型",
            "省", "市", "区县", "详细地址", "经度", "纬度", "层数", "项目概况图",
            "绿建星级", "执行节能标准", "是否经过节能改造", "供冷方式", "供暖方式", "是否利用可再生能源", "shape"
    };

    public static String[] rColumNames = {
            "编号", "项目名称", "项目地址", "建设单位", "设计单位", "施工单位", "监理单位", "建成时间", "建筑楼栋信息", "建筑高度", "总建筑面积",
            "地上建筑面积", "地下建筑面积", "空调面积", "地上建筑层数", "地下建筑层数",
            "主要遮阳形式", "有无楼宇自控系统", "有无能耗计量/远传电表数据",
            "有无用水计量/远传水表数据", "窗户/幕墙是否可开启", "新风系统形式", "末端用户数量", "暖通设备概况及信息", "电器设备概况及信息",
            "给排水系统概况", "近三年能耗数据", "是否建议改造", "改造建议",
            "经度", "纬度",
            "建筑整体照片", "空调机房照片", "主机照片", "水泵照片", "冷却塔照片", "空调箱照片", "末端设备照片",
            "空调系统形式"
    };
    private ProjectRepository projectRepository;

    private UploadService uploadService;

    @Autowired
    public void setUploadService(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * 根据文件后缀名返回相应excel对象
     *
     * @param file 文件
     * @return Workbook
     * @throws IOException 包括FileNotFoundException
     */
    public Workbook getWorkbook(MultipartFile file) throws IOException {
        Workbook wb = null;
        if (file == null) {
            return null;
        }
        String fileName = file.getOriginalFilename();
        //1.获取后缀名
        String extString = fileName.substring(fileName.lastIndexOf('.'));
        InputStream is = file.getInputStream();
        //2.根据后缀名返回相应的对象
        if (".xls".equals(extString)) {
            wb = new HSSFWorkbook(is);
        } else if (".xlsx".equals(extString)) {
            wb = new XSSFWorkbook(is);
        }
        return wb;
    }

    /**
     * 根据cell类型返回相应数据
     *
     * @param cell
     * @return
     */
    private Object getCellData(Cell cell) {
        if (cell == null) return null;
        CellType type = cell.getCellType();
        if (CellType.NUMERIC.equals(type)) {
            return cell.getNumericCellValue();
        } else if (CellType.STRING.equals(type)) {
            return cell.getStringCellValue().trim();
        } else if (CellType.FORMULA.equals(type)) {
            return cell.getNumericCellValue();
        } else if (CellType.BOOLEAN.equals(type)) {
            return cell.getBooleanCellValue();
        }
        return null;
    }

    /**
     * 获取图片以及图片位置（xls）
     *
     * @param sheet
     * @return
     */
    private Map<Integer, PictureData> getPicture(HSSFSheet sheet) {
        Map<Integer, PictureData> map = new HashMap<>();
        List<HSSFShape> list = sheet.getDrawingPatriarch().getChildren();
        for (HSSFShape shape :
                list) {
            if (shape instanceof HSSFPicture) {
                HSSFPicture picture = (HSSFPicture) shape;
                HSSFClientAnchor clientAnchor = (HSSFClientAnchor) picture.getAnchor();
                PictureData pictureData = picture.getPictureData();
                Integer key = clientAnchor.getRow1();
                map.put(key, pictureData);
            }
        }
        return map;
    }

    private Map<String, PictureData> getRPicture(HSSFSheet sheet) {
        Map<String, PictureData> map = new HashMap<>();
        List<HSSFShape> list = sheet.getDrawingPatriarch().getChildren();
        for (HSSFShape shape :
                list) {
            if (shape instanceof HSSFPicture) {
                HSSFPicture picture = (HSSFPicture) shape;
                HSSFClientAnchor clientAnchor = (HSSFClientAnchor) picture.getAnchor();
                PictureData pictureData = picture.getPictureData();
                Integer key1 = clientAnchor.getRow1();
//                Integer key2 = clientAnchor.getCol1();
                map.put(key1 + "", pictureData);
            }
        }
        return map;
    }

    /**
     * 获取图片以及图片位置（xlsx）
     *
     * @param sheet
     * @return
     */
    private Map<Integer, PictureData> getPicture(XSSFSheet sheet) {
        Map<Integer, PictureData> map = new HashMap<>();
        List<POIXMLDocumentPart> list = sheet.getRelations();
        for (POIXMLDocumentPart part :
                list) {
            if (part instanceof XSSFDrawing) {
                XSSFDrawing drawing = (XSSFDrawing) part;
                List<XSSFShape> shapes = drawing.getShapes();
                for (XSSFShape shape : shapes) {
                    XSSFPicture picture = (XSSFPicture) shape;
                    XSSFClientAnchor anchor = picture.getPreferredSize();
                    CTMarker marker = anchor.getFrom();
                    Integer key = marker.getRow();
                    map.put(key, picture.getPictureData());
                }
            }
        }
        return map;
    }

    private Map<String, PictureData> getRPicture(XSSFSheet sheet) {
        Map<String, PictureData> map = new HashMap<>();
        List<POIXMLDocumentPart> list = sheet.getRelations();
        for (POIXMLDocumentPart part :
                list) {
            if (part instanceof XSSFDrawing) {
                XSSFDrawing drawing = (XSSFDrawing) part;
                List<XSSFShape> shapes = drawing.getShapes();
                for (XSSFShape shape : shapes) {
                    XSSFPicture picture = (XSSFPicture) shape;
                    XSSFClientAnchor anchor = picture.getPreferredSize();
                    CTMarker marker = anchor.getFrom();
                    Integer key1 = marker.getRow();
                    Integer key2 = marker.getCol();
                    map.put(key1 + "," + key2, picture.getPictureData());
                }
            }
        }
        return map;
    }

    /**
     * 处理每页的第一行数据，获取字段index
     * columNamesIndex的第i个数据的值，
     * 对应columNames的第i个name在excel表中的列序号
     *
     * @param firstRow
     * @return
     */
    public Integer[] dealWithFirstRow(Row firstRow) {
        int cnsLength = columNames.length;
        Integer[] columNamesIndex = new Integer[cnsLength];
        int cellNum = firstRow.getPhysicalNumberOfCells();
        for (int i = 0; i < cellNum; i++) {
            Cell cell = firstRow.getCell(i);
            if (cell != null && cell.getCellType().equals(CellType.STRING)) {
                String columName = (String) getCellData(cell);
                for (int j = 0; j < cnsLength; j++) {
                    if (columNamesIndex[j] != null) continue;
                    if (columName.equals(columNames[j])) {
                        columNamesIndex[j] = i;
                        break;
                    }
                }
            }
        }
        return columNamesIndex;
    }

    public Integer[] dealWithRFirstRow(Row firstRow) {
        int cnsLength = rColumNames.length;
        Integer[] columNamesIndex = new Integer[cnsLength];
        int cellNum = firstRow.getPhysicalNumberOfCells();
        for (int i = 0; i < cellNum; i++) {
            Cell cell = firstRow.getCell(i);
            if (cell != null && cell.getCellType().equals(CellType.STRING)) {
                String columName = (String) getCellData(cell);
                for (int j = 0; j < cnsLength; j++) {
                    if (columNamesIndex[j] != null) continue;
                    if (columName.equals(rColumNames[j]) || columName.contains(rColumNames[j])) {
                        columNamesIndex[j] = i;
                        break;
                    }
                }
            }
        }
        return columNamesIndex;
    }

    /**
     * 处理每行的第2行数据，获取水电气年月数据index：
     * WATER("水", 0),
     * ELECTRICITY("电", 1),
     * GAS("气", 2);
     * 根据第一行的：
     * 全年电耗(kWh)-20，逐月电耗 (kWh)-21；
     * 全年气耗(m3)-23，逐月气耗 (m3)-24；
     * 全年水耗(m3)-26，逐月水耗 (m3)-27；
     *
     * @param secondRow
     */
    public Map<String, List<Map<String, Integer>>> dealWithSecondRow(Row secondRow, Integer[] columNamesIndex) {
        Map<String, List<Map<String, Integer>>> result = new HashMap<>();
        //1.除了月耗end——index都可以确定
        //全年电耗:[s_electricity_year,e_electricity_year)
        int[] electricityIndex = new int[]{columNamesIndex[20], columNamesIndex[21], -1};
        /*int s_electricity_year = columNamesIndex[20];
        int e_electricity_year = columNamesIndex[21];
        //逐月电耗[e_electricity_year,待确定)
        int s_electricity_month = columNamesIndex[21];*/
        //全年气耗
        int[] gasIndex = new int[]{columNamesIndex[23], columNamesIndex[24], -1};
        /*int s_gas_year = columNamesIndex[23];
        int e_gas_year = columNamesIndex[24];
        //逐月气耗
        int s_gas_month = columNamesIndex[24];*/
        //全年水耗
        int[] waterIndex = new int[]{columNamesIndex[26], columNamesIndex[27], -1};
        /*int s_water_year = columNamesIndex[26];
        int e_water_year = columNamesIndex[27];
        //逐月水耗
        int s_water_month = columNamesIndex[27];*/


        //2.确定水电气开始index顺序，以确定月耗end-index
        int totalLen = secondRow.getPhysicalNumberOfCells();
        //电耗
        int s_elec = columNamesIndex[19];
        //气耗
        int s_gas = columNamesIndex[22];
        //水耗
        int s_water = columNamesIndex[25];

        int[] insertData = new int[]{s_elec, s_gas, s_water, totalLen};
        for (int i = 0; i < insertData.length - 1; i++) {
            int iData = insertData[i];
            for (int j = i + 1; j < insertData.length; j++) {
                int jData = insertData[j];
                if (iData > jData) {
                    insertData[i] = jData;
                    insertData[j] = iData;
                }
            }
        }
        boolean elecFlag = true;
        boolean gasFlag = true;
        boolean waterFlag = true;
        //确定水电气数据结束index
        for (int i = 1; i < insertData.length; i++) {
            int iData = insertData[i];
            if (elecFlag && electricityIndex[1] < iData) {
                electricityIndex[2] = iData;
                elecFlag = false;
                continue;
            }
            if (gasFlag && gasIndex[1] < iData) {
                gasIndex[2] = iData;
                gasFlag = false;
                continue;
            }
            if (waterFlag && waterIndex[1] < iData) {
                waterIndex[2] = iData;
                waterFlag = false;
                continue;
            }
        }
        Map<String, List<Map<String, Integer>>> waterResult = getMonthAndYearIndex(waterIndex, secondRow);
        Map<String, List<Map<String, Integer>>> gasResult = getMonthAndYearIndex(gasIndex, secondRow);
        Map<String, List<Map<String, Integer>>> elecResult = getMonthAndYearIndex(electricityIndex, secondRow);

        result.put("water_year", waterResult.get("year"));
        result.put("electricity_year", elecResult.get("year"));
        result.put("gas_year", gasResult.get("year"));

        result.put("water_month", waterResult.get("month"));
        result.put("electricity_month", elecResult.get("month"));
        result.put("gas_month", gasResult.get("month"));

        return result;
    }

    private Map<String, List<Map<String, Integer>>> getMonthAndYearIndex(int[] index, Row row) {
        Map<String, List<Map<String, Integer>>> result = new HashMap<>();

        List<Map<String, Integer>> yearList = new ArrayList<>();
        List<Map<String, Integer>> monthList = new ArrayList<>();
        // 年数据
        for (int i = index[0]; i < index[1]; i++) {
            Cell cell = row.getCell(i);
            if (cell != null) {
                Object cellData = getCellData(cell);
                if (cellData != null && cellData instanceof Double) {
                    Double date = (Double) cellData;
                    String columName = String.valueOf(date.intValue());
                    Map<String, Integer> map = new HashMap<>();
                    map.put(columName, i);
                    yearList.add(map);
                }
            }
        }
        // 月数据
        for (int i = index[1]; i < index[2]; i++) {
            Cell cell = row.getCell(i);
            if (cell != null) {
                Object cellData = getCellData(cell);
                if (cellData != null && cellData instanceof Double) {
                    Double date = (Double) cellData;
                    String columName = String.valueOf(date.intValue());
                    Map<String, Integer> map = new HashMap<>();
                    map.put(columName, i);
                    monthList.add(map);
                }
            }
        }
        result.put("month", monthList);
        result.put("year", yearList);
        return result;
    }

    /**
     * 根据时间对应的index获取时间对应的数据
     *
     * @param list
     * @param row
     * @return
     */
    private Map<String, Double> getTimeAndDataByYearAndMonthList(List<Map<String, Integer>> list, Row row) {
        Map<String, Double> result = new HashMap<>();
        for (Map<String, Integer> map :
                list) {
            for (String key :
                    map.keySet()) {
                int index = map.get(key);
                Cell cCell = row.getCell(index);
                Object cellData = getCellData(cCell);
                if (cellData != null) {
                    if (cellData instanceof Double) {
                        result.put(key, (Double) cellData);
                    } else if (cellData instanceof String) {
                        String data = (String) cellData;
                        data = data.trim();
                        if (StringUtil.checkStrIsNum(data)) {
                            result.put(key, Double.valueOf(data));
                        }

                    }
                }
            }
        }
        return result;
    }

    private void exportPicture(Map<String, PictureData> pictureDataMap) throws IOException {
        for (String key :
                pictureDataMap.keySet()) {
            PictureData pictureData = pictureDataMap.get(key);
            //获取图片格式
            String ext = pictureData.suggestFileExtension();
            logger.info("图片格式：{}", ext);
            byte[] data = pictureData.getData();
            Byte[] bytes = new Byte[data.length];
            int i = 0;
            for (byte b : data) bytes[i++] = b;
//                Project project = repository.findById(645131296309248l).orElse(null);
//                project.setPhoto(bytes);
//                repository.saveAndFlush(project);
        }
    }

    public Map<String, Object> mapToProject(Map<Integer, Object> map) throws ParseException, IOException {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");

        Map<String, Object> result = new HashMap<>();

        //1.基础数据
        Project project = new Project();

        Object index0 = map.get(0);
        String projectName = "";
        if (index0 != null) {
            projectName = (String) index0;
            project.setName(projectName);
        }

        Object index1 = map.get(1);
        if (index1 != null) project.setProvince((String) index1);

        Object index2 = map.get(2);
        if (index2 != null) project.setCity((String) index2);

        Object index3 = map.get(3);
        if (index3 != null) project.setDistrict((String) index3);

        /*Object index4 = map.get(4);
        if (index4 != null) project.setStreet((String) index4);*/

        Object index5 = map.get(5);
        if (index5 != null) project.setAddress((String) index5);

        Object index6 = map.get(6);
        if (index6 != null) project.setArchitecturalType((String) index6);
        else project.setArchitecturalType("无");

        //建成时间
        Object index7 = map.get(7);
        if (index7 != null) {
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy");
            Date builtTime = null;
            String btString = (String) index7;
            if (btString.length() > 4) {
                builtTime = simpleDateFormat1.parse(btString);
            }
            if (btString.length() == 4) {
                builtTime = simpleDateFormat2.parse(btString);
            }
            project.setBuiltTime(builtTime);
        }
        //项目概况图
        Object index8 = map.get(8);
        if (index8 != null) {
            logger.info("项目图片：" + index8);
            project.setImgUrl((String) index8);
        }

        Object index9 = map.get(9);
        if (index9 != null) project.setGbes((String) index9);
        else project.setGbes("无");

        Object index10 = map.get(10);
        if (index10 != null) project.setEnergySavingStandard((String) index10);
        else project.setEnergySavingStandard("无");

        Object index11 = map.get(11);
        if (index11 != null) project.setEnergySavingTransformationOrNot((String) index11);
        else project.setEnergySavingTransformationOrNot("无");

        Object index12 = map.get(12);
        if (index12 != null) project.setCoolingMode((String) index12);
        else project.setCoolingMode("无");

        Object index13 = map.get(13);
        if (index13 != null) project.setHeatingMode((String) index13);
        else project.setHeatingMode("无");

        Object index14 = map.get(14);
        if (index14 != null) project.setWhetherToUseRenewableResources((String) index14);
        else project.setWhetherToUseRenewableResources("无");

        Object index15 = map.get(15);
        if (index15 != null && index15 instanceof Double) project.setLongitude((Double) index15);

        Object index16 = map.get(16);
        if (index16 != null && index16 instanceof Double) project.setLatitude((Double) index16);

        Object index17 = map.get(17);
        if (index17 != null && index17 instanceof Double) project.setArea((Double) index17);

        Object index18 = map.get(18);
        if (index18 != null && index18 instanceof Double) project.setFloor(((Double) index18).intValue());

        result.put("project", project);

        //2.水电气数据
        Map<String, Map<String, Double>> projectData = (Map<String, Map<String, Double>>) map.get(-1);
        Map<String, Double> waterData = projectData.get(Constants.DataType.WATER.getValue());
        Map<String, Double> gasDAta = projectData.get(Constants.DataType.GAS.getValue());
        Map<String, Double> elecData = projectData.get(Constants.DataType.ELECTRICITY.getValue());

        List<ProjectData> projectDataList = new ArrayList<>();
        for (String key :
                waterData.keySet()) {
//            logger.info("时间：{}", key);
            ProjectData data = new ProjectData();
            data.setProjectName(projectName);
            if (isYear(key)) {
                data.setIsMonth(false);
                data.setDate(sdf1.parse(key));
            } else {
                data.setIsMonth(true);
                data.setDate(sdf2.parse(key));
            }
            data.setProjectType(Constants.DataType.WATER.getCode());
            data.setValue(waterData.get(key));
            projectDataList.add(data);
        }
        for (String key :
                gasDAta.keySet()) {
//            logger.info("时间：{}", key);
            ProjectData data = new ProjectData();
            data.setProjectName(projectName);
            if (isYear(key)) {
                data.setIsMonth(false);
                data.setDate(sdf1.parse(key));
            } else {
                data.setIsMonth(true);
                data.setDate(sdf2.parse(key));
            }
            data.setProjectType(Constants.DataType.GAS.getCode());
            data.setValue(gasDAta.get(key));
            projectDataList.add(data);
        }
        for (String key :
                elecData.keySet()) {
//            logger.info("时间：{}", key);
            ProjectData data = new ProjectData();
            data.setProjectName(projectName);
            if (isYear(key)) {
                data.setIsMonth(false);
                data.setDate(sdf1.parse(key));
            } else {
                data.setIsMonth(true);
                data.setDate(sdf2.parse(key));
            }
            data.setProjectType(Constants.DataType.ELECTRICITY.getCode());
            data.setValue(elecData.get(key));
            projectDataList.add(data);
        }
        result.put("data", projectDataList);
        return result;
    }

    private boolean isYear(String key) {
        key = key.trim();
        return key.length() == 4;
    }

    private String getDateByCell(Cell cell) {
        if (cell == null || cell.toString().trim().equals("")) {
            return null;
        }
        String cellValue = "";
        CellType celltype = cell.getCellType();
        if (celltype.equals(CellType.NUMERIC)) {//数字
            short format = cell.getCellStyle().getDataFormat();
            if (DateUtil.isCellDateFormatted(cell)) {
                SimpleDateFormat sdf = null;
                //System.out.println("cell.getCellStyle().getDataFormat()="+cell.getCellStyle().getDataFormat());
                if (format == 20 || format == 32) {
                    sdf = new SimpleDateFormat("HH:mm");
                } else if (format == 14 || format == 31 || format == 57 || format == 58) {
                    // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    double value = cell.getNumericCellValue();
                    Date date = org.apache.poi.ss.usermodel.DateUtil
                            .getJavaDate(value);
                    cellValue = sdf.format(date);
                } else {// 日期
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                }
                try {
                    cellValue = sdf.format(cell.getDateCellValue());// 日期
                } catch (Exception e) {
                    try {
                        throw new Exception("exception on get date data !".concat(e.toString()));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } finally {
                    sdf = null;
                }
            } else {
                BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
                cellValue = bd.toPlainString();// 数值 这种用BigDecimal包装再获取plainString，可以防止获取到科学计数值
            }
        }
        return cellValue;
    }

    /**
     * 处理excel一页数据
     *
     * @param sheet
     */
    @Deprecated
    public List<Map<Integer, Object>> dealWithSheet(Sheet sheet) throws IOException {
        int rowNum = sheet.getPhysicalNumberOfRows();
        logger.info("------------------------------------");
        /**
         * 当页数据量>0(第一行为字段名称，第二行为水电气日期标识)
         */
        //保存项目基础数据
        List<Map<Integer, Object>> projectList = new ArrayList<>();
        if (rowNum > 2) {
//            logger.info("根据第一行确定字段index");
            //处理每页的第1行数据，获取字段index
            Integer[] columNamesIndex = dealWithFirstRow(sheet.getRow(0));
            //处理每页的第2行数据，获取水电气数据index
            Map<String, List<Map<String, Integer>>> dataMap = dealWithSecondRow(sheet.getRow(1), columNamesIndex);
//            Map<Integer, String> nameMap = new HashMap<>();
            //处理图片
            Map<Integer, PictureData> picMap = new HashMap<>();
            if (sheet instanceof XSSFSheet) {
                picMap = getPicture((XSSFSheet) sheet);
            } else if (sheet instanceof HSSFSheet) {
                picMap = getPicture((HSSFSheet) sheet);
            }
            for (int j = 2; j < rowNum; j++) {
                Row row = sheet.getRow(j);
                if (row != null) {
                    int cellNum = row.getPhysicalNumberOfCells();
//                    logger.info("第{}行共有{}个参数", (j + 1), cellNum);
                    int nameIndex = columNamesIndex[0];
                    Cell nameCell = row.getCell(nameIndex);
                    if (nameCell != null && nameCell.getCellType().equals(CellType.STRING)) {
                        String projectName = (String) getCellData(nameCell);
                        logger.info("项目:{}的数据有效，开始处理>>>>>>>", projectName);
                        Map<Integer, Object> projectBaseData = new HashMap<>();
                        projectBaseData.put(0, getCellData(nameCell));

                        int baseDataIndexEnd = columNamesIndex.length - 9;
                        //1.处理表格中的基础数据
                        for (int k = 1; k < baseDataIndexEnd; k++) {
                            if (columNamesIndex[k] == null) continue;
                            int cindex = columNamesIndex[k];
                            Cell cCell = row.getCell(cindex);
                            Object cellData = getCellData(cCell);
                            if (cellData != null) {
                                projectBaseData.put(k, cellData);
                            }
                        }
                        //处理建成时间数据
                        int btIndex = columNamesIndex[7];
                        Cell btCell = row.getCell(btIndex);
                        String btString = getDateByCell(btCell);
                        logger.info("建成时间：" + btString);
                        projectBaseData.put(7, btString);
                        //处理基础数据中的图片数据
                        PictureData projectPic = picMap.get(j);
                        if (projectPic != null) {
                            String imgUrl = uploadService.uploadProjectImg(projectPic.getData());
                            projectBaseData.put(8, imgUrl);
                        } else projectBaseData.put(8, null);

                        //2.处理表格中的水电气数据:
                        // water_year,electricity_year,gas_year,
                        // water_month,electricity_month,gas_month
                        List<Map<String, Integer>> water_year_list = dataMap.get("water_year");
                        Map<String, Double> waterYearData = getTimeAndDataByYearAndMonthList(water_year_list, row);
                        List<Map<String, Integer>> water_month_list = dataMap.get("water_month");
                        Map<String, Double> waterMonthData = getTimeAndDataByYearAndMonthList(water_month_list, row);
                        waterMonthData.putAll(waterYearData);

                        List<Map<String, Integer>> electricity_year_list = dataMap.get("electricity_year");
                        Map<String, Double> electricityYearData = getTimeAndDataByYearAndMonthList(electricity_year_list, row);
                        List<Map<String, Integer>> electricity_month_list = dataMap.get("electricity_month");
                        Map<String, Double> electricityMonthData = getTimeAndDataByYearAndMonthList(electricity_month_list, row);
                        electricityMonthData.putAll(electricityYearData);

                        List<Map<String, Integer>> gas_year_list = dataMap.get("gas_year");
                        Map<String, Double> gasYearData = getTimeAndDataByYearAndMonthList(gas_year_list, row);
                        List<Map<String, Integer>> gas_month_list = dataMap.get("gas_month");
                        Map<String, Double> gasMonthData = getTimeAndDataByYearAndMonthList(gas_month_list, row);
                        gasMonthData.putAll(gasYearData);

                        Map<String, Object> projectData = new HashMap<>();
                        projectData.put(Constants.DataType.WATER.getValue(), waterMonthData);
                        projectData.put(Constants.DataType.GAS.getValue(), gasMonthData);
                        projectData.put(Constants.DataType.ELECTRICITY.getValue(), electricityMonthData);

                        projectBaseData.put(-1, projectData);
                        projectList.add(projectBaseData);
                    }
                }
            }


        } else {
            logger.info("当页无数据");
        }
        return projectList;
    }

    public List<Map<String, Object>> dealWithExcelSheet(Sheet sheet) throws IOException {
        int rowNum = sheet.getPhysicalNumberOfRows();
        logger.info("------------------------------------");
        /**
         * 当页数据量>0(第一行为字段名称)
         */
        List<Map<String, Object>> result = new ArrayList<>();
        if (rowNum > 1) {
//            logger.info("根据第一行确定字段index");
            //处理每页的第1行数据，获取字段index
            Integer[] columNamesIndex = dealWithFirstRow(sheet.getRow(0));
            //处理图片
            Map<Integer, PictureData> picMap = new HashMap<>();
            if (sheet instanceof XSSFSheet) {
                picMap = getPicture((XSSFSheet) sheet);
            } else if (sheet instanceof HSSFSheet) {
                picMap = getPicture((HSSFSheet) sheet);
            }
            for (int j = 1; j < rowNum; j++) {
                Map<String, Object> projectBaseData = new HashMap<>();
                Row row = sheet.getRow(j);
                if (row != null) {
                    int cellNum = row.getPhysicalNumberOfCells();
//                    logger.info("第{}行共有{}个参数", (j + 1), cellNum);
                    int serialNumberIndex = columNamesIndex[0];
                    Cell serialNumberCell = row.getCell(serialNumberIndex);
                    if (serialNumberCell != null && serialNumberCell.getCellType().equals(CellType.STRING)) {
                        String serialNumber = (String) getCellData(serialNumberCell);
                        logger.info("项目编号:{}的数据有效，开始处理>>>>>>>", serialNumber);
                        projectBaseData.put(ExcelUtil.columNames[0], getCellData(serialNumberCell));
                        //1.处理表格中的基础数据
                        for (int k = 1; k < columNamesIndex.length; k++) {
                            if (columNamesIndex[k] == null) continue;
                            int cindex = columNamesIndex[k];
                            Cell cCell = row.getCell(cindex);
                            Object cellData;
                            if (k == 6) {
                                cellData = getDateByCell(cCell);
                            } else {
                                cellData = getCellData(cCell);
                            }
                            if (cellData != null) {
                                projectBaseData.put(ExcelUtil.columNames[k], cellData);
                            }
                        }
                        //处理基础数据中的图片数据
                        PictureData projectPic = picMap.get(j);
                        if (projectPic != null) {
                            String imgUrl = uploadService.uploadProjectImg(projectPic.getData());
                            projectBaseData.put(ExcelUtil.columNames[14], imgUrl);
                        } else projectBaseData.put(ExcelUtil.columNames[14], null);
                    }
                }
                result.add(projectBaseData);
            }
        } else {
            logger.info("当页无数据");
        }
        return result;
    }

    public List<Map<String, Object>> dealWithRExcelSheet(Sheet sheet) throws IOException {
        int rowNum = sheet.getPhysicalNumberOfRows();
        logger.info("------------------------------------");
        /**
         * 当页数据量>0(第一行为字段名称)
         */
        List<Map<String, Object>> result = new ArrayList<>();
        if (rowNum > 1) {
//            logger.info("根据第一行确定字段index");
            //处理每页的第1行数据，获取字段index
            Integer[] columNamesIndex = dealWithRFirstRow(sheet.getRow(0));
            //处理图片
            Map<String, PictureData> picMap = new HashMap<>();
            if (sheet instanceof XSSFSheet) {
                picMap = getRPicture((XSSFSheet) sheet);
            }
            /*else if (sheet instanceof HSSFSheet) {
                picMap = getRPicture((HSSFSheet) sheet);
            }*/
            Map<Integer, Map<Integer, PictureData>> picMapPre = new HashMap<>();
            for (String key :
                    picMap.keySet()) {
                Integer key1 = Integer.valueOf(key.split(",")[0]);
                Integer key2 = Integer.valueOf(key.split(",")[1]);
                if (null != picMapPre.get(key1)) {
                    Map<Integer, PictureData> map = picMapPre.get(key1);
                    map.put(key2, picMap.get(key));
                } else {
                    Map<Integer, PictureData> map = new HashMap<>();
                    map.put(key2, picMap.get(key));
                    picMapPre.put(key1, map);
                }
            }
            for (int j = 1; j < rowNum; j++) {
                Map<String, Object> projectBaseData = new HashMap<>();
                Row row = sheet.getRow(j);
                if (row != null) {
                    int cellNum = row.getPhysicalNumberOfCells();
//                    logger.info("第{}行共有{}个参数", (j + 1), cellNum);
                    int serialNumberIndex = columNamesIndex[0];
                    Cell serialNumberCell = row.getCell(serialNumberIndex);
                    if (serialNumberCell != null && serialNumberCell.getCellType().equals(CellType.STRING)) {
                        String serialNumber = (String) getCellData(serialNumberCell);
                        logger.info("项目编号:{}的数据有效，开始处理>>>>>>>", serialNumber);
                        projectBaseData.put(ExcelUtil.rColumNames[0], getCellData(serialNumberCell));
                        //1.处理表格中的基础数据
                        for (int k = 1; k < columNamesIndex.length; k++) {
                            if (columNamesIndex[k] == null) continue;
                            int cindex = columNamesIndex[k];
                            Cell cCell = row.getCell(cindex);
                            Object cellData;
                            if (k == 6) {
                                cellData = getDateByCell(cCell);
                            } else {
                                cellData = getCellData(cCell);
                            }
                            if (cellData != null) {
                                projectBaseData.put(ExcelUtil.rColumNames[k], cellData);
                            }
                        }
                        //处理基础数据中的图片数据
                        Map<Integer, PictureData> pictureDataMap = picMapPre.get(j);
                        for (Integer key :
                                pictureDataMap.keySet()) {
                            PictureData projectPic = pictureDataMap.get(key);
                            Integer index = null;
                            for (int i = 0; i < columNamesIndex.length; i++) {
                                if (columNamesIndex[i] != key) {
                                    continue;
                                } else {
                                    index = i;
                                    break;
                                }
                            }
                            if (projectPic != null) {
                                String imgUrl = uploadService.uploadProjectImg(projectPic.getData());
                                projectBaseData.put(ExcelUtil.rColumNames[index], imgUrl);
                            } else projectBaseData.put(ExcelUtil.rColumNames[index], null);
                        }
                    }
                }
                result.add(projectBaseData);
            }
        } else {
            logger.info("当页无数据");
        }
        return result;
    }
}
