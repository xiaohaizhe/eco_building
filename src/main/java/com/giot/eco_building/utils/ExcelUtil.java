package com.giot.eco_building.utils;

import com.giot.eco_building.entity.Project;
import com.giot.eco_building.repository.ProjectRepository;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.formula.ptg.AreaPtg;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: pyt
 * @Date: 2020/6/12 11:31
 * @Description:
 */
@Component
public class ExcelUtil {
    private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    @Autowired
    private ProjectRepository repository;

    /**
     * 根据文件后缀名返回相应excel对象
     *
     * @param file 文件
     * @return Workbook
     * @throws IOException 包括FileNotFoundException
     */
    private Workbook getWorkbook(MultipartFile file) throws IOException {
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
        CellType type = cell.getCellType();
        if (CellType.NUMERIC.equals(type)) {
            return cell.getNumericCellValue();
        } else if (CellType.STRING.equals(type)) {
            return cell.getStringCellValue();
        } else if (CellType.FORMULA.equals(type)) {

//            return cell.getCellFormula();
        } else if (CellType.BOOLEAN.equals(type)) {
            return cell.getBooleanCellValue();
        } else {
            return null;
        }
        return null;
    }

    /**
     * 获取图片以及图片位置（xls）
     *
     * @param sheet
     * @return
     */
    private Map<String, PictureData> getPicture(HSSFSheet sheet) {
        Map<String, PictureData> map = new HashMap<>();
        List<HSSFShape> list = sheet.getDrawingPatriarch().getChildren();
        for (HSSFShape shape :
                list) {
            if (shape instanceof HSSFPicture) {
                HSSFPicture picture = (HSSFPicture) shape;
                HSSFClientAnchor clientAnchor = (HSSFClientAnchor) picture.getAnchor();
                PictureData pictureData = picture.getPictureData();
                String key = clientAnchor.getRow1() + "-" + clientAnchor.getCol1();
                map.put(key, pictureData);
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
    private Map<String, PictureData> getPicture(XSSFSheet sheet) {
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
                    String key = marker.getRow() + "-" + marker.getCol();
                    map.put(key, picture.getPictureData());
                }
            }
        }
        return map;
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


    public void dealWithExcelFile(MultipartFile file) throws IOException {
        logger.info("start deal witn excel.....");
        Workbook wb = getWorkbook(file);
        if (wb != null) {
            int sheetNum = wb.getNumberOfSheets();
            logger.info("{}文件共有{}页", file.getOriginalFilename(), sheetNum);
            for (int i = 0; i < sheetNum; i++) {
                Sheet sheet = wb.getSheetAt(i);
                int rowNum = sheet.getPhysicalNumberOfRows();
                logger.info("第{}页共有{}行数据", (i + 1), rowNum);
                for (int j = 0; j < rowNum; j++) {
                    try {
                        Row row = sheet.getRow(j);
                        int cellNum = row.getPhysicalNumberOfCells();
                        logger.info("第{}行共有{}个数据", (j + 1), cellNum);
                        for (int k = 0; k < cellNum; k++) {
                            try {
                                Cell cell = row.getCell(k);
                                logger.info("第{}个数据，数据类型：{},数据内容：{}",
                                        (k + 1),
                                        cell.getCellType(),
                                        getCellData(cell));
                            } catch (NullPointerException e) {
                                logger.error("第{}个数据异常",
                                        (k + 1));
                            }

                        }
                    } catch (NullPointerException e) {
                        logger.error("第{}行数据异常", (j + 1));
                    }

                }
                Map<String, PictureData> map = new HashMap<>();
                if (sheet instanceof XSSFSheet) {
                    map = getPicture((XSSFSheet) sheet);
                } else if (sheet instanceof HSSFSheet) {
                    map = getPicture((HSSFSheet) sheet);
                }
                for (String key :
                        map.keySet()) {
                    logger.info("{}:{}", key, map.get(key));
                }
                exportPicture(map);
            }
        }
    }
}
