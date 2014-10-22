package cast.client.gdx.android;

/**
 * Handler for native android elements for libgdx.
 */
public interface NativeHandler {

	/**
	 * Display confirm dialog.
	 * 
	 * @param confirmInterface
	 * @param title
	 * @param msg
	 */
	public void showConfirm(ConfirmInterface confirmInterface, String title, String msg);

	/**
	 * Display alert.
	 * 
	 * @param title
	 * @param msg
	 */
	public void showAlert(String title, String msg);

	public void showLoading(String title, String msg);

	public void hideLoading();
}
