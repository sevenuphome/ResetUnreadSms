package net.tsoft.resetunreadsms;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;

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

    private Button mResetButton;

    private TextView mEmptyTextView;

//    private AboutDialog aboutDialog;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setTitle(getString(R.string.app_name) + " v" + BuildConfig.VERSION_NAME);

        mResetButton = (Button) findViewById(R.id.reset_button);
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetUnreadSms();
            }
        });

        mEmptyTextView = (TextView) findViewById(android.R.id.empty);

//        aboutDialog = new AboutDialog(this);
//        Button aboutButton = (Button) findViewById(R.id.about_button);
//        aboutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                aboutDialog.show();
//            }
//        });

        initLibrary();
        Log.i("RUS", "paypal instance=" + PayPal.getInstance());
        CheckoutButton checkoutButton = PayPal.getInstance().getCheckoutButton(this, PayPal.BUTTON_278x43, CheckoutButton.TEXT_DONATE);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchBrowser(ResetUnreadSms.this, getString(R.string.paypal_link));
            }
        });
        ((FrameLayout) findViewById(R.id.donate_button)).addView(checkoutButton);

        fillList();
    }

    public void initLibrary() {
        PayPal pp = PayPal.getInstance();

        if (pp == null) {  // Test to see if the library is already initialized

            // This main initialization call takes your Context, AppID, and target server
            pp = PayPal.initWithAppID(this, "thomas.bruyelle@gmail.com", PayPal.ENV_NONE);

            // Required settings:

            // Set the language for the library
//            pp.setLanguage(Locale.getDefault().getLanguage());

            // Some Optional settings:

            // Sets who pays any transaction fees. Possible values are:
            // FEEPAYER_SENDER, FEEPAYER_PRIMARYRECEIVER, FEEPAYER_EACHRECEIVER, and FEEPAYER_SECONDARYONLY
            pp.setFeesPayer(PayPal.FEEPAYER_EACHRECEIVER);

            // true = transaction requires shipping
            pp.setShippingEnabled(true);
        }
    }

    private void resetUnreadSms() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
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
                mEmptyTextView.setText(R.string.thanks);
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

    public static void launchBrowser(final Context context, String url) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }
}