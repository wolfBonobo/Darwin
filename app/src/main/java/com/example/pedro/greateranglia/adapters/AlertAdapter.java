package com.example.pedro.greateranglia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.pedro.greateranglia.objects.Alert;
import com.example.pedro.greateranglia.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Pedro on 16/04/2018.
 */

public class AlertAdapter extends BaseAdapter {

    Context context; //contexto de la aplicacion
    ArrayList<Alert> alerts;

    public AlertAdapter(Context contexto, ArrayList<Alert> alerts) {

        this.context = contexto;
        this.alerts = alerts;
    }

    @Override
    public int getCount() {
        return alerts.size();
    }

    @Override
    public Alert getItem(int i) {
        return alerts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi=view;
        LayoutInflater inflate = LayoutInflater.from(context);


        vi=inflate.inflate(R.layout.layout_adapter, null);
        TextView alarm=vi.findViewById(R.id.starting);
        TextView days=vi.findViewById(R.id.days);
        TextView forTrains=vi.findViewById(R.id.forTrains);
        TextView leaving=vi.findViewById(R.id.leaving);

        Alert alert=alerts.get(i);
        try {
            JSONObject al = new JSONObject(alert.getAlert());

        String starting="Alert starting at "+al.getString("startAlert");
            JSONArray daysN=(JSONArray)  al.get("days");
            System.out.println(daysN.toString());
        String setOn="Set on: ";

        for(int j=0;j<daysN.length();j++) {
            System.out.println(setOn);
            if(daysN.getString(j).equals("1")){
                setOn+="MON ";
            }else if(daysN.getString(j).equals("2")){
                setOn+="TUE ";
            }else if(daysN.getString(j).equals("3")){
                setOn+="WED ";
            }else if(daysN.getString(j).equals("4")){
                setOn+="THU ";
            }else if(daysN.getString(j).equals("5")){
                setOn+="FRI ";
            }else if(daysN.getString(j).equals("6")){
                setOn+="SAT ";
            }else if(daysN.getString(j).equals("7")){
                setOn+=" SUN";
            }
        }
        String trains="From  "+ al.getString("startName")+" To "+al.getString("endName");
        String time="Leaving between "+al.getString("startJourney") +" and "+ al.getString("endJourney") ;

            alarm.setText(starting);
            days.setText(setOn);
            forTrains.setText(trains);
            leaving.setText(time);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return vi;
    }
}
