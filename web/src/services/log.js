import request from '@/utils/request';

export async function getActionPage(params) {
  return request('/api/actions/actionPage', {
    method: 'GET',
    params:params
  });
}

export async function getActionPageByUserId(params) {
    return request('/api/actions/actionPageByUserId', {
      method: 'GET',
      params:params
    });
  }

export async function getActionType() {
  return request('/api/actions/actionType', {
    method: 'GET'
  });
}