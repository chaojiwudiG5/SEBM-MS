package com.group5.sebmmaintenanceservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.group5")
@MapperScan("com.group5.sebmmaintenanceservice.dao")
@EnableFeignClients(basePackages = "com.group5.sebmserviceclient.service")
public class SebmMaintenanceServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(SebmMaintenanceServiceApplication.class, args);
  }

}
