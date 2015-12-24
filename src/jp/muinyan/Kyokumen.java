package jp.muinyan;

public class Kyokumen {

	private int[][] bitboard = new int[28][3];

	public Kyokumen(String positionString) {

		String s = "lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL " + "w - 1 moves 5a6b 7g7f 3a3b";
		String[] lines = s.split(" ")[0].split("/");

		assert lines.length == 9;

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

					// その駒を表す数値
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
	}

	public void move(String moveString) {

		int fromX = Integer.parseInt(moveString.substring(0, 1)) - 1;
		int fromY = moveString.charAt(1) - 'a';
		int toX = Integer.parseInt(moveString.substring(3, 4)) - 1;
		int toY = moveString.charAt(3) - 'a';

		// 成
		boolean p = moveString.length() == 5 && moveString.charAt(4) == '+';

		// from,toのbitboard上の座標を求める
		int fromRow = fromY / 3;
		int fromCol = fromY % 3 * 9 + fromX;

		int toRow = toY / 3;
		int toCol = toY % 3 * 9 + toX;

		// 操作対象のbitboardを選択する
		for(int boardNumber = 0; boardNumber < bitboard.length; boardNumber++) {
			if((bitboard[boardNumber][fromRow] & (1 << fromCol)) != 0) {

				// 操作する
				// fromの座標を0で上書き
				int mask = 0xFFFFFFFF ^ (1 << fromCol);
				bitboard[boardNumber][fromRow] &= mask;

				// TODO toの座標に駒があったら取る


				// toの座標を1で上書き
				// TODO 成はboardNumberを+1する
				bitboard[boardNumber][toRow] |= (1 << toCol);

				break;
			}
		}
	}
}





















