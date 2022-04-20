package hu.petrik.koblog.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import hu.petrik.koblog.R;

public class VPAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater inflater;

    public VPAdapter(Context context) {
        this.context = context;
    }

    private int images[] ={
            R.drawable.p1,
            R.drawable.p2,
            R.drawable.p3,
    };

    private String titles[] ={
            "Ötletelj",
            "Alkoss",
            "Posztolj"
    };

    private String descs[] ={
            "Szerezz inspirációt az új általad készített díszhez! ",
            "Mutasd meg másoknak mit készítettél!",
            "Kérd ki mások véleményét, hogyan valósítsd meg elképzeléseid!"
    };

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.view_pager,container,false);

        ImageView imageView = v.findViewById(R.id.imgViewPager);
        TextView txtTitle = v.findViewById(R.id.txtTitleViewPager);
        TextView txtDesc = v.findViewById(R.id.txtDescViewPager);

        imageView.setImageResource(images[position]);
        txtTitle.setText(titles[position]);
        txtDesc.setText(descs[position]);

        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
