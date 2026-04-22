import apiClient from './client';
import type { ApiResponse, Disaster, DisasterRequest } from '../types';

export const disasterApi = {
  list: (filter: 'active' | 'all' = 'active') =>
    apiClient.get<ApiResponse<Disaster[]>>('/api/disasters', { params: { filter } }),

  get: (sn: number) =>
    apiClient.get<ApiResponse<Disaster>>(`/api/disasters/${sn}`),

  create: (data: DisasterRequest) =>
    apiClient.post<ApiResponse<Disaster>>('/api/disasters', data),

  update: (sn: number, data: DisasterRequest) =>
    apiClient.put<ApiResponse<Disaster>>(`/api/disasters/${sn}`, data),

  delete: (sn: number) =>
    apiClient.delete<ApiResponse<void>>(`/api/disasters/${sn}`),
};
