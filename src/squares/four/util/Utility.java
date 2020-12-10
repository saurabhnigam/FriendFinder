package squares.four.util;

import oauth.signpost.OAuthConsumer;
import squares.four.Keys.Keys;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import foursquare4j.oauth.FoursquareOAuthImpl;
import foursquare4j.type.Credentials;

public class Utility {
	
    
	private static SharedPreferences AuthTokens;
    private static Context context;
    private static FoursquareOAuthImpl foursquare;

    /**returns  SharedPreferences $PREFS_TOKEN**/
	public static SharedPreferences getAuthTokens(Context ctx) 
	{
		context = ctx;
		if(AuthTokens == null )
		{
		AuthTokens = context.getSharedPreferences(Keys.PREFS_TOKEN, Context.MODE_PRIVATE);
		}
		return AuthTokens;
	}

	/***Writes auth tokens & secret tokens to Shared Preferences $PREFS_TOKEN***/
	public static boolean writeTokens(Context ctx , String token_secret , String token)
	{
		Editor auth_editor = getAuthTokens(ctx).edit();
		auth_editor.putString(Keys.USER_TOKEN, token);
		auth_editor.putString(Keys.USER_SECRET, token_secret);
		return auth_editor.commit();
	}
	
	/**Check if keys are availabale in Shared Preferences $PREFS_TOKEN**/
	public static boolean areKeysAvailable()
	{
		// We look for saved user keys
		if(AuthTokens!=null && AuthTokens.contains(Keys.USER_TOKEN) && AuthTokens.contains(Keys.USER_SECRET)) {
/*			String mToken = AuthTokens.getString(USER_TOKEN, null);
			String mSecret = AuthTokens.getString(USER_SECRET, null);*/
			return true;
		}
		return false;
	}
	
	/**gets the tokens from consumer & returns Foursquare object**/
	public static FoursquareOAuthImpl authSetup(OAuthConsumer consumer , Context ctx)
	{
		if(foursquare==null)
		{
			
		String user_token = consumer.getToken();
		String user_secret = consumer.getTokenSecret();
		
        foursquare4j.type.Credentials credentials = new Credentials();
        credentials.setTokenSecret(user_secret);
        credentials.setAccessToken(user_token);
        
        writeTokens(ctx, user_secret, user_token);

        foursquare4j.oauth.OAuthConsumer newConsumer = new foursquare4j.oauth.OAuthConsumer(Keys.CONSUMER_KEY, Keys.CONSUMER_SECRET);

        foursquare = new FoursquareOAuthImpl(newConsumer, credentials);
		}
        return foursquare ;
	}
	
	/**gets the tokens from consumer & returns Foursquare object
	 * 
	 * @params token_secret is got from consumer.getTokenSecret ()
	 * @params token is got by consumer.getToken ()**/
	public static FoursquareOAuthImpl authSetupFromTokens(String token_secret , String token, Context ctx)
	{
		if(foursquare==null)
		{
		
        foursquare4j.type.Credentials credentials = new Credentials();
        credentials.setTokenSecret(token_secret);
        credentials.setAccessToken(token);
        
        writeTokens(ctx, token_secret, token);

        foursquare4j.oauth.OAuthConsumer newConsumer = new foursquare4j.oauth.OAuthConsumer(Keys.CONSUMER_KEY, Keys.CONSUMER_SECRET);

        foursquare = new FoursquareOAuthImpl(newConsumer, credentials);
		}
        return foursquare ;
	}

}
