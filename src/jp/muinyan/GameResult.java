package jp.muinyan;

/**
 * ゲーム結果を表す列挙型です。
 * @author muinyan
 *
 */
public enum GameResult {

	WIN, LOSE, DRAW;

	public static GameResult valueOfIgnoreCase(String name) {
		return valueOf(name.toUpperCase());
	}
}
