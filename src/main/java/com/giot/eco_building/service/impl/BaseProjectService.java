package com.giot.eco_building.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.constant.Constants;
import com.giot.eco_building.constant.HttpResponseStatusEnum;
import com.giot.eco_building.entity.Project;
import com.giot.eco_building.model.DataModel;
import com.giot.eco_building.model.EnergySortModel;
import com.giot.eco_building.model.ProjectData;
import com.giot.eco_building.model.ProjectModel;
import com.giot.eco_building.repository.ProjectDataRepository;
import com.giot.eco_building.repository.ProjectRepository;
import com.giot.eco_building.service.MapService;
import com.giot.eco_building.service.ProjectDataService;
import com.giot.eco_building.service.ProjectService;
import com.giot.eco_building.service.UploadService;
import com.giot.eco_building.utils.ExcelUtil;
import com.giot.eco_building.utils.HttpUtil;
import com.giot.eco_building.utils.ImageCheck;
import com.giot.eco_building.utils.ProjectCsvToBeanFilter;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: pyt
 * @Date: 2020/6/11 9:35
 * @Description:
 */
@Service
@Transactional
@Component("BaseProjectService")
public class BaseProjectService implements ProjectService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${baidu.address.url.decode}")
    private String MAP_URL;
    @Value("${baidu.address.ak}")
    private String AK;

    private ProjectRepository projectRepository;

    private UploadService uploadService;


    private ExcelUtil excelUtil;

    private ProjectDataService projectDataService;

    private ProjectDataRepository projectDataRepository;

    private MapService mapService;

    @Autowired
    private LocalContainerEntityManagerFactoryBean entityManagerFactory;

    @Autowired
    public void setMapService(MapService mapService) {
        this.mapService = mapService;
    }

    @Autowired
    public void setProjectDataRepository(ProjectDataRepository projectDataRepository) {
        this.projectDataRepository = projectDataRepository;
    }

    @Autowired
    public void setProjectDataService(ProjectDataService projectDataService) {
        this.projectDataService = projectDataService;
    }

    @Autowired
    public void setExcelUtil(ExcelUtil excelUtil) {
        this.excelUtil = excelUtil;
    }

    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Autowired
    public void setUploadService(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    /**
     * 检查项目名是否已存在
     * true-存在
     * false-不存在
     *
     * @param name
     * @return
     */
    private boolean projectNameExist(String name) {
        Optional<Project> project = projectRepository.findByNameAndDelStatus(name, Constants.DelStatus.NORMAL.isValue());
        return project.isPresent();
    }

    /**
     * 插入项目基础数据-根据项目名判断是否已存在：
     * -不存在项目--直接存储
     * -已存在项目--基础数据丢弃
     *
     * @param projectList
     * @return
     */
    @Transactional
    public Map<String, String> insertAll(List<Project> projectList) {
        Set<String> projectNames = new HashSet<>();
        List<Project> projectListResult = new ArrayList<>();
        Map<String, String> result = new HashMap<>();
        for (Project project : projectList) {
            logger.info(project.toString());
            if (project != null
                    && project.getName() != null
                    && project.getProvince() != null && project.getCity() != null && project.getDistrict() != null
//                    && project.getStreet() != null
                    && project.getAddress() != null && project.getArchitecturalType() != null
                    && project.getLongitude() != null && project.getLatitude() != null && project.getArea() != null
                    && project.getBuiltTime() != null) {
                String projectName = project.getName();
                if (!"".equals(projectName) &&
                        !projectNames.contains(projectName) &&
                        !projectNameExist(projectName)) {
                    logger.info("项目：{}正常", projectName);
                    projectNames.add(projectName);
                    project.setDelStatus(Constants.DelStatus.NORMAL.isValue());
                    projectListResult.add(project);
                } else {
                    logger.error("项目：{}，已存在", projectName);
                    result.put(projectName, "项目已存在");
                }
            } else if (project.getName() != null) {
                String projectName = project.getName();
                logger.error("项目{}参数不完整", projectName);
                result.put(projectName, "参数不完整");
            }
        }
        if (projectListResult.size() > 0) {
            logger.info("对以上正常项目执行保存操作>>>>>>>>>>>>>>>>");
            projectRepository.saveAll(projectListResult);
            logger.info("<<<<<<<<<<<<<<<<<<<保存结束");
        }
        return result;
    }

    private JSONObject getAddress(double lon, double lat) throws IOException {
        Map<String, Object> params = new HashMap<>();
        String location = "" + lat + ',' + lon;
        params.put("location", location);
        params.put("output", "json");
        params.put("ak", AK);
        JSONObject object = HttpUtil.get(MAP_URL, params);
        if (object.get("status") != null && (Integer) object.get("status") == 0) {
            JSONObject result = (JSONObject) object.get("result");
            return (JSONObject) result.get("addressComponent");
        }
        logger.info(object.toString());
        logger.error("经纬度转地址失败");
        return null;
    }

    /**
     * 保存新项目
     *
     * @param project
     * @return
     */
    @Override
    @Transactional
    public boolean insert(Project project) {
        if (project != null &&
                project.getName() != null &&
                !"".equals(project.getName()) &&
                !projectNameExist(project.getName())) {
            project.setDelStatus(Constants.DelStatus.NORMAL.isValue());
            projectRepository.save(project);
            return true;
        }
        return false;
    }

    @Override
    public WebResponse updateData(List<DataModel> dataModelList) throws ParseException {
        List<com.giot.eco_building.entity.ProjectData> projectDataList = new ArrayList<>();
        boolean flag = false;
        Set<Long> projectIds = new HashSet<>();
        for (DataModel dataModel :
                dataModelList) {
            Integer type = null;
            Boolean isMonth = null;
            switch (dataModel.getType()) {
                case "水":
                    type = Constants.DataType.WATER.getCode();
                    break;
                case "电":
                    type = Constants.DataType.ELECTRICITY.getCode();
                    break;
                case "气":
                    type = Constants.DataType.GAS.getCode();
                    break;
            }
            switch (dataModel.getTimeType()) {
                case "年":
                    isMonth = false;
                    flag = true;
                    break;
                case "月":
                    isMonth = true;
                    break;
            }
            if (type != null && isMonth != null && dataModel.getProjectId() != null) {
                projectIds.add(dataModel.getProjectId());
                SimpleDateFormat sdf;
                if (isMonth) sdf = new SimpleDateFormat("yyyyMM");
                else sdf = new SimpleDateFormat("yyyy");
                for (Map<String, Double> map : dataModel.getDataMap()) {
                    for (String key :
                            map.keySet()) {
                        String time = key;
                        Double value = map.get(key);
                        try {
                            Date actualDate = sdf.parse(key);
                            Long projectId = dataModel.getProjectId();
                            Optional<com.giot.eco_building.entity.ProjectData> optional
                                    = projectDataRepository.findByProjectIdAndActualDateAndTypeAndIsMonth(projectId, actualDate, type, isMonth);
                            if (!optional.isPresent()) {
                                com.giot.eco_building.entity.ProjectData projectData = new com.giot.eco_building.entity.ProjectData();
                                projectData.setProjectId(projectId);
                                projectData.setIsMonth(isMonth);
                                projectData.setActualDate(actualDate);
                                projectData.setType(type);
                                projectData.setValue(value);
                                projectData.setDelStatus(Constants.DelStatus.NORMAL.isValue());
                                projectDataList.add(projectData);
                            } else {
                                com.giot.eco_building.entity.ProjectData projectData = optional.get();
                                projectData.setValue(value);
                                projectDataRepository.saveAndFlush(projectData);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            logger.error("{}数据在{}时出错", dataModel.getType(), key);
                        }
                    }
                }
            }
        }
        projectDataRepository.saveAll(projectDataList);
        if (flag) {//存在更新年数据
            List<Project> projectList = projectRepository.findAll();
            for (Project project :
                    projectList) {
//            updateProjectAddress(project);
                updateLatestYearData(project);
            }
        }
        return WebResponse.success();
    }

    /**
     * 更新项目
     * 根据项目id
     * 更新其他非空数据
     *
     * @param project
     * @return
     */
    @Override
    public WebResponse update(ProjectModel project) {
        logger.info(project.toString());
        if (project != null &&
                project.getId() != null &&
                !"".equals(project.getId())) {
            Project projectNew = project.getProject();
            Project projectOld = projectRepository.findById(project.getId()).orElse(null);
            if (projectOld != null) {
                if (projectNew.getName() != null && !"".equals(projectNew.getName())) {
                    projectOld.setName(projectNew.getName());
                }
                if (!org.apache.commons.lang3.StringUtils.isEmpty(projectNew.getProjectName())) {
                    projectOld.setProjectName(projectNew.getProjectName());
                }
                if (projectNew.getProvince() != null && !"".equals(projectNew.getProvince())) {
                    projectOld.setProvince(projectNew.getProvince());
                }
                if (projectNew.getCity() != null && !"".equals(projectNew.getCity())) {
                    projectOld.setCity(projectNew.getCity());
                }
                if (projectNew.getDistrict() != null && !"".equals(projectNew.getDistrict())) {
                    projectOld.setDistrict(projectNew.getDistrict());
                }
                /*if (projectNew.getStreet() != null && !"".equals(projectNew.getStreet())) {
                    projectOld.setStreet(projectNew.getStreet());
                }*/
                if (projectNew.getAddress() != null && !"".equals(projectNew.getAddress())) {
                    projectOld.setAddress(projectNew.getAddress());
                }
                if (projectNew.getLongitude() != null) {
                    projectOld.setLongitude(projectNew.getLongitude());
                }
                if (projectNew.getLatitude() != null) {
                    projectOld.setLatitude(projectNew.getLatitude());
                }

                if (projectNew.getArchitecturalType() != null && !"".equals(projectNew.getArchitecturalType())) {
                    projectOld.setArchitecturalType(projectNew.getArchitecturalType());
                }
                if (projectNew.getArea() != null && projectNew.getArea() > 0) {
                    projectOld.setArea(projectNew.getArea());
                }
                if (projectNew.getBuiltTime() != null) {
                    projectOld.setBuiltTime(projectNew.getBuiltTime());
                }
                if (projectNew.getFloor() != null) {
                    projectOld.setFloor(projectNew.getFloor());
                }
                if (projectNew.getImgUrl() != null && !"".equals(projectNew.getImgUrl())) {
                    projectOld.setImgUrl(projectNew.getImgUrl());
                }

                if (projectNew.getGbes() != null) {
                    projectOld.setGbes(projectNew.getGbes());
                }
                if (projectNew.getEnergySavingStandard() != null) {
                    projectOld.setEnergySavingStandard(projectNew.getEnergySavingStandard());
                }
                if (projectNew.getEnergySavingTransformationOrNot() != null) {
                    projectOld.setEnergySavingTransformationOrNot(projectNew.getEnergySavingTransformationOrNot());
                }
                if (projectNew.getHeatingMode() != null) {
                    projectOld.setHeatingMode(projectNew.getHeatingMode());
                }
                if (projectNew.getCoolingMode() != null) {
                    projectOld.setCoolingMode(projectNew.getCoolingMode());
                }
                if (projectNew.getWhetherToUseRenewableResources() != null) {
                    projectOld.setWhetherToUseRenewableResources(projectNew.getWhetherToUseRenewableResources());
                }
                if (projectNew.getShape() != null) {
                    projectOld.setShape(projectNew.getShape());
                }
                projectRepository.saveAndFlush(projectOld);
                return WebResponse.success(projectOld);
            }
        }
        return WebResponse.failure(HttpResponseStatusEnum.PROJECT_NOT_EXISTED);
    }

    @Override
    public WebResponse importExcel(MultipartFile file, HttpServletRequest request) throws IOException, ParseException {
        logger.info("start deal witn excel.....");
        Workbook wb = excelUtil.getWorkbook(file);
        if (wb != null) {
            int sheetNum = wb.getNumberOfSheets();
            logger.info("{}文件共有{}页", file.getOriginalFilename(), sheetNum);
            for (int i = 0; i < sheetNum; i++) {
                Sheet sheet = wb.getSheetAt(i);
                logger.info("开始处理第{}页数据", (i + 1));
                //获取项目基础数据和日期数据
                List<Map<Integer, Object>> projectMapList = excelUtil.dealWithSheet(sheet);
                List<Project> projects = new ArrayList<>();
                List<ProjectData> projectDataList = new ArrayList<>();
                for (Map<Integer, Object> map :
                        projectMapList) {
                    Map<String, Object> resultMap = excelUtil.mapToProject(map);
                    Project project = (Project) resultMap.get("project");
                    projects.add(project);

                    List<ProjectData> dataList = (List<ProjectData>) resultMap.get("data");
                    projectDataList.addAll(dataList);
                }
                logger.info("开始存储第{}页项目基础数据>>>>>>>>>>>>>>>>>>>", (i + 1));
                insertAll(projects);
                logger.info("<<<<<<<<<<<<<<<<第{}页项目基础数据存储结束", (i + 1));
                logger.info("开始存储第{}页项目数据>>>>>>>>>>>>>>>>>>>", (i + 1));
                projectDataService.insertAll(projectDataList);
                logger.info("<<<<<<<<<<<<<<<<第{}页项目数据存储结束", (i + 1));
                logger.info("结束处理第{}页数据", (i + 1));
            }
        }
        logger.info("end up dealing witn excel.....");
        logger.info("start updating project info");
        List<Project> projectList = projectRepository.findAll();
        for (Project project :
                projectList) {
//            updateProjectAddress(project);
            updateLatestYearData(project);
        }
        logger.info("end up updating project info");
        return WebResponse.success();
    }


    @Override
    public WebResponse importFile(MultipartFile[] files, HttpServletRequest request) throws IOException, CsvValidationException, ParseException {
        if (files.length > 0) {
            String message = "";
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                message += file.getOriginalFilename() + ":";
                //1.判断文件类型：csv、xls、xlsx-0,1,2
                if (file.getContentType().equals("text/csv") && file.getOriginalFilename().contains(".csv")) {
                    message += dealWithCsvFile(file);
                } else if (file.getOriginalFilename().contains(".xls") || file.getOriginalFilename().contains(".xlsx")) {
                    message += dealWithExcelFile(file);
                } else {
                    message += HttpResponseStatusEnum.FILE_FORMAT_ERROR.getMessage();
                    continue;
                }
                message += ";";
            }
            return WebResponse.success(message);
        } else {
            return WebResponse.failure(HttpResponseStatusEnum.EMPTY_FILE);
        }
    }

    private String dealWithExcelFile(MultipartFile file) throws IOException, ParseException {
        String message = "";
        int size = file.getOriginalFilename().split("-").length;
        boolean isData = size == 4;
        Workbook wb = excelUtil.getWorkbook(file);
        if (isData) {
            //水电气数据
            message += "水电气数据请使用.csv上传";
        } else {
            //项目数据
            if (wb != null) {
                int sheetNum = wb.getNumberOfSheets();
                logger.info("{}文件共有{}页", file.getOriginalFilename(), sheetNum);
                for (int i = 0; i < sheetNum; i++) {
                    Sheet sheet = wb.getSheetAt(i);
                    logger.info("开始处理第{}页数据", (i + 1));
                    //获取项目基础数据
                    List<Map<String, Object>> projectMapList = excelUtil.dealWithExcelSheet(sheet);
                    List<com.giot.eco_building.model.Project> projectList = toProjectList(projectMapList);
                    saveProject(projectList);
                }
                message += HttpResponseStatusEnum.SUCCESS.getMessage();
            }
        }
        return message;
    }

    private List<com.giot.eco_building.model.Project> toProjectList(List<Map<String, Object>> projectMapList) throws ParseException {
        List<com.giot.eco_building.model.Project> projectList = new ArrayList<>();
        for (Map<String, Object> map :
                projectMapList) {
            String serialNumber = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.columNames[0]))) {
                serialNumber = (String) map.get(ExcelUtil.columNames[0]);
            } else continue;

            String name = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.columNames[1]))) {
                name = (String) map.get(ExcelUtil.columNames[1]);
            }

            String projectName = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.columNames[2]))) {
                projectName = (String) map.get(ExcelUtil.columNames[2]);
            }

            String contractor = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.columNames[3]))) {
                contractor = (String) map.get(ExcelUtil.columNames[3]);
            }

            Double area = null;
            if (map.get(ExcelUtil.columNames[4]) != null) {
                area = (Double) map.get(ExcelUtil.columNames[4]);
            }

            Integer numberOfBuildings = null;
            if (map.get(ExcelUtil.columNames[5]) != null) {
                Double value = (Double) map.get(ExcelUtil.columNames[5]);
                numberOfBuildings = value.intValue();
            }

            Date builtTime = null;
            if (map.get(ExcelUtil.columNames[6]) != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String sdfTime = (String) map.get(ExcelUtil.columNames[6]);
                builtTime = sdf.parse(sdfTime);
            }

            String architecturalType = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.columNames[7]))) {
                architecturalType = (String) map.get(ExcelUtil.columNames[7]);
            }
            String province = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.columNames[8]))) {
                province = (String) map.get(ExcelUtil.columNames[8]);
            }
            String city = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.columNames[9]))) {
                city = (String) map.get(ExcelUtil.columNames[9]);
            }
            String district = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.columNames[10]))) {
                district = (String) map.get(ExcelUtil.columNames[10]);
            }
            String address = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.columNames[11]))) {
                address = (String) map.get(ExcelUtil.columNames[11]);
            }

            Double longitude = null;
            if (map.get(ExcelUtil.columNames[12]) != null) {
                longitude = (Double) map.get(ExcelUtil.columNames[12]);
            }
            Double latitude = null;
            if (map.get(ExcelUtil.columNames[13]) != null) {
                latitude = (Double) map.get(ExcelUtil.columNames[13]);
            }

            Integer floor = null;
            if (map.get(ExcelUtil.columNames[14]) != null) {
                floor = (Integer) map.get(ExcelUtil.columNames[14]);
            }
            String imgUrl = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.columNames[15]))) {
                imgUrl = (String) map.get(ExcelUtil.columNames[15]);
            }
            String gbes = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.columNames[16]))) {
                gbes = (String) map.get(ExcelUtil.columNames[16]);
            }
            String energySavingStandard = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.columNames[17]))) {
                energySavingStandard = (String) map.get(ExcelUtil.columNames[17]);
            }
            String energySavingTransformationOrNot = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.columNames[18]))) {
                energySavingTransformationOrNot = (String) map.get(ExcelUtil.columNames[18]);
            }
            String heatingMode = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.columNames[19]))) {
                heatingMode = (String) map.get(ExcelUtil.columNames[19]);
            }
            String coolingMode = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.columNames[20]))) {
                coolingMode = (String) map.get(ExcelUtil.columNames[20]);
            }
            String whetherToUseRenewableResources = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.columNames[21]))) {
                whetherToUseRenewableResources = (String) map.get(ExcelUtil.columNames[21]);
            }
            String shape = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.columNames[22]))) {
                shape = (String) map.get(ExcelUtil.columNames[22]);
            }
            com.giot.eco_building.model.Project project = new com.giot.eco_building.model.Project(serialNumber, name,
                    projectName, contractor, area, numberOfBuildings, builtTime, architecturalType, province, city, district, address,
                    latitude, longitude, floor, imgUrl, gbes, energySavingStandard, energySavingTransformationOrNot,
                    heatingMode, coolingMode, whetherToUseRenewableResources, shape);
            projectList.add(project);
        }
        return projectList;
    }

    private String dealWithCsvFile(MultipartFile file) throws IOException, CsvValidationException, ParseException {
        String message = "";
        int size = file.getOriginalFilename().split("-").length;
        boolean isData = size == 4;
        if (isData) {//水电气数据
            logger.info("开始读取文件：{}", file.getOriginalFilename());
            //1.判断项目数据是否存在
            String fileName = file.getOriginalFilename().split(".csv")[0];
            String serialNumber = fileName.substring(0, fileName.length() - 2);
            char type = fileName.charAt(fileName.length() - 1);
            Optional<Project> optional = projectRepository.findBySerialNumberAndDelStatus(serialNumber, Constants.DelStatus.NORMAL.isValue());
            if (optional.isPresent()) {
                Project project = optional.get();
                Long projectId = project.getId();
                //2.判断水电气类型
                Integer projectType;
                switch (type) {
                    case 'w':
                        projectType = Constants.DataType.WATER.getCode();
                        break;
                    case 'g':
                        projectType = Constants.DataType.GAS.getCode();
                        break;
                    default:
                        projectType = Constants.DataType.ELECTRICITY.getCode();
                        break;
                }
                //3.判断年月
                char y = serialNumber.charAt(serialNumber.length() - 1);
                boolean isYear = y == 'Y';
                //4.新增
                Map<String, Double> dataMap = readCsv(file.getInputStream());
                List<com.giot.eco_building.entity.ProjectData> dataList = new ArrayList<>();
                SimpleDateFormat sdf;
                if (isYear) {
                    sdf = new SimpleDateFormat("yyyy");
                } else {
                    if (dataMap.keySet().size() > 0) {
                        String dateString = (String) dataMap.keySet().toArray()[0];
                        if (dateString.contains("-")) {
                            sdf = new SimpleDateFormat("yyyy-MM");
                        } else {
                            sdf = new SimpleDateFormat("yyyyMM");
                        }
                        for (String date :
                                dataMap.keySet()) {
                            com.giot.eco_building.entity.ProjectData projectData = new com.giot.eco_building.entity.ProjectData();
                            projectData.setIsMonth(!isYear);
                            projectData.setProjectId(projectId);
                            projectData.setSerialNumber(serialNumber);
                            projectData.setDelStatus(Constants.DelStatus.NORMAL.isValue());

                            projectData.setType(projectType);

                            Date d = sdf.parse(date);
                            projectData.setActualDate(d);
                            projectData.setValue(dataMap.get(date));
                            dataList.add(projectData);
                        }
                    }
                }
                if (dataList.size() > 0) {
                    projectDataService.saveOrUpdateByProjectId(dataList);
                    updateLatestYearData(project);
                }
                message += HttpResponseStatusEnum.SUCCESS.getMessage();
            } else {
                message += HttpResponseStatusEnum.PROJECT_NOT_EXISTED.getMessage();
            }
        } else {//项目数据
            try {
                //1.默认使用GBK编码
                String code = "GBK";
                //2.解析csv
                InputStreamReader is = new InputStreamReader(file.getInputStream(), code);
                HeaderColumnNameMappingStrategy strategy = new HeaderColumnNameMappingStrategy();
                strategy.setType(com.giot.eco_building.model.Project.class);
                List<com.giot.eco_building.model.Project> projects = new CsvToBeanBuilder(is)
                        .withType(com.giot.eco_building.model.Project.class)
                        .withFilter(new ProjectCsvToBeanFilter())
                        .withSeparator(',')
//                    .withSkipLines(1)
                        .withMappingStrategy(strategy)
                        .withIgnoreQuotations(true)
                        .build()
                        .parse();
                //3.确定新增数据和更新数据
                saveProject(projects);
                message += HttpResponseStatusEnum.SUCCESS.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                message += e.getMessage();
            }
        }
        return message;
    }

    private void saveProject(List<com.giot.eco_building.model.Project> projects) {
        //已存在序列号数据
//        Set<String> serialNumberSet = projectRepository.findSerialNumberByDelStatus(Constants.DelStatus.NORMAL.isValue());
        Set<String> serialNumberSet = projectRepository.findSerialNumber();
        //新增序列号数据
        Set<String> serialNumberSet_n = new HashSet<>();
        //新增项目数据
        List<com.giot.eco_building.entity.Project> newProjectList = new ArrayList<>();
        //更新项目数据
        List<com.giot.eco_building.entity.Project> oldProjectList = new ArrayList<>();
        List<com.giot.eco_building.entity.Project> oldProjectListNew = new ArrayList<>();
        /*
         * 根据serialNumber
         * 对数据库中已存在数据更新，不存在数据新增
         * 在新增数据中，若出现重复serialNumer，只保留第一个出现数据
         */
        for (com.giot.eco_building.model.Project p :
                projects) {
            String serialNumber = p.getSerialNumber();
            //数据库中不存在序列号数据
            if (!serialNumberSet.contains(serialNumber)) {
                //新增数据中不存在的数据
                if (!serialNumberSet_n.contains(serialNumber)) {
                    Project project = p.getEntity();
                    newProjectList.add(project);
                    serialNumberSet_n.add(serialNumber);
                }
            } else {
                //数据库中已存在序列号数据
                oldProjectList.add(p.getEntity());
            }
        }

        //4.对数据库中已存在需要的更新的数据进行更新赋值
        for (Project project :
                oldProjectList) {
            Optional<Project> optionalProject = projectRepository.findBySerialNumber(project.getSerialNumber());
            if (optionalProject.isPresent()) {
                Project project1 = optionalProject.get();
                if (!StringUtils.isEmpty(project.getName())) {
                    project1.setName(project.getName());
                }
                if (!StringUtils.isEmpty(project.getProjectName())) {
                    project1.setProjectName(project.getProjectName());
                }
                if (!StringUtils.isEmpty(project.getContractor())) {
                    project1.setContractor(project.getContractor());
                }
                if (!StringUtils.isEmpty(project.getArea())) {
                    project1.setArea(project.getArea());
                }
                if (!StringUtils.isEmpty(project.getBuiltTime())) {
                    project1.setBuiltTime(project.getBuiltTime());
                }
                if (!StringUtils.isEmpty(project.getArchitecturalType())) {
                    project1.setArchitecturalType(project.getArchitecturalType());
                }
                if (!StringUtils.isEmpty(project.getNumberOfBuildings())) {
                    project1.setNumberOfBuildings(project.getNumberOfBuildings());
                }
                if (!StringUtils.isEmpty(project.getProvince())) {
                    project1.setProvince(project.getProvince());
                }
                if (!StringUtils.isEmpty(project.getCity())) {
                    project1.setCity(project.getCity());
                }
                if (!StringUtils.isEmpty(project.getDistrict())) {
                    project1.setDistrict(project.getDistrict());
                }
                if (!StringUtils.isEmpty(project.getAddress())) {
                    project1.setAddress(project.getAddress());
                }
                if (!StringUtils.isEmpty(project.getLongitude())) {
                    project1.setLongitude(project.getLongitude());
                }
                if (!StringUtils.isEmpty(project.getLatitude())) {
                    project1.setLatitude(project.getLatitude());
                }
                if (!StringUtils.isEmpty(project.getFloor())) {
                    project1.setFloor(project.getFloor());
                }
                if (!StringUtils.isEmpty(project.getImgUrl())) {
                    project1.setImgUrl(project.getImgUrl());
                }
                if (!StringUtils.isEmpty(project.getGbes())) {
                    project1.setGbes(project.getGbes());
                }
                if (!StringUtils.isEmpty(project.getEnergySavingStandard())) {
                    project1.setEnergySavingStandard(project.getEnergySavingStandard());
                }
                if (!StringUtils.isEmpty(project.getEnergySavingTransformationOrNot())) {
                    project1.setEnergySavingTransformationOrNot(project.getEnergySavingTransformationOrNot());
                }
                if (!StringUtils.isEmpty(project.getHeatingMode())) {
                    project1.setHeatingMode(project.getHeatingMode());
                }
                if (!StringUtils.isEmpty(project.getCoolingMode())) {
                    project1.setCoolingMode(project.getCoolingMode());
                }
                if (!StringUtils.isEmpty(project.getWhetherToUseRenewableResources())) {
                    project1.setWhetherToUseRenewableResources(project.getWhetherToUseRenewableResources());
                }
                if (!StringUtils.isEmpty(project.getShape())) {
                    project1.setShape(project.getShape());
                }
                project1.setDelStatus(project.getDelStatus());
                oldProjectListNew.add(project1);
            }
        }
        projectRepository.saveAll(newProjectList);
        projectRepository.saveAll(oldProjectListNew);
    }


    public Map<String, Double> readCsv(InputStream fis) throws IOException, CsvValidationException {
        Map<String, Double> res = new HashMap<>();
        DataInputStream in = new DataInputStream(fis);
        CSVReader csvReader = new CSVReader(new InputStreamReader(in, "UTF-8"));
        String[] strs;
        while ((strs = csvReader.readNext()) != null) {
            if (strs[0] == null || "".equals(strs[0])) continue;
            if (!strs[0].contains("-")) {
                continue;
            }
            if (strs.length == 2) {
                for (int i = 0; i < strs.length / 2; i++) {
                    String date;
                    if (strs[0 + i * 2].contains("-")) {
                        date = strs[0 + i * 2];
                    } else {
                        date = (int) Float.parseFloat(strs[0 + i * 2]) + "";
                    }
                    res.put(date, Double.valueOf(strs[1 + i * 2]));
                }
            } else {
                throw new IOException("数据列错误");
            }
        }
        csvReader.close();
        return res;
    }

    /**
     * 项目省市区街道全为空
     * 但经纬度不为空的情况下
     * 更新项目的省市区街道信息
     */
    private void updateProjectAddress(Project project) {
        if (project.getProvince() == null && project.getCity() == null
                && project.getDistrict() == null
//                && project.getStreet() == null
                && project.getLongitude() != null && project.getLatitude() != null) {
            double longitude = project.getLongitude();
            double latitude = project.getLatitude();
            try {
                JSONObject addressComponent = getAddress(longitude, latitude);
                if (addressComponent != null) {
                    String city = addressComponent.getString("city");
                    String province = addressComponent.getString("province");
                    String street = addressComponent.getString("street");
                    String district = addressComponent.getString("district");
                    project.setProvince(province);
                    project.setCity(city);
                    project.setDistrict(district);
//                    project.setStreet(street);
                    projectRepository.saveAndFlush(project);
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.info(e.getMessage());
            }
        }
    }

    /**
     * 项目省市区街道全为空
     * 但经纬度不为空的情况下
     * 更新项目的省市区街道信息
     */
    @Override
    public void reDealWithProjectAddress() {
        List<Project> projects = projectRepository.findAll();
        for (Project project :
                projects) {
            updateProjectAddress(project);
        }
    }

    /**
     * 更新项目的最近一年的水电气单位面积消耗数据
     *
     * @param project
     */
    private void updateLatestYearData(Project project) throws ParseException {
        if (project != null && project.getId() != null && project.getArea() != null && project.getArea() > 0) {
            double area = project.getArea();
            Long projectId = project.getId();
            String serialNumber = project.getSerialNumber();
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);//获取年份
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            if (serialNumber.contains("Y")) {//项目数据为年数据
                logger.info("{}为年数据", project.getName());
                Date date = sdf.parse((year - 1) + "");
//                com.giot.eco_building.entity.ProjectData waterData = projectDataService.getLatestYearData(projectId, Constants.DataType.WATER.getCode());
                com.giot.eco_building.entity.ProjectData waterData = projectDataService.getByActualDateAndIsMonthAndType(projectId, false, Constants.DataType.WATER.getCode(), date);
                if (waterData != null) {
                    project.setWaterConsumptionPerUnitArea(waterData.getValue() / area);
                }
//                com.giot.eco_building.entity.ProjectData elecData = projectDataService.getLatestYearData(projectId, Constants.DataType.ELECTRICITY.getCode());
                com.giot.eco_building.entity.ProjectData elecData = projectDataService.getByActualDateAndIsMonthAndType(projectId, false, Constants.DataType.ELECTRICITY.getCode(), date);
                if (elecData != null) {
                    project.setPowerConsumptionPerUnitArea(elecData.getValue() / area);
                }
//                com.giot.eco_building.entity.ProjectData gasData = projectDataService.getLatestYearData(projectId, Constants.DataType.GAS.getCode());
                com.giot.eco_building.entity.ProjectData gasData = projectDataService.getByActualDateAndIsMonthAndType(projectId, false, Constants.DataType.GAS.getCode(), date);
                if (gasData != null) {
                    project.setGasConsumptionPerUnitArea(gasData.getValue() / area);
                }
            } else {
                /**
                 * 项目数据为月数据
                 * 统计去年1月到12月数据
                 */
                logger.info("{}为月数据", project.getName());
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                Date start = sdf.parse((year - 1) + "");
                Date end = sdf.parse(year + "");
                Calendar date = Calendar.getInstance();
                date.setTime(end);
                date.set(Calendar.DATE, date.get(Calendar.SECOND) - 1);
                Date endDate = sdf1.parse(sdf1.format(date.getTime()));
                List<com.giot.eco_building.entity.ProjectData> waterData = projectDataService.getDataByTime(Constants.DataType.WATER.getCode(), true, projectId, start, endDate);
                project.setWaterConsumptionPerUnitArea(getPerUnitData(waterData, area));
                List<com.giot.eco_building.entity.ProjectData> elecData = projectDataService.getDataByTime(Constants.DataType.ELECTRICITY.getCode(), true, projectId, start, endDate);
                project.setPowerConsumptionPerUnitArea(getPerUnitData(elecData, area));
                List<com.giot.eco_building.entity.ProjectData> gasData = projectDataService.getDataByTime(Constants.DataType.GAS.getCode(), true, projectId, start, endDate);
                project.setGasConsumptionPerUnitArea(getPerUnitData(gasData, area));
            }
            projectRepository.saveAndFlush(project);
        }
    }

    private Double getPerUnitData(List<com.giot.eco_building.entity.ProjectData> projectDatas, double area) {
        double count = 0;
        for (com.giot.eco_building.entity.ProjectData data :
                projectDatas) {
            count += data.getValue();
        }
        return count / area;
    }

    /**
     * 在使用表导入项目数据之后
     * 更新项目的最近一年的水电气单位面积消耗数据
     */
    @Override
    public void updatelatestYearData() throws ParseException {
        List<Project> projects = projectRepository.findAll();
        for (Project project :
                projects) {
            updateLatestYearData(project);
        }
    }

    /**
     * 省-0
     * 市-1
     * 区-2
     * 街道-3
     *
     * @return
     */
    public WebResponse getAddress(Integer level, String superiorDirectory) {
        List<String> result = new ArrayList<>();
        switch (level) {
            case 0:
                result = projectRepository.findDistinctProvince();
                break;
            case 1:
                result = projectRepository.findDistinctCityByProvince(superiorDirectory);
                break;
            case 2:
                result = projectRepository.findDistinctDistrictByCity(superiorDirectory);
                break;
            /*case 3:
                result = projectRepository.findDistinctStreetByDistrict(superiorDirectory);
                break;*/
            default:
        }

        return WebResponse.success(result);
    }

    @Override
    public WebResponse getAddress() {

        List<String> provinces = projectRepository.findDistinctProvince();
        JSONArray provinceArray = new JSONArray();
        for (String province :
                provinces) {
            List<String> cities = projectRepository.findDistinctCityByProvince(province);
            JSONArray cityArray = new JSONArray();
            for (String city :
                    cities) {
                List<String> districts = projectRepository.findDistinctDistrictByCity(city);
                JSONArray districtArray = new JSONArray();
                for (String district :
                        districts) {
                    /*JSONArray streetArray = new JSONArray();
                    List<String> streets = projectRepository.findDistinctStreetByDistrict(district);
                    for (String street :
                            streets) {
                        JSONObject streetObject = new JSONObject();
                        streetObject.put("label", street);
                        streetObject.put("value", street);
                        streetArray.add(streetObject);
                    }*/
                    JSONObject districtJson = new JSONObject();
                    districtJson.put("label", district);
                    districtJson.put("value", district);
//                    districtJson.put("children", streetArray);
                    districtArray.add(districtJson);
                }
                JSONObject cityJson = new JSONObject();
                cityJson.put("label", city);
                cityJson.put("value", city);
                cityJson.put("children", districtArray);
                cityArray.add(cityJson);
            }
            JSONObject provinceJson = new JSONObject();
            provinceJson.put("label", province);
            provinceJson.put("value", province);
            provinceJson.put("children", cityArray);
            provinceArray.add(provinceJson);
        }
        return WebResponse.success(provinceArray);
    }

    private List<Project> specialQuery(String province, String city, String district,
                                       //多选
                                       String[] architecturalType, Integer[] gbes, Integer[] energySavingStandard,
                                       Integer[] energySavingTransformationOrNot, Integer[] HeatingMode, Integer[] CoolingMode,
                                       Integer[] WhetherToUseRenewableResources,
                                       //范围
                                       Double[] area, Integer[] floor, String[] date,
                                       Double[] powerConsumptionPerUnitArea, Double[] gasConsumptionPerUnitArea,
                                       Double[] waterConsumptionPerUnitArea) {
        Specification<Project> projectSpecification = (Specification<Project>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list_and = new ArrayList<>();//查询条件集
            List<Predicate> list_or = new ArrayList<>();
            list_and.add(criteriaBuilder.equal(root.get("delStatus").as(Boolean.class), Constants.DelStatus.NORMAL.isValue()));
            //1. province,  city,  district,  street
            if (province != null && !"".equals(province))
                list_and.add(criteriaBuilder.equal(root.get("province").as(String.class), province));
            if (city != null && !"".equals(city))
                list_and.add(criteriaBuilder.equal(root.get("city").as(String.class), city));
            if (district != null && !"".equals(district))
                list_and.add(criteriaBuilder.equal(root.get("district").as(String.class), district));
            /*if (street != null && !"".equals(street))
                list_and.add(criteriaBuilder.equal(root.get("street").as(String.class), street));*/
            //2. architecturalType,gbes,energySavingStandard,energySavingTransformationOrNot,
            //   HeatingMode,CoolingMode,WhetherToUseRenewableResources,
            if (architecturalType != null && architecturalType.length > 0) {
                Path<String> path = root.get("architecturalType");
                CriteriaBuilder.In<String> in = criteriaBuilder.in(path);
                for (String type :
                        architecturalType) {
                    in.value(type);
                }
                list_and.add(criteriaBuilder.and(criteriaBuilder.and(in)));
            }
            if (gbes != null && gbes.length > 0) {
                Path<Integer> path = root.get("gbes");
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(path);
                for (Integer type :
                        gbes) {
                    in.value(type);
                }
                list_and.add(criteriaBuilder.and(criteriaBuilder.and(in)));
            }
            if (energySavingStandard != null && energySavingStandard.length > 0) {
                Path<Integer> path = root.get("energySavingStandard");
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(path);
                for (Integer type :
                        energySavingStandard) {
                    in.value(type);
                }
                list_and.add(criteriaBuilder.and(criteriaBuilder.and(in)));
            }
            if (energySavingTransformationOrNot != null && energySavingTransformationOrNot.length > 0) {
                Path<Integer> path = root.get("energySavingTransformationOrNot");
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(path);
                for (Integer type :
                        energySavingTransformationOrNot) {
                    in.value(type);
                }
                list_and.add(criteriaBuilder.and(criteriaBuilder.and(in)));
            }
            if (HeatingMode != null && HeatingMode.length > 0) {
                Path<Integer> path = root.get("heatingMode");
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(path);
                for (Integer type :
                        HeatingMode) {
                    in.value(type);
                }
                list_and.add(criteriaBuilder.and(criteriaBuilder.and(in)));
            }
            if (CoolingMode != null && CoolingMode.length > 0) {
                Path<Integer> path = root.get("coolingMode");
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(path);
                for (Integer type :
                        CoolingMode) {
                    in.value(type);
                }
                list_and.add(criteriaBuilder.and(criteriaBuilder.and(in)));
            }
            if (WhetherToUseRenewableResources != null && WhetherToUseRenewableResources.length > 0) {
                Path<Integer> path = root.get("whetherToUseRenewableResources");
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(path);
                for (Integer type :
                        WhetherToUseRenewableResources) {
                    in.value(type);
                }
                list_and.add(criteriaBuilder.and(criteriaBuilder.and(in)));
            }
            //3.Double[] area, Integer[] floor, String[] date,
            //Double[] powerConsumptionPerUnitArea, Double[] gasConsumptionPerUnitArea, Double[] waterConsumptionPerUnitArea
            if (area != null) {
                if (area.length == 2) {
                    list_and.add(criteriaBuilder.between(root.get("area").as(Double.class), area[0], area[1]));
                } else if (area.length == 1) {
                    list_and.add(criteriaBuilder.greaterThanOrEqualTo(root.get("area").as(Double.class), area[0]));
                }

            }
            if (floor != null) {

                if (floor.length >= 4) {
                    if (floor[0] == 1) {
                        list_or.add(criteriaBuilder.between(root.get("floor").as(Integer.class), 1, 3));
                    }
                    if (floor[1] == 1) {
                        list_or.add(criteriaBuilder.between(root.get("floor").as(Integer.class), 4, 6));
                    }
                    if (floor[2] == 1) {
                        list_or.add(criteriaBuilder.greaterThan(root.get("floor").as(Integer.class), 7));
                    }
                    if (floor.length == 6) {
                        if (floor[3] == 1) {
                            list_or.add(criteriaBuilder.between(root.get("floor").as(Integer.class), floor[4], floor[5]));
                        }
                    }
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            Date sdate = new Date();
            Date edate = new Date();
            if (date != null) {
                Boolean flag = true;
                if (date.length == 2) {
                    try {
                        sdate = sdf.parse(date[0]);
                        edate = sdf.parse(date[1]);
                        Calendar c = Calendar.getInstance();
                        c.setTime(edate);
                        c.add(Calendar.YEAR, 1);
                        edate = c.getTime();
                    } catch (ParseException e) {
                        flag = false;
                    }
                    if (flag) list_and.add(criteriaBuilder.between(root.get("builtTime").as(Date.class), sdate, edate));
                } else if (date.length == 1) {
                    try {
                        sdate = sdf.parse(date[0]);
                    } catch (ParseException e) {
                        flag = false;
                    }
                    if (flag)
                        list_and.add(criteriaBuilder.greaterThanOrEqualTo(root.get("builtTime").as(Date.class), sdate));
                }
            }
            if (powerConsumptionPerUnitArea != null) {
                if (powerConsumptionPerUnitArea.length > 0) {
                    if (powerConsumptionPerUnitArea.length == 2) {
                        list_and.add(criteriaBuilder.between(root.get("powerConsumptionPerUnitArea"), powerConsumptionPerUnitArea[0], powerConsumptionPerUnitArea[1]));
                    } else if (powerConsumptionPerUnitArea.length == 1) {
                        list_and.add(criteriaBuilder.greaterThanOrEqualTo(root.get("powerConsumptionPerUnitArea"), powerConsumptionPerUnitArea[0]));
                    }
                    list_and.add(criteriaBuilder.isNotNull(root.get("powerConsumptionPerUnitArea")));
                }
            }
            if (gasConsumptionPerUnitArea != null) {
                if (gasConsumptionPerUnitArea.length > 0) {
                    if (gasConsumptionPerUnitArea.length == 2) {
                        list_and.add(criteriaBuilder.between(root.get("gasConsumptionPerUnitArea"), gasConsumptionPerUnitArea[0], gasConsumptionPerUnitArea[1]));
                    } else if (gasConsumptionPerUnitArea.length == 1) {
                        list_and.add(criteriaBuilder.greaterThanOrEqualTo(root.get("gasConsumptionPerUnitArea"), gasConsumptionPerUnitArea[0]));
                    }
                    list_and.add(criteriaBuilder.isNotNull(root.get("gasConsumptionPerUnitArea")));
                }
            }
            if (waterConsumptionPerUnitArea != null) {
                if (waterConsumptionPerUnitArea.length > 0) {
                    if (waterConsumptionPerUnitArea.length == 2) {
                        list_and.add(criteriaBuilder.between(root.get("waterConsumptionPerUnitArea"), waterConsumptionPerUnitArea[0], waterConsumptionPerUnitArea[1]));
                    } else if (waterConsumptionPerUnitArea.length == 1) {
                        list_and.add(criteriaBuilder.greaterThanOrEqualTo(root.get("waterConsumptionPerUnitArea"), waterConsumptionPerUnitArea[0]));
                    }
                    list_and.add(criteriaBuilder.isNotNull(root.get("waterConsumptionPerUnitArea")));
                }
            }
            Predicate[] array_and = new Predicate[list_and.size()];
            Predicate pre_and = criteriaBuilder.and(list_and.toArray(array_and));

            Predicate[] array_or = new Predicate[list_or.size()];
            Predicate pre_or = criteriaBuilder.or(list_or.toArray(array_or));
            if (array_or.length == 0) {
                criteriaQuery.where(pre_and);
            } else {
                criteriaQuery.where(pre_and, pre_or);
            }
            return criteriaQuery.getRestriction();
        };
        Sort sort = Sort.by(Sort.Direction.DESC,
                "created"); //创建时间降序排序
        List<Project> projectList = projectRepository.findAll(projectSpecification, sort);
        return projectList;

    }

    @Override
    @Cacheable(value = "project",
            key = "#province+'_'+#city+'_'+#district+'_'+#architecturalType+'_'+#gbes+'_'+#energySavingStandard+'_'+#energySavingTransformationOrNot+'_'+#heatingMode+'_'+#coolingMode+'_'+#whetherToUseRenewableResources+'_'+#area+'_'+#floor+'_'+#date+'_'+#powerConsumptionPerUnitArea+'_'+#gasConsumptionPerUnitArea+'_'+#waterConsumptionPerUnitArea")
    public WebResponse screen(String province, String city, String district,
                              //多选
                              String[] architecturalType, Integer[] gbes, Integer[] energySavingStandard,
                              Integer[] energySavingTransformationOrNot, Integer[] HeatingMode, Integer[] CoolingMode,
                              Integer[] WhetherToUseRenewableResources,
                              //范围
                              Double[] area, Integer[] floor, String[] date,
                              Double[] powerConsumptionPerUnitArea, Double[] gasConsumptionPerUnitArea,
                              Double[] waterConsumptionPerUnitArea) {
        List<Project> projectList = specialQuery(province, city, district,
                architecturalType, gbes, energySavingStandard,
                energySavingTransformationOrNot, HeatingMode, CoolingMode, WhetherToUseRenewableResources,
                area, floor, date, powerConsumptionPerUnitArea, gasConsumptionPerUnitArea, waterConsumptionPerUnitArea);

        Double waterMin = null;
        Double waterMax = (double) 0;
        Double gasMin = null;
        Double gasMax = (double) 0;
        Double elecMin = null;
        Double elecMax = (double) 0;
        for (Project project :
                projectList) {
            if (project.getGasConsumptionPerUnitArea() != null) {
                double gas = project.getGasConsumptionPerUnitArea();
                if (gasMin == null) gasMin = gas;
                if (gas < gasMin) gasMin = gas;
                if (gas > gasMax) gasMax = gas;
            }
            if (project.getWaterConsumptionPerUnitArea() != null) {
                double water = project.getWaterConsumptionPerUnitArea();
                if (waterMin == null) waterMin = water;
                if (water < waterMin) waterMin = water;
                if (water > waterMax) waterMax = water;
            }
            if (project.getPowerConsumptionPerUnitArea() != null) {
                double elec = project.getPowerConsumptionPerUnitArea();
                if (elecMin == null) elecMin = elec;
                if (elec < elecMin) elecMin = elec;
                if (elec > elecMax) elecMax = elec;
            }
        }
        if (elecMin == null) elecMin = (double) 0;
        if (waterMin == null) waterMin = (double) 0;
        if (gasMin == null) gasMin = (double) 0;
        logger.info("项目数量：{}", projectList.size());
        logger.info("水：{}-{}", waterMin, waterMax);
        logger.info("电：{}-{}", elecMin, elecMax);
        logger.info("气：{}-{}", gasMin, gasMax);
        JSONArray projectArray = JSONArray.parseArray(JSON.toJSONString(projectList));
        for (int i = 0; i < projectArray.size(); i++) {
            JSONObject object = (JSONObject) projectArray.get(i);
            if (object.get("shape") != null) {
                String shape = object.getString("shape");
                JSONArray shapeArray = new JSONArray();
                String[] locations = shape.split(";");
                for (int j = 0; j < locations.length; j++) {
                    JSONArray locaArray = new JSONArray();
                    String loca = locations[j];
                    if (loca.contains("|")) {
                        String[] ll = loca.split("\\|");
                        for (int k = 0; k < ll.length; k++) {
                            String lls = ll[k];
                            shapeArray.add(lls.split(","));
                        }
                    } else {
                        shapeArray.add(loca.split(","));
                    }
                }
                object.put("shape", shapeArray);
            }
        }
        JSONObject result = new JSONObject();
//        result.put("project", projectList);
        result.put("project", projectArray);
        result.put("water", "" + (double) Math.round(waterMin * 100) / 100 + "-" + (double) Math.round(waterMax * 100) / 100);
        result.put("gas", "" + (double) Math.round(gasMin * 100) / 100 + "-" + (double) Math.round(gasMax * 100) / 100);
        result.put("elec", "" + (double) Math.round(elecMin * 100) / 100 + "-" + (double) Math.round(elecMax * 100) / 100);
        return WebResponse.success(result);
    }

    @Override
    @Cacheable(value = "project", key = "#projectId")
    public WebResponse projectDetail(Long projectId) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (projectOptional.isPresent()) {
            return WebResponse.success(projectOptional.get());
        } else {
            return WebResponse.failure(HttpResponseStatusEnum.PROJECT_NOT_EXISTED);
        }
    }

    @Override
    public WebResponse importFile(MultipartFile file, boolean isData, HttpServletRequest request) throws IOException, CsvValidationException, ParseException {
        String fileName = file.getOriginalFilename();
        String extString = fileName.substring(fileName.lastIndexOf('.'));
        /*if (file.getContentType().equals("text/csv") && ".csv".equals(extString)) {
            return importCsv(file, isData, request);
        } else */
        if (".xls".equals(extString) || ".xlsx".equals(extString)) {
            return importExcel(file, request);
        } else {
            return WebResponse.failure(HttpResponseStatusEnum.FILE_FORMAT_ERROR);
        }
    }

    @Override
    public void insertShape() {
        List<Project> projectList = projectRepository.findAll();
        for (Project pro :
                projectList) {
            String poiId;
            String shape = null;
            if (StringUtils.isEmpty(pro.getPoiId())) {
                poiId = mapService.getPoiId(pro);
                if (!StringUtils.isEmpty(poiId)) {
                    pro.setPoiId(poiId);
                }
            } else {
                poiId = pro.getPoiId();
            }
            if (StringUtils.isEmpty(pro.getShape()) && !StringUtils.isEmpty(poiId)) {
                logger.info("开始更新：{}的shape", pro.getName());
                if (!StringUtils.isEmpty(poiId)) {
                    try {
                        shape = mapService.getDistrictLocation(poiId);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!StringUtils.isEmpty(shape)) {
                        pro.setShape(shape);
                        projectRepository.saveAndFlush(pro);
                        long sleepTime = 60000 + (long) (Math.random() * 30000);
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public WebResponse JiangSuTop5() {
        List<Object[]> list = projectRepository.findCityCountByProvinceAndDelStatus("江苏省", Constants.DelStatus.NORMAL.isValue());
        List<Map<String, Object>> cityCounts = new ArrayList<>();
        for (Object[] objects :
                list) {
            Map<String, Object> cityCount = new HashMap<>();
            for (Object object : objects) {
                if (object instanceof String) {
                    String city = (String) object;
                    cityCount.put("name", city);
                }
                if (object instanceof BigInteger) {
                    Long count = ((BigInteger) object).longValue();
                    cityCount.put("value", count);
                }
            }
            cityCounts.add(cityCount);
        }
        return WebResponse.success(cityCounts);
    }

    @Override
    public WebResponse energySort() {
        List<Project> projectList = projectRepository.findByDelStatus(Constants.DelStatus.NORMAL.isValue());
        List<Project> modelList = new ArrayList<>();
        String[] types = {"办公", "商场", "文化教育", "餐饮", "医院", "酒店", "其他"};
        for (int i = 0; i < types.length; i++) {
            String type = types[i];
            List<Project> projectListByType = new ArrayList<>();
            for (Project project :
                    projectList) {
                if (StringUtils.isEmpty(project.getArchitecturalType())) {
                    continue;
                } else {
                    if (type.equals(project.getArchitecturalType())) {
                        projectListByType.add(project);
                    }
                }
            }
            for (int j = 0; j < projectListByType.size() - 1; j++) {
                for (int k = j + 1; k < projectListByType.size(); k++) {
                    Project project1 = projectListByType.get(j);
                    Project project2 = projectListByType.get(k);
                    Double pcpua1 = project1.getPowerConsumptionPerUnitArea();
                    Double pcpua2 = project2.getPowerConsumptionPerUnitArea();
                    if (pcpua1 == null && pcpua2 != null) {
                        Collections.swap(projectListByType, j, k);
                        continue;
                    }
                    if (pcpua1 != null && pcpua2 != null && pcpua2 > pcpua1) {
                        Collections.swap(projectListByType, j, k);
                        continue;
                    }
                }
            }
            modelList.addAll(projectListByType);
        }
        List<EnergySortModel> resList = new ArrayList<>();
        for (Project project :
                modelList) {
            EnergySortModel model = new EnergySortModel();
            model.setName(project.getSerialNumber());
            model.setType(project.getArchitecturalType());
            model.setValue(project.getPowerConsumptionPerUnitArea());
            resList.add(model);
        }
        return WebResponse.success(resList);
    }

    @Override
    public WebResponse statistic() {
        String sql = "SELECT p.architectural_type,COUNT(p.architectural_type),SUM(numberOfBuildings)\n" +
                "FROM project p GROUP BY p.architectural_type ORDER BY architectural_type DESC;";
        EntityManager em = entityManagerFactory.getNativeEntityManagerFactory().createEntityManager();
        Query nativeQuery = em.createNativeQuery(sql.toString());
        @SuppressWarnings({"unused", "unchecked"})
        List<Object[]> result = nativeQuery.getResultList();
        //关闭entityManagerFactory
        em.close();
        JSONArray mapRes = new JSONArray();
        for (Object[] objects :
                result) {
            if (objects.length == 3) {
                String type = (String) objects[0];
                if (!StringUtils.isEmpty(type)) {
                    BigInteger value = (BigInteger) objects[1];
                    BigDecimal sum = (BigDecimal) objects[2];
                    JSONObject jsonObject = new JSONObject();
                    if (type.equals("其他")) {
                        type = "其他公建";
                    }
                    jsonObject.put("type", type);
                    jsonObject.put("number", value);
                    jsonObject.put("sum", sum);
                    mapRes.add(jsonObject);
                }
            }
        }
        return WebResponse.success(mapRes);
    }


    @Override
    public WebResponse JiangSuElecTop10() {
        List<Project> list = projectRepository.findByProvinceAndDelStatusAndOrderByPowerConsumptionPerUnitAreaDescAndLimit10("江苏省", Constants.DelStatus.NORMAL.isValue());
        return WebResponse.success(list);
    }

    @Override
    public WebResponse uploadPic(MultipartFile file, HttpServletRequest request) throws IOException {
        if (null == file || file.isEmpty()) {
            return WebResponse.exception(new FileNotFoundException("提交图片未获取到"));
        }
        String fileName = file.getOriginalFilename();
        logger.info("上传的文件名为：" + fileName);
        // 获取文件的后缀名
        if (StringUtils.isEmpty(fileName)) {
            return WebResponse.exception(new Exception("fileName为空值，上传过程异常。"));
        }
        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        ImageCheck ic = new ImageCheck();
        boolean flag = ic.isImage(toFile);
        if (flag) {
            String imgUrl = uploadService.uploadProjectImg(file.getBytes());
            return WebResponse.success(imgUrl);
        } else return WebResponse.failure(HttpResponseStatusEnum.FILE_FORMAT_ERROR);
    }

    @Override
    @Cacheable(value = "project", key = "#name+'_'+#province+'_'+#city+'_'+#district+'_'+#architecturalType+'_'+#number+'_'+#size")
    public WebResponse page(String name, String province, String city, String district, String architecturalType, Integer number, Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC,
                "lastModified"); //最新修改时间降序排序
        Pageable pageable = PageRequest.of(number - 1, size, sort);
        Specification<Project> projectSpecification = (Specification<Project>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();//查询条件集
            if (name != null && !"".equals(name)) {
                list.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + name + "%"));
            }
            if (province != null && !"".equals(province))
                list.add(criteriaBuilder.equal(root.get("province").as(String.class), province));
            if (city != null && !"".equals(city))
                list.add(criteriaBuilder.equal(root.get("city").as(String.class), city));
            if (district != null && !"".equals(district))
                list.add(criteriaBuilder.equal(root.get("district").as(String.class), district));
            /*if (street != null && !"".equals(street))
                list.add(criteriaBuilder.equal(root.get("street").as(String.class), street));*/
            if (architecturalType != null && !"".equals(architecturalType)) {
                list.add(criteriaBuilder.equal(root.get("architecturalType").as(String.class), architecturalType));
            }
            list.add(criteriaBuilder.equal(root.get("delStatus").as(Boolean.class), Constants.DelStatus.NORMAL.isValue()));
            return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
        };
        Page<Project> projectPage = projectRepository.findAll(projectSpecification, pageable);
        return WebResponse.success(projectPage.getContent(), projectPage.getTotalPages(), projectPage.getTotalElements());
    }

    @Override
    public WebResponse deleteById(Long id) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            if (!project.getDelStatus()) {
                project.setDelStatus(Constants.DelStatus.DELETE.isValue());
                projectRepository.saveAndFlush(project);
                return WebResponse.success();
            }
        }
        return WebResponse.failure(HttpResponseStatusEnum.PROJECT_NOT_EXISTED);
    }

    /**
     * @param dataType:水/电/气
     * @param timeType：年/月
     * @param start
     * @param end
     * @return
     */
    @Override
    @Cacheable(value = "project", key = "#dataType+'_'+#timeType+'_'+#projectId+'_'+#start+'_'+#end")
    public WebResponse getDataByTime(String dataType, String timeType, Long projectId, String start, String end) {
        Integer type = null;
        switch (dataType) {
            case "水":
                type = Constants.DataType.WATER.getCode();
                break;
            case "电":
                type = Constants.DataType.ELECTRICITY.getCode();
                break;
            case "气":
                type = Constants.DataType.GAS.getCode();
                break;
        }
        Boolean isMonth = null;
        switch (timeType) {
            case "年":
                isMonth = false;
                break;
            case "月":
                isMonth = true;
                break;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date sdate = new Date();
        Date edate = new Date();
        boolean flag = false;
        if (start != null && end != null) {
            flag = true;
            try {
                sdate = sdf.parse(start);
                edate = sdf.parse(end);
                Calendar c = Calendar.getInstance();
                c.setTime(edate);
                c.add(Calendar.DAY_OF_MONTH, 1);
                edate = c.getTime();
            } catch (ParseException e) {
                flag = false;
            }
        }
        if (type != null && isMonth != null && flag) {
            List<com.giot.eco_building.entity.ProjectData> projectData = projectDataService.getDataByTime(type, isMonth, projectId, sdate, edate);
            return WebResponse.success(projectData);
        }
        return WebResponse.failure(HttpResponseStatusEnum.PARAM_ERROR);
    }

    @Override
    public void downloadExample(HttpServletRequest request, HttpServletResponse response) {

        response.setCharacterEncoding("UTF-8");
        //设置ContentType字段值
        response.setContentType("text/html;charset=utf-8");
        //通知浏览器以下载的方式打开
        response.addHeader("Content-type", "appllication/octet-stream");
        response.addHeader("Content-Disposition", "attachment;filename=" + "example.xlsx");
        String filePath = "D:\\eco_building\\示例表.xlsx";
        File file = new File(filePath);
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            FileInputStream fis = new FileInputStream(file);
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = fis.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            fis.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] getBytesByFile(String filePath) {
        try {
            File file = new File(filePath);
            //获取输入流
            FileInputStream fis = new FileInputStream(file);

            //新的 byte 数组输出流，缓冲区容量1024byte
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            //缓存
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            //改变为byte[]
            byte[] data = bos.toByteArray();
            //
            bos.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}