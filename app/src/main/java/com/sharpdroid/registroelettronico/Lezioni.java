package com.sharpdroid.registroelettronico;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Azione;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Lezione;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.LezioneM;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.MyUsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static com.sharpdroid.registroelettronico.MainActivity.msCookieManager;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.MateriaDecente;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ProfDecente;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.getPostDataString;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.isNetworkAvailable;

public class Lezioni extends AppCompatActivity {

    private static RVAdapter adapter;
    private static ViewPager mPager;
    private static Context context;
    private static final List<LezioneM> lezioniMateries = new ArrayList<>();
    private static List<Lezione> lezioni = new ArrayList<>();
    private TabLayout mTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lezioni);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(getString(R.string.lezioni));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> finish());

        mPager = (ViewPager) findViewById(R.id.pagerLezioni);

        mTabs = (TabLayout) findViewById(R.id.tabsLezioni);

        // mTabs.setViewPager(mPager);
        context = Lezioni.this;
        if (isNetworkAvailable(context)) {
            if (msCookieManager.getCookieStore().getCookies().isEmpty())
                new GetStringFromUrl().execute(MainActivity.BASE_URL + "/auth/app/default/AuthApi2.php?a=aLoginPwd");
            new GetStringFromUrl().execute(MainActivity.BASE_URL + "/fml/app/default/regclasse_lezioni_xstudenti.php");
        }
    }

    @SuppressLint("ValidFragment")
    public static class MyFragment extends Fragment {

        RecyclerView rv;
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
            rv = (RecyclerView) layout.findViewById(R.id.cardListLezioni);
            rv.setHasFixedSize(true);
            rv.setLayoutManager(new GridLayoutManager(context, 1));

            if (bundle != null) {
                final int position = bundle.getInt("position");
                lezioni = new ArrayList<>();
                adapter = new RVAdapter(lezioni);
                rv.setAdapter(adapter);

                lezioni.addAll(lezioniMateries.get(position).getLezioni());
                adapter.notifyDataSetChanged();
            }

            return layout;
        }
    }

    public static class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {
        final List<Lezione> leziones;

        RVAdapter(List<Lezione> leziones) {
            this.leziones = leziones;
        }

        @Override
        public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_lezioni, parent, false);
            return new PersonViewHolder(v);
        }

        @Override
        public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
            personViewHolder.Prof.setText(leziones.get(i).getProf());

            if (leziones.get(i).getDescrizione().length() > 0) {
                personViewHolder.Desc.setVisibility(View.VISIBLE);
                personViewHolder.Desc.setText(leziones.get(i).getDescrizione());
            } else personViewHolder.Desc.setVisibility(View.GONE);

            personViewHolder.Data.setText(leziones.get(i).getData());
        }

        @Override
        public int getItemCount() {
            return leziones.size();
        }

        class PersonViewHolder extends RecyclerView.ViewHolder {
            final CardView cv;
            final TextView Prof;
            final TextView Data;
            final TextView Desc;

            PersonViewHolder(View itemView) {
                super(itemView);
                cv = (CardView) itemView.findViewById(R.id.LezioneCv);
                Prof = (TextView) itemView.findViewById(R.id.LezioneProf);
                Data = (TextView) itemView.findViewById(R.id.LezioneData);
                Desc = (TextView) itemView.findViewById(R.id.LezioneDes);
            }
        }

    }

    class PagerAdapter extends FragmentPagerAdapter {

        PagerAdapter(FragmentManager fm) {
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

    public class GetStringFromUrl extends AsyncTask<String, Void, String> {
        String azione = "";
        String url;

        @Override
        protected String doInBackground(String... params) {
            Log.v("Scarico", params[0]);

            URL url;
            HashMap<String, String> postDataParams = new HashMap<>();
            SharedPreferences sharedPref = Lezioni.this.getSharedPreferences("Dati", Context.MODE_PRIVATE);

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
                            lm.setAutoriId(materien.get(i).attr("autori_id"));
                            lm.setMateria(MateriaDecente(materien.get(i).attr("title")));
                            lezioniMateries.add(lm);
                            i++;
                        }
                        mPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
                        mTabs.setupWithViewPager(mPager);

                        for (LezioneM l : lezioniMateries)
                            new GetStringFromUrl().execute(MainActivity.BASE_URL + "/fml/app/default/regclasse_lezioni_xstudenti.php?action=loadLezioni&autori_id=" + l.getAutoriId() + "&materia=" + l.getId());

                    } else {
                        String materia = url.substring(url.lastIndexOf("=") + 1);

                        for (LezioneM l : lezioniMateries) {
                            if (l.getId().equals(materia)) {

                                StringBuilder sb = new StringBuilder();
                                sb.append("<html><table>");
                                sb.append(result);
                                sb.append("</table></html>");


                                Document d = Jsoup.parse(sb.toString());
                                Elements el = d.select("html").select("body").select("table").select("tbody").select("tr");
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
                                    mTabs.setupWithViewPager(mPager);
                                }
                            }
                        }

                    }
                }

            }

        }

    }
}
