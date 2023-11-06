package com.example.storageanalyser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {
    private List<AppInfo> appList;

    public AppAdapter(List<AppInfo> appList) {
        this.appList = appList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AppInfo appInfo = appList.get(position);
        holder.appName.setText(appInfo.getName());
        holder.appIcon.setImageDrawable(appInfo.getIcon());
    }
    @Override
    public int getItemCount() {
        return appList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView appName;
        public ImageView appIcon;

        public ViewHolder(View view) {
            super(view);
            //appName = view.findViewById(R.id.app_name);
            //appIcon = view.findViewById(R.id.app_icon);
        }
    }
}