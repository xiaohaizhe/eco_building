import { getUserPage } from '@/services/userManage';

const UserManageModel = {
  namespace: 'userManage',
  state: {
    users: {}
  },
  effects: {
    *getUserPage({ payload }, { call, put }) {
      const response = yield call(getUserPage, payload);
      yield put({
        type: 'save',
        payload: response,
      });
    },

  },
  reducers: {
    save(state, { payload }){
      return { ...state, user: payload};
    },

  },
};
export default UserManageModel;