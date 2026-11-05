import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export const ProtectedRoute = ({ children, allowedRole }) => {
  const { isAuthenticated, user } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRole && user?.role !== allowedRole) {
    return (
      <div className="container" style={{ textAlign: 'center', marginTop: '50px' }}>
        <h2 style={{ color: '#ef4444' }}>Truy cập bị từ chối (403 Forbidden)</h2>
        <p>Bạn không có quyền truy cập vào chức năng này.</p>
      </div>
    );
  }

  return children;
};
