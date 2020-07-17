package com.giot.eco_building.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.constant.Constants;
import com.giot.eco_building.constant.HttpResponseStatusEnum;
import com.giot.eco_building.entity.Project;
import com.giot.eco_building.model.CityCount;
import com.giot.eco_building.model.DataModel;
import com.giot.eco_building.model.ProjectData;
import com.giot.eco_building.model.ProjectModel;
import com.giot.eco_building.repository.ProjectDataRepository;
import com.giot.eco_building.repository.ProjectRepository;
import com.giot.eco_building.service.ProjectDataService;
import com.giot.eco_building.service.ProjectService;
import com.giot.eco_building.service.UploadService;
import com.giot.eco_building.utils.ExcelUtil;
import com.giot.eco_building.utils.HttpUtil;
import com.giot.eco_building.utils.ImageCheck;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
        Project project = projectRepository.findByNameAndDelStatus(name, Constants.DelStatus.NORMAL.isValue());
        return (project != null);
    }

    /**
     * 插入项目基础数据-根据项目名判断是否已存在：
     * -不存在项目--直接存储
     * -已存在项目--基础数据丢弃
     *
     * @param projectList
     */
    @Transactional
    public void insertAll(List<Project> projectList) {
        Set<String> projectNames = new HashSet<>();
        List<Project> projectListResult = new ArrayList<>();
        for (Project project : projectList) {
            if (project != null && project.getName() != null) {
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
                }
            } else {
                logger.error("项目参数不完整（项目名不存在）");
            }
        }
        if (projectListResult.size() > 0) {
            logger.info("对以上正常项目执行保存操作>>>>>>>>>>>>>>>>");
            projectRepository.saveAll(projectListResult);
            logger.info("<<<<<<<<<<<<<<<<<<<保存结束");
        }
    }

    /**
     * 根据项目地址
     * 获取项目所在经纬度
     *
     * @param
     * @return
     */
    /*@Deprecated
    private void setLocation(Project project) throws IOException {
        String address = project.getAddress();
        if ((project.getLongitude() == null || project.getLatitude() == null)
                && !"".equals(address)) {
            Map<String, Object> params = new HashMap<>();
            params.put("address", address);
            params.put("output", "json");
            params.put("ak", AK);
            JSONObject object = HttpUtil.get(MAP_URL, params);
            int status = object.getInteger("status");
            if (status == 0) {
                JSONObject result = object.getJSONObject("result");
                JSONObject location = result.getJSONObject("location");
                float lng = location.getFloat("lng");
                float lat = location.getFloat("lat");
                project.setLongitude(lng);
                project.setLatitude(lat);
            }
        }
    }*/
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
    public WebResponse updateData(List<DataModel> dataModelList) {
        List<com.giot.eco_building.entity.ProjectData> projectDataList = new ArrayList<>();
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
                    break;
                case "月":
                    isMonth = true;
                    break;
            }
            if (type != null && isMonth != null && dataModel.getProjectId() != null) {
                SimpleDateFormat sdf = null;
                if (isMonth) sdf = new SimpleDateFormat("yyyy-MM");
                else sdf = new SimpleDateFormat("yyyy");
                for (Map<String, Double> map : dataModel.getDataMap()) {
                    for (String key :
                            map.keySet()) {
                        String time = key;
                        Double value = map.get(key);
                        try {
                            Date actualDate = sdf.parse(key);
                            com.giot.eco_building.entity.ProjectData projectData = new com.giot.eco_building.entity.ProjectData();
                            projectData.setProjectId(dataModel.getProjectId());
                            projectData.setIsMonth(isMonth);
                            projectData.setActualDate(actualDate);
                            projectData.setType(type);
                            projectData.setValue(value);
                            projectData.setDelStatus(Constants.DelStatus.NORMAL.isValue());
                            projectDataList.add(projectData);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            logger.error("{}数据在{}时出错", dataModel.getType(), key);
                        }
                    }
                }
            }
        }
        List<com.giot.eco_building.entity.ProjectData> result = projectDataRepository.saveAll(projectDataList);
        return WebResponse.success(result);
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
                if (projectNew.getProvince() != null && !"".equals(projectNew.getProvince())) {
                    projectOld.setProvince(projectNew.getProvince());
                }
                if (projectNew.getCity() != null && !"".equals(projectNew.getCity())) {
                    projectOld.setCity(projectNew.getCity());
                }
                if (projectNew.getDistrict() != null && !"".equals(projectNew.getDistrict())) {
                    projectOld.setDistrict(projectNew.getDistrict());
                }
                if (projectNew.getStreet() != null && !"".equals(projectNew.getStreet())) {
                    projectOld.setStreet(projectNew.getStreet());
                }
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
            updateProjectAddress(project);
            updateLatestYearData(project);
        }
        logger.info("end up updating project info");
        return WebResponse.success();
    }

    /**
     * 项目省市区街道全为空
     * 但经纬度不为空的情况下
     * 更新项目的省市区街道信息
     */
    private void updateProjectAddress(Project project) {
        if (project.getProvince() == null && project.getCity() == null
                && project.getDistrict() == null && project.getStreet() == null
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
                    project.setStreet(street);
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
    private void updateLatestYearData(Project project) {
        if (project != null && project.getId() != null && project.getArea() != null && project.getArea() > 0) {
            double area = project.getArea();
            com.giot.eco_building.entity.ProjectData waterData = projectDataService.getLatestYearData(project.getId(), Constants.DataType.WATER.getCode());
            if (waterData != null) {
                project.setWaterConsumptionPerUnitArea(waterData.getValue() / area);
            }
            com.giot.eco_building.entity.ProjectData elecData = projectDataService.getLatestYearData(project.getId(), Constants.DataType.ELECTRICITY.getCode());
            if (elecData != null) {
                project.setPowerConsumptionPerUnitArea(elecData.getValue() / area);
            }
            com.giot.eco_building.entity.ProjectData gasData = projectDataService.getLatestYearData(project.getId(), Constants.DataType.GAS.getCode());
            if (gasData != null) {
                project.setGasConsumptionPerUnitArea(gasData.getValue() / area);
            }
            projectRepository.saveAndFlush(project);
        }
    }

    /**
     * 在使用表导入项目数据之后
     * 更新项目的最近一年的水电气单位面积消耗数据
     */
    @Override
    public void latestYearData() {
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
            case 3:
                result = projectRepository.findDistinctStreetByDistrict(superiorDirectory);
                break;
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
                    JSONArray streetArray = new JSONArray();
                    List<String> streets = projectRepository.findDistinctStreetByDistrict(district);
                    for (String street :
                            streets) {
                        JSONObject streetObject = new JSONObject();
                        streetObject.put("label", street);
                        streetObject.put("value", street);
                        streetArray.add(streetObject);
                    }
                    JSONObject districtJson = new JSONObject();
                    districtJson.put("label", district);
                    districtJson.put("value", district);
                    districtJson.put("children", streetArray);
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

    private List<Project> specialQuery(String province, String city, String district, String street,
                                       //多选
                                       String[] architecturalType, Integer[] gbes, Integer[] energySavingStandard,
                                       Integer[] energySavingTransformationOrNot, Integer[] HeatingMode, Integer[] CoolingMode,
                                       Integer[] WhetherToUseRenewableResources,
                                       //范围
                                       Double[] area, Integer[] floor, String[] date,
                                       Double[] powerConsumptionPerUnitArea, Double[] gasConsumptionPerUnitArea,
                                       Double[] waterConsumptionPerUnitArea) {
        Specification<Project> projectSpecification = (Specification<Project>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();//查询条件集
            //1. province,  city,  district,  street
            if (province != null && !"".equals(province))
                list.add(criteriaBuilder.equal(root.get("province").as(String.class), province));
            if (city != null && !"".equals(city))
                list.add(criteriaBuilder.equal(root.get("city").as(String.class), city));
            if (district != null && !"".equals(district))
                list.add(criteriaBuilder.equal(root.get("district").as(String.class), district));
            if (street != null && !"".equals(street))
                list.add(criteriaBuilder.equal(root.get("street").as(String.class), street));
            //2. architecturalType,gbes,energySavingStandard,energySavingTransformationOrNot,
            //   HeatingMode,CoolingMode,WhetherToUseRenewableResources,
            if (architecturalType != null && architecturalType.length > 0) {
                Path<String> path = root.get("architecturalType");
                CriteriaBuilder.In<String> in = criteriaBuilder.in(path);
                for (String type :
                        architecturalType) {
                    in.value(type);
                }
                list.add(criteriaBuilder.and(criteriaBuilder.and(in)));
            }
            if (gbes != null && gbes.length > 0) {
                Path<Integer> path = root.get("gbes");
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(path);
                for (Integer type :
                        gbes) {
                    in.value(type);
                }
                list.add(criteriaBuilder.and(criteriaBuilder.and(in)));
            }
            if (energySavingStandard != null && energySavingStandard.length > 0) {
                Path<Integer> path = root.get("energySavingStandard");
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(path);
                for (Integer type :
                        energySavingStandard) {
                    in.value(type);
                }
                list.add(criteriaBuilder.and(criteriaBuilder.and(in)));
            }
            if (energySavingTransformationOrNot != null && energySavingTransformationOrNot.length > 0) {
                Path<Integer> path = root.get("energySavingTransformationOrNot");
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(path);
                for (Integer type :
                        energySavingTransformationOrNot) {
                    in.value(type);
                }
                list.add(criteriaBuilder.and(criteriaBuilder.and(in)));
            }
            if (HeatingMode != null && HeatingMode.length > 0) {
                Path<Integer> path = root.get("HeatingMode");
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(path);
                for (Integer type :
                        HeatingMode) {
                    in.value(type);
                }
                list.add(criteriaBuilder.and(criteriaBuilder.and(in)));
            }
            if (CoolingMode != null && CoolingMode.length > 0) {
                Path<Integer> path = root.get("CoolingMode");
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(path);
                for (Integer type :
                        CoolingMode) {
                    in.value(type);
                }
                list.add(criteriaBuilder.and(criteriaBuilder.and(in)));
            }
            if (WhetherToUseRenewableResources != null && WhetherToUseRenewableResources.length > 0) {
                Path<Integer> path = root.get("WhetherToUseRenewableResources");
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(path);
                for (Integer type :
                        WhetherToUseRenewableResources) {
                    in.value(type);
                }
                list.add(criteriaBuilder.and(criteriaBuilder.and(in)));
            }
            //3.Double[] area, Integer[] floor, String[] date,
            //Double[] powerConsumptionPerUnitArea, Double[] gasConsumptionPerUnitArea, Double[] waterConsumptionPerUnitArea
            if (area != null) {
                if (area.length == 2) {
                    list.add(criteriaBuilder.between(root.get("area"), area[0], area[1]));
                } else if (area.length == 1) {
                    list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("area"), area[0]));
                }

            }
            if (floor != null) {
                if (floor.length == 2) {
                    list.add(criteriaBuilder.between(root.get("floor"), floor[0], floor[1]));
                } else if (floor.length == 1) {
                    list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("floor"), floor[0]));
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
                    if (flag) list.add(criteriaBuilder.between(root.get("builtTime"), sdate, edate));
                } else if (date.length == 1) {
                    try {
                        sdate = sdf.parse(date[0]);
                    } catch (ParseException e) {
                        flag = false;
                    }
                    if (flag) list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("builtTime"), sdate));
                }
            }
            if (powerConsumptionPerUnitArea != null) {
                if (powerConsumptionPerUnitArea.length == 2) {
                    list.add(criteriaBuilder.between(root.get("powerConsumptionPerUnitArea"), powerConsumptionPerUnitArea[0], powerConsumptionPerUnitArea[1]));
                } else if (powerConsumptionPerUnitArea.length == 1) {
                    list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("powerConsumptionPerUnitArea"), powerConsumptionPerUnitArea[0]));
                }
                list.add(criteriaBuilder.notEqual(root.get("powerConsumptionPerUnitArea"), null));
            }
            if (gasConsumptionPerUnitArea != null) {
                if (gasConsumptionPerUnitArea.length == 2) {
                    list.add(criteriaBuilder.between(root.get("gasConsumptionPerUnitArea"), gasConsumptionPerUnitArea[0], gasConsumptionPerUnitArea[1]));
                } else if (gasConsumptionPerUnitArea.length == 1) {
                    list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("gasConsumptionPerUnitArea"), gasConsumptionPerUnitArea[0]));
                }
                list.add(criteriaBuilder.notEqual(root.get("gasConsumptionPerUnitArea"), null));
            }
            if (waterConsumptionPerUnitArea != null) {
                if (waterConsumptionPerUnitArea.length == 2) {
                    list.add(criteriaBuilder.between(root.get("waterConsumptionPerUnitArea"), waterConsumptionPerUnitArea[0], waterConsumptionPerUnitArea[1]));
                } else if (waterConsumptionPerUnitArea.length == 1) {
                    list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("waterConsumptionPerUnitArea"), waterConsumptionPerUnitArea[0]));
                }
                list.add(criteriaBuilder.notEqual(root.get("waterConsumptionPerUnitArea"), null));
            }
            list.add(criteriaBuilder.equal(root.get("delStatus"), Constants.DelStatus.NORMAL.isValue()));
            return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
        };
        List<Project> projectList = projectRepository.findAll(projectSpecification);
        return projectList;

    }

    @Override
    public WebResponse screen(String province, String city, String district, String street,
                              //多选
                              String[] architecturalType, Integer[] gbes, Integer[] energySavingStandard,
                              Integer[] energySavingTransformationOrNot, Integer[] HeatingMode, Integer[] CoolingMode,
                              Integer[] WhetherToUseRenewableResources,
                              //范围
                              Double[] area, Integer[] floor, String[] date,
                              Double[] powerConsumptionPerUnitArea, Double[] gasConsumptionPerUnitArea,
                              Double[] waterConsumptionPerUnitArea) {
        List<Project> projectList = specialQuery(province, city, district, street,
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
        JSONObject result = new JSONObject();
        result.put("project", projectList);
        result.put("water", "" + waterMin + "-" + waterMax);
        result.put("gas", "" + gasMin + "-" + gasMax);
        result.put("elec", "" + elecMin + "-" + elecMax);
        return WebResponse.success(result);
    }

    @Override
    public WebResponse projectDetail(Long projectId) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (projectOptional.isPresent()) {
            return WebResponse.success(projectOptional.get());
        } else {
            return WebResponse.failure(HttpResponseStatusEnum.PROJECT_NOT_EXISTED);
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
    public WebResponse page(String name, String province, String city, String district, String street, String architecturalType, Integer number, Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC,
                "lastModified"); //创建时间降序排序
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
            if (street != null && !"".equals(street))
                list.add(criteriaBuilder.equal(root.get("street").as(String.class), street));
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