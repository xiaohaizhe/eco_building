import { stringify } from 'querystring';
import { history } from 'umi';
import { login,logout } from '@/services/login';
import { setAuthority } from '@/utils/authority';
import { getPageQuery } from '@/utils/utils';
import { message } from 'antd';

const Model = {
  namespace: 'login',
  state: {
    status: undefined,
  },
  effects: {
    *login({ payload }, { call, put }) {
      const response = yield call(login, payload);
      yield put({
        type: 'changeLoginStatus',
        payload: {...response.result, username :payload.username},
      }); // Login successfully

      if (response.code === 0) {
        const urlParams = new URL(window.location.href);
        const params = getPageQuery();
        let { redirect } = params;
        if (redirect) {
          const redirectUrlParams = new URL(redirect);

          if (redirectUrlParams.origin === urlParams.origin) {
            redirect = redirect.substr(urlParams.origin.length);

            if (redirect.match(/^\/.*#/)) {
              redirect = redirect.substr(redirect.indexOf('#') + 1);
            }
          } else {
            window.location.href = '/';
            return;
          }
        }

        history.replace(redirect || '/');
      }
    },

    *logout(_, { call, put }) {
      // const { redirect } = getPageQuery(); // Note: There may be security issues, please note
      debugger
      const response = yield call(logout);
      yield put({
        type: 'changeLoginStatus',
        payload: {authority: "", username: "", id: ""},
      });
      if (response.code === 0) {
        const params = getPageQuery();
        let { redirect } = params;
        message.success('退出成功！');
        if (window.location.pathname !== '/user/login' && !redirect) {
          history.replace({
            pathname: '/user/login',
            search: stringify({
              redirect: window.location.href,
            }),
          });
        }
      }else{
        message.success('退出失败！');
        const params = getPageQuery();
        let { redirect } = params;
        if (window.location.pathname !== '/user/login' && !redirect) {
          history.replace({
            pathname: '/user/login',
            search: stringify({
              redirect: window.location.href,
            }),
          });
        }
      }
      
    },
  },
  reducers: {
    changeLoginStatus(state, { payload }) {
      setAuthority(payload);
      return { ...state };
    },
  },
};
export default Model;
