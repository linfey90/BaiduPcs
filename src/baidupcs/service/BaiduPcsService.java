package baidupcs.service;

import baidupcs.response.BasicResponse;
import baidupcs.response.file.CreateFileResponse;
import baidupcs.response.file.ListOrSearchResponse;
import baidupcs.response.file.MkdirResponse;
import baidupcs.response.file.QuotaResponse;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.http.Streaming;
import retrofit.mime.TypedOutput;

public interface BaiduPcsService {
	/**
	 * 构建RestAdapter.Builder需要使用的server配置。
	 */
	String SERVER = "https://c.pcs.baidu.com/rest/2.0/pcs";

	/**
	 * info方法的method参数值。
	 */
	String METHOD_INFO = "info";
	/**
	 * createSuperFile方法的method参数值。
	 */
	String METHOD_CREATESUPERFILE = "createsuperfile";
	/**
	 * mkdir方法的method参数值。
	 */
	String METHOD_MKDIR = "mkdir";
	
	/**
	 * list方法的method参数值。
	 */
	String METHOD_LIST = "list";


	/**
	 * delete、deleteBatch和clearRecycle方法的method参数值。
	 */
	String METHOD_DELETE = "delete";
	
	/**
	 * diff方法的method参数值。
	 */
	String METHOD_DIFF = "diff";
	
	/**
	 * listStream方法的method参数值。
	 */
	String METHOD_LIST_STREAM = "list";
	
	

	/**
	 * createSuperFile方法ondup参数值，表示覆盖同名文件。
	 */
	String CREATE_SUPER_FILE_ONDUP_OVERWRITE = "overwrite";
	/**
	 * createSuperFile方法ondup参数值，表示生成文件副本并进行重命名，命名规则为“文件名_日期.后缀”。
	 */
	String CREATE_SUPER_FILE_ONDUP_NEWCOPY = "newcopy";

	/**
	 * 获取目录下的文件列表的排序参数值，根据修改时间排序。
	 */
	String LIST_BY_TIME = "time";
	/**
	 * 获取目录下的文件列表的排序参数值，根据文件名排序。
	 */
	String LIST_BY_NAME = "name";
	/**
	 * 获取目录下的文件列表的排序参数值，根据大小（注意目录无大小）排序。
	 */
	String LIST_BY_SIZE = "size";

	/**
	 * 获取目录下的文件列表的顺序参数值，采用升序排序。
	 */
	String LIST_ORDER_ASC = "asc";
	/**
	 * 获取目录下的文件列表的顺序参数值，采用降序排序。
	 */
	String LIST_ORDER_DESC = "desc";


	/**
	 * 获取流式文件列表的类型参数值。文档。
	 */
	String LIST_STREAM_TYPE_DOC = "doc";
	
	/**
	 * upload和uploadBlock方法method参数值。
	 */
	String METHOD_UPLOAD = "upload";

	/**
	 * upload方法ondup参数值，表示覆盖同名文件。
	 */
	String UPLOAD_ONDUP_OVERWRITE = "overwrite";
	/**
	 * upload方法ondup参数值，表示生成文件副本并进行重命名，命名规则为“文件名_日期.后缀”。
	 */
	String UPLOAD_ONDUP_NEWCOPY = "newcopy";
	
	/**
	 * download方法method参数值。
	 */
	String METHOD_DOWNLOAD = "download";

	/**
	 * 下载单个文件。<br>
	 * Download接口支持HTTP协议标准range定义，通过指定range的取值可以实现断点下载功能。 例如：<br>
	 * 如果在request消息中指定“Range: bytes=0-99”，那么响应消息中会返回该文件的前100个字节的内容；继续指定“Range:
	 * bytes=100-199”，那么响应消息中会返回该文件的第二个100字节内容。
	 * 
	 * @param method
	 *             固定值，download。
	 * @param access_token
	 *             开发者准入标识。
	 * @param path
	 *             下载文件路径，以/开头的绝对路径。<br>
	 *             注意：<br>
	 *             <li>路径长度限制为1000<br> <li>路径中不能包含以下字符：\\ ? | " > < : *<br> <li>
	 *             文件名或路径名开头结尾不能是“.”或空白字符，空白字符包括: \r, \n, \t, 空格, \0, \x0B
	 * @param range
	 *             HTTP Range header，通过指定range的取值可以实现断点下载功能。 例如：<br>
	 *             如果指定“bytes=0-99”，那么响应消息中会返回该文件的前100个字节的内容；<br>
	 *             继续指定“bytes=100-199”，那么响应消息中会返回该文件的第二个100字节内容。<br>
	 *             如果为null则下载整个文件。
	 * @return 文件内容（原始Response对象）
	 * @throws Throwable
	 *              ErrorHandler可能返回的任何异常或错误
	 */
	@GET("/file")
	@Streaming
	Response download(@Query("method") String method, @Query("access_token") String access_token,
			@Query("path") String path, @Header("Range") String range) throws Throwable;

	/**
	 * 上传单个文件。<br>
	 * 百度PCS服务目前支持最大2G的单个文件上传。<br>
	 * 如需支持超大文件（>2G）的断点续传，请参考下面的“分片文件上传”方法。
	 * 
	 * @param method
	 *             固定值，upload。
	 * @param accessToken
	 *             开发者准入标识。
	 * @param path
	 *             上传文件路径（含上传的文件名称)。<br>
	 *             注意：<br>
	 *             <li>路径长度限制为1000<br> <li>路径中不能包含以下字符：\\ ? | " > < : *<br> <li>
	 *             文件名或路径名开头结尾不能是“.”或空白字符，空白字符包括: \r, \n, \t, 空格, \0, \x0B
	 * @param file
	 *             上传文件的内容。API要求fileName值不能为null。
	 * @param ondup
	 *             可选参数。overwrite：表示覆盖同名文件；newcopy：表示生成文件副本并进行重命名，命名规则为“文件名_日期.
	 *             后缀”。
	 * @return
	 * @throws Throwable
	 *              ErrorHandler可能返回的任何异常或错误
	 */
	@Multipart
	@POST("/file")
	CreateFileResponse upload(@Query("method") String method, @Query("access_token") String accessToken,
			@Query("path") String path, @Part("file") TypedOutput file, @Query("ondup") String ondup)
			throws Throwable;


	/**
	 * 获取当前用户空间配额信息。
	 * 
	 * @param method
	 *             固定值：info。
	 * @param accessToken
	 *             开发者准入标识。
	 * @return
	 * @throws Throwable
	 *              ErrorHandler可能返回的任何异常或错误
	 */
	@GET("/quota")
	QuotaResponse quotaInfo(@Query("method") String method, @Query("access_token") String accessToken)
			throws Throwable;


	/**
	 * 为当前用户创建一个目录。
	 * 
	 * @param method
	 *             固定值，mkdir。
	 * @param accessToken
	 *             开发者准入标识。
	 * @param path
	 *             需要创建的目录，以/开头的绝对路径。<br>
	 *             注意：<br>
	 *             <li>路径长度限制为1000<br> <li>路径中不能包含以下字符：\\ ? | " > < : *<br> <li>
	 *             文件名或路径名开头结尾不能是“.”或空白字符，空白字符包括: \r, \n, \t, 空格, \0, \x0B
	 * @return
	 * @throws Throwable
	 *              ErrorHandler可能返回的任何异常或错误
	 */
	@Multipart
	@POST("/file")
	MkdirResponse mkdir(@Query("method") String method, @Query("access_token") String accessToken,
			@Part("path") String path) throws Throwable;


	/**
	 * 获取目录下的文件列表。
	 * 
	 * @param method
	 *             固定值，list。
	 * @param accessToken
	 *             开发者准入标识。
	 * @param path
	 *             需要list的目录，以/开头的绝对路径。<br>
	 *             注意：<br>
	 *             <li>路径长度限制为1000<br> <li>路径中不能包含以下字符：\\ ? | " > < : *<br> <li>
	 *             文件名或路径名开头结尾不能是“.”或空白字符，空白字符包括: \r, \n, \t, 空格, \0, \x0B
	 * @param by
	 *             可选参数。排序字段，缺省根据文件类型排序：<br>
	 *             <li>time（修改时间）<br> <li>name（文件名）<br> <li>size（大小，注意目录无大小）
	 * @param order
	 *             可选参数。“asc”或“desc”，缺省采用降序排序。<br>
	 *             <li>asc（升序）<br> <li>desc（降序）
	 * @param limit
	 *             可选参数。返回条目控制，参数格式为：n1-n2。返回结果集的[n1, n2)之间的条目，缺省返回所有条目；n1从0开始。
	 * @return
	 * @throws Throwable
	 *              ErrorHandler可能返回的任何异常或错误
	 */
	@GET("/file")
	ListOrSearchResponse list(@Query("method") String method, @Query("access_token") String accessToken,
			@Query("path") String path, @Query("by") String by, @Query("order") String order,
			@Query("limit") String limit) throws Throwable;


	/**
	 * 删除单个文件/目录。<br>
	 * 注意：<br>
	 * <li>文件/目录删除后默认临时存放在回收站内，删除文件或目录的临时存放不占用用户的空间配额；<br> <li>
	 * 存放有效期为10天，10天内可还原回原路径下，10天后则永久删除。
	 * 
	 * @param method
	 *             固定值，delete。
	 * @param accessToken
	 *             开发者准入标识。
	 * @param path
	 *             需要删除的文件或者目录路径。如：/apps/album/a/b/c<br>
	 *             注意：<br>
	 *             <li>路径长度限制为1000<br> <li>路径中不能包含以下字符：\\ ? | " > < : *<br> <li>
	 *             文件名或路径名开头结尾不能是“.”或空白字符，空白字符包括: \r, \n, \t, 空格, \0, \x0B
	 * @return
	 * @throws Throwable
	 *              ErrorHandler可能返回的任何异常或错误
	 */
	@Multipart
	@POST("/file")
	BasicResponse delete(@Query("method") String method, @Query("access_token") String accessToken,
			@Part("path") String path) throws Throwable;

}
