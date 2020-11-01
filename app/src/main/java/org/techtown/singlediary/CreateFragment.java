package org.techtown.singlediary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateFragment extends Fragment{
    TextView dateText;
    TextView content;
    TextView address;
    int weatherInteger;
    ImageView weatherIcon;
    SeekBar smileBar;
    int smileGauge;
    ImageView thumbnail;
    SQLiteDatabase database;
    DatabaseHelper dbHelper;

    private static int RESULT_LOAD_IMG = 1;
    private static String TABLE_NAME = "diary";
    private FusedLocationProviderClient fusedLocationClient;
    static RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_fragment, container, false);

        address = view.findViewById(R.id.address);
        getAddressAndWeather();

        content = view.findViewById(R.id.content);
        thumbnail = view.findViewById(R.id.thumbnail);

        weatherIcon = view.findViewById(R.id.weather);

        String date_n = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(new Date());
        dateText = view.findViewById(R.id.dateText);

        dateText.setText(date_n);
        /*dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDate();
            }
        });*/

        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFromAlbum();
            }
        });

        smileBar = view.findViewById(R.id.seekBar);
        smileBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                smileGauge = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        Button saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDiary();
            }
        });

        Button deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDiary();
            }
        });

        Button exitButton = view.findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitFragment();
            }
        });

        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        }

        return view;
    }

    private void getAddressAndWeather(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder;
                                List<Address> addresses;
                                geocoder = new Geocoder(getActivity(), Locale.getDefault());

                                getWeather(location);

                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    String addr = addresses.get(0).getAddressLine(0);
                                    String[] addrList = addr.split(" ");

                                    address.setText(addrList[2] + " " + addrList[3]);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        } catch (SecurityException e) { e.printStackTrace();}
    }

    private void getWeather(Location location){
        WeatherAPI key = new WeatherAPI();
        final String API_KEY = key.getKey();

        String url = "http://api.openweathermap.org/data/2.5/weather?lat="+ location.getLatitude()
                +"&lon=" + location.getLongitude()
                + "&APPID=" + API_KEY;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);

                            String weather = json.getJSONArray("weather").getJSONObject(0).getString("main");
                            switch(weather){
                                case "Clear":
                                    weatherIcon.setImageResource(R.drawable.weather_1);
                                    weatherInteger = 1;
                                    break;
                                case "Clouds":
                                    weatherIcon.setImageResource(R.drawable.weather_2);
                                    weatherInteger = 2;
                                    break;
                                case "Rain":
                                    weatherIcon.setImageResource(R.drawable.weather_3);
                                    weatherInteger = 3;
                                    break;
                                case "ThunderStorm":
                                    weatherIcon.setImageResource(R.drawable.weather_4);
                                    weatherInteger = 4;
                                    break;
                                case "Drizzle":
                                    weatherIcon.setImageResource(R.drawable.weather_5);
                                    weatherInteger = 5;
                                    break;
                                case "Snow":
                                    weatherIcon.setImageResource(R.drawable.weather_6);
                                    weatherInteger = 6;
                                    break;
                                default:
                                    weatherIcon.setImageResource(R.drawable.weather_7);
                                    weatherInteger = 7;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void getImageFromAlbum(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                thumbnail.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                showToast("잘못됨");
            }

        }else {
            showToast("이미지 선택 안함");
        }
    }

    private void saveDiary(){
        dbHelper = new DatabaseHelper(getContext());
        database = dbHelper.getWritableDatabase();

        int dataNum = 1;
        Cursor cur = database.rawQuery("SELECT count(*) FROM " + TABLE_NAME, null);
        if (cur != null && cur.moveToFirst() && cur.getInt(0) > 0) {
            Cursor rCursor = database.rawQuery("SELECT MAX(_id) FROM " + TABLE_NAME, null);
            rCursor.moveToNext();
            dataNum = rCursor.getInt(0) + 1;
        }
        String imgPath = saveToInternalStorage(((BitmapDrawable)thumbnail.getDrawable()).getBitmap(), dataNum);

        database.execSQL("insert into " + TABLE_NAME
                    + "(content, imgPath, date, weather, address, smileGauge)"
                    + " values "
                    + "('" + content.getText().toString() + "','" + imgPath + "','" + dateText.getText().toString() + "'," + weatherInteger + ",'" + address.getText().toString() + "'," + smileGauge +")");
    }

    private String saveToInternalStorage(Bitmap bitmapImage, int number){
        ContextWrapper cw = new ContextWrapper(getContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory,"thumbnail" + number + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            showToast("fail to save");
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        showToast("Save at " + directory.getAbsolutePath());
        return directory.getAbsolutePath();
    }

    private void deleteDiary(){
        content.setText("");
        thumbnail.setImageResource(R.drawable.imagetoset);
        String date_n = new SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(new Date());
        dateText.setText(date_n);
        smileBar.setProgress(0);
        smileGauge = 0;
    }

    private void exitFragment(){
        showToast("exitFragment에 들어옴");
        getActivity().onBackPressed();
    }

    private void changeDate(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener mDataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month = month + 1;
                String date = year + "년 " + month + "월 " + day + "일";
                dateText.setText(date);
            }
        };

        DatePickerDialog dialog = new DatePickerDialog(
                getContext(),
                AlertDialog.THEME_HOLO_LIGHT,
                mDataSetListener,
                year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void showToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

}
