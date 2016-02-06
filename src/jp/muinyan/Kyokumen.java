package jp.muinyan;

public class Kyokumen {

	private static final int SENTE_PORN = 0;
	private static final int SENTE_PORN_P = 1;
	private static final int SENTE_LANCE = 2;
	private static final int SENTE_LANCE_P = 3;
	private static final int SENTE_KNIGHT = 4;
	private static final int SENTE_KNIGHT_P = 5;
	private static final int SENTE_SILVER = 6;
	private static final int SENTE_SILVER_P = 7;
	private static final int SENTE_GOLD = 8;
	private static final int SENTE_ROOK = 9;
	private static final int SENTE_ROOK_P = 10;
	private static final int SENTE_BISHOP = 11;
	private static final int SENTE_BISHOP_P = 12;
	private static final int SENTE_KING = 13;
	private static final int GOTE_PORN = 14;
	private static final int GOTE_PORN_P = 15;
	private static final int GOTE_LANCE = 16;
	private static final int GOTE_LANCE_P = 17;
	private static final int GOTE_KNIGHT = 18;
	private static final int GOTE_KNIGHT_P = 19;
	private static final int GOTE_SILVER = 20;
	private static final int GOTE_SILVER_P = 21;
	private static final int GOTE_GOLD = 22;
	private static final int GOTE_ROOK = 23;
	private static final int GOTE_ROOK_P = 24;
	private static final int GOTE_BISHOP = 25;
	private static final int GOTE_BISHOP_P = 26;
	private static final int GOTE_KING = 27;

	private int[][] bitboard = new int[28][3];

	// PLKSGRBplksgrb
	private int[] komadai = new int[14];

	public Kyokumen(String positionString) {

		// positionString
		// "lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w - 1
		// moves 5a6b 7g7f 3a3b";

		String sfen = positionString.split(" ")[0];
		bitboard = sfenToBitboard(sfen);

		// TODO movesの解決
	}

	/**
	 * sfen形式のmove文字列を解析して盤の操作を行います。形式は(移動元xy)(移動先xy)です。初手7六歩なら7g7f。成るときは末尾に+
	 * を付けます。打つときは移動元xyの代わりに(駒の種類)*を付けます。先手2三金打ならG*2c。
	 *
	 * @param moveString
	 */
	public void analyzeMoveString(String moveString) {

		if (moveString.charAt(1) == '*') {
			// 打った場合

			char koma = moveString.charAt(0);
			int toX = Integer.parseInt(moveString.substring(3, 4)) - 1;
			int toY = moveString.charAt(3) - 'a';

			put(koma, toX, toY);

		} else {

			int fromX = Integer.parseInt(moveString.substring(0, 1)) - 1;
			int fromY = moveString.charAt(1) - 'a';
			int toX = Integer.parseInt(moveString.substring(3, 4)) - 1;
			int toY = moveString.charAt(3) - 'a';
			// 成
			boolean evolution = moveString.length() == 5 && moveString.charAt(4) == '+';

			move(fromX, fromY, toX, toY, evolution);
		}
	}

	public void move(int fromX, int fromY, int toX, int toY, boolean evolution) {

		// from,toのbitboard上の座標を求める
		int fromRow = fromY / 3;
		int fromCol = fromY % 3 * 9 + fromX;

		int toRow = toY / 3;
		int toCol = toY % 3 * 9 + toX;

		// 操作対象のbitboardを選択する
		for (int boardNumber = 0; boardNumber < bitboard.length; boardNumber++) {
			if ((bitboard[boardNumber][fromRow] & (1 << fromCol)) != 0) {

				// 操作する
				// fromの座標を0にする(1をxorで反転)
				int mask = (1 << fromCol);
				bitboard[boardNumber][fromRow] ^= mask;

				// toの座標に駒があったら取る
				for (int checkTargetBoard = 0; checkTargetBoard < bitboard.length; checkTargetBoard++) {
					if ((bitboard[boardNumber][toRow] & (1 << toCol)) != 0) {
						// 敵の駒を発見

						// 敵の駒を消す
						bitboard[checkTargetBoard][toRow] ^= (1 << toCol);

						// 自分の駒台に加える
						komadai[boardNumberToKomadaiNumber(checkTargetBoard)] += 1;
					}
				}

				// toの座標を1にする(0をxorで反転)
				// 成はboardNumberを+1する
				bitboard[evolution ? boardNumber + 1 : boardNumber][toRow] ^= (1 << toCol);

				break;
			}
		}
	}

	private void put(char koma, int x, int y) {

		int boardNumber = charToBoardNumber(koma, false);
		int toRow = y / 3;
		int toCol = x % 3 * 9 + x;

		// 指定した位置に駒を置く
		bitboard[boardNumber][toRow] ^= (1 << toCol);

		// 駒台から駒を減らす
		komadai[charToKomadaiNumber(koma)] -= 1;
	}

	/**
	 * sfen形式(moves無し)からbitboardを作成します。
	 *
	 * @param sfen
	 * @return 与えられたsfenが表す局面のbitboard
	 */
	public static int[][] sfenToBitboard(String sfen) {

		int[][] bitboard = new int[28][3];

		String[] lines = sfen.split(" ")[0].split("/");

		for (int rowIndex = 0; rowIndex < lines.length; rowIndex++) {
			String line = lines[rowIndex];

			int cellCursol = 0;
			for (int colIndex = 0; colIndex < line.length(); colIndex++) {
				char cell = line.charAt(colIndex);

				if (Character.isDigit(cell)) {
					// 空白のときは何もせずにカーソルだけ進める
					int a = Integer.parseInt(String.valueOf(cell), 10);

					cellCursol += a;

				} else {

					// TODO その駒を表す数値
					int num = 0;

					// bitboardの (Koma, rowIndex colIndex) の位置に1を立てる
					int boardNumber = rowIndex / 3;
					int boardLine = rowIndex % 3;
					int cellNumber = boardLine * 9 + (8 - cellCursol);
					bitboard[num][boardNumber] |= (1 << cellNumber);
					cellCursol++;
				}
			}
		}

		return bitboard;
	}

	/**
	 * 「その駒を取った時に置く駒台」の番号を取得します。
	 *
	 * @param boardNumber
	 *            bitboardの番号
	 * @return 駒台の番号 エラーは負数
	 */
	private static int boardNumberToKomadaiNumber(int boardNumber) {

		switch (boardNumber) {
		case GOTE_PORN:
		case GOTE_PORN_P:
			return 0;

		case GOTE_LANCE:
		case GOTE_LANCE_P:
			return 1;

		case GOTE_KNIGHT:
		case GOTE_KNIGHT_P:
			return 2;

		case GOTE_SILVER:
		case GOTE_SILVER_P:
			return 3;

		case GOTE_GOLD:
			return 4;

		case GOTE_ROOK:
		case GOTE_ROOK_P:
			return 5;

		case GOTE_BISHOP:
		case GOTE_BISHOP_P:
			return 6;

		case SENTE_PORN:
		case SENTE_PORN_P:
			return 7;

		case SENTE_LANCE:
		case SENTE_LANCE_P:
			return 8;

		case SENTE_KNIGHT:
		case SENTE_KNIGHT_P:
			return 9;

		case SENTE_SILVER:
		case SENTE_SILVER_P:
			return 10;

		case SENTE_GOLD:
			return 11;

		case SENTE_ROOK:
		case SENTE_ROOK_P:
			return 12;

		case SENTE_BISHOP:
		case SENTE_BISHOP_P:
			return 13;

		default:
			return -1;
		}
	}

	/**
	 * 「その駒が今置かれている駒台」の番号を返します。
	 *
	 * @param koma
	 *            駒の種類
	 * @return 駒台の番号 エラーは負数
	 */
	private static int charToKomadaiNumber(char koma) {

		switch (koma) {
		case 'P':
			return 0;

		case 'L':
			return 1;

		case 'N':
			return 2;

		case 'S':
			return 3;

		case 'G':
			return 4;

		case 'R':
			return 5;

		case 'B':
			return 6;

		case 'p':
			return 7;

		case 'l':
			return 8;

		case 'n':
			return 9;

		case 's':
			return 10;

		case 'g':
			return 11;

		case 'r':
			return 12;

		case 'b':
			return 13;

		case 'K':
			return -1;

		case 'k':
			return -2;
		}

		return -9;
	}

	/**
	 * 駒の種類からbitboardの番号を取得します。
	 *
	 * @param koma
	 *            駒の種類
	 * @param evolution
	 *            成っているかどうか
	 * @return bitboardの番号 エラーは負数
	 */
	private static int charToBoardNumber(char koma, boolean evolution) {

		switch (koma) {
		case 'P':
			return evolution ? SENTE_PORN_P : SENTE_PORN;

		case 'L':
			return evolution ? SENTE_LANCE_P : SENTE_LANCE;

		case 'N':
			return evolution ? SENTE_KNIGHT_P : SENTE_KNIGHT;

		case 'S':
			return evolution ? SENTE_SILVER_P : SENTE_SILVER;

		case 'G':
			return evolution ? -1 : SENTE_GOLD;

		case 'R':
			return evolution ? SENTE_ROOK_P : SENTE_ROOK;

		case 'B':
			return evolution ? SENTE_BISHOP_P : SENTE_BISHOP;

		case 'K':
			return evolution ? -2 : SENTE_KING;

		case 'p':
			return evolution ? GOTE_PORN_P : GOTE_PORN;

		case 'l':
			return evolution ? GOTE_LANCE_P : GOTE_LANCE;

		case 'n':
			return evolution ? GOTE_KNIGHT_P : GOTE_KNIGHT;

		case 's':
			return evolution ? GOTE_SILVER_P : GOTE_SILVER;

		case 'g':
			return evolution ? -3 : GOTE_GOLD;

		case 'r':
			return evolution ? GOTE_ROOK_P : GOTE_ROOK;

		case 'b':
			return evolution ? GOTE_BISHOP_P : GOTE_BISHOP;

		case 'k':
			return evolution ? -4 : GOTE_KING;
		}

		return -9;
	}
}
