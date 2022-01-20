import {system_service} from '@/utils/request'

// 查询定时任务调度日志列表
export function listLog(query) {
  return system_service({
    url: '/quartz/log/list',
    method: 'get',
    params: query
  })
}

// 查询定时任务调度日志详细
export function getLog(jobLogId) {
  return system_service({
    url: '/quartz/log/' + jobLogId,
    method: 'get'
  })
}

// 新增定时任务调度日志
export function addLog(data) {
  return system_service({
    url: '/quartz/log',
    method: 'post',
    params: data
  })
}

// 修改定时任务调度日志
export function updateLog(data) {
  return system_service({
    url: '/quartz/log',
    method: 'put',
    params: data
  })
}

// 删除定时任务调度日志
export function delLog(jobLogId) {
  return system_service({
    url: '/quartz/log/' + jobLogId,
    method: 'delete'
  })
}

// 删除定时任务调度日志
export function cleanJobLog() {
  return system_service({
    url: '/quartz/log/clean/',
    method: 'delete'
  })
}

// 导出定时任务调度日志
export function exportLog(query) {
  return system_service({
    url: '/quartz/log/export',
    method: 'get',
    params: query
  })
}
