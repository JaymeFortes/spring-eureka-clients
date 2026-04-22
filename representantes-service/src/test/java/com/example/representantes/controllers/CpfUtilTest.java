package com.example.representantes.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.representantes.web.CpfUtil;

class CpfUtilTest {

    @Test
    void normalizeDeveRetornarSomenteDigitos() {
        assertEquals("12345678900", CpfUtil.normalize("123.456.789-00"));
    }

    @Test
    void normalizeDeveRetornarVazioQuandoNulo() {
        assertEquals("", CpfUtil.normalize(null));
    }

    @Test
    void isValidLengthDeveValidarOnzeDigitos() {
        assertTrue(CpfUtil.isValidLength("12345678900"));
        assertFalse(CpfUtil.isValidLength("123"));
        assertFalse(CpfUtil.isValidLength(null));
    }
}
