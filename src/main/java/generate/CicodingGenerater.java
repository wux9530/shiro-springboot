package generate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

public class CicodingGenerater {
	
	AutoGenerator mpg = new AutoGenerator();

	// 数据库database
	private String databaseName = "edxapp";

	// 数据库用户
	private String username = "root";

	// 数据库密码
	private String password = "CSpa2s_is";

	// 数据库url
	private String dbUrl = "jdbc:mysql://localhost:3306/" + databaseName
			+ "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

	// 生成代码包结构
	private String packageName = "com.cicoding.";

	// 生成代码路径
	private String fileName = "/Users/wuxiang/Desktop/code";

	private String tableName;
	private String modelName;
	private static VelocityEngine ve = new VelocityEngine();
	private File velocityOutdir = new File("/Users/wuxiang/Desktop/code/src/main/java/com/du/cicoding/");
	private List<String> attrs = new ArrayList<String>();
	private String menuName;
	private String icon;
	/**
	 * @param tableName 表名
	 */
	public CicodingGenerater(String tableName) {
		super();
		this.tableName = tableName;
		this.modelName = tableName.substring(0, 1).toUpperCase() + tableName.substring(1);
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getTableName() {
		return tableName;
	}

	public String getModelName() {
		return modelName;
	}
	
	
	
	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	 /**
	 * 全局配置
	 */
	private void globalConfigInit() {
		GlobalConfig gc = new GlobalConfig();
		gc.setOutputDir("D://cicoding生成代码/cicoding/src/main/java");
		gc.setFileOverride(true);
		gc.setActiveRecord(true);// 不需要ActiveRecord特性的请改为false
		gc.setEnableCache(false);// XML 二级缓存
		gc.setBaseResultMap(true);// XML ResultMap
		gc.setBaseColumnList(false);// XML columList
		gc.setAuthor("kyrie");
		mpg.setGlobalConfig(gc);
	}
	 /**
	 * 数据源配置
	 */
	private void dataSourceConfigInit() {
		DataSourceConfig dsc = new DataSourceConfig();
		dsc.setDbType(DbType.MYSQL);
		dsc.setDriverName("com.mysql.jdbc.Driver");
		dsc.setUsername(username);
		dsc.setPassword(password);
		dsc.setUrl(dbUrl);
		mpg.setDataSource(dsc);
	}
	 /**
	 * 策略配置
	 */
	private void strategyConfigInit() {
		StrategyConfig strategyConfig = new StrategyConfig();
		// strategy.setTablePrefix(new String[]{"_"});// 此处可以修改为您的表前缀
		strategyConfig.setInclude(new String[] { tableName });// 这里限制需要生成的表,不写则是生成所有表
		strategyConfig.setNaming(NamingStrategy.underline_to_camel);// 表名生成策略  下划线转驼峰命名
		mpg.setStrategy(strategyConfig);
	}
	 /**
	 * 包配置
	 */
	private void packageConfigInit() {
		PackageConfig pc = new PackageConfig();
		pc.setParent(null);
		pc.setEntity(packageName + "bean");
		pc.setMapper(packageName + "dao");
		mpg.setPackageInfo(pc);

	}
	 /**
	 * 初始化Velocity
	 */
	private void VelocityInit() {
		ve.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.setProperty(Velocity.ENCODING_DEFAULT, "utf8");
		ve.setProperty(Velocity.INPUT_ENCODING, "utf8");
		ve.setProperty(Velocity.OUTPUT_ENCODING, "utf8");
		ve.init();
	}
	 /**
	 * MP模板设置
	 */
	private void templateConfigInit(){
		TemplateConfig tc = new TemplateConfig();
		tc.setController(null);
		tc.setXml(null);
		tc.setService(null);
		tc.setServiceImpl(null);
		mpg.setTemplate(tc);
	}
	 /**
	 * 初始化
	 */	
	private void init() {
		attrs = getColumnList();
		globalConfigInit();
		dataSourceConfigInit();
		strategyConfigInit();
		packageConfigInit();
		templateConfigInit();
		VelocityInit();
	}
    /**
	 * 1.初始化 2.Velocity生成Controller、html、js、Service、ServiceImpl  3.MP生成Mapper、bean
	 */
	public void execute() {
		init();
		// 执行生成
		velocityGenerate();
		mpg.execute();
		System.out.println("可以使用CicodingGenerater中的insertMenu方法将菜单信息写入数据库");
		System.out.println("之后在角色管理中配置菜单即可");
	}
    /**
	 * 获取数据库表字段
	 */
	private List<String> getColumnList() {
		List<String> columnName = new ArrayList<String>();
		Connection conn = null;
		java.sql.PreparedStatement pst = null;
		ResultSet re = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(dbUrl,username, password);
			pst = conn.prepareStatement("select * from " + tableName);
			// 获取数据库列
			re = pst.executeQuery();
			// 数据库列名
			ResultSetMetaData data = re.getMetaData();

			for (int i = 1; i <= data.getColumnCount(); i++) {
				columnName.add(data.getColumnName(i));
			}
			
			columnName.remove("id");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				re.close();
				pst.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return columnName;
	}

	

    /**
	 * Velocity生成Controller
	 */
	private void controllerGenterate() throws IOException {
		Template t = ve.getTemplate("/templates/vm/Controller.java.vm");
		VelocityContext vc = new VelocityContext();
		vc.put("model", getTableName());
		// 首写字母大写
		vc.put("model1", getModelName());
		File file = new File(velocityOutdir.getAbsolutePath() + "/controller/" + getModelName() + "Controller.java");
		CreateParents(file);
		FileWriter fw = new FileWriter(file);
		t.merge(vc, fw);
		fw.flush();
		fw.close();
	}
    /**
	 * Velocity生成html
	 */
	private void htmlGenterate() throws IOException {
		Template t = ve.getTemplate("/templates/vm/Page.html.vm");
		VelocityContext vc = new VelocityContext();
		vc.put("model", getTableName());
		// 首写字母大写
		vc.put("model1", getModelName());
		File file = new File("/Users/wuxiang/Desktop/code/src/main/resources/templates/" + getTableName() + "page.html");
		CreateParents(file);
		FileWriter fw = new FileWriter(file);
		t.merge(vc, fw);
		fw.flush();
		fw.close();

	}
    /**
	 * Velocity生成js
	 */
	private void jsGenterate() throws IOException {
		Template t = ve.getTemplate("/templates/vm/Page.js.vm");
		VelocityContext vc = new VelocityContext();
		vc.put("model", getTableName());
		// 首写字母大写
		vc.put("model1", getModelName());
		vc.put("attrs", attrs);
//		File file = new File("d:/cicoding生成代码/cicoding/src/main/webapp/static/js/cicoding/" + getTableName() + ".js");
		File file = new File("/Users/wuxiang/Desktop/code/src/main/webapp/static/js/cicoding/" + getTableName() + ".js");
		CreateParents(file);
		FileWriter fw = new FileWriter(file);
		t.merge(vc, fw);
		fw.flush();
		fw.close();

	}
    /**
	 * Velocity生成Service
	 */
	private void serviceGenterate() throws IOException {
		Template t = ve.getTemplate("/templates/vm/Service.java.vm");
		VelocityContext vc = new VelocityContext();
		vc.put("model", getTableName());
		// 首写字母大写
		vc.put("model1", getModelName());
		File file = new File(
				velocityOutdir.getAbsolutePath() + "/service/" + getModelName() + "Service.java");
		CreateParents(file);
		FileWriter fw = new FileWriter(file);
		t.merge(vc, fw);
		fw.flush();
		fw.close();
	}
    /**
	 * Velocity生成ServiceImpl
	 */
	private void serviceImplGenterate() throws IOException {
		Template t = ve.getTemplate("/templates/vm/ServiceImpl.java.vm");
		VelocityContext vc = new VelocityContext();
		vc.put("model", getTableName());
		// 首写字母大写
		vc.put("model1", getModelName());
		
		File file = new File(
				velocityOutdir.getAbsolutePath() + "/service/impl/" + getModelName() + "ServiceImpl.java");


		CreateParents(file);
		FileWriter fw = new FileWriter(file);
		t.merge(vc, fw);
		fw.flush();
		fw.close();
	}

	private void velocityGenerate() {
		try {
			controllerGenterate();
			serviceGenterate();
			serviceImplGenterate();
			jsGenterate();
			htmlGenterate();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 创建文件的父文件夹
	 */
	private void CreateParents(File file){
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
	}
	/**
	 * 插入菜单数据到数据库
	 */	
	public void insertMenu(String tabelName , String menuName , String icon){
		if (tabelName == null ||"".equals(tabelName)) {
			System.out.println("tableName error");
		}
		if (menuName == null ||"".equals(menuName)) {
			System.out.println("menuName error");
		}
		if (icon == null ||"".equals(icon)) {
			System.out.println("icon error");
		}
		
		Connection conn = null;
		java.sql.PreparedStatement ps = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(dbUrl,username, password);
			ps =	conn.prepareStatement("insert into menu (name,icon,url,levels) values(?,?,?,?)");
			ps.setString(1, menuName);
			ps.setString(2, "fa-"+icon);
			ps.setString(3, "/" + tabelName);
			ps.setInt(4, 1);
			int result = ps.executeUpdate();
			if (result == 1) {
				System.out.println("菜单添加成功！");
				System.out.println("在角色管理中配置菜单，即可在右侧看到此菜单");
			}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}finally{
				try {
					ps.close();
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
	}
	
}
