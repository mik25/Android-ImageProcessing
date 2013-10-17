package com.image.imagemanip;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {

    private Bitmap image;
    private ImageView imageView;
    private Button greyImageBtn;
    private Button gammaImageBtn;
    private Button invertImageBtn;
    private Button sepiaBtn;
    private Button contrastBtn;
    private Button blurBtn;
    private Button sharpenBtn;
    private Button smoothBtn;
    private Button embossBtn;
    private Button engraveBtn;
    private Button watermarkBtn;
    private Button mirrorBtn;
    private Button flipVerticalBtn;
    private Button tintBtn;
    private Button reflectionBtn;
    private Button saveBtn;

    private ImageManipulator imageManipulator;

    private String imagename;
    private String imageorigname;

    private int tintValue;
    private float GammaR;
    private float GammaG;
    private float GammaB;
    private int Depth;
    private String WaterMarkText;
    private int WaterMarkSize;

    private static int RESULT_LOAD_IMAGE = 1;
    private Context context = this;
    private ProgressDialog progress;
    private Handler handler;
    private Bitmap result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        greyImageBtn = (Button) findViewById(R.id.GreyImageBtn);
        gammaImageBtn = (Button) findViewById(R.id.GammaImageBtn);
        invertImageBtn = (Button) findViewById(R.id.InvertImageBtn);
        sepiaBtn = (Button) findViewById(R.id.SepiaBtn);
        contrastBtn = (Button) findViewById(R.id.ContrastBtn);
        blurBtn = (Button) findViewById(R.id.BlurBtn);
        sharpenBtn = (Button) findViewById(R.id.SharpenBtn);
        smoothBtn = (Button) findViewById(R.id.SmoothBtn);
        embossBtn = (Button) findViewById(R.id.EmbossBtn);
        engraveBtn = (Button) findViewById(R.id.EngraveBtn);
        watermarkBtn = (Button) findViewById(R.id.WatermarkBtn);
        mirrorBtn = (Button) findViewById(R.id.MirrorBtn);
        flipVerticalBtn = (Button) findViewById(R.id.FlipVerticalBtn);
        tintBtn = (Button) findViewById(R.id.TintBtn);
        reflectionBtn = (Button) findViewById(R.id.ReflectionBtn);
        saveBtn = (Button) findViewById(R.id.SaveBtn);

        imageManipulator = new ImageManipulator();
        imagename = "";
        imageorigname = "";

        imageView.setOnClickListener(LoadImageFromGallery);
        greyImageBtn.setOnClickListener(OnGreyBtnClick);
        gammaImageBtn.setOnClickListener(OnGammaBtnClick);
        invertImageBtn.setOnClickListener(OnInvertImageBtnClick);
        sepiaBtn.setOnClickListener(OnSepiaBtnClick);
        contrastBtn.setOnClickListener(OnContrastBtnClick);
        blurBtn.setOnClickListener(OnBlurBtnClick);
        sharpenBtn.setOnClickListener(OnSharpenBtnClick);
        smoothBtn.setOnClickListener(OnSmoothBtnClick);
        embossBtn.setOnClickListener(OnEmbossBtnClick);
        engraveBtn.setOnClickListener(OnEngraveBtnClick);
        watermarkBtn.setOnClickListener(OnWatermarkBtnClick);
        mirrorBtn.setOnClickListener(OnMirrorBtnClick);
        flipVerticalBtn.setOnClickListener(OnflipVerticalBtnClick);
        tintBtn.setOnClickListener(OnTintBtnClick);
        reflectionBtn.setOnClickListener(OnReflectionBtnClick);
        saveBtn.setOnClickListener(OnSaveBtnClick);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private View.OnClickListener LoadImageFromGallery = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            String[] splitPath = picturePath.split("/");
            imageorigname = splitPath[splitPath.length - 1];
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            options.inPurgeable = true;
            image = Bitmap.createBitmap(BitmapFactory.decodeFile(picturePath, options));
            imageView.setImageBitmap(image);
        }
    }

    private View.OnClickListener OnGreyBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bitmap res= imageManipulator.doGrayscale(image);
            imageView.setImageBitmap(res);
        }
    };

    private View.OnClickListener OnGammaBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.VERTICAL);
            final EditText red = new EditText(context);
            red.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            red.setHint("Red (0.0 - 1.0)");
            final EditText green = new EditText(context);
            green.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            green.setHint("Green (0.0 - 1.0)");
            final EditText blue = new EditText(context);
            blue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            blue.setHint("Blue (0.0 - 1.0)");

            ll.addView(red);
            ll.addView(green);
            ll.addView(blue);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle("Set R, G, B values");
            alertDialogBuilder.setView(ll);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    GammaR = Float.parseFloat(red.getText().toString().trim());
                                    GammaG = Float.parseFloat(green.getText().toString().trim());
                                    GammaB = Float.parseFloat(blue.getText().toString().trim());

                                    progress = new ProgressDialog(context);
                                    progress.setTitle("ImageManip");
                                    progress.setMessage("Gamma Correction in Progress");
                                    progress.setCancelable(false);
                                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    handler = new Handler()
                                    {
                                        public void  handleMessage(Message msg)
                                        {
                                            imageView.setImageBitmap(result);
                                            progress.dismiss();
                                        }
                                    };
                                    progress.show();

                                    new Thread()
                                    {
                                        public void run()
                                        {
                                            result = imageManipulator.doGamma(image, GammaR, GammaG, GammaB);
                                            handler.sendEmptyMessage(0);
                                        }
                                    }.start();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

            imagename = "Gamma";
        }
    };

    private View.OnClickListener OnInvertImageBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            progress = new ProgressDialog(context);
            progress.setTitle("ImageManip");
            progress.setMessage("Inverting in Progress");
            progress.setCancelable(false);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            handler = new Handler()
            {
                public void  handleMessage(Message msg)
                {
                    imageView.setImageBitmap(result);
                    progress.dismiss();
                }
            };
            progress.show();

            new Thread()
            {
                public void run()
                {
                    result = imageManipulator.doInvert(image);
                    handler.sendEmptyMessage(0);
                }
            }.start();
            imagename = "Inverted";
        }
    };

    private View.OnClickListener OnSepiaBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.VERTICAL);

            final EditText depth = new EditText(context);
            depth.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            depth.setHint("Depth (1 - 100)");
            final EditText red = new EditText(context);
            red.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            red.setHint("Red (0.0 - 1.0)");
            final EditText green = new EditText(context);
            green.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            green.setHint("Green (0.0 - 1.0)");
            final EditText blue = new EditText(context);
            blue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            blue.setHint("Blue (0.0 - 1.0)");

            ll.addView(depth);
            ll.addView(red);
            ll.addView(green);
            ll.addView(blue);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle("Set Depth, R, G, B values");
            alertDialogBuilder.setView(ll);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    Depth = Integer.parseInt(depth.getText().toString());
                                    GammaR = Float.parseFloat(red.getText().toString());
                                    GammaG = Float.parseFloat(green.getText().toString());
                                    GammaB = Float.parseFloat(blue.getText().toString());

                                    progress = new ProgressDialog(context);
                                    progress.setTitle("ImageManip");
                                    progress.setMessage("Sepia Effect in Progress");
                                    progress.setCancelable(false);
                                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    handler = new Handler()
                                    {
                                        public void  handleMessage(Message msg)
                                        {
                                            imageView.setImageBitmap(result);
                                            progress.dismiss();
                                        }
                                    };
                                    progress.show();

                                    new Thread()
                                    {
                                        public void run()
                                        {
                                            result = imageManipulator.createSepiaToningEffect(image, Depth,  GammaR, GammaG, GammaB);
                                            handler.sendEmptyMessage(0);
                                        }
                                    }.start();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            imagename = "Sepia";
        }
    };

    private View.OnClickListener OnContrastBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setTitle("Contrast");
            alertBuilder.setMessage("Set Contrast Factor (1 - 100");

            final EditText userInput = new EditText(context);
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            alertBuilder.setView(userInput);

            alertBuilder.setCancelable(false);
            alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id) {
                    String val = userInput.getText().toString();
                    final float contrastVal = (float) Integer.parseInt(val)/100;

                    // progress bar
                    progress = new ProgressDialog(context);
                    progress.setTitle("ImageManip");
                    progress.setMessage("Contrast in progress");
                    progress.setCancelable(false);
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    handler = new Handler()
                    {
                        public void  handleMessage(Message msg)
                        {
                            imageView.setImageBitmap(result);
                            progress.dismiss();
                        }
                    };
                    progress.show();

                    new Thread()
                    {
                        public void run()
                        {
                            result = imageManipulator.createContrast(image, contrastVal);
                            handler.sendEmptyMessage(0);
                        }
                    }.start();
                }
            });
            alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alertBuilder.create();

            // show it
            alertDialog.show();
            imagename = "Contrast";
        }
    };

    private View.OnClickListener OnBlurBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setTitle("Blur");

            final EditText userInput = new EditText(context);
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            userInput.setHint("Set Blur value (1 - 100)");
            alertBuilder.setView(userInput);

            alertBuilder.setCancelable(false);
            alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id) {
                    final int blurVal = Integer.parseInt(userInput.getText().toString());

                    // progress bar
                    progress = new ProgressDialog(context);
                    progress.setTitle("ImageManip");
                    progress.setMessage("Blur effect in progress");
                    progress.setCancelable(false);
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    handler = new Handler()
                    {
                        public void handleMessage(Message msg)
                        {
                            imageView.setImageBitmap(result);
                            progress.dismiss();
                        }
                    };
                    progress.show();

                    new Thread()
                    {
                        public void run()
                        {
                            result = imageManipulator.applyGaussianBlur(image, blurVal);
                            handler.sendEmptyMessage(0);
                        }
                    }.start();


                }
            });
            alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alertBuilder.create();

            // show it
            alertDialog.show();
            imagename = "Blur";
        }
    };

    private View.OnClickListener OnSharpenBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setTitle("Sharpening");
            alertBuilder.setMessage("Set Sharpening Factor (1 - 100");

            final EditText userInput = new EditText(context);
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            alertBuilder.setView(userInput);

            alertBuilder.setCancelable(false);
            alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id) {
                    String val = userInput.getText().toString();
                    final float sharpnessVal = (float) Integer.parseInt(val)/100;

                    // progress bar
                    progress = new ProgressDialog(context);
                    progress.setTitle("ImageManip");
                    progress.setMessage("Sharpnening in progress");
                    progress.setCancelable(false);
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    handler = new Handler()
                    {
                        public void  handleMessage(Message msg)
                        {
                            imageView.setImageBitmap(result);
                            progress.dismiss();
                        }
                    };
                    progress.show();

                    new Thread()
                    {
                        public void run()
                        {
                            result = imageManipulator.sharpen(image, sharpnessVal);
                            handler.sendEmptyMessage(0);
                        }
                    }.start();
                }
            });
            alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alertBuilder.create();

            // show it
            alertDialog.show();
            imagename = "Sharp";
        }
    };

    private View.OnClickListener OnSmoothBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setTitle("Smooth");
            alertBuilder.setMessage("Set Smoothing Factor (1 - 100");

            final EditText userInput = new EditText(context);
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            alertBuilder.setView(userInput);

            alertBuilder.setCancelable(false);
            alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id) {
                    String val = userInput.getText().toString();
                    final float smoothVal = (float) Integer.parseInt(val)/100;

                    // progress bar
                    progress = new ProgressDialog(context);
                    progress.setTitle("ImageManip");
                    progress.setMessage("Smoothening in progress");
                    progress.setCancelable(false);
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    handler = new Handler()
                    {
                        public void  handleMessage(Message msg)
                        {
                            imageView.setImageBitmap(result);
                            progress.dismiss();
                        }
                    };
                    progress.show();

                    new Thread()
                    {
                        public void run()
                        {
                            result = imageManipulator.smooth(image, smoothVal);
                            handler.sendEmptyMessage(0);
                        }
                    }.start();

                    imagename = "Smooth";
            }
            });
            alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alertBuilder.create();

            // show it
            alertDialog.show();
        }
    };

    private View.OnClickListener OnEmbossBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            progress = new ProgressDialog(context);
            progress.setTitle("ImageManip");
            progress.setMessage("Emboss in progress");
            progress.setCancelable(false);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            handler = new Handler()
            {
                public void  handleMessage(Message msg)
                {
                    imageView.setImageBitmap(result);
                    progress.dismiss();
                }
            };
            progress.show();

            new Thread()
            {
                public void run()
                {
                    result = imageManipulator.emboss(image);
                    handler.sendEmptyMessage(0);
                }
            }.start();

            imagename = "Emboss";
        }
    };

    private View.OnClickListener OnEngraveBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            progress = new ProgressDialog(context);
            progress.setTitle("ImageManip");
            progress.setMessage("Engrave in progress");
            progress.setCancelable(false);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            handler = new Handler()
            {
                public void  handleMessage(Message msg)
                {
                    imageView.setImageBitmap(result);
                    progress.dismiss();
                }
            };
            progress.show();

            new Thread()
            {
                public void run()
                {
                    result = imageManipulator.engrave(image);
                    handler.sendEmptyMessage(0);
                }
            }.start();

            imagename = "Engrave";
        }
    };

    private View.OnClickListener OnWatermarkBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Point p = new Point(20, 20);

            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.VERTICAL);
            final EditText waterMark = new EditText(context);
            waterMark.setInputType(InputType.TYPE_CLASS_TEXT);
            waterMark.setHint("Enter the text to be written on image");
            final EditText size = new EditText(context);
            size.setInputType(InputType.TYPE_CLASS_NUMBER);
            size.setHint("Set the size of text");

            ll.addView(waterMark);
            ll.addView(size);

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setTitle("Enter WaterMark Details");
            alertBuilder.setView(ll);

            alertBuilder.setCancelable(false);
            alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id) {
                    WaterMarkText = waterMark.getText().toString();
                    WaterMarkSize = Integer.parseInt(size.getText().toString());

                    // progress bar
                    progress = new ProgressDialog(context);
                    progress.setTitle("ImageManip");
                    progress.setMessage("Watermarking in progress");
                    progress.setCancelable(false);
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    handler = new Handler()
                    {
                        public void  handleMessage(Message msg)
                        {
                            imageView.setImageBitmap(result);
                            progress.dismiss();
                        }
                    };
                    progress.show();

                    new Thread()
                    {
                        public void run()
                        {
                            result = imageManipulator.watermark(image, WaterMarkText, p, Color.BLUE, 100, WaterMarkSize, true);
                            handler.sendEmptyMessage(0);
                        }
                    }.start();
                }
            });
            alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alertBuilder.create();
            // show it
            alertDialog.show();

            imagename = "Watermark";
        }
    };

    private View.OnClickListener OnMirrorBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bitmap res = imageManipulator.flip(image, ImageManipulator.FLIP_HORIZONTAL);
            imagename = "Mirror";
            imageView.setImageBitmap(res);
        }
    };

    private View.OnClickListener OnflipVerticalBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bitmap res = imageManipulator.flip(image, ImageManipulator.FLIP_VERTICAL);
            imagename = "Flip";
            imageView.setImageBitmap(res);
        }
    };

    private View.OnClickListener OnTintBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            alertDialogBuilder.setTitle("Set Tint Value");

            final EditText userInput = new EditText(context);
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            userInput.setHint("Tint Value (0 - 100)");
            alertDialogBuilder.setView(userInput);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    tintValue = Integer.parseInt(userInput.getText().toString());

                                    progress = new ProgressDialog(context);
                                    progress.setTitle("ImageManip");
                                    progress.setMessage("Tint in Progress");
                                    progress.setCancelable(false);
                                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    handler = new Handler()
                                    {
                                        public void  handleMessage(Message msg)
                                        {
                                            imageView.setImageBitmap(result);
                                            progress.dismiss();
                                        }
                                    };
                                    progress.show();

                                    new Thread()
                                    {
                                        public void run()
                                        {
                                            result = imageManipulator.tintImage(image, tintValue);
                                            handler.sendEmptyMessage(0);
                                        }
                                    }.start();

                                    imagename = "Tint";
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
    };

    private View.OnClickListener OnReflectionBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bitmap res = imageManipulator.applyReflection(image);
            imagename = "Reflection";
            imageView.setImageBitmap(res);
        }
    };

    private View.OnClickListener OnSaveBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bitmap outImage = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            String fileName = imageorigname + "_" + imagename;
            WriteToFile(fileName, outImage);
        }
    };

    private void WriteToFile(String filename, Bitmap bmp)
    {
        String path = Environment.getExternalStorageDirectory().toString();
        FileOutputStream f = null;
        path += "/ImageManip/" + filename + ".png";
        Log.i("ImageManip Path: ", path);
        File file = new File(path);
        try
        {
            f = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, f);
            f.close();
            MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
        }
        catch (IOException ex)
        {
            Log.i("ImageManip", ex.getMessage());
            Toast.makeText(this, "Failed to save image to storage card", Toast.LENGTH_LONG);
        }
    }
}
