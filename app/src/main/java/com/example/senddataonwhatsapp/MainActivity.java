package com.example.senddataonwhatsapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.senddataonwhatsapp.ModelClasses.TransactionDataInfo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    //Mainactivity
    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private ImageView imageView;
    private ProgressBar progressBar;
    private TextInputLayout textInputLayoutMobile;
    private MaterialButton materialButtonSendImage;
    private MaterialButton materialButtonSendPdf;

    //  network
    AlertDialog alertDialog;

    File pictureDirectory;
    Bitmap bitmap;
    String date;
    String finalTime;

    ProgressDialog progressDialog;

    //dynamic image view
    LinearLayout linearLayoutDynamicImageView;

    InputMethodManager imm;


    //transaction data
    ArrayList<TransactionDataInfo> transactionDataInfoArrayList;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //for network register broadcast
        alertMessage();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        setContentView(R.layout.activity_main);

        scrollView = findViewById(R.id.idScrollview);
        linearLayout = findViewById(R.id.idLinearLayoutMainActivity);
        imageView = findViewById(R.id.imageview);
        progressBar = findViewById(R.id.progresbar);
        textInputLayoutMobile = findViewById(R.id.idTextInputLayoutMobileMainActivity);
        materialButtonSendImage = findViewById(R.id.idButtonSendImageMainActivity);
        materialButtonSendPdf = findViewById(R.id.idButtonSendPdfMainActivity);
        progressDialog = new ProgressDialog(this);

        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        transactionDataInfoArrayList = new ArrayList<>();

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
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v)
            {
                try
                {

                    dynamicImageView();

//                    textInputLayoutMobile.setErrorEnabled(false);
//                    String temp = textInputLayoutMobile.getEditText().getText().toString().trim();
//
//                    if(temp.length() != 10)
//                    {
//                        textInputLayoutMobile.setError("Enter WhatsApp Number");
//                        textInputLayoutMobile.requestFocus();
//                        return;
//                    }
//
//                    final String mobileNumber = "91"+temp;
//
//                    progressDialog.setMessage("Sending...");
//                    progressDialog.setCancelable(false);
//                    progressDialog.show();
//
//                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//
//                    new Handler().postDelayed(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            sendLayoutImage("image", mobileNumber);
//                        }
//                    },1000);

                }
                catch (Exception e)
                {
                    progressDialog.dismiss();
                    // TODO: handle exception
                }
            }
        });

        materialButtonSendPdf.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v)
            {
                try
                {
                    textInputLayoutMobile.setErrorEnabled(false);
                    String temp = textInputLayoutMobile.getEditText().getText().toString().trim();

                    if(temp.length() != 10)
                    {
                        textInputLayoutMobile.setError("Enter WhatsApp Number");
                        textInputLayoutMobile.requestFocus();
                        imm.toggleSoftInputFromWindow(getCurrentFocus().getWindowToken(),0,1);

                        return;
                    }

                    final String mobileNumber = "91"+temp;

                    progressDialog.setMessage("Sending...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
//                            sendLayoutImage("pdf", mobileNumber);

                            File filePdf = createPdf();
                            sendPdf(filePdf, mobileNumber);
                        }
                    },1000);

                    

                }
                catch (Exception e)
                {
                    progressDialog.dismiss();
                    // TODO: handle exception
                }
            }
        });

        isWriteStoragePermissionGranted();


//        String image_url = "http://images.cartradeexchange.com//img//800//vehicle//Honda_Brio_562672_5995_6_1438153637072.jpg";
//        String image_url = "http://www.mymanagerpro.com/assets/images/home/man-2.png";

//        Picasso.get().load(image_url).into(imageView, new Callback()
//        {
//            @Override
//            public void onSuccess()
//            {
//                progressBar.setVisibility(View.GONE);
//                imageView.setVisibility(View.VISIBLE);
//                textInputLayoutMobile.setVisibility(View.VISIBLE);
//                materialButtonSendImage.setVisibility(View.VISIBLE);
//                materialButtonSendPdf.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onError(Exception e)
//            {
//
//            }
//        });

        insertTransactionData();
    }

    //insert transaction data
    private void insertTransactionData()
    {
        transactionDataInfoArrayList.add(new TransactionDataInfo("1 June", "test credit", "100", ""));
        transactionDataInfoArrayList.add(new TransactionDataInfo("2 June", "test credit", "100", ""));
        transactionDataInfoArrayList.add(new TransactionDataInfo("3 June", "test credit", "", "200"));
        transactionDataInfoArrayList.add(new TransactionDataInfo("4 June", "test credit", "", "200"));

        transactionDataInfoArrayList.add(new TransactionDataInfo("5 June", "test credit", "100", ""));
        transactionDataInfoArrayList.add(new TransactionDataInfo("6 June", "test credit", "100", ""));
        transactionDataInfoArrayList.add(new TransactionDataInfo("7 June", "test credit", "", "200"));
        transactionDataInfoArrayList.add(new TransactionDataInfo("8 June", "test credit", "", "200"));


        transactionDataInfoArrayList.add(new TransactionDataInfo("9 June", "test credit", "100", ""));
        transactionDataInfoArrayList.add(new TransactionDataInfo("10 June", "test credit", "100", ""));
        transactionDataInfoArrayList.add(new TransactionDataInfo("11 June", "test credit", "", "200"));
        transactionDataInfoArrayList.add(new TransactionDataInfo("12 June", "test credit", "", "200"));

        transactionDataInfoArrayList.add(new TransactionDataInfo("13 June", "test credit", "100", ""));
        transactionDataInfoArrayList.add(new TransactionDataInfo("14 June", "test credit", "100", ""));
        transactionDataInfoArrayList.add(new TransactionDataInfo("15 June", "test credit", "", "200"));
        transactionDataInfoArrayList.add(new TransactionDataInfo("16 June", "test credit", "", "200"));


        transactionDataInfoArrayList.add(new TransactionDataInfo("17 June", "test credit", "100", ""));
        transactionDataInfoArrayList.add(new TransactionDataInfo("18 June", "test credit", "100", ""));
        transactionDataInfoArrayList.add(new TransactionDataInfo("19 June", "test credit", "", "200"));
        transactionDataInfoArrayList.add(new TransactionDataInfo("20 June", "test credit", "", "200"));

        transactionDataInfoArrayList.add(new TransactionDataInfo("21 June", "test credit", "100", ""));
        transactionDataInfoArrayList.add(new TransactionDataInfo("22 June", "test credit", "100", ""));
        transactionDataInfoArrayList.add(new TransactionDataInfo("23 June", "test credit", "", "200"));
        transactionDataInfoArrayList.add(new TransactionDataInfo("24 June", "test credit", "", "200"));


        transactionDataInfoArrayList.add(new TransactionDataInfo("25 June", "test credit", "100", ""));
        transactionDataInfoArrayList.add(new TransactionDataInfo("26 June", "test credit", "100", ""));
        transactionDataInfoArrayList.add(new TransactionDataInfo("27 June", "test credit", "", "200"));
        transactionDataInfoArrayList.add(new TransactionDataInfo("28 June", "test credit", "", "200"));

        transactionDataInfoArrayList.add(new TransactionDataInfo("29 June", "test credit", "100", ""));
        transactionDataInfoArrayList.add(new TransactionDataInfo("30 June", "test credit", "100", ""));
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

        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        if(broadcastReceiver != null)
        {
            unregisterReceiver(broadcastReceiver);
        }
    }

//    *******************************************

    //create dynamic view for sending image
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void dynamicImageView()
    {
        textInputLayoutMobile.setErrorEnabled(false);
        String temp = textInputLayoutMobile.getEditText().getText().toString().trim();

        if(temp.length() != 10)
        {
            textInputLayoutMobile.setError("Enter WhatsApp Number");
            textInputLayoutMobile.requestFocus();
            imm.toggleSoftInputFromWindow(getCurrentFocus().getWindowToken(),0,1);
            return;
        }

        final String mobileNumber = "91"+temp;

        progressDialog.setMessage("Sending...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        final androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dynamic_image_view,null);

        linearLayoutDynamicImageView = view.findViewById(R.id.idLinearLayoutDynamicImageView);
        TextView textView = view.findViewById(R.id.idTextViewNameDynamicView);

//        textView.setText("Sanket Sir");

        alert.setView(view);

        final androidx.appcompat.app.AlertDialog alertDialog = alert.create();

        alertDialog.show();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                sendLayoutImage("image", mobileNumber);
                alertDialog.dismiss();
            }
        },0);

    }

//    convert layout file to image and sent it
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void sendLayoutImage(String media, String mobileNumber)
    {
//        File file = saveBitmap(this, scrollView, media);
        File file;
        if(media.equals("pdf"))
        {
            file = saveBitmap( scrollView, media);
        }
        else
        {
            file = saveBitmap( linearLayoutDynamicImageView, media);
        }



        if(file != null)
        {
//            toastMessage("File Successfully Saved "+file);
            if(media.equals("image"))
            {
                sendImage(file, mobileNumber);
            }
            else
            {
                File filePdf = createDocument();
                sendPdf(filePdf, mobileNumber);
            }

        }
        else
        {
            progressDialog.dismiss();
            toastMessage("File Not Successfully Saved");
        }
    }

    private File saveBitmap(View view, String media)
    {
        if(media.equals("image"))
        {
            pictureDirectory = new File(Environment.getExternalStorageDirectory() + "/SendDataWhatsApp/Images");
        }
        else
        {
            pictureDirectory = new File(Environment.getExternalStorageDirectory() + "/SendDataWhatsApp/PDF");
        }
//        File pictureDirectory = new File(Environment.getExternalStorageDirectory() + "/SendDataWhatsApp");

        if(!pictureDirectory.exists())
        {
            pictureDirectory.mkdirs();
        }

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        String dateAndTime = String.valueOf(c);

        String[] temp = dateAndTime.split(" ");

        System.out.println("Current timsse => " + Arrays.toString(temp));

        date = temp[2] +"_"+ temp[1] +"_" + temp[5];

        String time = temp[3];

        String[] temptime = time.split(":");

        finalTime = temptime[0] + "_" + temptime[1] + "_" + temptime[2];

        String fileName = pictureDirectory.getPath() +File.separator+ "Gautam_"+date+"_"+finalTime+".png";

        Log.d(TAG, "saveBitmap: "+pictureDirectory.getPath());

//        String fileName = "image_"+System.currentTimeMillis()+".png";

        File pictureFile = new File(fileName);

        bitmap = getBitmapFromView(view);

        if(media.equals("image"))
        {

            try
            {
                pictureFile.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(pictureFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            catch (IOException e)
            {

                progressDialog.dismiss();
                Log.d(TAG, "saveBitmap: "+e);
            }
        }

//        scanGallery(context, pictureFile.getAbsolutePath());

        return pictureFile;
    }

    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(View view)
    {

        Bitmap returnedBitmap = Bitmap.createBitmap(
                view.getWidth(),
                view.getHeight(),
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(returnedBitmap);

        Drawable drawable = view.getBackground();

        if(drawable != null)
        {
            drawable.draw(canvas);
        }
        else
        {
            canvas.drawColor(Color.WHITE);
        }

        view.draw(canvas);

        return returnedBitmap;
    }

    //scanning gallery
    private void scanGallery(Context cntx, String path)
    {
        try
        {
            MediaScannerConnection.scanFile(
                    cntx,
                    new String[] { path },
                    null,
                    new MediaScannerConnection.OnScanCompletedListener()
                    {
                    public void onScanCompleted(String path, Uri uri)
                    {

                    }
            });
        }
        catch (Exception e)
        {
            progressDialog.dismiss();
            e.printStackTrace();
        }
    }

    //send image
    private void sendImage(File file, String mobileNumber)
    {
        //for sending imageview

//        BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView.getDrawable());
//        Bitmap bitmap = bitmapDrawable .getBitmap();
//        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,"some title", null);
//        Uri bitmapUri = Uri.parse(bitmapPath);

        progressDialog.dismiss();

        String link = "http://www.mymanagerpro.com";

        Intent sendIntent = new Intent("android.intent.action.MAIN");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage("com.whatsapp");
        sendIntent.setType("text/plain");
        sendIntent.putExtra("jid", mobileNumber + "@s.whatsapp.net");// here 91 is country code
        sendIntent.putExtra(Intent.EXTRA_TEXT, "From My Manager Pro\n"+link);

        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(String.valueOf(file)));
//        sendIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        sendIntent.setType("image/*");
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if(sendIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivity(sendIntent);
            progressDialog.dismiss();
        }
        else
        {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "WhatsApp Not Installed, Please Install Whatsapp", Toast.LENGTH_SHORT).show();
        }
    }

    //send pdf
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private File createDocument()
    {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(),1).create();

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);

        Bitmap bitmapPdf = Bitmap.createScaledBitmap(
                bitmap,
                bitmap.getWidth(),
                bitmap.getHeight(),
                true
        );

        paint.setColor(Color.BLUE);

        canvas.drawBitmap(bitmapPdf, 0, 0, null);
        pdfDocument.finishPage(page);

        String fileName = pictureDirectory.getPath() +File.separator+ "Gautam_"+date+"_"+finalTime+".pdf";

        File filePath = new File(fileName);

        try
        {
            pdfDocument.writeTo(new FileOutputStream(filePath));
        }
        catch (IOException e)
        {
            progressDialog.dismiss();
            toastMessage("Pdf Not Successfully Saved");
        }

        // close the document
        pdfDocument.close();

        return filePath;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void sendPdf(File file, String mobileNumber)
    {
        Intent sendIntent = new Intent("android.intent.action.MAIN");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage("com.whatsapp");
        sendIntent.putExtra("jid", mobileNumber + "@s.whatsapp.net");// here 91 is country code

        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(String.valueOf(file)));
        sendIntent.setType("application/pdf");
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if(sendIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivity(sendIntent);
            progressDialog.dismiss();
        }
        else
        {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "WhatsApp Not Installed, Please Install Whatsapp", Toast.LENGTH_SHORT).show();
        }
    }

    //dynamic testing pdf

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private File createPdf()
    {
        pictureDirectory = new File(Environment.getExternalStorageDirectory() + "/SendDataWhatsApp/PDF");

//        File pictureDirectory = new File(Environment.getExternalStorageDirectory() + "/SendDataWhatsApp");

        if(!pictureDirectory.exists())
        {
            pictureDirectory.mkdirs();
        }

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        String dateAndTime = String.valueOf(c);

        String[] temp = dateAndTime.split(" ");

        System.out.println("Current timsse => " + Arrays.toString(temp));

        date = temp[2] +"_"+ temp[1] +"_" + temp[5];

        String time = temp[3];

        String[] temptime = time.split(":");

        finalTime = temptime[0] + "_" + temptime[1] + "_" + temptime[2];

        String fileName = pictureDirectory.getPath() +File.separator+ "Gautam_"+date+"_"+finalTime+".pdf";

        File pictureFile = new File(fileName);

        try 
        {
            OutputStream output = new FileOutputStream(pictureFile);
            Document document = new Document(PageSize.A4);
            PdfPTable table = new PdfPTable(new float[]{3, 3, 3, 3});
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setFixedHeight(50);
            table.setTotalWidth(PageSize.A4.getWidth());
            table.setWidthPercentage(100);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell("Date");
            table.addCell("Particulers");
            table.addCell("Payment");
            table.addCell("Credit");
            table.setHeaderRows(1);
            PdfPCell[] cells = table.getRow(0).getCells();
            
            for (int j = 0; j < cells.length; j++)
            {
                cells[j].setBackgroundColor(BaseColor.LIGHT_GRAY);
            }


            for (int i = 0; i < transactionDataInfoArrayList.size(); i++)
            {
                TransactionDataInfo transactionDataInfo = transactionDataInfoArrayList.get(i);

                Log.d(TAG, "createPdf: "+transactionDataInfo.getCredit());

                table.addCell(transactionDataInfo.getDate());
                table.addCell(transactionDataInfo.getParticulers());
                table.addCell(transactionDataInfo.getPayment());
                table.addCell(transactionDataInfo.getCredit());
            }

            PdfWriter.getInstance(document, output);
            document.open();
            Font f = new Font(Font.FontFamily.TIMES_ROMAN, 30.0f, Font.UNDERLINE, BaseColor.BLUE);
            Font g = new Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.NORMAL, BaseColor.BLUE);

            Bitmap bitmap11 = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap11.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitMapData = stream.toByteArray();
            Image image = Image.getInstance(bitMapData);
            image.setAlignment(Element.ALIGN_CENTER);

            document.add(image);
            document.add(table);
            document.add(image);
            
            document.close();
            
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        return pictureFile;
    }

//    Toast Message Method
    private void toastMessage(String message)
    {
        Toast toast = Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_SHORT
                );
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

//    permissison
    public void isWriteStoragePermissionGranted()
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
            {
                Log.v("TAG","Permission is granted2");
            }
            else
            {

                Log.v("TAG","Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            }
        }
        else
        { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted2");
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
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    Log.v("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission
                }
                else
                {
                }
                break;
        }
    }


}