package ru.otus.orm.jdbc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.orm.api.dao.UserDaoException;
import ru.otus.orm.jdbc.DbExecutor;
import ru.otus.orm.jdbc.helpers.CreateSqlStatement;
import ru.otus.orm.jdbc.helpers.JdbcMapper;
import ru.otus.orm.jdbc.dao.UserDaoJdbc;
import ru.otus.orm.jdbc.sessionmanager.SessionManagerJdbc;

import java.util.*;

public class UserDaoJdbcTemplate <T> implements JdbcTemplate <T> {
    private static Logger logger = LoggerFactory.getLogger(UserDaoJdbc.class);

    private final SessionManagerJdbc sessionManager;
    private final DbExecutor<T> dbExecutor;
    private final JdbcMapper<T> jdbcMapper;
    private final CreateSqlStatement<T> createSqlStatement ;
    private final Map<Class, List> cachedUsersId = new HashMap<>();
    private final List<Long> cachedUsersIdlist = new ArrayList<>();
    private long id;

    public UserDaoJdbcTemplate(SessionManagerJdbc sessionManager, DbExecutor<T> dbExecutor, JdbcMapper<T> jdbcMapper,CreateSqlStatement<T> createSqlStatement) {
        this.sessionManager = sessionManager;
        this.dbExecutor = dbExecutor;
        this.jdbcMapper = jdbcMapper;
        this.createSqlStatement = createSqlStatement;
    }

    @Override
    public void create(T objectData) {
        try {
            cachedUsersIdlist.add(dbExecutor.insertRecord(sessionManager.getCurrentSession().getConnection(),
                    createSqlStatement.getSqlStatement((Class<T>) objectData.getClass(),"insert"),
                    jdbcMapper.getParams(objectData)));
            cachedUsersId.put(objectData.getClass(), cachedUsersIdlist);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new UserDaoException(e);
        }
    }

    @Override
    public void createOrUpdate(T objectData){
        id = jdbcMapper.getId(objectData);
        Class clazz = objectData.getClass();
        if (cachedUsersId.containsKey(clazz) && (cachedUsersId.get(clazz).contains(id)))
                update(objectData);
        else
            create(objectData);
    }

    @Override
    public void update(T objectData)  {
        try {
            dbExecutor.updateRecord(sessionManager.getCurrentSession().getConnection(),
                    createSqlStatement.getSqlStatement((Class<T>) objectData.getClass(),"update"), id,
                    jdbcMapper.getParams(objectData));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new UserDaoException(e);
        }
}

    @Override
    public  Optional <T> load(long id, Class<?> clazz) {
        try {
            return   dbExecutor.selectRecord(sessionManager.getCurrentSession().getConnection(),
                    createSqlStatement.getSqlStatement((Class<T>) clazz,"select"), id, resultSet -> {
            try {
                if (resultSet.next()) {
                    return jdbcMapper.createObjectFromResultSet(resultSet, (Class<T>) clazz);
                }
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return (T)Optional.empty();
          });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

}
