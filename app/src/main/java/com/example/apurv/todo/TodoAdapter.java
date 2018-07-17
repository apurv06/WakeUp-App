package com.example.apurv.todo;

import android.content.Context;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Apurv on 7/5/2018.
 */

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ListViewHolder> {

    Context mContext;
    CustomItemLongClickListener listener;
    CustomItemListner clickListner;
    ArrayList<todo> list;

    public TodoAdapter(ArrayList<todo> object,Context mContext,CustomItemLongClickListener listener,CustomItemListner clickListner) {
        list=object;
        this.clickListner=clickListner;
        this.mContext = mContext;
        this.listener = listener;
    }

    @NonNull
    @Override



    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.row_layout,parent,false);
        final  ListViewHolder mViewHolder=new ListViewHolder(view);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onItemLongClick(view,mViewHolder.getAdapterPosition());
                return true;
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListner.onClickListener(view,mViewHolder.getAdapterPosition());
            }
        });
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

        String title=list.get(position).getName();
        holder.tv1.setText(title);
        holder.tv2.setText(list.get(position).getDate());
        holder.tv3.setText(list.get(position).getTime());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView tv1;
        TextView tv2;
        TextView tv3;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            tv1=itemView.findViewById(R.id.title);
            tv2=itemView.findViewById(R.id.date);
            tv3=itemView.findViewById(R.id.time);
        }


    }
}
