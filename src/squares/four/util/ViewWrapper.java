package squares.four.util;

import squares.four.R;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


/**A class to cache all child views **/
public class ViewWrapper {
	
View base;
TextView label=null;
ImageView icon=null;

public ViewWrapper(View base) {
	this.base=base;
}

public TextView getLabel() {
if (label==null) {
label=(TextView)base.findViewById(R.id.labelChild);
}
return(label);
}
public ImageView getUserImage() {
if (icon==null) {
icon=(ImageView)base.findViewById(R.id.user_pic);
}
return(icon);
}
}
