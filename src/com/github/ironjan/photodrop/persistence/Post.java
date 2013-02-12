package com.github.ironjan.photodrop.persistence;



public class Post {
	public final PostMetadata metadata;
	public final String imagePath;

	Post(PostMetadata metadata, String photoPath) {
		super();
		this.metadata = metadata;		
		this.imagePath = photoPath;
	}

}
