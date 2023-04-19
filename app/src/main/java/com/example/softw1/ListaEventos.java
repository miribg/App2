package com.example.softw1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ListaEventos extends AppCompatActivity {
    String str_name;

    Button btn_ret;

    boolean delete;

    RecyclerView recycleView;
    ArrayList<String> name, date, time, lug, id, color;

    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventos);

        //obtener valores del Menu Principal
        str_name= getIntent().getExtras().getString("name");
       //click= Boolean.parseBoolean(getIntent().getExtras().getString("click"));
        delete= Boolean.parseBoolean(getIntent().getExtras().getString("delete"));
        //boton volver
        btn_ret=(Button) findViewById(R.id.buttonVol);
        btn_ret.setBackgroundColor(Integer.parseInt(getIntent().getExtras().getString("color")));
        btn_ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //volver a MenuPrincipal
                finish();
            }
        });

        //ArrayList que sirve para luego rellenar los datos de las cardview
        name = new ArrayList<>();
        date= new ArrayList<>();
        time = new ArrayList<>();
        lug = new ArrayList<>();
        id = new ArrayList<>();
        color = new ArrayList<>();
        recycleView= findViewById(R.id.recyclerView);
        obtenerEventos();   //rellenamos las anteriores arraylist con los datos obtenidos por la db
    }

   private void obtenerEventos(){
        //obtener todos los eventos de la persona usuaria que ha echo login
        //String url = "http://192.168.1.139/developeru/eventos.php";
        String url="http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/mbergaz001/WEB/developeru/eventos.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {;
                if (response != null && response.length()>0) {
                    if (response.trim().equalsIgnoreCase("incorrecto")) {
                    }else{
                        String [] eventos= response.trim().split("\\|"); //separar el string por eventos
                        for (int i=0;i< eventos.length;i++) {
                            String[] datos= eventos[i].split(","); //obtener todos los valores de cada evento
                            //añadir datos a los arrayList
                            id.add(datos[0]);
                            name.add(datos[1]);
                            if (datos[3].equals("0000-00-00")) {    //fechaF VACIO
                                date.add(datos[2]);
                            }else {
                                date.add(datos[2] + " - " + datos[3]);
                            }
                            int x=4;
                            while (x<6) {
                                if (datos[x].equals("00:00:00") || datos[x].equals("0") || datos[x].equals("")){
                                    datos[x]=" ";
                                }else{
                                    String[] hora=datos[x].split(":");
                                    datos[x]= hora[0]+":"+hora[1];
                                }
                                x++;
                            }
                            time.add(datos[4]+"-"+datos[5]);
                            lug.add(datos[6]);
                            color.add(datos[7]);
                        }
                        adapter= new MyAdapter(ListaEventos.this,name,date,time,lug,id, color, delete);//crear adapter
                        recycleView.setAdapter(adapter); //añadir al recycleView
                        recycleView.setLayoutManager(new LinearLayoutManager(ListaEventos.this));
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: CAMBIAR
                Toast.makeText(ListaEventos.this, error.toString(), Toast.LENGTH_LONG).show();
                Log.d("error",error.toString());
            }
        }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //pasar parametros a la base de datos
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("user_name", str_name);
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    protected void onStart() {
        // reiniciar
        super.onStart();
        //Toast.makeText(this, "OnStart", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onResume() {
        // hacer visible
        super.onResume();
        //Toast.makeText(this, "OnResume", Toast.LENGTH_SHORT).show();.
    }
    @Override
    protected void onPause() {
        // Pausar la actividad: poner la app en 2 plano
        super.onPause();
        //Toast.makeText(this, "OnPause", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onStop() {
        //Oculta la actividad: 2 plano
        super.onStop();
        //Toast.makeText(this, "OnStop", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onDestroy() {
        // cerrar la app:  no se puede recuoerar
        super.onDestroy();
        // Toast.makeText(this, "OnDestroy", Toast.LENGTH_SHORT).show();
    }
}