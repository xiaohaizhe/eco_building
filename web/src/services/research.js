import request from '@/utils/request';

export async function getList(params) {
  return request('/api/r_project/list', {
    method: 'GET',
    params:params
  });
}
//详情
export async function getDetail(params) {
  return request('/api/r_project/findById', {
    method: 'GET',
    params: params
  });
}
//暖通设备信息
export async function getHVAC(params) {
  return request('/api/r_project/getHVACEquipmentById', {
    method: 'GET',
    params: params
  });
}
//电气设备信息
export async function getElectrical(params) {
  return request('/api/r_project/getElectricalEquipmentById', {
    method: 'GET',
    params: params
  });
}
//获取气能耗数据
export async function getGasData(params) {
  return request('/api/r_project/getGasData', {
    method: 'GET',
    params: params
  });
}
//获取电能耗数据
export async function getElecData(params) {
  return request('/api/r_project/getElecData', {
    method: 'GET',
    params: params
  });
}
//获取热能耗数据
export async function getHeatData(params) {
  return request('/api/r_project/getHeatData', {
    method: 'GET',
    params: params
  });
}
//获取水能耗数据
export async function getWaterData(params) {
  return request('/api/r_project/getWaterData', {
    method: 'GET',
    params: params
  });
}
//导入
export async function importExcel(params) {
  return request('/api/r_project/import', {
    method: 'POST',
    data: params,
  });
}