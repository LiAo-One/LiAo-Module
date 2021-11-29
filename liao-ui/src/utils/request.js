import axios from 'axios'
import {Message, MessageBox, Notification} from 'element-ui'
import store from '@/store'
import {getToken} from '@/utils/auth'
import errorCode from '@/utils/errorCode'
import md5 from 'js-md5';

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'

// 创建axios实例
const system_service = axios.create({
  // axios中请求配置有baseURL选项，表示请求URL公共部分
  baseURL: process.env.VUE_APP_BASE_API,
  // 超时
  timeout: 10000
})

// System拦截器
system_service.interceptors.request.use(config => {
  // 是否需要设置 token
  const isToken = (config.headers || {}).isToken === false
  if (getToken() && !isToken) {
    config.headers['Authorization'] = 'Bearer ' + getToken() // 让每个请求携带自定义token 请根据实际情况自行修改
  }

  if (config.params) {
    if (config.params.createTime) {
      config.params.createTime = null
    }
    if (config.params.updateTime) {
      config.params.updateTime = null
    }
    if (config.params.loginTime) {
      config.params.loginTime = null
    }

    // 封装校验
    setTokenCheck(config);

  }
  return config
}, error => {
  Promise.reject(error)
})

// 响应拦截器
system_service.interceptors.response.use(res => {
    // 未设置状态码则默认成功状态
    const code = res.data.code || 200
    // 获取错误信息
    const msg = errorCode[code] || res.data.msg || errorCode['default']
    if (code === 401) {
      MessageBox.confirm('登录状态已过期，您可以继续留在该页面，或者重新登录', '系统提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        store.dispatch('LogOut').then(() => {
          location.href = '/index'
        })
      })
    } else if (code === 500) {
      Message({
        message: msg,
        type: 'error'
      })
      return Promise.reject(new Error(msg))
    } else if (code !== 200) {
      Notification.error({
        title: msg
      })
      return Promise.reject('error')
    } else {
      return res.data
    }
  },
  error => {
    console.log('err' + error)
    let {message} = error
    if (message == 'Network Error') {
      message = '后端接口连接异常'
    } else if (message.includes('timeout')) {
      message = '系统接口请求超时'
    } else if (message.includes('Request failed with status code')) {
      message = '系统接口' + message.substr(message.length - 3) + '异常'
    }
    Message({
      message: message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)


function setTokenCheck(data) {

  // 删除为空的
  Object.keys(data.params).forEach(item => {
    if (!data.params[item]) delete data.params[item]
  })

  // 排序
  let asciiSort = sort_ASCII(data.params);

  // 获取当前时间
  let timeInfo = format().toString();

  // 加密原始值
  let keys = timeInfo;

  for (const asciiSortKey in asciiSort) {
    keys += asciiSortKey;
  }

  let secret = "c353fdcac26c4035bdb123c6d8f2e2b1";


  data.headers['X-Sign'] = md5(timeInfo + keys + secret); // X-Sign

  data.headers['Time-Info'] = timeInfo; // Time-Info
}

// 格式化参数
function sort_ASCII(obj) {
  let arr = [];
  let num = 0;
  for (let i in obj) {
    if (obj[i]) {
      arr[num] = i;
      num++;
    }
  }
  let sortArr = arr.sort();
  let sortObj = {};
  for (let i in sortArr) {
    sortObj[sortArr[i]] = obj[sortArr[i]];
  }
  return sortObj;
}

// 获取当前时间
function format() {
  let date = new Date();
  let seperator1 = "-";
  let seperator2 = ":";
  let month = date.getMonth() + 1;
  let strDate = date.getDate();
  if (month >= 1 && month <= 9) {
    month = "0" + month;
  }
  if (strDate >= 0 && strDate <= 9) {
    strDate = "0" + strDate;
  }
  return date.getFullYear() + seperator1 + month + seperator1 + strDate
    + " " + date.getHours() + seperator2 + date.getMinutes()
    + seperator2 + date.getSeconds();
}

export {system_service}
