package jp.muinyan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GUIから送られるコマンドを解析する機能を提供するクラスです。
 *
 * @author muinyan
 *
 */
public class UsiCommandReceiver {

	private BufferedReader gui;

	private Runnable onUsi;

	private BiConsumer<String, String> onSetOption;

	private Runnable onIsReady;

	private Runnable onStop;

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

				if (command.equals("isready")) {
					call(onIsReady);
				}

				if (command.startsWith("setoption name")) {
					Pattern p = Pattern.compile("setoption name (.+) (.+)");
					Matcher mat = p.matcher(command);

					String name = mat.group(1);
					String value = mat.group(2);

					call(onSetOption, name, value);
				}

				if(command.equals("stop")) {
					call(onStop);
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

	/**
	 * コマンド "isready" に対するコールバックを設定します。
	 *
	 * @param callback
	 */
	public void setOnIsReady(Runnable callback) {
		this.onIsReady = callback;
	}

	/**
	 * コマンド "setoption" に対するコールバックを設定します。コールバックに対する引数はname,valueです。
	 *
	 * @param callback
	 */
	public void setOnSetOption(BiConsumer<String, String> callback) {
		this.onSetOption = callback;
	}

	/**
	 * コマンド "stop" に対するコールバックを設定します。
	 * @param callback
	 */
	public void setOnStop(Runnable callback) {
		this.onStop = callback;
	}

	/**
	 * 引数で渡されたRunnableを実行します。nullの場合は何もしません。
	 * @param runnable
	 */
	private void call(Runnable runnable) {
		if (runnable != null) {
			runnable.run();
		}
	}

	private <T, U> void call(BiConsumer<T, U> biConsumer, T value1, U value2) {

		if (biConsumer != null) {
			biConsumer.accept(value1, value2);
		}
	}
}
