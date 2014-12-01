package zq.whu.zhangshangwuda.ui.bus;

import zq.whu.zhangshangwuda.ui.BuildConfig;
import zq.whu.zhangshangwuda.ui.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ChooseBusLineFragment extends ListFragment {

	ChooseBusLineAdapter adapter;
	MyInterface mSelected;

	public interface MyInterface {
		void onLineItemSelected(int position);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		//getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		adapter = new ChooseBusLineAdapter(inflater);
		setListAdapter(adapter);
		
		return inflater.inflate(R.layout.choose_paper, container, false);
	}

	@Override
	public void onAttach(Activity activity) {
		try {
			mSelected = (MyInterface) BusFragmentSupport.mFragment;// 实例化mSelected
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement onItemSelected ");
		}
		super.onAttach(activity);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		mSelected.onLineItemSelected(position);
		super.onListItemClick(l, v, position, id);
	}

}
