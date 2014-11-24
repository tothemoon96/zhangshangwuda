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

public class ChooseFragment extends ListFragment {

	ChooseBusLineAdapter adapter;
	ItemSelected mSelected;

	public interface ItemSelected {
		void onLineItemSelected(int position);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new ChooseBusLineAdapter(getActivity());
		setListAdapter(adapter);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.choose_paper, container, false);
	}

	@Override
	public void onAttach(Activity activity) {
		if (BuildConfig.DEBUG)
			System.out.println("onAttach");
		try {
			mSelected = (ItemSelected) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement onItemSelected ");
		}
		super.onAttach(activity);
	}

	@Override
	public void onResume() {
		super.onResume();
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (BuildConfig.DEBUG)
			System.out.println("onListItemClick");
		mSelected.onLineItemSelected(position);
		super.onListItemClick(l, v, position, id);
	}

}
