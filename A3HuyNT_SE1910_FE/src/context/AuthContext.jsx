import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => authService.getCurrentUser());
  const [token, setToken] = useState(() => authService.getToken());

  const login = async (email, password) => {
    const userData = await authService.login(email, password);
    setUser(userData);
    setToken(userData.token);
    return userData;
  };

  const logout = () => {
    authService.logout();
    setUser(null);
    setToken(null);
  };

  const updateUser = (newUserData) => {
    const updated = { ...user, ...newUserData };
    localStorage.setItem('user', JSON.stringify(updated));
    setUser(updated);
  };

  const isStaff = user?.role === 'ROLE_STAFF';
  const isCustomer = user?.role === 'ROLE_CUSTOMER';

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!token,
        isStaff,
        isCustomer,
        login,
        logout,
        updateUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
