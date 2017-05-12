package com.sharpdroid.registroelettronico;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.MyUsers;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Utente;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    CookieManager msCookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
    private Context context;
    private String Nome = "";
    private String ErrMsg = "Errore sconosciuto";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        // Set up the login form.
        context = this;
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        mEmailSignInButton.setOnClickListener(view -> {
            if (isNetworkAvailable(LoginActivity.this))
                attemptLogin();
            else
                Toast.makeText(getApplicationContext(), R.string.nointernet, Toast.LENGTH_LONG).show();
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        TextView txCommento = (TextView) findViewById(R.id.TextViewCommento);
        txCommento.setMovementMethod(LinkMovementMethod.getInstance());
        populateAutoComplete();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String username = extras.getString("username");
            String password = extras.getString("password");

            mEmailView.setText(username);
            mPasswordView.setText(password);

            attemptLogin();
        }

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }


        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        List<String> accounts = new ArrayList<>();

        try {
            Account[] list = manager.getAccounts();

            for (Account account : list) {
                if (!accounts.contains(account.name) && account.name.contains("@"))
                    accounts.add(account.name);
            }
        } catch (SecurityException e) {
            // nothing, permission must be enabled
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
                    .setAction(android.R.string.ok, v -> requestPermissions(new String[]{GET_ACCOUNTS}, REQUEST_READ_CONTACTS));
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
            mAuthTask = new UserLoginTask(email, password);
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
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            URL url;
            String response = "";
            HashMap<String, String> postDataParams = new HashMap<>();
            String url_car = MainActivity.BASE_URL + MainActivity.LOGIN_URL;

            postDataParams.put("uid", mEmail.trim());
            postDataParams.put("pwd", mPassword.trim());

            try {
                url = new URL(url_car);

                CookieHandler.setDefault(msCookieManager);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
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


                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    JSONObject jo = new JSONObject(sb.toString()).getJSONObject("data");

                    JSONObject authdata = jo.getJSONObject("auth");

                    JSONArray errors = authdata.getJSONArray("errors");
                    if (errors.length() > 0) {
                        ErrMsg = errors.getString(0);
                        return false;
                    }

                    if (authdata.getBoolean("verified") && authdata.getBoolean("loggedIn")) {

                        JSONObject info = authdata.getJSONObject("accountInfo");

                        if (info.length() == 0 && errors.length() == 0)
                            System.out.print("Più account associati");

                        Nome = ProfDecente(info.getString("cognome") + " " + info.getString("nome"));
                        final String tipo = info.getString("type");
                        LoginActivity.this.runOnUiThread(() -> {
                            if (tipo.equals("G"))
                                Toast.makeText(LoginActivity.this, "Salve genitore! Mi raccomando, non stressare troppo tuo/a figlio/a", Toast.LENGTH_LONG).show();
                        });

                        CancellaPagineLocali(LoginActivity.this);
                        SharedPreferences sharedPref = getSharedPreferences("Dati", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        SQLiteDatabase db = new MyUsers(context).getWritableDatabase();
                        Cursor c = db.rawQuery("SELECT * FROM " + MyUsers.UserEntry.TABLE_NAME, null);
                        int count = c.getCount();
                        c.close();
                        db.close();
                        editor.putInt("CurrentProfile", count + 1);
                        editor.apply();

                        return true;
                    } else {
                        JSONArray fullList = jo.getJSONObject("pfolio").getJSONArray("fullList");

                        List<Utente> utenti = new Gson().fromJson(fullList.toString(), new TypeToken<List<Utente>>() {
                        }.getType());

                        String[] ut = new String[utenti.size()];

                        for (int i = 0; i < utenti.size(); i++)
                            ut[i] = utenti.get(i).getNome();

                        LoginActivity.this.runOnUiThread(() -> {
                            new MaterialDialog.Builder(LoginActivity.this)
                                    .title(R.string.selezionaaccount)
                                    .items(ut)
                                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                        @Override
                                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                            Utente u = utenti.get(which);

                                            Intent i = new Intent(LoginActivity.this, LoginActivity.class);
                                            i.putExtra("username", u.getAccount_string());
                                            i.putExtra("password", mPassword);

                                            startActivity(i);

                                            finish();

                                            return true;
                                        }
                                    })
                                    .positiveText(R.string.scegli)
                                    .theme(Theme.LIGHT)
                                    .titleColor(ContextCompat.getColor(context, R.color.md_black_1000))
                                    .contentColor(ContextCompat.getColor(context, R.color.md_black_1000))
                                    .positiveColor(ContextCompat.getColor(context, R.color.accent))
                                    .show();
                        });

                        ErrMsg = getString(R.string.selezionaaccount);
                        return false;
                    }
                } else {
                    response = "";
                    return false;

                }
            } catch (Exception e) {
                e.printStackTrace();
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

                SQLiteDatabase db = new MyUsers(context).getWritableDatabase();
                ContentValues dati = new ContentValues();
                dati.put(MyUsers.UserEntry.COLUMN_NAME_NAME, Nome);
                dati.put(MyUsers.UserEntry.COLUMN_NAME_USERNAME, mEmail);
                dati.put(MyUsers.UserEntry.COLUMN_NAME_PASSWORD, mPassword);
                db.insert(MyUsers.UserEntry.TABLE_NAME, MyUsers.UserEntry.COLUMN_NAME_NULLABLE, dati);
                db.close();

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

