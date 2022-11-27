package com.example.meethbourhood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Registro extends AppCompatActivity {

    TextView RegistroTXT;
    EditText Correo, Password, Nombre, Apellido, Edad, Telefono, Direccion;
    FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Registro");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(Registro.this);

        RegistroTXT = findViewById(R.id.RegistroTXT);
        Correo = findViewById(R.id.Correo);
        Password = findViewById(R.id.Password);
        Nombre = findViewById(R.id.Nombre);
        Apellido = findViewById(R.id.Apellido);
        Edad = findViewById(R.id.Edad);
        Telefono = findViewById(R.id.Telefono);
        Direccion = findViewById(R.id.Direccion);

        firebaseAuth = FirebaseAuth.getInstance();

        cambioletra();
    }

    private void cambioletra(){
        /*FUENTE DE LETRA*/
        String ubicacion = "fuentes/sans_medio.ttf";
        Typeface Tf = Typeface.createFromAsset(Registro.this.getAssets(),ubicacion);
        /*FUENTE DE LETRA*/
        RegistroTXT.setTypeface(Tf);
        Correo.setTypeface(Tf);
        Password.setTypeface(Tf);
        Nombre.setTypeface(Tf);
        Apellido.setTypeface(Tf);
        Edad.setTypeface(Tf);
        Telefono.setTypeface(Tf);
        Direccion.setTypeface(Tf);
    }

    public void Regist(View view){
        String correo = Correo.getText().toString();
        String pass = Password.getText().toString();

        //Validación
        if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            Correo.setError("Correo no válido");
            Correo.setFocusable(true);
        }else if(pass.length()<6){
            Password.setError("La contraseña debe tener al menos 6 caracteres");
            Password.setFocusable(true);
        }else{
            REGISTRAR(correo,pass);
        }
    }

    //Método para registrar un usuario
    private void REGISTRAR(String correo, String pass) {
        progressDialog.setTitle("Creando cuenta");
        progressDialog.setMessage("Espere por favor...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            //Para obtener el uid
                            assert user != null;
                            String uid = user.getUid();
                            String correo = Correo.getText().toString();
                            String pass = Password.getText().toString();
                            String nombre = Nombre.getText().toString();
                            String apellido = Apellido.getText().toString();
                            String edad = Edad.getText().toString();
                            String telefono = Telefono.getText().toString();
                            String direccion = Direccion.getText().toString();

                            //Creación de Hashmap para madar a la firebase
                            HashMap<Object,String> DatosUsuario = new HashMap<>();

                            DatosUsuario.put("uid", uid);
                            DatosUsuario.put("correo", correo);
                            DatosUsuario.put("pass", pass);
                            DatosUsuario.put("nombre", nombre);
                            DatosUsuario.put("apellido", apellido);
                            DatosUsuario.put("edad", edad);
                            DatosUsuario.put("telefono", telefono);
                            DatosUsuario.put("direccion", direccion);
                            //La imagen de momento estará vacía
                            DatosUsuario.put("imagen", "");

                            //Inicializamos la instancia de la base de datos de firebase
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //Creamos la base de datos
                            DatabaseReference reference = database.getReference("USUARIOS_DE_APP");
                            //Nombre de la BD "USUARIOS_DE_APP"
                            reference.child(uid).setValue(DatosUsuario);
                            Toast.makeText(Registro.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                            //Una vez que se ha registrado, nos mandará a la pantalla de inicio.
                            startActivity(new Intent(Registro.this, Inicio.class));
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(Registro.this, "Algo ha salido mal", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener((e) -> {
                    progressDialog.dismiss();
                    Toast.makeText(Registro.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    //Habilitamos la opción para retroceder una ventana
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}