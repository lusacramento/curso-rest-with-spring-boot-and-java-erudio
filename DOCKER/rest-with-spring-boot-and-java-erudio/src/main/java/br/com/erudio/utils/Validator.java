package br.com.erudio.utils;

import static br.com.erudio.utils.Utils.replaceCommaToDot;

public class Validator {
    public static boolean isNumeric(String strNumber){
        if(strNumber == null || strNumber.isEmpty()) return false;

        return replaceCommaToDot(strNumber).matches("[+-]?[0-9]*\\.?[0-9]+");
    }
}
