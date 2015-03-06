package zq.whu.zhangshangwuda.ui.bus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import zq.whu.zhangshangwuda.base.BaseSherlockFragment;
import zq.whu.zhangshangwuda.ui.BuildConfig;
import zq.whu.zhangshangwuda.ui.R;
import zq.whu.zhangshangwuda.ui.bus.ChooseBusLineFragment.MyInterface;
import zq.whu.zhangshangwuda.ui.bus.data.Data_daxunhuan;
import zq.whu.zhangshangwuda.ui.bus.data.Data_gong_shitang;
import zq.whu.zhangshangwuda.ui.bus.data.Data_gong_xiaomen;
import zq.whu.zhangshangwuda.ui.bus.data.Data_station_daxunhuan;
import zq.whu.zhangshangwuda.ui.bus.data.Data_station_gong_men;
import zq.whu.zhangshangwuda.ui.bus.data.Data_station_gong_shi;
import zq.whu.zhangshangwuda.ui.bus.data.Data_station_wen_hu;
import zq.whu.zhangshangwuda.ui.bus.data.Data_station_wen_men;
import zq.whu.zhangshangwuda.ui.bus.data.Data_wenli_hubin;
import zq.whu.zhangshangwuda.ui.bus.data.Data_wenli_xiaomen;
import zq.whu.zhangshangwuda.views.toast.ToastUtil;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

public class BusFragmentSupport extends BaseSherlockFragment implements
		OnClickListener, MyInterface, OnMarkerClickListener, OnMapClickListener {

	private Fragment chooseFragment;
	private Timer timer = null;
	private FragmentManager manager;
	private InfoWindow mInfoWindow;

	private final String baseURL = "http://120.24.64.232:12334/";
	private final String routeUrl = baseURL + "bus/get_bus_info";
	private final String KEY_lineId = "lineId";
	private final static String CHOOSE_LINE_TAG = "choose_paper";
	
	private final static int NET_CONNECT_FAILL = 0x00ff0f01;
	private final static int BUS_UPDATE = 0x00ff0f02;
	private final static int BUS_STATUS_0 = 0x00ff0f03;
	private final static int interval = 5000;
	

	private int[] colorList = { 0xaaff0000, 0xaa00ff00, 0xaa0000ff, 0xaaffff00,
			0xaa00ffff };
	private int lineName[] = { R.string.gong_men, R.string.gong_shi,
			R.string.wen_men, R.string.wen_hu, R.string.daxunhuan };

	private Map<String, Marker> busMarkers = new HashMap<String, Marker>();
	private List<List<LatLng>> lineLoc = new ArrayList<List<LatLng>>();
	private List<List<LatLng>> stationLoc = new ArrayList<List<LatLng>>();
	private List<List<String>> stationName = new ArrayList<List<String>>();
	private List<LatLng> lineCentre = new ArrayList<LatLng>();

	static Activity mActivity;
	static BusFragmentSupport mFragment;

	private BaiduMap baiduMap;
	private MapView mapView;
	private View rootView;
	private LinearLayout menuBar;
	private ImageButton dropBtn;
	private Button changeLine;
	private Button stationRemind;
	private Button firstChoose;
	private TextView remindShow;
	private TextView busLineShow;
	private RelativeLayout mainBar;
	private RelativeLayout mainFace;

	private BitmapDescriptor finalPoint = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_final_point);
	private BitmapDescriptor busIcon = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_bus);
	private BitmapDescriptor stationNotReach = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_p_not_reach);

	private int lineId = -1;
	
	//////////////////////////DEBUG////////////////////////
	File file = new File(Environment.getExternalStorageDirectory(),"bus_debug.txt");
	//////////////////////////////////////////////////////////
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		manager = getChildFragmentManager();
		mActivity = getActivity();
		mFragment = this;
		
		getLineId();
		
		Data_daxunhuan.init();
		Data_gong_shitang.init();
		Data_gong_xiaomen.init();
		Data_wenli_hubin.init();
		Data_wenli_xiaomen.init();
		lineLoc.add(Data_gong_xiaomen.gong_xiaomen);
		lineLoc.add(Data_gong_shitang.gong_shitang);
		lineLoc.add(Data_wenli_xiaomen.wenli_xiaomen);
		lineLoc.add(Data_wenli_hubin.wenli_hubin);
		lineLoc.add(Data_daxunhuan.daxunhuan);
		
		Data_station_daxunhuan.init();
		Data_station_gong_men.init();
		Data_station_gong_shi.init();
		Data_station_wen_hu.init();
		Data_station_wen_men.init();
		stationLoc.add(Data_station_gong_men.station);
		stationLoc.add(Data_station_gong_shi.station);
		stationLoc.add(Data_station_wen_men.station);
		stationLoc.add(Data_station_wen_hu.station);
		stationLoc.add(Data_station_daxunhuan.station);
		stationName.add(Data_station_gong_men.stationName);
		stationName.add(Data_station_gong_shi.stationName);
		stationName.add(Data_station_wen_men.stationName);
		stationName.add(Data_station_wen_hu.stationName);
		stationName.add(Data_station_daxunhuan.stationName);
		
		lineCentre.add(ConstantPos.gongxuebu_centre);
		lineCentre.add(ConstantPos.gongxuebu_centre);
		lineCentre.add(ConstantPos.wenli_centre);
		lineCentre.add(ConstantPos.wenli_centre);
		lineCentre.add(ConstantPos.daxunhuan_centre);
	}

	private void getLineId() {
		SharedPreferences sp = mActivity.getSharedPreferences("busProperty", Activity.MODE_PRIVATE);
		lineId = sp.getInt(KEY_lineId, -1);
	}
	
	private void storeLineId() {
		SharedPreferences sp = mActivity.getSharedPreferences("busProperty", Activity.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(KEY_lineId, lineId);
		editor.commit();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.bus, container, false);
		initMap();
		initView();
		if (lineId != -1) {
			busMarkers.clear();
			baiduMap.clear();
			drawLine(lineId);
			mainBar.setVisibility(View.VISIBLE);
			firstChoose.setVisibility(View.GONE);
			if (timer != null)
				timer.cancel();
			timer = new Timer(true);
			timer.schedule(new GetBusPostion(getRouteIDFromLineId(lineId)), 0, interval);
		} else {
			baiduMap.clear();
			drawLine(0);
			drawLine(1);
			drawLine(2);
			drawLine(3);
			drawLine(4);
		}
		return rootView;
	}

	/**
	 * 初始化地图
	 */
	private void initMap() {
		mapView = (MapView) rootView.findViewById(R.id.bmapView);
		baiduMap = mapView.getMap();
		MapStatus status = new MapStatus.Builder(baiduMap.getMapStatus())
				.target(ConstantPos.daxunhuan_centre).zoom(16).build();
		MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(status);
		baiduMap.setMapStatus(update);

		baiduMap.setOnMarkerClickListener(this);
		baiduMap.setOnMapClickListener(this);

		mapView.getChildAt(2).setVisibility(View.GONE);// 隐藏缩放控件
		mapView.getChildAt(3).setVisibility(View.GONE); // 隐藏比例尺控件
		// mapView.removeViewAt(1); // 删除百度地图logo

	}

	/**
	 * 初始化各个控件
	 */
	private void initView() {
		mainBar = (RelativeLayout) rootView.findViewById(R.id.main_bar);
		mainBar.getBackground().setAlpha(0xDD);
		mainBar.setVisibility(View.GONE);

		menuBar = (LinearLayout) rootView.findViewById(R.id.menuBar);

		dropBtn = (ImageButton) rootView
				.findViewById(R.id.main_bar_drop_button);
		dropBtn.setOnClickListener(this);

		changeLine = (Button) rootView.findViewById(R.id.lineChange);
		changeLine.setOnClickListener(this);

		stationRemind = (Button) rootView.findViewById(R.id.stationRemind);
		stationRemind.setOnClickListener(this);

		remindShow = (TextView) rootView.findViewById(R.id.remindShow);

		mainFace = (RelativeLayout) rootView.findViewById(R.id.main_face);

		firstChoose = (Button) rootView.findViewById(R.id.firstChoose);
		firstChoose.setOnClickListener(this);

		busLineShow = (TextView) rootView.findViewById(R.id.bus_line_show);
	}

	/**
	 * 根据客户端的线路id返回route_Id用于向服务器查询
	 * 
	 * @param lineId
	 * @return RouteID
	 */
	private int getRouteIDFromLineId(int lineId) {
		switch (lineId) {
		case 0 | 1:	//工学部
			return 3;
		case 2 | 3:	//文理学部
			return 2;
		case 4:			//大循环
			return 1;
		default:
			return 2;
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {

			case NET_CONNECT_FAILL:// 网络问题
				if (BuildConfig.DEBUG) {
					System.out.println(R.string.Bus_Connect_Tip);
				}
				ToastUtil.showToast(mActivity, R.string.No_Intenert_Tip,
						Toast.LENGTH_LONG);
				break;

			case BUS_UPDATE:// 正常更新
				JSONArray busList = (JSONArray) msg.obj;
				busListUpdate(msg.arg1, busList);
				break;

			case BUS_STATUS_0:// 返回数据status为0
				if (BuildConfig.DEBUG) {
					System.out.println("BUS_STATUS_0");
				}
				ToastUtil.showToast(mActivity, R.string.Bus_Status_0_Tip,
						Toast.LENGTH_LONG);
				break;
			}
		}

		/**
		 * 更新routeId路线上的校车
		 * 
		 * @param routeId
		 * @param busList
		 */
		private void busListUpdate(int routeId, JSONArray busList) {
			if (busList == null)
				return;
			for (int i = 0; i < busList.length(); i++) {
				JSONObject bus = busList.optJSONObject(i);
				if (bus != null) {
					BusInfo busInfo = new BusInfo(bus);
					updateOneBus(routeId, busInfo);
				}
			}
		}

		private void updateOneBus(int routeId, BusInfo busInfo) {

			Log.i("BUSINFO", busInfo.latitude + ", " + busInfo.longitude);
			Marker marker = busMarkers.get("" + busInfo.id);
			if (marker == null) {
				OverlayOptions overlay = new MarkerOptions()
						.icon(busIcon)
						.position(
								new LatLng(busInfo.latitude, busInfo.longitude))
						.zIndex(9);
				marker = (Marker) baiduMap.addOverlay(overlay);
				busMarkers.put(busInfo.id + "", marker);
			} else {
				marker.setPosition(new LatLng(busInfo.latitude,
						busInfo.longitude));
			}

		}
	};

	private class GetBusPostion extends TimerTask {
		int routeId = 2;

		GetBusPostion(int routeId) {
			this.routeId = routeId;
		}

		@Override
		public void run() {
			boolean flag = false;// 是否连接成功的标志
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(routeUrl + "?route_id=" + routeId);
			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);
				if (httpResponse == null) {
					System.out.println("getBusPos no reponse");
				} else {
					int state = httpResponse.getStatusLine().getStatusCode();
					if (state == HttpStatus.SC_OK) {
						flag = true;
						HttpEntity httpEntity = httpResponse.getEntity();
						String responseStr = EntityUtils.toString(httpEntity);
						if (BuildConfig.DEBUG)
							Log.i("BUSINFO", responseStr);
						getBusPosList(routeId, responseStr);
					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				System.out.println(e);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(e);
			} catch (Exception e) {
				System.out.println(e);
			} finally {
				httpClient.getConnectionManager().shutdown();
				if (!flag) {
					Message message = new Message();
					message.what = NET_CONNECT_FAILL;
					handler.sendMessage(message);
				}
			}
		}


		private void getBusPosList(int routeId, String busStr) {
			try {
				JSONObject all = new JSONObject(busStr);
				int flag = all.getInt("status");
				if (flag == 0) {
					Message message = new Message();
					message.what = BUS_STATUS_0;
					handler.sendMessage(message);
					return;
				}
				JSONArray busList = all.optJSONArray("bus_info");

				Message message = new Message();
				message.what = BUS_UPDATE;
				message.arg1 = routeId;
				message.obj = (Object) busList;
				handler.sendMessage(message);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.main_bar_drop_button:
			if (menuBar.getVisibility() == View.GONE) {
				menuBar.setVisibility(View.VISIBLE);
				dropBtn.setImageResource(R.drawable.btn_down_1);
			} else {
				menuBar.setVisibility(View.GONE);
				dropBtn.setImageResource(R.drawable.btn_drop_1);
			}
			break;

		case R.id.lineChange:
			if (menuBar.getVisibility() == View.VISIBLE) {
				menuBar.setVisibility(View.GONE);
				dropBtn.setImageResource(R.drawable.btn_drop_1);
			}
			showChoosePaper();
			break;

		case R.id.stationRemind:
			if (menuBar.getVisibility() == View.VISIBLE) {
				menuBar.setVisibility(View.GONE);
				dropBtn.setImageResource(R.drawable.btn_drop_1);
			}
			break;

		case R.id.firstChoose:
			showChoosePaper();
			firstChoose.setVisibility(View.GONE);
			mainBar.setVisibility(View.VISIBLE);
			break;
		}
	}

	private void showChoosePaper() {
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

		if (chooseFragment == null) {
			chooseFragment = manager.findFragmentByTag(CHOOSE_LINE_TAG);
		}

		if (chooseFragment == null) {
			chooseFragment = new ChooseBusLineFragment();
		}

		transaction.add(R.id.chooseFL, chooseFragment, CHOOSE_LINE_TAG);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onLineItemSelected(int lineId) {

		this.lineId = lineId;
		busMarkers.clear();
		baiduMap.clear();
		drawLine(lineId);

		if (chooseFragment == null)
			chooseFragment = manager.findFragmentByTag(CHOOSE_LINE_TAG);
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.remove(chooseFragment);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
		transaction.commit();

		if (timer != null)
			timer.cancel();
		timer = new Timer(true);
		timer.schedule(new GetBusPostion(getRouteIDFromLineId(lineId)), 0, interval);
	}

	private void drawLine(int lineId) {

		busLineShow.setText(lineName[lineId]);

		MapStatus status = new MapStatus.Builder(baiduMap.getMapStatus())
									.target(lineCentre.get(lineId)).zoom(16).build();
		MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(status);
		baiduMap.setMapStatus(update);
		
		/*String str = "画线\n";
		str = str + lineLoc.get(0).toString();
		try {
			FileWriter fw = new FileWriter(file, true);
			fw.write(str);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		OverlayOptions way = new PolylineOptions().color(colorList[lineId])
											.points(lineLoc.get(lineId)).width(10);
		baiduMap.addOverlay(way);

		Bundle bs = new Bundle();
		bs.putString("station", stationName.get(lineId).get(0));
		OverlayOptions startPoint = new MarkerOptions().icon(finalPoint)
											.position(stationLoc.get(lineId).get(0)).zIndex(9).extraInfo(bs);
		baiduMap.addOverlay(startPoint);

		Bundle be = new Bundle();
		be.putString("station", stationName.get(lineId).get(stationName.size() - 1));
		OverlayOptions endPoint = new MarkerOptions().icon(finalPoint)
															.position(stationLoc.get(lineId).get(stationLoc.get(lineId).size() - 1))
															.zIndex(9)	.extraInfo(be);
		baiduMap.addOverlay(endPoint);

		for (int i = 1; i < stationLoc.get(lineId).size() - 1; i++) {
			Bundle b = new Bundle();
			b.putString("station", stationName.get(lineId).get(i));
			OverlayOptions oo = new MarkerOptions().icon(stationNotReach)
					.position(stationLoc.get(lineId).get(i)).zIndex(9).extraInfo(b);
			baiduMap.addOverlay(oo);
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		TextView info = new TextView(getActivity());
		info.setTextSize(18);
		info.setTextColor(0xffff7700);
		//info.setBackgroundColor(0xff000000);
		String infoStr = marker.getExtraInfo().getString("station");
		if (infoStr != null) {
			info.setText(infoStr);
			final LatLng ll = marker.getPosition();
			Point p = baiduMap.getProjection().toScreenLocation(ll);
			p.y -= 47;
			LatLng llInfo = baiduMap.getProjection().fromScreenLocation(p);
			OnInfoWindowClickListener listener = new OnInfoWindowClickListener() {
				public void onInfoWindowClick() {
					baiduMap.hideInfoWindow();
				}
			};
			mInfoWindow = new InfoWindow(info, llInfo, listener);
			baiduMap.showInfoWindow(mInfoWindow);
			return true;
		}
		return false;
	}

	@Override
	public void onDestroy() {
		mapView.onDestroy();
		if (timer != null)
			timer.cancel();
		super.onDestroy();
		finalPoint.recycle();
		busIcon.recycle();
		stationNotReach.recycle();
		storeLineId();
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

	@Override
	public void onMapClick(LatLng arg0) {
		baiduMap.hideInfoWindow();
	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		return false;
	}

}
