package com.example.clientes.web;

final class CpfUtil {

	private CpfUtil() {
	}

	static String normalize(String cpf) {
		if (cpf == null) {
			return "";
		}
		return cpf.replaceAll("\\D", "");
	}

	static boolean isValidLength(String digits) {
		return digits != null && digits.length() == 11;
	}
}
