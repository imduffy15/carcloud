package ie.ianduffy.carcloud.service;

import ie.ianduffy.carcloud.domain.Authority;
import ie.ianduffy.carcloud.domain.User;
import ie.ianduffy.carcloud.repository.AuthorityRepository;
import ie.ianduffy.carcloud.repository.UserRepository;
import ie.ianduffy.carcloud.security.AuthoritiesConstants;
import ie.ianduffy.carcloud.security.SecurityUtils;
import ie.ianduffy.carcloud.web.dto.UserDTO;
import org.dozer.Mapper;
import org.hibernate.StaleStateException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    @Inject
    private AuthorityRepository authorityRepository;
    @Inject
    private Mapper mapper;
    @Inject
    private PasswordEncoder passwordEncoder;
    @Inject
    private UserRepository userRepository;

    public void addAuthority(String email, String authorityName) {
        User user = getUser(email);
        Authority authority = authorityRepository.findOne(authorityName);
        user.getAuthorities().add(authority);
        userRepository.save(user);
    }

    public void changePassword(String password) {
        User user = userRepository.findOne(SecurityUtils.getCurrentLogin());
        String encryptedPassword = passwordEncoder.encode(password);
        user.setPassword(encryptedPassword);
        userRepository.save(user);
    }

    public User create(UserDTO userDTO) {
        if (userRepository.findOne(userDTO.getLogin()) != null) {
            return null;
        }

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        User user = new User();
        user.setAuthorities(Arrays.asList(authorityRepository.findOne(AuthoritiesConstants.USER)));

        mapper.map(userDTO, user);
        userRepository.save(user);

        return user;
    }

    @Transactional(readOnly = true)
    public User getUser() {
        return userRepository.findOne(SecurityUtils.getCurrentLogin());
    }

    @Transactional(readOnly = true)
    public User getUser(String login) {
        User user = userRepository.findOne(login);
        user.getAuthorities().size();
        return user;
    }

    public void removeAuthority(String login, int index) {
        User user = getUser(login);
        user.getAuthorities().remove(index);
        userRepository.save(user);
    }

    public void update(UserDTO userDTO) {
        User currentUser = userRepository.findOne(SecurityUtils.getCurrentLogin());

        if (userDTO.getVersion() != currentUser.getVersion()) {
            throw new StaleStateException("Unexpected version. Got " + userDTO.getVersion() + " expected " + currentUser.getVersion());
        }

        currentUser.setFirstName(userDTO.getFirstName());
        currentUser.setLastName(userDTO.getLastName());
        currentUser.setEmail(userDTO.getEmail());

        userRepository.save(currentUser);
    }
}
