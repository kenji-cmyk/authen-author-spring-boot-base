package kna.springsecurity.migration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@RequiredArgsConstructor
public class UserMfaEnabledMigration implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        migrateNullMfaEnabled();
    }

    private void migrateNullMfaEnabled() {
        if (!tableExists("USERS")) {
            return;
        }

        String mfaColumn = resolveMfaColumnName();
        if (mfaColumn == null) {
            return;
        }

        jdbcTemplate.execute(
                "UPDATE users SET " + mfaColumn + " = FALSE WHERE " + mfaColumn + " IS NULL"
        );
    }

    private String resolveMfaColumnName() {
        if (columnExists("USERS", "MFA_ENABLED")) {
            return "mfa_enabled";
        }
        if (columnExists("USERS", "MFAENABLED")) {
            return "mfaEnabled";
        }
        return null;
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE UPPER(TABLE_NAME) = ?",
                Integer.class,
                tableName
        );
        return count != null && count > 0;
    }

    private boolean columnExists(String tableName, String columnName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE UPPER(TABLE_NAME) = ? AND UPPER(COLUMN_NAME) = ?",
                Integer.class,
                tableName,
                columnName
        );
        return count != null && count > 0;
    }
}