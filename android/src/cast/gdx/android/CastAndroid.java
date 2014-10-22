package cast.gdx.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import cast.client.gdx.Cast;
import cast.client.gdx.android.NativeHandler;
import cast.client.gdx.android.ConfirmInterface;

import com.badlogic.gdx.backends.android.AndroidApplication;

public class CastAndroid extends AndroidApplication implements NativeHandler {

	private View gameView;
	private Cast game;

	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// lock landscape orientation
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// keep screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		game = new Cast(this);
		gameView = initializeForView(game, false);

		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(gameView);
		setContentView(layout);
	}

	public void showAlert(final String title, final String msg) {
		gameView.post(new Runnable() {
			public void run() {
				new AlertDialog.Builder(CastAndroid.this).setTitle(title).setMessage(msg).setNeutralButton("OK", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// close
					}
				}).create().show();
			}
		});
	}

	public void showConfirm(final ConfirmInterface confirmInterface, final String title, final String msg) {
		gameView.post(new Runnable() {
			public void run() {
				new AlertDialog.Builder(CastAndroid.this).setTitle(title).setMessage(msg).setPositiveButton("Yes", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						confirmInterface.yes();
						dialog.cancel();
					}
				}).setNegativeButton("No", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						confirmInterface.no();
						dialog.cancel();
					}
				}).create().show();
			}
		});
	}

	public void showLoading(final String title, final String msg) {
		gameView.post(new Runnable() {
			public void run() {
				progressDialog = ProgressDialog.show(CastAndroid.this, title, msg);
			}
		});
	}

	public void hideLoading() {
		gameView.post(new Runnable() {
			public void run() {
				progressDialog.dismiss();
			}
		});
	}

}
