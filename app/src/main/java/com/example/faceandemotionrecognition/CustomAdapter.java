package com.example.faceandemotionrecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.faceandemotionrecognition.R;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Accessory;
import com.microsoft.projectoxford.face.contract.Emotion;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.contract.FacialHair;
import com.microsoft.projectoxford.face.contract.Hair;
import com.microsoft.projectoxford.face.contract.HeadPose;
import com.microsoft.projectoxford.face.contract.Makeup;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class CustomAdapter extends BaseAdapter {

    private Face[] faces;
    private Context context;
    private LayoutInflater inflater;
    private Bitmap orig;

    public CustomAdapter(Face[] faces, Context context, Bitmap orig) {
        this.faces = faces;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.orig = orig;
    }


    @Override
    public int getCount() {
        return faces.length;
    }

    @Override
    public Object getItem(int position) {
        return faces[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = inflater.inflate(R.layout.listview_layout, null);
        }

        TextView age, gender, hair, facialHair, makeup, glasses, accesories, eyesOccluded, mouthOccluded, emotionPrincipal, emotion1, emotion2;
        ImageView imageView;

        age = view.findViewById(R.id.textAge);
        gender = view.findViewById(R.id.textGender);
        hair = view.findViewById(R.id.textHair);
        facialHair = view.findViewById(R.id.textFacialHair);
        makeup = view.findViewById(R.id.textMakeup);
        glasses = view.findViewById(R.id.textGlasses);
        accesories = view.findViewById(R.id.textAccesories);
        eyesOccluded = view.findViewById(R.id.textEyesOccluded);
        mouthOccluded = view.findViewById(R.id.textMouthOccluded);
        emotionPrincipal = view.findViewById(R.id.textEmotionPrincipal);
        emotion1 = view.findViewById(R.id.emotion1);
        emotion2 = view.findViewById(R.id.emotion2);


        imageView = view.findViewById(R.id.imgThumb);
        String edad = "" + faces[position].faceAttributes.age;
        if (edad.length() == 4) {
            edad = edad.substring(0,2);
        }
        if (edad.length() == 3) {
            edad=edad.substring(0,1);

        }

        age.setText(edad);
        gender.setText("" + faces[position].faceAttributes.gender);
        hair.setText("" + getHair(faces[position].faceAttributes.hair));
        facialHair.setText("" + getFacialHair(faces[position].faceAttributes.facialHair));
        makeup.setText("" + getMakeup(faces[position].faceAttributes.makeup));
        glasses.setText("" + faces[position].faceAttributes.glasses);
        accesories.setText("" + getAccessories(faces[position].faceAttributes.accessories));
        eyesOccluded.setText("" + faces[position].faceAttributes.occlusion.eyeOccluded);
        mouthOccluded.setText("" + faces[position].faceAttributes.occlusion.mouthOccluded);


        TreeMap<Double, String> treeMap = new TreeMap<>();
        treeMap.put(faces[position].faceAttributes.emotion.happiness, "Happiness");
        treeMap.put(faces[position].faceAttributes.emotion.anger, "Anger");
        treeMap.put(faces[position].faceAttributes.emotion.disgust, "Disgust");
        treeMap.put(faces[position].faceAttributes.emotion.sadness, "Sadness");
        treeMap.put(faces[position].faceAttributes.emotion.neutral, "Neutral");
        treeMap.put(faces[position].faceAttributes.emotion.surprise, "Surprise");
        treeMap.put(faces[position].faceAttributes.emotion.fear, "Fear");

        ArrayList<Double> arrayList = new ArrayList<>();
        TreeMap<Integer, String> rank = new TreeMap<>();

        int counter = 0;
        for (Map.Entry<Double, String> entry : treeMap.entrySet()) {
            String key = entry.getValue();
            Double value = entry.getKey();
            rank.put(counter, key);
            counter++;
            arrayList.add(value);
        }

        DecimalFormat df = new DecimalFormat("0.00");

        emotionPrincipal.setText(rank.get(rank.size() - 1) + "  " + df.format(100 * arrayList.get(rank.size() - 1)) + "%");
        if (rank.get(rank.size() - 2) != null) {
            emotion1.setText(rank.get(rank.size() - 2) + ": " + df.format(100 * arrayList.get(rank.size() - 2)) + "%");
        }
        if (rank.get(rank.size() - 3) != null) {
            emotion2.setText(rank.get(rank.size() - 3) + ": " + df.format(100 * arrayList.get(rank.size() - 3)) + "%");
        }
        FaceRectangle faceRectangle = faces[position].faceRectangle;
        Bitmap bitmap = Bitmap.createBitmap(orig, faceRectangle.left, faceRectangle.top, faceRectangle.width, faceRectangle.height);

        imageView.setImageBitmap(bitmap);

        return view;
    }

    private String getFacialHair(FacialHair facialHair) {
        return (facialHair.moustache + facialHair.beard + facialHair.sideburns > 0) ? "Yes" : "No";
    }

    private String getMakeup(Makeup makeup) {
        return (makeup.eyeMakeup || makeup.lipMakeup) ? "Yes" : "No";
    }

    private String getEmotion(Emotion emotion) {
        String emotionType = "";
        double emotionValue = 0.0;
        if (emotion.anger > emotionValue) {
            emotionValue = emotion.anger;
            emotionType = "Anger";
        }
        if (emotion.contempt > emotionValue) {
            emotionValue = emotion.contempt;
            emotionType = "Contempt";
        }
        if (emotion.disgust > emotionValue) {
            emotionValue = emotion.disgust;
            emotionType = "Disgust";
        }
        if (emotion.fear > emotionValue) {
            emotionValue = emotion.fear;
            emotionType = "Fear";
        }
        if (emotion.happiness > emotionValue) {
            emotionValue = emotion.happiness;
            emotionType = "Happiness";
        }
        if (emotion.neutral > emotionValue) {
            emotionValue = emotion.neutral;
            emotionType = "Neutral";
        }
        if (emotion.sadness > emotionValue) {
            emotionValue = emotion.sadness;
            emotionType = "Sadness";
        }
        if (emotion.surprise > emotionValue) {
            emotionValue = emotion.surprise;
            emotionType = "Surprise";
        }
        return String.format("%s: %f", emotionType, 100 * emotionValue);
    }

    private String getHeadPose(HeadPose headPose) {
        return String.format("Pitch: %s, Roll: %s, Yaw: %s", headPose.pitch, headPose.roll, headPose.yaw);
    }

    private String getAccessories(Accessory[] accessories) {
        if (accessories.length == 0) {
            return "NoAccessories";
        } else {
            String[] accessoriesList = new String[accessories.length];
            for (int i = 0; i < accessories.length; ++i) {
                accessoriesList[i] = accessories[i].type.toString();
            }

            return TextUtils.join(",", accessoriesList);
        }
    }

    private String getHair(Hair hair) {
        if (hair.hairColor.length == 0) {
            if (hair.invisible)
                return "Invisible";
            else
                return "Bald";
        } else {
            int maxConfidenceIndex = 0;
            double maxConfidence = 0.0;

            for (int i = 0; i < hair.hairColor.length; ++i) {
                if (hair.hairColor[i].confidence > maxConfidence) {
                    maxConfidence = hair.hairColor[i].confidence;
                    maxConfidenceIndex = i;
                }
            }

            return hair.hairColor[maxConfidenceIndex].color.toString();
        }
    }
}
