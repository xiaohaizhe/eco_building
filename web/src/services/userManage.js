import request from '@/utils/request';

export async function getUserPage(params) {
  return request('/api/admin/userPage', {
    method: 'GET',
    params: params
  });
}

export async function register(params) {
  return request('/api/admin/register', {
    method: 'POST',
    data: params,
  });
}

export async function update(params) {
  return request('/api/admin/update', {
    method: 'POST',
    data: params,
  });
}

export async function deleteUser(params) {
  return request('/api/admin/delete', {
    method: 'POST',
    data: params,
  });
}
