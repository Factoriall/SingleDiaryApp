package org.techtown.singlediary;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;

public class ContentFragment extends Fragment {
    private DiaryAdapter adapter;

    DatabaseHelper dbHelper;
    SQLiteDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_fragment, container,false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DiaryAdapter();

        setItemsInAdapter(adapter);

        recyclerView.setAdapter(adapter);

        final Button button = view.findViewById(R.id.isContentBase);
        final Button button2 = view.findViewById(R.id.isPhotoBase);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setState(true);
                button.setBackgroundResource(R.drawable.button_pressed);
                button2.setBackgroundResource(R.drawable.button);
            }
        });


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setState(false);
                button.setBackgroundResource(R.drawable.button);
                button2.setBackgroundResource(R.drawable.button_pressed);
            }
        });

        Button createButton = view.findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new CreateFragment()).commit();
                //showToast("createButton 눌림");
            }
        });

        return view;
    }

    private void setItemsInAdapter(DiaryAdapter adapter){
        /*Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.picture1);
        adapter.addItem(new Diary("좋아요", "2012-03-03", "서울", 1 , 2, bitmap));
        adapter.addItem(new Diary("안 좋아요", "2012-03-03", "서울", 2 , 1, bitmap));
        adapter.addItem(new Diary("싫어요", "2012-03-03", "서울", 3 , 3, bitmap));
        adapter.addItem(new Diary("good", "2012-03-03", "서울", 4 , 4, bitmap));
        adapter.addItem(new Diary("bad", "2012-03-03", "서울", 5 , 7, bitmap));
        adapter.addItem(new Diary("fantastic", "2012-03-03", "서울", 1 , 6, bitmap));
        adapter.addItem(new Diary("happy", "2012-03-03", "서울", 3 , 5, bitmap));
        */
        dbHelper = new DatabaseHelper(getContext());
        database = dbHelper.getWritableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM diary", null);

        int recordCount = cursor.getCount();
        for(int i=0; i<recordCount; i++){
            cursor.moveToNext();

            int id = cursor.getInt(0);
            String content = cursor.getString(1);
            String imgPath = cursor.getString(2);
            String date = cursor.getString(3);
            int weather = cursor.getInt(4);
            String address = cursor.getString(5);
            int smileGauge = cursor.getInt(6);
            Bitmap bitmap = null;
            try {
                File f = new File(imgPath, "thumbnail" + id + ".jpg");
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            } catch (Exception e){e.printStackTrace();
                showToast("Fail to find at " + imgPath + "/" + "thumbnail" + id + ".jpg");}
            adapter.addItem(new Diary(content, date, address, smileGauge, weather, bitmap));

        }
    }

    private void showToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }
}
