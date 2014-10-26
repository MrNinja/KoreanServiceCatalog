package gdg.nat.ksc.present.activity;

import gdg.nat.base.BaseActivity;
import gdg.nat.connection.IWebServiceReceiverListener;
import gdg.nat.connection.RequestParam;
import gdg.nat.connection.ResponseCode;
import gdg.nat.connection.ResponseParser;
import gdg.nat.ksc.R;
import gdg.nat.ksc.connection.request.CheckCateVersionRequest;
import gdg.nat.ksc.connection.request.GetCategoriesRequest;
import gdg.nat.ksc.data.Categories;
import gdg.nat.util.ObjectCache;
import gdg.nat.util.PreferenceUtil;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends BaseActivity implements
		IWebServiceReceiverListener{
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_splash);
		requestVersion();
	}
	
	private void requestVersion(){
		int version = PreferenceUtil.getInstance().getCateVersion();
		CheckCateVersionRequest request = new CheckCateVersionRequest(version);
		restartRequest(request);
	}
	
	private void requestCategory(){
		GetCategoriesRequest requestParam = new GetCategoriesRequest();
		restartRequest(requestParam);
	}
	
	private void directToHome(){
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	public void dismisAllDialog(){
	}
	
	@Override
	public void onRequest(RequestParam requestParam){
	}
	
	@Override
	public void onReceiver(RequestParam requestParam,
			ResponseParser responseParser){
		if(requestParam instanceof CheckCateVersionRequest){
			int code = responseParser.getCode();
			if(code == ResponseCode.SERVER_OUT_OF_DATE_API.getCode()
					|| ObjectCache.getInstance().getCategories() == null){
				requestCategory();
			}else if(code == ResponseCode.CLIENT_SUCCESS.getCode()){
				directToHome();
			}else{
				Context context = this;
				Builder builder = new Builder(context);
				builder.setTitle(context.getString(R.string.title_error));
				builder.setMessage(context.getString(R.string.error_unknow));
				builder.setPositiveButton(
						context.getString(R.string.error_btn_positive),
						new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog,
									int which){
								requestCategory();
							}
						});
				// Set error message
				if(code == ResponseCode.CLIENT_ERROR_NO_CONNECTION.getCode()){
					builder.setMessage(context
							.getString(R.string.error_no_connection));
				}
				builder.show();
			}
		}else if(requestParam instanceof GetCategoriesRequest){
			int code = responseParser.getCode();
			if(code != ResponseCode.CLIENT_SUCCESS.getCode()){
				Context context = this;
				Builder builder = new Builder(context);
				builder.setTitle(context.getString(R.string.title_error));
				builder.setMessage(context.getString(R.string.error_unknow));
				builder.setPositiveButton(
						context.getString(R.string.error_btn_positive),
						new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog,
									int which){
								requestCategory();
							}
						});
				// Set error message
				if(code == ResponseCode.CLIENT_ERROR_NO_CONNECTION.getCode()){
					builder.setMessage(context
							.getString(R.string.error_no_connection));
				}
				builder.show();
			}else{
				Categories categories = ObjectCache.getInstance()
						.getCategories();
				if(categories == null
						|| categories.getListCategories().size() == 0){
					requestCategory();
				}
				directToHome();
			}
		}
	}
}