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

	@Override
	public String toString() {
		return String.format("Post [lat:%s, long:%s, comment:%s]", //$NON-NLS-1$
				metadata.latitude, metadata.longitude, metadata.comment);
	}

}
