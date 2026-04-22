export interface UserInfo {
  sn: number;
  email: string;
  name: string;
  title: string;
  cid: string;
  companyName: string;
  level: number;
  insurance: string;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface Disaster {
  sn: number;
  id: string;
  title: string;
  content: string;
  ddate: string;
  adate: string;
  sdate: string;
  vdate: string;
  claimDate: string;
  claimValid: string;
  allowIns: string;
  emailNotice: string;
  reason: string;
  df: string;
  showStatus: string;
  qstatus: string;
  qdate: string;
  claimAlert: number;
  authorCid: string;
  authorSn: number;
}

export interface DisasterRequest {
  title: string;
  content?: string;
  ddate?: string;
  adate?: string;
  sdate?: string;
  vdate?: string;
  claimDate?: string;
  claimValid?: string;
  allowIns?: string;
  emailNotice?: string;
  reason?: string;
  df?: string;
  showStatus?: string;
  qstatus?: string;
  claimAlert?: number;
}

export interface ReportMain {
  sn: number;
  ndsn: number;
  cid: string;
  companyName: string;
  nd: string;
  nd1: string;
  nd2: string;
  nd3: string;
  nd4: string;
  nd5: string;
  closs: string;
  author: string;
  adate: string;
  udate: string;
}

export interface ReportDetail {
  sn: number;
  ndsn: number;
  cid: string;
  bid: string;
  zip: string;
  city: string;
  area: string;
  hname: string;
  bname: string;
  pname: string;
  ndTypeSn: number;
  ndDate: string;
  preCost: number;
  preInum: number;
  preDnum: number;
  commited: number;
  pending: number;
  prepay: number;
  close: string;
  memo: string;
  showStatus: string;
  adate: string;
  udate: string;
}

export interface ReportDetailRequest {
  ndsn: number;
  cid: string;
  bid?: string;
  zip?: string;
  city?: string;
  area?: string;
  hname?: string;
  bname?: string;
  pname?: string;
  ndTypeSn?: number;
  ndDate?: string;
  preCost?: number;
  preInum?: number;
  preDnum?: number;
  commited?: number;
  pending?: number;
  prepay?: number;
  close?: string;
  memo?: string;
}

export interface Account {
  sn: number;
  cid: string;
  companyName: string;
  email: string;
  name: string;
  title: string;
  tel: string;
  mobile: string;
  email2: string;
  alevel: number;
  insurance: string;
  status: string;
  lastlogin: string;
  ip: string;
}

export interface AccountCreateRequest {
  email: string;
  password: string;
  name: string;
  title?: string;
  tel?: string;
  mobile?: string;
  email2?: string;
  alevel: number;
  insurance?: string;
  cid: string;
}

export interface AccountUpdateRequest {
  name?: string;
  title?: string;
  tel?: string;
  mobile?: string;
  email2?: string;
  alevel?: number;
  insurance?: string;
  status?: string;
}

export interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface Syslog {
  sn: number;
  adminsn: number;
  alevel: number;
  cid: string;
  company: string;
  loginid: string;
  action: string;
  mstatus: string;
  fromip: string;
  adate: number;
  detail: string;
  debug: string;
}

export interface SysConfig {
  sn: number;
  id: string;
  title: string;
  content: string;
  mstatus: string;
}

export interface NdType {
  sn: number;
  hname: string;
  bname: string;
  pname: string;
  hsort: number;
}

export interface NdReason {
  sn: number;
  id: string;
  content: string;
  adate: string;
  udate: string;
}

export interface AddrType {
  asn: string;
  csn: string;
  cname: string;
  aname: string;
}

export interface Company {
  cid: string;
  cname: string;
  name: string;
  domain: string;
  tel: string;
  fax: string;
  address: string;
  status: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

export type StatisticsRow = Record<string, string | number>;
