import { request } from '../utils/request';

export const login = (data: any) => request({ url: '/auth/login', method: 'POST', data });
export const getUserInfo = () => request({ url: '/auth/info', method: 'GET' });
export const getCourses = (data: any) => request({ url: '/course/page', method: 'GET', data });
export const getCourseDetail = (id: number) => request({ url: `/course/${id}`, method: 'GET' });
export const getExams = (data: any) => request({ url: '/exam/page', method: 'GET', data });
export const getCerts = (data: any) => request({ url: '/cert/page', method: 'GET', data });
