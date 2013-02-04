package com.github.ironjan.photodrop.model;

import android.os.FileObserver;

import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class DirObserver {
	FileObserver mObserver;

	@Bean
	DirKeeper dirKeeper;

	private static final DirObserverCallback mDummyCallback = new DirObserverCallback() {

		@Override
		public void fileDeleted(String path) {/* dummy */
		}

		@Override
		public void fileChanged(String path) {/* dummy */
		}
	};

	DirObserverCallback mCallback = mDummyCallback;

	@AfterInject
	void initObserver() {
		final String absPath = dirKeeper.getExtFilesDir().getPath();

		mObserver = new FileObserver(absPath) {

			@Override
			public void onEvent(int event, String path) {
				switch (event) {
				case FileObserver.CLOSE_WRITE:
					mCallback.fileChanged(path);
					break;
				case FileObserver.DELETE:
					mCallback.fileDeleted(path);
					break;
				default:
					break;
				}
			}
		};
	}

	public void startWatching(){
		mObserver.startWatching();
	}
	
	public void removeCallback() {
		this.mCallback = mDummyCallback;
	}

	public void setCallback(DirObserverCallback callback) {
		this.mCallback = callback;
	}

}
