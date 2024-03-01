# Taboolib-MyConfig

基于Taboolib设计的自己的Config类

# Config类使用指南

`Config`类是一个配置类，用于处理和操作配置文件。以下是对这个类功能和使用方法的详细介绍。

      val configSet = Config.Builder()

            // 不设置默认为插件主目录下的config.yml
            .setPath("test")
            .addDefaultFile("config.yml")

            // 不设置默认加载YAML类型
            .addFileType(Type.YAML)

            // build() 构建配置
            // init()  初始化配置 没有就从资源中释放
            // load()  加载配置到缓存中 接受参数autoReload true/false 是否自动重载
            .build()
            .init()
            .load(true)

        // 获取目标配置
        val config = configSet.getConfig("test/config.yml")

        // 读取内容
        val string = config.getString("String")
        val int = config.getInt("Int")
        val double = config.getDouble("Double")
        val boolean = config.getBoolean("Boolean")
        val stringList = config.getStringList("List.StringList")

        // 打印
        println(string)
        println(int)
        println(double)
        println(boolean)
        println(stringList)
