package com.jsite;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * JSite 快速开发平台启动类
 *
 * @author jsite
 */
@EnableAsync
@MapperScan("com.jsite.**.mapper")
@SpringBootApplication
public class JsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(JsiteApplication.class, args);
        System.out.println("===========================================");
        System.out.println("   _     _ _        ");
        System.out.println("  (_)___(_) |_ ___  ");
        System.out.println("  | / __| | __/ _ \\ ");
        System.out.println("  | \\__ \\ | ||  __/ ");
        System.out.println(" _/ |___/_|\\__\\___| ");
        System.out.println("|__/                ");
        System.out.println("===========================================");
        System.out.println("  JSite 快速开发平台启动成功!");
        System.out.println("  接口文档: http://localhost:8080/doc.html");
        System.out.println("===========================================");
    }
}
