import React, { useState, useEffect } from 'react';
import { bookingService } from '../../services/bookingService';
import { Link } from 'react-router-dom';

export const MyBookingsPage = () => {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    try {
      const res = await bookingService.getMyHistory();
      setBookings(res.data || []);
    } catch (err) {
      setError(err.message || 'Không thể tải lịch sử đặt phòng');
    } finally {
      setLoading(false);
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
    <div className="container" style={{ maxWidth: '950px' }}>
      <div className="section-header">
        <div>
          <h2>Lịch Sử Đặt Phòng Của Bạn</h2>
          <p>Xem danh sách các đơn đặt phòng và chi tiết các phòng đã thuê</p>
        </div>
        <Link to="/booking" className="btn btn-primary">
          + Đặt Phòng Mới
        </Link>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      {loading ? (
        <div className="loading-state">Đang tải lịch sử...</div>
      ) : bookings.length === 0 ? (
        <div className="card empty-state">
          <p>Bạn chưa có đơn đặt phòng nào.</p>
          <Link to="/booking" className="btn btn-primary" style={{ marginTop: '15px' }}>
            Khám Phá & Đặt Phòng Ngay
          </Link>
        </div>
      ) : (
        <div className="booking-history-list">
          {bookings.map((b) => (
            <div key={b.bookingReservationID} className="card booking-history-card">
              <div className="booking-history-header">
                <div>
                  <span className="booking-code">Mã đơn: #{b.bookingReservationID}</span>
                  <span className="booking-date">📅 Ngày đặt: {b.bookingDate}</span>
                </div>
                <div>{getStatusBadge(b.bookingStatus, b.bookingStatusText)}</div>
              </div>

              {/* Chi tiết các phòng trong đơn */}
              <div className="booking-details-table-wrapper">
                <table className="table">
                  <thead>
                    <tr>
                      <th>Số phòng</th>
                      <th>Loại phòng</th>
                      <th>Thời gian lưu trú</th>
                      <th>Số đêm</th>
                      <th style={{ textAlign: 'right' }}>Giá thực tế</th>
                    </tr>
                  </thead>
                  <tbody>
                    {b.bookingDetails?.map((d, idx) => (
                      <tr key={idx}>
                        <td>
                          <strong>Phòng {d.roomNumber}</strong>
                        </td>
                        <td>{d.roomTypeName}</td>
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

              <div className="booking-history-footer">
                <div className="booking-total-display">
                  <span>Tổng tiền thanh toán: </span>
                  <span className="total-highlight">{formatCurrency(b.totalPrice)}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
