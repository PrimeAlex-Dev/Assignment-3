import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const data = await login(email, password);
      if (data.role === 'ROLE_STAFF') {
        navigate('/staff/rooms');
      } else {
        navigate('/');
      }
    } catch (err) {
      setError(err.message || 'Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.');
    } finally {
      setLoading(false);
    }
  };

  const fillStaffDemo = () => {
    setEmail('staff@fuminihotel.com');
    setPassword('Staff@123456');
  };

  const fillCustomerDemo = () => {
    setEmail('customer@gmail.com');
    setPassword('123456');
  };

  return (
    <div className="auth-wrapper">
      <div className="card auth-card">
        <div className="auth-header">
          <h2>Đăng Nhập</h2>
          <p>Hệ Thống Quản Lý Khách Sạn FUMiniHotel</p>
        </div>

        {error && <div className="alert alert-danger">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email đăng nhập</label>
            <input
              type="email"
              required
              className="form-control"
              placeholder="name@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label>Mật khẩu</label>
            <input
              type="password"
              required
              className="form-control"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          <button type="submit" disabled={loading} className="btn btn-primary btn-block">
            {loading ? 'Đang xác thực...' : 'Đăng Nhập'}
          </button>
        </form>

        <div className="demo-accounts">
          <p className="demo-title">⚡ Chọn nhanh tài khoản kiểm thử:</p>
          <div className="demo-buttons">
            <button type="button" onClick={fillStaffDemo} className="btn btn-outline btn-sm">
              Staff (Properties)
            </button>
            <button type="button" onClick={fillCustomerDemo} className="btn btn-outline btn-sm">
              Customer (DB)
            </button>
          </div>
        </div>

        <div className="auth-footer">
          Chưa có tài khoản khách hàng?{' '}
          <Link to="/register" className="auth-link">
            Đăng ký ngay
          </Link>
        </div>
      </div>
    </div>
  );
};
