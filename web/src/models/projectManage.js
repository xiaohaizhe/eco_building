import { getProjectPage,update,importExcel,deleteProject,getProjectDetail,getDataByTime  } from '@/services/projectManage';
import { getMap,getAddressOnMap } from '@/services/display';

const ProjectManageModel = {
  namespace: 'projectManage',
  state: {
    mapData:[],
    cityData:[],
    projects: [],
    detail:{}
  },
  effects: {
    //分页
    *getProjectPage({ payload,callback }, { call, put }) {
      const response = yield call(getProjectPage, payload);
      if(response.code==0){
        callback(response.result)
      }
      yield put({
        type: 'save',
        payload: response,
      });
    },
    //详情
    *getProjectDetail({ payload }, { call, put }) {
      const response = yield call(getProjectDetail, payload);
      yield put({
        type: 'saveDetail',
        payload: response,
      });
    },
    //导入
    *submit({ payload,callback }, { call }) {
      const response = yield call(importExcel, payload); // post
      if(response.code==0){
        callback(response)
      }
      
    },
    //删除
    *deleteProject({ payload,callback }, { call }) {
      const response = yield call(deleteProject, payload); // post
      if(response.code==0){
        callback(response)
      }
      
    },
    //获取江苏省地图
    *filterData(_,{ call, put,select }){
      const response = yield call(getMap,{province:'江苏省'});
      yield put({
        type: 'saveCitydata',
        payload: response,
      });
    },
    //获取图表信息
    *getDataByTime({ payload,callback }, { call }) {
      const response = yield call(getDataByTime,payload);
      if (callback) callback(response);
    },
  },
  reducers: {
    save(state, { payload }){
      return { ...state, projects: payload.result};
    },
    saveDetail(state, { payload }){
      return { ...state, detail: payload.result};
    },
    saveCitydata(state, { payload }){
      let mapData = payload.result.project;
      // let data=[];
      // let cities=[];
      // for(let i=0;i<mapData.length;i++){
      //     if(cities.indexOf(mapData[i].city)<0){
      //         cities.push(mapData[i].city);
      //         data.push(
      //             {name:mapData[i].city,lnglat:[mapData[i].longitude,mapData[i].latitude],children:[{lnglat:[mapData[i].longitude,mapData[i].latitude],name:mapData[i].name}]}
      //         );
      //     }else{
      //         data[cities.indexOf(mapData[i].city)].children.push({lnglat:[mapData[i].longitude,mapData[i].latitude],name:mapData[i].name})
      //     }
          
      // }
      
      return { ...state, mapData:mapData};//cityData: data,
    },

  },
};
export default ProjectManageModel;