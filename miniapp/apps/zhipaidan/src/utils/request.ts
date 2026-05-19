const BASE_URL = process.env.NODE_ENV === 'development' ? 'http://127.0.0.1:8086' : 'https://api.yanxinzhipaidan.com';

export const request = <T = any>(options: UniApp.RequestOptions): Promise<T> => {
  return new Promise((resolve, reject) => {
    uni.request({
      ...options,
      url: options.url.startsWith('http') ? options.url : BASE_URL + options.url,
      header: {
        'Content-Type': 'application/json',
        'Authorization': uni.getStorageSync('token') || '',
        ...options.header
      },
      success: (res: any) => {
        if (res.statusCode === 200) {
          const data = res.data;

          // 如果返回的不是统一封装的Result结构，直接返回数据
          if (data.code === undefined) {
            resolve(data);
            return;
          }

          if (data.code === 200) {
            resolve(data.data);
          } else if (data.code === 401) {
            uni.removeStorageSync('token');
            // 静默处理 401，由业务层决定是否需要引导登录
            reject(new Error(data.message || 'Error'));
          } else {
            uni.showToast({ title: data.message || '请求失败', icon: 'none' });
            reject(new Error(data.message || 'Error'));
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