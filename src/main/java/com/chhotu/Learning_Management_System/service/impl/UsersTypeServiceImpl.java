package com.chhotu.Learning_Management_System.service.impl;

import com.chhotu.Learning_Management_System.entity.UsersType;
import com.chhotu.Learning_Management_System.repository.UsersTypeRepository;
import com.chhotu.Learning_Management_System.service.UsersTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link UsersTypeService}.
 * Handles operations related to fetching and managing user types.
 */
@Service
public class UsersTypeServiceImpl implements UsersTypeService {

    private static final Logger logger = LoggerFactory.getLogger(UsersTypeServiceImpl.class);

    private final UsersTypeRepository usersTypeRepository;

    public UsersTypeServiceImpl(UsersTypeRepository usersTypeRepository) {
        this.usersTypeRepository = usersTypeRepository;
        logger.info("UsersTypeServiceImpl initialized successfully");
    }

    /**
     * Retrieves all user types available in the system.
     *
     * @return List of {@link UsersType}
     */
    @Override
    public List<UsersType> getAll() {
        logger.info("Fetching all user types from repository...");
        List<UsersType> userTypes = usersTypeRepository.findAll();
        logger.info("Total user types retrieved: {}", userTypes.size());
        return userTypes;
    }
}
