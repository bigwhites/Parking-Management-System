package com.wtu.parking;


import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    DataSource dataSource;

    /**
     * 验证数据库连接
     * @throws Exception
     */
    @Test
    void testConnection() throws Exception {
        // 默认：class com.zaxxer.hikari.HikariDataSource
        // Druid：class com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceWrapper
        System.out.println(dataSource.getClass());
        Connection connection = dataSource.getConnection();
        // 默认：HikariProxyConnection@1323996324 wrapping com.mysql.cj.jdbc.ConnectionImpl@7c281eb8
        // Druid：com.mysql.cj.jdbc.ConnectionImpl@3166f664
        System.out.println(connection);
        connection.close();
    }

    /**
     * 验证连接池的配置信息，是否生效
     * @throws Exception
     */
    @Test
    void contextLoads() throws Exception {
        System.out.println(dataSource.getClass());
        DruidDataSource druidDataSource = (DruidDataSource) dataSource;
        System.out.println("initSize:" + druidDataSource.getInitialSize());
        System.out.println("maxSize:" + druidDataSource.getMaxActive());
        Connection connection = dataSource.getConnection();
        System.out.println(connection);
        connection.close();
    }

}
