package com.sharpdroid.registroelettronico;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Azione;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.ScrutiniFile;
import com.squareup.picasso.Picasso;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.support.v4.content.FileProvider.getUriForFile;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ConvertiDimensione;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.getPostDataString;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.isNetworkAvailable;

public class Scrutini extends AppCompatActivity {

    static Context context;
    static SwipeRefreshLayout swipeRefreshLayout;
    static CoordinatorLayout coordinatorLayout;
    static CookieManager msCookieManager = new CookieManager();
    static List<ScrutiniFile> scrutiniFiles = new ArrayList<>();
    public static RVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrutini);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(getString(R.string.scrutini));
        setSupportActionBar(toolbar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.CoordinatorScrutini);
        context = this;
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshScrutini);

        adapter = new RVAdapter(scrutiniFiles);
        ObservableRecyclerView rv = (ObservableRecyclerView) findViewById(R.id.ScrutiniCardList);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(Scrutini.this);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial);
        swipeRefreshLayout.setEnabled(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkAvailable(Scrutini.this)) {
                    new GetStringFromUrl().execute("https://web.spaggiari.eu/sol/app/default/documenti_sol.php");
                } else
                    Toast.makeText(getApplicationContext(), R.string.nointernet, Toast.LENGTH_LONG).show();
            }
        });

        if (isNetworkAvailable(Scrutini.this)) {
            new GetStringFromUrl().execute("https://web.spaggiari.eu/home/app/default/login.php");
            new GetStringFromUrl().execute("https://web.spaggiari.eu/sol/app/default/documenti_sol.php");
        } else
            Toast.makeText(getApplicationContext(), R.string.nointernet, Toast.LENGTH_LONG).show();
    }


    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {
        List<ScrutiniFile> scrutiniFiles;

        public class PersonViewHolder extends RecyclerView.ViewHolder {
            CardView cv;
            TextView Nome;
            ImageView imageView;

            PersonViewHolder(View itemView) {
                super(itemView);
                cv = (CardView) itemView.findViewById(R.id.Scrutinicv);
                Nome = (TextView) itemView.findViewById(R.id.FileNameScrut);
                imageView = (ImageView) itemView.findViewById(R.id.imageViewScrutini);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String link = scrutiniFiles.get(getAdapterPosition()).getLink();
                        Log.v("LINK", link);
                        if (link != null)
                            new GetStringFromUrl().execute(link);
                    }
                });
            }

        }

        RVAdapter(List<ScrutiniFile> scrutiniFiles) {
            this.scrutiniFiles = scrutiniFiles;
        }


        @Override
        public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.scrutiniadp, parent, false);
            return new PersonViewHolder(v);
        }


        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }


        @Override
        public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
            personViewHolder.Nome.setText(scrutiniFiles.get(i).getNome());
            String imglink = scrutiniFiles.get(i).getImgLink();
            if (imglink != null) {
                Picasso.with(context).load(imglink).into(personViewHolder.imageView);
            }

        }

        @Override
        public int getItemCount() {
            return scrutiniFiles.size();
        }

    }

    public class GetStringFromUrl extends AsyncTask<String, Integer, String> {

        Snackbar DownloadProgressSnak;
        String azione = "";
        private static final int BUFFER_SIZE = 4096;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (swipeRefreshLayout != null)
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

            if (DownloadProgressSnak == null) {
                DownloadProgressSnak = Snackbar.make(coordinatorLayout, "Download...", Snackbar.LENGTH_INDEFINITE);
                DownloadProgressSnak.show();
            } else {
                View sbView = DownloadProgressSnak.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setText(context.getResources().getString(R.string.scaricati, ConvertiDimensione(progress[0])));
            }
        }


        @Override
        protected String doInBackground(String... params) {


            final String COOKIES_HEADER = "Set-Cookie";
            Log.v("Scarico", params[0]);

            URL url;
            HashMap<String, String> postDataParams = new HashMap<>();
            SharedPreferences sharedPref = Scrutini.this.getSharedPreferences("Dati", Context.MODE_PRIVATE);

            String username = sharedPref.getString("Username", "");
            String url_car;
            if (username.contains("@")) {
                postDataParams.put("mode", "email");
                postDataParams.put("login", username);
                url_car = "https://web.spaggiari.eu/home/app/default/login_email.php";

            } else {
                postDataParams.put("custcode", sharedPref.getString("Custcode", ""));
                postDataParams.put("login", sharedPref.getString("Username", username));
                url_car = "https://web.spaggiari.eu/home/app/default/login.php";
            }
            postDataParams.put("password", sharedPref.getString("Password", ""));

            if (params[0].contains("login")) {
                azione = Azione.LOGIN;
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

                    StringBuilder sb = new StringBuilder();
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                            sb.append("\n");
                        }

                        return sb.toString();

                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                if (params[0].equals("https://web.spaggiari.eu/sol/app/default/documenti_sol.php"))
                    azione = Azione.SCRUTINI;
                else if (params[0].contains(Azione.SCRUTINIFILEWEB)) {
                    params[0] = params[0].replace(Azione.SCRUTINIFILEWEB, "");
                    azione = Azione.SCRUTINIFILEWEB;
                } else azione = Azione.DOWNLOAD;
                try {
                    url = new URL(params[0]);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);


                    if (msCookieManager.getCookieStore().getCookies().size() > 0) {
                        //Riutilizzo gli stessi cookie della sessione precedente
                        conn.setRequestProperty("Cookie", TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
                    }

                    url = new URL(params[0]);
                    conn = (HttpURLConnection) url.openConnection();

                    if (!azione.equals(Azione.DOWNLOAD)) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(
                                conn.getInputStream()));
                        String inputLine;
                        StringBuilder sb = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            //Log.v("Voti:", inputLine);
                            sb.append(inputLine);
                            sb.append("\n");
                        }

                        in.close();
                        return sb.toString();
                    } else {

                        String disposition = conn.getHeaderField("Content-Disposition");
                        String fileName = "";
                        String saveDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + "Registro Elettronico";

                        if (disposition != null) {
                            // Estraggo il nome del file dall'header
                            int index = disposition.indexOf("filename=");
                            if (index > 0) {
                                fileName = disposition.substring(index + 9,
                                        disposition.length());
                            }
                        }


                        InputStream inputStream = conn.getInputStream();
                        fileName = fileName.replaceAll("/", "-");
                        String saveFilePath = saveDir + File.separator + fileName;
                        // opens an output stream to save into file
                        FileOutputStream outputStream = new FileOutputStream(saveFilePath);

                        int bytesRead;
                        int total = 0;
                        byte[] buffer = new byte[BUFFER_SIZE];
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            total += bytesRead;
                            publishProgress(total);
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        outputStream.close();
                        inputStream.close();
                        Log.v("Download", "File scaricato");
                        return saveFilePath;

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }


            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

            switch (azione) {
                case Azione.SCRUTINI:
                    swipeRefreshLayout.setRefreshing(false);
                    scrutiniFiles.clear();

                    if (result != null) {
                        Elements elements = Jsoup.parse(result).select("table#table_documenti");

                        if (elements.size() != 0) {
                            elements = elements.get(0).select("tr");
                            for (int i = 1; i < elements.size(); i++) {
                                ScrutiniFile file = new ScrutiniFile();
                                Elements td = elements.get(i).select("td");
                                file.setNome(td.get(2).text().trim());
                                file.setAzione(elements.get(i).select("img").text().trim());
                                file.setLink(Azione.SCRUTINIFILEWEB + "https://web.spaggiari.eu/sol/app/default/" + elements.get(i).select("span").attr("xhref").trim());
                                String imglink = elements.get(i).select("img").attr("src");
                                imglink = imglink.replace("../../../", "https://web.spaggiari.eu/");
                                file.setImgLink(imglink);
                                scrutiniFiles.add(file);
                            }

                            try {
                                String documenti = result.substring(result.indexOf("var documenti=") + 14);
                                documenti = documenti.substring(0, documenti.indexOf("];") + 2);
                                JSONArray jsonarray = new JSONArray(documenti);
                                for (int i = 0; i < jsonarray.length(); i++) {
                                    ScrutiniFile file = new ScrutiniFile();
                                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                                    file.setNome(jsonobject.getString("desc"));
                                    String link = jsonobject.getString("link");
                                    link = link.substring(link.indexOf("/xdocument.php?"));
                                    file.setLink("https://web.spaggiari.eu/tools/app/default/" + link);
                                    file.setAzione(jsonobject.getString("scarica"));
                                    file.setImgLink("https://web.spaggiari.eu/img/20/" + jsonobject.getString("immagine"));
                                    scrutiniFiles.add(file);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                ACRA.getErrorReporter().handleException(e, false);
                            }
                        }
                    }

                    if (scrutiniFiles.isEmpty()) {
                        ScrutiniFile file = new ScrutiniFile();
                        file.setNome("Nessun file presente");
                        file.setLink(null);
                        scrutiniFiles.add(file);
                    }
                    adapter.notifyDataSetChanged();
                    break;

                case Azione.DOWNLOAD:

                    MainActivity.AggiornaFileOffline();
                    if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                    if (DownloadProgressSnak != null) DownloadProgressSnak.dismiss();
                    Intent myIntent = new Intent(Intent.ACTION_VIEW);
                    String mime;
                    File myFile = null;
                    try {
                        myFile = new File(result);
                        mime = URLConnection.guessContentTypeFromStream(new FileInputStream(myFile));
                        if (mime == null)
                            mime = URLConnection.guessContentTypeFromName(myFile.getName());
                        Uri uri = getUriForFile(context, MainActivity.FILE_PROVIDER_STRING, myFile);
                        myIntent.setDataAndType(uri, mime);
                        myIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(myIntent);
                    } catch (android.content.ActivityNotFoundException e) {
                        if (myFile != null) {
                            if (coordinatorLayout != null)
                                Snackbar.make(coordinatorLayout, "Nessuna app per aprire il file: " + myFile.getName(), Snackbar.LENGTH_LONG).show();

                        } else {
                            if (coordinatorLayout != null) {
                                Snackbar.make(coordinatorLayout, "Nessuna app per aprire il file", Snackbar.LENGTH_INDEFINITE).show();
                            }
                        }
                        e.printStackTrace();
                    } catch (java.lang.NullPointerException e) {
                        Snackbar.make(coordinatorLayout, "File non disponibile", Snackbar.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, "Errore:" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    break;


                case Azione.SCRUTINIFILEWEB:

                    if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                    if (DownloadProgressSnak != null) DownloadProgressSnak.dismiss();
                    if (result != null) {
                        WebView webView = new WebView(Scrutini.this);
                        String data;
                        Elements MainContainer = Jsoup.parse(result).select("div#main-container");
                        if (MainContainer.size() != 0)
                            data = MainContainer.toString();
                        else data = result;
                        webView.loadData(data, "text/html", "UTF-8");
                        new MaterialDialog.Builder(Scrutini.this)
                                .theme(Theme.LIGHT)
                                .customView(webView, false)
                                .positiveText(android.R.string.ok)
                                .show();
                    }

                    break;


            }

        }

    }
}
