package com.giot.eco_building.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSONObject;
import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.constant.HttpResponseStatusEnum;
import com.giot.eco_building.entity.*;
import com.giot.eco_building.model.ReseachProjectModel;
import com.giot.eco_building.model.ResearchProjectDataModel;
import com.giot.eco_building.repository.*;
import com.giot.eco_building.service.ResearchProjectService;
import com.giot.eco_building.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-11-17
 * Time: 9:50
 */
@Service
@Transactional
@Component("BaseResearchProjectService")
public class BaseResearchProjectService implements ResearchProjectService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ExcelUtil excelUtil;

    private ReseachProjectRepository reseachProjectRepository;
    private AirConditionBoxRepository airConditionBoxRepository;
    private CoolingTowerRepository coolingTowerRepository;
    private ElectricalLoadRepository electricalLoadRepository;
    private LightingEquipmentRepository lightingEquipmentRepository;
    private MainEngineRepository mainEngineRepository;
    private TerminalEquipmentRepository terminalEquipmentRepository;
    private WaterPumpRepository waterPumpRepository;
    private ResearchProjectDataRepository researchProjectDataRepository;

    @Autowired
    public void setResearchProjectDataRepository(ResearchProjectDataRepository researchProjectDataRepository) {
        this.researchProjectDataRepository = researchProjectDataRepository;
    }

    @Autowired
    public void setElectricalLoadRepository(ElectricalLoadRepository electricalLoadRepository) {
        this.electricalLoadRepository = electricalLoadRepository;
    }

    @Autowired
    public void setLightingEquipmentRepository(LightingEquipmentRepository lightingEquipmentRepository) {
        this.lightingEquipmentRepository = lightingEquipmentRepository;
    }

    @Autowired
    public void setMainEngineRepository(MainEngineRepository mainEngineRepository) {
        this.mainEngineRepository = mainEngineRepository;
    }

    @Autowired
    public void setTerminalEquipmentRepository(TerminalEquipmentRepository terminalEquipmentRepository) {
        this.terminalEquipmentRepository = terminalEquipmentRepository;
    }

    @Autowired
    public void setWaterPumpRepository(WaterPumpRepository waterPumpRepository) {
        this.waterPumpRepository = waterPumpRepository;
    }

    @Autowired
    public void setAirConditionBoxRepository(AirConditionBoxRepository airConditionBoxRepository) {
        this.airConditionBoxRepository = airConditionBoxRepository;
    }

    @Autowired
    public void setCoolingTowerRepository(CoolingTowerRepository coolingTowerRepository) {
        this.coolingTowerRepository = coolingTowerRepository;
    }

    @Autowired
    public void setReseachProjectRepository(ReseachProjectRepository reseachProjectRepository) {
        this.reseachProjectRepository = reseachProjectRepository;
    }

    @Autowired
    public void setExcelUtil(ExcelUtil excelUtil) {
        this.excelUtil = excelUtil;
    }

    @Override
    public WebResponse importFile(MultipartFile[] files, HttpServletRequest request) throws IOException, ParseException {
        if (files.length > 0) {
            String message = "";
            for (MultipartFile file :
                    files) {
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

    @Override
    public List<ReseachProject> list(String name, Boolean isItSuggestedToTransform) {
        if (null == isItSuggestedToTransform) {
            return reseachProjectRepository.findByNameLikeAndDelStatus("%" + name + "%", false);
        } else {
            return reseachProjectRepository.findByNameLikeAndIsItSuggestedToTransformAndDelStatus("%" + name + "%", isItSuggestedToTransform, false);
        }
    }

    @Override
    public ReseachProject findById(Long id) {
        Optional<ReseachProject> optional = reseachProjectRepository.findById(id);
        if (optional.isPresent() && !optional.get().getDelStatus()) {
            return optional.get();
        }
        return null;
    }

    @Override
    public JSONObject getHVACEquipmentById(Long id) {
        JSONObject result = new JSONObject();
        Optional<ReseachProject> optional = reseachProjectRepository.findById(id);
        if (optional.isPresent() && !optional.get().getDelStatus()) {
            ReseachProject reseachProject = optional.get();
            String serialNumer = reseachProject.getSerialNumber();

            List<MainEngine> mainEngineList = mainEngineRepository.findBySerialNumber(serialNumer);
            JSONObject mainEngine = new JSONObject();
            mainEngine.put("photo", reseachProject.getHostPhotoUrl());
            mainEngine.put("data", mainEngineList);
            result.put("mainEngine", mainEngine);

            List<WaterPump> waterPumpList = waterPumpRepository.findBySerialNumber(serialNumer);
            JSONObject waterPump = new JSONObject();
            waterPump.put("photo", reseachProject.getWaterPumpPhotoUrl());
            waterPump.put("data", waterPumpList);
            result.put("waterPump", waterPump);

            List<CoolingTower> coolingTowerList = coolingTowerRepository.findBySerialNumber(serialNumer);
            JSONObject coolingTower = new JSONObject();
            coolingTower.put("photo", reseachProject.getCoolingTowerPhotoUrl());
            coolingTower.put("data", coolingTowerList);
            result.put("coolingTower", coolingTower);

            List<AirConditionBox> airConditionBoxList = airConditionBoxRepository.findBySerialNumber(serialNumer);
            JSONObject airConditionBox = new JSONObject();
            airConditionBox.put("photo", reseachProject.getAirConditioningBoxPhotoUrl());
            airConditionBox.put("data", airConditionBoxList);
            result.put("airConditionBox", airConditionBox);

            List<TerminalEquipment> terminalEquipmentList = terminalEquipmentRepository.findBySerialNumber(serialNumer);
            JSONObject terminalEquipment = new JSONObject();
            terminalEquipment.put("photo", reseachProject.getTerminalEquipmentPhotoUrl());
            terminalEquipment.put("data", terminalEquipmentList);
            result.put("terminalEquipment", terminalEquipment);

            return result;
        }
        return null;
    }

    @Override
    public JSONObject getElectricalEquipmentById(Long id) {
        JSONObject result = new JSONObject();
        Optional<ReseachProject> optional = reseachProjectRepository.findById(id);
        if (optional.isPresent() && !optional.get().getDelStatus()) {
            ReseachProject reseachProject = optional.get();
            List<LightingEquipment> lightingEquipmentList = lightingEquipmentRepository.findBySerialNumber(reseachProject.getSerialNumber());
            result.put("lightingEquipment", lightingEquipmentList);
            List<ElectricalLoad> electricalLoadList = electricalLoadRepository.findBySerialNumber(reseachProject.getSerialNumber());
            result.put("electricalLoad", electricalLoadList);
            return result;
        }
        return null;
    }

    @Override
    public List<ResearchProjectData> getDataByTypeAndTime(Long id, Integer type, String start, String end) throws ParseException {
        if (StringUtils.isEmpty(start)) {
            start = "2017-01";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date startDate = sdf.parse(start);
        Date endDate = null;
        if (StringUtils.isEmpty(end)) {
            endDate = new Date();
        } else {
            endDate = sdf.parse(end);
        }
        List<ResearchProjectData> dataList =
                researchProjectDataRepository.findByReseachProjectIdAndTypeAndActualDateBetweenOrderByActualDate(id, type, startDate, endDate);
        return dataList;
    }

    /**
     * 调研项目子表导入
     *
     * @param file
     * @return
     */
    private String dealWithCsvFile(MultipartFile file) {
        return null;
    }

    /**
     * 调研项目excel表
     * + 子表数据
     * 数据导入
     *
     * @param file
     * @return
     */
    private String dealWithExcelFile(MultipartFile file) throws IOException, ParseException {
        String message = "";
        Workbook wb = excelUtil.getWorkbook(file);
        if (wb != null) {
            int sheetNum = wb.getNumberOfSheets();
            String fileName = file.getOriginalFilename();
            if (fileName.split("-").length == 4) {//项目子表数据
                dealWithResearchProjectSubExcel(file, fileName);
                message += HttpResponseStatusEnum.SUCCESS.getMessage();
            } else {
                logger.info("{}文件共有{}页", fileName, sheetNum);
                for (int i = 0; i < sheetNum; i++) {
                    Sheet sheet = wb.getSheetAt(i);
                    logger.info("开始处理第{}页数据", (i + 1));
                    //获取项目基础数据
                    List<Map<String, Object>> researchProjectMapList = excelUtil.dealWithRExcelSheet(sheet);
                    List<ReseachProjectModel> projectModelList = mapToReseachProjectModel(researchProjectMapList);
                    saveOrUpdateReseachProject(projectModelList);
                }
                message += HttpResponseStatusEnum.SUCCESS.getMessage();
            }
        }
        return message;
    }

    private void dealWithResearchProjectSubExcel(MultipartFile file, String fileName) throws ParseException {
        String serialNumber = fileName.split("\\.")[0].substring(0, fileName.split("\\.")[0].length() - 2);
        Optional<ReseachProject> optionalReseachProject = reseachProjectRepository.findBySerialNumber(serialNumber);
        if (optionalReseachProject.isPresent()) {
            ReseachProject reseachProject = optionalReseachProject.get();
            char type = fileName.split("\\.")[0].charAt(fileName.split("\\.")[0].length() - 1);
            List<?> list = new ArrayList<>();
            Class<?> pojoClass = null;
            switch (type) {
                case 'c'://主机
                    pojoClass = MainEngine.class;
                    break;
                case 'p'://水泵
                    pojoClass = WaterPump.class;
                    break;
                case 't'://冷却塔
                    pojoClass = CoolingTower.class;
                    break;
                case 'h'://空调箱
                    pojoClass = AirConditionBox.class;
                    break;
                case 'f'://末端设备
                    pojoClass = TerminalEquipment.class;
                    break;
                case 'l'://照明
                    pojoClass = LightingEquipment.class;
                    break;
                case 'm'://电路负载
                    pojoClass = ElectricalLoad.class;
                    break;
                case 'e'://能耗
                    pojoClass = ResearchProjectDataModel.class;
                    break;
            }
            list = dealWithExcel(file, pojoClass);
            logger.info("文件:{}数据展示如下：", fileName);
            switch (type) {
                case 'c'://主机

                    if (mainEngineRepository.findBySerialNumber(serialNumber).size() == 0) {
                        List<MainEngine> mainEngineList = new ArrayList<>();
                        for (Object o : list) {
                            MainEngine ro = (MainEngine) o;
                            if (ro.isNull()) {
                                continue;
                            }
                            ro.setSerialNumber(serialNumber);
                            ro.setReseachProjectId(reseachProject.getId());
                            mainEngineList.add(ro);
                        }
                        mainEngineRepository.saveAll(mainEngineList);
                    }
                    break;
                case 'p'://水泵

                    if (waterPumpRepository.findBySerialNumber(serialNumber).size() == 0) {
                        List<WaterPump> waterPumpList = new ArrayList<>();
                        for (Object o : list) {
                            WaterPump ro = (WaterPump) o;
                            if (ro.isNull()) {
                                continue;
                            }
                            ro.setSerialNumber(serialNumber);
                            ro.setReseachProjectId(reseachProject.getId());
                            waterPumpList.add(ro);
                        }
                        waterPumpRepository.saveAll(waterPumpList);
                    }
                    break;
                case 't'://冷却塔

                    if (coolingTowerRepository.findBySerialNumber(serialNumber).size() == 0) {
                        List<CoolingTower> coolingTowerList = new ArrayList<>();
                        for (Object o : list) {
                            CoolingTower ro = (CoolingTower) o;
                            if (ro.isNull()) {
                                continue;
                            }
                            ro.setSerialNumber(serialNumber);
                            ro.setReseachProjectId(reseachProject.getId());
                            coolingTowerList.add(ro);
                        }
                        coolingTowerRepository.saveAll(coolingTowerList);
                    }
                    break;
                case 'h'://空调箱

                    if (airConditionBoxRepository.findBySerialNumber(serialNumber).size() == 0) {
                        List<AirConditionBox> airConditionBoxList = new ArrayList<>();
                        for (Object o : list) {
                            AirConditionBox ro = (AirConditionBox) o;
                            if (ro.isNull()) {
                                continue;
                            }
                            ro.setSerialNumber(serialNumber);
                            ro.setReseachProjectId(reseachProject.getId());
                            airConditionBoxList.add(ro);
                        }
                        airConditionBoxRepository.saveAll(airConditionBoxList);
                    }
                    break;
                case 'f'://末端设备

                    if (terminalEquipmentRepository.findBySerialNumber(serialNumber).size() == 0) {
                        List<TerminalEquipment> terminalEquipmentList = new ArrayList<>();
                        for (Object o : list) {
                            TerminalEquipment ro = (TerminalEquipment) o;
                            if (ro.isNull()) {
                                continue;
                            }
                            ro.setSerialNumber(serialNumber);
                            ro.setReseachProjectId(reseachProject.getId());
                            terminalEquipmentList.add(ro);
                        }
                        terminalEquipmentRepository.saveAll(terminalEquipmentList);
                    }
                    break;
                case 'l'://照明

                    if (lightingEquipmentRepository.findBySerialNumber(serialNumber).size() == 0) {
                        List<LightingEquipment> lightingEquipmentList = new ArrayList<>();
                        for (Object o : list) {
                            LightingEquipment ro = (LightingEquipment) o;
                            if (ro.isNull()) {
                                continue;
                            }
                            ro.setSerialNumber(serialNumber);
                            ro.setReseachProjectId(reseachProject.getId());
                            lightingEquipmentList.add(ro);
                        }
                        lightingEquipmentRepository.saveAll(lightingEquipmentList);
                    }
                    break;
                case 'm'://电路负载

                    if (electricalLoadRepository.findBySerialNumber(serialNumber).size() == 0) {
                        List<ElectricalLoad> electricalLoadList = new ArrayList<>();
                        for (Object o : list) {
                            ElectricalLoad ro = (ElectricalLoad) o;
                            if (ro.isNull()) {
                                continue;
                            }
                            ro.setSerialNumber(serialNumber);
                            ro.setReseachProjectId(reseachProject.getId());
                            electricalLoadList.add(ro);
                        }
                        electricalLoadRepository.saveAll(electricalLoadList);
                    }

                    break;
                case 'e'://能耗
                    List<ResearchProjectData> dataList = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
                    long reseachProjectId = reseachProject.getId();
                    for (Object o : list) {
                        ResearchProjectDataModel ro = (ResearchProjectDataModel) o;
                        if (ro.isNull()) {
                            continue;
                        }
                        String date = ro.getDate();
                        Date actualDate = sdf.parse(date);

                        if (null != ro.getWater()) {
                            ResearchProjectData waterData = new ResearchProjectData();
                            waterData.setActualDate(actualDate);
                            waterData.setReseachProjectId(reseachProjectId);
                            waterData.setSerialNumber(serialNumber);
                            waterData.setType(0);
                            waterData.setValue(ro.getWater());
                            dataList.add(waterData);
                        }

                        if (null != ro.getElec()) {
                            ResearchProjectData elecData = new ResearchProjectData();
                            elecData.setActualDate(actualDate);
                            elecData.setReseachProjectId(reseachProjectId);
                            elecData.setSerialNumber(serialNumber);
                            elecData.setType(1);
                            elecData.setValue(ro.getElec());
                            dataList.add(elecData);
                        }

                        if (null != ro.getGas()) {
                            ResearchProjectData gasData = new ResearchProjectData();
                            gasData.setActualDate(actualDate);
                            gasData.setReseachProjectId(reseachProjectId);
                            gasData.setSerialNumber(serialNumber);
                            gasData.setType(2);
                            gasData.setValue(ro.getGas());
                            dataList.add(gasData);
                        }

                        if (null != ro.getHeat()) {
                            ResearchProjectData heatData = new ResearchProjectData();
                            heatData.setActualDate(actualDate);
                            heatData.setReseachProjectId(reseachProjectId);
                            heatData.setSerialNumber(serialNumber);
                            heatData.setType(3);
                            heatData.setValue(ro.getHeat());
                            dataList.add(heatData);
                        }
                    }
                    List<ResearchProjectData> dataListNew = new ArrayList<>();
                    for (ResearchProjectData data :
                            dataList) {
                        List<ResearchProjectData> researchProjectDatas = researchProjectDataRepository.findBySerialNumberAndTypeAndActualDate(serialNumber, data.getType(), data.getActualDate());
                        if (researchProjectDatas.size() > 0) {
                            if (researchProjectDatas.size() == 1) {
                                ResearchProjectData data1 = researchProjectDatas.get(0);
                                data1.setValue(data.getValue());
                                researchProjectDataRepository.saveAndFlush(data1);
                            }
                        } else {
                            dataListNew.add(data);
                        }
                    }
                    researchProjectDataRepository.saveAll(dataListNew);
                    break;
            }
        } else {
            logger.error("文件:{}所属调研项目不存在.", fileName);
        }
    }

    public List dealWithExcel(MultipartFile file, Class<?> pojoClass) {
        if (file != null) {
            ImportParams params = new ImportParams();
            params.setNeedSave(false);
            List list = null;
            try {
                list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
            } catch (NumberFormatException e) {
                logger.error(e.getMessage());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            return list;
        }
        return null;
    }

    private void saveOrUpdateReseachProject(List<ReseachProjectModel> projectModelList) {
        List<ReseachProject> resultNew = new ArrayList<>();
        for (ReseachProjectModel project :
                projectModelList) {
            String serialNumber = project.getSerialNumber();
            Optional<ReseachProject> optional = reseachProjectRepository.findBySerialNumber(serialNumber);
            if (optional.isPresent()) {//更新
                ReseachProject reseachProjectOld = optional.get();
                if (!StringUtils.isEmpty(project.getName())) {
                    reseachProjectOld.setName(project.getName());
                }
                if (!StringUtils.isEmpty(project.getAddress())) {
                    reseachProjectOld.setAddress(project.getAddress());
                }
                if (!StringUtils.isEmpty(project.getName())) {
                    reseachProjectOld.setName(project.getName());
                }
                if (!StringUtils.isEmpty(project.getLongitude())) {
                    reseachProjectOld.setLongitude(project.getLongitude());
                }
                if (!StringUtils.isEmpty(project.getLatitude())) {
                    reseachProjectOld.setLatitude(project.getLatitude());
                }
                if (!StringUtils.isEmpty(project.getDesigner())) {
                    reseachProjectOld.setDesigner(project.getDesigner());
                }
                if (!StringUtils.isEmpty(project.getConstructor())) {
                    reseachProjectOld.setConstructor(project.getConstructor());
                }
                if (!StringUtils.isEmpty(project.getSupervisor())) {
                    reseachProjectOld.setSupervisor(project.getSupervisor());
                }
                if (!StringUtils.isEmpty(project.getBuiltTime())) {
                    reseachProjectOld.setBuiltTime(project.getBuiltTime());
                }
                if (!StringUtils.isEmpty(project.getBuildingInfo())) {
                    reseachProjectOld.setBuildingInfo(project.getBuildingInfo());
                }
                if (!StringUtils.isEmpty(project.getHeight())) {
                    reseachProjectOld.setHeight(project.getHeight());
                }
                if (!StringUtils.isEmpty(project.getTotalArea())) {
                    reseachProjectOld.setTotalArea(project.getTotalArea());
                }
                if (!StringUtils.isEmpty(project.getAbovegroundArea())) {
                    reseachProjectOld.setAbovegroundArea(project.getAbovegroundArea());
                }
                if (!StringUtils.isEmpty(project.getUndergroundArea())) {
                    reseachProjectOld.setUndergroundArea(project.getUndergroundArea());
                }
                if (!StringUtils.isEmpty(project.getAirConditionArea())) {
                    reseachProjectOld.setAirConditionArea(project.getAirConditionArea());
                }
                if (!StringUtils.isEmpty(project.getAbovegroundFloor())) {
                    reseachProjectOld.setAbovegroundFloor(project.getAbovegroundFloor());
                }
                if (!StringUtils.isEmpty(project.getUndergroundFloor())) {
                    reseachProjectOld.setUndergroundFloor(project.getUndergroundFloor());
                }
                if (!StringUtils.isEmpty(project.getSunshade())) {
                    reseachProjectOld.setSunshade(project.getSunshade());
                }
                if (!StringUtils.isEmpty(project.getHasBas())) {
                    reseachProjectOld.setHasBas(project.getHasBas());
                }
                if (!StringUtils.isEmpty(project.getHasEnergyonsumptionMeasurement())) {
                    reseachProjectOld.setHasEnergyonsumptionMeasurement(project.getHasEnergyonsumptionMeasurement());
                }
                if (!StringUtils.isEmpty(project.getHasWaterMetering())) {
                    reseachProjectOld.setHasWaterMetering(project.getHasWaterMetering());
                }
                if (!StringUtils.isEmpty(project.getWindowIsOpen())) {
                    reseachProjectOld.setWindowIsOpen(project.getWindowIsOpen());
                }
                if (!StringUtils.isEmpty(project.getCDOAS())) {
                    reseachProjectOld.setCDOAS(project.getCDOAS());
                }
                if (!StringUtils.isEmpty(project.getNumberOfEndUsers())) {
                    reseachProjectOld.setNumberOfEndUsers(project.getNumberOfEndUsers());
                }
                if (!StringUtils.isEmpty(project.getOverviewOfHVACEquipment())) {
                    reseachProjectOld.setOverviewOfHVACEquipment(project.getOverviewOfHVACEquipment());
                }
                if (!StringUtils.isEmpty(project.getOverviewOfElectricalEquipment())) {
                    reseachProjectOld.setOverviewOfElectricalEquipment(project.getOverviewOfElectricalEquipment());
                }
                if (!StringUtils.isEmpty(project.getOverviewOfWaterSupplyAndDrainageSystem())) {
                    reseachProjectOld.setOverviewOfWaterSupplyAndDrainageSystem(project.getOverviewOfWaterSupplyAndDrainageSystem());
                }
                if (!StringUtils.isEmpty(project.getEnergyConsumptionDataInRecentThreeYears())) {
                    reseachProjectOld.setEnergyConsumptionDataInRecentThreeYears(project.getEnergyConsumptionDataInRecentThreeYears());
                }
                if (!StringUtils.isEmpty(project.getIsItSuggestedToTransform())) {
                    reseachProjectOld.setIsItSuggestedToTransform(project.getIsItSuggestedToTransform());
                }
                if (!StringUtils.isEmpty(project.getTransformationSuggestions())) {
                    reseachProjectOld.setTransformationSuggestions(project.getTransformationSuggestions());
                }
                if (!StringUtils.isEmpty(project.getOverallPhotoUrl())) {
                    reseachProjectOld.setOverallPhotoUrl(project.getOverallPhotoUrl());
                }
                if (!StringUtils.isEmpty(project.getAirConditioningRoomPhotoUrl())) {
                    reseachProjectOld.setAirConditioningRoomPhotoUrl(project.getAirConditioningRoomPhotoUrl());
                }
                if (!StringUtils.isEmpty(project.getHostPhotoUrl())) {
                    reseachProjectOld.setHostPhotoUrl(project.getHostPhotoUrl());
                }
                if (!StringUtils.isEmpty(project.getWaterPumpPhotoUrl())) {
                    reseachProjectOld.setWaterPumpPhotoUrl(project.getWaterPumpPhotoUrl());
                }
                if (!StringUtils.isEmpty(project.getCoolingTowerPhotoUrl())) {
                    reseachProjectOld.setCoolingTowerPhotoUrl(project.getCoolingTowerPhotoUrl());
                }
                if (!StringUtils.isEmpty(project.getAirConditioningBoxPhotoUrl())) {
                    reseachProjectOld.setAirConditioningBoxPhotoUrl(project.getAirConditioningBoxPhotoUrl());
                }
                if (!StringUtils.isEmpty(project.getTerminalEquipmentPhotoUrl())) {
                    reseachProjectOld.setTerminalEquipmentPhotoUrl(project.getTerminalEquipmentPhotoUrl());
                }
                if (!StringUtils.isEmpty(project.getAirConditioningSystem())) {
                    reseachProjectOld.setAirConditioningSystem(project.getAirConditioningSystem());
                }
                reseachProjectRepository.saveAndFlush(reseachProjectOld);
            } else {
                ReseachProject reseachProjectNew = project.toEntity();
                reseachProjectNew.setDelStatus(false);
                resultNew.add(reseachProjectNew);
            }
            reseachProjectRepository.saveAll(resultNew);
        }
    }

    private List<ReseachProjectModel> mapToReseachProjectModel(List<Map<String, Object>> mapList) throws ParseException {
        List<ReseachProjectModel> result = new ArrayList<>();
        for (Map<String, Object> map :
                mapList) {
            String serialNumber = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[0]))) {
                serialNumber = (String) map.get(ExcelUtil.rColumNames[0]);
            } else continue;

            String name = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[1]))) {
                name = (String) map.get(ExcelUtil.rColumNames[1]);
            }

            String address = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[2]))) {
                address = (String) map.get(ExcelUtil.rColumNames[2]);
            }
            /**
             * 建设单位
             */
            String constractor = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[3]))) {
                constractor = (String) map.get(ExcelUtil.rColumNames[3]);
            }
            /**
             * 设计单位
             */
            String designer = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[4]))) {
                designer = (String) map.get(ExcelUtil.rColumNames[4]);
            }
            /**
             * 施工单位
             */
            String constructor = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[5]))) {
                constructor = (String) map.get(ExcelUtil.rColumNames[5]);
            }
            /**
             * 监理单位
             */
            String supervisor = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[6]))) {
                supervisor = (String) map.get(ExcelUtil.rColumNames[6]);
            }

            /**
             * 建成时间
             */
            Date builtTime = null;
            if (map.get(ExcelUtil.rColumNames[7]) != null) {
                Object value = map.get(ExcelUtil.rColumNames[7]);
                if (!value.equals("/")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                    Double d_value = (Double) value;
                    String sdfTime = d_value.intValue() + "";
                    builtTime = sdf.parse(sdfTime);
                }
            }
            /**
             * 建筑楼栋信息
             */
            String buildingInfo = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[8]))) {
                buildingInfo = (String) map.get(ExcelUtil.rColumNames[8]);
            }
            /**
             * 建筑高度
             */
            Double height = null;
            if (map.get(ExcelUtil.rColumNames[9]) != null) {
                Object value = map.get(ExcelUtil.rColumNames[9]);
                if (!value.equals("/")) {
                    height = (Double) map.get(ExcelUtil.rColumNames[9]);
                }
            }
            /**
             * 总面积
             */
            Double totalArea = null;
            if (map.get(ExcelUtil.rColumNames[10]) != null) {
                Object value = map.get(ExcelUtil.rColumNames[10]);
                if (!value.equals("/")) {
                    totalArea = (Double) map.get(ExcelUtil.rColumNames[10]);
                }
            }

            /**
             * 地上面积
             */
            Double abovegroundArea = null;
            if (map.get(ExcelUtil.rColumNames[11]) != null) {
                Object value = map.get(ExcelUtil.rColumNames[11]);
                if (!value.equals("/")) {
                    abovegroundArea = (Double) map.get(ExcelUtil.rColumNames[11]);
                }
            }
            /**
             * 地下面积
             */
            Double undergroundArea = null;
            if (map.get(ExcelUtil.rColumNames[12]) != null) {
                Object value = map.get(ExcelUtil.rColumNames[12]);
                if (!value.equals("/")) {
                    undergroundArea = (Double) map.get(ExcelUtil.rColumNames[12]);
                }
            }

            /**
             * 空调面积
             */
            Double airConditionArea = null;
            if (map.get(ExcelUtil.rColumNames[13]) != null) {
                Object value = map.get(ExcelUtil.rColumNames[13]);
                if (!value.equals("/")) {
                    airConditionArea = (Double) map.get(ExcelUtil.rColumNames[13]);
                }
            }
            /**
             * 地上建筑层数
             */
            Integer abovegroundFloor = null;
            if (map.get(ExcelUtil.rColumNames[14]) != null) {
                Object value = map.get(ExcelUtil.rColumNames[14]);
                if (!value.equals("/")) {
                    abovegroundFloor = ((Double) map.get(ExcelUtil.rColumNames[14])).intValue();
                }
            }
            /**
             * 地下建筑层数
             */
            Integer undergroundFloor = null;
            if (map.get(ExcelUtil.rColumNames[15]) != null) {
                Object value = map.get(ExcelUtil.rColumNames[17]);
                if (!value.equals("/")) {
                    undergroundFloor = ((Double) map.get(ExcelUtil.rColumNames[15])).intValue();
                }
            }
            /**
             * 主要遮阳形式
             */
            String sunshade = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[16]))) {
                sunshade = (String) map.get(ExcelUtil.rColumNames[16]);
            }
            /**
             * 有无楼宇自控系统
             */
            Boolean hasBas = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[17]))) {
                String value = (String) map.get(ExcelUtil.rColumNames[17]);
                switch (value) {
                    case "是":
                    case "有":
                        hasBas = true;
                        break;
                    case "否":
                    case "无":
                        hasBas = false;
                        break;
                    default:
                        hasBas = null;
                }
            }
            /**
             * 有无能耗计量/远传电表数据
             */
            Boolean hasEnergyonsumptionMeasurement = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[18]))) {
                String value = (String) map.get(ExcelUtil.rColumNames[18]);
                switch (value) {
                    case "是":
                    case "有":
                        hasEnergyonsumptionMeasurement = true;
                        break;
                    case "否":
                    case "无":
                        hasEnergyonsumptionMeasurement = false;
                        break;
                    default:
                        hasEnergyonsumptionMeasurement = null;
                }
            }
            /**
             * 有无用水计量/远传水表数据
             */
            Boolean hasWaterMetering = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[19]))) {
                String value = (String) map.get(ExcelUtil.rColumNames[19]);
                switch (value) {
                    case "是":
                    case "有":
                        hasWaterMetering = true;
                        break;
                    case "否":
                    case "无":
                        hasWaterMetering = false;
                        break;
                    default:
                        hasWaterMetering = null;
                }
            }
            /**
             * 窗户/幕墙是否可开启
             */
            Boolean windowIsOpen = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[20]))) {
                String value = (String) map.get(ExcelUtil.rColumNames[20]);
                switch (value) {
                    case "是":
                    case "有":
                        windowIsOpen = true;
                        break;
                    case "否":
                    case "无":
                        windowIsOpen = false;
                        break;
                    default:
                        windowIsOpen = null;
                }
            }
            /**
             * 新风系统形式
             */
            String CDOAS = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[21]))) {
                CDOAS = (String) map.get(ExcelUtil.rColumNames[21]);
            }
            /**
             * 末端用户数量
             */
            Integer numberOfEndUsers = null;
            if (map.get(ExcelUtil.rColumNames[22]) != null) {
                Object value = map.get(ExcelUtil.rColumNames[22]);
                if (!value.equals("/")) {
                    numberOfEndUsers = ((Double) map.get(ExcelUtil.rColumNames[22])).intValue();
                }
            }
            /**
             * 暖通设备概况及信息
             */
            String overviewOfHVACEquipment = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[23]))) {
                overviewOfHVACEquipment = (String) map.get(ExcelUtil.rColumNames[23]);
            }
            /**
             * 电器设备概况及信息
             */
            String overviewOfElectricalEquipment = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[24]))) {
                overviewOfElectricalEquipment = (String) map.get(ExcelUtil.rColumNames[24]);
            }
            /**
             * 给排水系统概况
             */
            String overviewOfWaterSupplyAndDrainageSystem = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[25]))) {
                overviewOfWaterSupplyAndDrainageSystem = (String) map.get(ExcelUtil.rColumNames[25]);
            }
            /**
             * 近三年能耗数据
             */
            String energyConsumptionDataInRecentThreeYears = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[26]))) {
                energyConsumptionDataInRecentThreeYears = (String) map.get(ExcelUtil.rColumNames[26]);
            }
            /**
             * 是否建议改造
             */
            Boolean isItSuggestedToTransform = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[27]))) {
                String value = (String) map.get(ExcelUtil.rColumNames[27]);
                switch (value) {
                    case "是":
                    case "有":
                        isItSuggestedToTransform = true;
                        break;
                    case "否":
                    case "无":
                        isItSuggestedToTransform = false;
                        break;
                    default:
                        isItSuggestedToTransform = null;
                }
            }
            /**
             * 改造建议
             */
            String transformationSuggestions = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[28]))) {
                transformationSuggestions = (String) map.get(ExcelUtil.rColumNames[28]);
            }

            /**
             * 经度
             */
            Double longitude = null;
            if (map.get(ExcelUtil.rColumNames[29]) != null) {
                Object value = map.get(ExcelUtil.rColumNames[29]);
                if (!value.equals("/")) {
                    longitude = (Double) map.get(ExcelUtil.rColumNames[29]);
                }
            }
            /**
             * 纬度
             */
            Double latitude = null;
            if (map.get(ExcelUtil.rColumNames[30]) != null) {
                Object value = map.get(ExcelUtil.rColumNames[30]);
                if (!value.equals("/")) {
                    latitude = (Double) map.get(ExcelUtil.rColumNames[30]);
                }
            }
            String overallPhotoUrl = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[31]))) {
                overallPhotoUrl = (String) map.get(ExcelUtil.rColumNames[31]);
            }
            /**
             * 空调机房照片
             */
            String airConditioningRoomPhotoUrl = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[32]))) {
                airConditioningRoomPhotoUrl = (String) map.get(ExcelUtil.rColumNames[32]);
            }
            /**
             * 主机照片
             */
            String hostPhotoUrl = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[33]))) {
                hostPhotoUrl = (String) map.get(ExcelUtil.rColumNames[33]);
            }
            /**
             * 水泵照片
             */
            String waterPumpPhotoUrl = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[34]))) {
                waterPumpPhotoUrl = (String) map.get(ExcelUtil.rColumNames[34]);
            }
            /**
             * 冷却塔照片
             */
            String coolingTowerPhotoUrl = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[35]))) {
                coolingTowerPhotoUrl = (String) map.get(ExcelUtil.rColumNames[35]);
            }
            /**
             * 空调箱照片
             */
            String airConditioningBoxPhotoUrl = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[36]))) {
                airConditioningBoxPhotoUrl = (String) map.get(ExcelUtil.rColumNames[36]);
            }
            /**
             * 末端设备照片
             */
            String terminalEquipmentPhotoUrl = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[37]))) {
                terminalEquipmentPhotoUrl = (String) map.get(ExcelUtil.rColumNames[37]);
            }
            /**
             * 空调系统形式
             */
            String airConditioningSystem = null;
            if (!StringUtils.isEmpty(map.get(ExcelUtil.rColumNames[38]))) {
                airConditioningSystem = (String) map.get(ExcelUtil.rColumNames[38]);
            }

            ReseachProjectModel model = new ReseachProjectModel(serialNumber, name, address, constractor, longitude, latitude,
                    designer, constructor, supervisor, builtTime, buildingInfo, height, totalArea, abovegroundArea, undergroundArea,
                    airConditionArea, abovegroundFloor, undergroundFloor, sunshade, hasBas, hasEnergyonsumptionMeasurement, hasWaterMetering,
                    windowIsOpen, CDOAS, numberOfEndUsers, overviewOfHVACEquipment, overviewOfElectricalEquipment, overviewOfWaterSupplyAndDrainageSystem,
                    energyConsumptionDataInRecentThreeYears, isItSuggestedToTransform, transformationSuggestions, overallPhotoUrl, airConditioningRoomPhotoUrl,
                    hostPhotoUrl, waterPumpPhotoUrl, coolingTowerPhotoUrl, airConditioningBoxPhotoUrl, terminalEquipmentPhotoUrl,
                    airConditioningSystem
            );
            result.add(model);
        }
        return result;
    }
}
