package squares.four.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class WebviewAuth extends Activity{
	
	static ProgressDialog mSpinner;
    static final FrameLayout.LayoutParams FILL =
        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                         ViewGroup.LayoutParams.FILL_PARENT);
	protected static final String TAG = "WebviewAuth";
    
    String callback_url = "squaresfourty";
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        
        String auth_url = getIntent() .getStringExtra("auth_url");
        
		final WebView mywebview = new WebView(this);
		
		setContentView(mywebview);
		
		mSpinner = null;
		int ZOOM_LEVEL = 75;							//75 percent
        mywebview.setVerticalScrollBarEnabled(false);
        mywebview.setHorizontalScrollBarEnabled(false);
        mywebview.setLayoutParams(FILL);
		mywebview.getSettings().setJavaScriptEnabled(true);
		mywebview.getSettings().setBuiltInZoomControls(true);
		mywebview.setInitialScale(ZOOM_LEVEL);
		mywebview.loadUrl(auth_url);		
		setProgressBarIndeterminate(true);
		setProgressBarVisibility(true);
		
		mywebview.setWebViewClient(new WebViewClient() {  
		    /* On Android 1.1 shouldOverrideUrlLoading() will be called every time the user clicks a link, 
		     * but on Android 1.5 it will be called for every page load, even if it was caused by calling loadUrl()! */  
			    @Override  
			    public boolean shouldOverrideUrlLoading(WebView view, String url)  
			    {  
			    	return false;
			    }  
		    
	           @Override
	            public void onPageStarted(WebView view, String url, Bitmap favicon) {
	        	   if(mSpinner==null)
	        	   {
	               mSpinner = new ProgressDialog(WebviewAuth.this);
	               mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
	               mSpinner.setMessage("Loading...");
	               mSpinner.show();
	        	   }
	               
	               setProgressBarIndeterminate(true);
	                if (url != null && url.startsWith(callback_url)) {
	                    mywebview.stopLoading();
	                    dismissSpinner();
	                    Log.d(TAG , "Complete url is"+ url);
	                    Intent intent = new Intent(WebviewAuth.this , OAuth.class);
	                    intent.setData(Uri.parse(url));
	                    intent.putExtra("url", url);
	                    startActivity(intent);
	                        finish();
	                    }
	                super.onPageStarted(view, url, favicon);
	            }
	           
	           @Override
	           public void onPageFinished(WebView view , String url)
	           {
	        	   dismissSpinner();	

	        	   setProgressBarVisibility(false);
	        	   super.onPageFinished(view, url);
	           }
	        });
	}
	
	/**dismisses Progress DIalog**/
	public void dismissSpinner()
	{
  	   if(mSpinner!=null && mSpinner.isShowing())
	   {
		   mSpinner.dismiss();
    	   mSpinner = null;
	   }
	}
	
	@Override
	public void onConfigurationChanged(android.content.res.Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
