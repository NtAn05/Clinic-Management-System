/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import model.ServicePrice;

/**
 *
 * @author admin
 */
public class ServiceDAO extends DBContext {

    //lay toan bo dich vu
    public List<ServicePrice> getAllServices() {
        List<ServicePrice> list = new ArrayList<>();
        String sql = """
            SELECT service_id, name, service_type, price
            FROM service_prices
        """;

        try (PreparedStatement st = connection.prepareStatement(sql); ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                ServicePrice s = new ServicePrice();
                s.setServiceId(rs.getInt("service_id"));
                s.setName(rs.getString("name"));
                s.setServiceType(rs.getString("service_type"));
                s.setPrice(rs.getBigDecimal("price"));
                list.add(s);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Khong the tai danh sach dich vu", e);
        }
        return list;
    }

    //lay 1 dich vu theo id
    public ServicePrice getServiceById(int id) {
        String sql = """
            SELECT service_id, name, service_type, price
            FROM service_prices
            WHERE service_id = ?
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                ServicePrice s = new ServicePrice();
                s.setServiceId(rs.getInt("service_id"));
                s.setName(rs.getString("name"));
                s.setServiceType(rs.getString("service_type"));
                s.setPrice(rs.getBigDecimal("price"));
                return s;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Khong the tim dich vu theo ID", e);
        }
        return null;
    }

    public boolean isServiceExist(String name, String serviceType) {
        return isServiceExistNormalized(name, serviceType, null);
    }

    public boolean isServiceExistForOtherId(String name, String serviceType, int excludedServiceId) {
        return isServiceExistNormalized(name, serviceType, excludedServiceId);
    }

    public boolean isServiceExistNormalized(String normalizedName, String serviceType, Integer excludedServiceId) {
        String sql = """
            SELECT service_id, name
            FROM service_prices
            WHERE service_type = ?
        """;
        if (excludedServiceId != null) {
            sql += " AND service_id <> ?";
        }
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, serviceType);
            if (excludedServiceId != null) {
                st.setInt(2, excludedServiceId);
            }
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                String dbName = normalizeName(rs.getString("name"));
                if (dbName.equalsIgnoreCase(normalizedName)) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Khong the kiem tra trung ten dich vu", e);
        }
    }

    //update dich vu
    public int updateService(ServicePrice s) {
        String sql = """
            UPDATE service_prices
            SET name = ?, service_type = ?, price = ?
            WHERE service_id = ?
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, s.getName());
            st.setString(2, s.getServiceType());
            st.setBigDecimal(3, s.getPrice());
            st.setInt(4, s.getServiceId());
            return st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Khong the cap nhat dich vu", e);
        }
    }

    //them dich vu
    public int addService(ServicePrice s) {
        String sql = """
        INSERT INTO service_prices (name, service_type, price)
        VALUES (?, ?, ?)
    """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, s.getName());
            st.setString(2, s.getServiceType());
            st.setBigDecimal(3, s.getPrice());
            return st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Khong the them dich vu", e);
        }
    }

    //xoa dich vu
    public int deleteService(int serviceId) {
        String sql = "DELETE FROM service_prices WHERE service_id = ?";
        
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, serviceId);
            return st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Khong the xoa dich vu", e);
        }
    }

    private String normalizeName(String name) {
        if (name == null) {
            return "";
        }
        return name.trim().replaceAll("\\s+", " ");
    }
}
