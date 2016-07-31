package com.sharpdroid.registroelettronico;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Azione;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Lezione;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.LezioneM;
import com.sharpdroid.registroelettronico.Tabs.SlidingTabLayout;
import com.sharpdroid.registroelettronico.Tabs.SwipeViewPager;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.MateriaDecente;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ProfDecente;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.getPostDataString;

public class Lezioni extends AppCompatActivity {


    static RVAdapter adapter;
    static SwipeViewPager mPager;
    static CoordinatorLayout coordinatorLayout;
    SlidingTabLayout mTabs;
    static Context context;
    static CookieManager msCookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);

    static List<LezioneM> lezioniMateries = new ArrayList<>();
    static List<Lezione> lezioni = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lezioni);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(getString(R.string.lezioni));
        setSupportActionBar(toolbar);
        mPager = (SwipeViewPager) findViewById(R.id.pagerLezioni);
        //mPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        mTabs = (SlidingTabLayout) findViewById(R.id.tabsLezioni);
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(Lezioni.this, R.color.bluematerial);
            }
        });

        // mTabs.setViewPager(mPager);
        context = Lezioni.this;

        new GetStringFromUrl().execute("https://web.spaggiari.eu/cvv/app/default/regclasse_lezioni_xstudenti.php");
    }

    class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            MyFragment myFragment = new MyFragment();
            myFragment = myFragment.getInstance(position);
            return myFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return lezioniMateries.get(position).getMateria();
        }

        @Override
        public int getCount() {
            return lezioniMateries.size();
        }
    }

    @SuppressLint("ValidFragment")
    public static class MyFragment extends Fragment implements ObservableScrollViewCallbacks {

        ObservableRecyclerView rv;
        Bundle bundle;

        public MyFragment getInstance(int position) {
            MyFragment myFragment = new MyFragment();
            Bundle args = new Bundle();
            args.putInt("position", position);
            setRetainInstance(true);
            myFragment.setArguments(args);
            return myFragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
            final View layout = inflater.inflate(R.layout.fragment_lezioni, container, false);
            bundle = getArguments();
            rv = (ObservableRecyclerView) layout.findViewById(R.id.cardListLezioni);
            rv.setScrollViewCallbacks(this);
            rv.setHasFixedSize(true);
            rv.setLayoutManager(new GridLayoutManager(context, 1));

            if (bundle != null) {
                final int position = bundle.getInt("position");
                lezioni = new ArrayList<>();
                adapter = new RVAdapter(lezioni);
                rv.setAdapter(adapter);

                List<Lezione> lez = lezioniMateries.get(position).getLezioni();

                for (int i = lez.size() - 1; i > -1; i--) {
                    if (lez.get(i).getDescrizione().length() > 0)
                        lezioni.add(lez.get(i));
                }
                adapter.notifyDataSetChanged();
            }

            return layout;
        }

        @Override
        public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

        }

        @Override
        public void onDownMotionEvent() {

        }

        @Override
        public void onUpOrCancelMotionEvent(ScrollState scrollState) {

        }
    }

    public static class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {
        List<Lezione> leziones;

        public class PersonViewHolder extends RecyclerView.ViewHolder {
            CardView cv;
            TextView Prof;
            TextView Data;
            TextView Desc;

            PersonViewHolder(View itemView) {
                super(itemView);
                cv = (CardView) itemView.findViewById(R.id.LezioneCv);
                Prof = (TextView) itemView.findViewById(R.id.LezioneProf);
                Data = (TextView) itemView.findViewById(R.id.LezioneData);
                Desc = (TextView) itemView.findViewById(R.id.LezioneDes);
            }
        }

        RVAdapter(List<Lezione> leziones) {
            this.leziones = leziones;
        }

        @Override
        public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_lezioni, parent, false);
            return new PersonViewHolder(v);
        }


        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }


        @Override
        public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
            personViewHolder.Prof.setText(leziones.get(i).getProf());
            personViewHolder.Desc.setText(leziones.get(i).getDescrizione());
            personViewHolder.Data.setText(leziones.get(i).getData());
        }

        @Override
        public int getItemCount() {
            return leziones.size();
        }

    }

    public class GetStringFromUrl extends AsyncTask<String, Void, String> {
        String azione = "";
        String url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            final String COOKIES_HEADER = "Set-Cookie";
            Log.v("Scarico", params[0]);

            URL url;
            HashMap<String, String> postDataParams = new HashMap<>();
            SharedPreferences sharedPref = Lezioni.this.getSharedPreferences("Dati", Context.MODE_PRIVATE);

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
                    this.url = url_car;

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

                azione = Azione.LEZIONI;
                try {
                    url = new URL(params[0]);
                    this.url = url.toString();
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
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

            if (azione.equals(Azione.LEZIONI)) {
                if (result != null && result.length() > 0) {

                    if (!url.contains("materia")) {
                        Elements materien = Jsoup.parse(result).select("table#data_table").get(1).select("tbody").select("div").select("div");
                        for (int i = 0; i < materien.size(); i++) {
                            LezioneM lm = new LezioneM();
                            lm.setId(materien.get(i).attr("materia_id"));
                            lm.setMateria(MateriaDecente(materien.get(i).attr("title")));
                            lezioniMateries.add(lm);
                            i++;
                        }
                        mPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
                        mTabs.setViewPager(mPager);

                        for (LezioneM l : lezioniMateries)
                            new GetStringFromUrl().execute("https://web.spaggiari.eu/cvv/app/default/regclasse_lezioni_xstudenti.php?materia=" + l.getId());

                    } else {
                        String materia = url.substring(url.lastIndexOf("=") + 1);

                        for (LezioneM l : lezioniMateries) {
                            if (l.getId().equals(materia)) {
                                Elements el = Jsoup.parse(result).select("table#sort_table").select("tbody").select("tr");
                                for (Element e :
                                        el) {
                                    Elements dati = e.select("td");
                                    Lezione lezione = new Lezione();
                                    lezione.setProf(ProfDecente(dati.get(0).text()));
                                    lezione.setData(dati.get(1).text().trim());
                                    lezione.setDescrizione(dati.get(2).text());
                                    l.addLezione(lezione);
                                    adapter.notifyDataSetChanged();
                                }
                                if (lezioni.isEmpty()) {
                                    mPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
                                    mTabs.setViewPager(mPager);
                                }
                            }
                        }

                    }
                }

            }

        }

    }
}
