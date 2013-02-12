package com.github.ironjan.photodrop.persistence;

public class Post {
	public final PostMetadata metadata;
	public final String imageUri;

	Post(PostMetadata metadata, String imageUri) {
		super();
		this.metadata = metadata;
		this.imageUri = imageUri;
	}

}
