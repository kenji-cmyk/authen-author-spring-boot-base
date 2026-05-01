package kna.springsecurity.migration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@RequiredArgsConstructor
public class UserRolesMigration implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        migrateUserRolesTable();
    }

    private void migrateUserRolesTable() {
        if (!tableExists("USER_ROLES")) {
            return;
        }

        boolean hasRoleName = columnExists("USER_ROLES", "ROLE_NAME");
        boolean hasRoleId = columnExists("USER_ROLES", "ROLE_ID");

        if (!hasRoleName) {
            jdbcTemplate.execute("ALTER TABLE user_roles ADD COLUMN role_name VARCHAR(50)");
        }

        if (hasRoleId && tableExists("ROLES") && columnExists("ROLES", "ID") && columnExists("ROLES", "NAME")) {
            jdbcTemplate.execute(
                    """
                    UPDATE user_roles ur
                    SET role_name = (
                        SELECT CASE
                            WHEN UPPER(r.name) LIKE 'ROLE_%' THEN SUBSTRING(UPPER(r.name), 6)
                            ELSE UPPER(r.name)
                        END
                        FROM roles r
                        WHERE r.id = ur.role_id
                    )
                    WHERE ur.role_name IS NULL
                    """
            );
        }

        jdbcTemplate.execute(
                """
                UPDATE user_roles
                SET role_name = CASE
                    WHEN UPPER(role_name) LIKE 'ROLE_%' THEN SUBSTRING(UPPER(role_name), 6)
                    ELSE UPPER(role_name)
                END
                WHERE role_name IS NOT NULL
                """
        );

        Integer nullRoles = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_roles WHERE role_name IS NULL",
                Integer.class
        );

        if (nullRoles != null && nullRoles > 0) {
            throw new IllegalStateException("Unable to migrate some user roles. Please check user_roles data consistency.");
        }
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
