package me.armujahid.spotifystreamer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends Fragment {

    final String LOG_TAG = Activity.class.getName();
    private ArtistAdapter mArtistAdapter;


    public SearchActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        final Button button = (Button) rootView.findViewById(R.id.search_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                EditText input = (EditText) rootView.findViewById(R.id.search_src_text);
                FetchArtistTask artistTask = new FetchArtistTask();
                String query = input.getText().toString();
                artistTask.execute(query);
//                TextView output = (TextView) rootView.findViewById(R.id.search_output);
//                output.setText(input.getText());
            }
        });
        mArtistAdapter =
                new ArtistAdapter(
                        getActivity(), // The current context (this activity)
                        R.layout.linearlayout_artist, // The name of the layout ID.
                        new ArrayList<customArtist>(),
                        R.id.list_item_artist_imageview,
                        R.id.list_item_artist_textview
                );
        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_artist);
        listView.setAdapter(mArtistAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                customArtist artist = mArtistAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), TopTracksActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, artist.id);
                startActivity(intent);
            }
        });
        return rootView;

    }

    public class FetchArtistTask extends AsyncTask<String, Void, customArtist[]> {

        @Override
        protected customArtist[] doInBackground(String... params) {
            if (params.length == 0) {
                Log.e(LOG_TAG,"params length is 0");
                return null;
            }
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            ArtistsPager results = spotify.searchArtists(params[0]);
            if (results == null)
            {
                Log.e(LOG_TAG,"Artistpager is null");
                return null;
            }
            int size = results.artists.items.size();
            if (size == 0)
            {
                Log.e(LOG_TAG,"Artist items size is "+size);
                return null;
            }
            customArtist[] customArtists = new customArtist[size];


            int i = 0;
            try
            {
                for (Artist artist : results.artists.items) {
                    customArtists[i] = new customArtist();
                    customArtists[i].name = artist.name;
                    int imagelistsize = artist.images.size();
                    if (imagelistsize != 0)
                    {
                        customArtists[i].image = artist.images.get(imagelistsize-1); //almost 64x64 image
                    }
                    customArtists[i].id = artist.id;

                    i++;
                }
            }
            catch (Exception ex)
            {
                Log.e(LOG_TAG,ex.getMessage());
                Log.e(LOG_TAG,"i = "+i);
                return null;
            }

            return customArtists;
        }

        @Override
        protected void onPostExecute(customArtist[] result) {
            if (result != null) {
                mArtistAdapter.clear();
                for(customArtist artist : result) {
                    mArtistAdapter.add(artist);
                }
                // New data is back from the server.  Hooray!
            }
            else
            {
                Context context = getActivity();
                CharSequence text = "No Artist Found :)";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
    }
}
