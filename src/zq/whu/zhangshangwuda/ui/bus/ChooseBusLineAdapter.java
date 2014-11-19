package zq.whu.zhangshangwuda.ui.bus;

import zq.whu.zhangshangwuda.ui.BuildConfig;
import zq.whu.zhangshangwuda.ui.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChooseBusLineAdapter extends BaseAdapter  {
	
	private int lineName[] = { R.string.gong, 	R.string.wen,  R.string.daxunhuan };
	private int lineDetail[] = { R.string.detail_gong,	R.string.detail_wen, R.string.detail_daxunhuan };
	private LayoutInflater mInflater;
	private int chooseNumber = 0;

	public ChooseBusLineAdapter(Context context) {
		super();
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return lineName.length;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		TextView mline;
		TextView mDetail;
		ImageView drop;
		LinearLayout lineLayout;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.choose_item, null);
			holder.mline = (TextView) convertView
					.findViewById(R.id.choose_busline_name);
			holder.mDetail = (TextView) convertView
					.findViewById(R.id.choose_busline_detail);
			holder.drop = (ImageView) convertView
					.findViewById(R.id.choose_dropBtn);
			holder.lineLayout = (LinearLayout)convertView.findViewById(R.id.choose_line);
					convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mline.setText(lineName[position]);
		holder.mDetail.setText(lineDetail[position]);
		holder.drop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (BuildConfig.DEBUG)
					System.out.println("choose paper drop button is clicked");
				if (holder.mDetail.getVisibility() == View.GONE) {
					holder.mDetail.setVisibility(View.VISIBLE);
					holder.drop.setImageResource(R.drawable.btn_drop_1);
				} else {
					holder.mDetail.setVisibility(View.GONE);
					holder.drop.setImageResource(R.drawable.btn_down_1);
				}
			}
		});
		return convertView;
	}
}
