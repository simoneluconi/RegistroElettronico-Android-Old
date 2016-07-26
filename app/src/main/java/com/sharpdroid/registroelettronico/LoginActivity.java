package com.sharpdroid.registroelettronico;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import org.acra.ACRA;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.Manifest.permission.GET_ACCOUNTS;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.CancellaPagineLocali;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ProfDecente;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.getPostDataString;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.isNetworkAvailable;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mCodiceScuola;
    private View mProgressView;
    private View mLoginFormView;
    String Nome = "";
    String ErrMsg = "Errore sconosciuto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mCodiceScuola = (EditText) findViewById(R.id.codicescuola);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String codicescuola = extras.getString("com.sharpdroid.registroelettronico.codicescuola", null);
                if (codicescuola != null)
                    mCodiceScuola.setText(codicescuola);
            }
        }

        mEmailView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.toString().contains("@"))
                    mCodiceScuola.setVisibility(View.GONE);
                else mCodiceScuola.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable(LoginActivity.this))
                    attemptLogin();
                else
                    Toast.makeText(getApplicationContext(), R.string.nointernet, Toast.LENGTH_LONG).show();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        TextView AccFail = (TextView) findViewById(R.id.FailAccesso);
        AccFail.setPaintFlags(AccFail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        AccFail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(LoginActivity.this)
                        .title(R.string.nonriesciaccedere)
                        .content(getString(R.string.trovacodicescuola))
                        .theme(Theme.LIGHT)
                        .positiveText(android.R.string.ok)
                        .neutralText("Cerca Scuola")
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Uri uri = Uri.parse("http://sharpdroid.altervista.org/registroelettronico/scuole/");
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);

                            }
                        }).show();
            }
        });

        populateAutoComplete();

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();

        List<String> accounts = new ArrayList<>();
        for (Account a :
                list) {
            if (!accounts.contains(a.name) && a.name.contains("@"))
                accounts.add(a.name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, accounts);
        mEmailView.setAdapter(adapter);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(GET_ACCOUNTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{GET_ACCOUNTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{GET_ACCOUNTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String codicescuola = mCodiceScuola.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password, codicescuola);
            mAuthTask.execute((Void) null);
        }
    }

    /*private boolean isEmailValid(String email) {
        return email.length() > 1;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 1;
    }*/

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mCodiceScuola;

        UserLoginTask(String email, String password, String codicescuola) {
            mEmail = email;
            mPassword = password;
            mCodiceScuola = codicescuola;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            final String COOKIES_HEADER = "Set-Cookie";
            URL url;
            String response = "";
            CookieManager msCookieManager = new CookieManager();
            HashMap<String, String> postDataParams = new HashMap<>();
            SharedPreferences sharedPref = getSharedPreferences("Dati", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            String url_car;
            if (mEmail.contains("@")) {
                postDataParams.put("mode", "email");
                postDataParams.put("login", mEmail.trim());
                editor.putString("Username", mEmail.trim());
                editor.apply();
                url_car = "https://web.spaggiari.eu/home/app/default/login_email.php";

            } else {
                postDataParams.put("custcode", mCodiceScuola.toUpperCase().trim());
                postDataParams.put("login", mEmail.trim());
                editor.putString("Custcode", mCodiceScuola.toUpperCase().trim());
                editor.putString("Username", mEmail.trim());
                editor.apply();
                url_car = "https://web.spaggiari.eu/home/app/default/login.php";
            }
            postDataParams.put("password", mPassword.trim());

            editor.putString("Password", mPassword.trim());
            editor.apply();

            try {
                url = new URL(url_car);

                CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();

                Map<String, List<String>> headerFields = conn.getHeaderFields();
                List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                    }
                }


                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    Elements element = Jsoup.parse(sb.toString()).select(".error-msg.double");
                    ErrMsg = element.text();
                    int size = element.size();
                    if (size > 0)
                        return false;

                    if (msCookieManager.getCookieStore().getCookies().size() > 0) {
                        //Riutilizzo gli stessi cookie della sessione precedente
                        conn.setRequestProperty("Cookie", TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
                    }


                    url = new URL("https://web.spaggiari.eu/sso/app/default/me.php");
                    conn = (HttpURLConnection) url.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            conn.getInputStream()));
                    String inputLine;
                    sb = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        sb.append(inputLine);
                    }

                    in.close();
                    response = sb.toString();

                    Document data = Jsoup.parse(sb.toString());
                    element = data.select("p.double").select("span");
                    for (Element el : element)
                        if (el.text().contains("Studente")) {
                            String tmp = el.text().replace("Studente", "").trim();
                            Nome = ProfDecente(tmp);
                            break;
                        }
                    String CodiceScuola = data.select("span.redtext").get(0).text().split("\\.")[0];
                    url = new URL("http://sharpdroid.altervista.org/registroelettronico/scuole/AggiungiScuola.php?codice=" + CodiceScuola);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.getInputStream();
                    CancellaPagineLocali(LoginActivity.this);
                    List<HttpCookie> cookies = msCookieManager.getCookieStore().getCookies();
                    editor = sharedPref.edit();
                    editor.putBoolean("Acceduto", true);
                    int nAccount = sharedPref.getInt("nAccount", -1) + 1;
                    editor.putInt("nAccount", nAccount);
                    editor.putInt("CurrentProfile", nAccount);
                    editor.apply();
                    for (int i = 0; i < cookies.size(); i++) {
                        editor.putString("Cookie", cookies.get(i).toString());
                        editor.apply();

                    }
                    return true;
                } else {
                    response = "";
                    return false;

                }
            } catch (Exception e) {
                e.printStackTrace();
                ACRA.getErrorReporter().handleException(e, false);
                ErrMsg = e.getLocalizedMessage();
            }

            Log.v("Scaricato:", response);
            return false;

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {

                File myfile = new File(getApplicationContext().getFilesDir() + "/Accounts");
                try {
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(myfile, true));
                    bufferedWriter.write(mEmail + "%s" + mPassword + "%s" + Nome + "%s" + mCodiceScuola + "\n");
                    bufferedWriter.close();
                } catch (IOException e) {
                    Log.e("SaveFile", "File Write error: " + e.toString());
                }

                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            } else {
                if (ErrMsg.toLowerCase().contains("password")) {
                    mPasswordView.setError(ErrMsg);
                    mPasswordView.requestFocus();
                } else {
                    mEmailView.setError(ErrMsg);
                    mEmailView.requestFocus();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

