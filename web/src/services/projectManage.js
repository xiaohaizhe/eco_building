import request from '@/utils/request';

//项目分页
export async function getProjectPage(params) {
  return request('/api/project/page', {
    method: 'GET',
    params: params
  });
}
//导入
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

//删除
export async function deleteProject(params) {
  return request('/api/project/delete', {
    method: 'POST',
    data: params,
  });
}
//详情
export async function getProjectDetail(params) {
  return request('/api/show/projectDetail', {
    method: 'GET',
    params: params
  });
}

//折线图
export async function getDataByTime(params) {
  return request('/api/project/getDataByTime', {
    method: 'GET',
    params: params
  });
}