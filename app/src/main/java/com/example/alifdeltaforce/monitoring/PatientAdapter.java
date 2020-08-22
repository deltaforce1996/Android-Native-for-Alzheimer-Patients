package com.example.alifdeltaforce.monitoring;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static android.R.id.list;

/**
 * Created by Alif Delta Force on 9/7/2017.
 */

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViweHolder> {

    private List<PatientInformation> list;

    public PatientAdapter(List<PatientInformation> list) {
        this.list = list;
    }

    @Override
    public PatientViweHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PatientViweHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final PatientViweHolder holder, int position) {

        PatientInformation patientInformation = list.get(position);

        holder.S_pid.setText(patientInformation.Pid);
        holder.S_pname.setText(patientInformation.Pname);
        holder.S_page.setText(patientInformation.Page);

        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//                contextMenu.add(holder.getAdapterPosition(),0,0,"UPDATE");
                contextMenu.add(holder.getAdapterPosition(),1,0,"DELETE");
            }
        });
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    class PatientViweHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView S_pid;
        private TextView S_pname;
        private TextView S_page;

        public PatientViweHolder(View itemView) {

            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.image);
            S_pid = (TextView) itemView.findViewById(R.id.P_id);
            S_pname = (TextView) itemView.findViewById(R.id.P_name);
            S_page =(TextView) itemView.findViewById(R.id.P_age);
        }

    }

}
