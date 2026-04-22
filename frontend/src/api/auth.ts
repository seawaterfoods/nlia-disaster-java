import apiClient from './client';

export interface LoginRequest {
  email: string;
  password: string;
}

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

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  user: UserInfo;
  emailFailureNotifications: string[];
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export const authApi = {
  login: (data: LoginRequest) =>
    apiClient.post<ApiResponse<LoginResponse>>('/api/auth/login', data),

  logout: () =>
    apiClient.post<ApiResponse<void>>('/api/auth/logout'),

  forgotPassword: (email: string) =>
    apiClient.post<ApiResponse<{ message: string }>>('/api/auth/forgot-password', { email }),
};
