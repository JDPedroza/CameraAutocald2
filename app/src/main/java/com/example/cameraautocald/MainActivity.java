package com.example.cameraautocald;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageView imageview;
    Button mButton;
    String mAbsolutePath="";
    File mAbsoluteFile;
    final int PHOTO_CONST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)  != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)  != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},1000);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,},1000);
        }

        imageview = (ImageView) findViewById(R.id.imageViewPhoto);
        mButton = (Button)findViewById(R.id.buttonTakePhoto);

        mButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void takePhoto(){

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePhotoIntent.resolveActivity(getPackageManager())!=null){
            File photoFile = null;
            try{
                photoFile = createPhotoFile();

            }catch (Exception e){
                e.printStackTrace();
            }

            if(photoFile!=null){
                Uri photoUri = FileProvider.getUriForFile(MainActivity.this,BuildConfig.APPLICATION_ID + ".provider",photoFile);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(takePhotoIntent,PHOTO_CONST);
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private File createPhotoFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date());
        String imageFileName = "imagen "+ timestamp;

        File storageFile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "PDF");
        File photoFile = File.createTempFile(imageFileName,".jpg", storageFile);
        this.mAbsoluteFile = photoFile;
        mAbsolutePath = photoFile.getAbsolutePath();
        return photoFile;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PHOTO_CONST && resultCode == RESULT_OK){
            //intentos
            //1->Edgar
            //Uri uri = Uri.parse(mAbsolutePath);
            //imageview.setImageURI(uri);
            //2->Edgar
            imageview.setImageURI(data.getData());
            //3->Johan
            //Uri uri = Uri.fromFile(mAbsoluteFile);
            //imageview.setImageURI(uri);
            //4->Johan
            //imageview.setImageBitmap(BitmapFactory.decodeFile(mAbsolutePath));
            //5->
            //Uri uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", mAbsoluteFile);
            //imageview.setImageURI(uri);
            //6->
            //Uri uri = Uri.parse(mAbsoluteFile.getAbsolutePath());
            //imageview.setImageURI(uri);
            //appSendPDF();
        }
    }
    public void appSendPDF(){
        String[] mailto = {""};
        Uri uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", mAbsoluteFile);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, mailto);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Calc PDF Report");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,"Hi PDF is attached in this mail. ");
        emailIntent.setType("application/image");
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(emailIntent, "Send email using:"));
    }
}
