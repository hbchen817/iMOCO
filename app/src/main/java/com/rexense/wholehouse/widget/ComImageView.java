package com.rexense.wholehouse.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.utility.Network;

import java.io.IOException;

/**
 * Creator: xieshaobing
 * creat time:  14:16
 * Description: 图片控件
 */
public class ComImageView extends AppCompatImageView {
	private Thread mDownloadImage = null;
	private Handler mImageHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if(Constant.MSG_DOWNLOADIMAGE == msg.what){
				Bitmap bitmap = (Bitmap) msg.obj;
				setImageBitmap(bitmap);
				if(mDownloadImage != null) {
					if(!mDownloadImage.isInterrupted()) {
						mDownloadImage.interrupt();
					}
					mDownloadImage = null;
				}
			}
			return false;
		}
	});

	public ComImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ComImageView(Context context) {
		super(context);
	}

	public ComImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	//设置网络图片
	public void setImageURL(String url) {
		String mURL = url;
		//开启一个线程用于联网下载图片
		this.mDownloadImage = new Thread() {
			@Override
			public void run() {
				Bitmap bitmap = null;
				try {
					bitmap = Network.getHttpBitmap(mURL);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(bitmap != null){
					Message msg = new Message();
					msg.what = Constant.MSG_DOWNLOADIMAGE;
					msg.obj = bitmap;
					mImageHandler.sendMessage(msg);
				}
				this.interrupt();
			}
		};
		this.mDownloadImage.start();
	}
}
