package com.example.softw1;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

public class EscogerColor extends AppCompatActivity {

    Fragment  frCol; //mediante este atributo se puede ver el fragment pintado
    FragmentTransaction transaction; //para gestionar el cambio de color del fragment

    Button btn_vol, btn_guar;

    Spinner sp; //opciones de colores

    String color, colorEsc, str_name;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eleccion_color);



        //obtener valores del MenuPrincipal
        str_name=getIntent().getExtras().getString("user_name");
        color= getIntent().getExtras().getString("color");
        colorEsc=color;

        //iniciar el fragment con el color elegido anteriormente
        frCol=new ColorFragment(Integer.parseInt(color));
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentC,frCol).commit();

        sp= findViewById(R.id.spinnerCol2);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //al seleccionar un color, se podra observar en el fragment
                if (i!=0){
                    obtenerColor(i);
                }else{
                  if ( getResources().getConfiguration().orientation==ORIENTATION_LANDSCAPE){
                        obtenerColor(i);
                  }

                }
                transaction=getSupportFragmentManager().beginTransaction();
                frCol=new ColorFragment(Integer.parseInt(colorEsc));
                transaction.replace(R.id.fragmentC,frCol);
                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //boton volver
        btn_vol=findViewById(R.id.bVolver);
        btn_vol.setBackgroundColor(Integer.parseInt(color));
        btn_vol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //boton guardar
        btn_guar=findViewById(R.id.bGuardar);
        btn_guar.setBackgroundColor(Integer.parseInt(color));
        btn_guar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!colorEsc.equals(color)){
                    color=colorEsc;
                    //en el caso de haber cambiado de color guardarlo en la db
                    updateColor();
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",color);  //pasar color a MenuPrincipal
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

        }

    private void obtenerColor(int i) {
        //conseguir en string el color seleccionado
        int[] coloresOp= getResources().getIntArray(R.array.coloresOp);
        colorEsc= String.valueOf(coloresOp[i]);
        //Toast.makeText(this, "color"+color, Toast.LENGTH_SHORT).show();
    }

    private void updateColor(){
        //actualizar en la base datos el color
        String url = "http://192.168.1.135/developeru/updateCol.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               /* if (response != null && response.length()>0) {
                    if (response.equalsIgnoreCase("color actualizado")) {
                        Toast.makeText(EscogerColor.this,"Actualizado", Toast.LENGTH_SHORT).show();
                    }
                }*/
            }
        }, null) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //pasar a la base de datos los parametros que necesita para realizar la consulta
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("user_name", str_name);
                parametros.put("color",color);
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
