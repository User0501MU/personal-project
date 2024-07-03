//テーブル定義（エンティティ）参照
package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

	private final ReportRepository reportRepository;

	// 初期化
	@Autowired
	// DIコンテナから、インスタンスを注入するための指定のこと ※インスタンス化newの処理がspringのDIコンテナが管理している
	public ReportService(ReportRepository reportRepository) {
		this.reportRepository = reportRepository;
	}

//日報一覧表示処理★?
	public List<Report> findAll() {
		return reportRepository.findAll();
	}

//従業員の投稿を検索
	// 従業員の投稿を検索
    public List<Report> findByCode(String code) {
        List<Report> allReport = reportRepository.findAll();
        List<Report> reportList = new ArrayList<>();
        for (Report report : allReport) {
            if (report.getEmployee().getCode().equals(code)) {
                reportList.add(report);
            }
        }
        return reportList;
    }

//1件を検索
	public Report findById(String id) {
		// findByIdで検索
		Optional<Report> option = reportRepository.findById(Integer.valueOf(id));
		// 取得できなかった場合はnullを返す
		Report report = option.orElse(null);
		return report;
	}

// 日報保存
	@Transactional
	public ErrorKinds save(Report report, UserDetail userDetail) {
		// セッションから従業員情報を取得
		Employee employee = userDetail.getEmployee();

		// 日付重複チェック　
		//複数の変数にまとめられる箱　findALLで全検索
		List<Report> reportList = findAll();
		for (Report regReport : reportList) {
			if (regReport.getReportDate().equals(report.getReportDate())
					&& regReport.getEmployee().getCode().equals(userDetail.getEmployee().getCode())) {

				return ErrorKinds.DATECHECK_ERROR;
			}
		}

		report.setDeleteFlg(false);
		report.setEmployee(employee);

		LocalDateTime now = LocalDateTime.now();
		report.setCreatedAt(now);
		report.setUpdatedAt(now);

		reportRepository.save(report);
		return ErrorKinds.SUCCESS;
	}

	// 日報更新
    @Transactional
    public ErrorKinds save(String id, Report report) {
        //　日付重複チェック
        List<Report> reportList = findAll();
        for (Report regReport : reportList) {

            if (!(regReport.getId().equals(report.getId())) && regReport.getReportDate().equals(report.getReportDate())
                    && regReport.getEmployee().getCode().equals(report.getEmployee().getCode())) {

                return ErrorKinds.DATECHECK_ERROR;
            }
        }

        // 登録済みレポート情報と更新情報をマージ
        Report tmp = report;
        report = findById(id);
        report.setReportDate(tmp.getReportDate());
        report.setTitle(tmp.getTitle());
        report.setContent(tmp.getContent());

        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 日報削除機能
    @Transactional
    public ErrorKinds delete(String id) {
        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

}

