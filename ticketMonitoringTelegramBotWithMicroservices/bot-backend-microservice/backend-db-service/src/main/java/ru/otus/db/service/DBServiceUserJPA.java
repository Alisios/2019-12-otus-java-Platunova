package ru.otus.db.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.backend.model.User;
import ru.otus.db.repository.UserDaoRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DBServiceUserJPA implements DBServiceUser {

    private final UserDaoRepository userDaoRepository;

    @Transactional
    @Override
    public User saveUser(User user) {
        try {
            Optional<User> userIsPresent = findByChatIdAndArtist(user);
            return userIsPresent.orElseGet(() -> userDaoRepository.save(user));
        } catch (Exception ex) {
            log.error("Error with saving user {}. {}. {}", user, ex.getCause(), ex.getMessage());
            throw new DBException(ex);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUser(long id) {
        try {
            return userDaoRepository.findById(id);
        } catch (Exception ex) {
            log.error("Error with get user with id {}. {}. {}", id, ex.getCause(), ex.getMessage());
            throw new DBException(ex);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByChatIdAndArtist(User user) {
        try {
            return userDaoRepository.findByChatIdAndConcertArtist(user.getChatId(), user.getConcert().getArtist());
        } catch (Exception ex) {
            log.error("Error with getting user {} by id and artist. {}. {}", user, ex.getCause(), ex.getMessage());
            throw new DBException(ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        try {
            return userDaoRepository.findAll();
        } catch (Exception ex) {
            log.error("Error with finding all users {}. {}", ex.getCause(), ex.getMessage());
            throw new DBException(ex);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getUsersForNotifying() {
        try {

            return userDaoRepository.findByIsMonitoringSuccessfulIsTrueAndIsDateExpiredIsTrue();
        } catch (Exception ex) {
            log.error("Error with getting all users {}. {}", ex.getCause(), ex.getMessage());
            throw new DBException(ex);
        }
    }

    @Transactional
    @Override
    public void delete(long id) {
        try {
            userDaoRepository.deleteById(id);
        } catch (Exception ex) {
            log.error("Error with deleting user. No user with id {}. {}. {}", id, ex.getCause(), ex.getMessage());
            throw new DBException(ex);
        }
    }

    @Transactional
    @Override
    public void deleteAllUsers() {
        try {
            userDaoRepository.deleteAll();
        } catch (Exception ex) {
            log.error("Error with deleting all users {}. {}", ex.getCause(), ex.getMessage());
            throw new DBException(ex);
        }
    }
}
