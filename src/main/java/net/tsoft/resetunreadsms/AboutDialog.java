/**
 * 
 */
package net.tsoft.resetunreadsms;

import android.app.Dialog;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author tom
 * 
 */
public class AboutDialog
    extends Dialog
{
  /**
   * @param context
   */
  public AboutDialog(Context context)
  {
    super(context);
    setContentView(R.layout.about);
    setTitle(context.getText(R.string.app_name) + " " + context.getText(R.string.version));

    TextView donateLink = (TextView) findViewById(R.id.donate_link);
    donateLink.setMovementMethod(LinkMovementMethod.getInstance());

    Button ok = (Button) findViewById(R.id.ok_button);
    ok.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        AboutDialog.this.dismiss();
      }
    });
  }

}
