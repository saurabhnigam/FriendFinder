package squares.four.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Launcher extends Activity{

	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		startActivity(new Intent(Launcher.this , OAuth.class));
		this.finish();
	}
}
