package ru.otus.orm.jdbc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.orm.api.dao.UserDaoException;
import ru.otus.orm.jdbc.DbExecutor;
import ru.otus.orm.jdbc.helpers.CreateSqlStatement;
import ru.otus.orm.jdbc.helpers.JdbcMapper;
import ru.otus.orm.jdbc.dao.UserDaoJdbc;
import ru.otus.orm.jdbc.sessionmanager.SessionManagerJdbc;

import java.util.ArrayList;
import java.util.List;

public class UserDaoJdbcTemplate <T> implements JdbcTemplate <T> {
    private static Logger logger = LoggerFactory.getLogger(UserDaoJdbc.class);

    private final SessionManagerJdbc sessionManager;
    private final DbExecutor<T> dbExecutor;
    private final JdbcMapper jdbcMapper;
    private final CreateSqlStatement createSqlStatement ;
    private final List<Long> cachedUsersId = new ArrayList<>();

    public UserDaoJdbcTemplate(SessionManagerJdbc sessionManager, DbExecutor<T> dbExecutor, JdbcMapper jdbcMapper,CreateSqlStatement createSqlStatement) {
        this.sessionManager = sessionManager;
        this.dbExecutor = dbExecutor;
        this.jdbcMapper = jdbcMapper;
        this.createSqlStatement = createSqlStatement;
    }

    @Override
    public void create(T objectData) {
        try {
            cachedUsersId.add(dbExecutor.insertRecord(sessionManager.getCurrentSession().getConnection(),
                    createSqlStatement.getSqlStatement(objectData.getClass(),"insert"),
                    jdbcMapper.getParams(objectData)));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new UserDaoException(e);
        }
    }

    @Override
    public void createOrUpdate(T objectData){
        if (cachedUsersId.contains(jdbcMapper.getId(objectData)))
            update(objectData);
        else
            create(objectData);
    }

    @Override
    public void update(T objectData)  {
        try {
            dbExecutor.insertRecord(sessionManager.getCurrentSession().getConnection(),
                    createSqlStatement.getSqlStatement(objectData.getClass(),"update"),
                    jdbcMapper.getParams(objectData));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new UserDaoException(e);
        }
}
    @SuppressWarnings("unchecked")
    @Override
    public T load(long id, Class clazz) {
        try {
            return  (T) dbExecutor.selectRecord(sessionManager.getCurrentSession().getConnection(),
                    createSqlStatement.getSqlStatement(clazz,"select"), id, resultSet -> {
            try {
                if (resultSet.next()) {
                    return (T)jdbcMapper.createObjectFromResultSet(resultSet, clazz);
                }
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return null;
          });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

}
