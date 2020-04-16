package ru.otus.orm.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DbExecutor<T> {
  private static Logger logger = LoggerFactory.getLogger(DbExecutor.class);

  public long insertRecord(Connection connection, String sql, List<String> params) throws SQLException {
    Savepoint savePoint = connection.setSavepoint("savePointName");
    try (PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      for (int idx = 0; idx < params.size(); idx++) {
        pst.setString(idx + 1, params.get(idx));
      }
      pst.executeUpdate();
      return Long.parseLong(params.get(0));
    } catch (SQLException ex) {
      connection.rollback(savePoint);
      logger.error(ex.getMessage(), ex);
      throw ex;
    }
  }


  public Optional<T> selectRecord(Connection connection, String sql, long id, Function<ResultSet, T> rsHandler) throws SQLException {
    try (PreparedStatement pst = connection.prepareStatement(sql)) {
      pst.setLong( 1, id);
      try (ResultSet rs = pst.executeQuery()) {
        return Optional.ofNullable(rsHandler.apply(rs));
      }
    }
  }
}
