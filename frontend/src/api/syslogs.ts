import apiClient from './client';
import type { ApiResponse, Syslog, Page } from '../types';

interface SyslogSearchParams {
  adminsn?: number;
  cid?: string;
  action?: string;
  page?: number;
  size?: number;
}

export const syslogApi = {
  search: (params: SyslogSearchParams = {}) =>
    apiClient.get<ApiResponse<Page<Syslog>>>('/api/syslogs', { params }),
};
