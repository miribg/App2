package com.example.softw1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity  {

    private EditText et1, et2, et3, et4, et5, et6;
    private Button btn;
    private String str_name, str_uName, str_surname, str_password, str_email, str_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Unión entre vista y controlador
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        et1=(EditText) findViewById(R.id.uNombreText);
        et2=(EditText) findViewById(R.id.nombreText);
        et3=(EditText) findViewById(R.id.apellidosText);
        et4=(EditText) findViewById(R.id.contraseñaText);
        et5=(EditText) findViewById(R.id.emailText);
        et6=(EditText) findViewById(R.id.phoneText);

        btn=(Button) findViewById(R.id.buttonEv);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //obtener valores
                str_uName = et1.getText().toString().trim();
                str_password=et4.getText().toString().trim();
                if ( !str_password.isEmpty() && !str_uName.isEmpty()) {
                    comprobarNombreUsuario();
                } else {    //algún dato sin meter
                    //TODO dialogo mejor
                    Toast.makeText(Registro.this, R.string.vacioReg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void comprobarNombreUsuario(){
        //comprueba que el nombre de usuario no esta ya en la base de datos
        String url="http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/mbergaz001/WEB/developeru/doble.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && response.length()>0) {
                    if (response.trim().equalsIgnoreCase("correcto")) {
                        str_email=et5.getText().toString().trim();
                        str_phone=et6.getText().toString().trim();
                        if (isEmailValid(str_email) || str_email.isEmpty()) {
                            if (str_phone.length() == 9 || str_phone.length() == 0) {
                                if (str_phone.length()==0){
                                    str_phone="0'";
                                }
                                registrar();     //si no esta, se registra el usuario
                            } else {
                                Toast.makeText(Registro.this, R.string.formatoTel, Toast.LENGTH_SHORT).show();//el telefono debe tener 9 caracteres o no ponerlo
                            }
                        }
                    } else{ //nombre de usuario ya cogido
                        errorAlert();
                        et1.setText("", TextView.BufferType.EDITABLE);  //vaciar el nombre de usuario, para que puedan volver a meterlo
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: CAMBIAR
                Toast.makeText(Registro.this, error.toString(), Toast.LENGTH_LONG).show();
                Log.d("error",error.toString());
            }
        }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //parametros que se usaran en la base de datos
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("user_name", str_uName);
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
        private boolean isEmailValid(String email) {
            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }
    private void registrar(){
        //se cogen todos los parametros y se hace un insert en la db
        str_name = et2.getText().toString().trim();
        str_surname=et3.getText().toString().trim();
        String url="http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/mbergaz001/WEB/developeru/registrar.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && response.length()>0){
                    Toast.makeText(Registro.this,response,Toast.LENGTH_LONG).show();
                    if (response.trim().equalsIgnoreCase("registro correcto")) {
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if (!task.isSuccessful()) {
                                            Log.w("Error", "Fetching FCM registration token failed", task.getException());
                                            return;
                                        }

                                        // Get new FCM registration token
                                        String token = task.getResult();
                                        Toast.makeText(Registro.this,token, Toast.LENGTH_SHORT).show();
                                        Log.w("TOKEN",token);
                                    }
                                });
                        //Dirige al inicio de sesión
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }else { //Algún dato no es del tipo indicado
                        //TODO: dialogo mejor
                        Toast.makeText(Registro.this, R.string.vacio, Toast.LENGTH_LONG).show();
                    }
                }/*else{
                    Toast.makeText(Registro.this, "No parametrs", Toast.LENGTH_LONG).show();
                }*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: CAMBIAR
                Toast.makeText(Registro.this, error.toString(), Toast.LENGTH_LONG).show();
                Log.d("error",error.toString());
            }
        }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Recoge los parametros que se van a utilizar para la consulta en la db
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("user_name", str_uName);
                parametros.put("password", str_password);
                parametros.put("surname",str_surname);
                parametros.put("email", str_email);
                parametros.put("phone", str_phone);
                parametros.put("name", str_name);
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void errorAlert(){
        //error: usuario ya cogido
        AlertDialog.Builder al= new AlertDialog.Builder(this);
        al.setMessage(R.string.errorRepe)
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

}