package com.aier.environment.activity.historyfile.view;


import com.aier.environment.entity.FileItem;

import java.util.Comparator;


public class YMComparator implements Comparator<FileItem> {

	@Override
	public int compare(FileItem o1, FileItem o2) {
		return o1.getDate().compareTo(o2.getDate());
	}

}
