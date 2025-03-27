package data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private SQLHelper() {
    }

    private static Connection getConn() throws SQLException { // соединение с БД
        return DriverManager.getConnection(System.getProperty("db.url"), "app", "pass");
    }

    @SneakyThrows
    public static void cleanDatabase() {//очистка БД
        var conn = getConn();
        QUERY_RUNNER.execute(conn, "TRUNCATE credit_request_entity");
        QUERY_RUNNER.execute(conn, "TRUNCATE order_entity");
        QUERY_RUNNER.execute(conn, "TRUNCATE payment_entity");
    }

    @SneakyThrows
    public static String getPaymentStatus() { // статус покупки
        var codeSQL = "SELECT status FROM payment_entity";
        var conn = getConn();
        return QUERY_RUNNER.query(conn, codeSQL, new ScalarHandler<String>());
    }

    @SneakyThrows
    public static String getCreditStatus() { // статус покупки в кредит
        var codeSQL = "SELECT status FROM credit_request_entity";
        var conn = getConn();
        return QUERY_RUNNER.query(conn, codeSQL, new ScalarHandler<String>());
    }
}
