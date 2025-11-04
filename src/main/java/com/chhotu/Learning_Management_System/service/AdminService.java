package com.chhotu.Learning_Management_System.service;

import com.chhotu.Learning_Management_System.entity.Admin;

public interface AdminService {
    void save(Admin admin);
    Admin getAdminById(int adminId);
}
