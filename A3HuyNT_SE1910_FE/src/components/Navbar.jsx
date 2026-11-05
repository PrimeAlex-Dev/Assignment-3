import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export const Navbar = () => {
  const { user, isAuthenticated, isStaff, isCustomer, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="navbar-brand">
          <span className="brand-icon">🏨</span> FUMiniHotel
        </Link>

        <div className="navbar-links">
          {/* Menu công khai: Xem danh sách phòng */}
          <Link to="/" className="nav-link">
            Phòng Khách Sạn
          </Link>

          {/* Menu dành cho Customer */}
          {isCustomer && (
            <>
              <Link to="/booking" className="nav-link">
                Đặt Phòng Online
              </Link>
              <Link to="/my-bookings" className="nav-link">
                Lịch Sử Đặt Phòng
              </Link>
              <Link to="/profile" className="nav-link">
                Hồ Sơ Cá Nhân
              </Link>
            </>
          )}

          {/* Menu dành cho Staff */}
          {isStaff && (
            <>
              <Link to="/staff/rooms" className="nav-link nav-staff">
                Quản Lý Phòng
              </Link>
              <Link to="/staff/customers" className="nav-link nav-staff">
                Quản Lý Khách Hàng
              </Link>
              <Link to="/staff/bookings" className="nav-link nav-staff">
                Quản Lý Đặt Phòng
              </Link>
            </>
          )}
        </div>

        <div className="navbar-auth">
          {isAuthenticated ? (
            <div className="user-profile-badge">
              <span className={`role-badge ${isStaff ? 'role-staff' : 'role-customer'}`}>
                {isStaff ? 'STAFF' : 'CUSTOMER'}
              </span>
              <span className="user-name">{user?.fullName || user?.email}</span>
              <button onClick={handleLogout} className="btn btn-outline-danger btn-sm">
                Đăng Xuất
              </button>
            </div>
          ) : (
            <div className="auth-buttons">
              <Link to="/login" className="btn btn-outline btn-sm">
                Đăng Nhập
              </Link>
              <Link to="/register" className="btn btn-primary btn-sm">
                Đăng Ký
              </Link>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};
