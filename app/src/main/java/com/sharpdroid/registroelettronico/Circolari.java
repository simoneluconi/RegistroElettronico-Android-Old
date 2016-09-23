package com.sharpdroid.registroelettronico;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Azione;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Circolare;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.MyUsers;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static android.support.v4.content.FileProvider.getUriForFile;
import static com.sharpdroid.registroelettronico.MainActivity.msCookieManager;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ConvertiDimensione;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.getPostDataString;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.isNetworkAvailable;

public class Circolari extends AppCompatActivity {

    public static RVAdapter adapter;
    static List<Circolare> Circolaris = new ArrayList<>();
    static SwipeRefreshLayout swipeRefreshLayout;
    static CoordinatorLayout coordinatorLayout;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circolari);
        context = Circolari.this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(getString(R.string.circolari));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayoutCirc);
        adapter = new RVAdapter(Circolaris);
        ObservableRecyclerView rv = (ObservableRecyclerView) findViewById(R.id.CircolariCardList);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(Circolari.this);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshCircolari);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial);
        swipeRefreshLayout.setEnabled(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkAvailable(Circolari.this))
                    new GetStringFromUrl().execute(MainActivity.BASE_URL + "/sif/app/default/bacheca_utente.php");
                else
                    Toast.makeText(getApplicationContext(), R.string.nointernet, Toast.LENGTH_LONG).show();
            }
        });

        if (isNetworkAvailable(Circolari.this)) {
            if (msCookieManager.getCookieStore().getCookies().isEmpty())
                new GetStringFromUrl().execute(MainActivity.BASE_URL + "/auth/app/default/AuthApi2.php?a=aLoginPwd");
            new GetStringFromUrl().execute(MainActivity.BASE_URL + "/sif/app/default/bacheca_utente.php");
        } else
            Toast.makeText(getApplicationContext(), R.string.nointernet, Toast.LENGTH_LONG).show();
    }


    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {
        List<Circolare> Circolaris;

        RVAdapter(List<Circolare> Circolaris) {
            this.Circolaris = Circolaris;
        }

        @Override
        public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_oggiascuola, parent, false);
            return new PersonViewHolder(v);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
            personViewHolder.Titolo.setText(Circolaris.get(i).getTitolo());
            personViewHolder.NCircolare.setText(Circolaris.get(i).getNCircolare());
            personViewHolder.Tipo.setText(String.format("%1$s - %2$s", Circolaris.get(i).getData(), Circolaris.get(i).getTipo()));
            personViewHolder.Data.setText("");

        }

        @Override
        public int getItemCount() {
            return Circolaris.size();
        }

        class PersonViewHolder extends RecyclerView.ViewHolder {
            CardView cv;
            TextView NCircolare;
            TextView Titolo;
            TextView Data;
            TextView Tipo;

            PersonViewHolder(View itemView) {
                super(itemView);
                cv = (CardView) itemView.findViewById(R.id.OggiScuolaCv);
                NCircolare = (TextView) itemView.findViewById(R.id.OggiScuolaOra);
                Titolo = (TextView) itemView.findViewById(R.id.OggiScuolaProf);
                Data = (TextView) itemView.findViewById(R.id.OggiScuolaMateria);
                Tipo = (TextView) itemView.findViewById(R.id.OggiScuolaDes);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = Circolaris.get(getAdapterPosition()).getId();
                        if (!id.equals(""))
                            new GetStringFromUrl().execute(MainActivity.BASE_URL + "/sif/app/default/bacheca_utente.php?action=file_download&com_id=" + id);
                    }
                });
            }

        }

    }

    public class GetStringFromUrl extends AsyncTask<String, Integer, String> {

        private static final int BUFFER_SIZE = 4096;
        Snackbar DownloadProgressSnak;
        String azione = "";

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

            Log.v("Scarico", params[0]);

            URL url;
            HashMap<String, String> postDataParams = new HashMap<>();
            SharedPreferences sharedPref = Circolari.this.getSharedPreferences("Dati", Context.MODE_PRIVATE);

            int ActiveUsers = sharedPref.getInt("CurrentProfile", 0);
            SQLiteDatabase db = new MyUsers(context).getWritableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM " + MyUsers.UserEntry.TABLE_NAME, null);
            c.move(ActiveUsers);
            String username = c.getString(c.getColumnIndex(MyUsers.UserEntry.COLUMN_NAME_USERNAME));
            String password = c.getString(c.getColumnIndex(MyUsers.UserEntry.COLUMN_NAME_PASSWORD));
            c.close();
            db.close();
            if (params[0].contains("auth")) {
                try {
                    url = new URL(params[0]);

                    CookieHandler.setDefault(msCookieManager);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    postDataParams.put("uid", username);
                    postDataParams.put("pwd", password);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostDataString(postDataParams));

                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        //Non ho bisogno dei dati della pagina di login
                        return null;

                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                if (params[0].equals(MainActivity.BASE_URL + "/sif/app/default/bacheca_utente.php"))
                    azione = Azione.CIRCOLARI;
                else azione = Azione.DOWNLOAD;
                try {
                    url = new URL(params[0]);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            switch (azione) {
                case Azione.CIRCOLARI:
                    swipeRefreshLayout.setRefreshing(false);
                    Circolaris.clear();
                    if (result != null) {
                        Elements elements = Jsoup.parse(result).select("tr");
                        for (Element el :
                                elements) {
                            Elements td = el.select("td");
                            if (td.size() > 4) {
                                if (td.get(0).select("div").size() > 0) {
                                    if (td.get(0).select("div").get(0).className().equals("open_sans graytext font_size_12")) {
                                        Circolare circolare = new Circolare();
                                        circolare.setNCircolare(td.get(0).text());
                                        circolare.setTitolo(td.get(2).select("div").get(0).text());
                                        circolare.setTipo(td.get(2).select("div").get(1).text());
                                        circolare.setData(td.get(3).text());
                                        circolare.setId(td.get(4).select("a").attr("comunicazione_id"));
                                        Circolaris.add(circolare);
                                    }
                                }
                            }
                        }

                    }

                    if (Circolaris.isEmpty()) {
                        Circolare circolare = new Circolare();
                        circolare.setNCircolare("0");
                        circolare.setTitolo("Nessuna circolare presente");
                        circolare.setData("");
                        circolare.setTipo("");
                        circolare.setId("");
                        Circolaris.add(circolare);
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
                    } catch (Exception e) {
                        Toast.makeText(context, "Errore:" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    break;


            }

        }

    }
}

