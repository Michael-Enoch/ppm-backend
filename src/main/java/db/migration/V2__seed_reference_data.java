package db.migration;

import com.company.ppm.domain.enums.RoleName;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class V2__seed_reference_data extends BaseJavaMigration {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        Map<RoleName, Long> roleIds = ensureRoles(connection);
        long orgId = ensureOrganization(connection, "Default Organization", "DEFAULT");

        long adminId = ensureUser(connection, orgId, "admin@example.com", "System Admin", "AdminPass123!");
        long managerId = ensureUser(connection, orgId, "manager@example.com", "Org Manager", "AdminPass123!");
        long pmId = ensureUser(connection, orgId, "pm@example.com", "Project Manager", "AdminPass123!");
        long memberId = ensureUser(connection, orgId, "member@example.com", "Team Member", "AdminPass123!");
        long viewerId = ensureUser(connection, orgId, "viewer@example.com", "Read Only", "AdminPass123!");

        assignRole(connection, adminId, roleIds.get(RoleName.ADMIN));
        assignRole(connection, adminId, roleIds.get(RoleName.ORG_MANAGER));
        assignRole(connection, adminId, roleIds.get(RoleName.PM));

        assignRole(connection, managerId, roleIds.get(RoleName.ORG_MANAGER));
        assignRole(connection, pmId, roleIds.get(RoleName.PM));
        assignRole(connection, memberId, roleIds.get(RoleName.MEMBER));
        assignRole(connection, viewerId, roleIds.get(RoleName.VIEWER));
    }

    private Map<RoleName, Long> ensureRoles(Connection connection) throws SQLException {
        Map<RoleName, Long> ids = new EnumMap<>(RoleName.class);
        for (RoleName role : RoleName.values()) {
            Long existingId = findId(connection, "SELECT id FROM roles WHERE name = ?", role.name());
            if (existingId != null) {
                ids.put(role, existingId);
                continue;
            }
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO roles(name) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, role.name());
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        ids.put(role, keys.getLong(1));
                    }
                }
            }
        }
        return ids;
    }

    private long ensureOrganization(Connection connection, String name, String code) throws SQLException {
        Long existingId = findId(connection, "SELECT id FROM organizations WHERE code = ?", code);
        if (existingId != null) {
            return existingId;
        }

        Instant now = Instant.now();
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO organizations(name, code, created_at, updated_at) VALUES(?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, name);
            statement.setString(2, code);
            statement.setTimestamp(3, Timestamp.from(now));
            statement.setTimestamp(4, Timestamp.from(now));
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Unable to create default organization");
    }

    private long ensureUser(Connection connection, long organizationId, String email, String fullName, String rawPassword)
            throws SQLException {
        Long existingId = findId(connection, "SELECT id FROM app_users WHERE email = ?", email);
        if (existingId != null) {
            return existingId;
        }

        Instant now = Instant.now();
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO app_users(email, password_hash, full_name, active, organization_id, created_at, updated_at) "
                        + "VALUES(?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, email);
            statement.setString(2, ENCODER.encode(rawPassword));
            statement.setString(3, fullName);
            statement.setBoolean(4, true);
            statement.setLong(5, organizationId);
            statement.setTimestamp(6, Timestamp.from(now));
            statement.setTimestamp(7, Timestamp.from(now));
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Unable to create user " + email);
    }

    private void assignRole(Connection connection, long userId, long roleId) throws SQLException {
        Long existing = findId(
                connection,
                "SELECT user_id FROM user_roles WHERE user_id = ? AND role_id = ?",
                userId,
                roleId
        );
        if (existing != null) {
            return;
        }
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO user_roles(user_id, role_id) VALUES (?, ?)"
        )) {
            statement.setLong(1, userId);
            statement.setLong(2, roleId);
            statement.executeUpdate();
        }
    }

    private Long findId(Connection connection, String query, Object... params) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return null;
    }
}
