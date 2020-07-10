import request from '@/utils/request';

export async function getMap(params) {
  return request('/api/show/screen', {
    method: 'GET',
    params:params
  });
}

export async function getAddressOnMap() {
  return request('/api/show/getAddressOnMap', {
    method: 'GET'
  });
}

