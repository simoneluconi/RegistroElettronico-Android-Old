package com.sharpdroid.registroelettronico;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.sharpdroid.registroelettronico.ChromeTabs.CustomTabActivityHelper;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Azione;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.CVData;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Compito;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.FileDid;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Materia;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Medie;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.MyAccount;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.MyDB;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.MyUsers;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Nota;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Voto;
import com.sharpdroid.registroelettronico.SharpLibrary.ClockView;
import com.sharpdroid.registroelettronico.SharpLibrary.VotiDettAdp;
import com.sharpdroid.registroelettronico.Tabs.SlidingTabLayout;
import com.sharpdroid.registroelettronico.Tabs.SwipeViewPager;
import com.squareup.picasso.Picasso;

import org.acra.ACRA;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

import fr.ganfra.materialspinner.MaterialSpinner;

import static android.support.v4.content.FileProvider.getUriForFile;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.AvviaNotifiche;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.CancellaPagineLocali;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ColorByMedia;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ConvertiCompito;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ConvertiDimensione;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ImmagineAccout;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.InizialeMaiuscola;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.MateriaDecente;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.MediaIpotetica;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.MessaggioVoto;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ProfDecente;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ReadAccounts;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ReadAgenda;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ReadDidattica;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ReadMyAgenda;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ReadNote;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.SaveMyCompito;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.SpaziVoti;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.getEmojiByUnicode;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.getPostDataString;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.isNetworkAvailable;

public class MainActivity extends AppCompatActivity {

    static final String FILE_PROVIDER_STRING = "com.sharpdroid.fileprovider";
    public static final String BASE_URL = "https://web.spaggiari.eu";
    static CookieManager msCookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL); //Gestore Cookie
    static SwipeViewPager mPager;
    SlidingTabLayout mTabs;
    static Drawer Drawerresult;
    //Runnable
    static Runnable m_handlerMedie;
    static Runnable m_handlerTuttiVoti;
    static Runnable m_handlerQ1;
    static Runnable m_handlerQ2;
    static Runnable m_handlerNote;
    static Runnable m_handlerAgenda;
    static Runnable m_handlerDidattica;

    //Variabili per aggiornare i dati
    static boolean datiOffline = true;
    static boolean updateMedie = true;
    static boolean updateTuttiVoti = true;
    static boolean updateQ1 = true;
    static boolean updateQ2 = true;
    static boolean updateNote = true;
    static boolean updateAgenda = true;
    static boolean updateDidattica = true;

    //Stringhe con i dati WEB
    static String WP_Didattica = null;

    //Stringhe con i dati offline
    static String Off_Didattica = null;

    //Medie dei vari periodi
    static List<Medie> MedieVotiMG = new ArrayList<>();
    static List<Medie> MedieVotiP1 = new ArrayList<>();
    static List<Medie> MedieVotiP2 = new ArrayList<>();

    //Liste per dati
    static List<FileDid> fileDids = new ArrayList<>();
    static List<Compito> compitiDatas = new ArrayList<>();
    static List<Materia> votis = new ArrayList<>();
    static List<Nota> notes = new ArrayList<>();
    public static List<Event> events;
    static List<Compito> compitishow = new ArrayList<>();

    static int currPage = 0;
    static int currProf;
    static DateTime primadata;
    static DateTime secondadata;
    static Context context;
    public static final int CONTROLLO_VOTI_ID = 111101;
    static CoordinatorLayout coordinatorLayout;
    static CompactCalendarView compactCalendarView;
    static SwipeRefreshLayout swipeRefreshLayout;
    static int[] didatticaPos = {0, 0, 0};//Posizione, Elemento, Click
    static int nTentativiDownloadDidattica = 0; //Numero tentativi di download dei file della didattica
    static boolean giaAperta = false; //L'app era già aperta?
    public static String SEPARATORE_MATERIE = "grautext open_sans_condensed_bold font_size_14";
    public static App application;
    public static Tracker mTracker;
    public static String DataCal = null; //Data da aprire nel calendario dalla notifica
    static File DownloadFolder;
    static DateTime CalMostra = new DateTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        context = getApplicationContext();
        application = (App) getApplication();
        mTracker = application.getDefaultTracker();

        mPager = (SwipeViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                if (position >= 0 && position <= 3)
                    return ContextCompat.getColor(MainActivity.this, R.color.bluematerial);
                else if (position == 4)
                    return ContextCompat.getColor(MainActivity.this, R.color.colorAccent);
                else if (position == 5)
                    return ContextCompat.getColor(MainActivity.this, R.color.redmaterial);
                else if (position == 6)
                    return ContextCompat.getColor(MainActivity.this, R.color.greenmaterial);
                else return 0;
            }
        });

        mTabs.setViewPager(mPager);

        mTabs.setOnPageChangeListener(new SwipeViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currPage = position;
            }

            @Override
            public void onPageSelected(int position) {
                currPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //Recupero il tab da aprrie se l'app è stata aperta da una notifica
        int tabDaAprire = 0;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                tabDaAprire = 0;
            } else {
                tabDaAprire = extras.getInt("com.sharpdroid.registroelettronico.notifiche.TAB", 0);
                if (tabDaAprire == 4)
                    DataCal = extras.getString("com.sharpdroid.registroelettronico.notifiche.DATACAL", null);
            }
        }
        mPager.setCurrentItem(tabDaAprire);

        final SharedPreferences sharedPref = getSharedPreferences("Dati", Context.MODE_PRIVATE);

        //Scarico tutti i compiti dell'anno
        int mm = Calendar.getInstance().get(Calendar.MONTH) + 1;
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
        if (mm >= 9 && mm <= 12) {
            primadata = dtf.parseDateTime(Calendar.getInstance().get(Calendar.YEAR) + "-09-01");
            secondadata = dtf.parseDateTime((Calendar.getInstance().get(Calendar.YEAR) + 1) + "-09-01");
        } else {
            primadata = dtf.parseDateTime((Calendar.getInstance().get(Calendar.YEAR) - 1) + "-09-01");
            secondadata = dtf.parseDateTime(Calendar.getInstance().get(Calendar.YEAR) + "-09-01");
        }

        SQLiteDatabase db = new MyUsers(context).getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + MyUsers.UserEntry.TABLE_NAME, null);
        int count = c.getCount();
        c.close();
        db.close();

        if (count <= 0) {
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(myIntent);
            finish();
        } else {

            final List<MyAccount> Accounts = ReadAccounts(MainActivity.this);
            AccountHeader headerResult = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.drawable.header)
                    .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                        @Override
                        public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                            CancellaPagineLocali(MainActivity.this);

                            if (profile.getIdentifier() == 100010) {
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            } else {
                                if (!Accounts.isEmpty() && !currentProfile) {
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putInt("CurrentProfile", (int) profile.getIdentifier() + 1);
                                    editor.apply();
                                    Intent i = getBaseContext().getPackageManager()
                                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                }
                            }

                            return false;
                        }
                    }).withOnAccountHeaderItemLongClickListener(new AccountHeader.OnAccountHeaderItemLongClickListener() {
                        @Override
                        public boolean onProfileLongClick(View view, final IProfile profile, final boolean current) {
                            if (profile.getIdentifier() != 100010 && !Accounts.isEmpty()) {

                                new MaterialDialog.Builder(MainActivity.this)
                                        .title(R.string.eliminaacc)
                                        .content(getString(R.string.eliminaaccdes, profile.getName()))
                                        .theme(Theme.LIGHT)
                                        .positiveText(android.R.string.ok)
                                        .negativeText(android.R.string.cancel)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                MyAccount a = Accounts.get((int) profile.getIdentifier());

                                                SQLiteDatabase db = new MyUsers(context).getWritableDatabase();
                                                String[] datas = {a.getName(), a.getUsername(), a.getCodicescuola()};
                                                String command = MyUsers.UserEntry.COLUMN_NAME_NAME + "= ? AND "
                                                        + MyUsers.UserEntry.COLUMN_NAME_USERNAME + "= ? AND "
                                                        + MyUsers.UserEntry.COLUMN_NAME_CODICESCUOLA + "= ?";
                                                db.delete(MyUsers.UserEntry.TABLE_NAME, command, datas);
                                                db.close();
                                                CancellaPagineLocali(MainActivity.this);

                                                SharedPreferences.Editor editor = sharedPref.edit();
                                                editor.putInt("CurrentProfile", 1);
                                                editor.apply();
                                                Intent i = getBaseContext().getPackageManager()
                                                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(i);

                                            }
                                        })

                                        .show();

                            }

                            return false;
                        }
                    })
                    .build();

            DrawerImageLoader.init(new AbstractDrawerImageLoader() {
                @Override
                public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                    Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
                }

                @Override
                public void cancel(ImageView imageView) {
                    Picasso.with(imageView.getContext()).cancelRequest(imageView);
                }
            });

            int i = 0;
            if (!Accounts.isEmpty()) {

                for (i = 0; i < Accounts.size(); i++) {
                    MyAccount a = Accounts.get(i);
                    headerResult.addProfile(new ProfileDrawerItem()
                            .withName(a.getName())
                            .withIcon(ImmagineAccout(a.getName()))
                            .withNameShown(true)
                            .withEmail(a.getUsername())
                            .withIdentifier(i), i);
                }
            }

            headerResult.addProfile(new ProfileSettingDrawerItem().withName(getResources().getString(R.string.aggacc))
                    .withIcon(ContextCompat.getDrawable(this, R.drawable.iconaccplus))
                    .withIdentifier(100010), i);

            currProf = sharedPref.getInt("CurrentProfile", 0);
            headerResult.setActiveProfile(currProf - 1);

            Drawerresult = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withAccountHeader(headerResult)
                    .withActionBarDrawerToggleAnimated(true)
                    .withSelectedItemByPosition(0)
                    .addDrawerItems(
                            new PrimaryDrawerItem().withName(R.string.home).withIcon(ContextCompat.getDrawable(this, R.drawable.home)),
                            new PrimaryDrawerItem().withName(R.string.oggiscuola).withIcon(ContextCompat.getDrawable(this, R.drawable.calendartoday)),
                            new PrimaryDrawerItem().withName(R.string.lezioni).withIcon(ContextCompat.getDrawable(this, R.drawable.school)),
                            new PrimaryDrawerItem().withName(R.string.circolari).withIcon(ContextCompat.getDrawable(this, R.drawable.document)).withIdentifier(3).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.redmaterial)),
                            new PrimaryDrawerItem().withName(R.string.scrutini).withIcon(ContextCompat.getDrawable(this, R.drawable.book)).withIdentifier(4).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.redmaterial)),
                            new PrimaryDrawerItem().withName(R.string.fileoff).withIcon(ContextCompat.getDrawable(this, R.drawable.download)).withIdentifier(5).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.bluematerial)),
                            new PrimaryDrawerItem().withName(R.string.forzacontrollo).withIcon(ContextCompat.getDrawable(this, R.drawable.refresh)),
                            new DividerDrawerItem(),
                            new SecondaryDrawerItem().withName(R.string.segnalaproblema).withIcon(ContextCompat.getDrawable(this, R.drawable.email))
                    ).build();

            Drawerresult.setSelection(0);
            Drawerresult.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                    Drawerresult.setSelection(0);

                    switch (position) {
                        case 1:
                            mPager.setCurrentItem(0);
                            break;

                        case 2:
                            Intent myIntent = new Intent(MainActivity.this, OggiAScuola.class);
                            MainActivity.this.startActivity(myIntent);
                            break;

                        case 3:
                            myIntent = new Intent(MainActivity.this, Lezioni.class);
                            MainActivity.this.startActivity(myIntent);
                            break;

                        case 4:
                            myIntent = new Intent(MainActivity.this, Circolari.class);
                            MainActivity.this.startActivity(myIntent);
                            break;

                        case 5:
                            myIntent = new Intent(MainActivity.this, Scrutini.class);
                            MainActivity.this.startActivity(myIntent);
                            break;

                        case 6:
                            myIntent = new Intent(MainActivity.this, FileOffline.class);
                            MainActivity.this.startActivity(myIntent);
                            break;


                        case 7:
                            AvviaNotifiche(MainActivity.this);
                            break;

                        case 9:
                            File backupDB = new File(getExternalCacheDir(), "MyData.db");
                            try {
                                PackageManager m = getPackageManager();
                                PackageInfo p = m.getPackageInfo(getPackageName(), 0);
                                File currentDB = new File(p.applicationInfo.dataDir + "//databases//MyData.db");


                                FileChannel src = new FileInputStream(currentDB).getChannel();
                                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                                dst.transferFrom(src, 0, src.size());
                                src.close();
                                dst.close();
                            } catch (Exception e) {
                                Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                            }

                            ArrayList<Uri> uris = new ArrayList<>();
                            uris.add(Uri.fromFile(backupDB));

                            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"luconi.simone@gmail.com"});
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Registro Elettronico");
                            intent.putExtra(Intent.EXTRA_TEXT, "Inviando questa mail invierai anche il contenuto di voti, agenda e note.\n");
                            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                            intent.setType("text/plain");
                            startActivity(Intent.createChooser(intent, "Seleziona un client email:"));
                            break;

                    }
                    return false;
                }
            });

            updateMedie = true;
            updateTuttiVoti = true;
            updateQ2 = true;
            updateQ1 = true;
            updateNote = true;
            updateAgenda = true;
            updateDidattica = true;

            if (count >= 0) {
                CaricaVotiOffline();
                compitiDatas = ReadAgenda(this);
                notes = ReadNote(this);

                if (isNetworkAvailable(MainActivity.this)) {
                    new GetStringFromUrl().execute("https://play.google.com/store/apps/details?id=com.sharpdroid.registroelettronico");
                    AggiornaDati();
                } else
                    Toast.makeText(getApplicationContext(), R.string.nointernet, Toast.LENGTH_LONG).show();

                new Thread() {
                    @Override
                    public void run() {
                        Off_Didattica = ReadDidattica(MainActivity.this);
                    }
                }.run();
            }

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancelAll(); //Cancello tutte le notifiche dell'app se ci sono

            SharedPreferences.Editor editor = sharedPref.edit();
            if (sharedPref.getBoolean("primaapertura", true)) {

                if (sharedPref.getBoolean("notifichevoti", true) || sharedPref.getBoolean("notificheagenda", true)) {

                    AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(this, Notifiche.class);
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(this, CONTROLLO_VOTI_ID, intent, 0);

                    alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                            AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
                }

                editor.putBoolean("primaapertura", false);
                editor.apply();

                Intent myIntent = new Intent(MainActivity.this, Intro.class);
                MainActivity.this.startActivity(myIntent);
            }

            //Cartella dei download, se non esiste la creo, se esiste aggiorno il numero dei file presenti
            DownloadFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "Registro Elettronico");
            if (!DownloadFolder.exists()) {
                DownloadFolder.mkdir();
            } else {
                AggiornaFileOffline();
            }

            boolean alarmUp = (PendingIntent.getBroadcast(this, 0,
                    new Intent(this, Notifiche.class),
                    PendingIntent.FLAG_NO_CREATE) != null);

            if (alarmUp)
                Log.v("Notifiche", "Attive");
            else
                Log.v("Notifiche", "Non Attive");

        }
    }

    public static class GetStringFromUrl extends AsyncTask<String, Integer, String> {

        String azione = "";
        String url = "";
        private static final int BUFFER_SIZE = 4096;
        Snackbar DownloadProgressSnak;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

            url = params[0];
            Log.v("Scarico", params[0]);

            URL url;
            HashMap<String, String> postDataParams = new HashMap<>();
            SharedPreferences sharedPref = context.getSharedPreferences("Dati", Context.MODE_PRIVATE);

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

                if (params[0].equals(BASE_URL + "/cvv/app/default/genitori_note.php")) {
                    azione = Azione.VOTI;
                } else if (params[0].equals(BASE_URL + "/cvv/app/default/gioprof_note_studente.php")) {
                    azione = Azione.NOTE;
                } else if (params[0].contains(BASE_URL + "/cvv/app/default/agenda_studenti.php")) {
                    azione = Azione.AGENDA;
                } else if (params[0].equals(BASE_URL + "/cvv/app/default/didattica_genitori.php")) {
                    azione = Azione.DIDATTICA;
                } else if (params[0].contains(BASE_URL + "/cvv/app/default/didattica_genitori.php?a=downloadContenuto&contenuto_id=")) {
                    azione = Azione.DOWNLOAD;
                } else if (params[0].equals("https://play.google.com/store/apps/details?id=com.sharpdroid.registroelettronico")) {
                    azione = Azione.CONTROLLO_VERSIONE;
                } else if (params[0].equals(BASE_URL + "/sif/app/default/bacheca_utente.php"))
                    azione = Azione.CIRCOLARI;
                else if (params[0].equals(BASE_URL + "/sol/app/default/documenti_sol.php"))
                    azione = Azione.SCRUTINI;
                else if (params[0].equals(BASE_URL + "/sps/app/default/myprofile.php"))
                    azione = Azione.ACC_IMAGE;

                try {
                    url = new URL(params[0]);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    List<HttpCookie> cookies = msCookieManager.getCookieStore().getCookies();
                    if (cookies.size() > 0)
                        Log.v("PHPSESSID", cookies.get(0).toString());

                    url = new URL(params[0]);
                    conn = (HttpURLConnection) url.openConnection();


                    if (!azione.equals(Azione.DOWNLOAD)) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(
                                conn.getInputStream()));
                        String inputLine;
                        StringBuilder sb = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            sb.append(inputLine);
                            sb.append("\n");
                        }

                        in.close();

                        if (sb.length() > 0) {

                            switch (azione) {
                                case Azione.NOTE: {

                                    notes.clear();
                                    Elements metaElems = Jsoup.parse(sb.toString()).select("table#sort_table").select("tbody").select("tr");
                                    db = new MyDB(context).getWritableDatabase();
                                    db.execSQL("DELETE FROM " + MyDB.NotaEntry.TABLE_NAME);
                                    db.beginTransaction();
                                    for (Element e : metaElems) {
                                        Nota nota = new Nota();
                                        Elements el = e.select("td");
                                        String prof = el.get(0).text().trim();
                                        if (prof.length() > 0) {
                                            nota.setProf(ProfDecente(prof));
                                            nota.setTipo(el.get(3).text().trim());
                                            nota.setData(el.get(1).text().trim().replaceAll("-", "/"));
                                            nota.setContenuto(el.get(2).text().trim());
                                            notes.add(nota);

                                            ContentValues dati = new ContentValues();
                                            dati.put(MyDB.NotaEntry.COLUMN_NAME_AUTORE, nota.getProf());
                                            dati.put(MyDB.NotaEntry.COLUMN_NAME_CONTENUTO, nota.getContenuto());
                                            dati.put(MyDB.NotaEntry.COLUMN_NAME_DATA, nota.getData());
                                            dati.put(MyDB.NotaEntry.COLUMN_NAME_TIPO, nota.getTipo());
                                            db.insert(MyDB.NotaEntry.TABLE_NAME, MyDB.NotaEntry.COLUMN_NAME_NULLABLE, dati);
                                        }

                                    }
                                    db.setTransactionSuccessful();
                                    db.endTransaction();
                                    db.close();

                                }
                                break;

                                case Azione.VOTI: {

                                    votis.clear();
                                    Elements metaElems = Jsoup.parse(sb.toString()).select("tr");
                                    db = new MyDB(context).getWritableDatabase();
                                    db.execSQL("DELETE FROM " + MyDB.VotoEntry.TABLE_NAME);

                                    db.beginTransaction();
                                    for (int i = 0; i < metaElems.size(); i++) {

                                        if (metaElems.get(i).select("td").get(0).className().contains(MainActivity.SEPARATORE_MATERIE)) {
                                            String materia = MateriaDecente(metaElems.get(i).text().trim());
                                            Materia materias = new Materia(materia);
                                            i++;
                                            boolean esci = false;
                                            while (!metaElems.get(i).select("td").get(0).className().contains(MainActivity.SEPARATORE_MATERIE) && !esci) {

                                                Elements elT = metaElems.get(i).select("span"); //Tipo - data
                                                Elements elV = metaElems.get(i).select("p"); // Voto
                                                String tmp[] = elT.get(0).text().trim().split("-");
                                                boolean VotoBlu = metaElems.get(i).select("div").attr("class").contains("f_reg_voto_dettaglio");

                                                String[] periodotmp = metaElems.get(i).select("td").get(1).className().split("\\s+");
                                                String periodo = periodotmp[periodotmp.length - 1];
                                                String data = tmp[1].trim();
                                                String voto = elV.get(1).text().trim();
                                                String tipo = tmp[0].trim();
                                                String commento = elT.get(1).text();

                                                Voto votos = new Voto();
                                                votos.setVoto(voto);
                                                votos.setCommento(commento);
                                                votos.setData(data);
                                                votos.setTipo(tipo);
                                                votos.setVotoblu(VotoBlu);
                                                votos.setPeriodo(periodo);
                                                materias.addVoto(votos);

                                                ContentValues dati = new ContentValues();
                                                dati.put(MyDB.VotoEntry.COLUMN_NAME_MATERIA, materia);
                                                dati.put(MyDB.VotoEntry.COLUMN_NAME_DATA, data);
                                                dati.put(MyDB.VotoEntry.COLUMN_NAME_TIPO, tipo);
                                                dati.put(MyDB.VotoEntry.COLUMN_NAME_VOTOBLU, VotoBlu);
                                                dati.put(MyDB.VotoEntry.COLUMN_NAME_VOTO, voto);
                                                dati.put(MyDB.VotoEntry.COLUMN_NAME_COMMENTO, commento);
                                                dati.put(MyDB.VotoEntry.COLUMN_NAME_PERIODO, periodo);
                                                db.insert(MyDB.VotoEntry.TABLE_NAME, MyDB.VotoEntry.COLUMN_NAME_NULLABLE, dati);

                                                if (i + 1 != metaElems.size())
                                                    i++;
                                                else esci = true;

                                            }

                                            votis.add(materias);

                                            i--;
                                        }
                                    }
                                    db.setTransactionSuccessful();
                                    db.endTransaction();
                                    db.close();

                                }
                                break;
                                case Azione.AGENDA: {
                                    compitiDatas.clear();
                                    db = new MyDB(context).getWritableDatabase();
                                    db.execSQL("DELETE FROM " + MyDB.CompitoEntry.TABLE_NAME);
                                    JSONArray jsonCompiti = new JSONArray(sb.toString());
                                    db.beginTransaction();

                                    for (int i = 0; i < jsonCompiti.length(); i++) {
                                        Compito compito = ConvertiCompito(jsonCompiti.getJSONObject(i));
                                        if (compito != null) {
                                            compitiDatas.add(compito);
                                            ContentValues dati = new ContentValues();
                                            dati.put(MyDB.CompitoEntry.COLUMN_NAME_AUTORE, compito.getAutore());
                                            dati.put(MyDB.CompitoEntry.COLUMN_NAME_CONTENUTO, compito.getContenuto());
                                            dati.put(MyDB.CompitoEntry.COLUMN_NAME_DATAFINE, compito.getDataFineString());
                                            dati.put(MyDB.CompitoEntry.COLUMN_NAME_DATAINIZIO, compito.getDataInizioString());
                                            dati.put(MyDB.CompitoEntry.COLUMN_NAME_DATAINSERIMENTO, compito.getDataInserimentoString());
                                            dati.put(MyDB.CompitoEntry.COLUMN_NAME_TUTTOILGIORNO, compito.isTuttoIlGiorno());
                                            db.insert(MyDB.CompitoEntry.TABLE_NAME, MyDB.CompitoEntry.COLUMN_NAME_NULLABLE, dati);
                                            Log.v("Compito", compito.toString());
                                        }
                                    }
                                    db.setTransactionSuccessful();
                                    db.endTransaction();
                                    db.close();
                                }
                                break;

                                case Azione.ACC_IMAGE: {
                                    String ImmScr = sb.substring(sb.indexOf("<img src=\\\"") + 12);
                                    ImmScr = BASE_URL + "/" + ImmScr.substring(0, ImmScr.indexOf("\"")).replace("\\/", "/").replace("amp;", "");
                                    Log.v("ImgAcc", ImmScr);
                                    return ImmScr;
                                }
                            }
                        }

                        return sb.toString();
                    } else {

                        nTentativiDownloadDidattica++;
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            FileOutputStream fos = null;

            switch (azione) {

                case Azione.CONTROLLO_VERSIONE: {
                    try {

                        final String appPackageName = context.getPackageName();
                        PackageInfo pInfo = context.getPackageManager().getPackageInfo(appPackageName, 0);
                        int verCode = pInfo.versionCode;
                        String verCodeTmp = Jsoup.parse(result).select(".details-section-contents").get(4).select("div").get(12).text().trim();
                        int lastverCode = Integer.parseInt(verCodeTmp);

                        if (verCode < lastverCode) {
                            Snackbar.make(coordinatorLayout, R.string.ultimaversione, Snackbar.LENGTH_LONG)
                                    .setAction(android.R.string.ok, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent openPlayStore;
                                            try {
                                                openPlayStore = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
                                                openPlayStore.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                context.startActivity(openPlayStore);
                                            } catch (android.content.ActivityNotFoundException e) {
                                                openPlayStore = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                                                openPlayStore.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                context.startActivity(openPlayStore);
                                            }
                                        }
                                    }).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;

                case Azione.NOTE: {

                    if (m_handlerNote != null) {
                        updateNote = true;
                        m_handlerNote.run();
                    }
                }
                break;

                case Azione.VOTI: {

                    if (m_handlerMedie != null) {
                        updateMedie = true;
                        m_handlerMedie.run();
                    }

                    if (m_handlerTuttiVoti != null) {
                        updateTuttiVoti = true;
                        m_handlerTuttiVoti.run();
                    }

                    if (m_handlerQ1 != null) {
                        updateQ1 = true;
                        m_handlerQ1.run();
                    }

                    if (m_handlerQ2 != null) {
                        updateQ2 = true;
                        m_handlerQ2.run();
                    }

                }
                break;
                case Azione.AGENDA: {
                    if (m_handlerAgenda != null) {
                        updateAgenda = true;
                        m_handlerAgenda.run();
                    }

                }
                break;

                case Azione.DIDATTICA: {

                    if (result != null) {

                        WP_Didattica = result;
                        try {
                            fos = context.openFileOutput("Didattica", Context.MODE_PRIVATE);
                            fos.write(WP_Didattica.getBytes(), 0, WP_Didattica.getBytes().length);
                            fos.flush();
                            fos.close();
                        } catch (Exception ioe) {
                            ioe.printStackTrace();
                        } finally {
                            if (fos != null) try {
                                fos.close();
                            } catch (IOException ie) {
                                ie.printStackTrace();
                            }
                            Off_Didattica = "";
                        }


                        datiOffline = false;

                        if (swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        if (m_handlerDidattica != null) {
                            updateDidattica = true;
                            m_handlerDidattica.run();
                        }
                    }
                }
                break;

                case Azione.CIRCOLARI: {
                    if (result != null) {
                        Elements elements = Jsoup.parse(result).select("tr");
                        int n = 0;
                        for (Element el :
                                elements) {
                            Elements td = el.select("td");
                            if (td.size() > 4) {
                                if (td.get(0).select("div").size() > 0) {
                                    if (td.get(0).select("div").get(0).className().equals("open_sans graytext font_size_12")) {
                                        n++;
                                    }
                                }
                            }

                        }
                        AggiornaNCircolari(n);
                    }
                }
                break;

                case Azione.SCRUTINI: {
                    if (result != null) {
                        Elements elements = Jsoup.parse(result).select("table#table_documenti");
                        if (elements.size() != 0) {
                            elements = elements.get(0).select("tr");
                            {
                                int nFile = elements.size();
                                if (nFile != 0)
                                    nFile--;
                                try {
                                    String documenti = result.substring(result.indexOf("var documenti=") + 14);
                                    documenti = documenti.substring(0, documenti.indexOf("];") + 2);
                                    JSONArray jsonarray = new JSONArray(documenti);
                                    nFile += jsonarray.length();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ACRA.getErrorReporter().handleException(e, false);
                                }

                                AggiornaFileScrutini(nFile);
                            }
                        }
                    }
                }
                break;


                case Azione.DOWNLOAD: {
                    if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                    if (DownloadProgressSnak != null) DownloadProgressSnak.dismiss();
                    if (result != null) {
                        nTentativiDownloadDidattica = 0;
                        Intent myIntent = new Intent(Intent.ACTION_VIEW);
                        String mime;
                        File myFile = null;
                        try {
                            myFile = new File(result);
                            mime = URLConnection.guessContentTypeFromStream(new FileInputStream(myFile));
                            if (mime == null)
                                mime = URLConnection.guessContentTypeFromName(myFile.getName());
                            Uri uri = getUriForFile(context, FILE_PROVIDER_STRING, myFile);
                            myIntent.setDataAndType(uri, mime);
                            myIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
                            AggiornaFileOffline();
                            context.startActivity(myIntent);
                        } catch (android.content.ActivityNotFoundException e) {
                            if (myFile != null) {
                                if (coordinatorLayout != null)
                                    Snackbar.make(coordinatorLayout, "Nessuna app per aprire il file: " + myFile.getName(), Snackbar.LENGTH_LONG).show();

                            } else {
                                if (coordinatorLayout != null) {
                                    Snackbar.make(coordinatorLayout, "Nessuna app per aprire il file", Snackbar.LENGTH_LONG).show();
                                }
                            }
                            e.printStackTrace();
                        } catch (Exception e) {
                            Toast.makeText(context, "Errore:" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    } else {
                        if (nTentativiDownloadDidattica < 2) {
                            Toast.makeText(context, "Sessione scaduta. Login in corso...", Toast.LENGTH_LONG).show();
                            new GetStringFromUrl().execute(BASE_URL + "/home/app/default/pxlogin.php");
                            new GetStringFromUrl().execute(url);
                        } else {
                            Toast.makeText(context, "Si è verificato un problema", Toast.LENGTH_LONG).show();
                        }

                    }

                }
                break;

            }

        }
    }

    class PagerAdapter extends FragmentPagerAdapter {

        String[] tab = getResources().getStringArray(R.array.tab_title);

        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            MyFragment myFragment = new MyFragment();
            myFragment = myFragment.getInstance(position);

            //MyFragment myFragment = MyFragment.getInstance(position);
            return myFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tab[position];
        }

        @Override
        public int getCount() {
            return tab.length;
        }
    }

    @SuppressLint("ValidFragment")
    public static class MyFragment extends Fragment implements ObservableScrollViewCallbacks {

        ObservableRecyclerView rv;
        RVAdapter adapter;
        Bundle bundle;
        List<CVData> CVDataList;
        Snackbar snackbarVotiT;
        CardView CardViewCal;
        FloatingActionButton fab;


        public MyFragment getInstance(int position) {
            MyFragment myFragment = new MyFragment();
            Bundle args = new Bundle();
            args.putInt("position", position);
            setRetainInstance(true);
            myFragment.setArguments(args);
            return myFragment;
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
            final View layout = inflater.inflate(R.layout.fragment_principale, container, false);
            bundle = getArguments();
            Log.v("MainActivity", String.valueOf(bundle));
            //setRetainInstance(true);
            swipeRefreshLayout = null;
            swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swiperefresh);
            coordinatorLayout = (CoordinatorLayout) layout.findViewById(R.id.coordinatorLayout);
            swipeRefreshLayout.setColorSchemeResources(
                    R.color.bluematerial,
                    R.color.redmaterial,
                    R.color.greenmaterial,
                    R.color.orangematerial);
            swipeRefreshLayout.setEnabled(true);
            rv = (ObservableRecyclerView) layout.findViewById(R.id.cardList);
            rv.setScrollViewCallbacks(this);
            rv.setHasFixedSize(true);
            final int colonne = getResources().getInteger(R.integer.columns);
            rv.setLayoutManager(new GridLayoutManager(context, 1));
            Log.v("Colonne", String.valueOf(colonne));
            fab = (FloatingActionButton) layout.findViewById(R.id.fab);
            CVDataList = new ArrayList<>();
            adapter = new RVAdapter(CVDataList);
            rv.setAdapter(adapter);
            if (!giaAperta) {
                giaAperta = true;
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });

            }
            compactCalendarView = (CompactCalendarView) layout.findViewById(R.id.compactcalendar);
            compactCalendarView.setLocale(TimeZone.getTimeZone("Rome"), Locale.ITALIAN);
            compactCalendarView.setUseThreeLetterAbbreviation(true);

            final TextView txMese = (TextView) layout.findViewById(R.id.mesetx);
            CardViewCal = (CardView) layout.findViewById(R.id.calendarLay);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    WP_Didattica = null;
                    Off_Didattica = ReadDidattica(getContext());

                    if (isNetworkAvailable(context)) {
                        AggiornaDati();
                    } else {
                        Snackbar.make(coordinatorLayout, R.string.nointernet, Snackbar.LENGTH_LONG).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }

                }
            });

            if (bundle != null) {

                final int position = bundle.getInt("position");
                final SharedPreferences sharedPref = getContext().getSharedPreferences("Dati", Context.MODE_PRIVATE);


                if (position == 4) {
                    fab.setVisibility(View.VISIBLE);
                    fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.plus));

                    if (CardViewCal != null) {
                        CardViewCal.setVisibility(View.VISIBLE);
                    }

                } else if (position >= 0 && position <= 2) {
                    fab.setVisibility(View.GONE);
                    rv.setLayoutManager(new GridLayoutManager(context, colonne));

                } else {
                    if (CardViewCal != null) CardViewCal.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);
                }

                switch (position) {

                    case 0: {

                        CVDataList = new ArrayList<>();
                        adapter = new RVAdapter(CVDataList);
                        rv.setAdapter(adapter);
                        updateMedie = true;
                        m_handlerMedie = new Runnable() {
                            @Override
                            public void run() {
                                if (updateMedie) {
                                    CVDataList.clear();
                                    if (!votis.isEmpty()) {

                                        MedieVotiMG.clear();
                                        CVDataList.clear();
                                        double media = 0;
                                        int nMaterie = 0;
                                        for (Materia m : votis) {

                                            String materia = m.getMateria();
                                            Medie medie = new Medie();
                                            for (Voto v : m.getVoti())
                                                if (!v.isVotoblu())
                                                    medie.addVoto(v);

                                            if (medie.getMediaGenerale() > 0) {
                                                medie.setMateria(materia);
                                                MedieVotiMG.add(medie);
                                                double Obb = Double.parseDouble(context.getResources().getStringArray(R.array.votis)[sharedPref.getInt("obiettivovoto", 20)]);
                                                SpannableString mess = MessaggioVoto(Obb, medie.getMediaGenerale(), medie.getSommaGenerale(), medie.getnVotiGenerale());
                                                media += medie.getMediaGenerale();
                                                nMaterie++;
                                                CVDataList.add(new CVData(materia, mess, String.format(Locale.ENGLISH, "%.2f", medie.getMediaGenerale()), ((float) medie.getMediaGenerale() * 10f)));
                                            }
                                        }

                                        media = media / (double) nMaterie;

                                        CVDataList.add(new CVData("Media Generale", "", String.format(Locale.ENGLISH, "%.2f", media), ((float) media * 10f)));
                                        adapter.notifyDataSetChanged();

                                        if (!datiOffline)
                                            updateMedie = false;

                                    } else {
                                        CVDataList.clear();
                                        CVDataList.add(new CVData("Nessun Voto", "Non hai ancora nessun voto.", "", 0f));
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        };
                        m_handlerMedie.run();


                    }
                    break;


                    case 1: {

                        CVDataList = new ArrayList<>();
                        adapter = new RVAdapter(CVDataList);
                        rv.setAdapter(adapter);
                        updateQ1 = true;
                        m_handlerQ1 = new Runnable() {
                            @Override
                            public void run() {
                                if (updateQ1) {
                                    CVDataList.clear();
                                    if (!votis.isEmpty()) {

                                        MedieVotiP1.clear();
                                        CVDataList.clear();
                                        double media = 0;
                                        int nMaterie = 0;

                                        for (Materia m : votis) {

                                            String materia = m.getMateria();
                                            Medie medie = new Medie();
                                            for (Voto v : m.getVoti())
                                                if (!v.isVotoblu() && v.getPeriodo().equals(Voto.P1))
                                                    medie.addVoto(v);

                                            if (medie.getMediaGenerale() > 0) {
                                                medie.setMateria(materia);
                                                MedieVotiP1.add(medie);
                                                double Obb = Double.parseDouble(context.getResources().getStringArray(R.array.votis)[sharedPref.getInt("obiettivovoto", 20)]);
                                                SpannableString mess = MessaggioVoto(Obb, medie.getMediaGenerale(), medie.getSommaGenerale(), medie.getnVotiGenerale());
                                                media += medie.getMediaGenerale();
                                                nMaterie++;
                                                CVDataList.add(new CVData(materia, mess, String.format(Locale.ENGLISH, "%.2f", medie.getMediaGenerale()), ((float) medie.getMediaGenerale() * 10f)));
                                            }

                                        }

                                        media = media / (double) nMaterie;
                                        CVDataList.add(new CVData("Media Generale", "", String.format(Locale.ENGLISH, "%.2f", media), ((float) media * 10f)));
                                        adapter.notifyDataSetChanged();

                                        if (!datiOffline)
                                            updateQ1 = false;

                                    } else {
                                        CVDataList.clear();
                                        CVDataList.add(new CVData("Nessun Voto", "Non hai ancora nessun voto.", "", 0f));
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        };
                        m_handlerQ1.run();

                    }
                    break;

                    case 2: {

                        CVDataList = new ArrayList<>();
                        adapter = new RVAdapter(CVDataList);
                        rv.setAdapter(adapter);
                        updateQ2 = true;
                        m_handlerQ2 = new Runnable() {
                            @Override
                            public void run() {
                                if (updateQ2) {

                                    CVDataList.clear();
                                    if (!votis.isEmpty()) {

                                        MedieVotiP2.clear();
                                        CVDataList.clear();
                                        double media = 0;
                                        int nMaterie = 0;

                                        for (Materia m : votis) {

                                            String materia = m.getMateria();

                                            Medie medie = new Medie();
                                            for (Voto v : m.getVoti())
                                                if (!v.isVotoblu() && v.getPeriodo().equals(Voto.P2))
                                                    medie.addVoto(v);

                                            if (medie.getMediaGenerale() > 0) {
                                                medie.setMateria(materia);
                                                MedieVotiP2.add(medie);
                                                double Obb = Double.parseDouble(context.getResources().getStringArray(R.array.votis)[sharedPref.getInt("obiettivovoto", 20)]);
                                                SpannableString mess = MessaggioVoto(Obb, medie.getMediaGenerale(), medie.getSommaGenerale(), medie.getnVotiGenerale());
                                                media += medie.getMediaGenerale();
                                                nMaterie++;
                                                CVDataList.add(new CVData(materia, mess, String.format(Locale.ENGLISH, "%.2f", medie.getMediaGenerale()), ((float) medie.getMediaGenerale() * 10f)));
                                            }
                                        }

                                        media = media / (double) nMaterie;

                                        CVDataList.add(new CVData("Media Generale", "", String.format(Locale.ENGLISH, "%.2f", media), ((float) media * 10f)));
                                        adapter.notifyDataSetChanged();

                                        if (!datiOffline)
                                            updateQ2 = false;

                                    } else {
                                        CVDataList.clear();
                                        CVDataList.add(new CVData("Nessun Voto", "Non hai ancora nessun voto.", "", 0f));
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        };
                        m_handlerQ2.run();

                    }
                    break;

                    case 3: {
                        CVDataList = new ArrayList<>();
                        adapter = new RVAdapter(CVDataList);
                        rv.setAdapter(adapter);
                        updateTuttiVoti = true;
                        m_handlerTuttiVoti = new Runnable() {
                            @Override
                            public void run() {
                                if (updateTuttiVoti) {
                                    if (!votis.isEmpty()) {
                                        final int[] nVotiT = {0};
                                        CVDataList.clear();
                                        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy", Locale.ITALIAN);
                                        for (Materia m : votis) {
                                            final List<Voto> voti = m.getVoti();
                                            Collections.sort(voti, new Comparator<Voto>() {
                                                @Override
                                                public int compare(Voto o1, Voto o2) {
                                                    Date date1;
                                                    Date date2;
                                                    try {
                                                        date1 = format.parse(o1.getData());
                                                        date2 = format.parse(o2.getData());
                                                    } catch (ParseException e) {
                                                        throw new IllegalArgumentException("Impossibile convertire la data!", e);
                                                    }

                                                    return date1.compareTo(date2);
                                                }
                                            });

                                            StringBuilder sb = new StringBuilder();
                                            boolean separato = false;
                                            for (Voto v : voti) {
                                                if (v.getPeriodo().equals(Voto.P2) && !separato) {
                                                    sb.append("\n");
                                                    separato = true;
                                                }

                                                String spazi = SpaziVoti(v.getVoto());

                                                sb.append("\n   ");
                                                sb.append(v.getData().substring(0, 5));

                                                //Metodo molto rudimentale per allineare le stringhe
                                                if (v.getTipo().contains("Orale")) {
                                                    sb.append("           ");
                                                    sb.append(v.getVoto());
                                                    sb.append(spazi);
                                                    sb.append(v.getTipo());
                                                } else if (v.getTipo().contains("Scritto")) {
                                                    sb.append("           ");
                                                    sb.append(v.getVoto());
                                                    sb.append(spazi);
                                                    sb.append(v.getTipo());
                                                } else if (v.getTipo().contains("Pratico")) {
                                                    sb.append("           ");
                                                    sb.append(v.getVoto());
                                                    sb.append(spazi);
                                                    sb.append(v.getTipo());
                                                } else {
                                                    sb.append("           ");
                                                    sb.append(v.getVoto());
                                                    sb.append(spazi);
                                                    sb.append(v.getTipo());
                                                }

                                            }

                                            int nVoti = m.getVoti().size();
                                            nVotiT[0] += nVoti;
                                            CVDataList.add(new CVData(m.getMateria(), sb.toString(), nVoti + " voti", 0f));

                                        }

                                        snackbarVotiT = Snackbar.make(coordinatorLayout, "Voti totali: " + String.valueOf(nVotiT[0]), Snackbar.LENGTH_SHORT);
                                        snackbarVotiT.show();
                                        adapter.notifyDataSetChanged();

                                        if (!datiOffline)
                                            updateTuttiVoti = false;

                                    } else {
                                        CVDataList.clear();
                                        CVDataList.add(new CVData("Nessun Voto", "Non hai ancora nessun voto.", "", 0f));
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }

                        };
                        m_handlerTuttiVoti.run();

                    }
                    break;

                    case 4: {
                        CVDataList = new ArrayList<>();
                        adapter = new RVAdapter(CVDataList);
                        rv.setAdapter(adapter);
                        updateAgenda = true;
                        events = new ArrayList<>();

                        final Calendar cal = Calendar.getInstance();
                        boolean AggiungiGiornoSeSabato = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
                        boolean isDomenica = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
                        boolean OrarioScolastico = cal.get(Calendar.HOUR_OF_DAY) < 14;
                        if (AggiungiGiornoSeSabato && !OrarioScolastico)
                            CalMostra = CalMostra.plusDays(2);
                        else if (!OrarioScolastico || isDomenica)
                            CalMostra = CalMostra.plusDays(1);

                        if (DataCal != null)
                            CalMostra = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(DataCal);

                        compactCalendarView.setCurrentDate(CalMostra.toDate());
                        String mese = new SimpleDateFormat("MMMM yyyy", Locale.ITALIAN).format(CalMostra.toDate());
                        mese = InizialeMaiuscola(mese);
                        txMese.setText(mese);
                        m_handlerAgenda = new Runnable() {
                            @Override
                            public void run() {
                                if (updateAgenda) {
                                    compitishow.clear();
                                    List<Compito> myCompiti = ReadMyAgenda(context);
                                    compitiDatas.removeAll(myCompiti);
                                    compitiDatas.addAll(myCompiti);

                                    CVDataList.clear();
                                    compactCalendarView.removeAllEvents();

                                    for (Compito c : compitiDatas) {
                                        String autore;
                                        List<Event> events = compactCalendarView.getEvents(c.getDataInizio().toDate());
                                        Event newEvent;
                                        if (c.isVerifica()) {
                                            autore = getEmojiByUnicode(0x1F4DD) + " " + c.getAutore();
                                            newEvent = new Event(Color.RED, c.getDataInizio().getMillis());
                                        } else {
                                            newEvent = new Event(Color.YELLOW, c.getDataInizio().getMillis());
                                            autore = c.getAutore();
                                        }
                                        events.add(newEvent);

                                        compactCalendarView.removeEvents(c.getDataInizio().getMillis());
                                        compactCalendarView.addEvents(events);

                                        if (CalMostra.toLocalDate().isEqual(c.getDataInizio().withZone(CalMostra.getZone()).toLocalDate())) {
                                            compitishow.add(c);
                                            CVDataList.add(new CVData(autore, "<Agenda>" + c.getContenuto(), "", 0f));
                                        }
                                    }

                                    compactCalendarView.invalidate();
                                    if (CVDataList.isEmpty())
                                        CVDataList.add(new CVData("Nessun Evento", "Nessun evento per il giorno corrente.", "", 0f));

                                    if (!datiOffline) {
                                        updateAgenda = false;
                                    }
                                    adapter.notifyDataSetChanged();

                                }
                            }
                        };
                        m_handlerAgenda.run();


                        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
                                                            @Override
                                                            public void onDayClick(Date dateClicked) {
                                                                CalMostra = new DateTime(dateClicked);
                                                                updateAgenda = true;
                                                                m_handlerAgenda.run();
                                                                Log.v("NewDate", String.valueOf(dateClicked));
                                                            }

                                                            @Override
                                                            public void onMonthScroll(Date firstDayOfNewMonth) {
                                                                String mese = new SimpleDateFormat("MMMM yyyy", Locale.ITALIAN).format(firstDayOfNewMonth.getTime());
                                                                mese = InizialeMaiuscola(mese);
                                                                txMese.setText(mese);
                                                                CalMostra = new DateTime(firstDayOfNewMonth);
                                                                updateAgenda = true;
                                                                m_handlerAgenda.run();
                                                            }
                                                        }

                        );

                        compactCalendarView.setOnTouchListener(new View.OnTouchListener()

                                                               {
                                                                   @Override
                                                                   public boolean onTouch(View v, MotionEvent event) {

                                                                       switch (event.getAction()) {
                                                                           case MotionEvent.ACTION_UP:
                                                                               mPager.setPagingEnabled(true);
                                                                               break;
                                                                           default:
                                                                               mPager.setPagingEnabled(false);
                                                                               break;
                                                                       }
                                                                       return false;

                                                                   }
                                                               }

                        );

                        fab.setOnClickListener(null);
                        fab.setOnClickListener(new View.OnClickListener()

                                               {
                                                   @Override
                                                   public void onClick(View v) {
                                                       final String data = DateTimeFormat.forPattern("dd/MM/yyyy").print(CalMostra);
                                                       final String dataCal = DateTimeFormat.forPattern("yyyy-MM-dd").print(CalMostra);
                                                       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALIAN);
                                                       final String dataInserimento = sdf.format(Calendar.getInstance().getTime());
                                                       final MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                                                               .title(getResources().getString(R.string.aggcal))
                                                               .theme(Theme.LIGHT)
                                                               .iconRes(R.drawable.calendartoday)
                                                               .customView(R.layout.adapter_cust_comp, true)
                                                               .positiveText("Aggiungi")
                                                               .negativeText(android.R.string.cancel)
                                                               .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                                   @Override
                                                                   public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                                                       //Autore, DataInizio, DataInserimento, Commento
                                                                       EditText Autore = (EditText) dialog.findViewById(R.id.Tit);
                                                                       EditText Cont = (EditText) dialog.findViewById(R.id.Cont);
                                                                       ContentValues dati = new ContentValues();
                                                                       dati.put(MyDB.MyCompitoEntry.COLUMN_NAME_AUTORE, Autore.getText().toString().trim());
                                                                       dati.put(MyDB.MyCompitoEntry.COLUMN_NAME_DATA, dataCal);
                                                                       dati.put(MyDB.MyCompitoEntry.COLUMN_NAME_DATAINSERIMENTO, dataInserimento);
                                                                       dati.put(MyDB.MyCompitoEntry.COLUMN_NAME_CONTENUTO, Cont.getText().toString().trim());

                                                                       SaveMyCompito(getContext(), dati);
                                                                       updateAgenda = true;
                                                                       m_handlerAgenda.run();

                                                                   }
                                                               }).build();

                                                       dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                                                       TextView CurrDate = (TextView) dialog.findViewById(R.id.CurrTime);
                                                       CurrDate.setText(getString(R.string.eventoperil, data));
                                                       final EditText Autore = (EditText) dialog.findViewById(R.id.Tit);
                                                       final EditText Cont = (EditText) dialog.findViewById(R.id.Cont);
                                                       Autore.addTextChangedListener(new TextWatcher() {
                                                           @Override
                                                           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                           }

                                                           @Override
                                                           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                               dialog.getActionButton(DialogAction.POSITIVE).setEnabled(charSequence.length() > 0 && Cont.length() > 0);
                                                           }

                                                           @Override
                                                           public void afterTextChanged(Editable editable) {

                                                           }
                                                       });

                                                       Cont.addTextChangedListener(new TextWatcher() {
                                                           @Override
                                                           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                           }

                                                           @Override
                                                           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                               dialog.getActionButton(DialogAction.POSITIVE).setEnabled(charSequence.length() > 0 && Autore.length() > 0);
                                                           }

                                                           @Override
                                                           public void afterTextChanged(Editable editable) {

                                                           }
                                                       });


                                                       dialog.show();
                                                   }
                                               }

                        );

                    }
                    break;

                    case 5: {
                        CVDataList = new ArrayList<>();
                        adapter = new RVAdapter(CVDataList);
                        rv.setAdapter(adapter);
                        updateNote = true;
                        m_handlerNote = new Runnable() {
                            @Override
                            public void run() {
                                if (updateNote) {
                                    if (!notes.isEmpty()) {
                                        CVDataList.clear();

                                        List<Nota> notas = ReadNote(context);
                                        for (Nota nota : notas)
                                            CVDataList.add(new CVData(nota.getProf() + "(" + nota.getTipo() + ") - " + nota.getData(), nota.getContenuto(), "", 0f));

                                        adapter.notifyDataSetChanged();

                                        if (!datiOffline) {
                                            updateNote = false;
                                        }
                                    } else {
                                        CVDataList.clear();
                                        CVDataList.add(new CVData("Nessuna nota", "Ancora nessuna nota presente.", "", 0f));
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        };
                        m_handlerNote.run();

                    }
                    break;

                    case 6: {
                        didatticaPos[0] = 0;
                        CVDataList = new ArrayList<>();
                        adapter = new RVAdapter(CVDataList);
                        rv.setAdapter(adapter);
                        updateDidattica = true;
                        m_handlerDidattica = new Runnable() {
                            @Override
                            public void run() {
                                if (updateDidattica) {
                                    if (Off_Didattica != null) {
                                        CVDataList.clear();
                                        fileDids.clear();
                                        Elements metaElemsDidattica;
                                        if (WP_Didattica != null) {
                                            metaElemsDidattica = Jsoup.parse(WP_Didattica).select("tr");
                                        } else {
                                            metaElemsDidattica = Jsoup.parse(Off_Didattica).select("tr");
                                        }

                                        switch (didatticaPos[0]) {
                                            case 0: {
                                                for (int i = 0; i < metaElemsDidattica.size(); i++) {

                                                    int nCart = 0, nFile = 0, nLink = 0;
                                                    if (metaElemsDidattica.get(i).text().contains("Condivisi da")) {
                                                        Elements tmp = metaElemsDidattica.get(i).select("td");
                                                        String prof = tmp.get(2).text();
                                                        i++;
                                                        boolean stop = false;

                                                        while (i < metaElemsDidattica.size() && !stop) {
                                                            if (metaElemsDidattica.get(i).text().contains("Condivisi da"))
                                                                stop = true;
                                                            else {
                                                                if (metaElemsDidattica.get(i).className().equals("row row_parent"))  //Cartella
                                                                {
                                                                    nCart++;
                                                                    // metaElemsDidattica.get(i).text(); //Nome della cartella
                                                                } else { //File
                                                                    Elements tmp2 = metaElemsDidattica.get(i).select("span");
                                                                    if (tmp2.get(1).text().equals("file")) {
                                                                        nFile++;
                                                                    } else if (tmp2.get(1).text().equals("link")) {
                                                                        nLink++;
                                                                    }
                                                                }
                                                                i++;
                                                            }
                                                        }
                                                        i--;
                                                        String mess = getEmojiByUnicode(0x1F4C1) + ": " + nCart + " / " + getEmojiByUnicode(0x1F4C4) + ": " + nFile + " / " +
                                                                getEmojiByUnicode(0x1F4CE) + ": " + nLink;
                                                        if (prof.length() > 0) {
                                                            prof = ProfDecente(prof);
                                                            CVDataList.add(new CVData(prof, mess, "", 0f));
                                                        }
                                                    }

                                                }
                                            }
                                            break;

                                            case 1: {
                                                String cartella;
                                                int nProf = -1;
                                                boolean stop = false;
                                                for (int i = 0; i < metaElemsDidattica.size(); i++) {
                                                    int nFile, nLink;
                                                    if (metaElemsDidattica.get(i).text().contains("Condivisi da")) {
                                                        i++;
                                                        nProf++;

                                                        if (nProf == didatticaPos[1]) {
                                                            while (i < metaElemsDidattica.size() && !stop) {
                                                                if (metaElemsDidattica.get(i).text().contains("Condivisi da"))
                                                                    stop = true;
                                                                else if (metaElemsDidattica.get(i).className().equals("row row_parent"))  //Cartella
                                                                {
                                                                    Elements data = metaElemsDidattica.get(i).select("td");
                                                                    cartella = data.get(1).text();
                                                                    String condivisione = data.select("span").text();
                                                                    cartella = cartella.replace(condivisione, "").trim();
                                                                    condivisione = condivisione.replace("ultima condivisione:", "").trim();
                                                                    nFile = 0;
                                                                    nLink = 0;
                                                                    if (i + 1 != metaElemsDidattica.size())
                                                                        i++;
                                                                    else stop = true;
                                                                    while (metaElemsDidattica.get(i).className().contains("row contenuto") && !stop) {
                                                                        Elements tmp2 = metaElemsDidattica.get(i).select("span");
                                                                        if (tmp2.get(1).text().equals("file"))
                                                                            nFile++;
                                                                        else if (tmp2.get(1).text().equals("link"))
                                                                            nLink++;

                                                                        if (i + 1 != metaElemsDidattica.size())
                                                                            i++;
                                                                        else
                                                                            stop = true;
                                                                    }
                                                                    String mess = getEmojiByUnicode(0x1F4C4) + ": " + nFile + " / " +
                                                                            getEmojiByUnicode(0x1F4CE) + ": " + nLink + " / " + getEmojiByUnicode(0x1F550) + " " + condivisione;
                                                                    CVDataList.add(new CVData(getEmojiByUnicode(0x1F4C1) + " " + cartella, mess, "", 0f));
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            break;


                                            case 2: {
                                                int nProf = -1, nCart = -1, nFile = 0;
                                                boolean stop = false;
                                                for (int i = 0; i < metaElemsDidattica.size(); i++) {
                                                    if (metaElemsDidattica.get(i).text().contains("Condivisi da")) {
                                                        i++;
                                                        nProf++;

                                                        if (nProf == didatticaPos[1] && !stop) {
                                                            while (!metaElemsDidattica.get(i).text().contains("Condivisi da") && !stop) {
                                                                if (metaElemsDidattica.get(i).className().equals("row row_parent"))  //Cartella
                                                                {
                                                                    nCart++;
                                                                    if (i + 1 != metaElemsDidattica.size())
                                                                        i++;
                                                                    else stop = true;
                                                                    if (nCart == didatticaPos[2]) {

                                                                        while (metaElemsDidattica.get(i).className().contains("row contenuto") && !stop) {
                                                                            Elements tmp2 = metaElemsDidattica.get(i).select("span");
                                                                            FileDid fileDid = new FileDid();
                                                                            fileDid.setType(tmp2.get(1).text());
                                                                            fileDid.setDataInserimento(tmp2.get(2).text().replace("condiviso il:", "").trim());
                                                                            fileDid.setName(tmp2.get(0).text());
                                                                            String tipo = "";
                                                                            if (fileDid.isFile()) {
                                                                                tipo = getEmojiByUnicode(0x1F4C4);
                                                                                Elements fileattr = metaElemsDidattica.get(i).select("div");
                                                                                fileDid.setId(fileattr.attr("contenuto_id"));
                                                                                fileDid.setCksum(fileattr.attr("cksum"));
                                                                            } else if (fileDid.isLink()) {
                                                                                tipo = getEmojiByUnicode(0x1F4CE);
                                                                                Elements link = metaElemsDidattica.get(i).select("div");
                                                                                fileDid.setId(link.get(1).attr("ref"));
                                                                            }
                                                                            fileDid.setPos(nFile);
                                                                            fileDids.add(fileDid);
                                                                            CVDataList.add(new CVData(tipo + " " + fileDid.getName(), getEmojiByUnicode(0x1F550) + " " + fileDid.getDataInserimentoString(), "", 0f));
                                                                            nFile++;
                                                                            if (i + 1 != metaElemsDidattica.size())
                                                                                i++;
                                                                            else
                                                                                stop = true;
                                                                        }

                                                                    }
                                                                }
                                                                if (i + 1 != metaElemsDidattica.size())
                                                                    i++;
                                                                else stop = true;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            break;

                                        }

                                        if (WP_Didattica != null) {
                                            updateDidattica = false;
                                        }
                                        adapter.notifyDataSetChanged();

                                    } else {
                                        CVDataList.clear();
                                        CVDataList.add(new CVData("Nessun Elemento", "Nessun elemento presente nella sezione didattica.", "", 0f));
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }

                        ;
                        m_handlerDidattica.run();

                    }
                    break;


                }

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
            if (scrollState == ScrollState.UP) {
                if (snackbarVotiT != null && currPage == 3) snackbarVotiT.show();

            } else if (scrollState == ScrollState.DOWN) {
                if (snackbarVotiT != null && currPage == 3) snackbarVotiT.dismiss();
            }
        }

        public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {
            List<CVData> CVDataList;

            class ViewHolder extends RecyclerView.ViewHolder {
                CardView cv;
                TextView Title;
                TextView Des;
                TextView Media;
                CircularProgressView pv_circular;
                LinearLayout LayoutData;
                ClockView clockView;
                TextView data;

                ViewHolder(View itemView) {
                    super(itemView);
                    cv = (CardView) itemView.findViewById(R.id.cv);
                    Title = (TextView) itemView.findViewById(R.id.Materia);
                    Des = (TextView) itemView.findViewById(R.id.des);
                    Media = (TextView) itemView.findViewById(R.id.media);
                    pv_circular = (CircularProgressView) itemView.findViewById(R.id.progressvoti);
                    LayoutData = (LinearLayout) itemView.findViewById(R.id.layoutData);
                    clockView = (ClockView) itemView.findViewById(R.id.ClockView);
                    data = (TextView) itemView.findViewById(R.id.dataTx);

                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.v("Premuto", String.valueOf("Elemento:" + getAdapterPosition()) + " Pagina:" + currPage + " Lunghezza lista:" + CVDataList.size());
                            if (currPage <= 2 && CVDataList.size() > 1 && getAdapterPosition() < CVDataList.size() - 1) {
                                Medie medie = new Medie();
                                if (currPage == 0)
                                    medie = MedieVotiMG.get(getAdapterPosition());
                                else if (currPage == 1)
                                    medie = MedieVotiP1.get(getAdapterPosition());
                                else if (currPage == 2)
                                    medie = MedieVotiP2.get(getAdapterPosition());
                                MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                                        .title(getResources().getString(R.string.dettagliovoti, medie.getMateria()))
                                        .theme(Theme.LIGHT)
                                        .iconRes(R.drawable.chartline)
                                        .customView(R.layout.fragment_dettaglio_materia, true)
                                        .build();

                                TextView txOrale = (TextView) dialog.findViewById(R.id.mediaOrale);
                                CircularProgressView pvOrale = (CircularProgressView) dialog.findViewById(R.id.progressMediaOrale);
                                CardView cvOrale = (CardView) dialog.findViewById(R.id.cvOrale);
                                TextView txScritto = (TextView) dialog.findViewById(R.id.mediaScritto);
                                CircularProgressView pvScritto = (CircularProgressView) dialog.findViewById(R.id.progressvMediaScritto);
                                CardView cvScritto = (CardView) dialog.findViewById(R.id.cvScritto);
                                TextView txPratico = (TextView) dialog.findViewById(R.id.mediaPratico);
                                CircularProgressView pvPratico = (CircularProgressView) dialog.findViewById(R.id.progressMediaPratico);
                                CardView cvPratico = (CardView) dialog.findViewById(R.id.cvPratico);

                                double mediaorale = medie.getMediaOrale();
                                double mediascritto = medie.getMediaScritto();
                                double mediapratico = medie.getMediaPratico();
                                if (mediaorale < 10)
                                    txOrale.setText(String.format(Locale.ENGLISH, "%.2f", mediaorale));
                                else
                                    txOrale.setText(String.format(Locale.ENGLISH, "%.1f", mediaorale));
                                if (mediascritto < 10)
                                    txScritto.setText(String.format(Locale.ENGLISH, "%.2f", mediascritto));
                                else
                                    txScritto.setText(String.format(Locale.ENGLISH, "%.1f", mediascritto));
                                if (mediapratico < 10)
                                    txPratico.setText(String.format(Locale.ENGLISH, "%.2f", mediapratico));
                                else
                                    txPratico.setText(String.format(Locale.ENGLISH, "%.1f", mediapratico));
                                pvOrale.setColor(ContextCompat.getColor(context, ColorByMedia((float) mediaorale * 10)));
                                pvOrale.setProgress((float) mediaorale * 10);
                                pvScritto.setColor(ContextCompat.getColor(context, ColorByMedia((float) mediascritto * 10)));
                                pvScritto.setProgress((float) mediascritto * 10);
                                pvPratico.setColor(ContextCompat.getColor(context, ColorByMedia((float) mediapratico * 10)));
                                pvPratico.setProgress((float) mediapratico * 10);
                                if (Double.isNaN(mediaorale))
                                    cvOrale.setVisibility(View.GONE);
                                if (Double.isNaN(mediapratico))
                                    cvPratico.setVisibility(View.GONE);
                                if (Double.isNaN(mediascritto))
                                    cvScritto.setVisibility(View.GONE);

                                dialog.show();
                            } else if (currPage == 6 && CVDataList.size() > 0 && getAdapterPosition() < CVDataList.size()) {

                                if (didatticaPos[0] == 0) {
                                    didatticaPos[0]++;
                                    didatticaPos[1] = getAdapterPosition();
                                    updateDidattica = true;
                                    m_handlerDidattica.run();
                                } else if (didatticaPos[0] == 1) {
                                    didatticaPos[0]++;
                                    didatticaPos[2] = getAdapterPosition();
                                    updateDidattica = true;
                                    m_handlerDidattica.run();
                                } else if (didatticaPos[0] == 2 && fileDids.size() > 0) {
                                    FileDid fileDid = fileDids.get(getAdapterPosition());
                                    if (fileDid.isLink()) {
                                        Uri uri = Uri.parse(fileDid.getId());
                                        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().setShowTitle(true).build();
                                        CustomTabActivityHelper.openCustomTab(getActivity(), customTabsIntent, uri,
                                                new CustomTabActivityHelper.CustomTabFallback() {
                                                    @Override
                                                    public void openUri(Activity activity, Uri uri) {
                                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                        startActivity(intent);
                                                    }
                                                });
                                    } else {
                                        String par = "a=downloadContenuto&contenuto_id=" + fileDid.getId() + "&cksum=" + fileDid.getCksum();
                                        new GetStringFromUrl().execute(BASE_URL + "/cvv/app/default/didattica_genitori.php?" + par);
                                    }

                                }
                            } else if (currPage == 4 && compitishow.size() >= 0) {

                                final int pos = getAdapterPosition();

                                CVData data = CVDataList.get(pos);

                                MyDB DBAgenda = new MyDB(context);
                                final SQLiteDatabase db = DBAgenda.getReadableDatabase();

                                final String command = MyDB.MyCompitoEntry.COLUMN_NAME_AUTORE + "= ? AND "
                                        + MyDB.MyCompitoEntry.COLUMN_NAME_CONTENUTO + "= ?";
                                final String[] datas = new String[]{data.title, data.des};
                                Cursor c = db.rawQuery("select * from " + MyDB.MyCompitoEntry.TABLE_NAME + " where " + command, datas);
                                c.moveToFirst();

                                if (c.getCount() > 0) {
                                    final String autore, inizio, contenuto;
                                    autore = c.getString(c.getColumnIndex(MyDB.MyCompitoEntry.COLUMN_NAME_AUTORE));
                                    inizio = c.getString(c.getColumnIndex(MyDB.MyCompitoEntry.COLUMN_NAME_DATA));
                                    contenuto = c.getString(c.getColumnIndex(MyDB.MyCompitoEntry.COLUMN_NAME_CONTENUTO));
                                    c.close();

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALIAN);
                                    final String dataInserimento = sdf.format(Calendar.getInstance().getTime());
                                    final MaterialDialog modificaDialog = new MaterialDialog.Builder(getContext())
                                            .title(getResources().getString(R.string.aggcal))
                                            .theme(Theme.LIGHT)
                                            .iconRes(R.drawable.calendartoday)
                                            .customView(R.layout.adapter_cust_comp, true)
                                            .positiveText(R.string.salva)
                                            .negativeText(android.R.string.cancel)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                                    EditText Autore = (EditText) dialog.findViewById(R.id.Tit);
                                                    EditText Cont = (EditText) dialog.findViewById(R.id.Cont);

                                                    ContentValues dati = new ContentValues();
                                                    dati.put(MyDB.MyCompitoEntry.COLUMN_NAME_AUTORE, Autore.getText().toString().trim());
                                                    dati.put(MyDB.MyCompitoEntry.COLUMN_NAME_DATA, inizio);
                                                    dati.put(MyDB.MyCompitoEntry.COLUMN_NAME_DATAINSERIMENTO, dataInserimento);
                                                    dati.put(MyDB.MyCompitoEntry.COLUMN_NAME_CONTENUTO, Cont.getText().toString().trim());
                                                    db.update(MyDB.MyCompitoEntry.TABLE_NAME, dati, command, datas);
                                                    db.close();
                                                    compitiDatas.remove(compitishow.get(pos));
                                                    updateAgenda = true;
                                                    m_handlerAgenda.run();
                                                }
                                            }).build();

                                    String[] tmpData = inizio.trim().split("-");
                                    TextView CurrDate = (TextView) modificaDialog.findViewById(R.id.CurrTime);
                                    CurrDate.setText(getString(R.string.eventodel, tmpData[2], tmpData[1], tmpData[0]));
                                    final EditText Autore = (EditText) modificaDialog.findViewById(R.id.Tit);
                                    Autore.setText(autore);
                                    final EditText Cont = (EditText) modificaDialog.findViewById(R.id.Cont);
                                    Cont.setText(contenuto);

                                    Autore.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            modificaDialog.getActionButton(DialogAction.POSITIVE).setEnabled(charSequence.length() > 0 && Cont.length() > 0);
                                        }

                                        @Override
                                        public void afterTextChanged(Editable editable) {

                                        }
                                    });

                                    Cont.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            modificaDialog.getActionButton(DialogAction.POSITIVE).setEnabled(charSequence.length() > 0 && Autore.length() > 0);
                                        }

                                        @Override
                                        public void afterTextChanged(Editable editable) {

                                        }
                                    });

                                    new MaterialDialog.Builder(getContext())
                                            .title(R.string.modificaevento)
                                            .positiveText(R.string.elimina)
                                            .negativeText(android.R.string.cancel)
                                            .neutralText(R.string.modifica)
                                            .theme(Theme.LIGHT)
                                            .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    modificaDialog.show();
                                                }
                                            })
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    db.delete(MyDB.MyCompitoEntry.TABLE_NAME, command, datas);
                                                    db.close();
                                                    compitiDatas.remove(compitishow.get(pos));
                                                    updateAgenda = true;
                                                    m_handlerAgenda.run();
                                                }
                                            }).show();

                                }
                            } else if (currPage == 3 && votis.size() >= 1) {

                                int el = getAdapterPosition();
                                List<Voto> voti = new ArrayList<>();
                                for (Voto vot : votis.get(el).getVoti())
                                    if (!vot.getCommento().equals(""))
                                        voti.add(vot);

                                if (!voti.isEmpty()) {
                                    new MaterialDialog.Builder(getContext())
                                            .title(votis.get(el).getMateria())
                                            .theme(Theme.LIGHT)
                                            .adapter(new VotiDettAdp(getContext(), voti), null)
                                            .show();


                                } else
                                    Snackbar.make(coordinatorLayout, "Nessun commento ai voti della materia selezionata", Snackbar.LENGTH_SHORT).show();
                            }

                        }
                    });

                    itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            if (currPage == 4 && compitishow.size() >= 0) {

                                int el = getAdapterPosition();
                                Compito compito = compitishow.get(el);
                                Intent calIntent = new Intent(Intent.ACTION_INSERT);
                                calIntent.setType("vnd.android.cursor.item/event");
                                calIntent.putExtra(CalendarContract.Events.TITLE, getString(R.string.compitidi, compito.getAutore()));
                                calIntent.putExtra(CalendarContract.Events.DESCRIPTION, compito.getContenuto());
                                calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, compito.isTuttoIlGiorno());
                                calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, compito.getDataInizio().getMillis());
                                calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, compito.getDataFine().getMillis());
                                startActivity(calIntent);

                            } else if (currPage <= 2 && getAdapterPosition() < CVDataList.size() - 1) {
                                final Medie medie;
                                if (currPage == 0)
                                    medie = MedieVotiMG.get(getAdapterPosition());
                                else if (currPage == 1)
                                    medie = MedieVotiP1.get(getAdapterPosition());
                                else
                                    medie = MedieVotiP2.get(getAdapterPosition());
                                MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                                        .title(getResources().getString(R.string.mediaipotetica, CVDataList.get(getAdapterPosition()).title))
                                        .theme(Theme.LIGHT)
                                        .customView(R.layout.fragment_media_ipotetica, true)
                                        .positiveText(android.R.string.ok)
                                        .build();

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getResources().getStringArray(R.array.votispinner));
                                adapter.setDropDownViewResource(R.layout.spinner_item);
                                final CircularProgressView circularProgressView = (CircularProgressView) dialog.findViewById(R.id.progressMediaIpotetica);
                                MaterialSpinner spinner = (MaterialSpinner) dialog.findViewById(R.id.spinner);
                                spinner.setAdapter(adapter);
                                spinner.setSelection(20); //Seleziona il voto 6

                                final TextView mediaIpTx = (TextView) dialog.findViewById(R.id.MediaIpTx);
                                final boolean[] frstime = {true};

                                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if (position >= 0) {
                                            final float[] MediaBack = {Float.parseFloat(mediaIpTx.getText().toString()) * 10f};
                                            String[] votil = getResources().getStringArray(R.array.votis);
                                            double votosel = Double.parseDouble(votil[position]);
                                            final float mediaFl = MediaIpotetica(votosel, medie.getSommaGenerale(), medie.getnVotiGenerale()) * 10f;
                                            circularProgressView.setProgress(mediaFl);
                                            circularProgressView.setColor(ContextCompat.getColor(getContext(), ColorByMedia(mediaFl)));
                                            //circularProgressView.startAnimation();

                                            final Thread thread = new Thread() {

                                                MainActivity mainActivity = new MainActivity();

                                                @Override
                                                public void run() {
                                                    if (MediaBack[0] < mediaFl) {
                                                        while (MediaBack[0] < mediaFl - 0.1f) {
                                                            MediaBack[0] += 0.1f;

                                                            mainActivity.runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    mediaIpTx.setText(String.format(Locale.ENGLISH, "%.2f", MediaBack[0] / 10f));
                                                                }
                                                            });

                                                            try {
                                                                sleep(5);
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    } else if (MediaBack[0] > mediaFl) {
                                                        while (MediaBack[0] > mediaFl + 0.1f) {
                                                            MediaBack[0] -= 0.1f;
                                                            mainActivity.runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    mediaIpTx.setText(String.format(Locale.ENGLISH, "%.2f", MediaBack[0] / 10f));
                                                                }
                                                            });
                                                            try {
                                                                sleep(5);
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                }

                                            };

                                            if (!frstime[0])
                                                thread.start();
                                            else {
                                                mediaIpTx.setText(String.format(Locale.ENGLISH, "%.2f", mediaFl / 10f));
                                                frstime[0] = false;
                                            }


                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                                dialog.show();
                            }


                            return false;
                        }
                    });
                }
            }

            RVAdapter(List<CVData> CVDataList) {
                this.CVDataList = CVDataList;
            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_card, parent, false);
                return new ViewHolder(v);
            }


            @Override
            public void onAttachedToRecyclerView(RecyclerView recyclerView) {
                super.onAttachedToRecyclerView(recyclerView);
            }

            @Override
            public void onBindViewHolder(ViewHolder ViewHolder, int i) {
                if (CVDataList.get(i).title.contains("Nota disciplinare"))  //Se contiene nota disciplinare
                {
                    ViewHolder.Title.setTextColor(ContextCompat.getColor(context, R.color.redmaterial));
                } else
                    ViewHolder.Title.setTextColor(ContextCompat.getColor(context, R.color.material_blue_grey));
                ViewHolder.Title.setText(CVDataList.get(i).title);

                if (CVDataList.get(i).dess != null)
                    ViewHolder.Des.setText(CVDataList.get(i).dess);
                else
                    ViewHolder.Des.setText(CVDataList.get(i).des);
                ViewHolder.Media.setText(CVDataList.get(i).media);

                if (CVDataList.get(i).des.contains("<Agenda>")) //Se contiene "<Data>" fa parte della sezione agenda, quindi la splitto e la imposto
                {
                    CVDataList.get(i).des = CVDataList.get(i).des.replace("<Agenda>", "");
                    ViewHolder.Des.setTextIsSelectable(true);
                    ViewHolder.LayoutData.setVisibility(View.VISIBLE);
                    try {
                        //Comparo le date
                        Compito compito = compitishow.get(i);
                        Days d = Days.daysBetween(compito.getDataInserimento(), compito.getDataInizio());

                        //Imposto l'orologio
                        ViewHolder.clockView.setHour(compito.getDataInserimento().getHourOfDay());
                        ViewHolder.clockView.setMinute(compito.getDataInserimento().getMinuteOfHour());
                        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yy HH:mm:ss");
                        int days = d.getDays();
                        String date = dtf.print(compito.getDataInserimento());
                        SpannableString s;
                        if (days == 0) {
                            s = new SpannableString(getString(R.string.datainsogg, date));
                        } else if (days == 1)
                            s = new SpannableString(getString(R.string.datains, dtf.print(compito.getDataInserimento()), days, "o"));
                        else
                            s = new SpannableString(getString(R.string.datains, dtf.print(compito.getDataInserimento()), days, "i"));

                        s.setSpan(new StyleSpan(Typeface.BOLD), 0, date.length(), 0);
                        ViewHolder.data.setText(s);
                        ViewHolder.Des.setText(CVDataList.get(i).des);
                    } catch (Exception e) {
                        ACRA.getErrorReporter().handleException(e, false);
                    }
                } else {
                    ViewHolder.LayoutData.setVisibility(View.GONE);
                    ViewHolder.Des.setTextIsSelectable(false);
                }

                ViewHolder.pv_circular.setIndeterminate(false);
                if (CVDataList.get(i).prog < 55f && CVDataList.get(i).prog != 0) {
                    ViewHolder.pv_circular.setColor(ContextCompat.getColor(context, R.color.redmaterial));
                    ViewHolder.pv_circular.setVisibility(View.VISIBLE);
                    ViewHolder.Media.setVisibility(View.VISIBLE);
                    if (CVDataList.get(i).title.equals("Media Generale")) {
                        int unicode = 0x1F628; //Faccia impaurita
                        ViewHolder.Des.setText(getEmojiByUnicode(unicode));
                    }
                    ViewHolder.pv_circular.setProgress(CVDataList.get(i).prog);
                } else if (CVDataList.get(i).prog >= 55f && CVDataList.get(i).prog < 60f) {
                    ViewHolder.pv_circular.setColor(ContextCompat.getColor(context, R.color.orangematerial));
                    ViewHolder.pv_circular.setVisibility(View.VISIBLE);
                    ViewHolder.Media.setVisibility(View.VISIBLE);
                    if (CVDataList.get(i).title.equals("Media Generale")) {
                        int unicode = 0x1F44E; //Pollice in giù
                        ViewHolder.Des.setText(getEmojiByUnicode(unicode));
                    }
                    ViewHolder.pv_circular.setProgress(CVDataList.get(i).prog);
                } else if (CVDataList.get(i).prog == 0f) {
                    ViewHolder.pv_circular.setVisibility(View.GONE);
                    ViewHolder.pv_circular.setProgress(1f);
                    ViewHolder.pv_circular.stopAnimation();

                    if (CVDataList.get(i).des.equals("")) //Se la descrizione non c'è la nascondo
                        ViewHolder.Des.setVisibility(View.GONE);
                    else ViewHolder.Des.setVisibility(View.VISIBLE);

                } else {
                    ViewHolder.pv_circular.setColor(ContextCompat.getColor(context, R.color.greenmaterial));
                    ViewHolder.pv_circular.setVisibility(View.VISIBLE);
                    ViewHolder.Media.setVisibility(View.VISIBLE);
                    if (CVDataList.get(i).title.equals("Media Generale")) {
                        int unicode = 0x1F44D; //Pollice in su
                        ViewHolder.Des.setText(getEmojiByUnicode(unicode));
                    }
                    ViewHolder.pv_circular.setProgress(CVDataList.get(i).prog);
                }
            }

            @Override
            public int getItemCount() {
                return CVDataList.size();
            }

        }

    }

    public static void AggiornaFileOffline() {
        if (DownloadFolder != null) {
            File[] files = DownloadFolder.listFiles();
            if (files != null)
                Drawerresult.updateBadge(5, new StringHolder(files.length + ""));
        }
    }

    public static void AggiornaNCircolari(int n) {
        if (n != 0)
            Drawerresult.updateBadge(3, new StringHolder(n + ""));
    }

    public static void AggiornaFileScrutini(int n) {
        if (n != 0)
            Drawerresult.updateBadge(4, new StringHolder(n + ""));
    }

    private static void CaricaVotiOffline() {
        MyDB DBVoti = new MyDB(context);
        SQLiteDatabase db = DBVoti.getReadableDatabase();
        votis.clear();
        LinkedHashMap<String, List<Voto>> hashMap = new LinkedHashMap<>();

        Cursor c = db.rawQuery("select * from " + MyDB.VotoEntry.TABLE_NAME, null);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {

                String materia = c.getString(c.getColumnIndex(MyDB.VotoEntry.COLUMN_NAME_MATERIA));
                Voto voto = new Voto();
                voto.setVoto(c.getString(c.getColumnIndex(MyDB.VotoEntry.COLUMN_NAME_VOTO)));
                boolean VotoBlu = c.getInt(c.getColumnIndex(MyDB.VotoEntry.COLUMN_NAME_VOTOBLU)) == 1;
                voto.setVotoblu(VotoBlu);
                voto.setData(c.getString(c.getColumnIndex(MyDB.VotoEntry.COLUMN_NAME_DATA)));
                voto.setTipo(c.getString(c.getColumnIndex(MyDB.VotoEntry.COLUMN_NAME_TIPO)));
                voto.setPeriodo(c.getString(c.getColumnIndex(MyDB.VotoEntry.COLUMN_NAME_PERIODO)));
                voto.setCommento(c.getString(c.getColumnIndex(MyDB.VotoEntry.COLUMN_NAME_COMMENTO)));

                if (!hashMap.containsKey(materia)) {
                    List<Voto> list = new ArrayList<>();
                    list.add(voto);
                    hashMap.put(materia, list);
                } else {
                    hashMap.get(materia).add(voto);
                }

                c.moveToNext();
            }
        }

        for (Map.Entry<String, List<Voto>> entry : hashMap.entrySet()) {
            Materia materia = new Materia(entry.getKey());
            materia.setVoti(entry.getValue());
            votis.add(materia);
        }

        db.close();
        c.close();
    }


    private static void AggiornaDati() {
        new GetStringFromUrl().execute(BASE_URL + "/auth/app/default/AuthApi2.php?a=aLoginPwd");
        new GetStringFromUrl().execute(BASE_URL + "/cvv/app/default/genitori_note.php");
        new GetStringFromUrl().execute(BASE_URL + "/cvv/app/default/gioprof_note_studente.php");
        new GetStringFromUrl().execute(BASE_URL + "/cvv/app/default/agenda_studenti.php?ope=get_events&start=" + primadata.getMillis() / 1000 + "&end=" + secondadata.getMillis() / 1000);
        new GetStringFromUrl().execute(BASE_URL + "/cvv/app/default/didattica_genitori.php");
        new GetStringFromUrl().execute(BASE_URL + "/sif/app/default/bacheca_utente.php");
        new GetStringFromUrl().execute(BASE_URL + "/sol/app/default/documenti_sol.php");
    }


    @Override
    public void onBackPressed() {

        if (currPage == 6 && didatticaPos[0] > 0) {
            didatticaPos[0]--;
            updateDidattica = true;
            m_handlerDidattica.run();
        } else {
            if (Drawerresult != null)
                if (Drawerresult.isDrawerOpen())
                    Drawerresult.closeDrawer();
                else
                    moveTaskToBack(true);
            else moveTaskToBack(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mTracker.setScreenName("MainActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll(); //Cancello tutte le notifiche dell'app se ci sono
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        SharedPreferences sharedPref = getSharedPreferences("Dati", Context.MODE_PRIVATE);
        menu.getItem(1).setChecked(sharedPref.getBoolean("notifichevoti", true));
        menu.getItem(2).setChecked(sharedPref.getBoolean("notificheagenda", true));
        menu.getItem(3).setChecked(sharedPref.getBoolean("notifichescrutini", true));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.obbiettivobtn: {
                final SharedPreferences sharedPref = getSharedPreferences("Dati", Context.MODE_PRIVATE);
                final int[] obbv = {sharedPref.getInt("obiettivovoto", 20)};
                MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
                        .title(R.string.impostaobb)
                        .theme(Theme.LIGHT)
                        .customView(R.layout.fragment_imposta_obb, true)
                        .positiveText(android.R.string.ok)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt("obiettivovoto", obbv[0]);
                                editor.apply();

                                if (m_handlerMedie != null) {
                                    updateMedie = true;
                                    m_handlerMedie.run();
                                }

                                if (m_handlerQ1 != null) {
                                    updateQ1 = true;
                                    m_handlerQ1.run();
                                }

                                if (m_handlerQ2 != null) {
                                    updateQ2 = true;
                                    m_handlerQ2.run();
                                }
                            }
                        })
                        .build();


                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), R.layout.spinner_item, getResources().getStringArray(R.array.votispinner));
                adapter.setDropDownViewResource(R.layout.spinner_item);
                MaterialSpinner spinner = (MaterialSpinner) dialog.findViewById(R.id.spinner);
                spinner.setAdapter(adapter);
                spinner.setSelection(obbv[0]);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position >= 0) {
                            obbv[0] = position;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                dialog.show();
            }
            break;

            case R.id.notifichevoti: {

                SharedPreferences sharedPref = getSharedPreferences("Dati", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                if (item.isChecked()) {
                    if (sharedPref.getBoolean("notificheagenda", true) || sharedPref.getBoolean("notifichescrutini", true)) {
                        editor.putBoolean("notifichevoti", false);
                    } else {

                        AlarmManager mAlarmManger = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(MainActivity.this, Notifiche.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mAlarmManger.cancel(pendingIntent);
                        editor.putBoolean("notifichevoti", false);
                    }
                    item.setChecked(false);
                } else {
                    if (sharedPref.getBoolean("notificheagenda", true) || sharedPref.getBoolean("notifichescrutini", true)) {
                        editor.putBoolean("notifichevoti", true);
                    } else {

                        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(this, Notifiche.class);
                        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, CONTROLLO_VOTI_ID, intent, 0);

                        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
                        editor.putBoolean("notifichevoti", true);
                    }
                    item.setChecked(true);
                }

                editor.apply();


            }
            break;

            case R.id.notificheagenda: {

                SharedPreferences sharedPref = getSharedPreferences("Dati", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                if (item.isChecked()) {
                    if (sharedPref.getBoolean("notifichevoti", true) || sharedPref.getBoolean("notifichescrutini", true)) {
                        editor.putBoolean("notificheagenda", false);
                    } else {

                        AlarmManager mAlarmManger = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(MainActivity.this, Notifiche.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mAlarmManger.cancel(pendingIntent);
                        editor.putBoolean("notificheagenda", false);
                    }

                    item.setChecked(false);
                } else {
                    if (sharedPref.getBoolean("notificheavoti", true) || sharedPref.getBoolean("notifichescrutini", true)) {
                        editor.putBoolean("notificheagenda", true);
                    } else {

                        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(this, Notifiche.class);
                        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, CONTROLLO_VOTI_ID, intent, 0);

                        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
                        editor.putBoolean("notificheagenda", true);
                    }
                    item.setChecked(true);
                }

                editor.apply();

            }
            break;

            case R.id.notifichescrutini: {
                SharedPreferences sharedPref = getSharedPreferences("Dati", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                if (item.isChecked()) {
                    if (sharedPref.getBoolean("notifichevoti", true) || sharedPref.getBoolean("notificheagenda", true)) {
                        editor.putBoolean("notifichescrutini", false);
                    } else {

                        AlarmManager mAlarmManger = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(MainActivity.this, Notifiche.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mAlarmManger.cancel(pendingIntent);
                        editor.putBoolean("notifichescrutini", false);
                        editor.putInt("NFileScrutini", -1);
                    }

                    item.setChecked(false);
                } else {
                    if (sharedPref.getBoolean("notifichevoti", true) || sharedPref.getBoolean("notificheagenda", true)) {
                        editor.putBoolean("notifichescrutini", true);
                    } else {

                        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(this, Notifiche.class);
                        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, CONTROLLO_VOTI_ID, intent, 0);

                        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
                        editor.putBoolean("notifichescrutini", true);
                    }
                    item.setChecked(true);
                }

                editor.apply();
            }
            break;


            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
