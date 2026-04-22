import apiClient from './client';
import type {
  ApiResponse, Account, AccountCreateRequest,
  AccountUpdateRequest, ChangePasswordRequest, Page, Company,
} from '../types';

interface AccountSearchParams {
  email?: string;
  name?: string;
  cid?: string;
  page?: number;
  size?: number;
}

export const accountApi = {
  search: (params: AccountSearchParams = {}) =>
    apiClient.get<ApiResponse<Page<Account>>>('/api/accounts', { params }),

  get: (sn: number) =>
    apiClient.get<ApiResponse<Account>>(`/api/accounts/${sn}`),

  create: (data: AccountCreateRequest) =>
    apiClient.post<ApiResponse<Account>>('/api/accounts', data),

  update: (sn: number, data: AccountUpdateRequest) =>
    apiClient.put<ApiResponse<Account>>(`/api/accounts/${sn}`, data),

  delete: (sn: number) =>
    apiClient.delete<ApiResponse<void>>(`/api/accounts/${sn}`),

  changePassword: (sn: number, data: ChangePasswordRequest) =>
    apiClient.put<ApiResponse<void>>(`/api/accounts/${sn}/password`, data),

  listCompanies: (filter: 'active' | 'all' = 'active') =>
    apiClient.get<ApiResponse<Company[]>>('/api/accounts/companies', { params: { filter } }),
};
