import { request } from '../utils/request';

// 登录 (Mock)
export const login = (phone: string) => {
  return request({
    url: `/zhipaidan/staff/phone/${phone}`,
    method: 'GET'
  }).then((res: any) => {
    if (!res) {
      throw new Error('未找到该手机号对应的员工');
    }
    return res;
  });
};

// 获取家政员状态列表
export const getStaffList = () => {
  return request({
    url: '/zhipaidan/staff/list',
    method: 'GET'
  });
};

// 获取单个家政员状态
export const getStaffById = (id: number) => {
  return request({
    url: `/zhipaidan/staff/${id}`,
    method: 'GET'
  });
};

// 更新家政员信息
export const updateStaff = (data: any) => {
  return request({
    url: '/zhipaidan/staff/update',
    method: 'PUT',
    data
  });
};

// 获取派单记录
export const getDispatchRecords = (staffId?: number) => {
  return request({
    url: '/zhipaidan/dispatch/list',
    method: 'GET',
    data: staffId !== undefined ? { staffId } : {}
  });
};

// 开始服务
export const startDispatchService = (id: number) => {
  return request({
    url: `/zhipaidan/dispatch/${id}/start`,
    method: 'PUT'
  });
};

// 完成服务
export const finishDispatchService = (id: number) => {
  return request({
    url: `/zhipaidan/dispatch/${id}/finish`,
    method: 'PUT'
  });
};

// 获取单个订单信息
export const getOrderById = (id: number) => {
  return request({
    url: `/zhipaidan/order/${id}`,
    method: 'GET'
  });
};
