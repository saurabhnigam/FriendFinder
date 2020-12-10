package squares.four.activity;

	import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import squares.four.R;
import squares.four.Keys.Keys;
import squares.four.adapter.ExpandableListAdapter;
import squares.four.location.MyLocation;
import squares.four.location.MyLocation.LocationResult;
import squares.four.util.ImageDownloader;
import squares.four.util.LocationHelper;
import squares.four.util.ResourceProxyImpl;
import squares.four.util.Utility;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import foursquare4j.exception.FoursquareException;
import foursquare4j.oauth.FoursquareOAuthImpl;
import foursquare4j.type.Venue;
import foursquare4j.type.VenueGroup;
import foursquare4j.type.Venues;



	public class SideMapper extends Activity{
	private org.osmdroid.views.MapView map;
	private ExpandableListView mylist;
	private String TAG ="SideLoadMap";
	private boolean ListHidden =false;
	private List<LocationHelper> mapperList;
	private LinearLayout mRefreshView;
	private HashMap<String, ArrayList<LocationHelper>> groups;
	private ImageView staticSpinner;
	private TextView spinnerText;
	private ExpandableListAdapter adapter;
	private ItemizedOverlay<OverlayItem> mMyLocationOverlay;
	private ResourceProxy mResourceProxy;
	private ArrayList<LocationHelper> friendsLoc;

		@Override
		public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sideloadingmap2);
		
		Intent callingIntent = getIntent();
		mapperList = callingIntent.getParcelableArrayListExtra("arraylist");
		map = (MapView)findViewById(R.id.map);
		map.setOnClickListener(mapClickListener);
		/*map.getController().setCenter(getPoint(40.748963847316034,-73.96807193756104));*/
		
		map.getController().setZoom(17);
		map.setBuiltInZoomControls(true);
		mylist =(ExpandableListView)findViewById(R.id.list);
		
		LayoutInflater mInflater = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        mRefreshView = (LinearLayout) mInflater.inflate(
                R.layout.pull_to_refresh_header, null);
		mylist.addHeaderView(mRefreshView);
		

		/**adding groups and childs from mapperlist*/
		/**hashmap <GroupName , arraylist of group>**/
		groups = new HashMap<String, ArrayList<LocationHelper>>();
		friendsLoc = new ArrayList<LocationHelper>();
		ArrayList<LocationHelper> nearByVenue = new ArrayList<LocationHelper>();
		for(LocationHelper loc :mapperList)
		{
			if(loc.getIsFriendData().equals("true"))
				friendsLoc.add(loc);
			else
				nearByVenue.add(loc);
		}
		//put above list in hashmap
		groups.put("My Friends", friendsLoc);
		
		if(nearByVenue.size()>0)
		groups.put("NearBy Venues", nearByVenue);			//add nearby venues only if nearbyVenues arraylist 
															//is available

		adapter = new ExpandableListAdapter(this, groups);

	    // Set this blank adapter to the list view
	    mylist.setAdapter(adapter);
		mResourceProxy = new ResourceProxyImpl(getApplicationContext());
		drawMapOverLays();
		map.getOverlays().add(mMyLocationOverlay);
		
	    mylist.setOnChildClickListener(childClickListener);
	    
	    /**move refresher on click*/
	    mRefreshView.setOnClickListener(refreshViewListener);
	    
		}




		@Override
		public void onStart()
		{
			super.onStart();
        	MyLocation myLocation = new MyLocation();
        	myLocation.getLocation(SideMapper.this, locationResult);
		}
		
		/**get geopoints from lat,lon**/
		private GeoPoint getPoint(double lat, double lon) {
			return(new GeoPoint((int)(lat*1000000.0),(int)(lon*1000000.0)));
		}
		


		
		/**for handling click events & fire off particular loc**/
		OnItemClickListener myListListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				//since header is added
				if(position!=0)
				{
				position--;	
				LocationHelper locHelper = mapperList.get(position);
				Log.d(TAG , "Clicked on position"+position);
				//have added +position to generate random lon,lat each time :)
				GeoPoint geopointsSent = getPoint(Double.parseDouble(locHelper.getGeoLat()),
						Double.parseDouble(locHelper.getGeoLong()));
				Log.d(TAG , "geopints made");
				Message msg = Message.obtain();
				msg.obj = geopointsSent;
				//TODO send location of the clicked position
				mapUpdater.sendMessage(msg);

				Log.d(TAG , "handler msg sent");
				}
				else
					onRefresh();
			}
			};
		 
			
			/**Handler for updating map on each click**/
			public Handler mapUpdater = new Handler()
			{
				@Override
				public void handleMessage(Message msg)
				{
					GeoPoint geopointRecieved = (GeoPoint) msg.obj;

					map.getController().setCenter(geopointRecieved);
					map.getController().setZoom(17);
					map.setBuiltInZoomControls(true);
					
					map.invalidate();
				}
			};

			
			@Override
			public boolean onKeyDown(int keyCode, KeyEvent event) {
			
				if(ListHidden)
				{
				Log.d(getLocalClassName(), "sdk IS "+android.os.Build.VERSION.SDK);
				
			    if (Integer.valueOf(android.os.Build.VERSION.SDK) < 7
			            && keyCode == KeyEvent.KEYCODE_BACK
			            && event.getRepeatCount() == 0) {
			        // Take care of calling this method on earlier versions of
			        // the platform where it doesn't exist.
					Log.d(getLocalClassName(),"onKeyDown");
					onBackPressed();
			    }
				}
				return(super.onKeyDown(keyCode, event));
			}
			
			public void onBackPressed() {
				Log.d(getLocalClassName(),"onBackPressed");
				if(ListHidden)
				{
					bringMyListBack();
				}
				else
					this.finish();
			    // This will be called either automatically for you on 2.0
			    // or later, or by the code above on earlier versions of the
			    // platform.
			    return;
			}
			
			/**Does what it says :D**/
			public void bringMyListBack()
			{
				mylist.setVisibility(View.VISIBLE);
				ListHidden = false;
			}
			
			
		    public void onRefresh() {
		        Log.d(TAG, "onRefresh");

		        final int top = mRefreshView.getTop();
		        if (top < -30) {
		            Log.d(TAG, "Backing off refresh...");
		            return;
		        }
		        //scrollBy(0, mPullBounce);

		        Animation rotateAnimation =
		            AnimationUtils.loadAnimation(SideMapper.this,
		                    R.anim.pull_to_refresh_anim);
		        rotateAnimation.setRepeatMode(Animation.INFINITE);

		        spinnerText = (TextView) mRefreshView.findViewById(R.id.pull_to_refresh_text);
		        staticSpinner = (ImageView) mRefreshView.findViewById(R.id.pull_to_refresh_static_spinner);

		        spinnerText.setText("Updating");
		        staticSpinner.startAnimation(rotateAnimation);

		        // TODO: Temporary, to fake some network work or similar. Replace with callback.
		        new CountDownTimer(2000, 1000) {
		            @Override
		            public void onTick(long millisUntilFinished) {
		            }
		            @Override
		            public void onFinish() {
		                onRefreshComplete();
		            }
		        }.start();
		    }

		    public void onRefreshComplete() {        
		        Log.d(TAG, "onRefreshComplete");
		        spinnerText = (TextView) mRefreshView.findViewById(R.id.pull_to_refresh_text);
		        staticSpinner = (ImageView) mRefreshView.findViewById(R.id.pull_to_refresh_static_spinner);

		        spinnerText.setText("Refresh Me");
		        staticSpinner.setAnimation(null);
		    }
		    
		    
			/**for accessing Location from current gps co-ords**/
			  public LocationResult locationResult = new LocationResult(){
			      @Override
			      public void gotLocation(final Location location){
			    	  Log.d(TAG ,"gotLocation is " +location);
			          //after location fetch nearby venues 
						FoursquareOAuthImpl foursquare = Utility.authSetupFromTokens
						(Utility.getAuthTokens(SideMapper.this).getString(Keys.USER_SECRET, null), 
								Utility.getAuthTokens(SideMapper.this).getString(Keys.USER_TOKEN, null),
								SideMapper.this);
						
						if(location != null)
						{
						try {
							Venues nearByVenues = foursquare.venues(Double.toString(location.getLatitude()),
									Double.toString(location.getLongitude()), 
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
			                    if(mapperList.size()<2)
			                    	mapperList.add(new LocationHelper(geoLat ,  geoLong, venueName , null,"false"));
			                    else
			                    	mapperList.add(1 ,new LocationHelper(geoLat ,  geoLong, venueName , null, "false"));
			            }
						} catch (FoursquareException e) {
							Log.e(TAG , "FourSquareException" ,e);
						}
			    		
						//Now call handler to update UI by a notifyDataSetChanged
						UIupdater.sendEmptyMessage(0);
						}
			          }
			  };
			  
			  
			  public Handler UIupdater = new Handler() {
				  public void  handleMessage(Message msg) {
					  adapter.notifyDataSetChanged();
				  }
				};
				

			@Override
				public void onConfigurationChanged(android.content.res.Configuration newConfig) {
					super.onConfigurationChanged(newConfig);
				}
				
				/**draws Itemized Overlays getting location of all friends**/
				private void drawMapOverLays() 
				{
					/* Create a static ItemizedOverlay showing a some Markers on some cities. */
					final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
/*					items.add(new OverlayItem("San Francisco", "SampleDescription", new GeoPoint(37779300,
							-122419200))); // San Francisco*/
					for(LocationHelper loc: friendsLoc)
					{
						items.add(new OverlayItem(loc.getFriendAtLoc(), "SampleDescription", new GeoPoint(Double.parseDouble(loc.getGeoLat()),
								Double.parseDouble(loc.getGeoLong())))); 
					}
					
					/* OnTapListener for the Markers, shows a simple Toast. */
					this.mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items,
							new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
								@Override
								public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
/*									Toast.makeText(
											getApplicationContext(),
											"'" + item.mTitle + "' (" + index
													+ ".) ", Toast.LENGTH_LONG).show();*/
									createToast(index);
									return true; // We 'handled' this event.
								}

								@Override
								public boolean onItemLongPress(final int index, final OverlayItem item) {
/*									Toast.makeText(
											getApplicationContext(),
											"Item '" + item.mTitle + "' (index=" + index
													+ ") got long pressed", Toast.LENGTH_LONG).show();*/
									createToast(index);
									return false;
								}

								
								
							}, mResourceProxy);
					map.getOverlays().add(this.mMyLocationOverlay);
				}
				
				/**listens to click on child view***/
				OnChildClickListener childClickListener =new OnChildClickListener()
			    {
			        
			        @Override
			        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
			        {
			        	LocationHelper locHelper;
			        	
			        	/*groupPosition is 0 for "My Friends"
			        	 * and 1 for "NearBy Venues"*/
			        	if(groupPosition==0)
			        	{
						locHelper = groups.get("My Friends").get(childPosition);
			        	}
			        	else
			        	{
			        		locHelper = groups.get("NearBy Venues").get(childPosition);
			        	}
			        	
						Log.d(TAG , "Clicked on position"+childPosition);
						//have added +position to generate random lon,lat each time :)
						GeoPoint geopointsSent = getPoint(Double.parseDouble(locHelper.getGeoLat()),
								Double.parseDouble(locHelper.getGeoLong()));
						Log.d(TAG , "geopints made");
						Message msg = Message.obtain();
						msg.obj = geopointsSent;
						mapUpdater.sendMessage(msg);

						Log.d(TAG , "handler msg sent");
			            return false;
			        }
			    };
			    
			    /***listens to click on Refresh view*/
			    View.OnClickListener refreshViewListener =new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						onRefresh();
						
					}
				};
				
			    /***listens to click on map view*/
			    View.OnClickListener mapClickListener =new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Log.d(TAG ,"clicked on map");
						if(!ListHidden)
						mylist.setVisibility(View.GONE); //hiding list & displaying map in full
						ListHidden = true;
					}
				};
				
				/***creates Custom Toast
				 * @param item */
				private void createToast(int index) {
					
					LayoutInflater inflater = getLayoutInflater();
					View layout = inflater.inflate(R.layout.bottom_view,
					                               (ViewGroup) findViewById(R.id.directions_layout));
					Toast toast = new Toast(getApplicationContext());
					toast.setDuration(Toast.LENGTH_LONG);
					
					if(friendsLoc != null)
					{
					LocationHelper locHelper = friendsLoc.get(index);
					
					ImageView userImage = (ImageView) layout.findViewById(R.id.usericon);
					
					ImageDownloader imageDownloader;
					if(adapter.imageDownloader != null)
					{
						imageDownloader = new ImageDownloader();
						Log.d(TAG ,"adapter.imageDownloader != null");
					}
						imageDownloader = adapter.imageDownloader;
					imageDownloader.download(locHelper.getPicUrl(), userImage);
					
					TextView titleText = (TextView) layout.findViewById(R.id.firstLine);
					Log.d(TAG , "Display dat is "+locHelper.getFriendAtLoc());
					titleText.setText(locHelper.getFriendAtLoc());
					
					TextView descText = (TextView) layout.findViewById(R.id.secondLine);
					descText.setText("");
					}
					

					toast.setView(layout);
					toast.show();					
				}
	}
