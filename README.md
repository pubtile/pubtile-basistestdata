# Pubtile Basistestdata

## 介绍
自动化测试大家都不陌生，在日常开发中都会编写测试用例来校验系统是否按照我们的期望来执行，但是大家有都很讨厌写测试用例，不是觉得不重要，而是太麻烦，维护成本还高。
其中成本最高的就是造数据的成本，造出符合特定业务场景的数据不是一件轻松的事情。传统的测试数据的维护都很做到 成本最低的维护一个稳定的测试数据。这无疑对自动化测试的推行带来很多的障碍。

所以造测试数据的稳定性和数据准备成本是自动化测试能否成功推行的最最关键因素。

本框架实际上是个方法论，采用独特的角度去解决该问题，传统方式重点大都在“造”数据上。本框架的角度是”改"数据。

这个方法论是这样的，事先准备好一些典型样板数据作为基线测试数据，然后在测试开始前，根据特定的业务case需要，更改某个或某些基线数据已达到营造适合的测试场景要求，然后执行这些测试用例，执行完测试用例后，回滚为数据回到基线状态。姑且把这个测试方法叫做 “基线数据测试法“

该框架就是该方法的一种实践。你只需用json表达要更改的数据，然后在测试方法上加上个@DataPrepare 的annotation就搞定了。

https://github.com/pubtile/pubtile-basistestdata

## 使用场景

基本上所有的业务类系统的自动化测试都可以使用，只要满足测试用例是基于数据库的。

## 集成步骤

### 快速开始

步骤一： 准备基线数据

根据业务要求，先准备好 **基线** 测试数据

步骤二：添加依赖

```xml
<dependency>
   <groupId>com.pubtile</groupId>
   <artifactId>pubtile-basistestdata</artifactId>
   <version>0.6.17</version>
   <scope>test</scope>
</dependency>
```

步骤三：

在测试方法上添加@DataPrepare，标明该测试方法需要在基线测试数据基础上需要数据额外准备。

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ExampleApplication.class）
class IBillOrderServiceTest{
    /**
    * 场景：当用户是block状态，无法创建订单
    * @author jiayan
    * @version 0.0.1 8/30/21
    * @since 0.0.1 8/30/21
    */
    @DataPrepare
    @Test
    @Transactional
    public void createOrder_blockedUser_fail() throws ExecutionException, InterruptedException {
        ...
    }
}
```

步骤四：

准备 需修改的数据，用JSON格式表达出来存放在{ClassName}_{MethodName}&{TableName}.json。

例如

IBillOrderServiceTest_createOrder_blockedUser_fail&customer.json

```json
[
  {
    "pn": "id",
    "pv": 1,
    "cells": [
      {
        "c":"status",
        "v":"BLOCKED"
      }
    ]
  }
]  
```

这个例子就是把customer表id=1的顾客的状态改为BLOCKED

更多例子请参见 https://github.com/pubtile/pubtile-basistestdata-examples

## 核心特性

1. 在测试开始前，快速更改数据
2. 支持mysql, oracle
3. 多数据源 TODO ；
4. 支持先实际更改并提交，然后再更正回基线
5. 支持同一个事务更改，但未提交

 ## 最佳实践

+ 基线数据用可以使用docker来维护，每个开发和测试人员冲docker repository  pull 过来使用

+ 基线数据的要求是能够快速的通过简单的修改，就能产生不同的测试场景。 所以其特性是

  + 包含主要的特性数据

    譬如订单状态不同的数据都要有，因为从一个订单状态到另一个订单状态涉及的数据变化过多。

  + 容易识别，当出现一个测试场景时，很容易找到某一或某些测试数据

  

