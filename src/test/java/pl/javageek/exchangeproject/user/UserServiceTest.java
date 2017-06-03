package pl.javageek.exchangeproject.user;

import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserServiceTest {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void registerNewUserAccount() throws Exception {
        val passedUser = new User();
        passedUser.setUsername("a");
        passedUser.setPassword("a");
        when(userRepository.findByUsername("a")).thenReturn(Optional.empty());
        val expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("a");
        expectedUser.setPassword("b");
        when(userRepository.saveAndFlush(passedUser)).thenReturn(expectedUser);
        when(passwordEncoder.encode("a")).thenReturn("b");

        val newUser = service.registerNewUserAccount(passedUser);

        assertThat(newUser.getId(), is(1L));
    }

    @Test(expected = UsernameExistsException.class)
    public void throwExceptionWhenUsernameExists() throws Exception {
        val passedUser = new User();
        passedUser.setUsername("a");
        passedUser.setPassword("a");
        when(userRepository.findByUsername("a")).thenReturn(Optional.of(passedUser));

        service.registerNewUserAccount(passedUser);
    }

    @Test
    public void returnsThatUserExists() throws Exception {
        val existingUser = new User();
        existingUser.setUsername("a");
        val opt = Optional.of(existingUser);
        when(userRepository.findByUsername("a")).thenReturn(opt);

        val userExists = service.usernameExist("a");

        verify(userRepository).findByUsername("a");
        assertTrue(userExists);
    }

    @Test
    public void returnsThatUserDoesntExist() throws Exception {
        when(userRepository.findByUsername("a")).thenReturn(Optional.empty());

        val userExists = service.usernameExist("a");

        verify(userRepository).findByUsername("a");
        assertFalse(userExists);
    }
}