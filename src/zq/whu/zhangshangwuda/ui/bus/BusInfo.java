package zq.whu.zhangshangwuda.ui.bus;

import org.json.JSONObject;

public class BusInfo {
	public int id; // 车辆ID
	public int route_id; // 线路ID
	public double longitude; // 经度
	public double latitude; // 维度
	public String stop; // 最近站点
	public int arrive_time; // 到达终点的时间

	@Override
	public String toString() {
		String str = "id = " + id +" ; "
						+ "route_id =   " +route_id +" ; "
						+ "longitude =   " + longitude +" ; "
						+ "latitude =   " + latitude	+" ; "
						+ "stop =   " + stop +" ; "
						+ "arrive_time =   " + arrive_time+" ; ";
		return str;
	}

	public BusInfo(JSONObject bus) {
		this.id = bus.optInt("id", -1);
		this.route_id = bus.optInt("route_id", -1);
		this.arrive_time = bus.optInt("arrive_time", -1);
		this.longitude = bus.optDouble("longitude", -1);
		this.latitude = bus.optDouble("latitude", -1);
		this.stop = bus.optString("stop", "");
	}

}
