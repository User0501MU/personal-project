package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Employee.Role;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

	private final ReportService reportService;

	@Autowired
	public ReportController(ReportService reportService) {
		this.reportService = reportService;
	}

	// 1日報一覧画面
	@GetMapping
	public String list(@AuthenticationPrincipal UserDetail userDetail, Model model) {

		if (userDetail.getEmployee().getRole().equals(Role.ADMIN)) {
			model.addAttribute("listSize", reportService.findAll().size());
			model.addAttribute("reportList", reportService.findAll());
		} else {
			model.addAttribute("listSize", reportService.findByCode(userDetail.getEmployee().getCode()).size());
			model.addAttribute("reportList", reportService.findByCode(userDetail.getEmployee().getCode()));
		}

		return "reports/list";
	}

	 // 2日報新規登録画面
    @GetMapping(value = "/add")
    public String create(Report report, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        model.addAttribute("report", report);
        model.addAttribute("name", userDetail.getEmployee().getName());
        return "reports/new";
    }

    // 2日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, @AuthenticationPrincipal UserDetail userDetail,
            Model model) {

        // 入力foamがあるので入力チェック必須
    	//変数resがエラーを持ってるかチェックするメソッド（操作）trueを返す場合（エラーがある場合）以下の処理が実行される
        if (res.hasErrors()) {
        	//createメソッド
            return create(report, userDetail, model);
        }

        // 登録処理
        ErrorKinds result = reportService.save(report, userDetail);
        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return create(report, userDetail, model);
        }

        return "redirect:/reports";

    }


	// 3日報詳細画面
	@GetMapping(value = "/{ID}/")
	public String detail(@PathVariable("ID") String id, Model model) {

		model.addAttribute("report", reportService.findById(id));
		return "reports/detail";
	}



	// 4日報更新画面
    @GetMapping(value = "/{ID}/update")
    public String edit(@PathVariable(name = "ID", required = false) String id, Report report, Model model) {
        if (report.getId() == null) {
            model.addAttribute("report", reportService.findById(id));
        } else {
            model.addAttribute("report", report);
        }
        return "reports/update";
    }

    // 4日報更新処理
    @PostMapping(value = "/{ID}/update")
    public String update(@PathVariable("ID") String id, @Validated Report report, BindingResult res, Model model) {

        // データチェック用に必要なレポート情報をセット
        report.setId(Integer.valueOf(reportService.findById(id).getId()));
        report.setEmployee(reportService.findById(id).getEmployee());

        // 入力チェック
        if (res.hasErrors()) {
            return edit(null, report, model);
        }

        //4登録処理
        ErrorKinds result = reportService.save(id, report);
        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return edit(null, report, model);
        }

        return "redirect:/reports";
    }

    // 日報削除処理
    @PostMapping(value = "/{ID}/delete")
    public String delete(@PathVariable("ID") String id, Model model) {

        reportService.delete(id);

        return "redirect:/reports";
    }

}
