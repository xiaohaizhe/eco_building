import request from '@/utils/request';

export async function getTop10(params) {
  return request('/api/overview/top10', {
    method: 'GET'
  });
}

export async function getTop5() {
  return request('/api/overview/top5', {
    method: 'GET'
  });
}

export async function getTypeData() {
  return request('/api/overview/statistic', {
    method: 'GET'
  });
}

export async function getExcel() {
  return request('/api/overview/excel', {
    method: 'GET'
  });
}