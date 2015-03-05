package zq.whu.zhangshangwuda.ui.bus.data;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.model.LatLng;

public class Data_station_gong_shi {
	public static List<LatLng> station = new ArrayList<LatLng>();
	public static List<String> stationName = new ArrayList<String>();
	
	public static void init() {
		if (station != null) station.clear();
		if (stationName != null) stationName.clear();
		
		stationName.add("工学部食堂、体育场");
		stationName.add("工学部网球场");
		stationName.add("工学部教学楼");
		stationName.add("世纪广场");
		stationName.add("桂园、计院");
		stationName.add("教五、教四、化院");
		stationName.add("校门");

		station.add(new LatLng(30.549275, 114.372755)); // 工学部食堂、体育场
		station.add(new LatLng(30.548287, 114.370329)); // 工学部网球场
		station.add(new LatLng(30.549111, 114.367976)); // 工学部教学楼
		station.add(new LatLng(30.548108, 114.366646)); // 世纪广场
		station.add(new LatLng(30.544885, 114.364935)); // 桂园、计院
		station.add(new LatLng(30.54277, 114.366974)); // 教五、教四、化院
		station.add(new LatLng(30.540803, 114.365982)); // 校门（向南）
	}
}
