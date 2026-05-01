package kna.springsecurity.migration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(3)
@RequiredArgsConstructor
public class UserMfaVerifiedMigration implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        migrateNullMfaVerified();
    }

    private void migrateNullMfaVerified() {
        if (!tableExists("USERS")) {
            return;
        }

        String mfaVerifiedColumn = resolveMfaVerifiedColumnName();
        String mfaEnabledColumn = resolveMfaEnabledColumnName();

        if (mfaVerifiedColumn == null || mfaEnabledColumn == null) {
            return;
        }

        // Existing data before this feature should be considered verified to avoid lockouts.
        jdbcTemplate.execute(
                "UPDATE users SET " + mfaVerifiedColumn + " = TRUE WHERE " + mfaVerifiedColumn + " IS NULL"
        );

        // Accounts without MFA should always be verified.
        jdbcTemplate.execute(
                "UPDATE users SET " + mfaVerifiedColumn + " = TRUE WHERE " + mfaEnabledColumn + " = FALSE"
        );
    }

    private String resolveMfaVerifiedColumnName() {
        if (columnExists("USERS", "MFA_VERIFIED")) {
            return "mfa_verified";
        }
        if (columnExists("USERS", "MFAVERIFIED")) {
            return "mfaVerified";
        }
        return null;
    }

    private String resolveMfaEnabledColumnName() {
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
