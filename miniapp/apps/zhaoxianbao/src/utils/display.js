export const jobTypeMap = { CLEANER: '保洁', NANNY: '保姆' }
export const educationMap = { MIDDLE: '初中', HIGH: '高中', COLLEGE: '大专' }
export const cityMap = { SZ: '深圳', GZ: '广州', NS: '南山', TH: '天河' }
export const skillMap = { COOKING: '做饭', BABYCARE: '育儿', ELDERCARE: '护老' }
export const interviewStatusMap = { PENDING: '待面试', IN_PROGRESS: '面试中', PASS: '合格', REJECT: '不合格' }
export const onboardStatusMap = { PENDING_DOCS: '待材料', WAIT_ONBOARD: '待入职', ONBOARDED: '已入职' }

export const formatCodeList = (txt, map) => {
  if (!txt) return '-'
  return txt.split(',').map((i) => map[i] || i).join('、')
}

export const yesNo = (v) => (Number(v) === 1 ? '已完成' : '未完成')
