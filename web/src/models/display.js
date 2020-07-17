import { getMap,getAddressOnMap } from '@/services/display';

const DisplayModel = {
  namespace: 'display',
  state: {
    mapData: [],
    maxMin:[{
      max:0,
      min:0
    },{
      max:0,
      min:0
    },{
      max:0,
      min:0
    }],
    address:[],
    itemParams:{},
    height:window.innerHeight-170
  },
  effects: {
    *getMap(_,{ call, put, select }) {
      const {itemParams} = yield select(state => state.display)
      const response = yield call(getMap,itemParams);
      // console.log(response)
      yield put({
        type: 'save',
        payload: response,
      });
    },
    *getAddressOnMap(_,{ call, put }){
      const response = yield call(getAddressOnMap);
      yield put({
        type: 'saveAddress',
        payload: response,
      });
    },
    *getParams({payload},{ call, put }){
      yield put({
        type: 'saveParams',
        payload: payload,
      });
      yield put({
        type: 'getMap'
      });
    },
    

  },
  reducers: {
    save(state, { payload }){
        let elec = payload.result.elec.split('-');
        let gas = payload.result.gas.split('-');
        let water = payload.result.water.split('-');
        let maxMin = [{max:water[1],min:water[0]},{max:elec[1],min:elec[0]},{max:gas[1],min:gas[0]}]
        return { ...state, mapData: payload.result.project,maxMin:maxMin};
    },
    saveAddress(state, { payload }){
      return { ...state, address: payload.result};
    },
    saveParams(state, { payload }){
      return { ...state, itemParams: payload};
    },
    

  },
};
export default DisplayModel;