package com.example.satanin.vlctechhub;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.satanin.vlctechhub.data.EventDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class EventsFragment extends Fragment {

    private ArrayAdapter<String> mEventsAdapter;
    private EventDbHelper mEventDbHelper;
    private SQLiteDatabase db;

    public EventsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // We have to add this in order to be able to handle menu events in this fragment
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.eventsfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_upcoming){
            FetchEventsTask eventsTask = new FetchEventsTask();
            eventsTask.execute("upcoming");
            return true;
        }
        if (id == R.id.action_past){
            FetchEventsTask eventsTask = new FetchEventsTask();
            eventsTask.execute("past");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateEvents(){
        FetchEventsTask eventsTask = new FetchEventsTask();
        eventsTask.execute("upcoming");
    }

    @Override
    public void onStart(){
        super.onStart();
        updateEvents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create some dummy data for the ListView.  Here's a sample weekly forecast
        String[] data = {
        };
        List<String> nextEvents = new ArrayList<String>(Arrays.asList(data));

        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mEventsAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_event, // The name of the layout ID.
                        R.id.list_item_event_textview, // The ID of the textview to populate.
                        nextEvents);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_events);
        listView.setAdapter(mEventsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String event = mEventsAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, event);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public class FetchEventsTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchEventsTask.class.getSimpleName();

        private String[] getEventsDataFromJson(String eventsJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String EVENT_TITLE = "title";
            final String EVENT_DESCRIPTION = "description";
            final String EVENT_DATE = "date";
            final String EVENT_LINK = "link";
            final String EVENT_ID = "id";

            JSONArray eventsJson = new JSONArray(eventsJsonStr);

            mEventDbHelper = new EventDbHelper(getActivity().getApplicationContext());
            db = mEventDbHelper.getWritableDatabase();

            String[] resultStrs = new String[eventsJson.length()];
            for(int i = 0; i < eventsJson.length(); i++) {
                String title;
                String date;
                String description;
                String link;
                String id;
                ContentValues reg = new ContentValues();

                // Get the JSON object representing the day
                JSONObject eventInfo = eventsJson.getJSONObject(i);

                id = eventInfo.getString(EVENT_ID);
                title = eventInfo.getString(EVENT_TITLE);
                date = eventInfo.getString(EVENT_DATE);
                description = eventInfo.getString(EVENT_DESCRIPTION);
                link = eventInfo.getString(EVENT_LINK);
                reg.put(mEventDbHelper.,id);
                resultStrs[i] = title + " - " + date;

            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Event entry: " + s);
            }
            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params) {

            // fallback in case there are no params
            if (params.length == 0 ){
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String eventsJsonStr = null;

            try {
                final String EVENTS_BASE_URL =
                    "http://vlctechhub-api.herokuapp.com/v0/events/"+params[0];

                Uri builtUri = Uri.parse(EVENTS_BASE_URL).buildUpon().build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());


                // Create the request to VLCTechHub, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                eventsJsonStr = buffer.toString();

                Log.v(LOG_TAG,"EVENTS Json String:" + eventsJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the events data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getEventsDataFromJson(eventsJsonStr);

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mEventsAdapter.clear();
                for(String eventInfoStr : result) {
                    mEventsAdapter.add(eventInfoStr);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }
}