package com.grupoib3.schmidt.app_motorista.Utils;

import java.util.Date;
import java.util.regex.Pattern;

public class TransformaDados {

    public static Date ReturnData(String receiver){
        try{
            Date calendar;
            if(receiver.length() > 0){
                String[] sDataHora = receiver.split(Pattern.quote(" "));
                if(sDataHora.length == 3){
                    String[] sData = sDataHora[0].split(Pattern.quote("/"));
                    String[] sHora = sDataHora[1].split(Pattern.quote(":"));
                    int hora = Integer.parseInt(sHora[0]);
                    if(sDataHora[2].equals("PM")){
                        hora += 12;
                    }
                    if(sData.length == 3 || sHora.length == 3){
                        calendar = new Date(Integer.parseInt(sData[2]), Integer.parseInt(sData[0]), Integer.parseInt(sData[1]), hora, Integer.parseInt(sHora[1]), Integer.parseInt(sHora[2]));
                    }else {
                        calendar = new Date();
                    }
                } else{
                    calendar = new Date();
                }
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
