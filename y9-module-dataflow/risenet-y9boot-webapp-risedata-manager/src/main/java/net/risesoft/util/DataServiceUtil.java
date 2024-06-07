package net.risesoft.util;

/**
 * 任务配置工具类
 * @author pzx
 *
 */
public class DataServiceUtil {
	
	/**
	 * 数据闸口
	 */
	public static final String EXCHANGE = "exchange";
	
	/**
	 * 输入、输出线程池
	 */
	public static final String EXECUTOR = "executor";
	
	/**
	 * 输入、输出通道
	 */
	public static final String CHANNEL = "channel";
	
	/**
	 * 其它插件
	 */
	public static final String PLUGS = "plugs";
	
	/**
	 * 日志打印
	 */
	public static final String PRINTLOG = "printLog";
	
	/**
	 * 脏数据处理
	 */
	public static final String DIRTYDATA = "dirtyData";
	
	/**
	 * 数据脱敏
	 */
	public static final String MASK = "mask";
	/**
	 * 数据脱密执行类
	 */
	public static final String MASKCLASS = "";
	
	/**
	 * 数据加密
	 */
	public static final String ENCRYP = "encryp";
	/**
	 * 数据加密执行类
	 */
	public static final String ENCRYPCLASS = "";
	
	/**
	 * 增量同步
	 */
	public static final String BULKSYNC = "bulkSync";
	/**
	 * 增量同步执行类
	 */
	public static final String BULKSYNCCLASS = "";
	
	/**
	 * 异字段
	 */
	public static final String DIFFERENT = "different";
	/**
	 * 异字段执行类
	 */
	public static final String DIFFERENTCLASS = "risesoft.data.transfer.plug.data.rename.FieldReNamePlug";
	
	/**
	 * 时间格式
	 */
	public static final String DATE = "date";
	/**
	 * 时间格式执行类
	 */
	public static final String DATECLASS = "";
	
	/**
	 * 数据转换
	 */
	public static final String CONVERT = "convert";
	/**
	 * 数据转换执行类
	 */
	public static final String CONVERTCLASS = "";
	
	/**
	 * 输出
	 */
	public static final String OUTPUT = "output";
	
	/**
	 * 输入
	 */
	public static final String INPUT = "input";

	public static String getTitle(String name) {
		switch (name) {
			case "output":
				return "输出";
			case "input":
				return "输入";
			case "dirtyData":
				return "脏数据处理";
			case "printLog":
				return "日志";
			default:
				return name;
		}
	}
}
