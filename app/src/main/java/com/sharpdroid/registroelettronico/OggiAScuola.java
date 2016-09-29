package com.sharpdroid.registroelettronico;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Azione;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Firma;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.MyUsers;
import com.sharpdroid.registroelettronico.SharpLibrary.Ranger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static com.sharpdroid.registroelettronico.MainActivity.msCookieManager;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ProfDecente;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.getPostDataString;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.isNetworkAvailable;

public class OggiAScuola extends AppCompatActivity {

    private static RVAdapter adapter;
    private static int SelectedDay;
    private final List<Firma> oggiScuola = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oggi_ascuola);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(getString(R.string.oggiscuola));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        context = this;
        adapter = new RVAdapter(oggiScuola);
        RecyclerView rv = (RecyclerView) findViewById(R.id.OggiScuolCardList);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(OggiAScuola.this);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshOggiScuola);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial);
        swipeRefreshLayout.setEnabled(true);
        Ranger ranger = (Ranger) findViewById(R.id.HorizzontalCalendar);

        ranger.setDayViewOnClickListener(new Ranger.DayViewOnClickListener() {
            @Override
            public void onDaySelected(int day) {
                SelectedDay = day;
                TextView statoUtente = (TextView) findViewById(R.id.Statoutente);
                Calendar cl = Calendar.getInstance();
                String data = cl.get(Calendar.YEAR) + "-" + (cl.get(Calendar.MONTH) + 1) + "-" + day;
                if (isNetworkAvailable(OggiAScuola.this)) {
                    new GetStringFromUrl().execute(MainActivity.BASE_URL + "/cvv/app/default/regclasse.php?cerca=:cerca:&data_start=" + data);
                } else
                    Toast.makeText(getApplicationContext(), R.string.nointernet, Toast.LENGTH_LONG).show();

                int gg = cl.get(Calendar.DAY_OF_MONTH);
                if (gg > day)
                    statoUtente.setText(getString(R.string.ero));
                else if (gg == day)
                    statoUtente.setText(getString(R.string.oggi));
                else statoUtente.setText(getString(R.string.saro));
            }
        });
        if (isNetworkAvailable(OggiAScuola.this)) {
            if (msCookieManager.getCookieStore().getCookies().isEmpty())
                new GetStringFromUrl().execute(MainActivity.BASE_URL + "/auth/app/default/AuthApi2.php?a=aLoginPwd");
            new GetStringFromUrl().execute(MainActivity.BASE_URL + "/cvv/app/default/regclasse.php");
        } else
            Toast.makeText(getApplicationContext(), R.string.nointernet, Toast.LENGTH_LONG).show();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkAvailable(OggiAScuola.this)) {
                    Calendar cl = Calendar.getInstance();
                    String data = cl.get(Calendar.YEAR) + "-" + (cl.get(Calendar.MONTH) + 1) + "-" + SelectedDay;
                    new GetStringFromUrl().execute(MainActivity.BASE_URL + "/cvv/app/default/regclasse.php?cerca=:cerca:&data_start=" + data);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), R.string.nointernet, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {
        final List<Firma> oggiScuolas;

        RVAdapter(List<Firma> oggiScuolas) {
            this.oggiScuolas = oggiScuolas;
        }

        @Override
        public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_oggiascuola, parent, false);
            return new PersonViewHolder(v);
        }

        @Override
        public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
            personViewHolder.Prof.setText(oggiScuolas.get(i).getProf());


            if (oggiScuolas.get(i).getMateria().trim().equals(""))
                personViewHolder.Materia.setText("");
            else
                personViewHolder.Materia.setText(String.format("(%1$s)", oggiScuolas.get(i).getMateria()));

            personViewHolder.Ora.setText(oggiScuolas.get(i).getOra());

            String descrizione = oggiScuolas.get(i).getAttivita() + " " + oggiScuolas.get(i).getDescrizione();
            Spannable spannable = new SpannableString(descrizione);
            spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, oggiScuolas.get(i).getAttivita().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, oggiScuolas.get(i).getAttivita().length(), 0);
            personViewHolder.Des.setText(spannable, TextView.BufferType.SPANNABLE);

            if (oggiScuolas.get(i).getDescrizione().equals("") && oggiScuolas.get(i).getAttivita().length() > 1)
                personViewHolder.Des.setText(personViewHolder.Des.getText().subSequence(0, personViewHolder.Des.getText().length() - 2));


        }

        @Override
        public int getItemCount() {
            return oggiScuolas.size();
        }

        class PersonViewHolder extends RecyclerView.ViewHolder {
            final CardView cv;
            final TextView Ora;
            final TextView Prof;
            final TextView Materia;
            final TextView Des;

            PersonViewHolder(View itemView) {
                super(itemView);
                cv = (CardView) itemView.findViewById(R.id.OggiScuolaCv);
                Ora = (TextView) itemView.findViewById(R.id.OggiScuolaOra);
                Prof = (TextView) itemView.findViewById(R.id.OggiScuolaProf);
                Materia = (TextView) itemView.findViewById(R.id.OggiScuolaMateria);
                Des = (TextView) itemView.findViewById(R.id.OggiScuolaDes);

            }
        }

    }

    public class GetStringFromUrl extends AsyncTask<String, Void, String> {
        String azione = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }

        @Override
        protected String doInBackground(String... params) {
            Log.v("Scarico", params[0]);

            URL url;
            HashMap<String, String> postDataParams = new HashMap<>();
            SharedPreferences sharedPref = OggiAScuola.this.getSharedPreferences("Dati", Context.MODE_PRIVATE);

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

                azione = Azione.OGGI_SCUOLA;
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

            TextView TxStato = (TextView) findViewById(R.id.StatoutenteEff);
            TextView statoUtente = (TextView) findViewById(R.id.Statoutente);
            if (statoUtente.getText().equals(getString(R.string.caricamento_dati_in_corso)))
                statoUtente.setText(getString(R.string.oggi));
            if (azione.equals(Azione.OGGI_SCUOLA)) {
                swipeRefreshLayout.setRefreshing(false);
                if (result != null && result.length() > 0) {
                    oggiScuola.clear();
                    Elements elements = Jsoup.parse(result).select("tr");
                    for (Element el :
                            elements) {
                        Elements td = el.select("td");
                        if (td.size() > 4) {
                            if (td.get(1).select("div").size() > 0) {
                                if (td.get(1).className().equals("registro_firma_dett_docente")) {
                                    Firma oggiScuolas = new Firma();
                                    String prof = td.get(1).select("div").get(0).text();
                                    if (prof.length() > 0) {
                                        oggiScuolas.setProf(ProfDecente(prof));
                                        oggiScuolas.setOra(td.get(2).text().split("\\s+")[0]);
                                        oggiScuolas.setMateria(td.get(3).select("span").get(0).text().replaceAll("\u00A0", "")); // Rimuove il codice &nbsp; ("\u00A0")
                                        oggiScuolas.setAttivita(td.get(4).select("span").get(0).text());
                                        oggiScuolas.setDescrizione(td.get(4).select("span").get(1).text());
                                        oggiScuola.add(oggiScuolas);
                                    }
                                }
                            }
                        }

                        if (td.size() > 5) {
                            if (td.get(5).className().contains("statoassenza_g")) {
                                String stato = td.select("p").get(1).text().trim();
                                stato = stato.substring(0, 1).toUpperCase() + stato.substring(1).toLowerCase();
                                TxStato.setText(stato);
                                if (stato.toLowerCase().contains("presente"))
                                    TxStato.setTextColor(ContextCompat.getColor(OggiAScuola.this, R.color.greenmaterial));
                                else if (stato.toLowerCase().contains("assente"))
                                    TxStato.setTextColor(ContextCompat.getColor(OggiAScuola.this, R.color.redmaterial));
                                else if (stato.toLowerCase().contains("ritardo"))
                                    TxStato.setTextColor(ContextCompat.getColor(OggiAScuola.this, R.color.orangematerial));
                                else
                                    TxStato.setTextColor(ContextCompat.getColor(OggiAScuola.this, R.color.bluematerial));
                            }
                        }
                    }

                    if (oggiScuola.isEmpty()) {
                        Firma oggiScuolas = new Firma();
                        oggiScuolas.setDescrizione("");
                        oggiScuolas.setOra("");
                        oggiScuolas.setMateria("");
                        oggiScuolas.setAttivita("");
                        oggiScuolas.setProf("Nessuna firma presente");
                        oggiScuola.add(oggiScuolas);
                    }

                    adapter.notifyDataSetChanged();
                }
            }

        }

    }
}
