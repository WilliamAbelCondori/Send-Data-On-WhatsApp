package com.example.senddataonwhatsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
{
    //Mainactivity
    private ImageView imageView;
    private ProgressBar progressBar;
    private TextInputLayout textInputLayoutMobile;
    private MaterialButton materialButtonSendImage;
    private MaterialButton materialButtonSendPdf;

    //  network
    AlertDialog alertDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //for network register broadcast
        alertMessage();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageview);
        progressBar = findViewById(R.id.progresbar);
        textInputLayoutMobile = findViewById(R.id.idTextInputLayoutMobileMainActivity);
        materialButtonSendImage = findViewById(R.id.idButtonSendImageMainActivity);
        materialButtonSendPdf = findViewById(R.id.idButtonSendPdfMainActivity);

        textInputLayoutMobile.getEditText().setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                textInputLayoutMobile.setErrorEnabled(false);
                return false;
            }
        });

        materialButtonSendImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendImage();
            }
        });

        materialButtonSendPdf.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendPdf();
            }
        });

        isWriteStoragePermissionGranted();


        String image_url = "http://images.cartradeexchange.com//img//800//vehicle//Honda_Brio_562672_5995_6_1438153637072.jpg";

        Picasso.get().load(image_url).into(imageView, new Callback() {
            @Override
            public void onSuccess()
            {
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                textInputLayoutMobile.setVisibility(View.VISIBLE);
                materialButtonSendImage.setVisibility(View.VISIBLE);
                materialButtonSendPdf.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    public  boolean isWriteStoragePermissionGranted()
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
            {
                Log.v("TAG","Permission is granted2");
                return true;
            }
            else
            {

                Log.v("TAG","Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else
        { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted2");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d("TAG", "External storage2");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission
                }else{
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);

                }
                break;

            case 3:
                Log.d("TAG", "External storage1");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission
                }else{
                }
                break;
        }
    }


    //Mainactivity

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int network = NetworkUtil.getConnectivityStatus(context);

            if(network == 0)
            {
                alertDialog.show();
            }
            else
            {
                alertDialog.dismiss();
            }
        }
    };



    public void alertMessage()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage("Please connect with to working internet connection");

        builder.setTitle("Network Error!");

        builder.setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
                startActivity(getIntent());
            }
        });

        alertDialog = builder.create();

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        //network
        alertDialog.dismiss();

        if(broadcastReceiver != null)
        {
            unregisterReceiver(broadcastReceiver);
        }
    }

    //send image
    private void sendImage()
    {
//        PackageManager packageManager = getPackageManager();
//
//        Uri uri = Uri.parse("smsto:" + "9067649737");
//
//        try
//        {
//            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
////            intent.setType("text/plain");
//            intent.setDataAndType(uri, "text/plain");
//
//            String message = "Testing Whatsapp";
//
//            PackageInfo packageInfo = packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
//
//            intent.putExtra(Intent.EXTRA_TEXT, message);
//            intent.setPackage("com.whatsapp");
//            startActivity(Intent.createChooser(intent, "Share With"));
//        }
//        catch (PackageManager.NameNotFoundException e)
//        {
//            Toast.makeText(getApplicationContext(), "Whatsapp not installed", Toast.LENGTH_SHORT).show();
//        }

//        Uri uri = Uri.parse("smsto:" + "9067649737");
//        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
//        i.setType("text/plain");
//        i.putExtra("sms_body","HI ...");
//        i.setPackage("com.whatsapp");
//        startActivity(Intent.createChooser(i, "ggg"));


//        String smsNumber = "9067649737"; // E164 format without '+' sign
//        Intent sendIntent = new Intent(Intent.ACTION_SEND);
//        sendIntent.setType("text/plain");
//        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
//        sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
//        sendIntent.setPackage("com.whatsapp");
//        if (sendIntent.resolveActivity(getPackageManager()) == null) {
//            Toast.makeText(this, "Error/n", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        startActivity(sendIntent);

//        String toNumber = "+91 75714 24135"; // contains spaces.
//        toNumber = toNumber.replace("+", "").replace(" ", "");
//
//        Intent sendIntent = new Intent("android.intent.action.MAIN");
//        sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
//        sendIntent.putExtra(Intent.EXTRA_TEXT, "message");
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.setPackage("com.whatsapp");
//        sendIntent.setType("text/plain");
//        startActivity(sendIntent);

//        String smsNumber = "917517424135";
//        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
//        if (isWhatsappInstalled) {
//
//            Intent sendIntent = new Intent("android.intent.action.MAIN");
//            sendIntent.setType("text/plain");
//
//            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
//            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(smsNumber) + "@s.whatsapp.net");//phone number without "+" prefix
//            sendIntent.putExtra(Intent.EXTRA_TEXT, "gAaaaaaaaa");
//            startActivity(sendIntent);
//        } else {
//            Uri uri = Uri.parse("market://details?id=com.whatsapp");
//            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//            Toast.makeText(this, "WhatsApp not Installed",
//                    Toast.LENGTH_SHORT).show();
//            startActivity(goToMarket);
//        }
//    }
//
//    private boolean whatsappInstalledOrNot(String uri) {
//        PackageManager pm = getPackageManager();
//        boolean app_installed = false;
//        try {
//            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
//            app_installed = true;
//        } catch (PackageManager.NameNotFoundException e) {
//            app_installed = false;
//        }
//        return app_installed;

//        Uri imageUri = Uri.parse("https://cdn.mos.cms.futurecdn.net/huWFavoNm5ECK8UL23DG6N-650-80.jpg");

//        String s = "https://internshala.com/cached_uploads/logo%2F5ee5cbc78f5171592118215.png";

        textInputLayoutMobile.setErrorEnabled(false);
        String temp = textInputLayoutMobile.getEditText().getText().toString().trim();

        if(temp.length() != 10)
        {
            textInputLayoutMobile.setError("Enter Whatsapp Number");
            textInputLayoutMobile.requestFocus();
            return;
        }

        String mobileNumber = "91"+temp;

        BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView.getDrawable());
        Bitmap bitmap = bitmapDrawable .getBitmap();
        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,"some title", null);
        Uri bitmapUri = Uri.parse(bitmapPath);

        Intent sendIntent = new Intent("android.intent.action.MAIN");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage("com.whatsapp");
        sendIntent.setType("text/plain");
        sendIntent.putExtra("jid", mobileNumber + "@s.whatsapp.net");// here 91 is country code
        sendIntent.putExtra(Intent.EXTRA_TEXT, "From My Manager Pro...");

        sendIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        sendIntent.setType("image/*");
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if(sendIntent.resolveActivity(getPackageManager()) != null)
            startActivity(sendIntent);
        else
            Toast.makeText(getApplicationContext(), "Whatsapp Not Installed, Please Install Whatsapp", Toast.LENGTH_SHORT).show();
    }

    //send pdf
    private void sendPdf()
    {

    }

}