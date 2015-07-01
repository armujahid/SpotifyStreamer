package me.armujahid.spotifystreamer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Abdul-Rauf on 28/06/2015.
 */
public class ArtistAdapter extends ArrayAdapter<customArtist> {
    private Context mContext;
    private  LayoutInflater mInflater;
    int mResource;
    int imageID;
    int textID;

    public ArtistAdapter(Context context, int resource, List<customArtist> objects, int iid, int tid) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        imageID = iid ;
        textID = tid ;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        TextView textView;
        ImageView imageView;

        if (convertView == null) {
            view = mInflater.inflate(mResource, parent, false);
        } else {
            view = convertView;
        }

        try {
//            if (mFieldId == 0) {
//                //  If no custom field is assigned, assume the whole resource is a TextView
//                textView = (TextView) view;
//            } else {
                //  Otherwise, find the TextView field within the layout

                textView = (TextView) view.findViewById(textID);
                imageView = (ImageView) view.findViewById(imageID);

        }
        catch (ClassCastException e) {
            Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "ArrayAdapter requires the resource ID to be a TextView", e);
        }

        customArtist item = getItem(position);
        textView.setText(item.name);
        if (item.image != null) {
            Picasso.with(mContext).load(item.image.url).resize(64, 64).into(imageView);
        }


        return view;
    }
}
