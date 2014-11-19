package zq.whu.zhangshangwuda.ui.bus;

import java.io.IOException;
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
import zq.whu.zhangshangwuda.views.toast.ToastUtil;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

public class BusFragmentSupport extends BaseSherlockFragment implements
		OnClickListener,OnItemClickListener{

	private View rootView;
	private MapView mapView;
	private BaiduMap baiduMap;
	private final String baseURL = "http://115.29.17.73:12334/";
	private final String routeUrl = baseURL + "bus/get_bus_info";
	private Timer timer = null;
	private final int NET_FAILL = 0x00ff0f01;
	private final int BUS_UPDATE = 0x00ff0f02;
	private final int BUS_STATUS_0 = 0x00ff0f03;
	private String CHOOSE_LINE_TAG = "choose_paper";
	private LinearLayout dropBar;
	private ImageButton dropBtn;
	private Button changeLine;
	private Button stationAlarm;
	private TextView alarmTV;
	private int lineId;
	private ListView chooseList;
	private RelativeLayout mainBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.bus, container, false);
		
		initMap();
		initView();
		
		return rootView;
	}

	private void initMap() {
		mapView = (MapView) rootView.findViewById(R.id.bmapView);
		baiduMap = mapView.getMap();
		MapStatus status = new MapStatus.Builder(baiduMap.getMapStatus())
				.target(ConstantPos.jianhu).zoom(18).build();
		MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(status);
		baiduMap.setMapStatus(update);

		if (BuildConfig.DEBUG) {
			OverlayOptions circle = new CircleOptions()
					.center(ConstantPos.jianhu).radius(10)
					.stroke(new Stroke(3, 0xffff0000)).fillColor(0xffff0000);
			baiduMap.addOverlay(circle);
		}
	}

	private void initView() {

		mainBar = (RelativeLayout) rootView
				.findViewById(R.id.main_bar);
		mainBar.getBackground().setAlpha(0xDD);
		
		dropBar = (LinearLayout) rootView
				.findViewById(R.id.main_bar_drop_layout);

		dropBtn = (ImageButton) rootView
				.findViewById(R.id.main_bar_drop_button);
		dropBtn.setOnClickListener(this);
		
		changeLine = (Button) rootView.findViewById(R.id.change_line);
		changeLine.setOnClickListener(this);
		
		stationAlarm = (Button) rootView.findViewById(R.id.station_alarm);
		stationAlarm.setOnClickListener(this);

		alarmTV = (TextView) rootView.findViewById(R.id.alarm);

		chooseList = (ListView) rootView.findViewById(R.id.chooseFL);
		chooseList.setAdapter(new ChooseBusLineAdapter(getActivity()));
		chooseList.setItemChecked(0,true);
		chooseList.setOnItemClickListener(this);
	}

	private int getRouteFromLineId(int lineId){
		switch (lineId){
		case 0:return 2;
		case 1:return 4;
		case 2:return 6;
		default: return 2;
		}
	}
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			
			case NET_FAILL:
				if (BuildConfig.DEBUG) {
					System.out.println("NET_FAILL");
				}
				ToastUtil.showToast(getActivity(), R.string.No_Intenert_Tip,
						Toast.LENGTH_LONG);
				break;
			
			case BUS_UPDATE:
				JSONArray busList = (JSONArray) msg.obj;
				busListUpdate(msg.arg1, busList);
				break;
			
			case BUS_STATUS_0:
				if (BuildConfig.DEBUG) {
					System.out.println("BUS_STATUS_0");
				}
				ToastUtil.showToast(getActivity(), R.string.Bus_Status_0_Tip,
						Toast.LENGTH_LONG);
				break;
			}
		}

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
			if (BuildConfig.DEBUG)
				System.out.println("updateOneBus  " + busInfo.toString());
			OverlayOptions circle = new CircleOptions()
					.center(new LatLng(busInfo.latitude, busInfo.longitude))
					.radius(10).stroke(new Stroke(3, 0xffff0000))
					.fillColor(0xffff0000);
			baiduMap.addOverlay(circle);
		}

	};

	private class BusPostion extends TimerTask {
		int routeId = 2;
		Context context;

		BusPostion(Context context, int routeId) {
			this.routeId = routeId;
			this.context = context;
		}

		@Override
		public void run() {
			getBusPos();
		}

		private void getBusPos() {
			boolean flag = false;
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
							System.out.println("route id = " + routeId + " "
									+ responseStr);
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
					message.what = NET_FAILL;
					handler.sendMessage(message);
				}
			}
		}

		private void getBusPosList(int routeId, String busStr) {
			try {
				JSONObject all = new JSONObject(busStr);
				int flag = all.getInt("status");
				if (BuildConfig.DEBUG)
					System.out.println("routeId = " + routeId + " status = "
							+ flag);
				if (flag == 0) {
					Message message = new Message();
					message.what = BUS_STATUS_0;
					handler.sendMessage(message);
				} else {
					JSONArray busList = all.optJSONArray("bus_info");
					Message message = new Message();
					message.what = BUS_UPDATE;
					message.arg1 = routeId;
					message.obj = (Object) busList;
					handler.sendMessage(message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		mapView.onDestroy();
		timer.cancel();
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

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.main_bar_drop_button:

			if (BuildConfig.DEBUG)
				System.out.println("drop button is clicked");

			if (dropBar.getVisibility() == View.GONE) {
				dropBar.setVisibility(View.VISIBLE);
				dropBtn.setImageResource(R.drawable.btn_down_1);
			} else {
				dropBar.setVisibility(View.GONE);
				dropBtn.setImageResource(R.drawable.btn_drop_1);
			}
			break;

		case R.id.change_line:
			setChooseVisibility();
			break;

		case R.id.station_alarm:
			if (dropBar.getVisibility() == View.GONE) {
				dropBar.setVisibility(View.VISIBLE);
				dropBtn.setImageResource(R.drawable.btn_down_1);
			} else {
				dropBar.setVisibility(View.GONE);
				dropBtn.setImageResource(R.drawable.btn_drop_1);
			}
			break;
		}
	}

	private void setChooseVisibility() {
		mapView.setVisibility(View.GONE);
		alarmTV.setVisibility(View.GONE);
		mainBar.setVisibility(View.GONE);
		chooseList.setVisibility(View.VISIBLE);
	}
	
	private void setNormalVisibility() {
		mapView.setVisibility(View.VISIBLE);
		alarmTV.setVisibility(View.GONE);
		mainBar.setVisibility(View.VISIBLE);
		dropBtn.setImageResource(R.drawable.btn_drop_1);
		dropBar.setVisibility(View.GONE);
		chooseList.setVisibility(View.GONE);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		setNormalVisibility();
		if (timer != null){
			timer.cancel();
		} 
		timer.schedule(new BusPostion(getActivity(), getRouteFromLineId(position)),0,5000);
	}

}
