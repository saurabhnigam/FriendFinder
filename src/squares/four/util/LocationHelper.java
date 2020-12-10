package squares.four.util;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationHelper implements Parcelable{
	private String geoLat , geoLong , friendAtLoc ,picUrl ;
	/**String (boolean) isFriendData is to check if below data is Friend's data or Nearby Places**/
	String isFriendData;
/*	if  ISFRIENDDATA then true;
	else ISNEARBYPLACES then false;*/


	public LocationHelper(String geoLat, String geoLong, String friendAtLoc , String picUrl ,String isFriendData) {
		super();
		this.geoLat = geoLat;
		this.geoLong = geoLong;
		this.friendAtLoc = friendAtLoc;
		this.picUrl = picUrl;
		this.isFriendData = isFriendData;
	}

	public LocationHelper() {
		super();
	}

	public LocationHelper(Parcel in) {
		readFromParcel(in);
	}

	public String getGeoLat() {
		return geoLat;
	}

	public String getGeoLong() {
		return geoLong;
	}

	public String getFriendAtLoc() {
		return friendAtLoc;
	}


	public String getIsFriendData() {
		return isFriendData;
	}
	
	public String getPicUrl() {
		return picUrl;
	}

	public static Parcelable.Creator getCreator() {
		return CREATOR;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(geoLat);
		dest.writeString(geoLong);
		dest.writeString(friendAtLoc);
		dest.writeString(picUrl);
		dest.writeString(isFriendData);
				
	}
	
    public static final Parcelable.Creator CREATOR =
    	new Parcelable.Creator() {
            public LocationHelper createFromParcel(Parcel in) {
                return new LocationHelper(in);
            }
 
            public LocationHelper[] newArray(int size) {
                return new LocationHelper[size];
            }
        };
        
	private void readFromParcel(Parcel in) {
		 
		// We just need to read back each
		// field in the order that it was
		// written to the parcel
		geoLat = in.readString();
		geoLong = in.readString();
		friendAtLoc = in.readString();
		picUrl= in.readString();
		isFriendData = in.readString();

	}
}
