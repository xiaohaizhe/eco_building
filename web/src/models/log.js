import { getActionType } from '@/services/log';

const LogModel = {
  namespace: 'log',
  state: {
    actionTypes: {}
  },
  effects: {
    *getActionType({}, { call, put }) {
      const response = yield call(getActionType);
      yield put({
        type: 'save',
        payload: response,
      });
    },
  },
  reducers: {
    save(state, { payload }){
      return { ...state, actionTypes: payload};
    },


  },
};
export default LogModel;