package com.github.yeriomin.smsscheduler.Activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.yeriomin.smsscheduler.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by waqarbscs on 8/11/2016.
 */
public class SelectUserAdapter  extends BaseAdapter {
    public List<SelectUser> _data;
    private ArrayList<SelectUser> arraylist;
    Context _c;
    ViewHolder v;
    RoundImage roundedImage;
    private TextView mTxtAmountAdapter;
    int b;
    CheckBox cc;


    public SelectUserAdapter(List<SelectUser> selectUsers, Context context,TextView numCount,int bb,CheckBox abc) {
        _data = selectUsers;
        _c = context;
        this.arraylist = new ArrayList<SelectUser>();
        this.arraylist.addAll(_data);
        mTxtAmountAdapter=numCount;
        b=bb;
        cc=abc;
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    @Override
    public Object getItem(int i) {
        return _data.get(i);
    }



    @Override
    public long getItemId(int i) {
        return i;
    }

    ArrayList<String> vn=new ArrayList<>();
    ArrayList<String> vp=new ArrayList<>();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;

        if (view == null) {
            LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.contact_list, null);
            Log.e("Inside", "here--------------------------- In view1");
        } else {
            view = convertView;
            Log.e("Inside", "here--------------------------- In view2");
        }

        v = new ViewHolder();
        v.title = (TextView) view.findViewById(R.id.name);
        v.check = (CheckBox) view.findViewById(R.id.check);
        v.phone = (TextView) view.findViewById(R.id.no);
        v.imageView = (ImageView) view.findViewById(R.id.pic);




        final SelectUser data = (SelectUser) _data.get(i);
        v.title.setText(data.getName());
        if(cc.isChecked()){
            v.check.setChecked(true);
            mTxtAmountAdapter.setText(b+"/"+b+" selected");
            data.setCheckedBox(true);
        }else if(!cc.isChecked()) {
            //v.check.setChecked(false);
            //v.check.setChecked(data.getCheckedBox());
            data.setCheckedBox(false);
            mTxtAmountAdapter.setText(c+"/"+b+" selected");
        }
        v.phone.setText(data.getPhone());


        // Set image if exists
        try {

            if (data.getThumb() != null) {
                v.imageView.setImageBitmap(data.getThumb());
            } else {
                v.imageView.setImageResource(R.drawable.image);
            }
            // Seting round image
            Bitmap bm = BitmapFactory.decodeResource(view.getResources(),R.drawable.image); // Load default image
            roundedImage = new RoundImage(bm);
            v.imageView.setImageDrawable(roundedImage);
        } catch (OutOfMemoryError e) {
            // Add default picture
            v.imageView.setImageDrawable(this._c.getDrawable(R.drawable.image));
            e.printStackTrace();
        }

        Log.e("Image Thumb", "--------------" + data.getThumb());
        // Set check box listener android
        v.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    data.setCheckedBox(true);
                    c++;
                    mTxtAmountAdapter.setText(c+"/"+b+" selected");
                } else {
                    data.setCheckedBox(false);
                    c--;
                    if(c<0){
                        c=0;
                    }
                    mTxtAmountAdapter.setText(c+"/"+b+" selected");
                }
                if(c==b){
                    cc.setChecked(true);
                }else{
                    cc.setChecked(false);

                }
            }
        });
        /*
        SharedPreferences phoneP=_c.getSharedPreferences("phoneP",0);
        SharedPreferences.Editor phoneE=phoneP.edit();

        for(int p=0;i<vp.size();i++)
        {
            phoneE.putString("val"+i,vp.get(p));
        }
        phoneE.putInt("size",vp.size());
        phoneE.commit();
        */
        view.setTag(data);
        return view;

    }

    int c=0;

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        _data.clear();
        if (charText.length() == 0) {
            _data.addAll(arraylist);
        } else {
            for (SelectUser wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    _data.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
    static class ViewHolder {
        ImageView imageView;
        TextView title, phone;
        CheckBox check;
    }
}
