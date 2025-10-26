package app.parts.dao;

import app.parts.Feature;
import app.parts.FeatureType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeatureDao {

    public Feature insert(Connection c, long partId, Feature f) throws SQLException {
        String sql = "INSERT INTO features(part_id, type, x, y, d1, d2) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, partId);
            ps.setString(2, f.getType().name());
            ps.setDouble(3, f.getX());
            ps.setDouble(4, f.getY());
            ps.setDouble(5, f.getD1());
            ps.setDouble(6, f.getD2());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    return new Feature(id, partId, f.getType(), f.getX(), f.getY(), f.getD1(), f.getD2());
                }
            }
        }
        throw new SQLException("Failed to insert feature");
    }

    public List<Feature> listByPart(Connection c, long partId) throws SQLException {
        String sql = "SELECT id, part_id, type, x, y, d1, d2 FROM features WHERE part_id = ? ORDER BY id";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, partId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Feature> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(map(rs));
                }
                return out;
            }
        }
    }
        // delete a single feature ensuring it belongs to the same part
    public int deleteByIdAndPart(Connection c, long featureId, long partId) throws SQLException {
        try (var ps = c.prepareStatement(
            "DELETE FROM features WHERE id = ? AND part_id = ?")) {
            ps.setLong(1, featureId);
            ps.setLong(2, partId);
            return ps.executeUpdate(); // 0 or 1
        }
    }


    private Feature map(ResultSet rs) throws SQLException {
        return new Feature(
            rs.getLong("id"),
            rs.getLong("part_id"),
            FeatureType.valueOf(rs.getString("type")),
            rs.getDouble("x"),
            rs.getDouble("y"),
            rs.getDouble("d1"),
            rs.getDouble("d2")
        );
    }
}
