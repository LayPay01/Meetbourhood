package com.example.meethbourhood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.HashMap;

public class MisDatos extends AppCompatActivity {

    ImageView ImagenDato;
    TextView UidDatoTXT, UidDato, NombresDatoTXT, NombresDato, ApellidosDatoTXT, ApellidosDato, CorreoDatoTXT,
            CorreoDato, PasswordDatoTXT, PasswordDato, EdadDatoTXT, EdadDato, DireccionDatoTXT, DireccionDato,
            TelefonoDatoTXT, TelefonoDato, MisDatosTXT;
    ImageView QRCode;
    String InfoQR;

    private StorageReference ReferenciaAlm = FirebaseStorage.getInstance().getReference();
    private String storage_path = "ProfilePic/*";

    //Permisos
    private static final int COD_SEL_STORAGE = 200;
    private static final int COD_SEL_IMAGE = 300;

    //Matrices
    private String [] STORAGE_PERMISSION;
    private Uri image_uri;
    private String profile;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    DatabaseReference BASE_DE_DATOS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_datos2);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Mis Datos");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        STORAGE_PERMISSION = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        MisDatosTXT = findViewById(R.id.MisDatosTXT);
        ImagenDato = findViewById(R.id.ImagenDato);
        UidDatoTXT = findViewById(R.id.UidDatoTXT);
        UidDato = findViewById(R.id.UidDato);
        NombresDatoTXT = findViewById(R.id.NombresDatoTXT);
        NombresDato = findViewById(R.id.NombresDato);
        ApellidosDatoTXT = findViewById(R.id.ApellidosDatoTXT);
        ApellidosDato = findViewById(R.id.ApellidosDato);
        CorreoDatoTXT = findViewById(R.id.CorreoDatoTXT);
        CorreoDato = findViewById(R.id.CorreoDato);
        PasswordDatoTXT = findViewById(R.id.PasswordDatoTXT);
        PasswordDato = findViewById(R.id.PasswordDato);
        EdadDatoTXT = findViewById(R.id.EdadDatoTXT);
        EdadDato = findViewById(R.id.EdadDato);
        DireccionDatoTXT = findViewById(R.id.DireccionDatoTXT);
        DireccionDato = findViewById(R.id.DireccionDato);
        TelefonoDatoTXT = findViewById(R.id.TelefonoDatoTXT);
        TelefonoDato = findViewById(R.id.TelefonoDato);

        QRCode = findViewById(R.id.QRCode);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        BASE_DE_DATOS = FirebaseDatabase.getInstance().getReference("USUARIOS_DE_APP");
        camioletra();
        /*OBTENEMOS LOS DATOS DEL USUARIO*/
        BASE_DE_DATOS.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //SI EL USUARIO EXISTE
                if(snapshot.exists()){
                    String uid = ""+snapshot.child("uid").getValue();
                    String nombre = ""+snapshot.child("nombre").getValue();
                    String apellido = ""+snapshot.child("apellido").getValue();
                    String correo = ""+snapshot.child("correo").getValue();
                    String pass = ""+snapshot.child("pass").getValue();
                    String direccion = ""+snapshot.child("direccion").getValue();
                    String edad = ""+snapshot.child("edad").getValue();
                    String telefono = ""+snapshot.child("telefono").getValue();
                    String imagen = ""+snapshot.child("imagen").getValue();

                    //SETEAMOS LOS DATOS EN LOS TEXT VIEW E IMAGEVIEW
                    UidDato.setText(uid);
                    NombresDato.setText(nombre);
                    ApellidosDato.setText(apellido);
                    CorreoDato.setText(correo);
                    PasswordDato.setText(pass);
                    DireccionDato.setText(direccion);
                    EdadDato.setText(edad);
                    TelefonoDato.setText(telefono);

                    InfoQR = "Nombre del usuario: "+NombresDato.getText().toString()+", No. de telefono: "+TelefonoDato.getText().toString();

                    //PARA OBTENER LA IMAGEN
                    try {
                        //SI EXISTE IMAGEN
                        Picasso.get().load(imagen).placeholder(R.drawable.fotoperfil).into(ImagenDato);

                        //GENERA QR
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.encodeBitmap(InfoQR, BarcodeFormat.QR_CODE, 200, 200);
                        QRCode.setImageBitmap(bitmap);
                    }catch (Exception e){
                        //SI NO EXISTE IMAGEN
                        Picasso.get().load(R.drawable.fotoperfil).into(ImagenDato);

                        //GENERA QR
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void EditarFoto(View view){
        String [] Opciones = {"Foto de perfil"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(Opciones, (dialogInterface, i) -> {
            if (i == 0){
                profile = "imagen";
                ActualizarFoto();
            }
        });
        builder.create().show();
    }

    private void ActualizarFoto() {
        String [] opciones = {"Galería"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar imagen de: ");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0){
                    //Seleccionó de galería
                    if(!ComprobarPermisoAlm()){
                        //Si no habilitó el permiso
                        SolPerAlm();
                    }else{
                        //Si habilitó el permiso
                        ElegirIMGGaleria();
                    }
                }
            }
        });
        builder.create().show();
    }

    //PERMISO DE ALMACENAMIENTO EN TIEMPO DE EJECUCIÓN
    private void SolPerAlm() {
        requestPermissions(STORAGE_PERMISSION, COD_SEL_STORAGE);
    }

    //Comprueba si el permiso de almacenamiento está activado o no.
    private boolean ComprobarPermisoAlm() {
        boolean resultado = ContextCompat.checkSelfPermission(MisDatos.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return resultado;
    }

    //Se ejecuta cuando el usuario  presiona permitir o denegar el cuadro de dialogo
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case COD_SEL_STORAGE:{
                //Selección de galería
                if(grantResults.length > 0){
                    boolean StorageWriteGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(StorageWriteGranted){
                        //Permiso habilitado
                        ElegirIMGGaleria();
                    }else{
                        //Si el usuario dijo que no
                        Toast.makeText(MisDatos.this, "HABILITE EL PERMISO A LA GALERÍA", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //Se llama cuando el usuario ya ha elegido la imagen de la galería
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            //De la imagen vamos a obtener la URI
            if(requestCode == COD_SEL_IMAGE){
                image_uri = data.getData();
                SubirFoto(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Este método cambia la foto de perfil del usuario y actualiza la info en la BD
    private void SubirFoto(Uri image_uri) {
        String RutaArchivoNombre = storage_path + "" + profile + "" + user.getUid();
        StorageReference storageReference = ReferenciaAlm.child(RutaArchivoNombre);
        storageReference.putFile(image_uri)
                .addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while(!uriTask.isSuccessful());
                    Uri downloadUri = uriTask.getResult();

                        if(uriTask.isSuccessful()){
                            HashMap<String, Object> result = new HashMap<>();
                            result.put(profile, downloadUri.toString());
                            BASE_DE_DATOS.child(user.getUid()).updateChildren(result)
                                    .addOnSuccessListener(unused -> Toast.makeText(MisDatos.this, "La imágen ha sido cambiada con éxito", Toast.LENGTH_SHORT).show()).
                                    addOnFailureListener(e ->
                                            Toast.makeText(MisDatos.this, "Ha ocurrid un error", Toast.LENGTH_SHORT).show());
                        }else{
                            Toast.makeText(MisDatos.this, "Algo ha salido mal", Toast.LENGTH_SHORT).show();
                        }
                }).addOnFailureListener(e ->
                        Toast.makeText(MisDatos.this, "Algo ha salido mal", Toast.LENGTH_SHORT).show());
    }

    //Este método abre la galería
    private void ElegirIMGGaleria() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, COD_SEL_IMAGE);
    }

    public void cambiapass(View view){
        startActivity(new Intent(MisDatos.this, CambiarPassword.class));
    }

    private void camioletra(){
        /*FUENTE DE LETRA*/
        String ubicacion = "fuentes/sans_medio.ttf";
        Typeface Tf = Typeface.createFromAsset(MisDatos.this.getAssets(),ubicacion);
        /*FUENTE DE LETRA*/
        UidDatoTXT.setTypeface(Tf);
        UidDato.setTypeface(Tf);
        NombresDatoTXT.setTypeface(Tf);
        NombresDato.setTypeface(Tf);
        ApellidosDatoTXT.setTypeface(Tf);
        ApellidosDato.setTypeface(Tf);
        CorreoDatoTXT.setTypeface(Tf);
        CorreoDato.setTypeface(Tf);
        PasswordDatoTXT.setTypeface(Tf);
        PasswordDato.setTypeface(Tf);
        EdadDatoTXT.setTypeface(Tf);
        EdadDato.setTypeface(Tf);
        DireccionDatoTXT.setTypeface(Tf);
        DireccionDato.setTypeface(Tf);
        TelefonoDatoTXT.setTypeface(Tf);
        TelefonoDato.setTypeface(Tf);
        MisDatosTXT.setTypeface(Tf);
    }


    //Habilitamos la opción para retroceder una ventana
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}