import { getUserPage,update,register,deleteUser } from '@/services/userManage';
import { message } from 'antd';

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
    *submit({ payload,callback }, { call }) {
      let callbackFun;
      if (payload.id) {
        callbackFun = update;
      } else{
        callbackFun = register;
      }

      const response = yield call(callbackFun, payload); // post
      if(response.code==0){
        callback(response)
      }else{
        message.error(response.message)
      }
      
    },
    *deleteUser({ payload,callback }, { call }) {
      const response = yield call(deleteUser, payload); // post
      if(response.code==0){
        callback(response)
      }
      
    },
  },
  reducers: {
    save(state, { payload }){
      return { ...state, user: payload};
    },


  },
};
export default UserManageModel;