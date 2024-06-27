package com.techacademy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Employee.Role;
import com.techacademy.service.UserDetail;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class EmployeeControllerTest {

    private MockMvc mockMvc;

    private final WebApplicationContext webApplicationContext;

    EmployeeControllerTest(WebApplicationContext context) {
        this.webApplicationContext = context;
    }

    @BeforeEach
    void beforeEach() {
        // Spring Securityを有効にする
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
    }

    // 従業員一覧画面
    // テストケース1 正常終了
    @Test
    @WithMockUser(authorities = "ADMIN")
    void testList() throws Exception {
        // HTTPリクエストに対するレスポンスの検証
        MvcResult result = mockMvc.perform(get("/employees")) // URLにアクセス
                .andExpect(status().isOk()) // ステータスを確認
                .andExpect(model().attributeExists("employeeList")) // Modelの内容を確認
                .andExpect(model().hasNoErrors()) // Modelのエラー有無の確認
                .andExpect(view().name("employees/list")) // viewの確認
                .andReturn(); // 内容の取得

        @SuppressWarnings("unchecked")
        List<Employee> employeeList = (List<Employee>) result.getModelAndView().getModel().get("employeeList");

        // employeeListをstreamへ変換した上で、streamのfilterメソッドでCodeが2の受講生のオブジェクトのみ取得する
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

    }

    // 従業員詳細画面
    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDetail() throws Exception {
        // HTTPリクエストに対するレスポンスの検証
        MvcResult result = mockMvc.perform(get("/employees/1/")) // URLにアクセス
                .andExpect(status().isOk()) // ステータスを確認
                .andExpect(model().attributeExists("employee")) // Modelの内容を確認
                .andExpect(model().hasNoErrors()) // Modelのエラー有無の確認
                .andExpect(view().name("employees/detail")) // viewの確認
                .andReturn(); // 内容の取得

        Employee employee = (Employee) result.getModelAndView().getModel().get("employee");
        assertEquals(employee.getCode(), "1");
        assertEquals(employee.getName(), "煌木　太郎");
        assertEquals(employee.getRole(), Role.ADMIN);
    }

    // 従業員新規登録画面
    @Test
    @WithMockUser(authorities = "ADMIN")
    void testCreate() throws Exception {
        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(get("/employees/add")) // URLにアクセス
                .andExpect(status().isOk()) // ステータスを確認
                .andExpect(model().attributeExists("employee")) // Modelの内容を確認
                .andExpect(model().hasNoErrors()) // Modelのエラー有無の確認
                .andExpect(view().name("employees/new")); // viewの確認

    }

    // 従業員新規登録処理
    // テストケース1 正常終了
    @Test
    @WithMockUser(authorities = "ADMIN")
    @Transactional
    void testAddSuccess() throws Exception {

        Employee employee = new Employee();
        employee.setCode("3");
        employee.setName("テスト太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("12345678");
        Role role = Role.GENERAL;
        employee.setRole(role);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform((post("/employees/add")).flashAttr("employee", employee).with(csrf()))
                .andExpect(redirectedUrl("/employees"));

    }

    // テストケース2 氏名入力漏れ
    @Test
    @WithMockUser(authorities = "ADMIN")
    void testAddError1() throws Exception {

        Employee employee = new Employee();
        employee.setCode("3");
        employee.setName("");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("12345678");
        Role role = Role.GENERAL;
        employee.setRole(role);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform((post("/employees/add")).flashAttr("employee", employee).with(csrf()))
                .andExpect(view().name("employees/new"));

    }

    // テストケース3 パスワード入力漏れ
    @Test
    @WithMockUser(authorities = "ADMIN")
    void testAddError2() throws Exception {

        Employee employee = new Employee();
        employee.setCode("3");
        employee.setName("テスト太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.GENERAL;
        employee.setRole(role);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform((post("/employees/add")).flashAttr("employee", employee).with(csrf()))
                .andExpect(view().name("employees/new"));

    }

    // テストケース4 従業員番号重複
    @Test
    @WithMockUser(authorities = "ADMIN")
    void testAddError3() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("テスト太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("12345678");
        Role role = Role.GENERAL;
        employee.setRole(role);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform((post("/employees/add")).flashAttr("employee", employee).with(csrf()))
                .andExpect(view().name("employees/new"));

    }

    // テストケース5 従業員番号10文字以上
    @Test
    @WithMockUser(authorities = "ADMIN")
    void testAddError4() throws Exception {

        Employee employee = new Employee();
        employee.setCode("11111111111");
        employee.setName("テスト三郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("12345678");
        Role role = Role.GENERAL;
        employee.setRole(role);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform((post("/employees/add")).flashAttr("employee", employee).with(csrf()))
                .andExpect(view().name("employees/new"));

    }

    // テストケース6 パスワード8文字以下
    @Test
    @WithMockUser(authorities = "ADMIN")
    void testAddError5() throws Exception {

        Employee employee = new Employee();
        employee.setCode("3");
        employee.setName("テスト三郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("12345");
        Role role = Role.GENERAL;
        employee.setRole(role);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform((post("/employees/add")).flashAttr("employee", employee).with(csrf()))
                .andExpect(view().name("employees/new"));

    }

    // テストケース7 パスワード半角英数字以外
    @Test
    @WithMockUser(authorities = "ADMIN")
    void testAddError6() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("テスト太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("テスト");
        Role role = Role.GENERAL;
        employee.setRole(role);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform((post("/employees/add")).flashAttr("employee", employee).with(csrf()))
                .andExpect(view().name("employees/new"));

    }

    // 従業員削除処理
    // テストケース1 正常終了
    @Test
    @WithMockUser(authorities = "ADMIN")
    @Transactional
    void testDeleteSuccess1() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("煌木　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.ADMIN;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform((post("/employees/2/delete")).with(user(userDetail)).with(csrf()))
                .andExpect(redirectedUrl("/employees"));

    }

    // テストケース2 日報情報が存在
    @Test
    @WithMockUser(authorities = "ADMIN")
    @Transactional
    void testDeleteSuccess2() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("煌木　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.ADMIN;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform((post("/employees/2/delete")).with(user(userDetail)).with(csrf()))
                .andExpect(redirectedUrl("/employees"));

    }

    // テストケース2 ログイン中の従業員削除
    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDeleteError1() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("煌木　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.ADMIN;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform((post("/employees/1/delete")).with(user(userDetail)).with(csrf()))
                .andExpect(view().name("employees/detail"));

    }

}
