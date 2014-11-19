package zq.whu.zhangshangwuda.ui.bus;

import org.json.JSONObject;

public class BusInfo {
	public int id;
	public int route_id;
	public double longitude;
	public double latitude;
	public String stop ;
	public int arrive_time;

	@Override
	public String toString() {
		String str = "id =   ;"+id
						+"route_id =   ;"+route_id
						+"longitude =   ;"+longitude
						+"latitude =   ;"+latitude
						+"stop =   ;"+stop
						+"arrive_time =   ;"+arrive_time;
		return str;
	}

	public BusInfo(JSONObject bus){
		this.id = bus.optInt("id", -1);
		this.route_id = bus.optInt("route_id", -1);
		this.arrive_time = bus.optInt("arrive_time", -1);
		this.longitude = bus.optDouble("longitude", -1);
		this.latitude = bus.optDouble("latitude", -1);
		this.stop = bus.optString("stop", "");
	}
	
}
