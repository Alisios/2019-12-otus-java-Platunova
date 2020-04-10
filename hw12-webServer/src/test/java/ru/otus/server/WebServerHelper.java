package ru.otus.server;

import org.eclipse.jetty.http.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.List;
import java.util.Optional;




public final class WebServerHelper {

    private static final String COOKIE_NAME_JSESSIONID = "JSESSIONID";
    public static final String COOKIE_HEADER = "Cookie";


    private WebServerHelper() {
    }

    public static HttpCookie login(String url, String login, String password) throws IOException {
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        HttpURLConnection connection = sendRequest(url, HttpMethod.POST);
        try {
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(String.format("login=%s&password=%s", login, password).getBytes());
                os.flush();
            }
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
                return cookies.stream().filter(c -> c.getName().equalsIgnoreCase(COOKIE_NAME_JSESSIONID)).findFirst().orElse(null);
            }
        } finally {
            connection.disconnect();
        }
        return null;
    }

     static HttpURLConnection sendRequest(String url, HttpMethod method) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod(method.asString());
        return connection;
    }

     static String readResponseFromConnection(HttpURLConnection connection) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        return response.toString();
    }

     static String buildUrl(String host, String path, List<String> pathParams) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(host);
        stringBuilder.append(path);
        Optional.ofNullable(pathParams).ifPresent(paramsMap -> paramsMap.forEach(p -> stringBuilder.append("/").append(p)));
        return stringBuilder.toString();
    }

}
