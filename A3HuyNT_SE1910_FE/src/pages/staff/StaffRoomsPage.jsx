import React, { useState, useEffect } from 'react';
import { roomService } from '../../services/roomService';

export const StaffRoomsPage = () => {
  const [rooms, setRooms] = useState([]);
  const [roomTypes, setRoomTypes] = useState([]);
  const [keyword, setKeyword] = useState('');
  const [selectedType, setSelectedType] = useState('');
  const [selectedStatus, setSelectedStatus] = useState('');
  const [loading, setLoading] = useState(true);

  // Modal State
  const [showModal, setShowModal] = useState(false);
  const [modalMode, setModalMode] = useState('create'); // 'create' or 'edit'
  const [editingRoomId, setEditingRoomId] = useState(null);
  const [formData, setFormData] = useState({
    roomNumber: '',
    roomDetailDescription: '',
    roomMaxCapacity: 2,
    roomTypeID: '',
    roomStatus: 1,
    roomPricePerDay: '',
  });

  const [message, setMessage] = useState({ type: '', text: '' });

  useEffect(() => {
    fetchRoomTypes();
    fetchRooms();
  }, []);

  const fetchRoomTypes = async () => {
    try {
      const res = await roomService.getAllRoomTypes();
      setRoomTypes(res.data || []);
      if (res.data?.length > 0 && !formData.roomTypeID) {
        setFormData((prev) => ({ ...prev, roomTypeID: res.data[0].roomTypeID }));
      }
    } catch (err) {
      console.error(err);
    }
  };

  const fetchRooms = async (kw = keyword, tId = selectedType, st = selectedStatus) => {
    setLoading(true);
    try {
      const params = {};
      if (kw) params.keyword = kw;
      if (tId) params.roomTypeId = tId;
      if (st) params.status = st;

      const res = await roomService.getAllRooms(params);
      setRooms(res.data || []);
    } catch (err) {
      setMessage({ type: 'danger', text: err.message || 'Lỗi khi tải danh sách phòng' });
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    fetchRooms(keyword, selectedType, selectedStatus);
  };

  const openCreateModal = () => {
    setModalMode('create');
    setEditingRoomId(null);
    setFormData({
      roomNumber: '',
      roomDetailDescription: '',
      roomMaxCapacity: 2,
      roomTypeID: roomTypes[0]?.roomTypeID || '',
      roomStatus: 1,
      roomPricePerDay: '',
    });
    setShowModal(true);
  };

  const openEditModal = (room) => {
    setModalMode('edit');
    setEditingRoomId(room.roomID);
    setFormData({
      roomNumber: room.roomNumber,
      roomDetailDescription: room.roomDetailDescription || '',
      roomMaxCapacity: room.roomMaxCapacity,
      roomTypeID: room.roomTypeID,
      roomStatus: room.roomStatus,
      roomPricePerDay: room.roomPricePerDay,
    });
    setShowModal(true);
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    setMessage({ type: '', text: '' });

    try {
      if (modalMode === 'create') {
        await roomService.createRoom(formData);
        setMessage({ type: 'success', text: 'Thêm mới phòng thành công!' });
      } else {
        await roomService.updateRoom(editingRoomId, formData);
        setMessage({ type: 'success', text: 'Cập nhật phòng thành công!' });
      }
      setShowModal(false);
      fetchRooms();
    } catch (err) {
      alert(err.message || 'Lỗi khi lưu thông tin phòng');
    }
  };

  const handleDeleteRoom = async (room) => {
    if (!window.confirm(`Bạn có chắc chắn muốn xóa phòng ${room.roomNumber}?`)) {
      return;
    }

    try {
      const res = await roomService.deleteRoom(room.roomID);
      // Hiển thị thông báo giải thích rõ ràng Hard delete hay Soft delete theo đề bài
      setMessage({ type: 'info', text: res.message });
      fetchRooms();
    } catch (err) {
      setMessage({ type: 'danger', text: err.message || 'Xóa phòng thất bại' });
    }
  };

  const formatCurrency = (val) => {
    if (!val) return '0 VNĐ';
    return Number(val).toLocaleString('vi-VN') + ' VNĐ';
  };

  return (
    <div className="container">
      <div className="section-header">
        <div>
          <h2>Quản Lý Thông Tin Phòng (Staff)</h2>
          <p>Xem, thêm, sửa, xóa và theo dõi trạng thái các phòng khách sạn</p>
        </div>
        <button onClick={openCreateModal} className="btn btn-primary">
          + Thêm Phòng Mới
        </button>
      </div>

      {message.text && (
        <div className={`alert alert-${message.type}`}>
          <strong>{message.type === 'info' ? 'ℹ️ Quy tắc xóa phòng:' : 'Thông báo:'}</strong> {message.text}
        </div>
      )}

      {/* Filter */}
      <div className="card filter-card">
        <form onSubmit={handleSearch} className="filter-form">
          <div className="form-group flex-1">
            <input
              type="text"
              placeholder="Tìm theo số phòng hoặc mô tả..."
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              className="form-control"
            />
          </div>

          <div className="form-group">
            <select
              value={selectedType}
              onChange={(e) => setSelectedType(e.target.value)}
              className="form-control"
            >
              <option value="">Tất cả loại phòng</option>
              {roomTypes.map((rt) => (
                <option key={rt.roomTypeID} value={rt.roomTypeID}>
                  {rt.roomTypeName}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <select
              value={selectedStatus}
              onChange={(e) => setSelectedStatus(e.target.value)}
              className="form-control"
            >
              <option value="">Tất cả trạng thái</option>
              <option value="1">Đang hoạt động</option>
              <option value="2">Ngưng hoạt động / Đã xóa</option>
            </select>
          </div>

          <button type="submit" className="btn btn-primary">
            Lọc Dữ Liệu
          </button>
        </form>
      </div>

      {/* Room Table */}
      <div className="card">
        {loading ? (
          <div className="loading-state">Đang tải danh sách phòng...</div>
        ) : rooms.length === 0 ? (
          <div className="empty-state">Không có phòng nào trong danh sách.</div>
        ) : (
          <div className="table-responsive">
            <table className="table">
              <thead>
                <tr>
                  <th>Mã</th>
                  <th>Số phòng</th>
                  <th>Loại phòng</th>
                  <th>Sức chứa</th>
                  <th>Giá thuê / ngày</th>
                  <th>Trạng thái</th>
                  <th style={{ textAlign: 'center' }}>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {rooms.map((r) => (
                  <tr key={r.roomID} className={r.roomStatus === 2 ? 'row-inactive' : ''}>
                    <td>#{r.roomID}</td>
                    <td>
                      <strong>Phòng {r.roomNumber}</strong>
                    </td>
                    <td>{r.roomTypeName}</td>
                    <td>{r.roomMaxCapacity} người</td>
                    <td>{formatCurrency(r.roomPricePerDay)}</td>
                    <td>
                      {r.roomStatus === 1 ? (
                        <span className="badge badge-success">Đang hoạt động</span>
                      ) : (
                        <span className="badge badge-danger">Ngưng / Soft-deleted</span>
                      )}
                    </td>
                    <td style={{ textAlign: 'center' }}>
                      <button
                        onClick={() => openEditModal(r)}
                        className="btn btn-outline btn-sm"
                        style={{ marginRight: '8px' }}
                      >
                        Sửa
                      </button>
                      <button
                        onClick={() => handleDeleteRoom(r)}
                        className="btn btn-outline-danger btn-sm"
                        title="Xóa phòng (Hệ thống tự động Hard delete nếu chưa có booking, Soft delete nếu đã có)"
                      >
                        Xóa
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal Thêm / Sửa Phòng */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>{modalMode === 'create' ? 'Thêm Mới Phòng' : 'Cập Nhật Thông Tin Phòng'}</h3>
              <button onClick={() => setShowModal(false)} className="close-btn">
                ×
              </button>
            </div>
            <form onSubmit={handleFormSubmit}>
              <div className="modal-body">
                <div className="form-group">
                  <label>Số phòng *</label>
                  <input
                    type="text"
                    required
                    className="form-control"
                    placeholder="VD: 101, 201..."
                    value={formData.roomNumber}
                    onChange={(e) => setFormData({ ...formData, roomNumber: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label>Loại phòng *</label>
                  <select
                    required
                    className="form-control"
                    value={formData.roomTypeID}
                    onChange={(e) => setFormData({ ...formData, roomTypeID: e.target.value })}
                  >
                    {roomTypes.map((rt) => (
                      <option key={rt.roomTypeID} value={rt.roomTypeID}>
                        {rt.roomTypeName}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label>Sức chứa tối đa (người) *</label>
                  <input
                    type="number"
                    min="1"
                    required
                    className="form-control"
                    value={formData.roomMaxCapacity}
                    onChange={(e) =>
                      setFormData({ ...formData, roomMaxCapacity: Number(e.target.value) })
                    }
                  />
                </div>

                <div className="form-group">
                  <label>Giá thuê theo ngày (VNĐ) *</label>
                  <input
                    type="number"
                    min="1"
                    required
                    className="form-control"
                    placeholder="VD: 350000"
                    value={formData.roomPricePerDay}
                    onChange={(e) =>
                      setFormData({ ...formData, roomPricePerDay: e.target.value })
                    }
                  />
                </div>

                <div className="form-group">
                  <label>Trạng thái phòng</label>
                  <select
                    className="form-control"
                    value={formData.roomStatus}
                    onChange={(e) =>
                      setFormData({ ...formData, roomStatus: Number(e.target.value) })
                    }
                  >
                    <option value={1}>1: Đang hoạt động (Active)</option>
                    <option value={2}>2: Ngưng hoạt động / Đã xóa (Inactive)</option>
                  </select>
                </div>

                <div className="form-group">
                  <label>Mô tả chi tiết phòng</label>
                  <textarea
                    rows="3"
                    className="form-control"
                    placeholder="Mô tả tiện nghi, vị trí, ban công..."
                    value={formData.roomDetailDescription}
                    onChange={(e) =>
                      setFormData({ ...formData, roomDetailDescription: e.target.value })
                    }
                  ></textarea>
                </div>
              </div>

              <div className="modal-footer">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="btn btn-outline"
                >
                  Hủy Bỏ
                </button>
                <button type="submit" className="btn btn-primary">
                  {modalMode === 'create' ? 'Tạo Phòng' : 'Lưu Thay Đổi'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
