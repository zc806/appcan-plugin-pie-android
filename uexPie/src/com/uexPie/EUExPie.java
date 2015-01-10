package com.uexPie;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;

import com.uexPie.bean.PieBean;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

public class EUExPie extends EUExBase {
	static String opID = "0";
	static final String functionName = "uexPie.loadData";
	static final String cbOpenFunName = "uexPie.cbOpen";
	private Activity mainActivity;
	private PieActivity pieContext;
	public static final String TAG = "uexPie";
	private View theview;

	private int startX = 0;
	private int startY = 0;
	public static int screenWidth = 0;
	public static int screenHeight = 0;

	public EUExPie(Context context, EBrowserView arg1) {
		super(context, arg1);
		this.mainActivity = (Activity) context;
	}

	@Override
	protected boolean clean() {
		close(null);
		return false;
	}

	public void open(String[] params) {
		if (pieContext != null) {
			return;
		}
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		opID = params[0];
		if (params[1].length() != 0) {
			startX = Integer.parseInt(params[1]);
		}
		if (params[2].length() != 0) {
			startY = Integer.parseInt(params[2]);
		}
		if (params[3].length() != 0) {
			screenWidth = Integer.parseInt(params[3]);
		}
		if (params[4].length() != 0) {
			screenHeight = Integer.parseInt(params[4]);
		}
		mainActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				LocalActivityManager mgr = ((ActivityGroup) mContext)
						.getLocalActivityManager();
				Intent intent = new Intent(mContext, PieActivity.class);
				Window window = mgr.startActivity(TAG, intent);
				pieContext = (PieActivity) window.getContext();
				if (0 == screenWidth || 0 == screenHeight) {
					Display display = pieContext.getWindowManager()
							.getDefaultDisplay();
					screenWidth = display.getWidth();
					screenHeight = display.getHeight();
				}
				View pieDecorView = window.getDecorView();
				theview = pieDecorView;
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						screenWidth, screenHeight);
				lp.leftMargin = startX;
				lp.topMargin = startY;
				addViewToCurrentWindow(pieDecorView, lp);
			}
		});

		loadData(opID);
	}

	public void loadData(String opID) {
		jsCallback(functionName, Integer.parseInt(opID), 0, 0);
		jsCallback(cbOpenFunName, Integer.parseInt(opID), 0, 0);
	}

	@SuppressWarnings("deprecation")
	public void close(String[] params) {
		if (null != pieContext) {
			mainActivity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					LocalActivityManager mgr = ((ActivityGroup) mContext)
							.getLocalActivityManager();
					destroy(((ActivityGroup) mContext), TAG);
					View mPieView = theview;
					removeViewFromCurrentWindow(mPieView);
				}
			});
			
			
			pieContext = null;

		}
	}

	public void setJsonData(String[] params) {
		try {
			JSONObject json = new JSONObject(params[0]);
			String jsonResult = json.getString("data");
			final List<PieBean> pieList = PieUtility.parseData(jsonResult);
			mainActivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					pieContext.setData(pieList, screenWidth, screenHeight);
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static boolean destroy(ActivityGroup activityGroup, String id) {
		final LocalActivityManager activityManager = activityGroup
				.getLocalActivityManager();
		if (activityManager != null) {
			activityManager.destroyActivity(id, false);
			try {
				final Field mActivitiesField = LocalActivityManager.class
						.getDeclaredField("mActivities");
				if (mActivitiesField != null) {
					mActivitiesField.setAccessible(true);
					@SuppressWarnings("unchecked")
					final Map<String, Object> mActivities = (Map<String, Object>) mActivitiesField
							.get(activityManager);
					if (mActivities != null) {
						mActivities.remove(id);
					}
					final Field mActivityArrayField = LocalActivityManager.class
							.getDeclaredField("mActivityArray");
					if (mActivityArrayField != null) {
						mActivityArrayField.setAccessible(true);
						@SuppressWarnings("unchecked")
						final ArrayList<Object> mActivityArray = (ArrayList<Object>) mActivityArrayField
								.get(activityManager);
						if (mActivityArray != null) {
							for (Object record : mActivityArray) {
								final Field idField = record.getClass()
										.getDeclaredField("id");
								if (idField != null) {
									idField.setAccessible(true);
									final String _id = (String) idField
											.get(record);
									if (id.equals(_id)) {
										mActivityArray.remove(record);
										break;
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
}
