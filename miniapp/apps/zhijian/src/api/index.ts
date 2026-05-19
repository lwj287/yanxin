import { request, BASE_URL } from '../utils/request';

// 创建一个新的质检任务
export const createQualityTask = (data: any) => {
  return request({
    url: '/zhijian/task/save',
    method: 'POST',
    data
  });
};

// 获取任务详情
export const getTaskDetail = (id: number) => {
  return request({
    url: `/zhijian/task/detail/${id}`,
    method: 'GET'
  });
};

// 上传图片（需要用 uni.uploadFile 单独处理，因为 request 是 uni.request）
export const uploadTaskImage = (taskId: number, filePath: string) => {
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: BASE_URL + '/zhijian/task/upload-image',
      filePath: filePath,
      name: 'file',
      formData: {
        taskId: taskId.toString()
      },
      success: (res) => {
        if (res.statusCode === 200) {
          const data = JSON.parse(res.data);
          if (data.code === 200) {
            resolve(data.data);
          } else {
            uni.showToast({ title: data.message || '上传失败', icon: 'none' });
            reject(new Error(data.message || 'Upload Error'));
          }
        } else {
          uni.showToast({ title: '网络错误', icon: 'none' });
          reject(new Error('Network Error'));
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络连接失败', icon: 'none' });
        reject(err);
      }
    });
  });
};
