import React, { useState, useEffect } from 'react';
import { bookingService } from '../../services/bookingService';

export const StaffBookingsPage = () => {
  const [bookings, setBookings] = useState([]);
  const [statusFilter, setStatusFilter] = useState('');
  const [startDateFilter, setStartDateFilter] = useState('');
  const [endDateFilter, setEndDateFilter] = useState('');
  const [loading, setLoading] = useState(true);

  // Modal State for viewing details
  const [selectedBooking, setSelectedBooking] = useState(null);
  const [message, setMessage] = useState({ type: '', text: '' });

  useEffect(() => {
    fetchBookings();
  }, []);

  const fetchBookings = async (
    st = statusFilter,
    sDate = startDateFilter,
    eDate = endDateFilter
  ) => {
    setLoading(true);
    try {
      const params = {};
      if (st) params.status = st;
      if (sDate) params.startDate = sDate;
      if (eDate) params.endDate = eDate;

      const res = await bookingService.getAllBookings(params);
      setBookings(res.data || []);
    } catch (err) {
      setMessage({ type: 'danger', text: err.message || 'Lỗi khi tải danh sách đặt phòng' });
    } finally {
      setLoading(false);
    }
  };

  const handleFilterSubmit = (e) => {
    e.preventDefault();
    fetchBookings(statusFilter, startDateFilter, endDateFilter);
  };

  const handleStatusChange = async (bookingId, newStatus) => {
    try {
      await bookingService.updateBookingStatus(bookingId, Number(newStatus));
      setMessage({ type: 'success', text: `Đã cập nhật trạng thái đơn #${bookingId} thành công!` });

      if (selectedBooking && selectedBooking.bookingReservationID === bookingId) {
        setSelectedBooking((prev) => ({
          ...prev,
          bookingStatus: Number(newStatus),
          bookingStatusText:
            Number(newStatus) === 2
              ? 'Đã hoàn thành'
              : Number(newStatus) === 3
              ? 'Đã hủy'
              : 'Đã xác nhận',
        }));
      }

      fetchBookings();
    } catch (err) {
      alert(err.message || 'Cập nhật trạng thái thất bại');
    }
  };

  const formatCurrency = (val) => {
    if (!val) return '0 VNĐ';
    return Number(val).toLocaleString('vi-VN') + ' VNĐ';
  };

  const getStatusBadge = (status, text) => {
    let badgeClass = 'badge-info';
    if (status === 2) badgeClass = 'badge-success';
    if (status === 3) badgeClass = 'badge-danger';
    return <span className={`badge ${badgeClass}`}>{text}</span>;
  };

  return (
    <div className="container">
      <div className="section-header">
        <div>
          <h2>Quản Lý Đơn Đặt Phòng (Staff)</h2>
          <p>Xem toàn bộ đơn đặt phòng, chi tiết phòng thuê và cập nhật trạng thái</p>
        </div>
      </div>

      {message.text && <div className={`alert alert-${message.type}`}>{message.text}</div>}

      {/* Filter */}
      <div className="card filter-card">
        <form onSubmit={handleFilterSubmit} className="filter-form">
          <div className="form-group">
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="form-control"
            >
              <option value="">Tất cả trạng thái</option>
              <option value="1">1: Đã xác nhận / Hoạt động</option>
              <option value="2">2: Đã hoàn thành</option>
              <option value="3">3: Đã hủy</option>
            </select>
          </div>

          <div className="form-group">
            <input
              type="date"
              placeholder="Từ ngày đặt"
              value={startDateFilter}
              onChange={(e) => setStartDateFilter(e.target.value)}
              className="form-control"
            />
          </div>

          <div className="form-group">
            <input
              type="date"
              placeholder="Đến ngày đặt"
              value={endDateFilter}
              onChange={(e) => setEndDateFilter(e.target.value)}
              className="form-control"
            />
          </div>

          <button type="submit" className="btn btn-primary">
            Lọc Đơn
          </button>
        </form>
      </div>

      {/* Booking Table */}
      <div className="card">
        {loading ? (
          <div className="loading-state">Đang tải danh sách đặt phòng...</div>
        ) : bookings.length === 0 ? (
          <div className="empty-state">Không tìm thấy đơn đặt phòng nào.</div>
        ) : (
          <div className="table-responsive">
            <table className="table">
              <thead>
                <tr>
                  <th>Mã đơn</th>
                  <th>Ngày đặt</th>
                  <th>Khách hàng</th>
                  <th>Số phòng đặt</th>
                  <th>Tổng tiền</th>
                  <th>Trạng thái</th>
                  <th style={{ textAlign: 'center' }}>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {bookings.map((b) => (
                  <tr key={b.bookingReservationID}>
                    <td>
                      <strong>#{b.bookingReservationID}</strong>
                    </td>
                    <td>{b.bookingDate}</td>
                    <td>
                      <div>
                        <strong>{b.customerFullName}</strong>
                      </div>
                      <small className="text-muted">{b.customerEmail}</small>
                    </td>
                    <td>
                      <span className="badge badge-secondary">
                        {b.bookingDetails?.length || 0} phòng
                      </span>
                    </td>
                    <td>
                      <strong className="price-text">{formatCurrency(b.totalPrice)}</strong>
                    </td>
                    <td>{getStatusBadge(b.bookingStatus, b.bookingStatusText)}</td>
                    <td style={{ textAlign: 'center' }}>
                      <button
                        onClick={() => setSelectedBooking(b)}
                        className="btn btn-outline btn-sm"
                        style={{ marginRight: '8px' }}
                      >
                        Chi Tiết
                      </button>
                      <select
                        value={b.bookingStatus}
                        onChange={(e) =>
                          handleStatusChange(b.bookingReservationID, e.target.value)
                        }
                        className="status-dropdown-sm"
                      >
                        <option value={1}>Xác nhận</option>
                        <option value={2}>Hoàn thành</option>
                        <option value={3}>Đã hủy</option>
                      </select>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal Chi tiết đơn đặt phòng */}
      {selectedBooking && (
        <div className="modal-overlay" onClick={() => setSelectedBooking(null)}>
          <div
            className="modal-content"
            style={{ maxWidth: '800px' }}
            onClick={(e) => e.stopPropagation()}
          >
            <div className="modal-header">
              <h3>Chi Tiết Đơn Đặt Phòng #{selectedBooking.bookingReservationID}</h3>
              <button onClick={() => setSelectedBooking(null)} className="close-btn">
                ×
              </button>
            </div>
            <div className="modal-body">
              <div className="booking-summary-grid">
                <div>
                  <p>
                    <strong>Khách hàng:</strong> {selectedBooking.customerFullName}
                  </p>
                  <p>
                    <strong>Email:</strong> {selectedBooking.customerEmail}
                  </p>
                  <p>
                    <strong>Số điện thoại:</strong> {selectedBooking.customerPhone || 'N/A'}
                  </p>
                </div>
                <div>
                  <p>
                    <strong>Ngày đặt:</strong> {selectedBooking.bookingDate}
                  </p>
                  <p>
                    <strong>Trạng thái:</strong>{' '}
                    {getStatusBadge(
                      selectedBooking.bookingStatus,
                      selectedBooking.bookingStatusText
                    )}
                  </p>
                  <p>
                    <strong>Tổng tiền thanh toán:</strong>{' '}
                    <span className="price-highlight">
                      {formatCurrency(selectedBooking.totalPrice)}
                    </span>
                  </p>
                </div>
              </div>

              <h4 style={{ marginTop: '20px', marginBottom: '10px' }}>
                Danh sách phòng trong đơn ({selectedBooking.bookingDetails?.length} phòng)
              </h4>

              <div className="table-responsive">
                <table className="table">
                  <thead>
                    <tr>
                      <th>Số phòng</th>
                      <th>Loại phòng</th>
                      <th>Sức chứa</th>
                      <th>Từ ngày ➔ Đến ngày</th>
                      <th>Số đêm</th>
                      <th style={{ textAlign: 'right' }}>Giá thực tế</th>
                    </tr>
                  </thead>
                  <tbody>
                    {selectedBooking.bookingDetails?.map((d, idx) => (
                      <tr key={idx}>
                        <td>
                          <strong>Phòng {d.roomNumber}</strong>
                        </td>
                        <td>{d.roomTypeName}</td>
                        <td>{d.roomMaxCapacity} người</td>
                        <td>
                          {d.startDate} ➔ {d.endDate}
                        </td>
                        <td>{d.numberOfDays} đêm</td>
                        <td style={{ textAlign: 'right', fontWeight: 'bold' }}>
                          {formatCurrency(d.actualPrice)}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
            <div className="modal-footer">
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <span>Đổi trạng thái đơn:</span>
                <select
                  value={selectedBooking.bookingStatus}
                  onChange={(e) =>
                    handleStatusChange(
                      selectedBooking.bookingReservationID,
                      e.target.value
                    )
                  }
                  className="form-control"
                  style={{ width: 'auto' }}
                >
                  <option value={1}>1: Đã xác nhận</option>
                  <option value={2}>2: Đã hoàn thành</option>
                  <option value={3}>3: Đã hủy</option>
                </select>
              </div>
              <button onClick={() => setSelectedBooking(null)} className="btn btn-outline">
                Đóng
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
