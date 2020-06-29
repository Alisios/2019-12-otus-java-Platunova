package ru.otus.db.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.backend.model.User;
import ru.otus.db.repository.UserDaoRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class DBServiceUserJPA implements DBServiceUser {

    private final UserDaoRepository userDaoRepository;

    public DBServiceUserJPA(UserDaoRepository userDaoRepository) {
        this.userDaoRepository = userDaoRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ, readOnly = false, rollbackFor = Exception.class)
    @Override
    //не понимаю почему корректно не работают закомментированные строчки
    public User saveUser(User user) {
        try {
//            return findByChatIdAndArtist(user)
//                    .orElse(userDaoRepository.save(user));
            Optional<User> userIsPresent = findByChatIdAndArtist(user);
            if (userIsPresent.isEmpty())
                return userDaoRepository.save(user);
            else return userIsPresent.get();
        } catch (DataAccessException | IllegalArgumentException ex) {
            log.error("Error with saving user {}. {}. {}", user, ex.getCause(), ex.getMessage());
            throw new DBException(ex);
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, readOnly = true, rollbackFor = Exception.class)
    @Override
    public Optional<User> getUser(long id) {
        try {
            return userDaoRepository.findById(id);
        } catch (DataAccessException ex) {
            log.error("Error with get user with id {}. {}. {}", id, ex.getCause(), ex.getMessage());
            throw new DBException(ex);
        }
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public Optional<User> findByChatIdAndArtist(User user) {
        try {
            return userDaoRepository.findByChatIdAndConcertArtist(user.getChatId(), user.getConcert().getArtist());
        } catch (DataAccessException ex) {
            log.error("Error with get user {} by id and artist. {}. {}", user, ex.getCause(), ex.getMessage());
            throw new DBException(ex);
        }
    }

    @Override

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
    public List<User> getAllUsers() {
        return userDaoRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<User> getUsersForNotifying() {
        return userDaoRepository.findByIsMonitoringSuccessfulIsTrueAndIsDateExpiredIsTrue();
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ, readOnly = false, rollbackFor = Exception.class)
    @Override
    public void delete(long id) {
        try {
            userDaoRepository.deleteById(id);
        } catch (DataAccessException ex) {
            log.error("Error with deleting user. No user with id {}. {}. {}", id, ex.getCause(), ex.getMessage());
            throw new DBException(ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = false, rollbackFor = Exception.class)
    @Override
    public void deleteAllUsers() {
        try {
            userDaoRepository.deleteAll();
        } catch (DataAccessException ex) {
            log.error("Error with deleting all users {}. {}", ex.getCause(), ex.getMessage());
            throw new DBException(ex);
        }
    }
}
