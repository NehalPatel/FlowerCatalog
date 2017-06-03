package com.tecknic.nehal.flowercatalog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tecknic.nehal.flowercatalog.model.Flower;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class FlowerAdapter extends ArrayAdapter<Flower> {

    private Context context;
    private List<Flower> flowerList;
    private LruCache<Integer, Bitmap> imageCache;

    public FlowerAdapter(Context context, int resource, List<Flower> objects) {
        super(context, resource, objects);
        this.context = context;
        this.flowerList = objects;

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory/8;
        imageCache = new LruCache<>(cacheSize);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_flower, parent, false);

        Flower flower = flowerList.get(position);
        TextView tv = (TextView) view.findViewById(R.id.textView1);
        tv.setText(flower.getName());

        TextView pv = (TextView) view.findViewById(R.id.textViewPriceValue);
        double price = flower.getPrice();
        pv.setText(String.valueOf(price) + "$");


        Bitmap bitmap = imageCache.get(flower.getProductId());
        if(bitmap != null){
            ImageView iv = (ImageView) view.findViewById(R.id.imageView1);
            iv.setImageBitmap(flower.getBitmap());
        } else{
            FlowerAndView container = new FlowerAndView();
            container.flower = flower;
            container.view = view;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }

        return view;
    }

    class FlowerAndView{
        public Flower flower;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<FlowerAndView, Void, FlowerAndView>{


        @Override
        protected FlowerAndView doInBackground(FlowerAndView... params) {
            FlowerAndView container = params[0];
            Flower flower = container.flower;

            try {
                String imageUrl = MainActivity.PHOTOS_BASE_URL + flower.getPhoto();
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                flower.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;

                return container;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(FlowerAndView flowerAndView) {
            ImageView iv = (ImageView) flowerAndView.view.findViewById(R.id.imageView1);
            iv.setImageBitmap(flowerAndView.bitmap);
            //flowerAndView.flower.setBitmap(flowerAndView.bitmap);

            imageCache.put(flowerAndView.flower.getProductId(), flowerAndView.bitmap);
        }
    }
}
