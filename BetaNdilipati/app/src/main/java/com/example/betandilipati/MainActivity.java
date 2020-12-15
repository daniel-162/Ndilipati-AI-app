package com.example.betandilipati;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.*;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

import org.opencv.dnn.Dnn;
import org.opencv.utils.Converters;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

//tts
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import java.util.Locale;
import android.util.Pair;

//stt
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;

//class Pair<L,R> {
//    private L l;
//    private R r;
//    public Pair(L l, R r){
//        this.l = l;
//        this.r = r;
//    }
//    public L getL(){ return l; }
//    public R getR(){ return r; }
//    public void setL(L l){ this.l = l; }
//    public void setR(R r){ this.r = r; }
//}

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    int counter = 0;
    boolean startYolo = false;
    boolean firstTimeYolo = false;
    Net tinyYolo;
    //tts
    private TextToSpeech mTTS;
//    private EditText mEditText;

    public void YOLO (View Button){
        if(startYolo == false){
            startYolo = true;

            if(firstTimeYolo == false){

                String tinyYoloCfg = Environment.getExternalStorageDirectory() + "/dnns/yolov3-tiny.cfg" ;
                String tinyYoloWeights = Environment.getExternalStorageDirectory() + "/dnns/yolov3-tiny.weights";
                tinyYolo = Dnn.readNetFromDarknet(tinyYoloCfg, tinyYoloWeights);
                firstTimeYolo = true;

            }

        }
        else{
            startYolo = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.CameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);


        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);

                switch(status){

                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }

        };
        //tts
        //setContentView(R.layout.activity_main);
        // mButtonSpeak = findViewById(R.id.button_speak);
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        //stt


//        String className = cocoNames.get(idGuy);
        //     mButtonSpeak.setOnClickListener(new View.OnClickListener() {
        //         @Override
        //         public void onClick(View v) {
        //             speak();
        //         }
        //     });
        // }

    }
    private void speak(String text) {

        //float pitch = (float) mSeekBarPitch.getProgress() / 50;
        // if (pitch < 0.1) pitch = 0.1f;
        // float speed = (float) mSeekBarSpeed.getProgress() / 50;
        // if (speed < 0.1) speed = 0.1f;
        mTTS.setPitch(0.8f);
        mTTS.setSpeechRate(1.0f);
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    //tts
    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                }
                break;
        }
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat frame = inputFrame.rgba();
//        if(counter % 2 == 0){
//            Core.flip(frame, frame, 1);
//            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2GRAY);
//
//        }
//        counter += 1;
        //edge detection
//        if(startYolo == false){
//            //convert to grayscale
//            Imgproc.cvtColor(frame, frame,Imgproc.COLOR_RGBA2GRAY);
//            //add blur
//            // Imgproc.blur(frame, frame, new Size(3,3))
//            //edge detection
//            Imgproc.Canny(frame, frame, 100, 80);
//        }

        if (startYolo == true) {

            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2RGB);
            System.out.println("Here!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println(frame);
            System.out.println(frame.height());
            System.out.println(frame.width());
            //Extract Height and Width from frame
            int H,W;
            H = frame.height();
            W = frame.width();





            Mat imageBlob = Dnn.blobFromImage(frame, 0.00392, new Size(416,416),new Scalar(0, 0, 0),/*swapRB*/false, /*crop*/false);


            tinyYolo.setInput(imageBlob);



            java.util.List<Mat> result = new java.util.ArrayList<Mat>(2);

            List<String> outBlobNames = new java.util.ArrayList<>();
            outBlobNames.add(0, "yolo_16");
            outBlobNames.add(1, "yolo_23");

            tinyYolo.forward(result,outBlobNames);


            float confThreshold = 0.5f;



            List<Integer> clsIds = new ArrayList<>();
            List<Float> confs = new ArrayList<>();
            List<Rect> rects = new ArrayList<>();
            //List<Integer> centers = new ArrayList<>();
            Pair centers;
            List<Integer> valueX = new ArrayList<>();
            List<String> detected = new ArrayList<>();
            List<Pair> pz = new ArrayList<Pair>();
            List<String> text = new ArrayList<>();




            for (int i = 0; i < result.size(); ++i)
            {

                Mat level = result.get(i);

                for (int j = 0; j < level.rows(); ++j)
                {
                    Mat row = level.row(j);
                    Mat scores = row.colRange(5, level.cols());

                    Core.MinMaxLocResult mm = Core.minMaxLoc(scores);




                    float confidence = (float)mm.maxVal;


                    Point classIdPoint = mm.maxLoc;



                    if (confidence > confThreshold)
                    {
                        int centerX = (int)(row.get(0,0)[0] * frame.cols());
                        int centerY = (int)(row.get(0,1)[0] * frame.rows());
                        int width   = (int)(row.get(0,2)[0] * frame.cols());
                        int height  = (int)(row.get(0,3)[0] * frame.rows());


                        int left    = centerX - width  / 2;
                        int top     = centerY - height / 2;

                        clsIds.add((int)classIdPoint.x);
                        confs.add((float)confidence);
                        Pair value = new Pair(centerX, centerY);

                        pz.add(value);
                        valueX.add(centerX);
                        //centers.add(value);
                        System.out.println("AGAIN!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        System.out.println(pz);




                        rects.add(new Rect(left, top, width, height));
                    }
                }
            }
            int ArrayLength = confs.size();

            if (ArrayLength>=1) {
                // Apply non-maximum suppression procedure.
                float nmsThresh = 0.3f;




                MatOfFloat confidences = new MatOfFloat(Converters.vector_float_to_Mat(confs));


                Rect[] boxesArray = rects.toArray(new Rect[0]);

                MatOfRect boxes = new MatOfRect(boxesArray);

                MatOfInt indices = new MatOfInt();



                Dnn.NMSBoxes(boxes, confidences, confThreshold, nmsThresh, indices);


                // Draw result boxes:
                int[] ind = indices.toArray();
                for (int i = 0; i < ind.length; ++i) {

                    int idx = ind[i];
                    Rect box = boxesArray[idx];

                    int idGuy = clsIds.get(idx);

                    float conf = confs.get(idx);






                    List<String> cocoNames = Arrays.asList("person", "phone");
                    //List<String> cocoNames = Arrays.asList("person", "bicycle", "car", "motorcycle", "airplane", "bus", "train", "truck", "boat", "traffic light", "fire hydrant", "stop sign", "parking meter", "bench", "bird", "cat", "dog", "horse", "sheep", "cow", "elephant", "bear", "zebra", "giraffe", "backpack", "umbrella", "handbag", "tie", "suitcase", "frisbee", "skis", "snowboard", "sports ball", "kite", "baseball bat", "baseball glove", "skateboard", "surfboard", "tennis racket", "bottle", "wine glass", "cup", "fork", "knife", "spoon", "bowl", "banana", "apple", "sandwich", "orange", "broccoli", "carrot", "hot dog", "pizza", "doughnut", "cake", "chair", "sofa", "potted plant", "bed", "dining table", "toilet", "TV monitor", "laptop", "computer mouse", "remote control", "keyboard", "cell phone", "microwave", "oven", "toaster", "sink", "refrigerator", "book", "clock", "vase", "pair of scissors", "teddy bear", "hair drier", "toothbrush");


                    int intConf = (int) (conf * 100);



                    Imgproc.putText(frame,cocoNames.get(idGuy) + " " + intConf + "%",box.tl(),Core.FONT_HERSHEY_SIMPLEX, 2, new Scalar(255,255,0),2);
                    String className = cocoNames.get(idGuy).toString();

//                    if(detected.contains(className)){
//
//                    }
//                    else {
//                        //find positions
//                        //int centerX, centerY = centers[idGuy][0], centers[idGuy][1];
//                        //int centerX = (int) pz.get(i).first;
//                        int centerX = valueX.get(i);
//                        int centerY = (int) pz.get(i).second;
//                        String W_pos, H_pos;
//
//                        if (centerX <= W / 3) {
//                            W_pos = "left ";
//                        } else if (centerX <= ((W / 3) * 2)) {
//                            W_pos = "center ";
//                        } else {
//                            W_pos = "right ";
//                        }
////                        if (centerY <= H / 3) {
////                            H_pos = "top ";
////                        } else if (centerY <= (H / 3 * 2)) {
////                            H_pos = "mid ";
////                        } else {
////                            H_pos = "bottom ";
////                        }
////                        speak(H_pos + W_pos + className);
//                        text.add(className + W_pos);
//                        detected.add(className);


                   int centerX = valueX.get(i);
                    int centerY = (int) pz.get(i).second;
                    String W_pos, H_pos;

                    if (centerX <= W / 3) {
                        W_pos = " left";
                    } else if (centerX <= ((W / 3) * 2)) {
                        W_pos = " centre";
                    } else {
                        W_pos = " right";
                    }
                    text.add(className + W_pos);

                    Imgproc.rectangle(frame, box.tl(), box.br(), new Scalar(255, 0, 0), 2);
                }
                 if(text.size()>0){
                    try{

                        String description = text.toString();
                        speak(description);
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                        String description = text.toString();
//                        speak(description);

                
            
                }
            }

                   int centerX = valueX.get(i);
                    int centerY = (int) pz.get(i).second;
                    String W_pos, H_pos;

                    if (centerX <= W / 3) {
                        W_pos = " left";
                    } else if (centerX <= ((W / 3) * 2)) {
                        W_pos = " centre";
                    } else {
                        W_pos = " right";
                    }
                    text.add(className + W_pos);

                    Imgproc.rectangle(frame, box.tl(), box.br(), new Scalar(255, 0, 0), 2);
                }
                    speak(text);
                }
            }









        }

        return frame;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {


        if (startYolo == true){

            String tinyYoloCfg = Environment.getExternalStorageDirectory() + "/dnns/yolov3-tiny.cfg" ;
            String tinyYoloWeights = Environment.getExternalStorageDirectory() + "/dnns/yolov3-tiny.weights";

            tinyYolo = Dnn.readNetFromDarknet(tinyYoloCfg, tinyYoloWeights);


        }

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"Error!!!", Toast.LENGTH_SHORT).show();
        }

        else
        {
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraBridgeViewBase!=null){

            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
    }
}