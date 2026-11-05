import React, { useState, useEffect } from 'react';
import { customerService } from '../../services/customerService';

export const StaffCustomersPage = () => {
  const [customers, setCustomers] = useState([]);
  const [keyword, setKeyword] = useState('');
  const [selectedStatus, setSelectedStatus] = useState('');
  const [loading, setLoading] = useState(true);

  // Modal State
  const [showModal, setShowModal] = useState(false);
  const [modalMode, setModalMode] = useState('create');
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({
    customerFullName: '',
    telephone: '',
    emailAddress: '',
    customerBirthday: '',
    customerStatus: 1,
    password: '',
  });

  const [message, setMessage] = useState({ type: '', text: '' });

  useEffect(() => {
    fetchCustomers();
  }, []);

  const fetchCustomers = async (kw = keyword, st = selectedStatus) => {
    setLoading(true);
    try {
      const params = {};
      if (kw) params.keyword = kw;
      if (st) params.status = st;

      const res = await customerService.getAllCustomers(params);
      setCustomers(res.data || []);
    } catch (err) {
      setMessage({ type: 'danger', text: err.message || 'Lỗi khi tải danh sách khách hàng' });
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    fetchCustomers(keyword, selectedStatus);
  };

  const openCreateModal = () => {
    setModalMode('create');
    setEditingId(null);
    setFormData({
      customerFullName: '',
      telephone: '',
      emailAddress: '',
      customerBirthday: '',
      customerStatus: 1,
      password: '',
    });
    setShowModal(true);
  };

  const openEditModal = (cust) => {
    setModalMode('edit');
    setEditingId(cust.customerID);
    setFormData({
      customerFullName: cust.customerFullName,
      telephone: cust.telephone || '',
      emailAddress: cust.emailAddress,
      customerBirthday: cust.customerBirthday || '',
      customerStatus: cust.customerStatus,
      password: '', // để trống nếu không đổi
    });
    setShowModal(true);
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    setMessage({ type: '', text: '' });

    try {
      if (modalMode === 'create') {
        await customerService.createCustomer(formData);
        setMessage({ type: 'success', text: 'Thêm mới khách hàng thành công!' });
      } else {
        await customerService.updateCustomer(editingId, formData);
        setMessage({ type: 'success', text: 'Cập nhật khách hàng thành công!' });
      }
      setShowModal(false);
      fetchCustomers();
    } catch (err) {
      alert(err.message || 'Lỗi khi lưu khách hàng');
    }
  };

  const handleDelete = async (cust) => {
    if (!window.confirm(`Bạn có chắc muốn xóa/vô hiệu hóa khách hàng ${cust.customerFullName}?`)) {
      return;
    }

    try {
      const res = await customerService.deleteCustomer(cust.customerID);
      setMessage({ type: 'info', text: res.message });
      fetchCustomers();
    } catch (err) {
      setMessage({ type: 'danger', text: err.message || 'Xóa khách hàng thất bại' });
    }
  };

  return (
    <div className="container">
      <div className="section-header">
        <div>
          <h2>Quản Lý Khách Hàng (Staff)</h2>
          <p>Xem danh sách, tìm kiếm, tạo tài khoản và quản trị khách hàng</p>
        </div>
        <button onClick={openCreateModal} className="btn btn-primary">
          + Thêm Khách Hàng
        </button>
      </div>

      {message.text && (
        <div className={`alert alert-${message.type}`}>
          <strong>{message.type === 'info' ? 'ℹ️ Quy tắc bảo toàn dữ liệu:' : 'Thông báo:'}</strong> {message.text}
        </div>
      )}

      {/* Filter */}
      <div className="card filter-card">
        <form onSubmit={handleSearch} className="filter-form">
          <div className="form-group flex-1">
            <input
              type="text"
              placeholder="Tìm theo tên, email hoặc số điện thoại..."
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              className="form-control"
            />
          </div>

          <div className="form-group">
            <select
              value={selectedStatus}
              onChange={(e) => setSelectedStatus(e.target.value)}
              className="form-control"
            >
              <option value="">Tất cả trạng thái</option>
              <option value="1">Đang hoạt động</option>
              <option value="0">Khóa / Ngưng hoạt động</option>
            </select>
          </div>

          <button type="submit" className="btn btn-primary">
            Tìm Kiếm
          </button>
        </form>
      </div>

      {/* Customer Table */}
      <div className="card">
        {loading ? (
          <div className="loading-state">Đang tải danh sách khách hàng...</div>
        ) : customers.length === 0 ? (
          <div className="empty-state">Không tìm thấy khách hàng nào.</div>
        ) : (
          <div className="table-responsive">
            <table className="table">
              <thead>
                <tr>
                  <th>Mã</th>
                  <th>Họ và tên</th>
                  <th>Email</th>
                  <th>Số điện thoại</th>
                  <th>Ngày sinh</th>
                  <th>Trạng thái</th>
                  <th style={{ textAlign: 'center' }}>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {customers.map((c) => (
                  <tr key={c.customerID} className={c.customerStatus === 0 ? 'row-inactive' : ''}>
                    <td>#{c.customerID}</td>
                    <td>
                      <strong>{c.customerFullName}</strong>
                    </td>
                    <td>{c.emailAddress}</td>
                    <td>{c.telephone || 'Chưa cập nhật'}</td>
                    <td>{c.customerBirthday || 'Chưa cập nhật'}</td>
                    <td>
                      {c.customerStatus === 1 ? (
                        <span className="badge badge-success">Hoạt động</span>
                      ) : (
                        <span className="badge badge-danger">Đã khóa / Soft-del</span>
                      )}
                    </td>
                    <td style={{ textAlign: 'center' }}>
                      <button
                        onClick={() => openEditModal(c)}
                        className="btn btn-outline btn-sm"
                        style={{ marginRight: '8px' }}
                      >
                        Sửa
                      </button>
                      <button
                        onClick={() => handleDelete(c)}
                        className="btn btn-outline-danger btn-sm"
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

      {/* Modal Thêm / Sửa Khách hàng */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>{modalMode === 'create' ? 'Tạo Tài Khoản Khách Hàng' : 'Cập Nhật Khách Hàng'}</h3>
              <button onClick={() => setShowModal(false)} className="close-btn">
                ×
              </button>
            </div>
            <form onSubmit={handleFormSubmit}>
              <div className="modal-body">
                <div className="form-group">
                  <label>Họ và tên *</label>
                  <input
                    type="text"
                    required
                    className="form-control"
                    value={formData.customerFullName}
                    onChange={(e) =>
                      setFormData({ ...formData, customerFullName: e.target.value })
                    }
                  />
                </div>

                <div className="form-group">
                  <label>Email *</label>
                  <input
                    type="email"
                    required
                    className="form-control"
                    value={formData.emailAddress}
                    onChange={(e) =>
                      setFormData({ ...formData, emailAddress: e.target.value })
                    }
                  />
                </div>

                <div className="form-group">
                  <label>Số điện thoại</label>
                  <input
                    type="tel"
                    className="form-control"
                    value={formData.telephone}
                    onChange={(e) =>
                      setFormData({ ...formData, telephone: e.target.value })
                    }
                  />
                </div>

                <div className="form-group">
                  <label>Ngày sinh</label>
                  <input
                    type="date"
                    className="form-control"
                    value={formData.customerBirthday}
                    onChange={(e) =>
                      setFormData({ ...formData, customerBirthday: e.target.value })
                    }
                  />
                </div>

                <div className="form-group">
                  <label>Trạng thái</label>
                  <select
                    className="form-control"
                    value={formData.customerStatus}
                    onChange={(e) =>
                      setFormData({ ...formData, customerStatus: Number(e.target.value) })
                    }
                  >
                    <option value={1}>1: Hoạt động (Active)</option>
                    <option value={0}>0: Khóa / Vô hiệu hóa (Inactive)</option>
                  </select>
                </div>

                <div className="form-group">
                  <label>
                    {modalMode === 'create'
                      ? 'Mật khẩu khởi tạo * (tối thiểu 6 ký tự)'
                      : 'Mật khẩu mới (Bỏ trống nếu không đổi)'}
                  </label>
                  <input
                    type="password"
                    required={modalMode === 'create'}
                    placeholder="••••••••"
                    className="form-control"
                    value={formData.password}
                    onChange={(e) =>
                      setFormData({ ...formData, password: e.target.value })
                    }
                  />
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
                  {modalMode === 'create' ? 'Tạo Khách Hàng' : 'Lưu Thay Đổi'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
