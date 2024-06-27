package com.techacademy.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Employee.Role;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class EmployeeServiceTest {

    @Autowired
    private EmployeeService service;

    @Test
    @WithMockUser
    void testFindAll() {

        List<Employee> employeeList = service.findAll();

        // employeeListをstreamへ変換した上で、streamのfilterメソッドでCodeが1の受講生のオブジェクトのみ取得する
        Employee employeeCode1 = employeeList.stream().filter(e -> "1".equals(e.getCode())).findFirst().get();
        assertEquals(employeeCode1.getCode(), "1");
        assertEquals(employeeCode1.getName(), "煌木　太郎");
        assertEquals(employeeCode1.getRole(), Role.ADMIN);
        assertEquals(employeeCode1.getPassword(), "$2a$10$vY93/U2cXCfEMBESYnDJUevcjJ208sXav23S.K8elE/J6Sxr4w5jO");

        // employeeListをstreamへ変換した上で、streamのfilterメソッドでCodeが2の受講生のオブジェクトのみ取得する
        Employee employeeCode2 = employeeList.stream().filter(e -> "2".equals(e.getCode())).findFirst().get();
        assertEquals(employeeCode2.getCode(), "2");
        assertEquals(employeeCode2.getName(), "田中　太郎");
        assertEquals(employeeCode2.getRole(), Role.GENERAL);
        assertEquals(employeeCode2.getPassword(), "$2a$10$HPIjRCymeRZKEIq.71TDduiEotOlb8Ai6KQUHCs4lGNYlLhcKv4Wi");

        // 登録日付、更新日付はミリ秒単位での結果比較となるためテストでの確認不可
    }

    @Test
    @WithMockUser
    void testFindByCode() {

        // 取得できた場合
        Employee employee = service.findByCode("1");
        assertEquals(employee.getCode(), "1");
        assertEquals(employee.getName(), "煌木　太郎");
        assertEquals(employee.getRole(), Role.ADMIN);
        assertEquals(employee.getPassword(), "$2a$10$vY93/U2cXCfEMBESYnDJUevcjJ208sXav23S.K8elE/J6Sxr4w5jO");
        // 登録日付、更新日付はミリ秒単位での結果比較となるためテストでの確認不可

        // 取得できなかった場合
        Employee employeeNull = service.findByCode("100");
        assertEquals(employeeNull, null);

    }

}
