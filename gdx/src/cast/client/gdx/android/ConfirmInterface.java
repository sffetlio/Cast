package cast.client.gdx.android;

/**
 * Interface handling "yes" and "no" from
 * {@link cast.client.gdx.android.NativeHandler#showConfirm(cast.client.gdx.android.ConfirmInterface, String, String)}
 */
public interface ConfirmInterface {

	/**
	 * Override to handle "yes" from confirm exit dialog.
	 */
	void yes();

	/**
	 * Override to handle "no" from confirm exit dialog.
	 */
	void no();

}
