package org.techtown.singlediary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder>{
    ArrayList<Diary> items = new ArrayList<>();
    private boolean isContentBase = true;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;
        if(viewType == R.layout.diary_item1)
            itemView = inflater.inflate(R.layout.diary_item1, parent, false);
        else
            itemView = inflater.inflate(R.layout.diary_item2, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return isContentBase ? R.layout.diary_item1 : R.layout.diary_item2;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Diary item = items.get(position);
        holder.setItem(item);
    }

    public void setState(boolean state){
        isContentBase = state;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Diary item){
        items.add(item);
    }

    public void setItems(ArrayList<Diary> items){
        this.items = items;
    }

    public Diary getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, Diary item){
        items.set(position, item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView conditionIcon;
        ImageView weatherIcon;
        ImageView photo;
        TextView date;
        TextView content;
        TextView address;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            conditionIcon = itemView.findViewById(R.id.conditionIcon);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
            photo = itemView.findViewById(R.id.photo);
            date = itemView.findViewById(R.id.dateText);
            content = itemView.findViewById(R.id.content);
            address = itemView.findViewById(R.id.address);
        }

        public void setItem(Diary item){
            switch(item.getWeather()){
                case 1:
                    weatherIcon.setImageResource(R.drawable.weather_1);
                    break;
                case 2:
                    weatherIcon.setImageResource(R.drawable.weather_2);
                    break;
                case 3:
                    weatherIcon.setImageResource(R.drawable.weather_3);
                    break;
                case 4:
                    weatherIcon.setImageResource(R.drawable.weather_4);
                    break;
                case 5:
                    weatherIcon.setImageResource(R.drawable.weather_5);
                    break;
                case 6:
                    weatherIcon.setImageResource(R.drawable.weather_6);
                    break;
                case 7:
                    weatherIcon.setImageResource(R.drawable.weather_7);
                    break;
            }

            switch(item.getCondition()){
                case 1:
                    conditionIcon.setImageResource(R.drawable.smile1_24);
                    break;
                case 2:
                    conditionIcon.setImageResource(R.drawable.smile2_24);
                    break;
                case 3:
                    conditionIcon.setImageResource(R.drawable.smile3_24);
                    break;
                case 4:
                    conditionIcon.setImageResource(R.drawable.smile4_24);
                    break;
                case 5:
                    conditionIcon.setImageResource(R.drawable.smile5_24);
                    break;
            }
            date.setText(item.getDate());
            address.setText(item.getAddress());
            photo.setImageBitmap(item.getPhoto());
            content.setText(item.getContent());
        }
    }
}
