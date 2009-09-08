package net.tsoft.resetunreadsms;

import net.tsoft.resetunreadsms.R;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * @author tom
 * 
 */
public class ResetUnreadSms
    extends ListActivity
{
  private final static String SMS_CONTENT_URI = "content://sms";
  private final static String READ_CONDITION = "read=0";
  private final static String READ = "read";
  private final static String SMS_ID = "_ID";
  private final static String SMS_BODY = "body";

  private Button resetButton;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setTitle(getText(R.string.unreadsms_label));
    setContentView(R.layout.main);
    resetButton = (Button) findViewById(R.id.reset_button);
    resetButton.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        resetUnreadSms();
        fillList();
      }
    });
    fillList();
  }

  private void resetUnreadSms()
  {
    ProgressDialog progressDialog = ProgressDialog.show(this, "", this
        .getString(R.string.updating_sms), true);
    progressDialog.show();
    ContentValues values = new ContentValues(1);
    values.put(READ, 1);
    this.getContentResolver().update(Uri.parse(SMS_CONTENT_URI), values, null, null);
    Toast.makeText(this, R.string.reset_done, Toast.LENGTH_LONG).show();
    progressDialog.hide();
  }

  private void fillList()
  {
    ProgressDialog progressDialog = ProgressDialog.show(this, "", this
        .getString(R.string.loading_sms), true);
    progressDialog.show();
    Cursor c = this.getContentResolver().query(Uri.parse(SMS_CONTENT_URI),
        new String[] { SMS_ID, SMS_BODY }, READ_CONDITION, null, null);
    resetButton.setEnabled(c.getCount() != 0);
    startManagingCursor(c);
    String[] from = new String[] { SMS_BODY };
    int[] to = new int[] { android.R.id.text1 };
    SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this,
        android.R.layout.simple_list_item_2, c, from, to);
    setListAdapter(simpleCursorAdapter);
    progressDialog.hide();
  }
}