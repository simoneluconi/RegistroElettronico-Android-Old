package com.sharpdroid.registroelettronico;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;

import java.io.File;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.support.v4.content.FileProvider.getUriForFile;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ConvertiDimensione;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.FileImage;

public class FileOffline extends AppCompatActivity {

    public static RVAdapter adapter;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_offline);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.CoordinatorFileOff);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(getString(R.string.fileoff));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        File[] files = MainActivity.DownloadFolder.listFiles();
        adapter = new RVAdapter(files);
        ObservableRecyclerView rv = (ObservableRecyclerView) findViewById(R.id.OfflineFileList);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(FileOffline.this);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);

        if (files.length == 0)
            Snackbar.make(coordinatorLayout, "Non hai ancora nessun file scaricato offline", Snackbar.LENGTH_INDEFINITE).show();
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {
        File[] files;

        RVAdapter(File[] files) {
            this.files = files;
        }

        @Override
        public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_file_offline, parent, false);
            return new PersonViewHolder(v);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {

            File file = files[i];
            String filename = file.getName().substring(0, file.getName().lastIndexOf("."));

            personViewHolder.Nome.setText(filename);
            personViewHolder.Ext.setImageBitmap(FileImage(String.valueOf(file.getName().substring(file.getName().lastIndexOf(".")))));
            personViewHolder.Size.setText(ConvertiDimensione(file.length()));
            Date date = new Date(file.lastModified());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.ITALIAN);
            personViewHolder.LastModified.setText(sdf.format(date));

        }

        @Override
        public int getItemCount() {
            return files.length;
        }

        class PersonViewHolder extends RecyclerView.ViewHolder {
            CardView cv;
            ImageView Ext;
            TextView Nome;
            TextView LastModified;
            TextView Size;

            PersonViewHolder(View itemView) {
                super(itemView);
                cv = (CardView) itemView.findViewById(R.id.FileOfflineCv);
                Ext = (ImageView) itemView.findViewById(R.id.FileIcon);
                Nome = (TextView) itemView.findViewById(R.id.FileName);
                LastModified = (TextView) itemView.findViewById(R.id.FileLastModified);
                Size = (TextView) itemView.findViewById(R.id.FileSize);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (files.length != 0) {
                            File myFile = files[getAdapterPosition()];
                            try {
                                String mime = URLConnection.guessContentTypeFromName(myFile.getName());
                                Intent myIntent = new Intent(Intent.ACTION_VIEW);
                                Uri uri = getUriForFile(FileOffline.this, MainActivity.FILE_PROVIDER_STRING, myFile);
                                myIntent.setDataAndType(uri, mime);
                                myIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
                                FileOffline.this.startActivity(myIntent);
                            } catch (android.content.ActivityNotFoundException e) {
                                Snackbar.make(coordinatorLayout, "Nessuna app per aprire il file: " + myFile.getName(), Snackbar.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

        }

    }

}
