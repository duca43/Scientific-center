package org.scientificcenter.service;

import org.scientificcenter.dto.RegistrationUserDto;
import org.scientificcenter.dto.UserDto;
import org.scientificcenter.model.Authority;
import org.scientificcenter.model.ScientificArea;
import org.scientificcenter.model.User;

import java.util.List;

public interface UserService {

    User findById(Long id);

    User save(final RegistrationUserDto registrationUserDto);

    User update(User user);

    User findByUsername(String username);

    List<UserDto> findAllNonAdminUsers();

    User addEditor(User editor);

    List<User> findAllByUsernameNotAndAuthoritiesContainsAndScientificAreasIsIn(String username, Authority authority, List<ScientificArea> scientificAreas);

    UserDto findUserByUsername(String username);
}
