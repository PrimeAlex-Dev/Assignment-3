import React, { useState, useEffect } from 'react';
import { customerService } from '../../services/customerService';
import { useAuth } from '../../context/AuthContext';

export const ProfilePage = () => {
  const { updateUser } = useAuth();
  const [profile, setProfile] = useState(null);
  const [formData, setFormData] = useState({
    customerFullName: '',
    telephone: '',
    customerBirthday: '',
    newPassword: '',
  });

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const res = await customerService.getMyProfile();
      if (res.data) {
        setProfile(res.data);
        setFormData({
          customerFullName: res.data.customerFullName || '',
          telephone: res.data.telephone || '',
          customerBirthday: res.data.customerBirthday || '',
          newPassword: '',
        });
      }
    } catch (err) {
      setMessage({ type: 'danger', text: err.message || 'Không thể tải thông tin hồ sơ' });
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setMessage({ type: '', text: '' });

    try {
      const updatePayload = {
        customerFullName: formData.customerFullName,
        telephone: formData.telephone,
        customerBirthday: formData.customerBirthday || null,
      };

      if (formData.newPassword) {
        updatePayload.newPassword = formData.newPassword;
      }

      const res = await customerService.updateMyProfile(updatePayload);
      setMessage({ type: 'success', text: 'Cập nhật hồ sơ cá nhân thành công!' });
      setProfile(res.data);
      updateUser({ fullName: res.data.customerFullName });
      setFormData((prev) => ({ ...prev, newPassword: '' }));
    } catch (err) {
      setMessage({ type: 'danger', text: err.message || 'Cập nhật hồ sơ thất bại' });
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <div className="container loading-state">Đang tải thông tin hồ sơ...</div>;
  }

  return (
    <div className="container" style={{ maxWidth: '650px' }}>
      <div className="card">
        <div className="card-header">
          <h2>Hồ Sơ Khách Hàng</h2>
          <p>Quản lý và cập nhật thông tin cá nhân của bạn</p>
        </div>

        {message.text && <div className={`alert alert-${message.type}`}>{message.text}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email tài khoản</label>
            <input
              type="text"
              disabled
              className="form-control disabled-input"
              value={profile?.emailAddress || ''}
            />
            <small className="form-hint">Email dùng làm định danh đăng nhập và không thể thay đổi</small>
          </div>

          <div className="form-group">
            <label>Họ và tên *</label>
            <input
              type="text"
              name="customerFullName"
              required
              className="form-control"
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
              value={formData.telephone}
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

          <div className="divider"></div>

          <div className="form-group">
            <label>Đổi mật khẩu mới (Bỏ trống nếu không đổi)</label>
            <input
              type="password"
              name="newPassword"
              placeholder="Nhập mật khẩu mới (tối thiểu 6 ký tự)"
              className="form-control"
              value={formData.newPassword}
              onChange={handleChange}
            />
          </div>

          <button type="submit" disabled={saving} className="btn btn-primary btn-block">
            {saving ? 'Đang lưu...' : 'Lưu Thay Đổi'}
          </button>
        </form>
      </div>
    </div>
  );
};
