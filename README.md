# reggie

#### 介绍
黑马程序员的瑞吉外卖


[瑞吉外卖](https://www.yuque.com/u2304658432/ueukxd/aorp21?view=doc_embed)<br />[正式开始写代码](https://www.yuque.com/u2304658432/ueukxd/rxhaxw?view=doc_embed)
<a name="jquwK"></a>

# 1. common层工具

<a name="kiCeC"></a>

## 1.1. 返回结果类R

此类是一个通用结果类，服务端响应的所有结果最终都会包装成此种类型返回给前端页面

```properties
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果，服务端响应的数据最终都会封装成此对象
 * @param <T>
 */
@Data
public class R<T> {
    private Integer code; //编码：1成功，0和其它数字为失败
    private String msg; //错误信息
    private T data; //数据
    private Map map = new HashMap(); //动态数据

    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }
    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }
    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
//A. 如果业务执行结果为成功, 构建R对象时, 只需要调用 success 方法; 如果需要返回数据传递 object 参数, 如果无需返回, 可以直接传递null。

//B. 如果业务执行结果为失败, 构建R对象时, 只需要调用error 方法, 传递错误提示信息即可。
```

<a name="4a356695"></a>

## 1.2. 业务逻辑

<a name="1c7c3d38"></a>

### 新增信息

以前的项目中，我在Controller方法中设置形参都是把一个个字段设置为形参，然后在DAO层分别写每个字段的添加方法，这样十分麻烦，我们可以把接收的形参设置为JSON实体类对象，再由mabatis-plus自动生成sql语句，就能省很多事了

```properties
public R<AddressBook> save(@RequestBody AddressBook addressBook) {}
```

<a name="e21231fb"></a>

### 菜品分页查询

在前端回显数据时，我们要回显菜品分类的名称，但是菜品名称不在Dish这张表中，因此Dish实体类中也没有菜品名称的字段，因此我们用DishDto来扩展Dish,来辅助后端给前端发数据



<a name="SYMuw"></a>

# 2. 其他要点

<a name="77db15c9"></a>

### 2.1. 全局异常处理

使用全局异常处理可以避免重复在每一个业务逻辑里面写try...catch来捕获异常

```properties
/**
 * 全局异常处理
 在项目中自定义一个全局异常处理器，在异常处理器上加上注解 @ControllerAdvice,可以通过属性annotations指定拦截哪一类的Controller方法。 并在异常处理器的方法上加上注解 @ExceptionHandler 来指定拦截的是那一类型的异常。
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody//用这个注解可以将返回值R对象以JSON格式响应给页面
@Slf4j
public class  GlobalExceptionHandler {

    /**
     * 异常处理方法
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)//声明拦截异常的类型
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }
}
```

<a name="df775987"></a>

### 2.2. java基础

<a name="5d28833a"></a>

#### Lambda表达式

Lambda表达式简化了函数式接口（只有一个抽象方法的接口）的实现操作，用Lambda表达式可以很快地创建函数式接口的实现对象。<br />当要传递给Lambda体的操作，已经有实现的方法了，就可以使用方法引用

```properties
Comparator<Integer> comparable=(x,y)->Integer.compare(x,y);
    
Comparator<Integer> integerComparable=Integer::compare;//使用方法引用实现相同效果
```

<a name="6d24bd9f"></a>

#### Stream 流


> Java 8 API添加了一个新的抽象称为流Stream，可以让你以一种声明的方式处理数据。
> Stream 使用一种类似用 SQL 语句从数据库查询数据的直观方式来提供一种对 Java 集合运算和表达的高阶抽象。
> Stream API可以极大提高Java程序员的生产力，让程序员写出高效率、干净、简洁的代码。
> 这种风格将要处理的元素集合看作一种流， 流在管道中传输， 并且可以在管道的节点上进行处理， 比如筛选， 排序，聚合等。
> 元素流在管道中经过中间操作（intermediate operation）的处理，最后由最终操作(terminal operation)得到前面处理的结果。


<a name="ICrMB"></a>

### 2.3. Serliazeable

在Redis中存储对象，该对象是需要被序列化的，而对象要想被成功的序列化，就必须得实现 Serializable 接口.Java 序列化技术可以使你将一个对象的状态写入一个Byte 流里（系列化），并且可以从其它地方把该Byte 流里的数据读出来（反序列化）<br />只要让类继承Serialzable接口就可以实现序列化

```java
public class R<T> implements Serializable{
    ......
}
```

<a name="176ce807"></a>

### 2.4. Spring 基础

<a name="7ef6d35b"></a>

#### 接收前端发来的JSON数据

```java
//接收前端发来的JSON数据，需要在相应形参前加@RequestBody注解，因为JSON数据在请求体中被发过来
public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee){}
```

<a name="d27796a9"></a>

#### Springboot中 json序列化与反序列化

Springboot集成并封装了Jackson，使用Jackson来操作json，JSON的序列化与反序列化我们可以通过`@Responsebody`和`@RequestBody`轻松实现

<a name="Lombok"></a>

### 2.5. Lombok

Lombok是一个可以减少java模板代码的工具，我们可以用Lombok来简化Entity类的代码，省去了get,set及构造方法，接下来介绍lombok的常用注解
<a name="921f3d4f"></a>

#### [@Data ](/Data ) 

在Entity类中使用该注解，在项目编译时，会帮我们自动加上set,get以及toString方法
<a name="8505b68b"></a>

#### [@Slf4j ](/Slf4j ) 

在类上使用该注解，我们可以在类中的方法使用`log`函数来输出日志信息

<a name="MybatisPlus"></a>

### 2.6. MybatisPlus

<a name="d0594632"></a>

#### 条件构造器

顾名思义，作用就是封装查询条件，生成sql的where条件<br />在项目中，查询数据库用到了LambdaQuaryWrapper

```java
//2、根据页面提交的用户名username查询数据库
LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Employee::getUsername,employee.getUsername());//这里通过Lambda表达式来获取User实体类中username的字段名，省去了查数据库的步骤，这就是lambda条件构造器的优点
Employee emp = employeeService.getOne(queryWrapper);//相当于里面放了一个查询语句，通过查询语句获取结果
```

<a name="6018a39e"></a>

#### 公共字段填充

数据库里经常需要填充一些公共字段，如用户id,更新时间等，我们可以把这些操作交给mybatis-plus自动完成<br />第一步去实体类给要自动填充的字段加注解

```java
//通过@tablefield声明要自动填充的注解，并指定填充策略
@TableField(fill = FieldFill.INSERT) //插入时填充字段
private LocalDateTime createTime;

@TableField(fill = FieldFill.INSERT_UPDATE) //插入和更新时填充字段
private LocalDateTime updateTime;

@TableField(fill = FieldFill.INSERT) //插入时填充字段
private Long createUser;

@TableField(fill = FieldFill.INSERT_UPDATE) //插入和更新时填充字段
private Long updateUser;
```

第二步在common层添加自定义元数据对象处理器

```java
@Component
    @Slf4j
    public class MyMetaObjecthandler implements MetaObjectHandler {
        /**
* 插入操作，自动填充
* @param metaObject
*/
        @Override
        public void insertFill(MetaObject metaObject) {//实现插入和更新对应的方法
            log.info("公共字段自动填充[insert]...");
            log.info(metaObject.toString());

            metaObject.setValue("createTime", LocalDateTime.now());
            metaObject.setValue("updateTime",LocalDateTime.now());
            metaObject.setValue("createUser",BaseContext.getCurrentId());
            metaObject.setValue("updateUser",BaseContext.getCurrentId());
        }

        /**
* 更新操作，自动填充
* @param metaObject
*/
        @Override
        public void updateFill(MetaObject metaObject) {
            log.info("公共字段自动填充[update]...");
            log.info(metaObject.toString());

            long id = Thread.currentThread().getId();
            log.info("线程id为：{}",id);

            metaObject.setValue("updateTime",LocalDateTime.now());
            metaObject.setValue("updateUser",BaseContext.getCurrentId());
        }
    }
```

<a name="ThreadLocal"></a>

#### ThreadLocal

ThreadLocal并不是一个Thread，而是Thread的局部变量。当使用ThreadLocal维护变量时，ThreadLocal为每个使用该变量的线程提供独立的变量副本，所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。<br />ThreadLocal为每个线程提供单独一份存储空间，具有线程隔离的效果，只有在线程内才能获取到对应的值，线程外则不能访问当前线程对应的值。<br />我们可以在LoginCheckFilter的doFilter方法中获取当前登录用户id，并调用ThreadLocal的set方法来设置当前线程的线程局部变量的值（用户id），然后在MyMetaObjectHandler的updateFill方法中调用ThreadLocal的get方法来获得当前线程所对应的线程局部变量的值（用户id）。 如果在后续的操作中, 我们需要在Controller / Service中要使用当前登录用户的ID, 可以直接从ThreadLocal直接获取。

```java
/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
```

然后我们在filter中判断用户的登录情况，放行前给ThreadLocal赋值

```java
Long empId = (Long) request.getSession().getAttribute("employee");
BaseContext.setCurrentId(empId);
```

然后我们就可以在各个地方获取ThreadLocal变量了
<a name="hJVgH"></a>

# 3. 项目优化

<a name="SpringCache"></a>

### 3.1. SpringCache

Spring Cache只是提供了一层抽象，底层可以切换不同的cache实现。具体就是通过**CacheManager**接口来统一不同的缓存技术。CacheManager是Spring提供的各种缓存技术抽象接口。<br />针对不同的缓存技术需要实现不同的CacheManager：

| **CacheManager**    | **描述**                           |
| ------------------- | ---------------------------------- |
| EhCacheCacheManager | 使用EhCache作为缓存技术            |
| GuavaCacheManager   | 使用Google的GuavaCache作为缓存技术 |
| RedisCacheManager   | 使用Redis作为缓存技术              |

<a name="c11db1c1"></a>

#### 注解

在SpringCache中提供了很多缓存操作的注解，常见的是以下的几个：

| **注解**       | **说明**                                                     |
| -------------- | ------------------------------------------------------------ |
| @EnableCaching | 开启缓存注解功能                                             |
| @Cacheable     | 在方法执行前spring先查看缓存中是否有数据，如果有数据，则直接返回缓存数据；若没有数据，调用方法并将方法返回值放到缓存中 |
| @CachePut      | 将方法的返回值放到缓存中                                     |
| @CacheEvict    | 将一条或多条数据从缓存中删除                                 |

<a name="769cc844"></a>

#### @CachePut注解

> @CachePut  说明： 
> 	作用: 将方法返回值，放入缓存
> 	value: 缓存的名称, 每个缓存名称下面可以有很多key
> 	key: 缓存的key  ----------> 支持Spring的表达式语言SPEL语法


**在save方法上加注解@CachePut  **<br />当前UserController的save方法是用来保存用户信息的，我们希望在该用户信息保存到数据库的同时，也往缓存中缓存一份数据，我们可以在save方法上加上注解 @CachePut，用法如下：

```java
/**
* CachePut：将方法返回值放入缓存
* value：缓存的名称，每个缓存名称下面可以有多个key
* key：缓存的key
*/
@CachePut(value = "userCache", key = "#user.id")
@PostMapping
public User save(User user){
    userService.save(user);
    return user;
}
```

> key的写法如下：
> 	#user.id : #user指的是方法形参的名称, id指的是user的id属性 , 也就是使用user的id属性作为key ;
> 	#user.name: #user指的是方法形参的名称, name指的是user的name属性 ,也就是使用user的name属性作为key ;


<a name="62d50de2"></a>

#### @CacheEvict注解

> @CacheEvict  说明： 
> 	作用: 清理指定缓存
> 	value: 缓存的名称，每个缓存名称下面可以有多个key
> 	key: 缓存的key  ----------> 支持Spring的表达式语言SPEL语法

**在 delete 方法上加注解@CacheEvict  **<br />当我们在删除数据库user表的数据的时候,我们需要删除缓存中对应的数据,此时就可以使用@CacheEvict注解, 具体的使用方式如下:

```java
/**
* CacheEvict：清理指定缓存
* value：缓存的名称，每个缓存名称下面可以有多个key
* key：缓存的key
*/
@CacheEvict(value = "userCache",key = "#p0")  //#p0 代表第一个参数
//@CacheEvict(value = "userCache",key = "#root.args[0]") //#root.args[0] 代表第一个参数
//@CacheEvict(value = "userCache",key = "#id") //#id 代表变量名为id的参数
@DeleteMapping("/{id}")
public void delete(@PathVariable Long id){
    userService.removeById(id);
}
```

<a name="9e2dd6e7"></a>

#### @Cacheable注解

> [@Cacheable ](/Cacheable ) 说明: 
> 	作用: 在方法执行前，spring先查看缓存中是否有数据，如果有数据，则直接返回缓存数据；若没有数据，调用方法并将方法返回值放到缓存中
> 	value: 缓存的名称，每个缓存名称下面可以有多个key
> 	key: 缓存的key  ----------> 支持Spring的表达式语言SPEL语法

**在getById上加注解@Cacheable  **

```java
/**
* Cacheable：在方法执行前spring先查看缓存中是否有数据，如果有数据，则直接返回缓存数据；若没有数据，调用方法并将方法返回值放到缓存中
* value：缓存的名称，每个缓存名称下面可以有多个key
* key：缓存的key
*/
@Cacheable(value = "userCache",key = "#id")
@GetMapping("/{id}")
public User getById(@PathVariable Long id){
    User user = userService.getById(id);
    return user;
}
```

**缓存非null值**<br />在@Cacheable注解中，提供了两个属性分别为： condition， unless 。

> condition : 表示满足什么条件, 再进行缓存 ;
> unless : 表示满足条件则不缓存 ; 与上述的condition是反向的 ;

具体实现方式如下:

```java
/**
 * Cacheable：在方法执行前spring先查看缓存中是否有数据，如果有数据，则直接返回缓存数据；若没有数据，调用方法并将方法返回值放到缓存中
 * value：缓存的名称，每个缓存名称下面可以有多个key
 * key：缓存的key
 * condition：条件，满足条件时才缓存数据
 * unless：满足条件则不缓存
 */
@Cacheable(value = "userCache",key = "#id", unless = "#result == null")
@GetMapping("/{id}")
public User getById(@PathVariable Long id){
    User user = userService.getById(id);
    return user;
}
```

<a name="babad409"></a>

### 3.2. MySQL主从数据库

<a name="cb09c9c5"></a>

### 主从复制

MySQL数据库默认是支持主从复制的，不需要借助于其他的技术，我们只需要在数据库中简单的配置即可。<br />MySQL主从复制是一个异步的复制过程，底层是基于Mysql数据库自带的 **二进制日志** 功能。就是一台或多台MySQL数据库（slave，即**从库**）从另一台MySQL数据库（master，即**主库**）进行日志的复制，然后再解析日志并应用到自身，最终实现 **从库** 的数据和 **主库** 的数据保持一致。MySQL主从复制是MySQL数据库自带功能，无需借助第三方工具。

> **二进制日志：**
> 	二进制日志（BINLOG）记录了所有的 DDL（数据定义语言）语句和 DML（数据操纵语言）语句，但是不包括数据查询语句。此日志对于灾难时的数据恢复起着极其重要的作用，MySQL的主从复制， 就是通过该binlog实现的。默认MySQL是未开启该日志的。

[瑞吉外卖项目优化-Day02](https://www.yuque.com/u2304658432/ueukxd/wr15dn?view=doc_embed)
<a name="dd145fa0"></a>

### 读写分离

[瑞吉外卖项目优化-Day02](https://www.yuque.com/u2304658432/ueukxd/wr15dn?view=doc_embed)



