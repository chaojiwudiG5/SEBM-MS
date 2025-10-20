package com.group5.sebmmodels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * List<Integer> 类型处理器
 * 用于处理 notificationMethod 字段的 JSON 序列化和反序列化
 */
public class ListTypeHandler extends BaseTypeHandler<List<Integer>> {

    private final ObjectMapper objectMapper;
    
    public ListTypeHandler() {
        this.objectMapper = new ObjectMapper();
        // 配置ObjectMapper以确保正确的JSON输出
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Integer> parameter, JdbcType jdbcType) throws SQLException {
        try {
            if (parameter == null || parameter.isEmpty()) {
                ps.setNull(i, Types.VARCHAR);
                return;
            }
            
            String jsonString = objectMapper.writeValueAsString(parameter);
            // 确保JSON字符串是有效的UTF-8编码
            if (jsonString == null || jsonString.trim().isEmpty()) {
                ps.setNull(i, Types.VARCHAR);
                return;
            }
            
            // 使用setObject方法，让JDBC驱动处理类型转换
            ps.setObject(i, jsonString, Types.VARCHAR);
        } catch (Exception e) {
            throw new SQLException("Failed to serialize List<Integer> to JSON: " + parameter, e);
        }
    }

    @Override
    public List<Integer> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return parseJson(json);
    }

    @Override
    public List<Integer> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return parseJson(json);
    }

    @Override
    public List<Integer> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return parseJson(json);
    }

    /**
     * 解析JSON字符串为List<Integer>
     * @param json JSON字符串
     * @return List<Integer> 或 null
     */
    private List<Integer> parseJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<Integer>>() {});
        } catch (Exception e) {
            // 如果解析失败，返回null
            return null;
        }
    }
}


