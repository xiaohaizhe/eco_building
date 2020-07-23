import { getProjectPage,importExcel,deleteProject,getProjectDetail,getDataByTime,update,updateData} from '@/services/projectManage';
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
    *getProjectDetail({ payload ,callback}, { call, put }) {
      const response = yield call(getProjectDetail, payload);
      yield put({
        type: 'saveDetail',
        payload: response,
      });
      if(callback){
        callback(response)
      }
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
      if (callback && response.code == 0) {
        let sum = 0;
        response.result.map((val, index, arr) => {
            if(val.value){
              sum += val.value
            }
            
        })
        

        callback(response,sum);
      }
    },
    //更新数据
    *update({ payload,callback }, { call ,put}) {
      const response = yield call(update,payload);
      yield put({
        type: 'saveDetail',
        payload: response,
      });
      callback(response)
    },
    //更新表格数据
    *updateData({ payload,callback }, { call }) {
      const response = yield call(updateData,payload);
    },
    *changeLongLat({ payload }, { put ,select}){
      const {detail} = yield select(state => state.projectManage);
      let temp = {...detail,...payload}
      yield put({
        type: 'change',
        payload: temp,
      });
    },
    // *changeLat({ payload }, { put ,select}){
    //   const {detail} = yield select(state => state.projectManage);
    //   let temp = {...detail,latitude:payload}
    //   yield put({
    //     type: 'change',
    //     payload: temp,
    //   });
    // }
  },
  reducers: {
    save(state, { payload }){
      return { ...state, projects: payload.result};
    },
    saveDetail(state, { payload }){
      let result = payload.result;
      if(result.province){
        if(result.city){
          if(result.district){
            if(result.street){
              result.division= [result.province,result.city,result.district,result.street];
            }else{
              result.division= [result.province,result.city,result.district];
            }
          }else{
            result.division= [result.province,result.city];
          }
        }else{
          result.division= [result.province];
        }
      }else{
        result.division= [];
      }
      return { ...state, detail:result };
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
    change(state, { payload }){
      return {...state, detail:payload}
    }

  },
};
export default ProjectManageModel;