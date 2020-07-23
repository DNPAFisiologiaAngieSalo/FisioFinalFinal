package com.example.fisiofinalfinal.OpenCV;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.*;


import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class ReconocimientoColor {
    private static final String  TAG              = "Print console";

    // Lower and Upper bounds for range checking in HSV color space
    private Scalar mLowerBound = new Scalar(0);
    private Scalar mUpperBound = new Scalar(0);
    // Minimum contour area in percent for contours filtering
    private static double mMinContourArea = 0.1;
    // Color radius for range checking in HSV color space
    private Scalar mColorRadius = new Scalar(25,50,50,0);
    private Mat mSpectrum = new Mat();
    private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();

    // Cache
    Mat mPyrDownMat = new Mat();
    Mat mHsvMat = new Mat();
    Mat mMask = new Mat();
    Mat mDilatedMask = new Mat();
    Mat mHierarchy = new Mat();

    public void setColorRadius(Scalar radius) {
        mColorRadius = radius;
    }

    public void setHsvColor(Scalar hsvColor) {//asigno un color en base a un escalar
        double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0]-mColorRadius.val[0] : 0;
        double maxH = (hsvColor.val[0]+mColorRadius.val[0] <= 255) ? hsvColor.val[0]+mColorRadius.val[0] : 255;

        mLowerBound.val[0] = minH;
        mUpperBound.val[0] = maxH;

        mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
        mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

        mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
        mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];

        mLowerBound.val[3] = 0;
        mUpperBound.val[3] = 255;

        Mat spectrumHsv = new Mat(1, (int)(maxH-minH), CvType.CV_8UC3);

        for (int j = 0; j < maxH-minH; j++) {
            byte[] tmp = {(byte)(minH+j), (byte)255, (byte)255};
            spectrumHsv.put(0, j, tmp);
        }

        Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);
    }

    public Mat getSpectrum() {
        return mSpectrum;
    }

    public void setMinContourArea(double area) {
        mMinContourArea = area;
    }

    public void process(Mat rgbaImage) {
        Imgproc.pyrDown(rgbaImage, mPyrDownMat);
        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);

        Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);

        Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
        Imgproc.dilate(mMask, mDilatedMask, new Mat());

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);


        //Dibujar centro de los contornos
        List<Moments> mu = new ArrayList<Moments>(contours.size());
        List<Point> puntos = new ArrayList<Point>(contours.size());
        for (int i = 0; i < contours.size(); i++) {
            mu.add(i, Imgproc.moments(contours.get(i), false));
            Moments p = mu.get(i);

            int x = (int) (p.get_m10() / p.get_m00());
            int y = (int) (p.get_m01() / p.get_m00());

            //Imgproc.putText(rgbaImage, "Bola", new Point(x*4, y*4), 3, 0.5, new Scalar(255, 0, 0, 255), 1);
            Log.e(TAG, "X " + (x*4) +"  Y  "+(y*4));
            //guardo los puntos en base a las coordenadas
            puntos.add(new Point(x*4,y*4));//se multiplica * 4 por se un celular huawei (por que su coordenada X y Y son diferentes)
        }

        if(puntos.size() > 2) {
            //Dibujar Lineas
            Scalar lineColor = new Scalar(255, 0, 0, 255);
            ;
            int lineWidth = 3;
            ;
            for (int i = 0; i < contours.size() - 1; i++) {
                Imgproc.line(rgbaImage, puntos.get(i), puntos.get(i + 1), lineColor, lineWidth);
            }
            //ultima linea (dibujo del primer punto hasta el ultimo - cierro el poligono)
            if (contours.size() < 4)
            Imgproc.line(rgbaImage, puntos.get(0), puntos.get(contours.size() - 1), lineColor, lineWidth);

            //angulos en base a triangulos
            List<Float> angulos = new ArrayList<Float>(contours.size());
            int tamanio = contours.size();

            for (int i = 0; i < tamanio - 2; i++) {
                //calcular distancias angulos opuestos, suma de cuadrados
                double a2 = Math.pow(puntos.get(i + 1).x - puntos.get(i + 2).x, 2) + Math.pow(puntos.get(i + 1).y - puntos.get(i + 2).y, 2);
                double b2 = Math.pow(puntos.get(i).x - puntos.get(i + 2).x, 2) + Math.pow(puntos.get(i).y - puntos.get(i + 2).y, 2);
                double c2 = Math.pow(puntos.get(i).x - puntos.get(i + 1).x, 2) + Math.pow(puntos.get(i).y - puntos.get(i + 1).y, 2);

                float a = (float) Math.sqrt(a2);
                float b = (float) Math.sqrt(b2);
                float c = (float) Math.sqrt(c2);

                float alpha = (float) Math.acos((b2 + c2 - a2)/(2*b*c));
                float betta = (float) Math.acos((a2 + c2 - b2)/(2*a*c));
                float gamma = (float) Math.acos((a2 + b2 - c2)/(2*a*b));

                // Converting to degree
                alpha = (float) (alpha * 180 / Math.PI);
                betta = (float) (betta * 180 / Math.PI);
                gamma = (float) (gamma * 180 / Math.PI);


                //Imprimir
                int fuente = 3;
                double tamanio_fuente = 1;
                Scalar color = new Scalar(255,0,0,255);
                int grosor =2;

                //solo si es triangulo
                if(tamanio == 3) {
                    //angulos mas alejados
                    Imgproc.putText(rgbaImage, Float.toString(alpha), puntos.get(i), fuente, tamanio_fuente, color, grosor);
                    Imgproc.putText(rgbaImage, Float.toString(gamma), puntos.get(i + 2), fuente, tamanio_fuente, color, grosor);
                }
                //dibuja el angulo del  centro del triangulo o angulo
                Imgproc.putText(rgbaImage, Float.toString(betta), puntos.get(i + 1), fuente, tamanio_fuente, color, grosor);

                //ver si flexiona la rodilla de buena manera
                Scalar colorMalo= new Scalar(255,0,0,255);
                Scalar colorBueno= new Scalar(110,200,0,80);
                tamanio_fuente = 1;
                grosor =1;
                if(tamanio > 3  && i == 1) {
                    if (betta < 160 && betta > 60)
                        Imgproc.putText(rgbaImage, "FLEXION CORRECTA!", new Point(10, 580), fuente, tamanio_fuente, colorBueno, grosor);
                    else
                        Imgproc.putText(rgbaImage, "MALA FLEXION",new Point(10, 580), fuente, tamanio_fuente, colorMalo, grosor);
                }
            }

        }

        // Find max contour area
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea)
                maxArea = area;
        }

        // Filter contours by area and resize to fit the original image size
        mContours.clear();
        each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            if (Imgproc.contourArea(contour) > mMinContourArea*maxArea) {
                Core.multiply(contour, new Scalar(4,4), contour);
                mContours.add(contour);
            }
        }
    }

    public List<MatOfPoint> getContours() {
        return mContours;
    }
}