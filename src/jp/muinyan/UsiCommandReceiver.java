package jp.muinyan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * GUIから送られるコマンドを解析する機能を提供するクラスです。
 *
 * @author muinyan
 *
 */
public class UsiCommandReceiver {

	private BufferedReader gui;

	private Runnable onUsi;

	public UsiCommandReceiver(InputStream in) {

		this.gui = new BufferedReader(new InputStreamReader(in));
	}

	/**
	 * GUIからのコマンド受付を開始します。コマンドを受信すると、対応するコールバックが発火します。
	 */
	public void start() {
		try {
			while (true) {
				String command = gui.readLine();

				if (command == null) {
					break;
				}

				if (command.equals("usi")) {
					call(onUsi);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	/**
	 * コマンド "usi" に対するコールバックを設定します。
	 *
	 * @param callback
	 */
	public void setOnUsi(Runnable callback) {
		this.onUsi = callback;
	}

	private void call(Runnable runnable) {
		if (runnable != null) {
			runnable.run();
		}
	}
}
