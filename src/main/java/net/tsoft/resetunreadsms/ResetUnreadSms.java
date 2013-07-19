package net.tsoft.resetunreadsms;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * @author tom
 */
public class ResetUnreadSms
        extends ListActivity {
    private final static String SMS_CONTENT_URI = "content://sms";

    private final static String MMS_CONTENT_URI = "content://mms";

    private final static String READ_CONDITION = "read=0";

    /*
     * SMS fields thread_id, address, person, date, protocol, read, status, type,
     * reply_path_present, subject, body,
     */

    private final static String SMS_ID = "_ID";

    private final static String SMS_BODY = "body";

    private final static String SMS_PERSON = "person";

    private final static String SMS_READ = "read";

    private final static String SMS_STATUS = "status";

    private final static String SMS_ADDRESS = "address";

    private Button resetButton;

    private AboutDialog aboutDialog;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        resetButton = (Button) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetUnreadSms();
            }
        });

        aboutDialog = new AboutDialog(this);
        Button aboutButton = (Button) findViewById(R.id.about_button);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutDialog.show();
            }
        });

        fillList();
    }

    private void resetUnreadSms() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setMessage(getString(R.string.processing));
        dialog.show();
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ContentValues values = new ContentValues(1);
                values.put(SMS_READ, 1);
                ResetUnreadSms.this.getContentResolver().update(Uri.parse(SMS_CONTENT_URI), values, null, null);
                ResetUnreadSms.this.getContentResolver().update(Uri.parse(MMS_CONTENT_URI), values, null, null);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                dialog.dismiss();
                fillList();
                Toast.makeText(ResetUnreadSms.this, R.string.reset_done, Toast.LENGTH_LONG).show();
            }
        };
        task.execute();
    }

    private void fillList() {
        Cursor c = this.getContentResolver().query(
                Uri.parse(SMS_CONTENT_URI),
                new String[]{
                        SMS_ID,
                        SMS_BODY,
                        SMS_ADDRESS,
                        SMS_PERSON,
                        SMS_READ,
                        SMS_STATUS}, READ_CONDITION, null, null);
        //resetButton.setEnabled(c.getCount() != 0);
        startManagingCursor(c);
        String[] from = new String[]{SMS_BODY, SMS_ADDRESS};
        int[] to = new int[]{R.id.sms_content, R.id.sms_address};
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.sms, c, from, to);
        setListAdapter(simpleCursorAdapter);
    }
}