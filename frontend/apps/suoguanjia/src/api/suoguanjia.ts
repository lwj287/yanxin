import request from '@/utils/request'

export interface DeviceInfo {
  id: number
  deviceSn: string
  deviceName: string
  deviceType: number
  status: number
  batteryLevel: number
  location: string
  createTime: string
  updateTime: string
}

export interface DeviceUserRel {
  id: number
  deviceId: number
  userId: number
  userName: string
  authType: number
  validStartTime: string | null
  validEndTime: string | null
  createTime: string
}

export interface DeviceLog {
  id: number
  deviceId: number
  logType: number
  action: string
  operatorId: number
  content: any
  createTime: string
}

export interface DashboardStat {
  totalDevices: number
  onlineDevices: number
  faultDevices: number
  totalLogs: number
}

export function getDeviceList() {
  return request.get<DeviceInfo[]>('/suoguanjia/deviceinfo/list')
}

export function getDashboardStat() {
  return request.get<DashboardStat>('/suoguanjia/dashboard/statistics')
}

export function getAuthList() {
  return request.get<DeviceUserRel[]>('/suoguanjia/deviceuserrel/list')
}

export function addAuth(data: Partial<DeviceUserRel>) {
  return request.post('/suoguanjia/deviceuserrel/add', data)
}

export function revokeAuth(id: number) {
  return request.delete(`/suoguanjia/deviceuserrel/revoke/${id}`)
}

export function getLogList() {
  return request.get<DeviceLog[]>('/suoguanjia/devicelog/list')
}

export function remoteUnlock(deviceId: number, operatorId: number) {
  return request.post('/suoguanjia/control/unlock', { deviceId, operatorId })
}

export function lockDevice(deviceId: number, operatorId: number) {
  return request.post('/suoguanjia/control/lock', { deviceId, operatorId })
}

export function rebootDevice(deviceId: number, operatorId: number) {
  return request.post('/suoguanjia/control/reboot', { deviceId, operatorId })
}

export function applyRepair(deviceId: number, operatorId: number) {
  return request.post('/suoguanjia/control/repair', { deviceId, operatorId })
}
