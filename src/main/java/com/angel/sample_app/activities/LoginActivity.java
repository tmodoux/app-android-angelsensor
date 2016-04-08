package com.angel.sample_app.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.angel.sample_app.Credentials;
import com.angel.sample_app.R;
import com.pryv.Pryv;
import com.pryv.api.model.Permission;
import com.pryv.auth.AuthController;
import com.pryv.auth.AuthView;

import java.util.ArrayList;

/**
 * Launcher class that allows to connect as Pryv user or to create a Pryv account
 * This is here that credentials storage, domain choice, app id and permissions are managed
 */
public class LoginActivity extends Activity {

    private String webViewUrl;
    private WebView webView;

    private Permission creatorPermission = new Permission("*", Permission.Level.manage, "Creator");
    private ArrayList<Permission> permissions;

    private String errorMessage = "Unknown error";
    public final static String DOMAIN = "pryv-switch.ch";
    public final static String APPID = "app-android-iHealth";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        new Credentials(this).resetCredentials();

        webView = (WebView) findViewById(R.id.webview);
        Pryv.setDomain(DOMAIN);
        permissions = new ArrayList<Permission>();
        permissions.add(creatorPermission);
        new SigninAsync().execute();
    }

    /**
     * AsyncTask that requests the login page from Pryv
     * using AuthController and shows it in a WebView
     */
    private class SigninAsync extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            progressDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            AuthController authenticator = new AuthController(APPID, permissions, null, null, new CustomAuthView());
            authenticator.signIn();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
            if (webViewUrl != null) {
                webView.requestFocus(View.FOCUS_DOWN);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.loadUrl(webViewUrl);
            } else {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Authentification error: ")
                        .setMessage(errorMessage)
                        .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                startActivity(getIntent());
                            }
                        })
                        .show();
            }
        }

    }

    /**
     * Custom AuthView that configures some callbacks
     */
    private class CustomAuthView implements AuthView {

        @Override
        // Set up the WebView url when we get it from AuthController
        public void displayLoginView(String loginURL) {
            webViewUrl = loginURL;
        }

        @Override
        // Save the credentials if authentication succeeds
        public void onAuthSuccess(String username, String token) {
            new Credentials(LoginActivity.this).setCredentials(username,token);
            Intent intent = new Intent(LoginActivity.this, ScanActivity.class);
            startActivity(intent);
        }

        @Override
        // Set up error messages if authentication fails
        public void onAuthError(String msg) {
            errorMessage = msg;
        }

        @Override
        // Set up error messages if authentication is refused
        public void onAuthRefused(int reasonId, String msg, String detail) {
            errorMessage = msg;
        }
    }

}
