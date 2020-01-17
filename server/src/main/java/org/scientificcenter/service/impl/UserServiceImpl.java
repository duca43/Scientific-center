package org.scientificcenter.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.modelmapper.ModelMapper;
import org.scientificcenter.dto.AccountVerificationDto;
import org.scientificcenter.dto.RegistrationUserDto;
import org.scientificcenter.exception.UserNotFoundException;
import org.scientificcenter.model.Authority;
import org.scientificcenter.model.Location;
import org.scientificcenter.model.ScientificArea;
import org.scientificcenter.model.User;
import org.scientificcenter.repository.UserRepository;
import org.scientificcenter.service.AuthorityService;
import org.scientificcenter.service.LocationService;
import org.scientificcenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService, JavaDelegate {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final AuthorityService authorityService;
    private final LocationService locationService;
    private final PasswordEncoder passwordEncoder;
    private final IdentityService identityService;
    private static final String ACCOUNT_VERIFICATION_DTO = "accountVerificationDto";
    private static final String USER_ENABLED = "user_enabled";
    private static final String ROLE_ADMINISTRATOR = "ROLE_ADMINISTRATOR";
    private static final String ROLE_EDITOR = "ROLE_EDITOR";

    @Autowired
    public UserServiceImpl(final UserRepository userRepository, final AuthorityService authorityService, final LocationService locationService, final PasswordEncoder passwordEncoder, final IdentityService identityService) {
        this.userRepository = userRepository;
        this.authorityService = authorityService;
        this.locationService = locationService;
        this.passwordEncoder = passwordEncoder;
        this.identityService = identityService;
    }

    @Override
    public User findById(final Long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    @Override
    public User save(final RegistrationUserDto registrationUserDto) {
        Assert.notNull(registrationUserDto, "Registration user object can't be null!");

        User user = this.prepareUserForSave(this.modelMapper.map(registrationUserDto, User.class));

        user.setEnabled(false);

        user = this.userRepository.save(user);
        UserServiceImpl.log.info("Username: {}. User is saved into DB successfully", registrationUserDto.getUsername());

        return user;
    }

    private User prepareUserForSave(final User user) {
        Assert.noNullElements(Stream.of(user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                user.getLocation(),
                user.getScientificAreas())
                        .toArray(),
                "One or more required fields are null!");

        UserServiceImpl.log.info("Saving user with username '{}'", user.getUsername());

        Location location = user.getLocation();
        location = this.locationService.save(location);
        user.setLocation(location);
        UserServiceImpl.log.info("Username: {}. Set following location: lat - {}, lon - {}",
                user.getUsername(),
                user.getLocation().getLatitude(),
                user.getLocation().getLongitude());

        user.setScientificAreas(new HashSet<>(user.getScientificAreas()));
        UserServiceImpl.log.info("Username: {}. Set {} scientific areas", user.getUsername(), user.getScientificAreas().size());

        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        UserServiceImpl.log.info("Username: {}. Password is encoded", user.getUsername());
        return user;
    }

    @Override
    public User update(final User user) {
        Assert.notNull(user, "User object can't be null!");
        if (this.findById(user.getId()) == null) throw new UserNotFoundException(user.getId());
        return this.userRepository.save(user);
    }

    @Override
    public User findByUsername(final String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public List<User> findAllNonAdminUsers() {
        final Authority adminAuthority = this.authorityService.findByName(UserServiceImpl.ROLE_ADMINISTRATOR);
        return this.userRepository.findAllByAuthoritiesNotContains(adminAuthority);
    }

    @Override
    public User addEditor(final User editor) {
        Assert.notNull(editor, "Editor object can't be null!");

        User user = this.prepareUserForSave(editor);

        user.setEnabled(true);

        final Authority userAuthority = this.authorityService.findByName(UserServiceImpl.ROLE_EDITOR);
        user.setAuthorities(Collections.singleton(userAuthority));

        user = this.userRepository.save(user);
        UserServiceImpl.log.info("Username: {}. Editor is saved into DB successfully", editor.getUsername());

        final org.camunda.bpm.engine.identity.User camundaUser = this.identityService.newUser(user.getUsername());
        camundaUser.setPassword(editor.getPassword());
        camundaUser.setEmail(user.getEmail());
        camundaUser.setFirstName(user.getFirstname());
        camundaUser.setLastName(user.getLastname());
        this.identityService.saveUser(camundaUser);
        UserServiceImpl.log.info("Username: {}. Camunda user is saved successfully", camundaUser.getId());

        return user;
    }

    @Override
    public List<User> findAllByUsernameNotAndAuthoritiesContainsAndScientificAreasIsIn(final String username, final Authority authority, final List<ScientificArea> scientificAreas) {
        return this.userRepository.findDistinctByUsernameNotAndAuthoritiesContainsAndScientificAreasIsIn(username, authority, scientificAreas);
    }

    @Override
    public UserDetails loadUserByUsername(final String s) throws UsernameNotFoundException {
        final User user = this.userRepository.findByUsername(s);

        if (user == null) throw new UsernameNotFoundException("User with username '" + s + "' is not found");

        return user;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) {
        final AccountVerificationDto accountVerificationDto = (AccountVerificationDto) delegateExecution.getVariable(UserServiceImpl.ACCOUNT_VERIFICATION_DTO);
        final org.scientificcenter.model.User user = (org.scientificcenter.model.User) this.loadUserByUsername(accountVerificationDto.getUsername());

        delegateExecution.setVariable(UserServiceImpl.USER_ENABLED, true);

        user.setEnabled(true);
        this.update(user);
        UserServiceImpl.log.info("User with username '{}' is enabled successfully", accountVerificationDto.getUsername());
    }
}