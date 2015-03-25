package baidupcs.api;

import static baidupcs.service.BaiduPcsService.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;
import baidupcs.auth.AccessToken;
import baidupcs.error.api.BaiduPcsException;
import baidupcs.error.api.ErrorParseHandler;
import baidupcs.request.api.OnDup;
import baidupcs.request.api.Order;
import baidupcs.request.api.OrderBy;
import baidupcs.response.api.FileMetaWithExtra;
import baidupcs.response.api.Quota;
import baidupcs.service.BaiduPcsService;

/**
 * 封装的百度个人云存储空间，提供比{@link BaiduPcsService}、{@link BaiduPcsCService}、
 * {@link BaiduPcsDService}更方便的接口。
 * 
 * @author blove
 */
public class BaiduPcs {
	private final String accessToken;
	private final String pathPrefix;

	private BaiduPcsService pcsService;

	private final LogLevel logLevel;
	
	public BaiduPcs(AccessToken ac) {
	    this(ac.getToken(), ac.getAppName(), LogLevel.NONE);
	}

	/**
	 * 新建一个实例，不输出日志。
	 * 
	 * @param accessToken
	 *             百度的开发者准入标识。
	 * @param appName
	 *             应用名称。用于根目录的路径中。
	 */
	public BaiduPcs(String accessToken, String appName) {
		this(accessToken, appName, LogLevel.NONE);
	}
	
	/**
     * 获取当前用户空间配额信息。
     * 
     * @return Quota
     * @throws BaiduPcsException
     * @throws IOException
     *              网络错误
     */
    public Quota quota() throws BaiduPcsException, IOException {
        try {
            return Quota.fromResponse(pcsService.quotaInfo(METHOD_INFO, accessToken));
        } catch (IOException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

	/**
	 * 新建一个实例。
	 * 
	 * @param accessToken
	 *             百度的开发者准入标识。
	 * @param appName
	 *             应用名称。用于根目录的路径中。
	 * @param logLevel
	 *             日志等级。
	 */
	public BaiduPcs(String accessToken, String appName, LogLevel logLevel) {
		this.accessToken = accessToken;
		this.pathPrefix = "/apps/" + appName;

		if(logLevel == null) logLevel = LogLevel.NONE;
		
		ErrorHandler errorHandler = new ErrorParseHandler();
		pcsService = new RestAdapter.Builder().setLogLevel(logLevel).setEndpoint(BaiduPcsService.SERVER)
				.setErrorHandler(errorHandler).build().create(BaiduPcsService.class);

		this.logLevel = logLevel;
	}
	
	/**
     * 上传单个文件。<br>
     * 百度PCS服务目前支持最大2G的单个文件上传。<br>
     * 如需支持超大文件（>2G）的断点续传，请参考下面的“分片文件上传”方法。<br>
     * 遇到同名文件,抛出异常
     * 
     * @param path
     *             上传后的文件路径。此路径是以应用文件夹为根目录的路径。
     * @param bytes
     *             文件内容
     * @return Creation
     * @throws BaiduPcsException
     * @throws IOException
     *              网络错误
     */
    public FileMetaWithExtra upload(String path, byte[] bytes) throws BaiduPcsException, IOException {
        return upload(path, new ByteArrayInputStream(bytes), bytes.length, OnDup.EXCEPTION);
    }

	/**
	 * 上传单个文件。<br>
	 * 百度PCS服务目前支持最大2G的单个文件上传。<br>
	 * 如需支持超大文件（>2G）的断点续传，请参考下面的“分片文件上传”方法。
	 * 
	 * @param path
	 *             上传后的文件路径。此路径是以应用文件夹为根目录的路径。
	 * @param bytes
	 *             文件内容
	 * @param ondup
	 *             文件已存在的处理方式。默认为抛出异常。
	 * @return Creation
	 * @throws BaiduPcsException
	 * @throws IOException
	 *              网络错误
	 */
	public FileMetaWithExtra upload(String path, byte[] bytes, OnDup ondup) throws BaiduPcsException, IOException {
		return upload(path, new ByteArrayInputStream(bytes), bytes.length, ondup);
	}

	/**
	 * 上传单个文件。<br>
	 * 百度PCS服务目前支持最大2G的单个文件上传。<br>
	 * 如需支持超大文件（>2G）的断点续传，请参考下面的“分片文件上传”方法。
	 * 
	 * @param path
	 *             上传后的文件路径。此路径是以应用文件夹为根目录的路径。
	 * @param in
	 *             文件内容输入流
	 * @param size
	 *             文件内容的长度
	 * @param ondup
	 *             文件已存在的处理方式。默认为抛出异常。
	 * @return Creation
	 * @throws BaiduPcsException
	 * @throws IOException
	 *              网络错误
	 */
	public FileMetaWithExtra upload(String path, final InputStream in, final long size, OnDup ondup)
			throws BaiduPcsException, IOException {
		try {
			if (ondup == null)
				ondup = OnDup.EXCEPTION;

			TypedOutput out = new TypedOutput() {

				@Override
				public void writeTo(OutputStream out) throws IOException {
					byte[] buf = new byte[1024 * 8];
					int n;
					while ((n = in.read(buf)) >= 0) {
						out.write(buf, 0, n);
					}
				}

				@Override
				public String mimeType() {
					return "application/octet-stream";
				}

				@Override
				public long length() {
					return size;
				}

				@Override
				public String fileName() {
					return "file";
				}
			};
			return FileMetaWithExtra.fromResponse(pcsService.upload(METHOD_UPLOAD, accessToken, realPath(path),
					out, ondup.getRestParam()));
		} catch (IOException | Error e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 下载单个文件。
	 * 
	 * @param path
	 *             下载文件路径。此路径是以应用文件夹为根目录的路径。
	 * @return TypedInput
	 * @throws BaiduPcsException
	 * @throws IOException
	 *              网络错误
	 */
	public TypedInput download(String path) throws BaiduPcsException, IOException {
		return download(path, -1, -1);
	}
	
	/**
	 * 下载单个文件的指定部分。
	 * 
	 * @param path
	 *             下载文件路径。此路径是以应用文件夹为根目录的路径。
	 * @param firstBytePos
	 *             下载部分第一个字节的位置，索引从0开始。如为负数则下载整个文件（此时lastBytePos无效）。
	 * @param lastBytePos
	 *             下载部分最后一个字节的位置，索引从0开始。如为负数或超过文件末尾，则默认为文件末尾位置。
	 * @return TypedInput
	 * @throws BaiduPcsException
	 * @throws IOException
	 *              网络错误
	 */
	public TypedInput download(String path, long firstBytePos, long lastBytePos) throws BaiduPcsException, IOException {
		try {
			String range = null;
			if (firstBytePos >= 0) {
				if (lastBytePos >= 0 && lastBytePos < firstBytePos)
					throw new IllegalArgumentException("lastBytePos cannot be smaller than firstBytePos ("
							+ lastBytePos + ">" + firstBytePos + ")");
				range = "bytes=" + firstBytePos + "-" + (lastBytePos >= 0 ? lastBytePos : "");
				if (logLevel == LogLevel.BASIC)
					System.out.println("Range:" + range);
			}
			return pcsService.download(METHOD_DOWNLOAD, accessToken, realPath(path), range).getBody();
		} catch (IOException | Error e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * 创建一个目录。
	 * 
	 * @param path
	 *             需要创建的目录路径。此路径是以应用文件夹为根目录的路径。
	 * @return Creation
	 * @throws BaiduPcsException
	 * @throws IOException
	 *              网络错误
	 */
	public FileMetaWithExtra mkdir(String path) throws BaiduPcsException, IOException {
		try {
			return FileMetaWithExtra.fromResponse(pcsService.mkdir(METHOD_MKDIR, accessToken, realPath(path)));
		} catch (IOException | Error e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取指定目录下的所有文件或目录列表。按照文件类型降序排列。<br>
	 * 如果path是普通文件，会返回空列表。
	 * 
	 * @param path
	 *             需要list的目录路径。此路径是以应用文件夹为根目录的路径。
	 * @return FileInfo列表
	 * @throws BaiduPcsException
	 * @throws IOException
	 *              网络错误
	 */
	public List<FileMetaWithExtra> list(String path) throws BaiduPcsException, IOException {
		return list(path, null, null, -1, -1);
	}

	/**
	 * 获取指定目录下的所有文件或目录列表。
	 * 
	 * @param path
	 *             需要list的目录路径。此路径是以应用文件夹为根目录的路径。
	 * @param by
	 *             排序字段。如果为null，则默认为按照文件类型排序。
	 * @param order
	 *             排序顺序。如果为null，则默认为降序排序。
	 * @return FileInfo列表
	 * @throws BaiduPcsException
	 * @throws IOException
	 *              网络错误
	 */
	public List<FileMetaWithExtra> list(String path, OrderBy by, Order order) throws BaiduPcsException, IOException {
		return list(path, by, order, -1, -1);
	}

	/**
	 * 获取指定目录下的文件或目录列表。
	 * 
	 * @param path
	 *             需要list的目录路径。此路径是以应用文件夹为根目录的路径。
	 * @param by
	 *             排序字段。如果为null，则默认为按照文件类型排序。
	 * @param order
	 *             排序顺序。如果为null，则默认为降序排序。
	 * @param startIndex
	 *             返回条目的起始索引，包含。如果为负数或endIndex为负数，则默认返回所有条目。
	 * @param endIndex
	 *             返回条目的结束索引，不包含。如果为负数或startIndex为负数，则默认返回所有条目。
	 * @return FileInfo列表
	 * @throws BaiduPcsException
	 * @throws IOException
	 *              网络错误
	 */
	public List<FileMetaWithExtra> list(String path, OrderBy by, Order order, int startIndex, int endIndex)
			throws BaiduPcsException, IOException {
		try {
			if (by == null)
				by = OrderBy.DEFAULT;
			if (order == null)
				order = Order.DEFAULT;
			return FileMetaWithExtra.fromResponse(pcsService.list(METHOD_LIST, accessToken, realPath(path),
					by.getRestParam(), order.getRestParam(), (startIndex >= 0 && endIndex >= 0) ? (startIndex
							+ "-" + endIndex) : null));
		} catch (IOException | Error e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
     * 删除单个文件/目录。<br>
     * 注意：<br>
     * <li>文件/目录删除后默认临时存放在回收站内，删除文件或目录的临时存放不占用用户的空间配额；<br> <li>
     * 存放有效期为10天，10天内可还原回原路径下，10天后则永久删除。
     * 
     * @param path
     *             需要删除的文件或者目录路径。此路径是以应用文件夹为根目录的路径。
     * @throws BaiduPcsException
     * @throws IOException
     *              网络错误
     */
    public void delete(String path) throws BaiduPcsException, IOException {
        try {
            pcsService.delete(METHOD_DELETE, accessToken, realPath(path));
        } catch (IOException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

	private String realPath(String path) {
		if (path == null)
			return null;

		if (!path.startsWith("/"))
			path = "/" + path;
		return pathPrefix + path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessToken == null) ? 0 : accessToken.hashCode());
		result = prime * result + ((pathPrefix == null) ? 0 : pathPrefix.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BaiduPcs))
			return false;
		BaiduPcs other = (BaiduPcs) obj;
		if (accessToken == null) {
			if (other.accessToken != null)
				return false;
		} else if (!accessToken.equals(other.accessToken))
			return false;
		if (pathPrefix == null) {
			if (other.pathPrefix != null)
				return false;
		} else if (!pathPrefix.equals(other.pathPrefix))
			return false;
		return true;
	}

}
