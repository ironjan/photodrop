package com.github.ironjan.photodrop.model;

public interface DirObserverCallback {
	public void fileChanged(String path);

	public void fileDeleted(String path);
}
