package com.grupoib3.schmidt.app_motorista.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class TransformaDados {

    public static Date ReturnData(String receiver, String _Utc) throws ParseException {
        try{
            Date calendar;
            if(receiver.length() > 0){
                SimpleTimeZone.setDefault(TimeZone.getTimeZone(_Utc));
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
                calendar = format.parse(receiver);
            } else
            {
                calendar = new Date();
            }
            return calendar;
        }catch (Exception ex){
            throw ex;
        }
    }

    public static String FormataData(String data){
        try{
            if(data.length() == 8){
                String dataFormatada = data.substring(6,8) + "/" + data.substring(4,6) + "/" + data.substring(0,4);
                return dataFormatada;
            }else{
                return data;
            }

        }catch (Exception Ex){
            throw  Ex;
        }
    }

    public static String PrimeiraLetraMaius(String dado){
        try{
            if(!dado.equals("")){
                String[] quebraStr = dado.split(" ");
                String retorno = "";
                for (int i = 0; i < quebraStr.length; i++){
                    retorno += quebraStr[i].substring(0,1).toUpperCase() + quebraStr[i].substring(1, quebraStr[i].length()).toLowerCase() + " ";
                }
                return retorno;
            }
            return dado;
        }catch (Exception ex){
            throw ex;
        }
    }

    private static final int[] pesoCPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] pesoCNPJ = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private static int calcularDigito(String str, int[] peso) {
        int soma = 0;
        for (int indice=str.length()-1, digito; indice >= 0; indice-- ) {
            digito = Integer.parseInt(str.substring(indice,indice+1));
            soma += digito*peso[peso.length-str.length()+indice];
        }
        soma = 11 - soma % 11;
        return soma > 9 ? 0 : soma;
    }

    public static boolean isValidCPF(String cpf) {
        if ((cpf==null) || (cpf.length()!=11)) return false;

        Integer digito1 = calcularDigito(cpf.substring(0,9), pesoCPF);
        Integer digito2 = calcularDigito(cpf.substring(0,9) + digito1, pesoCPF);
        return cpf.equals(cpf.substring(0,9) + digito1.toString() + digito2.toString());
    }

    public static boolean isValidCNPJ(String cnpj) {
        if ((cnpj==null)||(cnpj.length()!=14)) return false;

        Integer digito1 = calcularDigito(cnpj.substring(0,12), pesoCNPJ);
        Integer digito2 = calcularDigito(cnpj.substring(0,12) + digito1, pesoCNPJ);
        return cnpj.equals(cnpj.substring(0,12) + digito1.toString() + digito2.toString());
    }
}
