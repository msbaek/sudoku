package com.example.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class PuzzleView extends View {
	private static final String TAG = "Sudoku";
	private final Game game;
	private float width; // 한 칸의 폭(width of one tile);
	private float height; // 한 칸의 높이(height of one tile);
	private int selX; // 선택된 것의 X 인덱스(X index of selection)
	private int selY; // 선택된 것의 Y 인덱스(Y index of selection)
	private final Rect selRect = new Rect();

	public PuzzleView(Context context) {
		super(context);
		this.game = (Game) context;
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = w / 9f;
		height = h / 9f;
		getRect(selX, selY, selRect);
		Log.d(TAG, "onSizeChanged: width " + width + ", height " + height); 
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void getRect(int x, int y, Rect rect) {
		rect.set((int)(x * width), (int)(y * height), (int)(x * width + width), (int)(y * height + height));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// 1. 배경 그리기(draw the background)
		Paint background = new Paint();
		background.setColor(getResources().getColor(R.color.puzzle_background));
		canvas.drawRect(0,  0, getWidth(), getHeight(), background);
		// 2. 게임판 그리기(draw the board)
		// 2.1 격자 선의 색깔 정의하기(define colors for the grid lines)
		Paint dark = new Paint();
		dark.setColor(getResources().getColor(R.color.puzzle_dark));
		
		Paint hilite = new Paint();
		hilite.setColor(getResources().getColor(R.color.puzzle_hilite));
		
		Paint light = new Paint();
		light.setColor(getResources().getColor(R.color.puzzle_light));
		// 2.2 세부적인 격자 선 그리기(draw the minor grid lines)
		for(int i = 0; i < 9; i++) {
			canvas.drawLine(0,  i * height,  getWidth(),  i * height, light);
			canvas.drawLine(0,  i * height + 1,  getWidth(),  i * height + 1, hilite);
			canvas.drawLine(i * width, 0,  i * width, getHeight(),  light);
			canvas.drawLine(i * width + 1, 0,  i * width + 1, getHeight(),  hilite);
		}
		// 2.3 큰 격자 선 그리기(draw the major grid lines)
		for(int i = 0; i < 9; i++) {
			if(i % 3 != 0)
				continue;
			canvas.drawLine(0,  i * height,  getWidth(),  i * height, dark);
			canvas.drawLine(0,  i * height + 1,  getWidth(),  i * height + 1, hilite);
			canvas.drawLine(i * width, 0,  i * width, getHeight(),  dark);
			canvas.drawLine(i * width + 1, 0,  i * width + 1, getHeight(),  hilite);
		}
		
		// 3. 숫자 그리기(draw the numbers)
        // 3.1 숫자에 쓸 색깔과 스타일 정의하기(Define color and style for numbers)
        Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
        foreground.setColor(getResources().getColor(R.color.puzzle_foreground));
        foreground.setStyle(Paint.Style.FILL);
        foreground.setTextSize(height * 0.75f);
        foreground.setTextScaleX(width / height);
        foreground.setTextAlign(Paint.Align.CENTER);
        // 3.2 숫자를 칸의 중앙에 그리기(Draw the number in the center of the tile)
        Paint.FontMetrics fm = foreground.getFontMetrics();
        // 3.3 X축 중앙 정렬:정렬 사용(X를 중앙으로)(Centering in X:use alignment (and X at midpoint))
        float x = width / 2;
        // 3.4 Y축 중앙 정렬:폰트의 위 아래 높이를 먼저 측정(Centering in Y:measure ascent/descent first)
        float y = height / 2 - (fm.ascent + fm.descent) / 2;
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                canvas.drawText(String.valueOf(this.game.getTileString(i, j)), i * width + x, j * height + y, foreground);
            }
        }
		// 4. 힌트 그리기(draw the hints)
        Log.d(TAG, "selRect=" + selRect);
        Paint selected = new Paint();
        selected.setColor(getResources().getColor(R.color.puzzle_selected));
        canvas.drawRect(selRect, selected);
		// 5. 선택 그리기(draw the selection)
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown: keyCode=" + keyCode + ", event=" + event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                select(selX, selY - 1);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                select(selX, selY + 1);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                select(selX - 1, selY);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                select(selX + 1, selY);
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    private void select(int x, int y) {
        invalidate(selRect);
        selX = Math.min(Math.max(x, 0), 8);
        selY = Math.min(Math.max(y, 0), 8);
        getRect(selX, selY, selRect);
        invalidate(selRect);
    }
}