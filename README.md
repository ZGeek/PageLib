# PageLib
 > һ��Ϊ�������ӵ�Fragment�������ں;޴��Activity�蹵�����Ŀ�

 ## �˿�Ϊ�ζ���
 - FragmentΪ��׿������������Ƭ�����濪���������飬Ȼ������������Ƿ��֣�Fragment�������������ӵ�����������Fragment�ڹ����ʱ���ر��֣������ж�һ��Fragment�Ƿ���û��ɼ�����Щʱ����Ҫ�ۺ�����onResume onPause setUserVisiableHint onHiddenChanged���ڶ෽����
 - Android��Activity��Activity֮���ֱ������̫�����ף���Ϊ����Ա����û������Activity��new��release��Activity֮���ͨ�Ÿ����������startActivityForResult�����������Ϣ���߽��еײ�����ͨ�ţ���ʹ�����ڱ�дAndroid����ʱ���������Լ������ڱ�дһ����׿���򣬶����ڱ�д�ܶ���򣬲��������������������Activity֮��ĺ蹵ʹ����Ҫ��Activityֱ�ӽ��в���ʱ�����߽��н�������ʱ�ܲ�����
 - ÿ��Activity����һ����һ��PhoneWindow������Activity��Activity֮��Ľ��渴�ý��������ܣ������������A-Acvitity�е�ĳһ��View���õ�B-Activity��ʹ���Ǽ��������ܵģ���Ϊ�����Ĳ��������׵����ڴ�й¶
 - ͬʱ��Activity��һ���ǳ��ص������ÿһ�εĳ�ʼ�������������ĺܶ�cpu��Դ���⵼�ºܶ��Android����ֻʹ��һ��Activity���п���

 ## �˿���ﵽ��Ŀ��
 - ��Activity��
 - �����������ӵ�Fragment

 ## ���ʹ��
 ### �˽�Page
 ÿһ��Page������Ļ�п���ʾ�����һ���֣������µ��������ں���

| ���� | ���� |
| -- |--|
| onViewInited |��Page�����е�View��ʼ����ʱ����ã�ÿ��Page������һ��|
|onShow | ��Page������ʾʱ����|
|onShown| ��Page�Ѿ���ʾʱ����|
|onHide| ��Page��Ҫ����ʱ����|
|onHidden| ��Page�Ѿ�����ʱ����|
|onDestroy| ��Page��Ҫ����ʱ���ã����Page�����е�view��δ��ʼ�����򲻻���ô˷���|
|onLowMemory| ��ϵͳ�ڴ治���ʱ�����|
|onActivityResult| ͬActivity��onActivityResult��ֻ�е������ⲿActivityʱ����|
|onKeyDown| �����̰�������ʱ����|
|onKeyUp|�����̰����ͷ�ʱ����|
|onConfigurationChanged| ͬActivity��onConfigurationChanged��������ӦConfiguration�仯�������ͼ�仯�����ñ�����ͼ״̬|
|getParentPage| ��ô�Page�ĸ�Page������PageΪPageActivity��rootPage�����ߴ�page��δ�����ӵ��κ�page�ϣ����丸pageΪnull|
|getRootView| �õ���page�����еĸ�View������Page��δ��ʼ����������@PageLayout����@PageLayoutNameָ����layout�Ը�View���г�ʼ�����ڳ�ʼ����ػص�onViewInited����|
|getChildPageAt|�Դ�Page���е���Page���й���|
|getChildPageIndex|ͬ��|
|getSubChildPages|ͬ��|
|getChildPageCount|ͬ��|
|setName|���ô�page��name�����ڵ��Ժ���ViewPagerPage���ṩ���ƣ����ú󷵻ش�page|

### ����Page����
|����|����|
|---|---|
|SingleActivePage| �����࣬ÿ��ֻ��һ��Page�����Page�����࣬��֪������SwitchPage�� SubSwitchPage�� ViewPagerPage�� NavigationPage|
|NavigationPage|����Page�����ڲ�ά����һ������ջ��ͨ��pushPage��popPage��deletePage�ȶԻ���ջ���й���ʹ�÷��ؼ����Խ���pop������ǰ�ڶ��˵�page������ʹ�÷��ؼ����л��ˣ�����Լ̳�CanSwipToHide�ӿڲ�ʹcanSwipToHide����false|
|ViewPagerPage|�ڲ�ʹ��viewpager�����е�page���й���ͨ��switchToPage��addPage��addPages��removePage��ViewPagerPage�ڲ���Page���й���
|SwitchPage|��ViewPagerPage���ƣ�ÿ��ֻ����һ��page������ʾ�����page֮������л�|
|SubSwitchPage|��SwitchPage���ƣ���֮ͬ�����ڣ�SwitchPage�������е�view��������ʾ��������page��ʾ����SubSwitchPage������page��ʾ������ֻ�������view����ʾ�����һ���֣����Դ�page���Ժ�TabLayout���ʹ�ã�ʵ�ֶ�Tab���л�|
