import { getList,getDetail,getHVAC,getElectrical,getGasData,getElecData,getHeatData,getWaterData,importExcel} from '@/services/research';

const ResearchModel = {
  namespace: 'research',
  state: {
    mapData:[],
    cityData:[],
    projects: [],
    detail:{},
    HVAC:{},
    electrical:{}
  },
  effects: {
    //搜索
    *getSearchData({ payload,callback }, { call, put }) {
      const response = yield call(getList, payload);
      if(response.code==0){
        callback(response.result)
      }
    },
    //获取地图
    *filterData(_,{ call, put,select }){
      const response = yield call(getList,{name:'',isItSuggestedToTransform:''});
      yield put({
        type: 'saveCitydata',
        payload: response,
      }); 
    },
    //详情
    *getDetail({ payload ,callback}, { call, put }) {
      const response = yield call(getDetail, payload);
      yield put({
        type: 'saveDetail',
        payload: response,
      });
      if(callback){
        callback(response.result)
      }
    },
    //暖通设备信息
    *getHVAC({ payload ,callback}, { call, put }) {
      const response = yield call(getHVAC, payload);
      yield put({
        type: 'saveHVAC',
        payload: response,
      });
      if(callback){
        callback(response.result)
      }
    },
    //电气设备信息
    *getElectrical({ payload ,callback}, { call, put }) {
      const response = yield call(getElectrical, payload);
      yield put({
        type: 'saveElectrical',
        payload: response,
      });
      if(callback){
        callback(response.result)
      }
    },
    //获取能耗数据
    *getEchartData({ payload ,callback}, { call, put }) {
      let type = getElecData;
      if(payload.dataType=='气'){
        type = getGasData;
      }else if(payload.dataType=='水'){
        type = getWaterData;
      }else if(payload.dataType=='热'){
        type = getHeatData;
      }
      const response = yield call(type, payload);
      if(callback){
        callback(response.result)
      }
    },
    //导入
    *submit({ payload,callback }, { call }) {
      const response = yield call(importExcel, payload); // post
      if(response.code==0){
        callback(response)
      }
      
    },
  },
  reducers: {
    saveDetail(state, { payload }){
      let result = payload.result;
      return { ...state, detail:result };
    },
    saveHVAC(state, { payload }){
      let result = payload.result;
      return { ...state, HVAC:result };
    },
    saveElectrical(state, { payload }){
      let result = payload.result;
      return { ...state, electrical:result };
    },
    saveCitydata(state, { payload }){
      let mapData = payload.result;
      return { ...state, mapData:mapData};
    },
  },
};
export default ResearchModel;