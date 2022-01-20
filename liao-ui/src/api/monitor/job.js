import {system_service} from '@/utils/request'

// 查询定时任务调度列表
export function listJob(query) {
  return system_service({
    url: '/quartz/job/list',
    method: 'get',
    params: query
  })
}

// 查询定时任务调度详细
export function getJob(jobId) {
  return system_service({
    url: '/quartz/job/' + jobId,
    method: 'get'
  })
}

// 新增定时任务调度
export function addJob(data) {
  return system_service({
    url: '/quartz/job',
    method: 'post',
    params: data
  })
}

// 修改定时任务调度
export function updateJob(data) {
  return system_service({
    url: '/quartz/job',
    method: 'put',
    params: data
  })
}

// 删除定时任务调度
export function delJob(jobId) {
  return system_service({
    url: '/quartz/job/' + jobId,
    method: 'delete'
  })
}

// 导出定时任务调度
export function exportJob(query) {
  return system_service({
    url: '/quartz/job/export',
    method: 'get',
    params: query
  })
}

// 任务状态修改
export function changeJobStatus(jobId, status) {
  return system_service({
    url: '/quartz/job/changeStatus',
    method: 'put',
    params: {
      jobId,
      status
    }
  })
}

export function runJob(jobId, jobGroup) {
  return system_service({
    url: '/quartz/job/run',
    method: 'put',
    params: {
      jobId,
      jobGroup
    }
  })
}

