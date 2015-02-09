package com.uexPie;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.uexPie.bean.PieBean;

public class EUExPie extends EUExBase {
	static String opID = "0";
	static final String functionName = "uexPie.loadData";
	static final String cbOpenFunName = "uexPie.cbOpen";
	private PieActivity pieContext;
	public static final String TAG = "uexPie";

	private int startX = 0;
	private int startY = 0;
	public static int screenWidth = 0;
	public static int screenHeight = 0;
	
	private Map<String, View> map_activity;

	public EUExPie(Context context, EBrowserView arg1) {
		super(context, arg1);
		map_activity = new HashMap<String, View>();
	}

	@Override
	protected boolean clean() {
		close(null);
		return false;
	}

	public void open(String[] params) {
		opID = params[0];
		if(map_activity.containsKey(opID)) {
			return;
		}
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
		((Activity)mContext).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				LocalActivityManager mgr = ((ActivityGroup) mContext)
						.getLocalActivityManager();
				Intent intent = new Intent(mContext, PieActivity.class);
				Window window = mgr.startActivity(TAG + opID, intent);
				pieContext = (PieActivity) window.getContext();
				if (0 == screenWidth || 0 == screenHeight) {
					Display display = pieContext.getWindowManager()
							.getDefaultDisplay();
					screenWidth = display.getWidth();
					screenHeight = display.getHeight();
				}
				View pieDecorView = window.getDecorView();
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						screenWidth, screenHeight);
				lp.leftMargin = startX;
				lp.topMargin = startY;
				addView2CurrentWindow(pieDecorView, lp);
				map_activity.put(opID, pieDecorView);
			}
		});

		loadData(opID);
	}

	private void addView2CurrentWindow(View child, RelativeLayout.LayoutParams parms) {
		int l = (int) (parms.leftMargin);
		int t = (int) (parms.topMargin);
		int w = parms.width;
		int h = parms.height;
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
		lp.gravity = Gravity.NO_GRAVITY;
		lp.leftMargin = l;
		lp.topMargin = t;
		adptLayoutParams(parms, lp);
		mBrwView.addViewToCurrentWindow(child, lp);
	}
	
	public void loadData(String opID) {
		jsCallback(functionName, Integer.parseInt(opID), 0, 0);
		jsCallback(cbOpenFunName, Integer.parseInt(opID), 0, 0);
	}

	public void close(String[] params) {
		((Activity)mContext).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (!map_activity.isEmpty()) {
					Set<Entry<String,View>> entrySet = map_activity.entrySet();
					Iterator<Entry<String, View>> iterator = entrySet.iterator();
					while (iterator.hasNext()) {
						Entry<String, View> entry = iterator.next();
						String activityId = entry.getKey();
						View view = entry.getValue();
						destroy(((ActivityGroup) mContext), TAG+activityId);
						removeViewFromCurrentWindow(view);
					}
					map_activity.clear();
				}
			}
		});
	}

	public void setJsonData(String[] params) {
		try {
			JSONObject json = new JSONObject(params[0]);
			String jsonResult = json.getString("data");
			final List<PieBean> pieList = PieUtility.parseData(jsonResult);
			((Activity)mContext).runOnUiThread(new Runnable() {

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
