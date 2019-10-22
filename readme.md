### 开发约定
- 及时沟通
- 用json进行前后端数据交换，采用驼峰命名
- 统一使用git，每天进行pull request
- 后台需对提交的数据进行安全性检查
- 安卓使用Android Studio开发
- 所使用接口前后端要及时沟通

### 开发顺序说明
|id|安卓|后台|备注|安卓分工|
|---|---|---|---|---|
|1|顶部和底部常驻所有页面|数据库搭建||李国平|
|2|用户登录、密码修改|账号分发（密码生成和修改）|账号登录采用IEMI（安卓自动获取），密码（hash）登录|王露|
|3|照片拍摄、完成命名||安卓需要维护机内数据库，参见图片Android_4|李国平|
|4|照片数据上传|照片处理|后台要分类存储|李国平|
|5|数据采集|生成相应excel||王露|
|6|地图标定|地图数据存储，坐标转换|默认谷歌地图、提供百度、腾讯地图转换|李国平|
|7|地图数据搜索|数据采集的一部分，填入相关表||李国平|
|8|图片涂鸦，画板（支持导入图片和将结果导出成图片）|图片处理||李国平|
|9|修改自己采集的错误信息|提供相应接口||王露|

- 中期检查（2019年11月6日前）要完成**1-6**，此时进行一部分工资结算。
- 中期检查结束之后再详细规划后面的开发
- 开发过程中设计到小功能的分工要多沟通，建议直接在群里沟通

### 机型
- 目标机型 honour 8C
- 系统 Android 8.1.0
- 屏幕 1520*720
- 重点优化此机型，其他机型要保持兼容
