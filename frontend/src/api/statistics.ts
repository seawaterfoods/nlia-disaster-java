import apiClient from './client';
import type { ApiResponse, StatisticsRow } from '../types';

export const statisticsApi = {
  byCompany: (ndsn: number) =>
    apiClient.get<ApiResponse<StatisticsRow[]>>('/api/statistics/by-company', { params: { ndsn } }),

  byArea: (ndsn: number) =>
    apiClient.get<ApiResponse<StatisticsRow[]>>('/api/statistics/by-area', { params: { ndsn } }),

  byProduct: (ndsn: number) =>
    apiClient.get<ApiResponse<StatisticsRow[]>>('/api/statistics/by-product', { params: { ndsn } }),

  byAreaProduct: (ndsn: number) =>
    apiClient.get<ApiResponse<StatisticsRow[]>>('/api/statistics/by-area-product', { params: { ndsn } }),
};
