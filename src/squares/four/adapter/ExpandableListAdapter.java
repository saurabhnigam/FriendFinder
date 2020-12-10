
package squares.four.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import squares.four.R;
import squares.four.util.ImageDownloader;
import squares.four.util.LocationHelper;
import squares.four.util.ViewWrapper;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    public static final squares.four.util.ImageDownloader imageDownloader = new ImageDownloader();
    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    private Context context;
/**groups- maps group name to the arraylist of its child's names**/
    private HashMap groups = new HashMap<String, ArrayList<LocationHelper>>(); 


    public ExpandableListAdapter(Context context, HashMap<String, ArrayList<LocationHelper>> groups2) 
    {	
        this.groups = groups2;
        this.context =context;
    }



    @Override
    public Object getChild(int groupPosition, int childPosition) {
    	/**groupPosition is 0 for "My Friends"
    	 * and 1 for "NearBy Venues"*/
    	if(groupPosition==0)
    		return ((ArrayList<LocationHelper>)groups.get("My Friends")).get(childPosition);
    	else if(groupPosition==1)
    		return ((ArrayList<LocationHelper>)groups.get("NearBy Venues")).get(childPosition);
		
    return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    
    // Return a child view. You can load your custom layout here.
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {

    	ViewWrapper wrapper;
    	TextView label ;
    	ImageView userImage;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_child_layout, null);
            wrapper=new ViewWrapper(convertView);
            convertView.setTag(wrapper); 
            
            label = wrapper.getLabel();
            userImage = wrapper.getUserImage();
        }
        else
        {
        	wrapper=(ViewWrapper)convertView.getTag();
            label = wrapper.getLabel();
            userImage = wrapper.getUserImage();

        }
        
        LocationHelper locHelper = (LocationHelper) getChild(groupPosition, childPosition);
        label.setText(locHelper.getFriendAtLoc());

        String url =locHelper.getPicUrl();
        if(url != null)
        	imageDownloader.download(url, userImage);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
    	/**groupPosition is 0 for "My Friends"
    	 * and 1 for "NearBy Venues"*/
    	if(groupPosition==0)
    	return ((ArrayList<LocationHelper>)groups.get("My Friends")).size();
    	else 
    		return ((ArrayList<LocationHelper>)groups.get("NearBy Venues")).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
    	/**groupPosition is 0 for "My Friends"
    	 * and 1 for "NearBy Venues"*/
    	if(groupPosition==0)
        return ("My Friends");
    	else 
    		return ("NearBy Venues");	
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    // Return a group view. You can load your custom layout here.
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
        String group = (String) getGroup(groupPosition);
        
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_group_layout, null);
        }
        
        TextView tv = (TextView) convertView.findViewById(R.id.tvGroup);
        tv.setText(group);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

}
