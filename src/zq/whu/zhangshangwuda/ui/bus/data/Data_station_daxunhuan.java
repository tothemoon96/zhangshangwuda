package zq.whu.zhangshangwuda.ui.bus.data;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.model.LatLng;

public class Data_station_daxunhuan {
	public static List<LatLng> station = new ArrayList<LatLng>();
	public static List<String> stationName = new ArrayList<String>();
	
	public static void init() {
		stationName.add("信息学部二食堂");
		stationName.add("信息学部青楼");
		stationName.add("武大附中");
		stationName.add("校医院、图书馆");
		stationName.add("梅园");
		stationName.add("枫园");
		stationName.add("经管、外院、法院");
		stationName.add("湖滨");
		stationName.add("工学部网球场");
		stationName.add("世纪广场");
		stationName.add("桂园、计院");
		stationName.add("教五、教四、化院");
		stationName.add("校门");
		stationName.add("信息学部教工楼");
		stationName.add("信息学部学生寝室");
		
		station.add(new LatLng(30.533128, 114.364904)); // 信息学部二食堂
		station.add(new LatLng(30.533132, 114.368223)); // 信息学部青楼
		station.add(new LatLng(30.536938, 114.36931)); // 武大附中
		station.add(new LatLng(30.539753, 114.370033)); // 校医院、图书馆
		station.add(new LatLng(30.539753, 114.370033)); // 梅园
		station.add(new LatLng(30.543116, 114.378832)); // 枫园
		station.add(new LatLng(30.545379, 114.378513)); // 经管、外院、法院
		station.add(new LatLng(30.547646, 114.377202)); // 湖滨
		station.add(new LatLng(30.548287, 114.370329)); // 工学部网球场
		station.add(new LatLng(30.548108, 114.366646)); // 世纪广场
		station.add(new LatLng(30.544885, 114.364935)); // 桂园、计院
		station.add(new LatLng(30.54277, 114.366974)); // 教五、教四、化院
		station.add(new LatLng(30.540803, 114.365982)); // 校门（向南）
		station.add(new LatLng(30.537697, 114.363772)); // 信息学部教工楼
		station.add(new LatLng(30.534886, 114.364176)); // 信息学部学生寝室
		
		
	}
}
