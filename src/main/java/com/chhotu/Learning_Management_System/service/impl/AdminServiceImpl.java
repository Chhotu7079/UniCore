package com.chhotu.Learning_Management_System.service.impl;

import com.chhotu.Learning_Management_System.entity.Admin;
import com.chhotu.Learning_Management_System.repository.AdminRepository;
import com.chhotu.Learning_Management_System.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public void save(Admin admin) {
        if (admin == null) {
            log.warn("Attempted to save null Admin entity");
            throw new IllegalArgumentException("Admin cannot be null");
        }
        adminRepository.save(admin);
        log.info("Admin '{} {}' saved successfully", admin.getFirstName(), admin.getLastName());
    }

    @Override
    public Admin getAdminById(int adminId) {
        Optional<Admin> admin = adminRepository.findById(adminId);
        if (admin.isEmpty()) {
            log.error("Admin with ID {} not found", adminId);
            throw new IllegalArgumentException("Admin with ID " + adminId + " not found");
        }
        log.info("Admin with ID {} retrieved successfully", adminId);
        return admin.get();
    }
}
