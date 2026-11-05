import api from './api';

export const roomService = {
  // Lấy danh sách phòng (Công khai, hỗ trợ lọc & tìm kiếm)
  getAllRooms: async (params = {}) => {
    return await api.get('/rooms', { params });
  },

  // Lấy chi tiết phòng
  getRoomById: async (id) => {
    return await api.get(`/rooms/${id}`);
  },

  // Lấy danh sách loại phòng
  getAllRoomTypes: async () => {
    return await api.get('/room-types');
  },

  // Staff thêm phòng mới
  createRoom: async (roomData) => {
    return await api.post('/rooms', roomData);
  },

  // Staff sửa phòng
  updateRoom: async (id, roomData) => {
    return await api.put(`/rooms/${id}`, roomData);
  },

  // Staff xóa phòng (Hệ thống tự phân biệt Hard delete hoặc Soft delete)
  deleteRoom: async (id) => {
    return await api.delete(`/rooms/${id}`);
  },

  // Staff tạo loại phòng
  createRoomType: async (roomTypeData) => {
    return await api.post('/room-types', roomTypeData);
  },
};
