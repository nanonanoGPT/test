package com.naa.utils;


 
import com.naa.data.Nson;
 
 

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

public class InternetX {

    public static String multipartHttp(String url, Map<String, String> arg, String imagename, InputStream file) {
        HashtableMulti<String, String, InputStream> inps = new HashtableMulti<String, String, InputStream>();
        inps.put("image", imagename, file);
        return multipartHttp(url, arg, inps);
    }

    public static String multipartHttp(String url, Map<String, String> arg, HashtableMulti<String, String, InputStream> file) {
        Nson args = new Nson(arg);
        URL object;
        final String LINE_FEED = "\r\n";
        try {
            object = new URL(nikitaYToken(url));//nikitaYToken(stringURL); tidak perlu

            HttpURLConnection con;
            try {
                con = (HttpURLConnection) object.openConnection();
                // creates a unique boundary based on time stamp
                String boundary = "===" + System.currentTimeMillis() + "===";

                con.setDoOutput(true);
                con.setDoInput(true);
                //con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");
                con.setConnectTimeout(30000);

                OutputStream outputStream = con.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(outputStream);

               

                Nson keys = args.getObjectKeys();
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < keys.size(); i++) {

                    builder.append((urlEncode(keys.get(i).asString())+"="));
                    builder.append(urlEncode(args.get(keys.get(i).asString()).asString()));

                    writer.append("--" + boundary).append(LINE_FEED);
                    writer.append("Content-Disposition: form-data; name=\"" + keys.get(i).asString() + "\"")
                            .append(LINE_FEED);
                    writer.append("Content-Type: text/plain; charset=UTF-8" ).append(
                            LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.append(  args.get(keys.get(i).asString()).asString()  ).append(LINE_FEED);
                    writer.flush();
                }

                Nson nson = new Nson(file) ;
                keys =  nson.getObjectKeys();
                for (int i = 0; i < keys.size(); i++) {
                    String fieldName = "image";
                    String fileName = file.get(keys.get(i).asString());
                    writer.append("--" + boundary).append(LINE_FEED);
                    writer.append(
                            "Content-Disposition: form-data; name=\"" + fieldName
                                    + "\"; filename=\"" + fileName + "\"")
                            .append(LINE_FEED);
                    writer.append(
                            "Content-Type: "
                                    + URLConnection.guessContentTypeFromName(fileName))
                            .append(LINE_FEED);
                    writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.flush();

                    InputStream inputStream = file.getData(keys.get(i).asString());
                    byte[] buffer = new byte[4096];
                    int bytesRead = -1;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                    inputStream.close();

                    writer.append(LINE_FEED);
                    writer.flush();
                }

                writer.append(LINE_FEED).flush();
                writer.append("--" + boundary + "--").append(LINE_FEED);
                writer.close();
                //display what returns the POST request

                StringBuilder sb = new StringBuilder();
                int HttpResult = con.getResponseCode();
                sendBroadcastIfUnauthorized401(HttpResult);
                if (HttpResult >= HttpURLConnection.HTTP_OK) {
                    //Utility.sessionExpired(con.getHeaderFields());
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(con.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    return sb.toString();
                } else {
                    System.out.println(con.getResponseMessage());
                    System.out.println(con.getResponseMessage());
                    //System.out.println(con.getResponseMessage());
                    InputStream inputStream = con.getErrorStream();
                    /*String contentEncodingHeader = con.getHeaderField("Content-Encoding");
                    if (contentEncodingHeader != null && contentEncodingHeader.equals("gzip")) {
                        // Read the zipped contents.
                        inputStream = new GZIPInputStream(con.getInputStream());
                    } else {
                        // Read the normal contents.
                        inputStream = con.getInputStream();
                    }*/
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
                }
            } catch (IOException e) {
                //Utility.nikitaErrorConn();
                e.printStackTrace();
                Nson nson = Nson.newObject();
                nson.set("STATUS", "ERROR");
                nson.set("ERROR", e.getMessage());
                return nson.toJson();
            } catch (Exception e) {
                //Utility.nikitaErrorConn();
                e.printStackTrace();
                Nson nson = Nson.newObject();
                nson.set("STATUS", "ERROR");
                nson.set("ERROR", e.getMessage());
                return nson.toJson();
            }

        } catch (MalformedURLException e) {
            //Utility.nikitaErrorConn();
            // TODO Auto-generated catch block
            e.printStackTrace();
            Nson nson = Nson.newObject();
            nson.set("STATUS", "ERROR");
            nson.set("ERROR", e.getMessage());
            return nson.toJson();
        }

    }
    public static String urlEncode(String s){
        try {
            return URLEncoder.encode(s,"UTF-8");
        } catch (Exception e) { }
        return  "";
    }
    public static String postHttpConnection(String stringURL, Map args) {
        return  postHttpConnection(stringURL, new Nson(args));
    }
    public static String postHttpConnection(String stringURL, Nson args) {
        return postHttp(stringURL, null, args, null, null);
    }
    public static String postHttpConnectionRaw(String stringURL, Nson header, Nson rowdata) {
        return postHttp(stringURL, header, null, null, rowdata);
    }
    public static String postHttpConnectionRaw(String stringURL, Nson header, String rowdata) {
        return postHttp(stringURL,  header, null, rowdata, null);
    }
    private static String postHttp(String stringURL, Nson header, Nson args, String sdata, Nson ndata) {
        URL object;
        try {
            object = new URL(stringURL);//nikitaYToken(stringURL); tidak perlu
            HttpURLConnection con;
            try {
                con = (HttpURLConnection) object.openConnection();

                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("User-Agent", "Mozilla/5.0 (Android 4.4; Mobile; rv:41.0; Nikita V3) Gecko/41.0 Firefox/41.0");
                con.setRequestProperty("Accept", "text/plain, */*; q=0.01");
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("Accept", "application/json");

                Nson keys = new Nson(header).getObjectKeys();
                for (int i = 0; i < keys.size(); i++) {
                    con.setRequestProperty(keys.get(i).asString(),  header.get(keys.get(i).asString()).asString());
                }

                con.setRequestMethod("POST");
                con.setConnectTimeout(30000);


                if (ndata!=null){
                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    ndata.toJson(wr);
                    wr.flush();
                    wr.close();
                }else if (sdata!=null){
                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    wr.write(sdata);
                    wr.flush();
                    wr.close();
                }else{
                    keys = args.getObjectKeys();

                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < keys.size(); i++) {
                        String ds = keys.get(i).asString();
                        if (i > 0){
                            builder.append("&");
                        }
                        builder.append(urlEncode(keys.get(i).asString())+"=");
                        builder.append(urlEncode(args.get(keys.get(i).asString()).asString()));
                    }
                    /*builder.append("&");
                    builder.append(("ytoken="));
                    builder.append(Utility.urlEncode(getSetting("TOKEN")));
                    builder.append("&");
                    builder.append(("yuserid="));
                    builder.append(Utility.urlEncode(getSetting("ID_USER")));*/

                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    wr.write(builder.toString());
                    wr.flush();
                    wr.close();
                }


                //display what returns the POST request

                StringBuilder sb = new StringBuilder();
                int HttpResult = con.getResponseCode();
                sendBroadcastIfUnauthorized401(HttpResult);
                if (HttpResult >= HttpURLConnection.HTTP_OK) {
                    //Utility.sessionExpired(con.getHeaderFields());

                    InputStream inputStream = con.getInputStream();
                    /*String contentEncodingHeader = con.getHeaderField("Content-Encoding");
                    if (contentEncodingHeader != null && contentEncodingHeader.equals("gzip")) {
                        // Read the zipped contents.
                        inputStream = new GZIPInputStream(con.getInputStream());
                    } else {
                        // Read the normal contents.
                        inputStream = con.getInputStream();
                    }*/
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();

                    //return Utility.readFile(inputStream);
                } else {
                    System.out.println(con.getResponseMessage());
                    System.out.println(con.getResponseMessage());
                    //System.out.println(con.getResponseMessage());
                    InputStream inputStream = con.getErrorStream();
                    /*String contentEncodingHeader = con.getHeaderField("Content-Encoding");
                    if (contentEncodingHeader != null && contentEncodingHeader.equals("gzip")) {
                        // Read the zipped contents.
                        inputStream = new GZIPInputStream(con.getInputStream());
                    } else {
                        // Read the normal contents.
                        inputStream = con.getInputStream();
                    }*/
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
                //Utility.nikitaErrorConn();
                // TODO Auto-generated catch block
                Nson nson = Nson.newObject();
                nson.set("STATUS", "ERROR");
                nson.set("ERROR", e.getMessage());
                return nson.toJson();
            } catch (Exception e) {
                e.printStackTrace();
                // Utility.nikitaErrorConn();
                Nson nson = Nson.newObject();
                nson.set("STATUS", "ERROR");
                nson.set("ERROR", e.getMessage());
                return nson.toJson();
            }

        } catch (MalformedURLException e) {
            //Utility.nikitaErrorConn();
            // TODO Auto-generated catch block
            Nson nson = Nson.newObject();
            nson.set("STATUS", "ERROR");
            nson.set("ERROR", e.getMessage());
            return nson.toJson();
        }

    }
    public static String postHttpConnectionMultipart(String url, Nson header, Nson args, Nson file) {
        URL object;
        final String LINE_FEED = "\r\n";
        try {
            object = new URL(nikitaYToken(url));//nikitaYToken(stringURL); tidak perlu

            HttpURLConnection con;
            try {
                con = (HttpURLConnection) object.openConnection();
                // creates a unique boundary based on time stamp
                String boundary = "===" + System.currentTimeMillis() + "===";

                con.setDoOutput(true);
                con.setDoInput(true);
                //con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                con.setRequestProperty("Accept", "application/json");

                Nson keys = new Nson(header).getObjectKeys();
                for (int i = 0; i < keys.size(); i++) {
                    con.setRequestProperty(keys.get(i).asString(),  header.get(keys.get(i).asString()).asString());
                }

                con.setRequestMethod("POST");
                con.setConnectTimeout(30000);

                OutputStream outputStream = con.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(outputStream);

              

                keys = args.getObjectKeys();
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < keys.size(); i++) {

                    builder.append((urlEncode(keys.get(i).asString())+"="));
                    builder.append(urlEncode(args.get(keys.get(i).asString()).asString()));

                    writer.append("--" + boundary).append(LINE_FEED);
                    writer.append("Content-Disposition: form-data; name=\"" + keys.get(i).asString() + "\"")
                            .append(LINE_FEED);
                    writer.append("Content-Type: text/plain; charset=UTF-8" ).append(
                            LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.append(  args.get(keys.get(i).asString()).asString()  ).append(LINE_FEED);
                    writer.flush();
                }

                Nson nson = file ;
                keys =  nson.getObjectKeys();
                for (int i = 0; i < keys.size(); i++) {
                    File fileData = new File(file.get(keys.get(i).asString()).asString());
                    String fieldName = keys.get(i).asString();
                    String fileName =  fileData.getName();
                    writer.append("--" + boundary).append(LINE_FEED);
                    writer.append(
                            "Content-Disposition: form-data; name=\"" + fieldName
                                    + "\"; filename=\"" + fileName + "\"")
                            .append(LINE_FEED);
                    writer.append(
                            "Content-Type: "
                                    + URLConnection.guessContentTypeFromName(fileName))
                            .append(LINE_FEED);
                    writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.flush();

                    try {
                        InputStream inputStream = new FileInputStream(fileData);
                        byte[] buffer = new byte[4096];
                        int bytesRead = -1;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        outputStream.flush();
                        inputStream.close();
                    }catch (Exception e){}


                    writer.append(LINE_FEED);
                    writer.flush();
                }

                writer.append(LINE_FEED).flush();
                writer.append("--" + boundary + "--").append(LINE_FEED);
                writer.close();
                //display what returns the POST request

                StringBuilder sb = new StringBuilder();
                int HttpResult = con.getResponseCode();
                sendBroadcastIfUnauthorized401(HttpResult);
                if (HttpResult >= HttpURLConnection.HTTP_OK) {
                    //Utility.sessionExpired(con.getHeaderFields());
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(con.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    return sb.toString();
                } else {
                    System.out.println(con.getResponseMessage());
                    System.out.println(con.getResponseMessage());
                    //System.out.println(con.getResponseMessage());
                    InputStream inputStream = con.getErrorStream();
                    /*String contentEncodingHeader = con.getHeaderField("Content-Encoding");
                    if (contentEncodingHeader != null && contentEncodingHeader.equals("gzip")) {
                        // Read the zipped contents.
                        inputStream = new GZIPInputStream(con.getInputStream());
                    } else {
                        // Read the normal contents.
                        inputStream = con.getInputStream();
                    }*/
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
                }
            } catch (IOException e) {
                //Utility.nikitaErrorConn();
                e.printStackTrace();
                Nson nson = Nson.newObject();
                nson.set("STATUS", "ERROR");
                nson.set("ERROR", e.getMessage());
                return nson.toJson();
            } catch (Exception e) {
                //Utility.nikitaErrorConn();
                e.printStackTrace();
                Nson nson = Nson.newObject();
                nson.set("STATUS", "ERROR");
                nson.set("ERROR", e.getMessage());
                return nson.toJson();
            }

        } catch (MalformedURLException e) {
            //Utility.nikitaErrorConn();
            // TODO Auto-generated catch block
            e.printStackTrace();
            Nson nson = Nson.newObject();
            nson.set("STATUS", "ERROR");
            nson.set("ERROR", e.getMessage());
            return nson.toJson();
        }

    }
    public static String nikitaYToken(String url) {
        StringBuffer result = new StringBuffer();
        StringBuffer addParam = new StringBuffer();
       /* addParam.append(("ytoken="));
        addParam.append(Utility.urlEncode(getSetting("TOKEN")));
        addParam.append(("&yuserid="));
        addParam.append(Utility.urlEncode(getSetting("ID_USER")));*/

        if (url.contains("?")) {
            result.append(url.substring(0,url.indexOf("?")+1)).append(addParam.toString()).append("&").append(url.substring(url.indexOf("?")+1));
        }else if (url.contains("&")) {
            result.append(url.substring(0, url.indexOf("&"))).append("?").append(addParam.toString()).append(url.substring(url.indexOf("&")));
        }else{
            result.append(url).append("?").append(addParam.toString());
        }

        return result.toString();
    }
    
    public static String getHttpConnectionX(String stringURL, Map<String, String> args) {
        Nson nset = new Nson(args) ;
        Nson keys =  nset.getObjectKeys();

        String[] strings = new String[keys.size()];
        for (int i = 0; i < keys.size(); i++) {
            strings[i] = nset.get(keys.get(i).asString()).asString();
        }
        return getHttpConnectionX(stringURL, strings);
    }
    public static String getHttpConnectionX(String stringURL, String...paramvalue) {
        URL object;
        try {
            stringURL = nikitaYToken(stringURL);
            if (paramvalue!=null) {
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < paramvalue.length; i++) {
                    if (paramvalue[i].contains("=")) {
                        int split = paramvalue[i].indexOf("=");
                        String sdata = urlEncode(paramvalue[i].substring(split+1));
                        stringBuffer.append(paramvalue[i].substring(0, split)).append("=").append(sdata).append("&");
                    }
                }
                stringURL =  stringURL+(stringURL.contains("?")?"&":"?")+stringBuffer.toString();
            }
            object = new URL(stringURL);



            HttpURLConnection con;
            try {
                con = (HttpURLConnection) object.openConnection();

                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("GET");
                con.setConnectTimeout(30000);
                //display what returns the POST request

                StringBuilder sb = new StringBuilder();
                int HttpResult = con.getResponseCode();
                sendBroadcastIfUnauthorized401(HttpResult);
                if (HttpResult >= HttpURLConnection.HTTP_OK) {
                    //Utility.sessionExpired(con.getHeaderFields());
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    return sb.toString();
                } else {
                    System.out.println(con.getResponseMessage());
                    System.out.println(con.getResponseMessage());
                    //System.out.println(con.getResponseMessage());
                    InputStream inputStream = con.getErrorStream();
                    /*String contentEncodingHeader = con.getHeaderField("Content-Encoding");
                    if (contentEncodingHeader != null && contentEncodingHeader.equals("gzip")) {
                        // Read the zipped contents.
                        inputStream = new GZIPInputStream(con.getInputStream());
                    } else {
                        // Read the normal contents.
                        inputStream = con.getInputStream();
                    }*/
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
                }
            } catch (IOException e) {
                //Utility.nikitaErrorConn();
                // TODO Auto-generated catch block
                e.printStackTrace();
                Nson nson = Nson.newObject();
                nson.set("STATUS", "ERROR");
                nson.set("ERROR", e.getMessage());
                return nson.toJson();
            } catch (Exception e) {
                //Utility.nikitaErrorConn();
                e.printStackTrace();
                Nson nson = Nson.newObject();
                nson.set("STATUS", "ERROR");
                nson.set("ERROR", e.getMessage());
                return nson.toJson();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            //Utility.nikitaErrorConn();
            Nson nson = Nson.newObject();
            nson.set("STATUS", "ERROR");
            nson.set("ERROR", e.getMessage());
            return nson.toJson();
        }

    }
    public static String getString(String str){
        return str;
    }


    public static void sendBroadcastIfUnauthorized401(int HttpResult){
         
    }
}
