## 这是一个 java 工具库
`MoonUtil`是一个的`java`工具库，也是在这小几年的工作中所遇到的问题的一个归纳，
也在实际工作中解决了一些实际问题，它将一些常用的操作通过静态方法封装用来提高开发效率。
希望能在实际应用中帮助到更多人，和得到更多人的帮助和完善！

### 安装
要求环境`JDK1.8+`，想出了`com.moon.core`之外的包是依赖一些第三方`jar`的，
需要根据实际情况添加，代码中也尽量给出了友好提示
``` xml
<dependency>
    <groupId>com.github.moon-util</groupId>
    <artifactId>moon-util</artifactId>
    <version>0.0.32</version>
</dependency>
```

### 如何使用
1. 如何截取字符串呢？我们可能用`StringUtils.substringAfter(..)`，那如何只保留
某个子串前的内容呢？`commons`或`hutool`包并没有这种方法，那么现在我们可以用：
`StringUtil.discardAfter(..)`，直译是“丢弃之后的”，实现了这样的功能；
2. `MoonUtil`结合了`POI`提供`Excel`支持，它结合了`html`的思路，将`Excel`结构化成一张表
从`Workbook`、`Sheet`、`Row`、`Cell`具有层级关系，而且由于`Excel`有样式上限，`ExcelUtil`像
`CSS`一样集中管理了所有样式，形成“先声明 + 后使用”的方式，不同单元格直接引用样式定义的`key`就可以了。
3. 有时候我就想能不能就用一个简单的方式迭代所有集合？有，`moon-util`提供了`IteratorUtil`
类可以迭代所有（目前想到的）可迭代的数据，比如数组、集合、文件、SQL结果集等，我们可以这样用
```java
public class Demo {
    public static void main(String[] args) {
        // 这里可以输数组、集合等
        Iterator iterator = IteratorUtil.of(1,2,3,4,5,6);
        
        int[] ints = {1,2,3,4,5,6};
        IteratorUtil.forEach(ints, (value, idx) -> {
            // do something
        });
    }
}
```
当然还有其他很多工具，如`TestUtil`、`ValidationUtil`等提供表单验证，等你来发现，也期待你
提供反馈逐步完善！

收到的问题都会尽快修改，最快可能一天不只发布一个版本！

### 反馈或建议

1. 提`Issue`或者`PR`；
2. 联系作者本人微信
<div style="text-align: center;width:100%;">
<img src="https://oss.gowapp.com/images/2006/30/jpg/6e0afd03199b4931b7b9d93d1646188b/moon-wx-320.jpg?x-oss-process=image/resize,w_320,limit_0"></a>
</div>