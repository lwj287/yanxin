import { request } from '../utils/request';

export const sendSms = (data: any) => request({ url: '/member/sendSms', method: 'POST', data });
export const login = (data: any) => request({ url: '/member/login', method: 'POST', data });
export const getUserInfo = () => request({ url: '/member/info', method: 'GET' });

export const getActivities = (data: any) => request({ url: '/activity-template/page', method: 'GET', data });
export const getActivityDetail = (id: string | number) => request({ url: `/activity-template/${id}`, method: 'GET' });
export const participateActivity = (id: string | number) => request({ url: `/activity-template/${id}/participate`, method: 'POST' });
export const getCoupons = (data: any) => request({ url: '/member-coupon/page', method: 'GET', data });
export const getOrders = (data: any) => request({ url: '/orders/page', method: 'GET', data });
export const getPointRecords = (data: any) => request({ url: '/point-record/page', method: 'GET', data });
export const getMembers = (data: any) => request({ url: '/member/page', method: 'GET', data });
