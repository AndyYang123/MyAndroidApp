package com.facebook.android;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class MovieArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] image;
	private final String[] title;
	private final String[] rating;
 
	public MovieArrayAdapter(Context context, String[] image, String[] title, String[] rating) {
		super(context, R.layout.list_item, title);
		this.context = context;
		this.title = title;
		this.image = image;
		this.rating = rating;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View row = inflater.inflate(R.layout.list_item, parent, false);
		
		TextView textView1 = (TextView) row.findViewById(R.id.titleList);
		textView1.setText(title[position]);
		
		TextView textView2 = (TextView) row.findViewById(R.id.ratingList);
		textView2.setText("Rating:" + rating[position]);
		
		ImageView imageView = (ImageView) row.findViewById(R.id.imageList);
		try 
		{
			URL url = new URL(image[position]);
			InputStream in = (InputStream) url.getContent();
			Bitmap bMap = BitmapFactory.decodeStream(in);
            imageView.setImageBitmap(bMap);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return row;
	}
}
