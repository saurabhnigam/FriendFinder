package squares.four.activity;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import squares.four.R;
import squares.four.Keys.Keys;
import squares.four.util.LocationHelper;
import squares.four.util.Utility;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import foursquare4j.exception.FoursquareException;
import foursquare4j.oauth.FoursquareOAuthImpl;
import foursquare4j.type.Checkin;
import foursquare4j.type.Venue;
import foursquare4j.type.VenueGroup;
import foursquare4j.type.Venues;
 
public class OAuth extends Activity{

	
    private final String TAG = "OAuth"; 
    private static final String callbackUrl = "squaresfourty:///";
    
    private static String tokenSecret;
    private static OAuthConsumer consumer;
    private static OAuthProvider provider;
    private static ArrayList<LocationHelper> ListData;
    private static LocationManager lm ;
     
    public void onCreate(Bundle b)
    {
    	super.onCreate(b);
    	setContentView(R.layout.pull_to_refresh_header);
    	onRefresh();
		if(Utility.areKeysAvailable()
				&& Utility.getAuthTokens(OAuth.this).getString(Keys.USER_SECRET, null) != null 
				&& Utility.getAuthTokens(OAuth.this).getString(Keys.USER_TOKEN, null) != null)
		{
			FoursquareOAuthImpl fs = Utility.authSetupFromTokens
			(Utility.getAuthTokens(OAuth.this).getString(Keys.USER_SECRET, null), 
					Utility.getAuthTokens(OAuth.this).getString(Keys.USER_TOKEN, null),
					OAuth.this);
		
	       processingData(fs);
		}
		else
		{

	    	consumer = new CommonsHttpOAuthConsumer(Keys.CONSUMER_KEY, Keys.CONSUMER_SECRET);
	
	        provider = new CommonsHttpOAuthProvider(Keys.FOURSQUARE_OAUTH_REQUEST_TOKEN,
	        		Keys.FOURSQUARE_OAUTH_ACCESS_TOKEN,Keys.FOURSQUARE_OAUTH_AUTHORIZE);
	        
	        provider.setOAuth10a(true);
	        
	        Log.d(TAG ,"Fetching request token from Twitter...");
	
	        // we do support callbacks
	        String authUrl = null;
			try {
	/*			authUrl = provider.retrieveRequestToken(consumer, OAuth.OUT_OF_BAND);*/
				authUrl = provider.retrieveRequestToken(consumer, callbackUrl);
				Log.d(TAG,"Now visit:\n" + authUrl + "\n... and grant this app authorization");
			} catch (Exception e) {
				e.printStackTrace();
			} 
	
	        tokenSecret = consumer.getTokenSecret();
	        Log.d(TAG,"Request token: " + consumer.getToken());
	        Log.d(TAG,"Token secret: " + tokenSecret);
	    	
	      //starting webview
	    	Intent intent = new Intent(OAuth.this , WebviewAuth.class);
	    	intent.putExtra("auth_url", authUrl);
	    	startActivityForResult(intent , 1);
		
		}
}
    
	    @Override
	    public void onNewIntent(Intent intent)
	    {
	    	super.onNewIntent(intent);
	    	Log.d(TAG , "in on new intent");
	    	String access_token = null;
			// extract the OAUTH access token if it exists

			Uri uri = intent.getData();
		    if(uri != null) 
		    {
		      access_token = uri.getQueryParameter("oauth_token");
		      String oauth_verifier = uri.getQueryParameter("oauth_verifier");
		      
		      try {
				provider.retrieveAccessToken(consumer ,oauth_verifier);
				
			      //now store credentials in shared preferences
			      Utility.writeTokens(this, consumer.getTokenSecret(), consumer.getToken());
			} catch (OAuthMessageSignerException e1) {
				e1.printStackTrace();
			} catch (OAuthNotAuthorizedException e1) {
				e1.printStackTrace();
			} catch (OAuthExpectationFailedException e1) {
				e1.printStackTrace();
			} catch (OAuthCommunicationException e1) {
				e1.printStackTrace();
			} 
		      Log.d(TAG , "access token is "+access_token);

		      
		      //TODO Below code can be removed after testing is done
		      ///Using consumer to use foursquared APIs
		       FoursquareOAuthImpl fs = Utility.authSetup(consumer , OAuth.this);

		       processingData(fs);
		    }
		      
	    }
	    
	    
	    public void processingData(FoursquareOAuthImpl foursquare )
	    {
	    	String textdata = null;

	        try {
	            ListData = new ArrayList<LocationHelper>();
	            
            	//for friend's data
            	for (Checkin checkin : foursquare.checkins(null, null))
	            {
	                    Log.d(TAG, "checkin is "+String.format("%s\n", checkin.getDisplay()));
	                    Venue venue = checkin.getVenue();
	                    String geoLat = venue.getGeolat();
	                    String geoLong = venue.getGeolong();
	                    String friendAtLoc =checkin.getDisplay();
	                    String picUrl = checkin.getUser().getPhoto();
	                    
	                    textdata+=(checkin.getDisplay()+"\n" + "with geolat"+geoLat+"& long "+geoLong);
	                    
	                    ListData.add(new LocationHelper(geoLat ,  geoLong, friendAtLoc ,picUrl, "true"));
	            }
	            
	            //get last location first because its fast & get current location later in SideMapper
	    		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
	    		Location lastLoc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    		
	    		//check if its null
	    		if(lastLoc != null)
		    		{
		            Venues nearByVenues=foursquare.venues(Double.toString(lastLoc.getLatitude()),
		            		Double.toString(lastLoc.getLongitude()), 
		            		null, null);
		            Collection c = nearByVenues.values();
		            
		            //obtain an Iterator for Collection
		            Iterator itr = c.iterator();
	            	VenueGroup vG = (VenueGroup) itr.next();
		            //iterate through HashMap values iterator
	
	
	            	//for nearby places
		            for (Venue venue : vG)
		            {
		                    String geoLat = venue.getGeolat();
		                    String geoLong = venue.getGeolong();
		                    String venueName =venue.getName();
		                    Log.d(TAG, "nearby venue is "+venueName);
		                    ListData.add(new LocationHelper(geoLat ,  geoLong, venueName , null,"false"));
		            }
		    		}
	            callNextActivity();
	        } catch (FoursquareException e) {
	                Log.e(TAG, "Error ",e);
	        }
			Log.d(TAG ,"textdata is"+textdata);
	    }
	    
		/****calls next activity*/
		public void callNextActivity()
		{
			Intent callIntent = new Intent(OAuth.this, SideMapper.class);
			callIntent.putParcelableArrayListExtra("arraylist", ListData);
			callIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(callIntent);	
			OAuth.this.finish();
		}
		
		@Override
		public void onConfigurationChanged(android.content.res.Configuration newConfig) {
			super.onConfigurationChanged(newConfig);
		}
		
	    public void onRefresh() {
	        Log.d(TAG, "onRefresh");

	        Animation rotateAnimation =
	            AnimationUtils.loadAnimation(OAuth.this,
	                    R.anim.pull_to_refresh_anim);
	        rotateAnimation.setRepeatCount(Animation.INFINITE);

	        TextView spinnerText = (TextView) findViewById(R.id.pull_to_refresh_text);
	        spinnerText.setText("Loading data..");
	        ImageView staticSpinner = (ImageView)findViewById(R.id.pull_to_refresh_static_spinner);

	        staticSpinner.startAnimation(rotateAnimation);


	    }
}