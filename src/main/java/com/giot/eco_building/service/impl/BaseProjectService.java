package com.giot.eco_building.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.constant.Constants;
import com.giot.eco_building.constant.HttpResponseStatusEnum;
import com.giot.eco_building.entity.Project;
import com.giot.eco_building.model.ProjectData;
import com.giot.eco_building.repository.ProjectRepository;
import com.giot.eco_building.service.ProjectDataService;
import com.giot.eco_building.service.ProjectService;
import com.giot.eco_building.utils.ExcelUtil;
import com.giot.eco_building.utils.HttpUtil;
import com.giot.eco_building.utils.UpdateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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


    private ExcelUtil excelUtil;

    private ProjectDataService projectDataService;

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

    /**
     * 检查项目名是否已存在
     * true-存在
     * false-不存在
     *
     * @param name
     * @return
     */
    private boolean projectNameExist(String name) {
        Project project = projectRepository.findByName(name);
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

    /**
     * 更新项目
     * 根据项目id
     * 更新其他非空数据
     *
     * @param project
     * @return
     */
    @Override
    @Transactional
    public boolean update(Project project) {
        if (project != null &&
                project.getId() != null &&
                !"".equals(project.getId())) {
            Project projectOld = projectRepository.findById(project.getId()).orElse(null);
            if (projectOld != null) {
                UpdateUtil.copyNullProperties(projectOld, project);
                projectRepository.saveAndFlush(project);
                return true;
            }
        }
        return false;
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
        return WebResponse.success();
    }

    @Override
    public void reDealWithProjectAddress() {
        List<Project> projects = projectRepository.findAll();
        for (Project project :
                projects) {
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
                }

            }
        }
    }

    @Override
    public void latestYearData() {
        List<Project> projects = projectRepository.findAll();
        for (Project project :
                projects) {
            if (project.getArea() != null && project.getArea() > 0) {
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
    }

    /**
     * 省-0
     * 市-1
     * 区-2
     * 街道-3
     *
     * @return
     */
    @Override
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
            if (powerConsumptionPerUnitArea != null && powerConsumptionPerUnitArea.length == 2) {
                if (powerConsumptionPerUnitArea.length == 2) {
                    list.add(criteriaBuilder.between(root.get("powerConsumptionPerUnitArea"), powerConsumptionPerUnitArea[0], powerConsumptionPerUnitArea[1]));
                } else if (powerConsumptionPerUnitArea.length == 1) {
                    list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("powerConsumptionPerUnitArea"), powerConsumptionPerUnitArea[0]));
                }
            }
            if (gasConsumptionPerUnitArea != null) {
                if (gasConsumptionPerUnitArea.length == 2) {
                    list.add(criteriaBuilder.between(root.get("gasConsumptionPerUnitArea"), gasConsumptionPerUnitArea[0], gasConsumptionPerUnitArea[1]));
                } else if (gasConsumptionPerUnitArea.length == 1) {
                    list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("gasConsumptionPerUnitArea"), gasConsumptionPerUnitArea[0]));
                }
            }
            if (waterConsumptionPerUnitArea != null) {
                if (waterConsumptionPerUnitArea.length == 2) {
                    list.add(criteriaBuilder.between(root.get("waterConsumptionPerUnitArea"), waterConsumptionPerUnitArea[0], waterConsumptionPerUnitArea[1]));
                } else if (waterConsumptionPerUnitArea.length == 1) {
                    list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("waterConsumptionPerUnitArea"), waterConsumptionPerUnitArea[0]));
                }
            }
            return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
        };
        List<Project> projectList = projectRepository.findAll(projectSpecification);
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
}
