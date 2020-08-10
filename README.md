## 这是一个 java 工具库
`MoonUtil`是一个的`java`工具库，也是在这小几年的工作中所遇到的问题的一个归纳，
也在实际工作中解决了一些实际问题，它将一些常用的操作通过静态方法封装用来提高开发效率。
希望能在实际应用中帮助到更多人，和得到更多人的帮助和完善！

### 安装
要求环境`JDK1.8+`，除了`com.moon.core`之外的包是依赖一些第三方`jar`的，
需要根据实际情况添加，代码中也尽量给出了友好提示，**`github`的代码可能和`maven`不同，
`github`是最新的，现有`api`名称和功能不会更改，过时方法以主版本为单位视情况可能在下一个主版本删除**
``` xml
<dependency>
    <groupId>com.github.moon-util</groupId>
    <artifactId>moon-util</artifactId>
    <version>0.0.33</version>
</dependency>
```

### 部分特点介绍
- `StringUtil.discardXxx(..)`以`discard`开头的方法；以前的一些工具库中通常有`substringBefore`、
`substringAfter`这样的方法，但是如果我们想要保留某个子串以及它前面的内容呢？目前的没有，于是这里提供了
`discardAfter`意为“丢弃...之后”即为保留它以及它之前的内容；
- `Excel`支持，此工具类基于`POI`参考`HTML`中`table`标签实现了`ExcelUtil`，可以很方便的用层级关系导出`Excel`，如：
```
// 最好用 lambda 表达式吧
ExcelUtil.xlsx().sheet('sheetName', sheetFactory -> {
    sheetFactory.row(rowFactory -> {
        rowFactory.next("姓名"); // 创建一个单元格并设置单元格值为 "姓名"
        rowFactory.cell().val("年龄").styleAs("classname"); // 创建一个单元格并设置单元格值为 "年龄"，同时应用应用样式"classname"
        rowFactory.cell(1, 2).val("家庭住址"); // 创建单元格，并合并单元格，然后设置单元格内容

        // 注：样式上面已经用过了，但这里才定义，这是有效的
        // ExcelUtil 同时参照 html 的同时参照了 css，将样式统一管理，然后通过 classname 引用，这样方便个单元格样式重用
        // 另一方面，POI 中样式是有上限的，统一管理也能避免重复创建样式 
        // 而且不一定要在 rowFactory 上定义，可以在任何 xxxxFactory 上定义，并引用在任意位置
        rowFactory.definitionStyle("classname", (style, font) -> {
            font.setBold(true); // 字体加粗
            style.setFont(font);
        });
    });
});
```
> `ExcelUtil`还实现了基于注解的导出(sheetFactory.table(..))，目前已实现复杂表头、列排序、样式等，但还未实现表头样式；
- 日期相关工具类：`CalendarUtil`、`DateUtil`、`DateTimeUtil`、`Datetime`；
- `IteratorUtil`迭代器，可以迭代目前能想到的任何集合、数组、数据库结果集`ResultSet`甚至文件等，还可以
按需求将一个集合拆分成多个固定长度的集合等；
- 其他集合工具类`CollectUtil`、`SetUtil`、`ListUtil`、`FilterUtil`、`GroupUtil`、`Collects`等；
- 表单验证(挺全面的，可以看看)：`TestUtil`、`ValidationUtil`和`ValidatorUtil`；
- 字符串表达式执行工具: `RunnerUtil`可以执行符合`java`语义的任何表达式，如：`RunnerUtil.run("1 + 1 * 5")`；
> `RunnerUtil`还可以执行方法等;


### 反馈或建议
1. 收到的问题都会尽快修改，最快可能一天不只发布一个版本（你看版本号`0.0.33`就知道了!）！
1. 提`Issue`或者`PR`；
2. 作者微信（这个更方便）
<div style="text-align: center;width:100%;">
<img src="https://oss.gowapp.com/images/2006/30/jpg/6e0afd03199b4931b7b9d93d1646188b/moon-wx-320.jpg?x-oss-process=image/resize,w_320,limit_0"></a>
</div>