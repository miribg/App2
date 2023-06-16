package com.example.softw1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText et1, et2;
    private TextView btn_reg;
    private Button btn_iniciar;

    private Spinner spinner;

    private String str_name, str_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // abrir la app:  La actividad está creada.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //Unión entre vista y controlador
        //Toast: mostra un mensaje, Toast.makeText(sitio, texto, duración)
        //Toast.makeText(this, "OnCreate", Toast.LENGTH_SHORT).show();

        //obtener valores
        et1 = (EditText) findViewById(R.id.nombreUsuario);
        et2 = (EditText) findViewById(R.id.contraseñaUsuario);
        str_name = et1.getText().toString().trim();
        str_password = et2.getText().toString().trim();

        //opciones de idiomas
        spinner=(Spinner)findViewById(R.id.spinnerId1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i!=0){   //si ha escogido un idioma
                    String idioma=adapterView.getItemAtPosition(i).toString();  //obtiene el valor escogido
                    cambiarIdioma(idioma); //cambia el idioma
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //boton registrar
        btn_reg = (TextView) findViewById(R.id.regView);
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //te lleva a registrar
                Intent intentReg = new Intent(MainActivity.this, Registro.class);
                startActivity(intentReg);
            }
        });

        //boton iniciar
        btn_iniciar = (Button) findViewById(R.id.buttonIn);
        btn_iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_name = et1.getText().toString().trim();
                str_password = et2.getText().toString().trim();
                if (!str_name.isEmpty() && !str_password.isEmpty()) {
                   validarUsuario();
                } else {
                    //  TODO: Dialogo mejor
                    Toast.makeText(MainActivity.this, R.string.vacio, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void validarUsuario()  {
        // comprueba que exista un usuario con ese nombre y contraseña
       // String url = "http://192.168.1.139/developeru/validar_usuario.php";
        String url="http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/mbergaz001/WEB/developeru/validar_usuario.php";
       /* try{
            URL destino= new URL(url) ;
            HttpURLConnection urlConnection= (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && response.length()>0){
                    if (response.trim().equalsIgnoreCase("ingreso correctamente")) {
                        Intent intent = new Intent(getApplicationContext(), MenuPrincipal.class);
                        intent.putExtra("name",str_name);
                        startActivity(intent);
                    }else {
                        errorAlert();
                        et1.setText("");
                        et2.setText("");
                    }
                }/*else{
                    Toast.makeText(MainActivity.this, "No parametrs", Toast.LENGTH_LONG).show();
                }*/
            }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                //TODO: CAMBIAR
                    Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    Log.d("error",error.toString());
                }
            }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("user_name", str_name);
                parametros.put("password", str_password);
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void errorAlert(){
        //error: usuario y/o contraseña incorrecta
        AlertDialog.Builder al= new AlertDialog.Builder(this);
        al.setMessage(R.string.errorSign)
                .setTitle(R.string.errorSignEn)
                .setCancelable(false)
                .setNeutralButton(R.string.btn_acep, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert= al.create();
        alert.show();
    }
    private void cambiarIdioma(String idioma){
        Locale nuevaloc = new Locale(idioma);
        Locale.setDefault(nuevaloc);
        Resources resources= getBaseContext().getResources();
        Configuration configuration =resources.getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);
        Context context = getBaseContext().createConfigurationContext(configuration);
        resources.updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        /*finish();
        startActivity(getIntent());*/
        actualizar();
    }

    private void actualizar() {
        //actualiza los datos al idioma seleccionado
        et1.setHint(getString(R.string.str_uName));
        et2.setHint(getString(R.string.str_cont));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.idiomas,
                android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        btn_reg.setText(getString(R.string.btn_reg));
        btn_iniciar.setText(getString(R.string.btn_iniciar));

    }

}