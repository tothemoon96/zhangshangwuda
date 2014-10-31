package zq.whu.zhangshangwuda.ui.lessons;

import java.util.List;
import java.util.Map;

import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import zq.whu.zhangshangwuda.base.BaseThemeSwipeBackSherlockActivity;
import zq.whu.zhangshangwuda.db.LessonsDb;
import zq.whu.zhangshangwuda.ui.R;

public class LessonsCoursePhotoTidyActivity extends BaseThemeSwipeBackSherlockActivity{
	private ListView lessonsListView;
	private List<Map<String,String>> courseList;
	SimpleAdapter simpleAdapter;
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getWindow().setBackgroundDrawable(null);
		setContentView(R.layout.lessons_coursephototidy_listview);
		findViews();
		refreshData();
		init();
		setListener();
	}
	
	private void findViews(){
		lessonsListView=(ListView) findViewById(R.id.lessons_coursephototidy_listview);
	}
	
	private void refreshData(){
        courseList=LessonsDb.getInstance(this).getLocalLessonsListGroupByName();
	}
	
	private void init(){
		simpleAdapter=new SimpleAdapter(this,courseList,R.layout.lessons_coursephototidy_item,
				new String[]{"id","name"},new int[]{R.id.lessons_coursephototidy_id,
				R.id.lessons_coursephototidy_courseName});
		lessonsListView.setAdapter(simpleAdapter);
	}
	
	private void setListener(){
		lessonsListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				}
			});
	}
}
