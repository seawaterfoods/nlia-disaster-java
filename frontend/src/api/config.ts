import apiClient from './client';
import type { ApiResponse, SysConfig, NdType, NdReason, AddrType } from '../types';

export const configApi = {
  // SysConfig
  listConfigs: () =>
    apiClient.get<ApiResponse<SysConfig[]>>('/api/config'),

  updateConfig: (id: string, content: string) =>
    apiClient.put<ApiResponse<SysConfig>>(`/api/config/${id}`, { content }),

  // NdType (Products)
  listProducts: () =>
    apiClient.get<ApiResponse<NdType[]>>('/api/products'),

  createProduct: (data: Omit<NdType, 'sn'>) =>
    apiClient.post<ApiResponse<NdType>>('/api/products', data),

  updateProduct: (sn: number, data: Omit<NdType, 'sn'>) =>
    apiClient.put<ApiResponse<NdType>>(`/api/products/${sn}`, data),

  deleteProduct: (sn: number) =>
    apiClient.delete<ApiResponse<void>>(`/api/products/${sn}`),

  // NdReason
  listReasons: () =>
    apiClient.get<ApiResponse<NdReason[]>>('/api/reasons'),

  createReason: (data: Pick<NdReason, 'id' | 'content'>) =>
    apiClient.post<ApiResponse<NdReason>>('/api/reasons', data),

  updateReason: (sn: number, data: Pick<NdReason, 'id' | 'content'>) =>
    apiClient.put<ApiResponse<NdReason>>(`/api/reasons/${sn}`, data),

  deleteReason: (sn: number) =>
    apiClient.delete<ApiResponse<void>>(`/api/reasons/${sn}`),

  // AddrType (Addresses)
  listAddresses: () =>
    apiClient.get<ApiResponse<AddrType[]>>('/api/addresses'),

  listCities: () =>
    apiClient.get<ApiResponse<string[]>>('/api/addresses/cities'),

  listAreas: (city: string) =>
    apiClient.get<ApiResponse<AddrType[]>>('/api/addresses/areas', { params: { city } }),
};
