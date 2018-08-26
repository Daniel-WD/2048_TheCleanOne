package com.titaniel.twothousandeightyfour.fragments.game;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.titaniel.twothousandeightyfour.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameField extends View {

    public static final int MAX_IMAGE_SIZE = 20;

    interface SelectListener {
        void onTileSelected(Tile tile);

        void onTileDeselected(Tile tile);
    }

    private SelectListener mSelectListener = null;

    interface MoveListener {
        void onMove(int direction);

        void onJoin(int newNumber);

        void onMoveCompleted();

        void onBackCompleted();
    }

    private MoveListener mMoveListener = null;

    class Tile {

        final static int STATE_UNPAIRED = 0, STATE_REMOVE_IN_FUTURE = 1, STATE_INCREASE_IN_FUTURE = 2;

        Tile(int row, int col, int number) {
            int[] pos = positionForRowCol(row, col);
            this.col = col;
            this.row = row;
            this.x = pos[0];
            this.y = pos[1];
            this.number = number;
        }

        float x, y; //top left corner of block!!!
        float scale = 1;
        float rotation = 0; //affects all(inclusive border)
        float borderRotation = 0; //affects only border
        int number, row, col;

        int nextRow = -1, nextCol = -1;
        int state = STATE_UNPAIRED;

        void animateToSelectMode() {

            ValueAnimator anim = ValueAnimator.ofFloat(scale, 0.8f);
            anim.addUpdateListener(valueAnimator -> {
                scale = (float) valueAnimator.getAnimatedValue();
                invalidate();
            });
            anim.setDuration(SELECT_MODE_TOGGLE_DURATION);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.start();

        }

        void animateToNormalMode() {

            ValueAnimator anim = ValueAnimator.ofFloat(scale, 1);
            anim.addUpdateListener(valueAnimator -> {
                scale = (float) valueAnimator.getAnimatedValue();
                invalidate();
            });
            anim.setDuration(SELECT_MODE_TOGGLE_DURATION);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.start();

        }

        void animateReturn() {
            ValueAnimator anim = ValueAnimator.ofFloat(0, 1f);
            anim.addUpdateListener(valueAnimator -> {
                scale = (float) valueAnimator.getAnimatedValue();
                invalidate();
            });
            anim.setDuration(RETURN_DURATION);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.start();
        }

        void animateRemove() {
            ValueAnimator anim = ValueAnimator.ofFloat(scale, 0f);
            anim.addUpdateListener(valueAnimator -> {
                scale = (float) valueAnimator.getAnimatedValue();
                invalidate();
            });
            anim.setDuration(REMOVE_DURATION);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.start();
        }

        void animateGeneration() {
            ValueAnimator anim = ValueAnimator.ofFloat(0, 1f);
            anim.addUpdateListener(valueAnimator -> {
                scale = (float) valueAnimator.getAnimatedValue();
                invalidate();
            });
            anim.setDuration(GENERATE_DURATION);
            anim.setInterpolator(new FastOutSlowInInterpolator());
            anim.start();
        }

        void animateFirstGeneration() {
            long dur = 250;

            long delay = 0;

            scale = 0;
            ValueAnimator anim = ValueAnimator.ofFloat(0, 1f);
            anim.addUpdateListener(valueAnimator -> {
                scale = (float) valueAnimator.getAnimatedValue();
                invalidate();
            });
            anim.setStartDelay(delay);
            anim.setDuration(dur);
            anim.setInterpolator(new FastOutSlowInInterpolator());
            anim.start();

            ValueAnimator rotationAnim = ValueAnimator.ofFloat(mRandom.nextInt(80) - 40, 0);
            rotationAnim.addUpdateListener(valueAnimator -> {
                borderRotation = (float) valueAnimator.getAnimatedValue();
                invalidate();
            });
            rotationAnim.setStartDelay(delay);
            rotationAnim.setDuration(dur);
            rotationAnim.setInterpolator(new FastOutSlowInInterpolator());
            rotationAnim.start();
        }

        void animateRegeneration(long delay) {
            long dur = 400;

            scale = 0;
            ValueAnimator anim = ValueAnimator.ofFloat(0, 1f);
            anim.addUpdateListener(valueAnimator -> {
                scale = (float) valueAnimator.getAnimatedValue();
                invalidate();
            });
            anim.setStartDelay(delay);
            anim.setDuration(dur);
            anim.setInterpolator(new FastOutSlowInInterpolator());
            anim.start();

            ValueAnimator rotationAnim = ValueAnimator.ofFloat(-20, 0);
            rotationAnim.addUpdateListener(valueAnimator -> {
                borderRotation = (float) valueAnimator.getAnimatedValue();
                invalidate();
            });
            rotationAnim.setStartDelay(delay);
            rotationAnim.setDuration(dur);
            rotationAnim.setInterpolator(new FastOutSlowInInterpolator());
            rotationAnim.start();
        }

        @Deprecated
        void moveToNextLocation() {
            if(nextCol == col) nextCol = -1;
            if(nextRow == row) nextRow = -1;
            if(nextRow == -1 && nextCol == -1) return;
            row = nextRow < 0 ? row : nextRow;
            col = nextCol < 0 ? col : nextCol;

            nextRow = -1;
            nextCol = -1;

            float oldX = x, oldY = y;

            int[] newPos = positionForRowCol(row, col);
            float dX = newPos[0] - x;
            float dY = newPos[1] - y;

            ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
            anim.addUpdateListener(valueAnimator -> {
                float f = (float) valueAnimator.getAnimatedValue();
                x = oldX + dX*f;
                y = oldY + dY*f;
                invalidate();
            });
            anim.setDuration(MOVE_DURATION);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.start();
        }

        void increase() {
            number *= 2;

            ValueAnimator rotationAnim = ValueAnimator.ofFloat(0, 90);
            rotationAnim.addUpdateListener(valueAnimator -> {
                borderRotation = (float) valueAnimator.getAnimatedValue();
                invalidate();
            });
            rotationAnim.setDuration(JOIN_DURATION);
            rotationAnim.setInterpolator(new FastOutSlowInInterpolator());
            rotationAnim.start();
/*
            ValueAnimator scaleAnim = ValueAnimator.ofFloat(1, 0.8f, 1);
            scaleAnim.addUpdateListener(valueAnimator -> {
                scale = (float) valueAnimator.getAnimatedValue();
                invalidate();
            });
            scaleAnim.setDuration(JOIN_DURATION);
            scaleAnim.setInterpolator(new FastOutSlowInInterpolator());
            scaleAnim.start();*/

            state = STATE_UNPAIRED;
        }

        // SELECTION THINGS

        boolean isSelected = false;

        void toggleSelected() {
            isSelected = !isSelected;
            if(isSelected) {
                ValueAnimator anim = ValueAnimator.ofFloat(scale, 1f);
                anim.addUpdateListener(valueAnimator -> {
                    scale = (float) valueAnimator.getAnimatedValue();
                    invalidate();
                });
                anim.setDuration(SELECT_MODE_TOGGLE_DURATION);
                anim.setInterpolator(new AccelerateDecelerateInterpolator());
                anim.start();
                if(mSelectListener != null) mSelectListener.onTileSelected(this);
            } else {
                ValueAnimator anim = ValueAnimator.ofFloat(scale, 0.8f);
                anim.addUpdateListener(valueAnimator -> {
                    scale = (float) valueAnimator.getAnimatedValue();
                    invalidate();
                });
                anim.setDuration(SELECT_MODE_TOGGLE_DURATION);
                anim.setInterpolator(new AccelerateDecelerateInterpolator());
                anim.start();
                if(mSelectListener != null) mSelectListener.onTileDeselected(this);
            }
        }

        //MASS MOVING THINGS

        float oldX, oldY;
        float dX, dY;

        boolean prepareForMassMove() {
            boolean needsMove = true;
            if(nextCol == col) nextCol = -1;
            if(nextRow == row) nextRow = -1;
            if(nextRow == -1 && nextCol == -1) needsMove = false;
            row = nextRow < 0 ? row : nextRow;
            col = nextCol < 0 ? col : nextCol;

            nextRow = -1;
            nextCol = -1;

            oldX = x;
            oldY = y;

            int[] newPos = positionForRowCol(row, col);
            dX = newPos[0] - x;
            dY = newPos[1] - y;

            return needsMove;
        }

        void updateMassMove(float f) {
            x = oldX + dX*f;
            y = oldY + dY*f;
        }
    }

    public static class FieldImage {

        public FieldImage(int[][] image) {
            this.image = image;
        }

        public int[][] image;

    }

    private ArrayList<FieldImage> mImages = new ArrayList<>();

    private final static long GENERATE_DURATION = 150;
    private final static long MOVE_DURATION = 130;
    private final static long JOIN_DURATION = 180;

    private final static long REMOVE_DURATION = 150;
    private final static long RETURN_DURATION = 150;

    private final static long SELECT_MODE_TOGGLE_DURATION = 150;

    public final static int DIRECTION_UP = 0;
    public final static int DIRECTION_DOWN = 1;
    public final static int DIRECTION_LEFT = 2;
    public final static int DIRECTION_RIGHT = 3;

    private final SparseIntArray mColorMap = new SparseIntArray();

    private final float[] mScalings = {
            1f,//1
            0.90f,//2
            0.65f,//3
            0.5f,//4
            0.4f,//5
            0.35f,//6
            0.3f,//7
            0.28f,//8
            0.25f//9
    };

    private ArrayList<Tile> mTiles = new ArrayList<>();

    private SpannableStringBuilder mDynamicText = new SpannableStringBuilder("");
    private DynamicLayout mDynamicLayout;

    private final Paint mWhitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mTilePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    private int mBorderColor;
    private int mDividerColor;

    private final float mBorderWidth = 1f; //1f
    private final float mDividerWidth = 0.8f; //0.8f
    private final float mTileBorderWidth = 2f;
    //    private final float mTileBorderWidth = 2f;
    private final float mMinSwipeDistance = 10;
    private float mBorderWidthPx, mDividerWidthPx, mTileBorderWidthPx, mTextSizePx, mMinSwipeDistancePx;

    private float mDividerScale = 0.78f;
    private float mBorderScale = 1f;
    private final float mTileScale = 0.77f;
    private final float mTileRadiusRatio = 0.042f;
    private final float mTextSizeRatio = 0.55f;
    private final float mFieldScale = 1f; //0.98

    private int mWidth, mHeight;

    private int mFullBlockSize;

    private int mFieldSize = 4;

    private Random mRandom = new Random();

    private float mDownX = 0, mDownY = 0;
    private boolean canMove = true;
    boolean inSelectMode = false;

    private Tile mDownTile = null;

    private Path mBaseTilePath, mBorderPath, mDividersPath;

    public GameField(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

//        setScaleX(2f - mFieldScale);
//        setScaleY(2f - mFieldScale);

        mBorderColor = ContextCompat.getColor(context, R.color.game_border_color);
        mDividerColor = ContextCompat.getColor(context, R.color.game_divider_color);

        mBorderWidthPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources().getDisplayMetrics());
        mDividerWidthPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mDividerWidth, getResources().getDisplayMetrics());
        mTileBorderWidthPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mTileBorderWidth, getResources().getDisplayMetrics());
        mMinSwipeDistancePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mMinSwipeDistance, getResources().getDisplayMetrics());

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidthPx);

        mDividerPaint.setStyle(Paint.Style.STROKE);
        mDividerPaint.setColor(mDividerColor);
        mDividerPaint.setStrokeWidth(mDividerWidthPx);

        mWhitePaint.setStyle(Paint.Style.FILL);
        mWhitePaint.setColor(Color.WHITE);
//        mWhitePaint.setAlpha((int) (0 * 256));

        mTilePaint.setStyle(Paint.Style.STROKE);
        mTilePaint.setStrokeWidth(mTileBorderWidthPx);
        mTilePaint.setColor(Color.BLACK);

        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));

        //colors
        float[] firstColor = {282, 0.64f, 0.93f};


        for(int i = 1; i <= 6; i++) {
//            mColorMap.append((int) Math.pow(2, i), Color.BLACK/*Color.HSVToColor(firstColor)*/);
            mColorMap.append((int) Math.pow(2, i), Color.HSVToColor(firstColor));
            //firstColor[0] -= 30;
            firstColor[2] -= 0.1f;
        }

        float[] secondColor = {214, 0.70f, 0.84f};

        for(int i = 7; i <= 10; i++) {
            mColorMap.append((int) Math.pow(2, i), Color.HSVToColor(secondColor));
            //firstColor[0] -= 30;
            secondColor[2] -= 0.1f;
        }

        float[] thirdColor = {170, 0.99f, 0.79f};

        for(int i = 11; i <= 20; i++) {
            mColorMap.append((int) Math.pow(2, i), Color.HSVToColor(thirdColor));
            //firstColor[0] -= 30;
            thirdColor[2] -= 0.1f;
        }
    }

    public void animateIn() {

        long delay = 0;

        long dur = 400;

        //divider
        ValueAnimator divScaleAnim = ValueAnimator.ofFloat(0f, mDividerScale);
        divScaleAnim.addUpdateListener(animator -> {
            mDividerScale = (float) animator.getAnimatedValue();
            invalidate();
        });
        divScaleAnim.setStartDelay(delay);
        divScaleAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        divScaleAnim.setDuration(dur);
        divScaleAnim.start();

        mDividerPaint.setColor(Color.TRANSPARENT);
        ValueAnimator divAlphaAnim = ValueAnimator.ofArgb(Color.TRANSPARENT, mDividerColor);
        divAlphaAnim.addUpdateListener(animator -> {
            mDividerPaint.setColor((int) animator.getAnimatedValue());
            invalidate();
        });
        divAlphaAnim.setStartDelay(delay);
        divAlphaAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        divAlphaAnim.setDuration(dur);
        divAlphaAnim.start();

        delay += 0;

        dur = 600;

        //border
        ValueAnimator borderScaleAnim = ValueAnimator.ofFloat(0f, mBorderScale);
        borderScaleAnim.addUpdateListener(animator -> {
            mBorderScale = (float) animator.getAnimatedValue();
            invalidate();
        });
        borderScaleAnim.setStartDelay(delay);
        borderScaleAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        borderScaleAnim.setDuration(dur);
        borderScaleAnim.start();

        mBorderPaint.setColor(Color.TRANSPARENT);
        ValueAnimator borderAlphaAnim = ValueAnimator.ofArgb(Color.TRANSPARENT, mBorderColor);
        borderAlphaAnim.addUpdateListener(animator -> {
            mBorderPaint.setColor((int) animator.getAnimatedValue());
            invalidate();
        });
        borderAlphaAnim.setStartDelay(delay);
        borderAlphaAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        borderAlphaAnim.setDuration(dur);
        borderAlphaAnim.start();

    }

    public void startSelectMode(SelectListener selectListener) {
        mSelectListener = selectListener;
        inSelectMode = true;
        setAllTilesSelected(false);
        for(Tile tile : mTiles) {
            tile.animateToSelectMode();
        }
    }

    public void stopSelectMode() {
        mSelectListener = null;
        inSelectMode = false;
        setAllTilesSelected(false);
        for(Tile tile : mTiles) {
            tile.animateToNormalMode();
        }
    }

    public void removeSelectListener() {
        mSelectListener = null;
    }

    public boolean performBack() {
        if(mImages.size() == 0) return false;
        ArrayList<Tile> mRemovedTiles = new ArrayList<>();
        int[][] lastImage = mImages.get(mImages.size() - 1).image;
        for(Tile tile : mTiles) {
            if(lastImage[tile.row][tile.col] != tile.number) {
                tile.animateRemove();
                mRemovedTiles.add(tile);
            }
        }
        postDelayed(() -> {
            mTiles.removeAll(mRemovedTiles);

            for(int i = 0; i < lastImage.length; i++) {
                nextTile:
                for(int j = 0; j < lastImage.length; j++) {
                    if(lastImage[i][j] == 0) continue;
                    for(Tile tile : mTiles) {
                        if(tile.row == i && tile.col == j) continue nextTile;
                    }
                    Tile newTile = new Tile(i, j, lastImage[i][j]);
                    mTiles.add(newTile);
                    newTile.animateReturn();
                }
            }

            mImages.remove(mImages.size() - 1);
        }, REMOVE_DURATION);

        postDelayed(() -> {
            if(mMoveListener != null) mMoveListener.onBackCompleted();
        }, REMOVE_DURATION*2);

        return true;
    }

    public boolean canPerformBack() {
        return mImages.size() != 0;
    }

    public void deleteTile(Tile tile) {
        saveBackup();
        tile.animateRemove();
        postDelayed(() -> {
            mTiles.remove(tile);
        }, REMOVE_DURATION);
    }

    public void swapTiles(Tile tile1, Tile tile2) {
        saveBackup();
        tile1.nextCol = tile2.col;
        tile1.nextRow = tile2.row;
        tile2.nextCol = tile1.col;
        tile2.nextRow = tile1.row;
        massMove();
    }

    public void doubleTile(Tile tile) {
        saveBackup();
        tile.increase();
        if(mMoveListener != null) mMoveListener.onJoin(tile.number);
    }

    public boolean canUserMove() {
        if(mTiles.size() != mFieldSize*mFieldSize) return true;
        boolean result = false;
        moveVertical(true, true);
        for(Tile tile : mTiles) {
            if(tile.nextRow != -1 && tile.nextRow != tile.row) {
                tile.nextRow = -1;
                result = true;
            }
            tile.state = Tile.STATE_UNPAIRED;
        }
        moveHorizontal(true, true);
        for(Tile tile : mTiles) {
            if(tile.nextCol != -1 && tile.nextCol != tile.col) {
                tile.nextCol = -1;
                result = true;
            }
            tile.state = Tile.STATE_UNPAIRED;
        }
        canMove = true;
        return result;
    }

    public ArrayList<FieldImage> getSaveImage() {
        ArrayList<FieldImage> save = new ArrayList<>(mImages);
        save.add(new FieldImage(buildFieldImage()));
        return save;
    }

    public void setSaveImageAndAnimate(ArrayList<FieldImage> images) {
        if(images == null) return;
        canMove = false;
        int[][] last = images.get(images.size() - 1).image;
        for(int i = 0; i < last.length; i++) {
            for(int j = 0; j < last.length; j++) {
                if(last[i][j] == 0) continue;
                Tile newTile = new Tile(i, j, last[i][j]);
                mTiles.add(newTile);
                newTile.animateRegeneration(mRandom.nextInt(300));
            }
        }
        if(images.size() > 0) images.remove(images.size() - 1);
        mImages = images;
        postDelayed(() -> canMove = true, 300);
    }


    private void moveVertical(boolean up, boolean calcOnly) {
        canMove = false;
        long first = System.currentTimeMillis();

        if(mMoveListener != null) {
            mMoveListener.onMove(up ? DIRECTION_UP : DIRECTION_DOWN);
        }

        Collections.sort(mTiles, (tile, t1) -> {
            if(up) {
                return Integer.compare(tile.row, t1.row);
            } else {
                return Integer.compare(t1.row, tile.row);
            }
        });

        for(Tile tile : mTiles) {
            if((up && tile.row > 0) || (!up && tile.row < mFieldSize - 1)) {
                Tile nextTile = null;
                for(Tile t : mTiles) {
                    if(t.equals(tile)) break;
                    if(tile.col == t.col) { //same col
                        nextTile = t;
                    }
                }

                if(nextTile == null) {
                    tile.nextRow = up ? 0 : mFieldSize - 1;
                } else {
                    if(nextTile.state != Tile.STATE_UNPAIRED || tile.number != nextTile.number) {

                        if(up) {
                            tile.nextRow = nextTile.nextRow == -1 ? nextTile.row + 1 : nextTile.nextRow + 1;
                        } else {
                            tile.nextRow = nextTile.nextRow == -1 ? nextTile.row - 1 : nextTile.nextRow - 1;
                        }
                    } else {
                        tile.state = Tile.STATE_REMOVE_IN_FUTURE;
                        nextTile.state = Tile.STATE_INCREASE_IN_FUTURE;
                        tile.nextRow = nextTile.nextRow == -1 ? nextTile.row : nextTile.nextRow;
                    }
                }
            }
        }

        if(!calcOnly) actionsAfterMoveCalc();

        Log.e("moveVertical:duration", String.valueOf(System.currentTimeMillis() - first));
    }

    private void moveHorizontal(boolean left, boolean calcOnly) {
        canMove = false;
        long first = System.currentTimeMillis();

        if(mMoveListener != null) {
            mMoveListener.onMove(left ? DIRECTION_LEFT : DIRECTION_RIGHT);
        }

        Collections.sort(mTiles, (tile, t1) -> {
            if(left) {
                return Integer.compare(tile.col, t1.col);
            } else {
                return Integer.compare(t1.col, tile.col);
            }
        });

        for(Tile tile : mTiles) {
            if((left && tile.col > 0) || (!left && tile.col < mFieldSize - 1)) {
                Tile nextTile = null;
                for(Tile t : mTiles) {
                    if(t.equals(tile)) break;
                    if(tile.row == t.row) { //same row
                        nextTile = t;
                    }
                }

                if(nextTile == null) {
                    tile.nextCol = left ? 0 : mFieldSize - 1;
                } else {
                    if(nextTile.state != Tile.STATE_UNPAIRED || tile.number != nextTile.number) {

                        if(left) {
                            tile.nextCol = nextTile.nextCol == -1 ? nextTile.col + 1 : nextTile.nextCol + 1;
                        } else {
                            tile.nextCol = nextTile.nextCol == -1 ? nextTile.col - 1 : nextTile.nextCol - 1;
                        }
                    } else {
                        tile.state = Tile.STATE_REMOVE_IN_FUTURE;
                        nextTile.state = Tile.STATE_INCREASE_IN_FUTURE;
                        tile.nextCol = nextTile.nextCol == -1 ? nextTile.col : nextTile.nextCol;
                    }
                }
            }
        }

        if(!calcOnly) actionsAfterMoveCalc();

        Log.e("moveHorizontal:duration", String.valueOf(System.currentTimeMillis() - first));
    }

    private void actionsAfterMoveCalc() {

//        for(Tile tile : mTiles) {
//            tile.moveToNextLocation(); --> deprecated
//        }
        saveBackup();

        boolean smtNeedToMove = massMove();

        if(smtNeedToMove) {
            postDelayed(this::joinTiles, MOVE_DURATION - 10);
            postDelayed(() -> generateTile(false), JOIN_DURATION + 20);
            postDelayed(this::moveCompleted, JOIN_DURATION + 30);
        } else {
            mImages.remove(mImages.size() - 1);
            canMove = true;
        }
    }

    private boolean massMove() {
        boolean smtNeedsMove = false;
        for(Tile tile : mTiles) {
            if(tile.prepareForMassMove() && !smtNeedsMove) {
                smtNeedsMove = true;
            }
        }
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(valueAnimator -> {
            float f = (float) valueAnimator.getAnimatedValue();
            for(Tile tile : mTiles) {
                tile.updateMassMove(f);
            }
            invalidate();
        });
        anim.setDuration(MOVE_DURATION);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        if(smtNeedsMove) anim.start();

        return smtNeedsMove;
    }

    private boolean joinTiles() {
        ArrayList<Tile> removableTiles = new ArrayList<>();
        for(Tile tile : mTiles) {
            if(tile.state == Tile.STATE_REMOVE_IN_FUTURE) {
                removableTiles.add(tile);
            } else if(tile.state == Tile.STATE_INCREASE_IN_FUTURE) {
                tile.increase();
                if(mMoveListener != null) mMoveListener.onJoin(tile.number);
            }
        }
        mTiles.removeAll(removableTiles);
        return removableTiles.size() != 0;
    }

    private void moveCompleted() {
        Log.d("tilesize", "" + mTiles.size());
        Log.d("imagesize", "" + mImages.size());
        if(mMoveListener != null) mMoveListener.onMoveCompleted();
        canMove = true;
    }

    private void generateTile(boolean first) {
        if(mTiles.size() >= mFieldSize*mFieldSize) return;

        int rCol;
        int rRow;
        int pow = mRandom.nextInt(2) + 1;

        do {
            rCol = mRandom.nextInt(mFieldSize);
            rRow = mRandom.nextInt(mFieldSize);
        } while(!isPositionEmpty(rRow, rCol));

        Tile tile = new Tile(rRow, rCol, (int) Math.pow(2, pow));

        mTiles.add(tile);
        if(first) {
            tile.animateFirstGeneration();
        } else {
            tile.animateGeneration();
        }

//        animateGeneration(tile);
    }

    private void setAllTilesSelected(boolean selected) {
        for(Tile tile : mTiles) {
            tile.isSelected = selected;
        }
    }

    public void showStartTiles() {
        int tileCount = 2;
        canMove = false;
        long delay = 150;
        for(int i = 0; i < tileCount; i++) {
            postDelayed(() -> generateTile(true), i*delay);
        }
        postDelayed(() -> canMove = true, tileCount*delay + GENERATE_DURATION);
    }

    private void saveBackup() {
        if(mImages.size() == MAX_IMAGE_SIZE) mImages.remove(0);
        mImages.add(new FieldImage(buildFieldImage()));
    }

    private int[][] buildFieldImage() {
        int[][] image = new int[mFieldSize][mFieldSize];
        for(int i = 0; i < image.length; i++) {
            for(int j = 0; j < image[i].length; j++) {
                image[i][j] = 0;
            }
        }
        for(Tile tile : mTiles) {
            image[tile.row][tile.col] = tile.number;
        }
        return image;
    }

    private boolean isPositionEmpty(int row, int col) {
        for(Tile tile : mTiles) {
            if(tile.row == row && tile.col == col) return false;
        }
        return true;
    }

    private int[] positionForRowCol(int row, int col) {
        int[] res = new int[2];
        res[0] = col*mFullBlockSize;
        res[1] = row*mFullBlockSize;
        return res;
    }

    private int realTileSize() {
        float negative = mFullBlockSize*(1 - mTileScale);
        return (int) (mFullBlockSize - negative);
    }

    public void clear() {
        mTiles.clear();
        mImages.clear();
        invalidate();
    }

    Rect textRect = new Rect();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        long first = System.nanoTime();

//        canvas.save();
//
//        canvas.scale(mFieldScale, mFieldScale, canvas.getWidth()/2, canvas.getHeight()/2);


//        canvas.drawRect(0, 0, mWidth, mHeight, mWhitePaint);
        //canvas.drawRect(0, 0, mWidth, mHeight, mBorderPaint);

        float halfNegative = mFullBlockSize*(1 - mDividerScale)/2;
        float borderLength = getWidth() - 2*halfNegative;
        float borderMargin = halfNegative + (1 - mBorderScale)*borderLength/2;

//        if(mDividerScale == 1 && mBorderScale == 1) {
//            canvas.drawPath(mBorderPath, mBorderPaint);
//            canvas.drawPath(mDividersPath, mDividerPaint);
//        } else {

        //left border
        canvas.drawLine(0, borderMargin, 0, mHeight - borderMargin, mBorderPaint);

        //right border
        canvas.drawLine(mWidth, borderMargin, mWidth, mHeight - borderMargin, mBorderPaint);

        //top border
        canvas.drawLine(borderMargin, 0, mWidth - borderMargin, 0, mBorderPaint);

        //bottom border
        canvas.drawLine(borderMargin, mHeight, mWidth - borderMargin, mHeight, mBorderPaint);


        for(int i = 0; i < mFieldSize; i++) {
            for(int j = 0; j < mFieldSize; j++) {
                if(i != 0)
                    canvas.drawLine(mFullBlockSize*i, mFullBlockSize*j + halfNegative, mFullBlockSize*i, mFullBlockSize*(j + 1) - halfNegative, mDividerPaint);
                if(j != 0)
                    canvas.drawLine(mFullBlockSize*i + halfNegative, mFullBlockSize*j, mFullBlockSize*(i + 1) - halfNegative, mFullBlockSize*j, mDividerPaint);
            }
        }
//        }


        float radius = (mFullBlockSize - halfNegative*2)*mTileRadiusRatio;
        for(Tile tile : mTiles) {

            canvas.save();
            canvas.translate(tile.x + halfNegative, tile.y + halfNegative);

            if(tile.scale != 1) {
                canvas.scale(tile.scale, tile.scale, realTileSize()/2, realTileSize()/2);
            }

            mTilePaint.setColor(mColorMap.get(tile.number));
            mTextPaint.setColor(mColorMap.get(tile.number));

            canvas.save();
            if(tile.borderRotation + tile.rotation != 0 && tile.borderRotation + tile.rotation != 360) {
                canvas.rotate(tile.borderRotation + tile.rotation, realTileSize()/2, realTileSize()/2);
            }

//            canvas.drawPath(mBaseTilePath, mTilePaint);
            canvas.drawRoundRect(0, 0, realTileSize(), realTileSize(),
                    radius, radius, mTilePaint);

            canvas.restore();

            if(tile.rotation != 0 && tile.rotation != 360) {
                canvas.rotate(tile.rotation, realTileSize()/2, realTileSize()/2);
            }

//            long first = System.nanoTime();

            String text = String.valueOf(tile.number);

            float textScale = mScalings[text.length() - 1];
            mTextPaint.setTextSize(mTextSizePx*textScale);

//            Log.e("draw::checkTWO", String.valueOf(System.nanoTime() - first));
//            first = System.nanoTime();

            textRect.setEmpty();
            mTextPaint.getTextBounds(text, 0, text.length(), textRect);

            float textWidth = mTextPaint.measureText(text);

//            Log.e("draw::checkTHREE", String.valueOf(System.nanoTime() - first));
//            first = System.nanoTime();

            float yOffset = (realTileSize() + textRect.height())/2 - 1;
            canvas.translate(0, yOffset);

            canvas.drawText(text, ((float) mFullBlockSize - 2*halfNegative)/2f - textWidth/2 - 1, 0, mTextPaint);

            canvas.restore();

//            Log.e("draw::checkFOUR", String.valueOf(System.nanoTime() - first));
//            Log.e("draw::check", "------------------------------------------------------------------------------------");

        }

//        canvas.restore();


//        Log.e("draw::check", String.valueOf(System.nanoTime() - first));
//        first = System.nanoTime();

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isEnabled()) return false;
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(inSelectMode) {
                    mDownTile = findTileArea(event.getX(), event.getY());
                    break;
                }

                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if(inSelectMode) {
                    if(mDownTile == null) break;
                    Tile tile = findTileArea(event.getX(), event.getY());
                    if(mDownTile.equals(tile)) {
                        tile.toggleSelected();
                    }
                    break;
                }
            case MotionEvent.ACTION_MOVE:
                if(inSelectMode) break;
                if(!canMove) {
                    return false;
                }
                float dX = event.getX() - mDownX;
                float dY = event.getY() - mDownY;
                if(Math.abs(dX) > Math.abs(dY) && Math.abs(dX) >= mMinSwipeDistancePx) {
                    mDownX = event.getX();
                    mDownY = event.getY();
                    if(dX < 0) {
                        moveHorizontal(true, false);
                    } else {
                        moveHorizontal(false, false);
                    }
                } else if(Math.abs(dY) > Math.abs(dX) && Math.abs(dY) >= mMinSwipeDistancePx) {
                    mDownX = event.getX();
                    mDownY = event.getY();
                    if(dY < 0) {
                        moveVertical(true, false);
                    } else {
                        moveVertical(false, false);
                    }
                }
                break;
        }
        return true;
    }

    private Tile findTileArea(float x, float y) {
        for(Tile tile : mTiles) {
            if(tile.x <= x && tile.y <= y && tile.x + mFullBlockSize >= x && tile.y + mFullBlockSize >= y) {
                return tile;
            }
        }
        return null;
    }


    public void setFieldSize(int size) {
        mFieldSize = size;
        onSizeChanged(getWidth(), getHeight(), getWidth(), getHeight());
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        mFullBlockSize = w/mFieldSize;

        mDynamicLayout = new DynamicLayout(mDynamicText, mTextPaint, realTileSize(),
                Layout.Alignment.ALIGN_CENTER, 1, 0, false);

        //textsize px
        mTextSizePx = realTileSize()*mTextSizeRatio;

        //base tile path
        mBaseTilePath = new Path();
        float halfNegative = mFullBlockSize*(1 - mTileScale)/2;
        float radius = (mFullBlockSize - halfNegative*2)*mTileRadiusRatio;
        mBaseTilePath.addRoundRect(0, 0, realTileSize(), realTileSize(),
                radius, radius, Path.Direction.CW);

        // border path
        mBorderPath = new Path();

        float borderLength = getWidth() - 2*halfNegative;

        float borderMargin = halfNegative + (1 - mBorderScale)*borderLength/2;

        //left border
        mBorderPath.moveTo(0, borderMargin);
        mBorderPath.lineTo(0, mHeight - borderMargin);

        //right border
        mBorderPath.moveTo(mWidth, borderMargin);
        mBorderPath.lineTo(mWidth, mHeight - borderMargin);

        //top border
        mBorderPath.moveTo(borderMargin, 0);
        mBorderPath.lineTo(mWidth - borderMargin, 0);

        //bottom border
        mBorderPath.moveTo(borderMargin, mHeight);
        mBorderPath.lineTo(mWidth - borderMargin, mHeight);

        //dividers path
        mDividersPath = new Path();

        for(int i = 0; i < mFieldSize; i++) {
            for(int j = 0; j < mFieldSize; j++) {
                if(i != 0) {
                    mDividersPath.moveTo(mFullBlockSize*i, mFullBlockSize*j + halfNegative);
                    mDividersPath.lineTo(mFullBlockSize*i, mFullBlockSize*(j + 1) - halfNegative);
                }
                if(j != 0) {
                    mDividersPath.moveTo(mFullBlockSize*i + halfNegative, mFullBlockSize*j);
                    mDividersPath.lineTo(mFullBlockSize*(i + 1) - halfNegative, mFullBlockSize*j);
                }
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.v("GameField onMeasure w", MeasureSpec.toString(widthMeasureSpec));
        Log.v("GameField onMeasure h", MeasureSpec.toString(heightMeasureSpec));

        int width = MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(width, heightMeasureSpec));
    }

    private int getMaxPadding() {
        return Math.max(getPaddingStart(), Math.max(getPaddingTop(), Math.max(getPaddingBottom(), getPaddingEnd())));
    }


    public void setMoveListener(MoveListener listener) {
        mMoveListener = listener;
    }

    public void removeMoveListener() {
        mMoveListener = null;
    }
}
