package com.sharpdroid.registroelettronico.SharpLibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Voto;

import java.util.ArrayList;
import java.util.List;

import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ConvertiInVoto;
import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.VotoValido;

public class VotiDettAdp extends BaseAdapter implements View.OnClickListener {

    private final Context mContext;
    private List<Voto> votiDetts = new ArrayList<>();

    public VotiDettAdp(Context context, List<Voto> votiDett) {
        this.mContext = context;
        this.votiDetts = votiDett;
    }

    @Override
    public int getCount() {
        return votiDetts.size();
    }

    @Override
    public String getItem(int position) {
        return votiDetts.get(position).getCommento();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = View.inflate(mContext, R.layout.voti_dett, null);

        TextView txVoto = (TextView) convertView.findViewById(R.id.textVoto);
        Voto votot = votiDetts.get(position);
        txVoto.setText(votot.getVoto());
        double voto = ConvertiInVoto(votot.getVoto());

        if (voto >= 6)
            txVoto.setTextColor(ContextCompat.getColor(mContext, R.color.greenmaterial));
        else if (voto < 6 && voto >= 5.5)
            txVoto.setTextColor(ContextCompat.getColor(mContext, R.color.orangematerial));
        else if (voto != -1)
            txVoto.setTextColor(ContextCompat.getColor(mContext, R.color.redmaterial));
        else txVoto.setTextColor(ContextCompat.getColor(mContext, R.color.bluematerial));

        ((TextView) convertView.findViewById(R.id.datatipo)).setText(String.format("%1$s - %2$s", votot.getData(),votot.getTipo()));
        ((TextView) convertView.findViewById(R.id.commento)).setText(votiDetts.get(position).getCommento());
        return convertView;
    }

    @Override
    public void onClick(View v) {
        Integer index = (Integer) v.getTag();
    }
}
