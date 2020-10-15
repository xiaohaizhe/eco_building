import { getTop10,getTop5,getTypeData,getExcel } from '@/services/overview';

const OverviewModel = {
  namespace: 'overview',
  state: {
    top5: [],
    top10:[],
    typeData:[],
    excel:[]
  },
  effects: {
    *getTop10({}, { call, put }) {
      const response = yield call(getTop10);
      yield put({
        type: 'save10',
        payload: response,
      });
    },
    *getTop5({}, { call, put }) {
        const response = yield call(getTop5);
        yield put({
          type: 'save5',
          payload: response,
        });
      },
    *getTypeData({}, { call, put }) {
      const response = yield call(getTypeData);
      yield put({
        type: 'saveTypeData',
        payload: response,
      });
    },
    *getExcel({}, { call, put }) {
      const response = yield call(getExcel);
      yield put({
        type: 'saveExcel',
        payload: response,
      });
    },
  },
  reducers: {
    save10(state, { payload }){
      return { ...state, top10: payload.result};
    },
    save5(state, { payload }){
        let temp = [...payload.result];
        // let min = (payload.result[payload.result.length-1].value-0)>1?payload.result[payload.result.length-1].value-0:2;//payload.result.length-1
        for(let i=0;i<temp.length;i++){
          temp[i].realValue = temp[i].value;
          temp[i].value = Math.log(temp[i].value-0>1?temp[i].value-0:2);
        }
        return { ...state, top5:temp};
    },
    saveTypeData(state, { payload }){
      return { ...state, typeData: payload.result};
    },
    saveExcel(state, { payload }){
      let x = [];
      payload.result.forEach(element => {
        x.push(element.name)
      });
      return { ...state, excel: {
        x:x,
        data:payload.result
      }};
    },
  },
};
export default OverviewModel;