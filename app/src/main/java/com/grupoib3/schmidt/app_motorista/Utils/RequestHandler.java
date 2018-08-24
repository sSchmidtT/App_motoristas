package com.grupoib3.schmidt.app_motorista.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Benidict Dulce on 6/23/2016.
 */
public class RequestHandler {

    //argu url of the script and hash for containing data
    public String sendPostRequest(String requestURL, HashMap<String, String> postDataParams){
        URL url;
        StringBuilder sb = new StringBuilder();//store message retrieved from the server


        try{
            url = new URL(requestURL); //Initialized url

            HttpURLConnection conn  = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream(); //create output stream

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8")); //writing param to request
            writer.write(getPostDataString(postDataParams));
            writer.flush();
            writer.close();

            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                sb = new StringBuilder();
                String response;
                //read server response
                while ((response = br.readLine()) != null){
                    sb.append(response);
                }
            }
        }catch (Exception e){
            e.printStackTrace();

        }
        return sb.toString();
    }

    public String sendgetRequest(String requestUrl, String token){
        StringBuilder sb = new StringBuilder();
        try {
            URL url  = new URL(requestUrl); //Initialized url
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String res;
            while ((res = bufferedReader.readLine()) != null){
                sb.append(res + "\n");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return sb.toString();
    }

    public String sendGetRequestParam(String requestUrl, String id){
        StringBuilder sb = new StringBuilder();
        try{
            URL url = new URL(requestUrl + id);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String res;
            while ((res = br.readLine()) != null){
                sb.append(res + "\n");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry: params.entrySet()){
            if (first)
                first = false;
            else
                sb.append("&");
                sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                sb.append("=");
                sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return sb.toString();
    }

}
