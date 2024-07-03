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

	// 日報一覧画面
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

	// 日報詳細画面
	@GetMapping(value = "/{ID}/")
	public String detail(@PathVariable("ID") String id, Model model) {

		model.addAttribute("report", reportService.findById(id));
		return "reports/detail";
	}
}