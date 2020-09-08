package com.jimmy.ebac_estimate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.jimmy.ebac_estimate.RoomDataBase.Data;
import com.jimmy.ebac_estimate.RoomDataBase.DataBase;
import com.jimmy.ebac_estimate.function.CalendarStringConvert;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ViewDrinkActivity extends AppCompatActivity {
    CalendarStringConvert Convert = new CalendarStringConvert();
    MyAdapter myAdapter;
    Calendar viewDate;
    DatePickerDialog datePickerDialog;
    List<Data> data;
    SimpleDateFormat DateFormatter = new SimpleDateFormat("Y年M月d日 EEEE");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_drink);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_drink);
        toolbar.setTitle("瀏覽酒杯");


        TextView bt_viewDate = findViewById(R.id.button_view_date);
        ImageView img_forward = findViewById(R.id.imageView_forward);
        ImageView img_backward = findViewById(R.id.imageView_backward);



        viewDate = Calendar.getInstance();
        viewDate.set(Calendar.HOUR_OF_DAY,0);
        viewDate.set(Calendar.MINUTE,0);
        viewDate.set(Calendar.SECOND,0);

        String viewDate_s = Convert.CalendarToString(viewDate);
        bt_viewDate.setText(DateFormatter.format(viewDate.getTime()));

        /**set date picker*/
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar selectDate = Calendar.getInstance();
                selectDate.set(year, month, dayOfMonth,0,0,0);
                viewDate = selectDate;
                String viewDate_s = Convert.CalendarToString(viewDate);
                bt_viewDate.setText(DateFormatter.format(viewDate.getTime()));
                myAdapter.getFilter().filter(viewDate_s);
            }

        },viewDate.get(Calendar.YEAR),viewDate.get(Calendar.MONTH),viewDate.get(Calendar.DAY_OF_MONTH));

        img_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDate.add(Calendar.DAY_OF_MONTH,-1);
                String viewDate_s = Convert.CalendarToString(viewDate);
                bt_viewDate.setText(DateFormatter.format(viewDate.getTime()));
                myAdapter.getFilter().filter(viewDate_s);
                datePickerDialog.updateDate(viewDate.get(Calendar.YEAR),viewDate.get(Calendar.MONTH),viewDate.get(Calendar.DAY_OF_MONTH));
            }
        });

        img_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDate.add(Calendar.DAY_OF_MONTH,1);
                String viewDate_s = Convert.CalendarToString(viewDate);
                bt_viewDate.setText(DateFormatter.format(viewDate.getTime()));
                myAdapter.getFilter().filter(viewDate_s);
                datePickerDialog.updateDate(viewDate.get(Calendar.YEAR),viewDate.get(Calendar.MONTH),viewDate.get(Calendar.DAY_OF_MONTH));
            }
        });


        /**create recycler view*/
        RecyclerView recyclerView = findViewById(R.id.ViewDrink_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//設置分隔線

        new Thread(() -> {
            data = DataBase.getInstance(this).getDataUao().displayAllByTime();
            runOnUiThread(() -> {
                myAdapter = new MyAdapter(this, data);
                recyclerView.setAdapter(myAdapter);
                myAdapter.getFilter().filter(viewDate_s);

                /**===============================================================================*/
                myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {//原本的樣貌
                    @Override
                    public void onItemClick(Data myData) {
                        Intent intent = new Intent(ViewDrinkActivity.this, ModifyDrink.class);
                        intent.putExtra("DrinkName", myData.getDrinkName());
                        intent.putExtra("Id", myData.getId());
                        intent.putExtra("DrinkAbv", myData.getDrinkAbv());
                        intent.putExtra("DrinkML", myData.getDrinkML());
                        intent.putExtra("startDrinkTime", Convert.CalendarToString(myData.getStartDrinkTime()));
                        intent.putExtra("SoberDrinkTime", Convert.CalendarToString(myData.getSoberDrinkTime()));
                        startActivity(intent);
                    }
                });
            });
        }).start();
        /**=======================================================================================*/
    }


    /**adapter is bind with data, so when data changes refresh the data then notify adapter*/
    @Override
    public void onResume() {
        super.onResume();
        if(myAdapter != null){
            new Thread(() -> {
                data = DataBase.getInstance(this).getDataUao().displayAllByTime();
                String viewDate_s = Convert.CalendarToString(viewDate);
                myAdapter.getFilter().filter(viewDate_s);
                runOnUiThread(() -> {
                    myAdapter.notifyDataSetChanged();
                        });
            }).start();
        }
    }

    public void clickViewDate(View v) { datePickerDialog.show(); }



    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements Filterable {

        private List<Data> myData;
        private List<Data> myDataFull;
        private Activity activity;
        private OnItemClickListener onItemClickListener;
        private CalendarStringConvert Convert = new CalendarStringConvert();

        public MyAdapter(Activity activity, List<Data> myData) {
            this.activity = activity;
            this.myData = myData;
            myDataFull = new ArrayList<>(myData);
        }
        /**建立對外接口*/
        public void setOnItemClickListener(OnItemClickListener onItemClickListener){
            this.onItemClickListener = onItemClickListener;
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            View view;
            TextView view_DrinkName,view_Abv,view_Ml,view_Time;
            ImageView view_delete;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                view_DrinkName = itemView.findViewById(R.id.text_view_DrinkName);
                view_Abv = itemView.findViewById(R.id.text_view_Abv);
                view_Ml = itemView.findViewById(R.id.text_view_Ml);
                view_Time = itemView.findViewById(R.id.text_view_Time);
                view = itemView;
                view_delete = itemView.findViewById(R.id.img_delete);
            }
        }
        /**刪除資料*/
        public void deleteData(int position){
            new Thread(()->{
                DataBase.getInstance(activity).getDataUao().deleteData(myData.get(position).getId());
                this.myData.remove(position);
                this.myDataFull.remove(position);
                activity.runOnUiThread(()->{
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, myData.size() - position);
                });
            }).start();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_drink,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.view_DrinkName.setText(myData.get(position).getDrinkName());
            holder.view_Abv.setText(myData.get(position).getDrinkAbv() + " %");
            holder.view_Ml.setText(myData.get(position).getDrinkML() + " ml");
            holder.view_Time.setText(Convert.CalendarToString(myData.get(position).getStartDrinkTime()));
            holder.view.setOnClickListener((v)->{
                onItemClickListener.onItemClick(myData.get(position));
            });

            /**set delete image listener*/
            holder.view_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteData(position);
                }
            });

        }
        @Override
        public int getItemCount() {
            return myData.size();
        }
        /**建立對外接口*/
        public interface OnItemClickListener {
            void onItemClick(Data myData);
        }

        /**set filter*/
        @Override
        public Filter getFilter() {
            return timeFilter;
        }
        private Filter timeFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Data> filteredList = new ArrayList<>();
                Calendar filterTime = Convert.StringToCalendar(constraint.toString());
                Calendar filterTimeEnd = Convert.StringToCalendar(constraint.toString());
                filterTimeEnd.add(Calendar.DAY_OF_MONTH,1);
                for (Data filterData : myDataFull){
                    Calendar startDrinkTime = filterData.getStartDrinkTime();
                    if (startDrinkTime.getTimeInMillis()>filterTime.getTimeInMillis()){
                        if (startDrinkTime.getTimeInMillis()<filterTimeEnd.getTimeInMillis()){
                            filteredList.add(filterData);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                myData.clear();
                myData.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }


}
