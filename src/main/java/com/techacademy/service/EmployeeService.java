package com.techacademy.service;//Serviceには、モデルのユースケースを書く

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.repository.EmployeeRepository;
import org.springframework.transaction.annotation.Transactional;

@Service // @Service注釈がついてるから、サービスとして動作する準備完了
public class EmployeeService {

	private final EmployeeRepository employeeRepository;// リポジトリ
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
		this.employeeRepository = employeeRepository;
		this.passwordEncoder = passwordEncoder;
	}

	// 従業員保存
	@Transactional
	public ErrorKinds save(Employee employee) {//+employeeクラス

		// パスワードチェック
		ErrorKinds result = employeePasswordCheck(employee);
		if (ErrorKinds.CHECK_OK != result) {
			return result;
		}

		// 従業員番号重複チェック
		if (findByCode(employee.getCode()) != null) {
			return ErrorKinds.DUPLICATE_ERROR;
		}

		employee.setDeleteFlg(false);

		LocalDateTime now = LocalDateTime.now();
		employee.setCreatedAt(now);
		employee.setUpdatedAt(now);

		employeeRepository.save(employee);
		return ErrorKinds.SUCCESS;
	}

	// 従業員更新★6
	// @Transactional アノテーションがついているので、このメソッドはトランザクションの管理下で実行されることが期待されています
	@Transactional
	public ErrorKinds save(String code, Employee employee) {//+引数をcontrollerに渡す
		// String code は従業員のコード（もしくはID主キー）を受け取るパラメータで、Employee employee
		// は更新する従業員の情報を受け取るパラメータ
		// パスワードチェック
		// まず従業員のパスワードが空であるかどうかをチェックしています equalsメソッド演算子
		if ("".equals(employee.getPassword())) {
			employee.setPassword(findByCode(code).getPassword());
			// もし employee.getPassword() が空文字列 ""
			// であれば、既存の従業員情報からパスワードを取得して、employee.setPassword() で設定しています
			// つまり、パスワードが更新されていない場合は、以前のパスワードをそのまま利用するということです。
			// 上記、※employee.getPassword() が空でない場合、=
			// employeePasswordCheck(employee)を呼び出してパスワードのチェックを行います
		} else {
			ErrorKinds result = employeePasswordCheck(employee);
			// = employeePasswordCheck(employee) でエラーが検出された場合、それを ErrorKinds の形式で返しています。
			if (ErrorKinds.CHECK_OK != result) {
			// ErrorKinds.CHECK_OK はおそらく正常な状態を表す定数で、それ以外の場合はエラーが発生したことを示しています。
				return result;
			}
		}

		employee.setDeleteFlg(false);

		LocalDateTime now = LocalDateTime.now();
		employee.setCreatedAt(findByCode(code).getCreatedAt());
		employee.setUpdatedAt(now);

		employeeRepository.save(employee);
		return ErrorKinds.SUCCESS;
	}

	// 従業員削除
	@Transactional
	public ErrorKinds delete(String code, UserDetail userDetail) {

		// 自分を削除しようとした場合はエラーメッセージを表示
		if (code.equals(userDetail.getEmployee().getCode())) {
			return ErrorKinds.LOGINCHECK_ERROR;
		}
		Employee employee = findByCode(code);
		LocalDateTime now = LocalDateTime.now();
		employee.setUpdatedAt(now);
		employee.setDeleteFlg(true);

		return ErrorKinds.SUCCESS;
	}

	// 従業員一覧表示処理
	public List<Employee> findAll() {
		return employeeRepository.findAll();
	}

	// 1件を検索
	public Employee findByCode(String code) {
		// findByIdで検索
		Optional<Employee> option = employeeRepository.findById(code);
		// 取得できなかった場合はnullを返す
		Employee employee = option.orElse(null);
		return employee;
	}

	// 従業員パスワードチェック
	private ErrorKinds employeePasswordCheck(Employee employee) {

		// 従業員パスワードの半角英数字チェック処理
		if (isHalfSizeCheckError(employee)) {

			return ErrorKinds.HALFSIZE_ERROR;
		}

		// 従業員パスワードの8文字～16文字チェック処理
		if (isOutOfRangePassword(employee)) {

			return ErrorKinds.RANGECHECK_ERROR;
		}

		employee.setPassword(passwordEncoder.encode(employee.getPassword()));

		return ErrorKinds.CHECK_OK;
	}

	// 従業員パスワードの半角英数字チェック処理
	private boolean isHalfSizeCheckError(Employee employee) {

		// 半角英数字チェック
		Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
		Matcher matcher = pattern.matcher(employee.getPassword());
		return !matcher.matches();
	}

	// 従業員パスワードの8文字～16文字チェック処理
	public boolean isOutOfRangePassword(Employee employee) {

		// 桁数チェック
		int passwordLength = employee.getPassword().length();
		return passwordLength < 8 || 16 < passwordLength;
	}

}
