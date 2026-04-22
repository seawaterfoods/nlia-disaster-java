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
