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
