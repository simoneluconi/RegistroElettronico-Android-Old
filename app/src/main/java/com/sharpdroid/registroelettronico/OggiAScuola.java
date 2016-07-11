package com.sharpdroid.registroelettronico;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Azione;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Firma;
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
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ProfDecente;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.getPostDataString;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.isNetworkAvailable;

public class OggiAScuola extends AppCompatActivity {

    CookieManager msCookieManager = new CookieManager();
    List<Firma> oggiScuola = new ArrayList<>();
    public static RVAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    static int SelectedDay;
    static CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oggi_ascuola);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(getString(R.string.oggiscuola));
        setSupportActionBar(toolbar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayoutOggiScuola);
        adapter = new RVAdapter(oggiScuola);
        ObservableRecyclerView rv = (ObservableRecyclerView) findViewById(R.id.OggiScuolCardList);
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
                    new GetStringFromUrl().execute("https://web.spaggiari.eu/cvv/app/default/regclasse.php?cerca=:cerca:&data_start=" + data);
                } else
                    Toast.makeText(getApplicationContext(), R.string.nointernet, Toast.LENGTH_LONG).show();

                int gg = cl.get(Calendar.DAY_OF_MONTH);
                if (gg > day)
                    statoUtente.setText("Ero:");
                else if (gg == day)
                    statoUtente.setText("Oggi:");
                else statoUtente.setText("Sarò?");
            }
        });
        if (isNetworkAvailable(OggiAScuola.this)) {
            msCookieManager = MainActivity.msCookieManager;

            if (msCookieManager == null)
                new GetStringFromUrl().execute("https://web.spaggiari.eu/home/app/default/login.php");
            new GetStringFromUrl().execute("https://web.spaggiari.eu/cvv/app/default/regclasse.php");
        } else
            Toast.makeText(getApplicationContext(), R.string.nointernet, Toast.LENGTH_LONG).show();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkAvailable(OggiAScuola.this)) {
                    Calendar cl = Calendar.getInstance();
                    String data = cl.get(Calendar.YEAR) + "-" + (cl.get(Calendar.MONTH) + 1) + "-" + SelectedDay;
                    new GetStringFromUrl().execute("https://web.spaggiari.eu/cvv/app/default/regclasse.php?cerca=:cerca:&data_start=" + data);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), R.string.nointernet, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {
        List<Firma> oggiScuolas;

        public class PersonViewHolder extends RecyclerView.ViewHolder {
            CardView cv;
            TextView Ora;
            TextView Prof;
            TextView Materia;
            TextView Des;

            PersonViewHolder(View itemView) {
                super(itemView);
                cv = (CardView) itemView.findViewById(R.id.OggiScuolaCv);
                Ora = (TextView) itemView.findViewById(R.id.OggiScuolaOra);
                Prof = (TextView) itemView.findViewById(R.id.OggiScuolaProf);
                Materia = (TextView) itemView.findViewById(R.id.OggiScuolaMateria);
                Des = (TextView) itemView.findViewById(R.id.OggiScuolaDes);

            }
        }

        RVAdapter(List<Firma> oggiScuolas) {
            this.oggiScuolas = oggiScuolas;
        }


        @Override
        public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oggiscuola_card, parent, false);
            return new PersonViewHolder(v);
        }


        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
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


            final String COOKIES_HEADER = "Set-Cookie";
            Log.v("Scarico", params[0]);

            URL url;
            HashMap<String, String> postDataParams = new HashMap<String, String>();
            SharedPreferences sharedPref = OggiAScuola.this.getSharedPreferences("Dati", Context.MODE_PRIVATE);

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

                azione = Azione.OGGI_SCUOLA;
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
            if (statoUtente.getText().equals("Caricamento dati in corso..."))
                statoUtente.setText("Oggi:");
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
                        oggiScuolas.setOra("0");
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
