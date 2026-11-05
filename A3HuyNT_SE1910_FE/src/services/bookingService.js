import api from './api';

export const bookingService = {
  // Customer tạo đơn đặt phòng trực tuyến (chọn 1 hoặc nhiều phòng)
  createBooking: async (bookingData) => {
    return await api.post('/bookings', bookingData);
  },

  // Customer xem lịch sử đặt phòng của mình
  getMyHistory: async () => {
    return await api.get('/bookings/my-history');
  },

  // Xem chi tiết một đơn đặt phòng
  getBookingById: async (id) => {
    return await api.get(`/bookings/${id}`);
  },

  // Staff xem tất cả đơn đặt phòng
  getAllBookings: async (params = {}) => {
    return await api.get('/bookings', { params });
  },

  // Staff cập nhật trạng thái đơn (1: Xác nhận, 2: Hoàn thành, 3: Đã hủy)
  updateBookingStatus: async (id, bookingStatus) => {
    return await api.put(`/bookings/${id}/status`, { bookingStatus });
  },
};
