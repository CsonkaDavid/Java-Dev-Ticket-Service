package com.epam.training.ticketservice.core.dto;

import com.epam.training.ticketservice.core.dao.UserDAO;
import lombok.Value;

@Value
public class UserDTO {
    String username;
    UserDAO.Role role;
}
