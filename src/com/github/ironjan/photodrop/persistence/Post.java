package com.github.ironjan.photodrop.persistence;

import com.dropbox.sync.android.DbxPath;



public class Post {
	public final PostMetadata metadata;
	public final DbxPath imagePath;

	Post(PostMetadata metadata, DbxPath photo) {
		super();
		this.metadata = metadata;		
		this.imagePath = photo;
	}

}
