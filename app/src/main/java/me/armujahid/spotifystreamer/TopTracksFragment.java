package me.armujahid.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import java.util.Hashtable;

/**
 * Created by Abdul-Rauf on 01/07/2015.
 */
public  class TopTracksFragment extends Fragment {

    final String LOG_TAG = Activity.class.getName();
    String message = "";
    private TrackAdapter mTrackAdapter;

    public TopTracksFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//            setHasOptionsMenu(true); //for sending fragment's menu options to detailactivity
        Intent intent = getActivity().getIntent();
        message = intent.getStringExtra(intent.EXTRA_TEXT);//ForecastFragment.EXTRA_TEXT);
//            ((TextView)rootView.findViewById(R.id.detail_text)).setText(message);
        FetchTopTracksTask task = new FetchTopTracksTask();
        task.execute(message);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_tracks, container, false);

        mTrackAdapter =
                new TrackAdapter(
                        getActivity(), // The current context (this activity)
                        R.layout.linearlayout_artist, // The name of the layout ID.
                        new ArrayList<customTrack>(),
                        R.id.list_item_artist_imageview,
                        R.id.list_item_artist_textview
                );
        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_tracks);
        listView.setAdapter(mTrackAdapter);
        //TODO: onclick listener is remaining
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                customArtist artist = mTrackAdapter.getItem(position);
//                Intent intent = new Intent(getActivity(), TopTracksActivity.class)
//                        .putExtra(Intent.EXTRA_TEXT, artist.id);
//                startActivity(intent);
//            }
//        });
        return rootView;

    }

    public class FetchTopTracksTask extends AsyncTask<String, Void, customTrack[]> {

        @Override
        protected customTrack[] doInBackground(String... params) {
            if (params.length == 0) {
                Log.e(LOG_TAG, "params length is 0");
                return null;
            }
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            Map<String, Object> options = new Hashtable<>();
            options.put("country", "US"); //Replace here
            Tracks results = spotify.getArtistTopTrack(params[0],options);
            if (results == null) {
                Log.e(LOG_TAG, "Tracks are null");
                return null;
            }
            int size = results.tracks.size();

            if (size == 0) {
                Log.e(LOG_TAG, "Tracks list size is " + size);
                return null;
            }
            customTrack[] customTracks = new customTrack[size];


            int i = 0;
            try {
                for (Track track : results.tracks) {
                    customTracks[i] = new customTrack();
                    customTracks[i].name = track.name;
                    customTracks[i].albumName = track.album.name;
                    int imagelistsize =track.album.images.size();
                    if (imagelistsize != 0) {
                        customTracks[i].image = track.album.images.get(imagelistsize - 1); //almost 64x64 image
                    }
                    customTracks[i].uri= track.uri;
                    i++;
                }
            } catch (Exception ex) {
                Log.e(LOG_TAG, ex.getMessage());
                Log.e(LOG_TAG, "i = " + i);
                return null;
            }

            return customTracks;
        }

        @Override
        protected void onPostExecute(customTrack[] result) {
            if (result != null) {
                mTrackAdapter.clear();
                for (customTrack track : result) {
                    mTrackAdapter.add(track);
                }
                // New data is back from the server.  Hooray!
            } else {
                Context context = getActivity();
                CharSequence text = "No Track Found :)";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
    }
}
