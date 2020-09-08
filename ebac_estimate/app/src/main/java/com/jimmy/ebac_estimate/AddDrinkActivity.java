package com.jimmy.ebac_estimate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class AddDrinkActivity extends AppCompatActivity {
    private RecyclerView recycler_view;
    private MyAdapter adapter;
    private ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drink);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_drink);
        toolbar.setTitle("新增酒杯");

        setSupportActionBar(toolbar);


        /**get common drink data from arrays.xml*/
        String[] commonDrinks;
        commonDrinks = getResources().getStringArray(R.array.common_drinks);

        for (int i = 0;i < commonDrinks.length;i+=5){
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("Id", commonDrinks[i]);
            hashMap.put("abv", commonDrinks[i+1]);
            hashMap.put("ml", commonDrinks[i+2]);
            hashMap.put("img", commonDrinks[i+3]);
            hashMap.put("comment",commonDrinks[i+4]);
            arrayList.add(hashMap);
        }

        // 連結元件
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        // 設置RecyclerView為列表型態
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        // 設置格線
        recycler_view.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // 將資料交給adapter
        adapter = new MyAdapter(arrayList);
        // 設置adapter給recycler_view
        recycler_view.setAdapter(adapter);
    }



    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();

        MyAdapter(ArrayList<HashMap<String, String>> data) {
            arrayList = data;
        }

        // 建立ViewHolder
        class ViewHolder extends RecyclerView.ViewHolder{
            // 宣告元件
            private TextView tvId,tvSub1,tvSub2;
            private ImageView img;


            ViewHolder(View itemView) {
                super(itemView);
                tvId = itemView.findViewById(R.id.textView_Id);
                tvSub1 = itemView.findViewById(R.id.textView_sub1);
                tvSub2 = itemView.findViewById(R.id.textView_sub2);
                img = (ImageView) itemView.findViewById(R.id.imageView2);


                // 點擊項目時
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), AddDrinkActivity2.class);

                        intent.putExtra("id",arrayList.get(getAdapterPosition()).get("Id"));
                        intent.putExtra("abv",arrayList.get(getAdapterPosition()).get("abv"));
                        intent.putExtra("ml",arrayList.get(getAdapterPosition()).get("ml"));
                        intent.putExtra("comment",arrayList.get(getAdapterPosition()).get("comment"));
                        view.getContext().startActivity(intent);
                    }
                });


            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 連結項目布局檔list_item
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // 設置txtItem要顯示的內容
            holder.tvId.setText(arrayList.get(position).get("Id"));
            holder.tvSub1.setText(arrayList.get(position).get("abv"));
            holder.tvSub2.setText(arrayList.get(position).get("ml"));
            holder.img.setImageResource(getResources().getIdentifier(arrayList.get(position).get("img")
                    , "drawable", getPackageName()));

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }


    }

}
