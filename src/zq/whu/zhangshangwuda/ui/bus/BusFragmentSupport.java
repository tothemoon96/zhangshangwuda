package zq.whu.zhangshangwuda.ui.bus;

import com.baidu.mapapi.map.MapView;
import com.baidu.navi.location.am;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import zq.whu.zhangshangwuda.base.BaseSherlockFragment;
import zq.whu.zhangshangwuda.ui.R;

public class BusFragmentSupport extends BaseSherlockFragment{

	private View rootView;
	private MapView mapView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {	
		rootView = inflater.inflate(R.layout.bus,container,false);
		mapView = (MapView)rootView.findViewById(R.id.bmapView);
		return rootView;
	}

	@Override
	public void onDestroy() {
		mapView.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onPause() {
		mapView.onPause();
		super.onPause();
	}

	@Override
	public void onResume() {
		mapView.onResume();
		super.onResume();
	}

}
