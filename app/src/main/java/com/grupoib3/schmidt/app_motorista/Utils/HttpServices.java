package com.grupoib3.schmidt.app_motorista.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpServices {

    //private static String BaseUrl = "https://mpark-m4-ws.postomirian.com.br/AppMotoristas/Api/";
    //private static String BaseUrl = "https://192.168.1.19/AppMotoristas/Api/";

    //Responsavel por carregar o Objeto JSON
    public static String getJSONFromAPI(String url, String json, String methodo, String accessToken){

        //Crie um gerenciador de confiança que não valide as cadeias de certificados
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        } };
        String retorno = "";
        try {
            //Instala o gerenciador de confiança totalmente confiável
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            //Cria um verificador de nome de host confiável
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Instala o verificador de host totalmente confiável
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            URL apiEnd = new URL(url);
            int codigoResposta;
            HttpURLConnection conexao;
            InputStream is;

            conexao = (HttpURLConnection) apiEnd.openConnection();
            conexao.setRequestMethod(methodo);
            if(!accessToken.equals("")){
                String bearer = "Bearer " + accessToken;
                conexao.setRequestProperty("Authorization", bearer);
                conexao.setRequestProperty("Accept", "*");
            }if(methodo.equals("POST")){
                conexao.setRequestProperty("Content-Type", "application/json");
                conexao.setRequestProperty("Accept", "application/json");
                conexao.setDoOutput(true);
                conexao.setDoInput(true);
            }
            conexao.setReadTimeout(15000);
            conexao.setConnectTimeout(15000);
            conexao.connect();
            if(methodo.equals("POST")){
                conexao.getOutputStream().write(json.getBytes());
            }

            BufferedReader in;
            String inputLine;
            StringBuffer response = new StringBuffer();



            codigoResposta = conexao.getResponseCode();
            if(codigoResposta < HttpURLConnection.HTTP_BAD_REQUEST){
                //is = conexao.getInputStream();
                in = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
            }else if(codigoResposta == HttpURLConnection.HTTP_UNAUTHORIZED){
                return "HTTP_UNAUTHORIZED";
            }else{
               //is = conexao.getErrorStream();
                in = new BufferedReader(new InputStreamReader(conexao.getErrorStream()));

            }

            //retorno = converterInputStreamToString(is);
            //is.close();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            conexao.disconnect();
            retorno = response.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            //return e.getMessage();
        }catch (IOException e){
            e.printStackTrace();
            //return e.getMessage();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            //return e.getMessage();
        } catch (KeyManagementException e) {
            e.printStackTrace();
            //return e.getMessage();
        }

        return retorno;
    }

    private static String converterInputStreamToString(InputStream is){
        StringBuffer buffer = new StringBuffer();
        try{
            BufferedReader br;
            String linha;

            br = new BufferedReader(new InputStreamReader(is));
            while((linha = br.readLine())!=null){
                buffer.append(linha);
            }

            br.close();
        }catch(IOException e){
            e.printStackTrace();
        }

        return buffer.toString();
    }
}
