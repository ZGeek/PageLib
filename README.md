# PageLib
 > 一个为抛弃复杂的Fragment生命周期和巨大的Activity鸿沟而生的库

![demo_video](https://github.com/ZGeek/PageLib/blob/master/art/demo.gif)

 ## 此库为何而生
 - Fragment为安卓开发带来了碎片化界面开发的新体验，然而体验过后我们发现，Fragment并不完美，复杂的生命周期让Fragment在管理的时候特别棘手，例如判断一个Fragment是否对用户可见，有些时候需要综合运用onResume onPause setUserVisiableHint onHiddenChanged等众多方法。
 - Android的Activity与Activity之间又被相隔的太过彻底，作为程序员，你没法控制Activity的new和release，Activity之间的通信更多的是依赖startActivityForResult或第三方的消息总线进行底层数据通信，这使得我在编写Android程序时甚至怀疑自己不是在编写一个安卓程序，而是在编写很多程序，并把他们组合起来，这种Activity之间的鸿沟使得需要对Activity直接进行操作时，或者进行交互操作时很不方便
 - 每个Activity持有一个单一的PhoneWindow对像导致Activity和Activity之间的界面复用近乎不可能，例如你若想把A-Acvitity中的某一个View，拿到B-Activity中使用是几乎不可能的，因为这样的操作很容易导致内存泄露
 - 同时，Activity是一个非常重的组件，每一次的初始化操作都会消耗很多cpu资源，这导致很多的Android程序只使用一个Activity进行开发

 ## 此库想达到的目的
 - 单Activity化
 - 彻底抛弃复杂的Fragment

 ## 如何使用
 ### 了解Page
 每一个Page都是屏幕中可显示区域的一部分，有如下的生命周期函数

| 方法 | 意义 |
| --- | ---|
| onViewInited |当Page所持有的View初始化的时候调用，每个Page仅调用一次|
|onShow | 当Page即将显示时调用|
|onShown| 当Page已经显示时调用|
|onHide| 当Page将要隐藏时调用|
|onHidden| 当Page已经隐藏时调用|
|onDestroy| 当Page将要销毁时调用，如果Page所持有的view尚未初始化，则不会调用此方法|
|onLowMemory| 当系统内存不足的时候调用|
|onActivityResult| 同Activity的onActivityResult，只有当启动外部Activity时调用|
|onKeyDown| 当键盘按键按下时调用|
|onKeyUp|当键盘按键释放时调用|
|onConfigurationChanged| 同Activity的onConfigurationChanged，用来响应Configuration变化引起的视图变化，不用保存视图状态|
|getParentPage| 获得此Page的父Page，若此Page为PageActivity的rootPage，或者此page尚未被附加到任何page上，则其父page为null|
|getRootView| 得到此page所持有的根View，若此Page尚未初始化，则会根据@PageLayout或者@PageLayoutName指定的layout对根View进行初始化，在初始化后回回调onViewInited方法|
|getChildPageAt|对此Page持有的子Page进行管理|
|getChildPageIndex|同上|
|getSubChildPages|同上|
|getChildPageCount|同上|
|setPageName|设置此page的name，用于调试和在ViewPagerPage中提供名称，调用后返回此page|

### 已有Page介绍
|名称|介绍|
|---|---|
|SingleActivePage| 抽象类，每次只有一个Page激活的Page管理类，已知子类有SwitchPage， SubSwitchPage， ViewPagerPage， NavigationPage|
|NavigationPage|导航Page，其内部维护了一个回退栈，通过pushPage，popPage，deletePage等对回退栈进行管理，使用返回键可以进行pop，若当前在顶端的page不允许使用返回键进行回退，则可以继承CanSwipToHide接口并使canSwipToHide返回false|
|ViewPagerPage|内部使用viewpager对所有的page进行管理，通过switchToPage，addPage，addPages，removePage对ViewPagerPage内部的Page进行管理
|SwitchPage|和ViewPagerPage类似，每次只激活一个page进行显示，多个page之间可以切换|
|SubSwitchPage|和SwitchPage类似，不同之处在于，SwitchPage的所持有的view的整个显示区域供其子page显示，而SubSwitchPage供其子page显示的区域只是其持有view可显示区域的一部分，所以此page可以和TabLayout配合使用，实现多Tab的切换|

# Feature
1. ~~View懒加载~~
1. ~~实现ViewPager样式的page切换~~
2. ~~实现View激活状态精确判断~~
3. ~~实现activity的状态保存~~
4. 在onLowMemory时销毁所有不可见View，只保留所有对用户可见的View
