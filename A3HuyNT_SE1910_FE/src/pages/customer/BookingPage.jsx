import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { roomService } from '../../services/roomService';
import { bookingService } from '../../services/bookingService';

export const BookingPage = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const [rooms, setRooms] = useState([]);
  const [loadingRooms, setLoadingRooms] = useState(true);

  // Mặc định ngày nhận phòng là ngày mai, ngày trả phòng là ngày kia
  const getDefaultDates = () => {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const dayAfter = new Date();
    dayAfter.setDate(dayAfter.getDate() + 2);

    return {
      startDate: tomorrow.toISOString().split('T')[0],
      endDate: dayAfter.toISOString().split('T')[0],
    };
  };

  const [bookingDates, setBookingDates] = useState(getDefaultDates());
  const [selectedRoomIds, setSelectedRoomIds] = useState([]);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    fetchAvailableRooms();
  }, []);

  const fetchAvailableRooms = async () => {
    try {
      const res = await roomService.getAllRooms({ status: 1 });
      const availableRooms = res.data || [];
      setRooms(availableRooms);

      // Nếu có roomId từ URL query parameter, tự động chọn phòng đó
      const preselectId = searchParams.get('roomId');
      if (preselectId && availableRooms.some((r) => r.roomID === Number(preselectId))) {
        setSelectedRoomIds([Number(preselectId)]);
      }
    } catch (err) {
      setError(err.message || 'Không thể tải danh sách phòng');
    } finally {
      setLoadingRooms(false);
    }
  };

  const handleRoomToggle = (roomId) => {
    if (selectedRoomIds.includes(roomId)) {
      setSelectedRoomIds(selectedRoomIds.filter((id) => id !== roomId));
    } else {
      setSelectedRoomIds([...selectedRoomIds, roomId]);
    }
  };

  // Tính số đêm ở
  const calculateDays = () => {
    const start = new Date(bookingDates.startDate);
    const end = new Date(bookingDates.endDate);
    const diffTime = end - start;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays > 0 ? diffDays : 0;
  };

  const days = calculateDays();

  // Tính tổng tiền ước tính
  const calculateTotal = () => {
    if (days <= 0) return 0;
    return selectedRoomIds.reduce((sum, id) => {
      const room = rooms.find((r) => r.roomID === id);
      return sum + (room ? Number(room.roomPricePerDay) * days : 0);
    }, 0);
  };

  const formatCurrency = (val) => {
    if (!val) return '0 VNĐ';
    return Number(val).toLocaleString('vi-VN') + ' VNĐ';
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (days <= 0) {
      setError('Ngày trả phòng phải sau ngày nhận phòng');
      return;
    }

    if (selectedRoomIds.length === 0) {
      setError('Vui lòng chọn ít nhất 1 phòng');
      return;
    }

    setSubmitting(true);

    try {
      const items = selectedRoomIds.map((id) => ({
        roomID: id,
        startDate: bookingDates.startDate,
        endDate: bookingDates.endDate,
      }));

      const res = await bookingService.createBooking({ items });
      setSuccess('Đặt phòng thành công! Đang chuyển hướng sang lịch sử đặt phòng...');
      setTimeout(() => {
        navigate('/my-bookings');
      }, 1500);
    } catch (err) {
      setError(err.message || 'Đặt phòng thất bại. Vui lòng kiểm tra lại.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="container" style={{ maxWidth: '900px' }}>
      <div className="card">
        <div className="card-header">
          <h2>Đặt Phòng Trực Tuyến (Online Booking)</h2>
          <p>Chọn ngày lưu trú và chọn một hoặc nhiều phòng phù hợp</p>
        </div>

        {error && <div className="alert alert-danger">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        <form onSubmit={handleSubmit}>
          {/* Chọn ngày */}
          <div className="date-picker-row">
            <div className="form-group flex-1">
              <label>📅 Ngày nhận phòng (Start Date) *</label>
              <input
                type="date"
                required
                className="form-control"
                value={bookingDates.startDate}
                min={new Date().toISOString().split('T')[0]}
                onChange={(e) =>
                  setBookingDates({ ...bookingDates, startDate: e.target.value })
                }
              />
            </div>

            <div className="form-group flex-1">
              <label>📅 Ngày trả phòng (End Date) *</label>
              <input
                type="date"
                required
                className="form-control"
                value={bookingDates.endDate}
                min={bookingDates.startDate}
                onChange={(e) =>
                  setBookingDates({ ...bookingDates, endDate: e.target.value })
                }
              />
            </div>
          </div>

          <div className="booking-summary-pill">
            Số đêm lưu trú: <strong>{days} đêm</strong>
          </div>

          {/* Chọn phòng */}
          <div className="form-section">
            <h3>Chọn phòng muốn đặt ({selectedRoomIds.length} phòng đã chọn)</h3>
            {loadingRooms ? (
              <div className="loading-state">Đang tải danh sách phòng...</div>
            ) : rooms.length === 0 ? (
              <div className="empty-state">Hiện không có phòng nào sẵn sàng.</div>
            ) : (
              <div className="room-selection-list">
                {rooms.map((room) => {
                  const isChecked = selectedRoomIds.includes(room.roomID);
                  const roomTotal = days > 0 ? Number(room.roomPricePerDay) * days : 0;

                  return (
                    <div
                      key={room.roomID}
                      className={`room-select-item ${isChecked ? 'selected' : ''}`}
                      onClick={() => handleRoomToggle(room.roomID)}
                    >
                      <input
                        type="checkbox"
                        checked={isChecked}
                        onChange={() => {}} // handled by parent onClick
                        className="room-checkbox"
                      />
                      <div className="room-select-info">
                        <div className="room-select-title">
                          <strong>Phòng {room.roomNumber}</strong> - {room.roomTypeName}
                        </div>
                        <div className="room-select-desc">
                          👥 Tối đa {room.roomMaxCapacity} người | {room.roomDetailDescription}
                        </div>
                      </div>
                      <div className="room-select-price">
                        <div className="daily-price">{formatCurrency(room.roomPricePerDay)}/đêm</div>
                        {days > 0 && isChecked && (
                          <div className="item-total-price">
                            {formatCurrency(roomTotal)} ({days} đêm)
                          </div>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          {/* Bảng tổng tiền */}
          <div className="total-calculation-box">
            <div className="total-row">
              <span>Số lượng phòng đã chọn:</span>
              <strong>{selectedRoomIds.length} phòng</strong>
            </div>
            <div className="total-row">
              <span>Tổng số đêm:</span>
              <strong>{days} đêm</strong>
            </div>
            <div className="divider"></div>
            <div className="total-row grand-total">
              <span>Tổng thanh toán dự kiến:</span>
              <span className="grand-total-amount">{formatCurrency(calculateTotal())}</span>
            </div>
          </div>

          <button
            type="submit"
            disabled={submitting || selectedRoomIds.length === 0 || days <= 0}
            className="btn btn-primary btn-block btn-lg"
          >
            {submitting ? 'Đang xử lý đơn đặt...' : 'Xác Nhận Đặt Phòng'}
          </button>
        </form>
      </div>
    </div>
  );
};
