package com.uexPie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

import com.uexPie.bean.PieBean;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;

public class PieActivity extends Activity {
	ChartView mChartView;

	int fontSize[] = new int[] { 20, 20, 20, 20, 20 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	public void setData(List<PieBean> pieList,int screenWidth, int screenHeight){
		int centerX = screenWidth/2;
		int centerY = screenHeight/2;
		setContentView(EUExUtil.getResLayoutID("plugin_uexpie_view"));
		mChartView = (ChartView) this.findViewById(EUExUtil.getResIdID("plugin_uexpie_view_chartView"));
		mChartView.setAntiAlias(true);
		mChartView.setCenter(new Point(centerX, centerY));
		mChartView.setStartAngle(270);
		
		int size = pieList.size();
		float percentSum = 0;
		for(int i=0;i<size;i++){
			PieBean pieBean = pieList.get(i);
			percentSum += Float.parseFloat(pieBean.getValue());
		}
		List<PieBean> resultList = dataReset(pieList);
		ArrayList<ChartProp> acps = mChartView.createCharts(size,screenWidth,screenHeight);
		for (int i = 0; i < size; i++) {
			ChartProp chartProp = acps.get(i);
			PieBean pieBean = resultList.get(i);
			chartProp.setColor(PieUtility.parseColor(pieBean.getColor()));
			chartProp.setPercent(Float.parseFloat(pieBean.getValue())/percentSum);
			chartProp.setValue(pieBean.getSubTitle());
			chartProp.setName(pieBean.getTitle());
		}
	}
	public List<PieBean> dataReset(List<PieBean> list){
		if(null==list || list.size()==0){
			return null;
		}
		List<PieBean> tempList = list;
		Collections.sort(tempList, new SortByAge());
		List<PieBean> pieList = new ArrayList<PieBean>();
		List<PieBean> evenList = new ArrayList<PieBean>();
		for(int i=0;i<tempList.size();i++){
			if(i%2==0){
				pieList.add(tempList.get(i));
			}else{
				evenList.add(tempList.get(i));
			}
		}
		for(int m=evenList.size()-1;m>=0;m--){
			pieList.add(evenList.get(m));
		}
		return pieList;
	}
	class SortByAge implements Comparator {
		 @Override
		public int compare(Object o1, Object o2) {
		  PieBean s1 = (PieBean) o1;
		  PieBean s2 = (PieBean) o2;
		  if (Float.parseFloat(s1.getValue()) > Float.parseFloat(s2.getValue())){
			  return 1;
		  }else if(Float.parseFloat(s1.getValue()) == Float.parseFloat(s2.getValue())){
			  return 0;
		  }else{
			  return -1;
		  }
		 }
		}
}
