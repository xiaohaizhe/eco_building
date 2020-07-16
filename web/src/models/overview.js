import { getTop10,getTop5 } from '@/services/overview';

const OverviewModel = {
  namespace: 'overview',
  state: {
    top5: [],
    top10:[]
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
  },
  reducers: {
    save10(state, { payload }){
      return { ...state, top10: payload.result};
    },
    save5(state, { payload }){
        return { ...state, top5: payload.result};
    },

  },
};
export default OverviewModel;