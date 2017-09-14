# 数据库导出到CSV文件 
>将数据库表导出为CSV文件，逗号分隔符文件

## 应用目录介绍
/bin
>编译后打包成jar文件的存放目录

/build
>编码文件的目标目录，存放。class文件

/conf
>配置数据库连接和文件存放目录的jdbc.properties文件
>配置取数据的表列表文件tab.properties

/file
>存放测试数据库导出文件

/lib
>需要用到的jdbc的jar文件和其他包文件

## 编译源代码
/build.xml
>源代码用ant编译

## 应用使用
/sql2csv.bat 
>是应用启动

/start.bat 
>批量导出使用，需要配合配置文件