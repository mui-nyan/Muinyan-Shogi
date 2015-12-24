package jp.muinyan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
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

	private Optional<Runnable> onUsi;

	private Optional<BiConsumer<String, String>> onSetOption;

	private Optional<Runnable> onIsReady;

	private Optional<Runnable> onUsiNewGame;

	private Optional<Consumer<String>> onPosition;

	private Optional<Consumer<String>> onGo;

	private Optional<Runnable> onPonderHit;

	private Optional<Runnable> onStop;

	private Optional<Runnable> onQuit;

	private Optional<Consumer<GameResult>> onGameOver;

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
					onUsi.ifPresent(r -> r.run());
				}

				if (command.equals("isready")) {
					onIsReady.ifPresent(c -> c.run());
				}

				if (command.equals("usinewgame")) {
					onUsiNewGame.ifPresent(r -> r.run());
				}

				if (command.equals("position")) {
					String value = command.substring(9);
					onPosition.ifPresent(r -> r.accept(value));
				}

				if (command.equals("go")) {
					String goString = command.substring(3);
					onGo.ifPresent(c -> c.accept(goString));
				}

				if (command.equals("ponderhit")) {
					onPonderHit.ifPresent(r -> r.run());
				}

				if (command.startsWith("setoption name")) {

					onSetOption.ifPresent(c -> {
						// TODO valueなしのパターンにも対応する
						Pattern p = Pattern.compile("setoption name (.+) value (.+)");
						Matcher mat = p.matcher(command);

						String name = mat.group(1);
						String value = mat.group(2);
						c.accept(name, value);
					});
				}

				if (command.equals("stop")) {
					onStop.ifPresent(c -> c.run());
				}

				if (command.equals("quit")) {
					onQuit.ifPresent(c -> c.run());
				}

				if (command.equals("gameover")) {
					String resultString = command.substring(9);
					onGameOver.ifPresent(c -> c.accept(GameResult.valueOfIgnoreCase(resultString)));
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
		this.onUsi = Optional.ofNullable(callback);
	}

	/**
	 * コマンド "isready" に対するコールバックを設定します。
	 *
	 * @param callback
	 */
	public void setOnIsReady(Runnable callback) {
		this.onIsReady = Optional.ofNullable(callback);
	}

	/**
	 * コマンド "usinewgame" に対するコールバックを設定します。
	 *
	 * @param callback
	 */
	public void setOnUsiNewGame(Runnable callback) {
		this.onUsiNewGame = Optional.ofNullable(callback);
	}

	/**
	 * コマンド "position" に対するコールバックを設定します。コールバックの引数はPositionStringです。
	 *
	 * @param callback
	 */
	public void setOnPosition(Consumer<String> callback) {
		this.onPosition = Optional.ofNullable(callback);
	}

	/**
	 * コマンド "go" に対するコールバックを設定します。
	 *
	 * @param callback
	 */
	public void setOnGo(Consumer<String> callback) {
		this.onGo = Optional.ofNullable(callback);
	}

	/**
	 * コマンド "ponderhit" に対するコールバックを設定します。
	 *
	 * @param callback
	 */
	public void setOnPonderHit(Runnable callback) {
		this.onPonderHit = Optional.ofNullable(callback);
	}

	/**
	 * コマンド "setoption" に対するコールバックを設定します。コールバックに対する引数はname,valueです。
	 *
	 * @param callback
	 */
	public void setOnSetOption(BiConsumer<String, String> callback) {
		this.onSetOption = Optional.ofNullable(callback);
	}

	/**
	 * コマンド "stop" に対するコールバックを設定します。
	 *
	 * @param callback
	 */
	public void setOnStop(Runnable callback) {
		this.onStop = Optional.ofNullable(callback);
	}

	/**
	 * コマンド "quit" に対するコールバックを設定します。
	 *
	 * @param callback
	 */
	public void setOnQuit(Runnable callback) {
		this.onQuit = Optional.ofNullable(callback);
	}

	public void setOnGameOver(Consumer<GameResult> callback) {
		this.onGameOver = Optional.ofNullable(callback);
	}
}
