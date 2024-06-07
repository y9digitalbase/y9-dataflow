package risesoft.data.transfer.core.util;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.SecureClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

/**
 * 通过包名获取class
 *
 * @author sunchenbin
 * @version 2016年6月23日 下午5:55:18
 */
public class ClassTools {

	/**
	 * 从包package中获取所有的Class
	 *
	 * @param pack
	 * @return
	 */
	public static List<Class<?>> getClasses(String pack) {

		// 第一个class类的集合
		List<Class<?>> list = new ArrayList<Class<?>>();
		// 是否循环迭代
		boolean recursive = true;
		// 获取包的名字 并进行替换
		String packageName = pack;
		String packageDirName = packageName.replace('.', '/');
		// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			// 循环迭代下去
			while (dirs.hasMoreElements()) {
				// 获取下一个元素
				URL url = dirs.nextElement();
				// 得到协议的名称
				String protocol = url.getProtocol();
				// 如果是以文件的形式保存在服务器上
				if ("file".equals(protocol)) {
					// 获取包的物理路径
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					// 以文件的方式扫描整个包下的文件 并添加到集合中

					findAndAddClassesInPackageByFile(packageName, filePath, recursive, list);
				} else if ("jar".equals(protocol)) {
					// 如果是jar包文件
					// 定义一个JarFile
					JarFile jar;
					try {
						// 获取jar
						jar = ((JarURLConnection) url.openConnection()).getJarFile();
						// 从此jar包 得到一个枚举类
						Enumeration<JarEntry> entries = jar.entries();
						// 同样的进行循环迭代
						while (entries.hasMoreElements()) {
							// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							// 如果是以/开头的
							if (name.charAt(0) == '/') {
								// 获取后面的字符串
								name = name.substring(1);
							}
							// 如果前半部分和定义的包名相同
							if (name.startsWith(packageDirName)) {
								int idx = name.lastIndexOf('/');
								// 如果以"/"结尾 是一个包
								if (idx != -1) {
									// 获取包名 把"/"替换成"."
									packageName = name.substring(0, idx).replace('/', '.');
								}
								// 如果可以迭代下去 并且是一个包
								if ((idx != -1) || recursive) {
									// 如果是一个.class文件 而且不是目录
									if (name.endsWith(".class") && !entry.isDirectory()) {
										// 去掉后面的".class" 获取真正的类名
										String className = name.substring(packageName.length() + 1, name.length() - 6);
										try {
											// 添加到classes

											if (packageName != null && !"".equals(packageName)) {
												list.add(Class.forName(packageName + '.' + className));
											}
											// list.add(ClassLoader.getSystemClassLoader().loadClass(packageName + '.' +
											// className));
										} catch (ClassNotFoundException e) {
											// log
											// .error("添加用户自定义视图类错误 找不到此类的.class文件");
											e.printStackTrace();
										}
									}
								}
							}
						}
					} catch (IOException e) {
						// log.error("在扫描用户定义视图时从jar包获取文件出错");
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}

	public static List<Class<?>> getJarsFileClass(String rootPath) throws Exception {
		File file = new File(rootPath);
		if (file.isDirectory()) {
			List<Class<?>> classes = new ArrayList<>();
			List<File> childrens = FileUtils.getChildrens(file);
			for (File children : childrens) {
				if (children.getName().endsWith(".jar")) {
					classes.addAll(getJarFileClass(new FileInputStream(children)));
				}
			}
			return classes;
		} else {
			return getJarFileClass(new FileInputStream(file));
		}
	}

	public static List<Class<?>> getJarFileClass(InputStream inputStream) throws Exception {
		@SuppressWarnings("resource")
		JarInputStream jarInputStream = new JarInputStream(inputStream);
		List<Class<?>> res = new ArrayList<>();
		// 类名->字节码索引
		Map<String, int[]> classByteMap = new HashMap<>();
		// jar 包分开读取的话压缩率达到了2.3 左右 3倍
		byte[] buffer = new byte[inputStream.available() * 3];
		int head = 0;
		int chunk = 256;
		JarEntry jarEntry = jarInputStream.getNextJarEntry();
//        Map<String, int[]> jarFileMap = new HashMap<>();
		while (jarEntry != null) {
			String name = jarEntry.getName();
			// TODO 如果是jar 包的话则加载
			if (name.endsWith(".class")) {
				String className = name.replaceAll("/", ".").replaceAll(".class", "");

				int size = 0, read;
				while (true) {
					read = jarInputStream.read(buffer, head + size, chunk);
					if (read == -1) {
						break;
					}
					size += read;
				}
				classByteMap.put(className, new int[] { head, size });
				head += size;// 上一次的位置
			}
//            else if (name.endsWith(".jar")) {
//                System.out.println(name);
//                if (jarFileMap.containsKey(jarEntry)) {
//                    continue;
//                }
//                //TODO 读取
//                int size = 0, read;
//                while (true) {
//                    read = jarInputStream.read(buffer, head + size, chunk);
//                    if (read == -1) {
//                        break;
//                    }
//                    size += read;
//                }
//                jarFileMap.put(name, new int[]{head, size});
//                res.addAll(getJarFileClass(new ByteArrayInputStream(Arrays.copyOfRange(buffer, head, size))));
//                head += size;//上一次的位置
//            }
			jarEntry = jarInputStream.getNextJarEntry();
		}
		SecureClassLoader classLoader = new SecureClassLoader() {
			@Override
			protected Class<?> findClass(String name) {
				int[] arr = classByteMap.get(name);
				return super.defineClass(name, buffer, arr[0], arr[1]);
			}
		};
		for (String name : classByteMap.keySet()) {
			res.add(classLoader.loadClass(name));
		}
		return res;
	}

	public static List<Class<?>> getInterfaceClass(Class<?> cla) {
		List<Class<?>> interfaceCla = new ArrayList<>();
		loadInterfaceClass(cla, interfaceCla);
		return interfaceCla;
	}

	/**
	 * 获取实例 根据包名和需要的类名
	 * 
	 * @param <T>
	 * @param packName
	 * @param retClass
	 * @return
	 */
	public static <T> List<T> getInstancesOfPack(String packName, Class<T> retClass) throws Exception {
		List<Class<?>> classes = getClasses(packName);
		List<T> resList = new ArrayList<T>();
		for (Class<?> cls : classes) {
			if (retClass.isAssignableFrom(cls)) {
				resList.add(retClass.cast(cls.newInstance()));
			}
		}
		return resList;
	}

	private static void loadInterfaceClass(Class<?> cla, List<Class<?>> classes) {

		Class<?> superclass = cla.getSuperclass();
		if (superclass != null && superclass != Object.class) {
			loadInterfaceClass(superclass, classes);
		}
		Class<?>[] interfaces = cla.getInterfaces();
		if (interfaces.length == 0) {
			return;
		}
		for (Class<?> anInterface : interfaces) {
			classes.add(anInterface);
			loadInterfaceClass(anInterface, classes);
		}
	}

	/**
	 * 以文件的形式来获取包下的所有Class
	 *
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 */
	public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive,
			List<Class<?>> classes) {
		// 获取此包的目录 建立一个File
		File dir = new File(packagePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			// log.warn("用户定义包名 " + packageName + " 下没有任何文件");
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter() {

			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		// 循环所有文件
		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
						classes);
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					// 添加到集合中去
					// classes.add(Class.forName(packageName + '.' +
					// className));
					// 经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净

					classes.add(
							Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
				} catch (ClassNotFoundException e) {
					// log.error("添加用户自定义视图类错误 找不到此类的.class文件");
					e.printStackTrace();
				}
			}
		}
	}

}
