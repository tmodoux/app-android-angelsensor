package com.angel.sample_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

/**
 * Created by Thieb on 06.04.2016.
 */
public class Credentials {
    private final static String USERNAME = "username";
    private final static String TOKEN = "token";
    private SharedPreferences preferences;

    public Credentials(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Public method that saves the credentials in the shared preferences using encryption
     * @param username: Pryv username
     * @param token: Pryv access token
     */
    public void setCredentials(String username, String token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USERNAME, encrypt(username));
        editor.putString(TOKEN, encrypt(token));
        editor.apply();
    }

    /**
     * Public method allowing to remove stored credentials (at logout for example)
     */
    public void resetCredentials() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(USERNAME);
        editor.remove(TOKEN);
        editor.apply();
    }

    /**
     * Getter for username
     * @return: decrypted Pryv username
     */
    public String getUsername() {
        return decrypt(preferences.getString(USERNAME, null));
    }

    /**
     * Getter for token
     * @return: decrypted Pryv access token
     */
    public String getToken() {
        return decrypt(preferences.getString(TOKEN, null));
    }

    /**
     * Encryption method using Base64
     * @param input: string to encrypt
     * @return: encrypted string or null if input is null
     */
    private static String encrypt(String input) {
        return (input != null) ? Base64.encodeToString(input.getBytes(), Base64.DEFAULT) : null;
    }

    /**
     * Decryption method using Base64
     * @param input: string to decrypt
     * @return: decrypted string or null if input is null
     */
    private static String decrypt(String input) {
        return (input != null) ? new String(Base64.decode(input, Base64.DEFAULT)) : null;
    }
}
