export interface Staff {
  staffId: number;
  roleId: number;
  phone: string;
  password?: string;
  realName: string;
  avatarUrl?: string;
  status: number;
  createTime: string;
  updateTime?: string;
}

export interface RealnameAuth {
  authId: number;
  staffId: number;
  staffType: number;
  idCard: string;
  faceMaterialUrl?: string;
  authStatus: number;
  authTime?: string;
  authReport?: string;
  createTime: string;
}

export interface Contract {
  contractId: number;
  staffId: number;
  templateId: number;
  contractContent?: string;
  signTime?: string;
  contractStatus: number;
  expireTime?: string;
  storageUrl?: string;
  createTime: string;
}

export interface ContractTemplate {
  templateId?: number;
  templateName: string;
  contractType: number;
  templateContent: string;
  status: number;
  createTime?: string;
}

export interface InsuranceProduct {
  productId: number;
  productName: string;
  insuranceType: number;
  coverageAmount: number;
  premium: number;
  durationDays: number;
  conditions?: string;
  status: number;
  createTime: string;
}

export interface InsuranceOrder {
  orderId: number;
  orderNo?: string;
  staffId: number;
  productId: number;
  premium: number;
  insureTime?: string;
  expireTime?: string;
  claimStatus: number;
  status: number;
  createTime: string;
}

export interface ClaimRecord {
  claimId: number;
  orderId: number;
  orderNo?: string;
  staffId: number;
  claimAmount: number;
  materialUrl?: string;
  claimStatus: number;
  applyTime?: string;
  claimTime?: string;
  createTime: string;
}

export interface Claim {
  claimId: number;
  staffId: number;
  orderId: number;
  claimAmount: number;
  materialUrl?: string;
  claimStatus: number;
  applyTime?: string;
  claimTime?: string;
  createTime: string;
}
