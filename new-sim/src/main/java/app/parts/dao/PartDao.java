package app.parts.dao;

import app.parts.Part;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PartDao {

    // existing insert/find/list...
    public Part insert(Connection c, Part part) throws SQLException {
        String sql = "INSERT INTO parts(part_number, name, unit) VALUES(?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, part.getPartNumber());
            ps.setString(2, part.getName());
            ps.setString(3, part.getUnit());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    return new Part(id, part.getPartNumber(), part.getName(), part.getUnit());
                }
            }
        }
        throw new SQLException("Failed to insert part");
    }
    /** Return a Part row with featureCount populated (no features loaded). */
    public Optional<Part> findByPartNumber(Connection c, String partNumber) throws SQLException {
         String sql = """
           SELECT p.id, p.part_number, p.name, p.unit,
                   (SELECT COUNT(*) FROM features f WHERE f.part_id = p.id) AS feature_count
            FROM parts p
            WHERE p.part_number = ?
            """;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, partNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Part(
                        rs.getLong("id"),
                        rs.getString("part_number"),
                        rs.getString("name"),
                        rs.getString("unit"),
                        rs.getLong("feature_count")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    public List<Part> listAll(Connection c) throws SQLException {
        String sql = """
            SELECT p.id, p.part_number, p.name, p.unit,
                   (SELECT COUNT(*) FROM features f WHERE f.part_id = p.id) AS feature_count
            FROM parts p
            ORDER BY p.id
            """;
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Part> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new Part(
                    rs.getLong("id"),
                    rs.getString("part_number"),
                    rs.getString("name"),
                    rs.getString("unit"),
                    rs.getLong("feature_count")
                ));
            }
            return out;
        }
    }
    // delete a part by business key; features will cascade if FK is set correctly
    public int deleteByPartNumber(Connection c, String partNumber) throws SQLException {
        try (var ps = c.prepareStatement("DELETE FROM parts WHERE part_number = ?")) {
            ps.setString(1, partNumber);
            return ps.executeUpdate(); // rows deleted (0 or 1)
        }
    }

}
