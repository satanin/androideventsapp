package com.example.satanin.vlctechhub;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.satanin.vlctechhub.data.EventDbHelper;

import java.sql.SQLException;


/**
 * A placeholder fragment containing a simple view.
 */

public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String EVENTS_SHARE_HASHTAG = " #VLCTechHub";
    private String mEventsStr;

    private EventDbHelper mEventDbHelper;
    private SQLiteDatabase db;

    private TextView description;
    private TextView link;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        // Consultamos la base de datos
        mEventDbHelper = new EventDbHelper(getActivity().getApplicationContext());
        db = mEventDbHelper.getWritableDatabase();

        // The detail Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {

            mEventsStr = intent.getStringExtra(Intent.EXTRA_TEXT);

            final String[] data  = mEventsStr.split(" - ");
            final String title = data[0];
            final String date = data[1];

            description = ((TextView) rootView.findViewById(R.id.text_description));
            link = ((TextView) rootView.findViewById(R.id.text_link));
            ((TextView) rootView.findViewById(R.id.text_title)).setText(title);
            ((TextView) rootView.findViewById(R.id.text_date)).setText(date);

            try {
                consultar(title, date);
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }




        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareEventsIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    private Intent createShareEventsIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mEventsStr + EVENTS_SHARE_HASHTAG);
        return shareIntent;
    }

    private void consultar(String title, String date) throws SQLException {
        //
        // Consultamos el centro por el identificador
        //
        Cursor cursor = mEventDbHelper.getRegistro(title, date);
        description.setText(cursor.getString(cursor.getColumnIndex(EventDbHelper.C_DESCRIPTION)));
        link.setText(cursor.getString(cursor.getColumnIndex(EventDbHelper.C_LINK)));
    }
}