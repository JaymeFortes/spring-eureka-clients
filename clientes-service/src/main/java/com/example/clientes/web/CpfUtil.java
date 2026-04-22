package com.example.clientes.web;

public final class CpfUtil {

	private CpfUtil() {
	}

	public static String normalize(String cpf) {
		if (cpf == null) {
			return "";
		}
		return cpf.replaceAll("\\D", "");
	}

	public static boolean isValidLength(String digits) {
		return digits != null && digits.length() == 11;
	}
}
