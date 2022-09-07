package br.edu.ifpi.ads.readingapp.filter;

/*
* Deprecated
*
* */
public class AccessTokenSingleton {
    private static String accessToken = "";

    private AccessTokenSingleton(){}

    public static String getAccessToken() {
        return accessToken;
    }
    public static void setAccessToken(String accessToken) {
        AccessTokenSingleton.accessToken = accessToken;
    }

    public static boolean invalidateToken(){
        accessToken = "";
        return accessToken.isEmpty();
    }

}
