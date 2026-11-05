import api from './api';

export const customerService = {
  // Customer xem profile cá nhân
  getMyProfile: async () => {
    return await api.get('/customers/profile');
  },

  // Customer cập nhật profile cá nhân
  updateMyProfile: async (profileData) => {
    return await api.put('/customers/profile', profileData);
  },

  // Staff xem danh sách toàn bộ khách hàng
  getAllCustomers: async (params = {}) => {
    return await api.get('/customers', { params });
  },

  // Staff xem chi tiết khách hàng
  getCustomerById: async (id) => {
    return await api.get(`/customers/${id}`);
  },

  // Staff tạo mới khách hàng
  createCustomer: async (customerData) => {
    return await api.post('/customers', customerData);
  },

  // Staff cập nhật khách hàng
  updateCustomer: async (id, customerData) => {
    return await api.put(`/customers/${id}`, customerData);
  },

  // Staff xóa khách hàng (Hard delete hoặc Soft delete)
  deleteCustomer: async (id) => {
    return await api.delete(`/customers/${id}`);
  },
};
