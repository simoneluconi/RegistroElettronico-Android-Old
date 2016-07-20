package com.sharpdroid.registroelettronico;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Azione;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Compito;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.MyDB;

import org.acra.ACRA;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ConvertiCompito;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ConvertiInVoto;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.MateriaDecente;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.getPostDataString;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.isNetworkAvailable;

public class Notifiche extends BroadcastReceiver {


    CookieManager msCookieManager = new CookieManager();
    Context ct;
    int nNotif = 0;

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharePref = context.getSharedPreferences("Dati", Context.MODE_PRIVATE);
        ct = context;
        Log.v("Notifiche", "Inizio download...");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 7);

        String primadata, secondadata;
        int mm = Calendar.getInstance().get(Calendar.MONTH) + 1;
        if (mm >= 9 && mm <= 12) {
            primadata = String.valueOf(Calendar.getInstance().get(Calendar.YEAR)) + "-09-01";
            secondadata = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) + 1) + "-09-01";
        } else {
            primadata = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 1) + "-09-01";
            secondadata = String.valueOf(Calendar.getInstance().get(Calendar.YEAR)) + "-09-01";
        }
        if (isNetworkAvailable(context) && sharePref.getBoolean("Acceduto", false)) {
            new GetStringFromUrl().execute("https://web.spaggiari.eu/home/app/default/login.php");

            if (sharePref.getBoolean("notifichevoti", true))
                new GetStringFromUrl().execute("https://web.spaggiari.eu/cvv/app/default/genitori_note.php");

            if (sharePref.getBoolean("notificheagenda", true))
                new GetStringFromUrl().execute("https://web.spaggiari.eu/cvv/app/default/xml_export.php?stampa=%3Astampa%3A&tipo=agenda&tipo_export=EVENTI_AGENDA_STUDENTI&ope=RPT&dal=" + primadata + "&al=" + secondadata + "&formato=html");

            if (sharePref.getBoolean("notifichescrutini", true))
                new GetStringFromUrl().execute("https://web.spaggiari.eu/sol/app/default/documenti_sol.php");
        }

    }


    public class GetStringFromUrl extends AsyncTask<String, Void, String> {

        String azione = "";

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
            SharedPreferences sharedPref = ct.getSharedPreferences("Dati", Context.MODE_PRIVATE);

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


                if (params[0].equals("https://web.spaggiari.eu/cvv/app/default/genitori_note.php"))
                    azione = Azione.VOTI;
                else if (params[0].contains("xml_export.php"))
                    azione = Azione.AGENDA;
                else if (params[0].equals("https://web.spaggiari.eu/sol/app/default/documenti_sol.php"))
                    azione = Azione.SCRUTINI;

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

            if (result != null && result.length() > 0) {
                switch (azione) {
                    case Azione.VOTI:

                        Document doc = Jsoup.parse(result);
                        Elements metaElems = doc.select("tr");
                        NotificationManagerCompat notificationManager;
                        NotificationCompat.Builder mBuilder;

                        SQLiteDatabase db = new MyDB(ct).getWritableDatabase();
                        boolean notifica = true;

                        Cursor c = db.rawQuery("SELECT count(*) FROM " + MyDB.VotoEntry.TABLE_NAME, null);
                        c.moveToFirst();
                        int icount = c.getInt(0);
                        if (icount <= 0)
                            notifica = false;
                        c.close();
                        db.beginTransaction();
                        for (int i = 0; i < metaElems.size(); i++) {

                            if (metaElems.get(i).select("td").get(0).className().contains(MainActivity.SEPARATORE_MATERIE)) {
                                String materia = MateriaDecente(metaElems.get(i).text().trim());
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

                                    String fakebool = VotoBlu ? "1" : "0";
                                    String[] datas = new String[]{materia, voto, fakebool, data, periodo, tipo};
                                    String command = MyDB.VotoEntry.COLUMN_NAME_MATERIA + "= ? AND "
                                            + MyDB.VotoEntry.COLUMN_NAME_VOTO + "= ? AND "
                                            + MyDB.VotoEntry.COLUMN_NAME_VOTOBLU + "= ? AND "
                                            + MyDB.VotoEntry.COLUMN_NAME_DATA + "= ? AND "
                                            + MyDB.VotoEntry.COLUMN_NAME_PERIODO + "= ? AND "
                                            + MyDB.VotoEntry.COLUMN_NAME_TIPO + "= ?";

                                    c = db.rawQuery("select * from " + MyDB.VotoEntry.TABLE_NAME + " where " + command, datas);

                                    if (c.getCount() <= 0) {
                                        ContentValues dati = new ContentValues();
                                        dati.put(MyDB.VotoEntry.COLUMN_NAME_MATERIA, materia);
                                        dati.put(MyDB.VotoEntry.COLUMN_NAME_DATA, data);
                                        dati.put(MyDB.VotoEntry.COLUMN_NAME_TIPO, tipo);
                                        dati.put(MyDB.VotoEntry.COLUMN_NAME_VOTOBLU, VotoBlu);
                                        dati.put(MyDB.VotoEntry.COLUMN_NAME_VOTO, voto);
                                        dati.put(MyDB.VotoEntry.COLUMN_NAME_PERIODO, periodo);
                                        dati.put(MyDB.VotoEntry.COLUMN_NAME_COMMENTO, commento);
                                        db.insert(MyDB.VotoEntry.TABLE_NAME, MyDB.VotoEntry.COLUMN_NAME_NULLABLE, dati);

                                        if (notifica) {
                                            Intent intent = new Intent(ct, MainActivity.class);
                                            intent.putExtra("com.sharpdroid.registroelettronico.notifiche.TAB", 3);
                                            PendingIntent pIntent = PendingIntent.getActivity(ct, 0, intent, 0);
                                            Log.v("NuovoVoto", "Nuovo voto trovato");
                                            mBuilder = new NotificationCompat.Builder(ct)
                                                    .setSmallIcon(R.drawable.notification)
                                                    .setContentTitle(voto + " in " + materia)
                                                    .setContentText("Il " + data + " (" + tipo + ")")
                                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                    .setLights(Color.BLUE, 3000, 3000)
                                                    .setVibrate(new long[]{250, 250, 250, 250, 250, 250})
                                                    .setContentIntent(pIntent)
                                                    .setAutoCancel(true);

                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                double votod = ConvertiInVoto(voto);

                                                if (votod >= 6)
                                                    mBuilder.setColor(ContextCompat.getColor(ct, R.color.greenmaterial));
                                                else if (votod < 6 && votod >= 5.5)
                                                    mBuilder.setColor(ContextCompat.getColor(ct, R.color.orangematerial));
                                                else if (votod != -1)
                                                    mBuilder.setColor(ContextCompat.getColor(ct, R.color.redmaterial));
                                                else
                                                    mBuilder.setColor(ContextCompat.getColor(ct, R.color.bluematerial));


                                                notificationManager = NotificationManagerCompat.from(ct);
                                                notificationManager.notify(nNotif, mBuilder.build());
                                                nNotif++;
                                            }
                                        }
                                    }

                                    c.close();

                                    if (i + 1 != metaElems.size())
                                        i++;
                                    else esci = true;

                                }
                                i--;
                            }
                        }
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        db.close();

                        break;


                    case Azione.AGENDA: {

                        try {
                            JSONArray jsonCompiti = new JSONArray(result);
                            db = new MyDB(ct).getWritableDatabase();

                            notifica = true;
                            c = db.rawQuery("SELECT count(*) FROM " + MyDB.CompitoEntry.TABLE_NAME, null);
                            c.moveToFirst();
                            icount = c.getInt(0);
                            if (icount <= 0)
                                notifica = false;
                            c.close();

                            db.beginTransaction();
                            for (int i = 0; i < jsonCompiti.length(); i++) {
                                Compito compito = ConvertiCompito(jsonCompiti.getJSONObject(i));

                                if (compito != null) {
                                    String fakstring = compito.isTuttoIlGiorno() ? "1" : "0";
                                    String[] datas = new String[]{compito.getAutore(), compito.getContenuto(), compito.getDataInizioString(), compito.getDataFineString(), fakstring, compito.getDataInserimentoString()};
                                    String command = MyDB.CompitoEntry.COLUMN_NAME_AUTORE + "= ? AND "
                                            + MyDB.CompitoEntry.COLUMN_NAME_CONTENUTO + "= ? AND "
                                            + MyDB.CompitoEntry.COLUMN_NAME_DATAINIZIO + "= ? AND "
                                            + MyDB.CompitoEntry.COLUMN_NAME_DATAFINE + "= ? AND "
                                            + MyDB.CompitoEntry.COLUMN_NAME_TUTTOILGIORNO + "= ? AND "
                                            + MyDB.CompitoEntry.COLUMN_NAME_DATAINSERIMENTO + "= ?";

                                    c = db.rawQuery("select * from " + MyDB.CompitoEntry.TABLE_NAME + " where " + command, datas);

                                    if (c.getCount() <= 0) {
                                        ContentValues dati = new ContentValues();
                                        dati.put(MyDB.CompitoEntry.COLUMN_NAME_AUTORE, compito.getAutore());
                                        dati.put(MyDB.CompitoEntry.COLUMN_NAME_DATAINIZIO, compito.getDataInizioString());
                                        dati.put(MyDB.CompitoEntry.COLUMN_NAME_DATAFINE, compito.getDataFineString());
                                        dati.put(MyDB.CompitoEntry.COLUMN_NAME_DATAINSERIMENTO, compito.getDataInserimentoString());
                                        dati.put(MyDB.CompitoEntry.COLUMN_NAME_CONTENUTO, compito.getContenuto());
                                        dati.put(MyDB.CompitoEntry.COLUMN_NAME_TUTTOILGIORNO, fakstring);
                                        db.insert(MyDB.CompitoEntry.TABLE_NAME, MyDB.CompitoEntry.COLUMN_NAME_NULLABLE, dati);

                                        if (notifica) {
                                            String[] dataDecente = compito.getDataInserimentoString().split("-");
                                            String dataSeria = dataDecente[2] + "/" + dataDecente[1] + "/" + dataDecente[0];
                                            Intent intent = new Intent(ct, MainActivity.class);
                                            intent.putExtra("com.sharpdroid.registroelettronico.notifiche.TAB", 4);
                                            intent.putExtra("com.sharpdroid.registroelettronico.notifiche.DATACAL", compito.getDataInserimentoString().split("\\s+")[0]);
                                            PendingIntent pIntent = PendingIntent.getActivity(ct, 0, intent, 0);
                                            Log.v("NuovoCompito", "Nuovo compito trovato");
                                            mBuilder = new NotificationCompat.Builder(ct)
                                                    .setSmallIcon(R.drawable.notification)
                                                    .setContentTitle("Nuovo compito di " + compito.getAutore())
                                                    .setContentText("Per il " + dataSeria)
                                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                    .setLights(Color.BLUE, 3000, 3000)
                                                    .setVibrate(new long[]{250, 250, 250, 250, 250, 250})
                                                    .setContentIntent(pIntent)
                                                    .setStyle(new NotificationCompat.BigTextStyle()
                                                            .bigText(compito.getContenuto() + " (" + dataSeria + ")"))
                                                    .setAutoCancel(true);


                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                mBuilder.setColor(ContextCompat.getColor(ct, R.color.bluematerial));
                                            }

                                            notificationManager = NotificationManagerCompat.from(ct);
                                            notificationManager.notify(nNotif, mBuilder.build());
                                            nNotif++;
                                        }
                                    }
                                }
                                c.close();
                            }

                            db.setTransactionSuccessful();
                            db.endTransaction();
                            db.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                    case Azione.SCRUTINI: {

                        Elements scrutini = Jsoup.parse(result).select("table#table_documenti");
                        if (scrutini.size() != 0) {

                            int nFile = scrutini.get(0).select("tr").size();
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

                            SharedPreferences sharePref = ct.getSharedPreferences("Dati", Context.MODE_PRIVATE);
                            int OldNFileScrutini = sharePref.getInt("NFileScrutini", -1);
                            SharedPreferences.Editor sharededitor = sharePref.edit();
                            sharededitor.putInt("NFileScrutini", nFile);
                            sharededitor.apply();
                            if (OldNFileScrutini != -1) {
                                if (OldNFileScrutini != nFile) {

                                    Intent intent = new Intent(ct, Scrutini.class);
                                    PendingIntent pIntent = PendingIntent.getActivity(ct, 0, intent, 0);
                                    Log.v("NuovoFileScrutini", "Nuovo file scrutini");
                                    mBuilder = new NotificationCompat.Builder(ct)
                                            .setSmallIcon(R.drawable.notification)
                                            .setContentTitle("Scrutini")
                                            .setContentText("Ci sono nuovi file nella sezione scrutini")
                                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                            .setLights(Color.BLUE, 3000, 3000)
                                            .setVibrate(new long[]{250, 250, 250, 250, 250, 250})
                                            .setContentIntent(pIntent)
                                            .setAutoCancel(true);

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        mBuilder.setColor(ContextCompat.getColor(ct, R.color.bluematerial));
                                    }

                                    notificationManager = NotificationManagerCompat.from(ct);
                                    notificationManager.notify(nNotif, mBuilder.build());
                                    nNotif++;
                                }

                            }
                        }

                    }
                    break;
                }
            }


        }

    }
}
