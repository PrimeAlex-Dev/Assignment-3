import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { Navbar } from './components/Navbar';
import { ProtectedRoute } from './components/ProtectedRoute';

// Public Pages
import { RoomsPage } from './pages/public/RoomsPage';
import { LoginPage } from './pages/public/LoginPage';
import { RegisterPage } from './pages/public/RegisterPage';

// Customer Pages
import { ProfilePage } from './pages/customer/ProfilePage';
import { BookingPage } from './pages/customer/BookingPage';
import { MyBookingsPage } from './pages/customer/MyBookingsPage';

// Staff Pages
import { StaffRoomsPage } from './pages/staff/StaffRoomsPage';
import { StaffCustomersPage } from './pages/staff/StaffCustomersPage';
import { StaffBookingsPage } from './pages/staff/StaffBookingsPage';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="app-layout">
          <Navbar />
          <main className="main-content">
            <Routes>
              {/* Public Routes */}
              <Route path="/" element={<RoomsPage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />

              {/* Customer Protected Routes */}
              <Route
                path="/profile"
                element={
                  <ProtectedRoute allowedRole="ROLE_CUSTOMER">
                    <ProfilePage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/booking"
                element={
                  <ProtectedRoute allowedRole="ROLE_CUSTOMER">
                    <BookingPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/my-bookings"
                element={
                  <ProtectedRoute allowedRole="ROLE_CUSTOMER">
                    <MyBookingsPage />
                  </ProtectedRoute>
                }
              />

              {/* Staff Protected Routes */}
              <Route
                path="/staff/rooms"
                element={
                  <ProtectedRoute allowedRole="ROLE_STAFF">
                    <StaffRoomsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/staff/customers"
                element={
                  <ProtectedRoute allowedRole="ROLE_STAFF">
                    <StaffCustomersPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/staff/bookings"
                element={
                  <ProtectedRoute allowedRole="ROLE_STAFF">
                    <StaffBookingsPage />
                  </ProtectedRoute>
                }
              />

              {/* Fallback */}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>
          <footer className="footer">
            <div className="container" style={{ textAlign: 'center' }}>
              <p>© 2026 FUMiniHotelSystem - Assignment 03 SBA301 (Spring Boot 3 + ReactJS)</p>
            </div>
          </footer>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
