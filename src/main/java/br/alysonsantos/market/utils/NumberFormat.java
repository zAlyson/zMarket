package br.alysonsantos.market.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberFormat {

    public static String formartAmount(double amount) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##", new DecimalFormatSymbols(Locale.US));

        String numeroFormatado = decimalFormat.format(amount);

        String sigla = "";
        String[] casas = numeroFormatado.split(",");

        int tamanho = casas.length;
//		System.out.println("Tamanho: " + tamanho);
        if (tamanho >= 12) {
            sigla = "D";
        } else if (tamanho >= 11) {
            sigla = "N";
        } else if (tamanho >= 10) {
            sigla = "O";
        } else if (tamanho >= 9) {
            sigla = "SS";
        } else if (tamanho >= 8) {
            sigla = "S";
        } else if (tamanho >= 7) {
            sigla = "QQ";
        } else if (tamanho >= 6) {
            sigla = "Q";
        } else if (tamanho >= 5) {
            sigla = "T";
        } else if (tamanho >= 4) {
            sigla = "B";
        } else if (tamanho >= 3) {
            sigla = "M";
        } else if (tamanho >= 2) {
            sigla = "K";
        }

        if (tamanho > 10) {

        }
        if (tamanho >= 2) {
            return casas[0] + sigla;
        } else {
            return casas[0];
        }
    }

    public static String formartAmount2(double amount) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##", new DecimalFormatSymbols(Locale.US));
        String numeroFormatado = decimalFormat.format(amount);

        return numeroFormatado;
    }
}
