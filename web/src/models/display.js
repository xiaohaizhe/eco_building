import { getMap } from '@/services/display';

const DisplayModel = {
  namespace: 'display',
  state: {
    mapData: [],
    elec:[],
    gas:[],
    water:[]
  },
  effects: {
    *getMap(_,{ call, put }) {
      const response = yield call(getMap);
      console.log(response)
      yield put({
        type: 'save',
        payload: response,
      });
    },
  },
  reducers: {
    save(state, { payload }){
        debugger
        let elec = payload.result.elec.split('-');
        let gas = payload.result.gas.split('-');
        let water = payload.result.water.split('-');
        return { ...state, mapData: payload.result.project,elec:elec,gas:gas,water:water};
    },


  },
};
export default DisplayModel;