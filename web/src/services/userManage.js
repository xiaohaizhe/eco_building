import request from '@/utils/request';

export async function getUserPage(params) {
  return request('/api/admin/userPage', {
    method: 'GET',
    params: params
  });
}