import React, { useState, useEffect } from 'react';
import { roomService } from '../../services/roomService';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export const RoomsPage = () => {
  const [rooms, setRooms] = useState([]);
  const [roomTypes, setRoomTypes] = useState([]);
  const [keyword, setKeyword] = useState('');
  const [selectedType, setSelectedType] = useState('');
  const [loading, setLoading] = useState(true);
  const [selectedRoom, setSelectedRoom] = useState(null);

  const { isCustomer } = useAuth();

  useEffect(() => {
    fetchRoomTypes();
    fetchRooms();
  }, []);

  const fetchRoomTypes = async () => {
    try {
      const res = await roomService.getAllRoomTypes();
      setRoomTypes(res.data || []);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchRooms = async (searchKey = keyword, typeId = selectedType) => {
    setLoading(true);
    try {
      const params = {};
      if (searchKey) params.keyword = searchKey;
      if (typeId) params.roomTypeId = typeId;
      // Khách xem phòng thì ưu tiên xem phòng hoạt động (status = 1)
      params.status = 1;

      const res = await roomService.getAllRooms(params);
      setRooms(res.data || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    fetchRooms(keyword, selectedType);
  };

  const handleReset = () => {
    setKeyword('');
    setSelectedType('');
    fetchRooms('', '');
  };

  const formatCurrency = (val) => {
    if (!val) return '0 VNĐ';
    return Number(val).toLocaleString('vi-VN') + ' VNĐ';
  };

  return (
    <div className="container">
      {/* Banner */}
      <div className="hero-banner">
        <h1>Chào mừng đến với FUMiniHotelSystem</h1>
        <p>Hệ thống phòng nghỉ tiện nghi, dịch vụ chu đáo và giá cả hợp lý</p>
      </div>

      {/* Filter / Search Bar */}
      <div className="card filter-card">
        <form onSubmit={handleSearch} className="filter-form">
          <div className="form-group flex-1">
            <input
              type="text"
              placeholder="Tìm kiếm số phòng hoặc mô tả..."
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

          <button type="submit" className="btn btn-primary">
            🔍 Tìm Kiếm
          </button>
          <button type="button" onClick={handleReset} className="btn btn-outline">
            Làm Mới
          </button>
        </form>
      </div>

      {/* Danh sách phòng */}
      <div className="section-header">
        <h2>Danh Sách Phòng Khách Sạn</h2>
        <span className="badge badge-info">{rooms.length} phòng sẵn sàng</span>
      </div>

      {loading ? (
        <div className="loading-state">Đang tải danh sách phòng...</div>
      ) : rooms.length === 0 ? (
        <div className="empty-state">Không tìm thấy phòng phù hợp với tiêu chí tìm kiếm.</div>
      ) : (
        <div className="room-grid">
          {rooms.map((room) => (
            <div key={room.roomID} className="room-card card">
              <div className="room-card-header">
                <span className="room-number">Phòng {room.roomNumber}</span>
                <span className="room-type-tag">{room.roomTypeName}</span>
              </div>
              <div className="room-card-body">
                <p className="room-desc">{room.roomDetailDescription || 'Đầy đủ tiện nghi cơ bản'}</p>
                <div className="room-specs">
                  <span>👥 Sức chứa: <strong>{room.roomMaxCapacity} người</strong></span>
                </div>
                <div className="room-price">
                  <span className="price-val">{formatCurrency(room.roomPricePerDay)}</span>
                  <span className="price-unit">/ đêm</span>
                </div>
              </div>
              <div className="room-card-footer">
                <button
                  onClick={() => setSelectedRoom(room)}
                  className="btn btn-outline btn-sm flex-1"
                >
                  Chi Tiết
                </button>
                {isCustomer ? (
                  <Link
                    to={`/booking?roomId=${room.roomID}`}
                    className="btn btn-primary btn-sm flex-1"
                  >
                    Đặt Phòng
                  </Link>
                ) : (
                  <Link to="/login" className="btn btn-primary btn-sm flex-1">
                    Đăng Nhập Để Đặt
                  </Link>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Modal Chi tiết phòng */}
      {selectedRoom && (
        <div className="modal-overlay" onClick={() => setSelectedRoom(null)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Thông Tin Chi Tiết Phòng {selectedRoom.roomNumber}</h3>
              <button onClick={() => setSelectedRoom(null)} className="close-btn">
                ×
              </button>
            </div>
            <div className="modal-body">
              <p><strong>Loại phòng:</strong> {selectedRoom.roomTypeName}</p>
              <p><strong>Sức chứa tối đa:</strong> {selectedRoom.roomMaxCapacity} người</p>
              <p><strong>Giá thuê theo ngày:</strong> {formatCurrency(selectedRoom.roomPricePerDay)}</p>
              <p><strong>Mô tả chi tiết:</strong></p>
              <div className="detail-box">{selectedRoom.roomDetailDescription}</div>
            </div>
            <div className="modal-footer">
              <button onClick={() => setSelectedRoom(null)} className="btn btn-outline">
                Đóng
              </button>
              {isCustomer ? (
                <Link
                  to={`/booking?roomId=${selectedRoom.roomID}`}
                  className="btn btn-primary"
                  onClick={() => setSelectedRoom(null)}
                >
                  Tiến Hành Đặt Phòng
                </Link>
              ) : (
                <Link
                  to="/login"
                  className="btn btn-primary"
                  onClick={() => setSelectedRoom(null)}
                >
                  Đăng Nhập Để Đặt Phòng
                </Link>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
