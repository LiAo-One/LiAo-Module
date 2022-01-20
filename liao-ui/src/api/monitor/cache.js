import {system_service} from '@/utils/request'

// 查询服务器详细
export function getCache() {
  return system_service({
    url: '/monitor/cache',
    method: 'get'
  })
}
