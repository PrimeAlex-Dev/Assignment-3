import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authService } from '../../services/authService';

export const RegisterPage = () => {
  const [formData, setFormData] = useState({
    customerFullName: '',
    telephone: '',
    emailAddress: '',
    customerBirthday: '',
    password: '',
    confirmPassword: '',
  });

  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (formData.password !== formData.confirmPassword) {
      setError('Mật khẩu xác nhận không khớp');
      return;
    }

    if (formData.password.length < 6) {
      setError('Mật khẩu phải từ 6 ký tự trở lên');
      return;
    }

    setLoading(true);

    try {
      await authService.register({
        customerFullName: formData.customerFullName,
        telephone: formData.telephone,
        emailAddress: formData.emailAddress,
        customerBirthday: formData.customerBirthday || null,
        password: formData.password,
      });

      setSuccess('Đăng ký tài khoản thành công! Đang chuyển hướng sang trang đăng nhập...');
      setTimeout(() => {
        navigate('/login');
      }, 2000);
    } catch (err) {
      setError(err.message || 'Đăng ký thất bại. Vui lòng thử lại.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-wrapper">
      <div className="card auth-card">
        <div className="auth-header">
          <h2>Đăng Ký Khách Hàng</h2>
          <p>Tạo tài khoản để đặt phòng và xem ưu đãi</p>
        </div>

        {error && <div className="alert alert-danger">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Họ và tên *</label>
            <input
              type="text"
              name="customerFullName"
              required
              className="form-control"
              placeholder="Nguyễn Văn A"
              value={formData.customerFullName}
              onChange={handleChange}
            />
          </div>

          <div className="form-group">
            <label>Số điện thoại</label>
            <input
              type="tel"
              name="telephone"
              className="form-control"
              placeholder="0912345678"
              value={formData.telephone}
              onChange={handleChange}
            />
          </div>

          <div className="form-group">
            <label>Email *</label>
            <input
              type="email"
              name="emailAddress"
              required
              className="form-control"
              placeholder="name@example.com"
              value={formData.emailAddress}
              onChange={handleChange}
            />
          </div>

          <div className="form-group">
            <label>Ngày sinh</label>
            <input
              type="date"
              name="customerBirthday"
              className="form-control"
              value={formData.customerBirthday}
              onChange={handleChange}
            />
          </div>

          <div className="form-group">
            <label>Mật khẩu *</label>
            <input
              type="password"
              name="password"
              required
              className="form-control"
              placeholder="Ít nhất 6 ký tự"
              value={formData.password}
              onChange={handleChange}
            />
          </div>

          <div className="form-group">
            <label>Xác nhận mật khẩu *</label>
            <input
              type="password"
              name="confirmPassword"
              required
              className="form-control"
              placeholder="Nhập lại mật khẩu"
              value={formData.confirmPassword}
              onChange={handleChange}
            />
          </div>

          <button type="submit" disabled={loading} className="btn btn-primary btn-block">
            {loading ? 'Đang đăng ký...' : 'Tạo Tài Khoản'}
          </button>
        </form>

        <div className="auth-footer">
          Đã có tài khoản?{' '}
          <Link to="/login" className="auth-link">
            Đăng nhập ngay
          </Link>
        </div>
      </div>
    </div>
  );
};
