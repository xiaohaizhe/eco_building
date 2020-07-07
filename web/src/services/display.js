import request from '@/utils/request';

export async function getMap() {
  return request('/api/show/screen', {
    method: 'GET'
  });
}

// export async function register(params) {
//   return request('/api/admin/register', {
//     method: 'POST',
//     data: params,
//   });
// }
