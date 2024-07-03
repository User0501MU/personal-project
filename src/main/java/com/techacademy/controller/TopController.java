
package com.techacademy.controller;//ログインcontrollerのTOP

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TopController {

    // ログイン画面表示
    @GetMapping(value = "/login")
    public String login() {
        return "login/login";
    }

    // ログイン後のトップページ表示
    @GetMapping(value = "/")
    public String top() {
    	return "redirect:/reports";
    }

}

//日報一覧画面：ログイン後の遷移先画面を従業員一覧画面から日報一覧画面に変更する（提供ソースコードの修正）

//修正前は、従業員一覧画面に当たる「/employees」にリダイレクト
// return "redirect:/employees";

// 修正後は、日報一覧画面に当たる「/reports」にリダイレクト
