package baidupcs.response.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import baidupcs.response.file.CreateFileResponse;
import baidupcs.response.file.ListOrSearchResponse;
import baidupcs.response.file.MkdirResponse;

/**
 * 文件或目录被创建后返回的元信息，或list/search方法返回的元信息。
 * 
 * @author blove
 */
public class FileMetaWithExtra extends FileMeta {
	private String md5;

	public static FileMetaWithExtra fromResponse(CreateFileResponse res) {
		FileMetaWithExtra creation = new FileMetaWithExtra();
		creation.fsID = res.getFs_id();
		creation.path = res.getPath();
		creation.ctime = res.getCtime();
		creation.mtime = res.getMtime();
		creation.size = res.getSize();
		creation.md5 = res.getMd5();
		creation.isDir = 0;
		return creation;
	}

	public static FileMetaWithExtra fromResponse(MkdirResponse res) {
		FileMetaWithExtra creation = new FileMetaWithExtra();
		creation.fsID = res.getFs_id();
		creation.path = res.getPath();
		creation.ctime = res.getCtime();
		creation.mtime = res.getMtime();
		creation.isDir = 1;
		return creation;
	}

	public static List<FileMetaWithExtra> fromResponse(ListOrSearchResponse res) {
		List<ListOrSearchResponse.FileInfo> resList = res.getList();
		if (resList == null || resList.isEmpty())
			return Collections.emptyList();

		List<FileMetaWithExtra> infos = new ArrayList<>(resList.size());
		for (ListOrSearchResponse.FileInfo resInfo : resList) {
			FileMetaWithExtra info = new FileMetaWithExtra();
			info.fsID = resInfo.getFs_id();
			info.path = resInfo.getPath();
			info.ctime = resInfo.getCtime();
			info.mtime = resInfo.getMtime();
			info.size = resInfo.getSize();
			info.md5 = resInfo.getMd5();
			info.isDir = resInfo.getIsdir();
			infos.add(info);
		}

		return infos;
	}

	/**
	 * 文件的md5签名。
	 * 
	 * @return
	 */
	public String getMd5() {
		return md5;
	}

	@Override
	public String toString() {
		return "FileInfo [\n\tfs_id=" + fsID + "\n\tpath=" + path + "\n\tctime=" + ctime + "\n\tmtime=" + mtime
				+ "\n\tsize=" + size + "\n\tmd5=" + md5 + "\n\tisdir=" + isDir + "\n]";
	}

}
