package com.facebook.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.lang.Float;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
 
public class MovieListView extends ListActivity
{
	public static final String APP_ID = "290486764403784";
	
	private Context context = this;
	private String[] image;
	private String[] title;
	private String[] year;
	private String[] director;
	private String[] rating;
	private String[] link;
	private int globalPosition;
	
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsyncRunner;
	
	private static final String TAG_IMAGE = "image";
    private static final String TAG_TITLE = "title";
    private static final String TAG_YEAR = "year";
    private static final String TAG_DIRECTOR = "director";
    private static final String TAG_RATING = "rating";
    private static final String TAG_LINK = "link";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		if (APP_ID == null)
		{
            Util.showAlert(this, "Warning", "Facebook Applicaton ID must be " + "specified before running this example: see HotelListView.java");
        }

		image = getIntent().getStringArrayExtra(TAG_IMAGE);
		title = getIntent().getStringArrayExtra(TAG_TITLE);
		year =  getIntent().getStringArrayExtra(TAG_YEAR);
		director = getIntent().getStringArrayExtra(TAG_DIRECTOR);
		rating = getIntent().getStringArrayExtra(TAG_RATING);
		link = getIntent().getStringArrayExtra(TAG_LINK);
		setListAdapter(new MovieArrayAdapter(this, image, title, rating));

		mFacebook = new Facebook(APP_ID);
       	mAsyncRunner = new AsyncFacebookRunner(mFacebook);
		
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
	}
	
	@Override    
	protected void onListItemClick(ListView lv, View view, int position, long id) {
		AlertDialog alertDialog = getDialog(position);
    	alertDialog.show(); 
	}
		
	private AlertDialog getDialog(final int position) 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	 
	    LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
	    View layout = inflater.inflate(R.layout.custom_dialog, (ViewGroup) findViewById(R.id.dialog));
	   		
    	ImageView image2 = (ImageView) layout.findViewById(R.id.imageDialog);
    	try 
		{
			URL url = new URL(image[position]);
			InputStream in = (InputStream) url.getContent();
			Bitmap bMap = BitmapFactory.decodeStream(in);
			image2.setImageBitmap(bMap);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
    	
    	TextView title2 = (TextView) layout.findViewById(R.id.titleDialog);
	   	title2.setText("Name:" + title[position]);
    	
    	TextView year2 = (TextView) layout.findViewById(R.id.yearDialog);
    	year2.setText("Year:" + year[position]);
    			
    	TextView director2 = (TextView) layout.findViewById(R.id.directorDialog);
    	director2.setText("Director:" + director[position]);   
    	
    	TextView rating2 = (TextView) layout.findViewById(R.id.ratingDialog);
    	rating2.setText("Rating:" + rating[position] + "/10"); 
    	
    	Button fbButton = (Button) layout.findViewById(R.id.postDialog);
    	fbButton.setOnClickListener(new OnClickListener() 
    	{
			@Override
			public void onClick(View v) 
			{
				Bundle params = new Bundle();

    			params.putString("name", title[position]);
    			params.putString("link", link[position]);
    			params.putString("picture", image[position]);
    			params.putString("caption", "I am interested in this movie/series/game");
    			params.putString("description", title[position] + " released in " + year[position] + " has a rating of " + rating[position]);
    			params.putString("properties", "{ 'Look at users reviews': { 'text': 'here', 'href': '" +  link[position] + "reviews' } }");
    			mFacebook.dialog(MovieListView.this, "feed", params, new SampleDialogListener());
			}
		});
    	
	   	builder.setView(layout);    	
	   	return builder.create();
	}		
				
	  
	 public class SampleDialogListener extends BaseDialogListener {
	        public void onComplete(Bundle values) {
	            final String postId = values.getString("post_id");
	            if (postId != null) {
	                Log.d("Facebook-Example", "Dialog Success! post_id=" + postId);
	                Toast.makeText(getApplicationContext(), "Post succeed", Toast.LENGTH_SHORT).show();
	            } else {
	                Log.d("Facebook-Example", "No wall post made");
	                Toast.makeText(getApplicationContext(), "Post failed", Toast.LENGTH_SHORT).show();
	            }
	        }
	    }
 
}


