/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.SessionEvents.AuthListener;
import com.facebook.android.SessionEvents.LogoutListener;


public class Example extends Activity {

    // Your Facebook Application ID must be set before running this example
    // See http://www.facebook.com/developers/createapp.php
    public static final String APP_ID = "290486764403784";

    private TextView mSearchTextView;
    private EditText mTitleEditText;
    private Spinner mTypeSpinner;
    private Button mSearchButton;
    private TextView mText;

    private Facebook mFacebook;
    //private AsyncFacebookRunner mAsyncRunner;
    
    private static final String TAG_TITLE = "title";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_YEAR = "year";
    private static final String TAG_DIRECTOR = "director";
    private static final String TAG_RATING = "rating";
    private static final String TAG_LINK = "link";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (APP_ID == null) {
            Util.showAlert(this, "Warning", "Facebook Applicaton ID must be " +
                    "specified before running this example: see Example.java");
        }

        setContentView(R.layout.main);
        mSearchTextView = (TextView) Example.this.findViewById(R.id.searchTextView);
        mTitleEditText = (EditText) findViewById(R.id.titleEditText);
        mTypeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        mSearchButton = (Button) findViewById(R.id.searchButton);
        mText = (TextView) Example.this.findViewById(R.id.txt);

       	mFacebook = new Facebook(APP_ID);
       	//mAsyncRunner = new AsyncFacebookRunner(mFacebook);

        SessionStore.restore(mFacebook, this);
        //SessionEvents.addAuthListener(new SampleAuthListener());
        //SessionEvents.addLogoutListener(new SampleLogoutListener());
        
        //mSearchTextView.setVisibility(mFacebook.isSessionValid() ?
          //      View.VISIBLE :
          //      View.INVISIBLE);
        
       // mTitleEditText.setVisibility(mFacebook.isSessionValid() ?
        //        View.VISIBLE :
        //        View.INVISIBLE);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner.setAdapter(adapter);
       // mTypeSpinner.setVisibility(mFacebook.isSessionValid() ?
	    //        View.VISIBLE :
	     //       View.INVISIBLE);

        mSearchButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	//String urlString = "http://cs-server.usc.edu:10829/examples/servlet/movie_search?"
            	//		+ "title=" + mTitleEditText.getText().toString().replace(' ', '+')
            	//		+ "&type=" + mTypeSpinner.getSelectedItem().toString();
            	
            	String movieName = mTitleEditText.getText().toString().replace(' ', '+');
                String typeName = mTypeSpinner.getSelectedItem().toString().replace(' ', '+');
                
                if (typeName.equals("All+Types"))
                {
                	typeName = "feature,tv_series,game";
                }
                if (typeName.equals("Feature"))
                {
                	typeName = "feature";
                }
                if (typeName.equals("TV+Series"))
                {
                	typeName = "tv_series";
                }
                if (typeName.equals("Video+Game"))
                {
                	typeName = "game";
                }
                
                //mText.setText(typeName);
                
                String myServer = "http://cs-server.usc.edu:10829/examples/servlet/movie_search?";
                String searchURL = myServer + "title=" + movieName + "&type=" + typeName;
                
                if(movieName.compareTo("") == 0) 
                {
                	Toast.makeText(getApplicationContext(), "Please enter the movie title", Toast.LENGTH_SHORT).show();
                }
                
                else
                {
                	HttpGet xhr = null;
                	try 
                	{
                		xhr = new HttpGet(searchURL);
                	} 
                	catch (IllegalArgumentException e) 
                	{
                		e.printStackTrace();
                		
                	}
                	if(xhr!=null)
                	{
                		StringBuilder builder = new StringBuilder();
                		JSONObject movies = new JSONObject();
                		JSONObject movieArray = new JSONObject();
                		String result = "";
                		try 
                		{               			
                    		HttpClient client = new DefaultHttpClient();
                    		HttpResponse response = client.execute(xhr);
                    		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"));
                    		String line;
                    		while((line = bufferedReader.readLine()) != null)
                    		{
                    			builder.append(line);
                    		}
                    		result = builder.toString();
                    		
                    		// check if the result is complete JSON file
                    		if(result.indexOf(TAG_TITLE) == -1)
                    		{
                    			Toast.makeText(Example.this, "no movies found, please check the name and type", Toast.LENGTH_LONG).show();
                    		}
                    		else
                    		{
                    			
                    			movies = new JSONObject(result);
                        		movieArray = new JSONObject(movies.getString("movies"));
                        		JSONArray movie = movieArray.getJSONArray("movie");
                        		
                        		int length = movie.length();
                        		String[] image = new String[length];
            	            	String[] title = new String[length];
            	            	String[] year = new String[length];
            	            	String[] director = new String[length];
            	            	String[] rating = new String[length];
            	            	String[] link = new String[length];
            	            	
            	            	for (int i=0; i<length; i++) 
            	            	{
            	            		JSONObject h = movie.getJSONObject(i);
            	            		image[i] = h.getString("image");
            	            		title[i] = h.getString("title");
            	            		year[i] = h.getString("year");
            	            		director[i] = h.getString("director");
            	            		rating[i] = h.getString("rating");
            	            		link[i] = h.getString("link");
            	            	}
            	            	
            	            	Intent secondScreen = new Intent(getApplicationContext(), MovieListView.class);
            	            	secondScreen.putExtra(TAG_TITLE, title);
            	            	secondScreen.putExtra(TAG_IMAGE, image);
            	            	secondScreen.putExtra(TAG_YEAR, year);
            	            	secondScreen.putExtra(TAG_DIRECTOR, director);
            	            	secondScreen.putExtra(TAG_RATING, rating);
            	            	secondScreen.putExtra(TAG_LINK, link);
            	            	
            	            	startActivity(secondScreen);
                    		}
                		}
                		catch (ClientProtocolException e) 
                		{
    						e.printStackTrace();
    					}
                		catch (IOException e)
    					{
    						e.printStackTrace();
    					}
                		catch (JSONException e)
                		{
        					e.printStackTrace();
        				}
                		
    					}
                		
                	}
                }
        });
        //mSearchButton.setVisibility(mFacebook.isSessionValid() ?
            //    View.VISIBLE :
             //   View.INVISIBLE);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        mFacebook.authorizeCallback(requestCode, resultCode, data);
    }

    public class SampleAuthListener implements AuthListener {

        public void onAuthSucceed() {
        	mSearchTextView.setVisibility(View.VISIBLE);
            mTitleEditText.setVisibility(View.VISIBLE);
            mTypeSpinner.setVisibility(View.VISIBLE);
            mSearchButton.setVisibility(View.VISIBLE);
            mText.setText("You have logged in! ");
        }

        public void onAuthFail(String error) {
            mText.setText("Login Failed: " + error);
        }
    }

    public class SampleLogoutListener implements LogoutListener {
        public void onLogoutBegin() {
            mText.setText("Logging out...");
        }

        public void onLogoutFinish() {
        	mSearchTextView.setVisibility(View.INVISIBLE);
            mTitleEditText.setVisibility(View.INVISIBLE);
            mTypeSpinner.setVisibility(View.INVISIBLE);
            mSearchButton.setVisibility(View.INVISIBLE);
            mText.setText("You have logged out! ");
        }
    }*/

}