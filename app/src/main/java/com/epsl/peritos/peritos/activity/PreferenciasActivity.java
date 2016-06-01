package com.epsl.peritos.peritos.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.epsl.peritos.peritos.R;
import com.epsl.peritos.sintomas_registro.AdapterTakes;
import com.epsl.peritos.sintomas_registro.BBDDTratamiento;
import com.epsl.peritos.sintomas_registro.StructureParametersBBDD;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by noni_ on 29/05/2016.
 */
public class PreferenciasActivity extends AppCompatActivity {

    /*
Código del mensaje de envío y
Uri de contenido global AGENDA
 */
    public static final int PICK_CONTACT_REQUEST = 1 ;
    private Uri contactUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferencias_activity);

        TextView boton_preferencias_telefono=(TextView)findViewById(R.id.txt_cuidador_pref);
        TextView boton_nueva_hora = (TextView)findViewById(R.id.txt_nueva_hora);
        TextView boton_nuevo_tratamiento=(TextView)findViewById(R.id.txt_nuevo_tratamiento);
        TextView contactName = (TextView)findViewById(R.id.contactName);
        TextView contactPhone = (TextView)findViewById(R.id.contactPhone);
        ImageView contactPic = (ImageView)findViewById(R.id.contactPic);

        //Pref compartidas
        SharedPreferences p = getSharedPreferences("PRFS", MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        boolean isNewTratament=false;
         boolean isNewWakeUp = false;
        ed.putBoolean("NEW_TRATAMENT", isNewTratament);
        ed.putBoolean("NEW_HOUR",isNewWakeUp);
        ed.apply();


        TextView boton_tratamiento = (TextView)findViewById(R.id.txt_visualizar_tratamiento);
        boton_tratamiento.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<StructureParametersBBDD.Treatment> listTreatment = new ArrayList<StructureParametersBBDD.Treatment>(new BBDDTratamiento(getApplicationContext()).getListTreatment());
                        FragmentManager fragManager = getSupportFragmentManager();
                        DialogListFolder dialogoFolder = new DialogListFolder(listTreatment);
                        dialogoFolder.show(fragManager, "tag");
                    }
                }
        );



        boton_preferencias_telefono.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            initPickContacts(v);
                    }
                }
        );


        boton_nuevo_tratamiento.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        SharedPreferences pr = getSharedPreferences("PRFS", MODE_PRIVATE);
                        boolean comprobar_tratamiento = pr.getBoolean("NEW_TRATAMENT", false);
                        if(comprobar_tratamiento==false){
                            final SharedPreferences.Editor ed = pr.edit();
                            boolean isNewTratament=true;
                            ed.putBoolean("NEW_TRATAMENT", isNewTratament);
                            ed.apply();
                            Intent i = new Intent(PreferenciasActivity.this,OnlyScanner.class);
                            startActivity(i);
                            ;

                        }

                    }
                }

        );

        boton_nueva_hora.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        SharedPreferences pr = getSharedPreferences("PRFS", MODE_PRIVATE);
                        boolean comprobar_hora = pr.getBoolean("NEW_HOUR", false);
                        if(comprobar_hora==false){
                            final SharedPreferences.Editor ed = pr.edit();
                            boolean isNewWakeUp=true;
                            ed.putBoolean("NEW_HOUR", isNewWakeUp);
                            ed.apply();
                            Intent i = new Intent(PreferenciasActivity.this,OnlyTimePickerActivity.class);
                            startActivity(i);
                            ;

                        }

                    }
                }

        );

        //Obtiene de preferencias el numero de telefono que haya almacenado y lo muestra
        //para tenerlo siempre visible
        //SharedPreferences p = getSharedPreferences("PRFS", MODE_PRIVATE);
        String nombre = p.getString("CARER_NAME", "NO HAY CUIDADOR REGISTRADO");
        String tlf = p.getString("CARER_PHONE", " ");
        String image = p.getString("CARER_IMAGE","NO-PHOTO");

        Bitmap imagen_contacto=null;
        if(image.equals("NO-PHOTO")==false){
            //Decodificar Imagen para devolver a BitMap la cogemos en Base64 de SharedPreferences
            byte[] decodedByte = Base64.decode(image, 0);
            imagen_contacto=BitmapFactory.decodeByteArray(decodedByte, 0,   decodedByte.length);
        }else{
            imagen_contacto=null;
        }


        contactName.setText(nombre);
        contactPhone.setText(tlf);
        if(imagen_contacto!=null) {
            contactPic.setImageBitmap(imagen_contacto);
        }


    }


    RecyclerView recyclerFolder;
    @SuppressLint("ValidFragment")
    public class DialogListFolder extends DialogFragment {
        private ArrayList<StructureParametersBBDD.Treatment> dataTreatment;
        private AdapterTakes adaptadorTreatment;

        public DialogListFolder(ArrayList<StructureParametersBBDD.Treatment> dataTreatment) {
            this.dataTreatment = dataTreatment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Tratamiento");
            recyclerFolder = new RecyclerView(getContext());
            adaptadorTreatment = new AdapterTakes(dataTreatment, getContext());
            recyclerFolder.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            builder.setView(recyclerFolder);
            recyclerFolder.setAdapter(adaptadorTreatment);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });
            return builder.create();
        }
    }



    public void initPickContacts(View v){
        /*
        Crear un intent para seleccionar un contacto del dispositivo
         */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M   && checkSelfPermission(Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    0);
        }
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        /*
        Iniciar la actividad esperando respuesta a través
        del canal PICK_CONTACT_REQUEST
         */
        startActivityForResult(i, PICK_CONTACT_REQUEST);
    }

    private void renderContact(Uri uri) {

        /*
        Obtener instancias de los Views
         */
        TextView contactName = (TextView)findViewById(R.id.contactName);
        TextView contactPhone = (TextView)findViewById(R.id.contactPhone);
        ImageView contactPic = (ImageView)findViewById(R.id.contactPic);

        /*
        Setear valores
         */
        contactName.setText(getName(uri));
        contactPhone.setText(getPhone(uri));
        contactPic.setImageBitmap(getPhoto(uri));



        //Convertir imagen a String para almacenarla en SHared Preferences
        String image_contact="";
        if(getPhoto(uri)!=null){
            ByteArrayOutputStream baos=new  ByteArrayOutputStream();
            getPhoto(uri).compress(Bitmap.CompressFormat.PNG,100, baos);
            byte[] arr = baos.toByteArray();
            image_contact=Base64.encodeToString(arr, Base64.DEFAULT);
        }




        //ALMACENAMOS NOMBRE DE USUARIO Y TELEFONO EN LAS PREFERENCIAS COMPARTIDAS DE LA APLICACION
        SharedPreferences p = getSharedPreferences("PRFS", MODE_PRIVATE);
        SharedPreferences.Editor ed = p.edit();
        ed.putString("CARER_NAME",getName(uri));
        ed.putString("CARER_PHONE", getPhone(uri));
        if(image_contact.equals("")==false){
            ed.putString("CARER_IMAGE",image_contact);
        }else{
            ed.putString("CARER_IMAGE","NO-PHOTO");
        }
        ed.apply();
        //ed.commit();
        Toast.makeText(this,"Número almacenado en preferencias",Toast.LENGTH_SHORT).show();
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                /*
                Capturar el valor de la Uri
                 */
                contactUri = intent.getData();
                /*
                Procesar la Uri
                 */
                renderContact(contactUri);
            }
        }
    }

    private String getPhone(Uri uri) {
        /*
        Variables temporales para el id y el teléfono
         */
        String id = null;
        String phone = null;

        /************* PRIMERA CONSULTA ************/
        /*
        Obtener el _ID del contacto
         */
        Cursor contactCursor = getContentResolver().query(
                uri,
                new String[]{ContactsContract.Contacts._ID},
                null,
                null,
                null);


        if (contactCursor.moveToFirst()) {
            id = contactCursor.getString(0);
        }
        contactCursor.close();

        /************* SEGUNDA CONSULTA ************/
        /*
        Sentencia WHERE para especificar que solo deseamos
        números de telefonía móvil
         */
        String selectionArgs =
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE+"= " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;

        /*
        Obtener el número telefónico
         */
        Cursor phoneCursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
                selectionArgs,
                new String[] { id },
                null
        );
        if (phoneCursor.moveToFirst()) {
            phone = phoneCursor.getString(0);
        }
        phoneCursor.close();

        return phone;
    }

    private String getName(Uri uri) {

        /*
        Valor a retornar
         */
        String name = null;

         /*
        Obtener una instancia del Content Resolver
         */
        ContentResolver contentResolver = getContentResolver();

        /*
        Consultar el nombre del contacto
         */
        Cursor c = contentResolver.query(
                uri,
                new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                null,
                null,
                null);

        if(c.moveToFirst()){
            name = c.getString(0);
        }

        /*
        Cerramos el cursor
         */
        c.close();

        return name;
    }

    private Bitmap getPhoto(Uri uri) {
        /*
        Foto del contacto y su id
         */
        Bitmap photo = null;
        String id = null;

        /************* CONSULTA ************/
        Cursor contactCursor = getContentResolver().query(
                uri,
                new String[]{ContactsContract.Contacts._ID},
                null,
                null,
                null);

        if (contactCursor.moveToFirst()) {
            id = contactCursor.getString(0);
        }
        contactCursor.close();

        /*
        Usar el método de clase openContactPhotoInputStream()
         */
        try {
            Uri contactUri = ContentUris.withAppendedId(
                    ContactsContract.Contacts.CONTENT_URI,
                    Long.parseLong(id));

            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(
                    getContentResolver(),
                    contactUri);

            if (input != null) {
                /*
                Dar formato tipo Bitmap a los bytes del BLOB
                correspondiente a la foto
                 */
                photo = BitmapFactory.decodeStream(input);
                input.close();
            }

        } catch (IOException iox) { /* Manejo de errores */ }

        return photo;
    }





}
