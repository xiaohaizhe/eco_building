import request from '@/utils/request';

export async function getProjectPage(params) {
  return request('/api/project/page', {
    method: 'GET',
    params: params
  });
}

export async function importExcel(params) {
  return request('/api/project/importExcel', {
    method: 'POST',
    data: params,
  });
}

// export async function update(params) {
//   return request('/api/admin/update', {
//     method: 'POST',
//     data: params,
//   });
// }

export async function deleteProject(params) {
  return request('/api/project/delete', {
    method: 'POST',
    data: params,
  });
}
