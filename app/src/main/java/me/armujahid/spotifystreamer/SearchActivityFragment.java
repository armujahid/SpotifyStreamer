package me.armujahid.spotifystreamer;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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

    private ArrayAdapter<String> mArtistAdapter;


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
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_artist, // The name of the layout ID.
                        R.id.list_item_artist_textview, // The ID of the textview to populate.
                        new ArrayList<String>());
        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_artist);
        listView.setAdapter(mArtistAdapter);
        return rootView;

    }

    public class FetchArtistTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            ArtistsPager results = spotify.searchArtists(params[0]);
            String[] result = new String[results.artists.items.size()];
            int i = 0;
            for(Artist artist : results.artists.items)
            {
                result[i] = artist.name.toString();
                i++;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mArtistAdapter.clear();
                for(String artist : result) {
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


