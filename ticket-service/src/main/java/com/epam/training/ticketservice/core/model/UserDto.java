package com.epam.training.ticketservice.core.model;

import com.epam.training.ticketservice.core.entity.User;
import lombok.Value;

@Value
public class UserDto {
    String username;
    User.Role role;
}
