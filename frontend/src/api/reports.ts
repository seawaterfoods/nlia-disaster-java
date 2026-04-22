import apiClient from './client';
import type { ApiResponse, ReportMain, ReportDetail, ReportDetailRequest } from '../types';

export const reportApi = {
  listMains: (ndsn: number) =>
    apiClient.get<ApiResponse<ReportMain[]>>(`/api/reports/main/${ndsn}`),

  getMain: (ndsn: number, cid: string) =>
    apiClient.get<ApiResponse<ReportMain>>(`/api/reports/main/${ndsn}/${cid}`),

  ensureMain: (ndsn: number) =>
    apiClient.post<ApiResponse<void>>('/api/reports/main/ensure', { ndsn }),

  updateMainStatus: (ndsn: number, cid: string, field: string, status: string) =>
    apiClient.put<ApiResponse<ReportMain>>(`/api/reports/main/${ndsn}/${cid}/status`, { field, status }),

  listDetails: (ndsn: number, cid: string) =>
    apiClient.get<ApiResponse<ReportDetail[]>>(`/api/reports/detail/${ndsn}/${cid}`),

  createDetail: (data: ReportDetailRequest) =>
    apiClient.post<ApiResponse<ReportDetail>>('/api/reports/detail', data),

  updateDetail: (sn: number, data: ReportDetailRequest) =>
    apiClient.put<ApiResponse<ReportDetail>>(`/api/reports/detail/${sn}`, data),

  deleteDetail: (sn: number) =>
    apiClient.delete<ApiResponse<void>>(`/api/reports/detail/${sn}`),
};
