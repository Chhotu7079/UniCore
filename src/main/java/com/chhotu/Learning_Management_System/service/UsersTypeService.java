package com.chhotu.Learning_Management_System.service;

import com.chhotu.Learning_Management_System.entity.UsersType;
import java.util.List;

/**
 * Service interface for handling user type related operations.
 */
public interface UsersTypeService {

    /**
     * Fetches all available user types from the system.
     *
     * @return List of all {@link UsersType} entities
     */
    List<UsersType> getAll();
}
