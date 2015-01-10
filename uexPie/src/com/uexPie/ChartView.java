package com.uexPie;

import java.util.ArrayList;
import java.util.List;

import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

import com.uexPie.bean.PointBean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

public class ChartView extends View {
	private boolean mAa;
	private int mChartsNum;
	private int windowWidth;
	private int windowHeight;
	private ArrayList<ChartProp> mChartProps;
	private Point mCenterPoint;
	private int mR;
	private float mStartAngle;
	private int mWizardLineLength;
	private int mScreenWidth;
	private int mScreenHeight;
	private final int marLength = 0;// 文字框距两边的距离
	private final int marTextLength = 10;// 文字距文字边框的距离
	private final int apexLength = 10;// 顶端三角形的高度
	private int bgR = 382;// 背景图，里面这个圆的直径 这个跟背景图有关
	private float backR = 0;// 缩放之后背景图 半径
	private int mFontSize = 16;// 字体大小
	private float RATIO;
	private int allLength = 0;
	private Bitmap bgBitmap;
	private Bitmap rectangleBitmap;
	private Bitmap triangleBitmap;
	private Bitmap rightRectBitmap;
	private Bitmap rightTriBitmap;
	private Bitmap bkBitmap;
	private Bitmap portraintBkBitmap;

	public ChartView(Context context) {
		super(context);
		initParams(context);
	}

	public ChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initParams(context);
	}

	public ChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initParams(context);
	}

	/**
	 * initial some params 初始化默认参数*
	 */
	private void initParams(Context context) {
		mAa = true;
		mChartsNum = 1;
		mChartProps = new ArrayList<ChartProp>();
		mCenterPoint = new Point(100, 100);
		mR = 50;
		mStartAngle = 0;
		mWizardLineLength = 10;

		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		mScreenWidth = wm.getDefaultDisplay().getWidth();
		mScreenHeight = wm.getDefaultDisplay().getHeight();
		float ratioWidth = (float) mScreenWidth / 480;
		float ratioHeight = (float) mScreenHeight / 800;
		RATIO = Math.min(ratioWidth, ratioHeight);
		mFontSize = Math.round(mFontSize * RATIO);
		bgBitmap = ((BitmapDrawable) context.getResources().getDrawable(
				EUExUtil.getResDrawableID("plugin_uexpie_bingtu_bg")))
				.getBitmap();
		bgR = 382 * bgBitmap.getWidth() / 414;
		rectangleBitmap = ((BitmapDrawable) context.getResources().getDrawable(
				EUExUtil.getResDrawableID("plugin_uexpie_bingtu_rectangle")))
				.getBitmap();
		triangleBitmap = ((BitmapDrawable) context.getResources().getDrawable(
				EUExUtil.getResDrawableID("plugin_uexpie_bingtu_triangle")))
				.getBitmap();
		rightRectBitmap = ((BitmapDrawable) context
				.getResources()
				.getDrawable(
						EUExUtil.getResDrawableID("plugin_uexpie_bingtu_rectangle_right")))
				.getBitmap();
		rightTriBitmap = ((BitmapDrawable) context
				.getResources()
				.getDrawable(
						EUExUtil.getResDrawableID("plugin_uexpie_bingtu_triangle_right")))
				.getBitmap();
		bkBitmap = ((BitmapDrawable) context.getResources().getDrawable(
				EUExUtil.getResDrawableID("plugin_uexpie_bingtu_back")))
				.getBitmap();
		portraintBkBitmap = ((BitmapDrawable) context
				.getResources()
				.getDrawable(
						EUExUtil.getResDrawableID("plugin_uexpie_bingtu_portrait_back")))
				.getBitmap();
	}

	/**
	 * create charts' property 创建饼状图的属性
	 * 
	 * @param chartsNum
	 *            charts' number 饼状图的个数
	 * @return charts' property's list 饼状图属性的list
	 */
	public ArrayList<ChartProp> createCharts(int chartsNum, int width,
			int height) {
		mChartsNum = chartsNum;
		createChartProp(chartsNum);
		windowWidth = width;
		windowHeight = height;
		return mChartProps;
	}

	/**
	 * set the first chart's start angle when draw 设置第一个扇形绘制时的起始角度
	 * 
	 * @param startAngle
	 *            the first chart's start angle 第一个扇形绘制时的起始角度
	 */
	public void setStartAngle(float startAngle) {
		mStartAngle = startAngle;
		invalidate();
	}

	/**
	 * set the view anti alias. 设置是否抗锯齿。
	 * 
	 * @param aa
	 *            true means will draw hightly; true 意味着高质量绘图
	 */
	public void setAntiAlias(boolean aa) {
		mAa = aa;
		invalidate();
	}

	/**
	 * set chart's center point 设置饼状图的中心点
	 * 
	 * @param centerPoint
	 *            chart's center point 饼状图的中心点坐标
	 */
	public void setCenter(Point centerPoint) {
		mCenterPoint = centerPoint;
		invalidate();
	}

	/**
	 * set chart's radius 设置饼状图半径
	 * 
	 * @param r
	 *            chart's radius 饼状图的半径
	 */
	public void setR(int r) {
		if (r <= 0) {
			mR = 1;
		} else {
			mR = r;
		}
		invalidate();
	}

	/**
	 * set wizard line's length 设置引导线的长度。斜着的和横着的是一样长的。
	 * 
	 * @param length
	 *            line's length 引导线的长度
	 */
	public void setWizardLineLength(int length) {
		if (length <= 0) {
			mWizardLineLength = 1;
		} else {
			mWizardLineLength = length;
		}
		invalidate();
	}

	/**
	 * actually create chartProp objects. 真正创建扇形属性的方法
	 * 
	 * @param chartsNum
	 *            charts' number 饼状图的个数
	 */
	private void createChartProp(int chartsNum) {
		for (int i = 0; i < chartsNum; i++) {
			ChartProp chartProp = new ChartProp(this);
			chartProp.setId(i);
			mChartProps.add(chartProp);
		}
	}

	/**
	 * get the chartProp when Action_UP happened 获取当抬起时，坐标所在的charProp
	 * 
	 * @param x
	 *            action_up's x up时的x坐标
	 * @param y
	 *            action_up's y up时的y坐标
	 * @return chartProp If equals null, means not in any charts!
	 *         如果返回值为null，说明不在任何的扇形内。
	 */
	private ChartProp getUpChartProp(float x, float y) {
		double angle = Math.atan2(y - mCenterPoint.y, x - mCenterPoint.x) * 180
				/ Math.PI;
		if (angle < 0) {
			angle = 360 + angle;
		}

		ChartProp chartPropPosible = getPosibleChartProp(angle);
		if (chartPropPosible != null && inChartZone(x, y)) {
			return chartPropPosible;
		}

		return null;
	}

	/**
	 * judge if the action X Y in the circle. 判断抬起时，坐标是否在圆内。
	 * 
	 * @param x
	 *            action_up's x up时的x坐标
	 * @param y
	 *            action_up's y up时的y坐标
	 * @return true means in circle. 返回值为true，表示在圆内。
	 */
	private boolean inChartZone(float x, float y) {
		float a2 = (x - mCenterPoint.x) * (x - mCenterPoint.x);
		float b2 = (y - mCenterPoint.y) * (y - mCenterPoint.y);
		float R2 = backR * backR;
		if (a2 + b2 <= R2) {
			return true;
		}
		return false;
	}

	/**
	 * judge if the action_up's angle is in one chartProp
	 * 根据抬起时的角度，获取可能的ChartProp
	 * 
	 * @param angle
	 *            the action_up's angle 抬起时的角度
	 * @return the posible chartProp 可能的charProp。因为还要判断是不是在圆内。
	 */
	private ChartProp getPosibleChartProp(double angle) {
		int size = mChartProps.size();
		for (int i = 0; i < size; i++) {
			ChartProp chartProp = mChartProps.get(i);
			if ((angle > chartProp.getStartAngle() && angle <= chartProp
					.getEndAngle())
					|| (angle + 360 > chartProp.getStartAngle() && angle + 360 <= chartProp
							.getEndAngle())) {
				return chartProp;
			}
		}
		return null;
	}

	public Bitmap parseBg(Bitmap bitmap) {
		float percent = (float) mR / ((float) (bgR / 2));
		Matrix matrix = new Matrix();
		matrix.reset();
		matrix.postScale(percent, percent);
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		String samepleText = "最长五个字";
		Paint paint = new Paint();

		paint.setTextSize(mFontSize);
		Rect samepleRect = new Rect();
		paint.getTextBounds(samepleText, 0, samepleText.length(), samepleRect);
		int maxLength = samepleRect.width() + marTextLength + apexLength;
		allLength = maxLength;
		int allHeight = samepleRect.height();
		Matrix matrix = new Matrix();
		int rectWidht = rectangleBitmap.getWidth();
		int rectHeight = rectangleBitmap.getHeight();
		float scaleWidth = (float) (allLength - apexLength) / (float) rectWidht;
		float scaleHeight = (float) (allHeight + marTextLength + marTextLength - 3)
				/ (float) rectHeight;
		matrix.reset();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap rectTempBitmap = Bitmap.createBitmap(rectangleBitmap, 0, 0,
				rectangleBitmap.getWidth(), rectangleBitmap.getHeight(),
				matrix, true);
		Matrix triMatrix = new Matrix();
		float triScalWidth = (float) apexLength
				/ (float) triangleBitmap.getWidth();
		float triScaleHeight = (float) (allHeight + marTextLength
				+ marTextLength - 3)
				/ (float) triangleBitmap.getHeight();
		triMatrix.reset();
		triMatrix.postScale(triScalWidth, triScaleHeight);
		Bitmap triTempBitmap = Bitmap.createBitmap(triangleBitmap, 0, 0,
				triangleBitmap.getWidth(), triangleBitmap.getHeight(),
				triMatrix, true);
		Bitmap rightRectTempBitmap = Bitmap.createBitmap(rightRectBitmap, 0, 0,
				rightRectBitmap.getWidth(), rightRectBitmap.getHeight(),
				matrix, true);
		Bitmap rightTriTempBitmap = Bitmap.createBitmap(rightTriBitmap, 0, 0,
				rightTriBitmap.getWidth(), rightTriBitmap.getHeight(),
				triMatrix, true);
		if ((windowWidth - maxLength - maxLength) <= windowHeight) {
			setR((windowWidth - maxLength - maxLength - 25) / 2);
		} else {
			setR(windowHeight / 2 - 25);
		}
		setWizardLineLength((windowWidth - maxLength - maxLength) / 3);
		Bitmap backBitmap = parseBg(bgBitmap);
		backR = backBitmap.getWidth() / 2;
		paint.setAntiAlias(mAa);
		float startAngle = mStartAngle;
		int size = mChartProps.size();
		RectF oval = new RectF(mCenterPoint.x - mR, mCenterPoint.y - mR,
				mCenterPoint.x + mR, mCenterPoint.y + mR);
		List<PointBean> pointList = parseData();
		if (windowWidth > windowHeight) {
			Matrix backMatrix = new Matrix();
			float backWdithSale = (float) (windowWidth + 2)
					/ (float) bkBitmap.getWidth();
			float backHeightScale = (float) windowHeight
					/ (float) bkBitmap.getHeight();
			backMatrix.reset();
			backMatrix.postScale(backWdithSale, backHeightScale);
			Bitmap tempBackBitmap = Bitmap
					.createBitmap(bkBitmap, 0, 0, bkBitmap.getWidth(),
							bkBitmap.getHeight(), backMatrix, true);
			canvas.drawBitmap(tempBackBitmap, -4, 0, paint);
		} else {
			Matrix backMatrix = new Matrix();
			float backWdithSale = (float) (windowWidth)
					/ (float) portraintBkBitmap.getWidth();
			float backHeightScale = (float) windowHeight
					/ (float) portraintBkBitmap.getHeight();
			backMatrix.reset();
			backMatrix.postScale(backWdithSale, backHeightScale);
			Bitmap tempBackBitmap = Bitmap.createBitmap(portraintBkBitmap, 0,
					0, portraintBkBitmap.getWidth(),
					portraintBkBitmap.getHeight(), backMatrix, true);
			canvas.drawBitmap(tempBackBitmap, 0, 0, paint);
		}
		for (int i = 0; i < size; i++) {
			PointBean pointBean = pointList.get(i);
			Point point1 = pointBean.getPoint1();
			Point point2 = pointBean.getPoint2();
			Point point3 = pointBean.getPoint3();
			ChartProp chartProp = mChartProps.get(i);
			String title = chartProp.getName();
			String value = chartProp.getValue();
			String showTarget = "";
			if (title.length() >= value.length()) {
				showTarget = "title";
			} else {
				showTarget = "value";
			}
			// drawArc
			paint.setColor(chartProp.getColor());
			float sweepAngle = chartProp.getSweepAngle();
			canvas.drawArc(oval, startAngle, sweepAngle, true, paint);

			// drawWizardLines -----splash line
			float wizardLineAngle = (float) ((startAngle + sweepAngle / 2)
					* Math.PI / 180);
			float deltaR = mR - mWizardLineLength / 2;

			double cosAngle = Math.cos(wizardLineAngle);
			int deltaXs = (int) (deltaR * cosAngle);

			paint.setTextSize(mFontSize);
			int textHeight = 0;
			int tempX = point3.x - apexLength - marTextLength - marLength;
			int valueSize = value.length();
			int titleSize = title.length();
			for (int m = 0; m < valueSize; m++) {
				Rect rectTemp = new Rect();
				paint.getTextBounds(value, 0, value.length(), rectTemp);
				textHeight = rectTemp.height();
				if (rectTemp.width() <= tempX) {
					break;
				}
				value = value.substring(0, valueSize - 1 - m);
			}
			for (int n = 0; n < titleSize; n++) {
				Rect rectTemp = new Rect();
				paint.getTextBounds(title, 0, title.length(), rectTemp);
				if (rectTemp.width() <= tempX) {
					break;
				}
				title = title.substring(0, titleSize - 1 - n);
			}
//			if (deltaXs <= 0) {
			if(i >=(float)size / (float)2){
				Paint paintText = new Paint();
				paintText.setStyle(Style.FILL);
				paintText.setAntiAlias(true);
				paintText.setColor(chartProp.getColor());
				paintText.setPathEffect(new CornerPathEffect(5));
				Path path = new Path();
				path.moveTo(point3.x, point3.y);
				path.lineTo(point3.x - apexLength, point3.y - textHeight / 2
						- marTextLength);
				path.lineTo(marLength, point3.y - textHeight / 2
						- marTextLength);
				path.lineTo(marLength, point3.y + textHeight / 2
						+ marTextLength);
				path.lineTo(point3.x - apexLength, point3.y + textHeight / 2
						+ marTextLength);
				path.lineTo(point3.x, point3.y);

				canvas.drawPath(path, paintText);

				canvas.drawText(value, marLength + marTextLength, point3.y
						+ textHeight + marTextLength + marTextLength + 3, paint);
				paint.setColor(Color.BLACK);
				canvas.drawText(title, marLength + marTextLength, point3.y
						+ textHeight / 2, paint);
				canvas.drawBitmap(rectTempBitmap, 0, point3.y - textHeight / 2
						- marTextLength, paint);
				canvas.drawBitmap(triTempBitmap, allLength - apexLength,
						point3.y - textHeight / 2 - marTextLength, paint);

			} else {
				Paint paintText = new Paint();
				paintText.setStyle(Style.FILL);
				paintText.setAntiAlias(true);
				paintText.setColor(chartProp.getColor());
				paintText.setPathEffect(new CornerPathEffect(5));
				Path path = new Path();
				path.moveTo(point3.x, point3.y);
				path.lineTo(point3.x + apexLength, point3.y - textHeight / 2
						- marTextLength);
				path.lineTo(windowWidth - marLength, point3.y - textHeight / 2
						- marTextLength);
				path.lineTo(windowWidth - marLength, point3.y + textHeight / 2
						+ marTextLength);
				path.lineTo(point3.x + apexLength, point3.y + textHeight / 2
						+ marTextLength);
				path.lineTo(point3.x, point3.y);

				canvas.drawPath(path, paintText);
				Rect tempRightRect = new Rect();
				paint.getTextBounds(value, 0, value.length(), tempRightRect);
				canvas.drawText(value,
						point3.x + allLength - tempRightRect.width()
								- marTextLength, point3.y + textHeight
								+ marTextLength + marTextLength + 3, paint);
				paint.setColor(Color.BLACK);
				paint.getTextBounds(title, 0, title.length(), tempRightRect);
				canvas.drawText(title,
						point3.x + allLength - tempRightRect.width()
								- marTextLength, point3.y + textHeight / 2,
						paint);
				canvas.drawBitmap(rightRectTempBitmap, windowWidth - allLength
						+ apexLength,
						point3.y - textHeight / 2 - marTextLength, paint);
				canvas.drawBitmap(rightTriTempBitmap, windowWidth - allLength,
						point3.y - textHeight / 2 - marTextLength, paint);
			}

			// add startAngle
			chartProp.setStartAngle(startAngle);
			startAngle += sweepAngle;
			chartProp.setEndAngle(startAngle);
		}
		for (int i = 0; i < size; i++) {
			ChartProp chartProp = mChartProps.get(i);
			float sweepAngle = chartProp.getSweepAngle();
			float wizardLineAngle = (float) ((startAngle + sweepAngle / 2)
					* Math.PI / 180);
			float deltaR = mR - mWizardLineLength / 2;

			double cosAngle = Math.cos(wizardLineAngle);
			int deltaXs = (int) (deltaR * cosAngle);
			PointBean pointBean = pointList.get(i);
			Point point1 = pointBean.getPoint1();
			Point point2 = pointBean.getPoint2();
			Point point3 = pointBean.getPoint3();
			Paint linePaint = new Paint();
			linePaint.setStyle(Style.STROKE);
			linePaint.setColor(chartProp.getColor());
			// linePaint.setColor(Color.BLACK);
			linePaint.setStrokeWidth(2);
			if (((point1.x - point2.x) * (point1.x - point2.x) + (point1.y - point2.y)
					* (point1.y - point2.y)) > ((point1.x - point3.x)
					* (point1.x - point3.x) + (point1.y - point3.y)
					* (point1.y - point3.y))) {
				canvas.drawLine(point1.x, point1.y, point3.x, point3.y,
						linePaint);
			} else {
				canvas.drawLine(point1.x, point1.y, point2.x, point2.y,
						linePaint);
			}
//			if (deltaXs <= 0) {
			if(i >=(float)size / (float)2){
				if (point3.x < point2.x) {
					canvas.drawLine(point3.x, point3.y, point2.x, point2.y,
							linePaint);
				}
			} else {
				if (point3.x > point2.x) {
					canvas.drawLine(point3.x, point3.y, point2.x, point2.y,
							linePaint);
				}
			}
			chartProp.setStartAngle(startAngle);
			startAngle += sweepAngle;
			chartProp.setEndAngle(startAngle);
		}
		float startX = mCenterPoint.x - backR;
		float startY = mCenterPoint.y - backR;
		canvas.drawBitmap(backBitmap, startX, startY, paint);
	}

	public List<PointBean> parseData() {

		float startAngle = mStartAngle;
		int size = mChartProps.size();
		int leftCount = 0;
		List<PointBean> pointList = new ArrayList<PointBean>();
		for (int i = 0; i < size; i++) {
			ChartProp chartProp = mChartProps.get(i);
			float sweepAngle = chartProp.getSweepAngle();
			float wizardLineAngle = (float) ((startAngle + sweepAngle / 2)
					* Math.PI / 180);
			float deltaR = mR - mWizardLineLength / 2;
			double cosAngle = Math.cos(wizardLineAngle);
			int deltaXs = (int) (deltaR * cosAngle);
			// Point lineSplashStart = new Point(mCenterPoint.x + deltaXs,
			// mCenterPoint.y + deltaYs);
			if (deltaXs <= 0) {
				leftCount++;
			}
			chartProp.setStartAngle(startAngle);
			startAngle += sweepAngle;
			chartProp.setEndAngle(startAngle);
		}
		int leftItemHeight = 0;
		int rightItemHeight = 0;
		if (size > 1) {
			if (size % 2 == 0) {
				leftCount = size / 2;
			} else {
				leftCount = (size - 1) / 2;
			}
		}
		if (leftCount != 0) {
			leftItemHeight = windowHeight / leftCount;
		}
		if (leftCount != size) {
			rightItemHeight = windowHeight / (size - leftCount);
		}
		int rightNum = 0;
		int leftNum = leftCount - 1;
		for (int i = 0; i < size; i++) {
			ChartProp chartProp = mChartProps.get(i);
			PointBean pointBean = new PointBean();
			String title = chartProp.getName();
			String value = chartProp.getValue();
			// int nameLen = 0;
			String showTarget = "";
			if (title.length() >= value.length()) {
				showTarget = "title";
			} else {
				showTarget = "value";
			}

			float sweepAngle = chartProp.getSweepAngle();

			// drawWizardLines -----splash line
			float wizardLineAngle = (float) ((startAngle + sweepAngle / 2)
					* Math.PI / 180);
			float deltaR = mR - mWizardLineLength / 2;
			double sinAngle = Math.sin(wizardLineAngle);
			double cosAngle = Math.cos(wizardLineAngle);
			int deltaXs = (int) (deltaR * cosAngle);
			int deltaYs = (int) (deltaR * sinAngle);
			Point lineSplashStart = new Point(mCenterPoint.x + deltaXs,
					mCenterPoint.y + deltaYs);
			pointBean.setPoint1(lineSplashStart);
			// if (deltaXs <= 0) {
			if (i >=(float)size / (float)2) {
					int point2Y = (int) ((leftNum + 0.4) * leftItemHeight);
					int point2X = 0;
					if (Math.abs(point2Y - lineSplashStart.y) > mWizardLineLength) {
						point2X = lineSplashStart.x;
					} else {
						point2X = (int) (lineSplashStart.x - Math
								.sqrt(mWizardLineLength * mWizardLineLength
										- (point2Y - lineSplashStart.y)
										* (point2Y - lineSplashStart.y)));
					}
					Point point2 = new Point(point2X, point2Y);
					pointBean.setPoint2(point2);
					Paint textPaint = new Paint();
					textPaint.setTextSize(mFontSize);
					Rect rectValue = new Rect();
					textPaint.getTextBounds(value, 0, value.length(), rectValue);
					Rect rectTitle = new Rect();
					textPaint.getTextBounds(title, 0, title.length(), rectTitle);
					int textWidth = 0;
					if (rectTitle.width() >= rectValue.width()) {
						textWidth = rectTitle.width();
					} else {
						textWidth = rectValue.width();
					}
					float point3X = 0;
					if (inChartZone(allLength, point2Y)) {
						point3X = (float) (point2.x - (Math.sqrt(backR * backR
								- (point2.y - mCenterPoint.y)
								* (point2.y - mCenterPoint.y)) - Math.abs(point2.x
										- mCenterPoint.x)));
					} else {
						point3X = allLength;
					}
					pointBean.setPoint3(new Point((int) point3X, point2Y));
					leftNum--;
			} else {
				int point2Y = (int) ((rightNum + 0.4) * rightItemHeight);
				int point2X = 0;
				if (Math.abs(point2Y - lineSplashStart.y) >= mWizardLineLength) {
					point2X = lineSplashStart.x;
				} else {
					point2X = (int) (lineSplashStart.x + Math
							.sqrt(mWizardLineLength * mWizardLineLength
									- (point2Y - lineSplashStart.y)
									* (point2Y - lineSplashStart.y)));
				}
				pointBean.setPoint2(new Point(point2X, point2Y));
				Paint textPaint = new Paint();
				textPaint.setTextSize(mFontSize);
				Rect rectValue = new Rect();
				textPaint.getTextBounds(value, 0, value.length(), rectValue);
				Rect rectTitle = new Rect();
				textPaint.getTextBounds(title, 0, title.length(), rectTitle);
				int textWidth = 0;
				if (rectTitle.width() >= rectValue.width()) {
					textWidth = rectTitle.width();
				} else {
					textWidth = rectValue.width();
				}
				float point3X = 0;
				if (inChartZone(windowWidth - allLength, point2Y)) {
					point3X = (float) (point2X + (Math.sqrt(backR * backR
							- (point2Y - mCenterPoint.y)
							* (point2Y - mCenterPoint.y)) - Math.abs(point2X
							- mCenterPoint.x)));
				} else {
					point3X = windowWidth - allLength;
				}
				pointBean.setPoint3(new Point((int) point3X, point2Y));
				rightNum++;
			}
			chartProp.setStartAngle(startAngle);
			startAngle += sweepAngle;
			chartProp.setEndAngle(startAngle);
			pointList.add(pointBean);
		}
		return pointList;
	}
}
