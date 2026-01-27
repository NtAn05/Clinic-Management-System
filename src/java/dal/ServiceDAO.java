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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return null;
    }

    //update dich vu
    public void updateService(ServicePrice s) {
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
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //them dich vu
    public void addService(ServicePrice s) {
        String sql = """
        INSERT INTO service_prices (name, service_type, price)
        VALUES (?, ?, ?)
    """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, s.getName());
            st.setString(2, s.getServiceType());
            st.setBigDecimal(3, s.getPrice());
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //xoa dich vu
    public void deleteService(int serviceId) {
        String sql = "DELETE FROM service_price WHERE service_id = ?";
        
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, serviceId);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
