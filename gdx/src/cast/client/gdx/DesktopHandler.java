package cast.client.gdx;

import cast.client.gdx.android.NativeHandler;
import cast.client.gdx.android.ConfirmInterface;

public class DesktopHandler implements NativeHandler {

	@Override
	public void showConfirm(ConfirmInterface confirmInterface, String title, String msg) {
		Cast.log(title + ": " + msg + " : YES");
		confirmInterface.yes();
	}

	@Override
	public void showAlert(String title, String msg) {
		Cast.log(title + ": " + msg);
	}

	@Override
	public void showLoading(String title, String msg) {
		Cast.log(title + ": " + msg);
	}

	@Override
	public void hideLoading() {
		Cast.log("Done loading");
	}

}
