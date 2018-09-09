
package lang;
public class Lang {
  public Lang() {}
/** Goto Перейти  */
  public static final short goto_=0;
/** Reset Сброс  */
  public static final short reset=1;
/** Cancel Отмена  */
  public static final short cancel=2;
/** Back Назад  */
  public static final short back=3;
/** Exit Выход  */
  public static final short exit_button=4;
/** Marks Метки  */
  public static final short marks=5;
/** Save Сохранить  */
  public static final short save=6;
/** OK OK  */
  public static final short ok=7;
/** Close Закрыть  */
  public static final short close=8;
/** GPS on GPS вкл  */
  public static final short gps_on=9;
/** GPS off GPS выкл  */
  public static final short gps_off=10;
/** Routes Маршруты  */
  public static final short routes=11;
/** To mark На метку  */
  public static final short nav2=12;
/** Refresh Обновить  */
  public static final short refresh=13;
/** Settings Настройки  */
  public static final short options=14;
/** Help Помощь  */
  public static final short help=15;
/** Error Ошибка  */
  public static final short error=16;
/** Navigation Навигация  */
  public static final short navigation=17;
/** Full screen Полный экран  */
  public static final short fullscreen=18;
/** Mark saved Метка сохранена  */
  public static final short marksaved=19;
/** Loaded Загружено  */
  public static final short loaded=20;
/** Wait  Ждет  */
  public static final short wait=21;
/** km/h км/ч  */
  public static final short kmh=22;
/** km км  */
  public static final short km=23;
/** File is loading Идет загрузка файла  */
  public static final short routeloading=24;
/** 1 - scale 1 - сетка  */
  public static final short scaleyes=25;
/** 1 - no scale 1 - нет сетки  */
  public static final short scaleno=26;
/** 2 - online 2 - онлайн  */
  public static final short online=27;
/** 2 - offline 2 - оффлайн  */
  public static final short offline=28;
/** 3 - navigation 3 - навигация  */
  public static final short routem=29;
/** 1 - next 1 - следующая  */
  public static final short next1=30;
/** 2 - prev 2 - предыдущая  */
  public static final short prev2=31;
/** 3 - point/route 3 - точка/маршрут  */
  public static final short ptrt=32;
/** 4- with/without labels 4 - с названиями/без  */
  public static final short withnonames=33;
/** Language Язык  */
  public static final short language=34;
/** Load route Загрузить маршрут  */
  public static final short loadroute=35;
/** Close route Закрыть маршрут  */
  public static final short closeroute=36;
/** Delete all Удалить все  */
  public static final short deleteall=37;
/** Add mark Добавить метку  */
  public static final short addmark=38;
/** File access is not available! Доступ к файлам не поддерживается!  */
  public static final short nofileaccess=39;
/** To load route or waypoints you have to enter its address, like\nwww.mysite.com/route.rte\nor enter path to local file in phone memory or card, like\nc:/other/lake.rte\nDon't forget to check right position after - Internet or File.\nList of available roots:\n Для загрузки маршрута или путевых точек нужно указать его адрес в Интернете, вида\nwww.mysite.com/route.rte\nлибо, указать путь к файлу в памяти телефона, вида\nc:/other/lake.rte\nНе забудьте установить переключатель в нужное положение - Интернет или Файл.\nСписок доступных корневых папок:\n  */
  public static final short filehelp=40;
/** Connection error Ошибка связи  */
  public static final short errorcon=41;
/** Map is loading Загрузка карты  */
  public static final short mapload=42;
/** Surface is loading Идет загрузка поверхности  */
  public static final short surfaceload=43;
/** Latitude Широта  */
  public static final short latitude=44;
/** Longitude Долгота  */
  public static final short longitude=45;
/** Test Тест  */
  public static final short test=46;
/** CacheClear КешОчистка  */
  public static final short clearcache=47;
/** Additional Дополнительно  */
  public static final short advanceopt=48;
/** Scaled preview Масштабирование просмотра  */
  public static final short preview=49;
/** Parallel download Параллельная загрузка  */
  public static final short parallelload=50;
/** In-memory image pool Буфер изображений в памяти  */
  public static final short imagebuf=51;
/** Image cache size: Размер кеша изображений:  */
  public static final short imagecachesize=52;
/** Available for map and cache: Свободно для карт и кеша:  */
  public static final short imagecachefree=53;
/** Enter mark name Укажите название метки  */
  public static final short spmarkname=54;
/** Load from Загрузить из  */
  public static final short loadfrom=55;
/** URL or filename URL или имя файла  */
  public static final short urlfile=56;
/** Internet Интернет  */
  public static final short internet=57;
/** File Файл  */
  public static final short file=58;
/** How to load Как загрузить  */
  public static final short howload=59;
/** Create Создать  */
  public static final short create=60;
/** Select Выбрать  */
  public static final short select=61;
/** Delete Удалить  */
  public static final short delete=62;
/** Load Загрузить  */
  public static final short load=63;
/** Connect Соединить  */
  public static final short connect=64;
/** Searching... Идет поиск...  */
  public static final short searchgoes=65;
/** Search done Поиск завершен  */
  public static final short searchdone=66;
/** Connecting... Соединяем...  */
  public static final short connecting=67;
/** Info Информация  */
  public static final short info=68;
/** About О программе  */
  public static final short about=69;
/** Map Mobile Navigator\n\nGPS navigation system for J2ME\n Мобильный навигатор карт\n\nGPS навигатор для J2ME\n  */
  public static final short about_info=70;
/** Keys: Клавиши:  */
  public static final short helplabel=71;
/** \n 1 - zoom in 2x\n 2 - change screen\n 3 - zoom out 2x\n 4 - landscape view\n 5 - Bind to GPS position / map scroll\n 7 - zoom in 8x\n 8 - change screen\n 9 - zoom out 8x\n * - quick menu\n 0 - change map source (defined in Settings-Maps)\n # - change map/surface \n 1 - Увеличение 2х\n 2 - Переключение экранов\n 3 - Уменьшение 2х\n 4 - Поворот экрана\n 5 - Привязка к GPS / перемещение по карте\n 6 - Управление подсветкой\n 7 - Увеличение 8х\n 8 - Переключение экранов\n 9 - Уменьшение 8х\n * - Быстрое меню\n 0 - Переключение источников карт\n # - Переключение карта/поверхность   */
  public static final short help_info=72;
/** Waypoints Пут.точки  */
  public static final short waypoints=73;
/** Load waypoints Загрузить пут.точки  */
  public static final short loadwp=74;
/** Close waypoints Закрыть пут.точки  */
  public static final short closewp=75;
/** Attention Внимание  */
  public static final short attention=76;
/** Changes saved!\nDue memory optimization changes take effect fully after application restart only Настройки сохранены!\nИзменения полность вступят в силу только после перезапуска приложения.  */
  public static final short chrestart=77;
/** Map sending... Отсылаем карту...  */
  public static final short mapsending=78;
/** Sent Отправлено  */
  public static final short sent=79;
/** File saved successfully Файл сохранен успешно  */
  public static final short filesaved=80;
/** MMS sent successfully MMS-cообщение отправлено успешно  */
  public static final short mmssent=81;
/** Phone number or email Номер телефона или e-mail  */
  public static final short sendaddrmms=82;
/** Path to save Путь для сохранения  */
  public static final short sendaddrfile=83;
/** Image size Размер изображения  */
  public static final short picsize=84;
/** Screenshot Снимок экрана  */
  public static final short takescreen=85;
/** Send Послать  */
  public static final short send=86;
/** Action Сделать  */
  public static final short action=87;
/** More.. Еще..  */
  public static final short more=88;
/** Exit Выход  */
  public static final short exit=89;
/** Map Карта  */
  public static final short map=90;
/** Compass Компас  */
  public static final short compass=91;
/** Max speed Макс. скорость  */
  public static final short maxmsp=92;
/** Min speed Мин. скорость  */
  public static final short minmsp=93;
/** Max altitude Макс. высота  */
  public static final short maxal=94;
/** Min altitude Мин. высота  */
  public static final short minal=95;
/** Track elevation Профиль трека  */
  public static final short trheight=96;
/** Hide profile Скрыть профиль  */
  public static final short notrheight=97;
/** Tracks Треки  */
  public static final short tracks=98;
/** Start track Начать запись  */
  public static final short starttr=99;
/** Safe mode Безопасный режим  */
  public static final short safemode=100;
/** Font style Стиль шрифта  */
  public static final short fontstyle=101;
/** Font size Размер шрифта  */
  public static final short fontsize=102;
/** Large Большой  */
  public static final short large=103;
/** Normal Нормальный  */
  public static final short normal=104;
/** Small Малый  */
  public static final short small=105;
/** Bold Полужирный  */
  public static final short bold=106;
/** Track write period Период записи трека  */
  public static final short trackperiod=107;
/** Close track Закрыть трек  */
  public static final short closetr=108;
/** Opened Открыто  */
  public static final short opened=109;
/** Points count: Количество точек:  */
  public static final short pointscount=110;
/** Reading map: Читаем карту:  */
  public static final short maploading=111;
/** Sending Отправляется  */
  public static final short sending=112;
/** Input phone number Введите номер телефона  */
  public static final short plinptelnum=113;
/** Phone number Номер телефона  */
  public static final short telnum=114;
/** Send coordinates Отправить координаты  */
  public static final short sendcsms=115;
/** Next Дальше  */
  public static final short next=116;
/** Enter message text Введите сообщение  */
  public static final short entershtext=117;
/** Wait SMS Ждать SMS  */
  public static final short waitsms=118;
/** Stop SMS Стоп SMS  */
  public static final short stopsms=119;
/** SMS Coordinates SMS Координаты  */
  public static final short smscoords=120;
/** sec сек  */
  public static final short sec=121;
/** min мин  */
  public static final short min=122;
/** m м  */
  public static final short m=123;
/** Record track by Запись трека по  */
  public static final short trackopt=124;
/** time period периоду времени  */
  public static final short usetrtime=125;
/** min distance мин расстоянию  */
  public static final short usetrdist=126;
/** Next point min distance Мин расстояние до след. точки  */
  public static final short trackdist=127;
/** Track Трек  */
  public static final short track=128;
/** General Основное  */
  public static final short general=129;
/** Cache Кеш  */
  public static final short cache=130;
/** Setup your language Выберите ваш язык  */
  public static final short setlang=131;
/** Show location as Показать координаты как  */
  public static final short coordtype=132;
/** Enter map file name with path Укажите полный путь к файлу и его имя  */
  public static final short entermn=133;
/** Available roots: Доступные корневые папки:  */
  public static final short roots=134;
/** Show track points Показывать точки трека  */
  public static final short trackpc=135;
/** last 100 последние 100  */
  public static final short last100=136;
/** all (slow!) все (медленно!)  */
  public static final short all=137;
/** Enable Включить  */
  public static final short enable=138;
/** Debug options Параметры отладки  */
  public static final short debugopt=139;
/** Debug Отладка  */
  public static final short debug=140;
/** Log Лог  */
  public static final short log=141;
/** Clear Очистить  */
  public static final short clear=142;
/** Backlight On Подсветка Вкл  */
  public static final short lighton=143;
/** Map Image Size: Размер загруженной карты:  */
  public static final short mapimagesize=144;
/** Open Открыть  */
  public static final short open=145;
/** Browse Указать...  */
  public static final short browse=146;
/** Browse file Указать файл  */
  public static final short browsefile=147;
/** Browse dir Указать папку  */
  public static final short browsedir=148;
/** Show coordinates Показывать координаты  */
  public static final short showcoord=149;
/** Surface Поверх  */
  public static final short surface=150;
/** Position Положение  */
  public static final short position=151;
/** Points Точек  */
  public static final short points=152;
/** Length Длина  */
  public static final short totdist=153;
/** Av. Speed Ср. скор.  */
  public static final short avgspeed=154;
/** Speed Скорость  */
  public static final short speed=155;
/** Track Info Трек Инфо  */
  public static final short trackstat=156;
/** No active track Трек неактивен  */
  public static final short noacttrack=157;
/** Show Показать  */
  public static final short show=158;
/** Hide Спрятать  */
  public static final short hide=159;
/** Blink Мигать  */
  public static final short blinklight=160;
/** GeoCache Info GeoCache ближ.  */
  public static final short getgeocache=161;
/** Connecting... Подключаем...  */
  public static final short gpsautoconn=162;
/** Alerts Оповещения  */
  public static final short alerts=163;
/** Sounds on Звуки вкл  */
  public static final short sndon=164;
/** Satellites status Спутники сост  */
  public static final short sndsat=165;
/** Sounds Звуки  */
  public static final short sounds=166;
/** Example Пример  */
  public static final short example=167;
/** Follow format example Следуйте примеру координат  */
  public static final short followf=168;
/** Last Последние  */
  public static final short last=169;
/** New Новый  */
  public static final short new_=170;
/** Saved Сохранено  */
  public static final short saved=171;
/** Deleted Удалено  */
  public static final short deleted=172;
/** Tiles Блоков  */
  public static final short tiles=173;
/** Some values are changed. Save changes? Настройки изменены. Сохранить изменения?  */
  public static final short savech=174;
/** Changes Изменения  */
  public static final short changed=175;
/** On Вкл  */
  public static final short on=176;
/** Off Выкл  */
  public static final short off=177;
/** No available users Нет доступных пользователей  */
  public static final short noradaruser=178;
/** NetRadar traffic NetRadar трафик  */
  public static final short radarbytes=179;
/** Map correct mode Режим корректировки карты  */
  public static final short mapcorrect=180;
/** File not found Файл не найден  */
  public static final short filenotfnd=181;
/** Navigate to Провести к  */
  public static final short nav2u=182;
/** Users Пользователи  */
  public static final short users=183;
/** 4 - users 4 - пользователи  */
  public static final short usersm=184;
/** Real size Настоящий размер  */
  public static final short realsize=185;
/** Fox Hunter Охота На Лис  */
  public static final short foxhunter=186;
/** Travel Пройдено  */
  public static final short travel=187;
/** Not selected Не выбран  */
  public static final short nosel=188;
/** Save track Сохранить трек  */
  public static final short savetrack=189;
/** Log2File ЛогФайл  */
  public static final short savefile=190;
/** Log2Site ЛогСайт  */
  public static final short savesite=191;
/** Maps Карты  */
  public static final short maps=192;
/** Use maps: Использовать карты:  */
  public static final short mapstorotate=193;
/** Bind Привязать  */
  public static final short bind2u=194;
/** Unbind Отвязать  */
  public static final short unbind=195;
/** 5 - no rotating 5 - не вращается  */
  public static final short norotating=196;
/** 5 - rotating 5 - вращается  */
  public static final short rotating=197;
/** Limit rotate image size Ограничить размер вращ. изображения  */
  public static final short limitrot=198;
/** Default track Трек по-умолчанию  */
  public static final short trackautostart=199;
/** Hold background connection Фоновое соединение  */
  public static final short connhold=200;
/** Waypoints proximity Радиус путевых точек  */
  public static final short wayptsprox=201;
/** Autoselect next waypoint Автопереключение путевых точек  */
  public static final short autoselectwpt=202;
/** 6 - compass over 6 - компас поверх  */
  public static final short compassmap=203;
/** 6 - no compass 6 - без компаса  */
  public static final short nocompassmap=204;
/** Progress Выполнено  */
  public static final short progress=205;
/** Yes Да  */
  public static final short yes=206;
/** No Нет  */
  public static final short no=207;
/** course change изменению курса  */
  public static final short usetrturn=208;
/** Latest version available Последняя доступная версия  */
  public static final short lastver=209;
/** Available Доступно  */
  public static final short available=210;
/** Required Требуется  */
  public static final short required=211;
/** Connection type Тип соединения  */
  public static final short conntype=212;
/** Port Порт  */
  public static final short port=213;
/** Auto reconnect Авто переподключение  */
  public static final short autoreconn=214;
/** Points list Список точек  */
  public static final short pointslist=215;
/** Edit Правка  */
  public static final short edit=216;
/** Latest debug version Последняя версия отладки  */
  public static final short lastdeb=217;
/** Point Edit Правка точки  */
  public static final short editpoint=218;
/** Icon Значок  */
  public static final short icon=219;
/** Altitude Высота  */
  public static final short altitude=220;
/** Rename Переименовать  */
  public static final short rename=221;
/** Label Название  */
  public static final short label=222;
/** Custom Другой  */
  public static final short custom=223;
/** Forecolor Цвет  */
  public static final short forecolor=224;
/** Backcolor Фон  */
  public static final short backcolor=225;
/** Clone Клонировать  */
  public static final short clone=226;
/** Insert Вставить  */
  public static final short insert=227;
/** Up Вверх  */
  public static final short up=228;
/** Loading Загружает  */
  public static final short loading=229;
/** Export Экспорт  */
  public static final short export=230;
/** Free memory Свободно памяти  */
  public static final short freemem=231;
/** Turn on Bluetooth Включите Bluetooth  */
  public static final short turnblueon=232;
/** Built-in Встроенный  */
  public static final short builtin=233;
/** Route Маршрут  */
  public static final short route=234;
/** Save NMEA Писать NMEA  */
  public static final short savenmea=235;
/** Save map correct Сохранить коррекцию карты  */
  public static final short savemapcorrect=236;
/** Interval Интервал  */
  public static final short interval=237;
/** Count Количество  */
  public static final short count=238;
/** Display datum Отображать систему координат  */
  public static final short dispdatum=239;
/** Light Свет  */
  public static final short light=240;
/** Precise Точность  */
  public static final short precise=241;
/** Precise position Уточнение положения  */
  public static final short precpos=242;
/** Max deviation Макс отклонение  */
  public static final short maxdev=243;
/** m/s м/с  */
  public static final short mps=244;
/** Odometer Одометр  */
  public static final short odometer=245;
/** Time Время  */
  public static final short time=246;
/** Distance Расстояние  */
  public static final short distance=247;
/** Acceleration Ускорение  */
  public static final short curA=248;
/** m/s2 м/c2  */
  public static final short ms2=249;
/** Altitude speed Верт. скорость  */
  public static final short curAS=250;
/** Min acceleration Мин. ускорение  */
  public static final short minA=251;
/** Max acceleration Маск. ускорение  */
  public static final short maxA=252;
/** Max alt speed Макс. верт. скорость  */
  public static final short maxAS=253;
/** Min alt speed Мин. верт. скорость  */
  public static final short minAS=254;
/** Track speed Скорость трека  */
  public static final short trspeed=255;
/** External map Внешняя карта  */
  public static final short extmap=256;
/** Hybrid Гибрид  */
  public static final short hybrid=257;
/** Clear default track Очищать трек по-умолчанию  */
  public static final short cleandeftr=258;
/** Correct for all maps Коррекция всех карт  */
  public static final short mapcorrectall=259;
/** BT devices BT устройства  */
  public static final short btdevices=260;
/** Add device Добавить устройство  */
  public static final short adddevice=261;
/** Use cached Использ. кеш  */
  public static final short gotoc=262;
/** Geo Info Геоданные  */
  public static final short geoinfo=263;
/** Projection Проекция  */
  public static final short dispproj=264;
/** Geodetic Геодезическая  */
  public static final short geodetic=265;
/** Please, wait for search end Пожалуйста, подождите окончания поиска  */
  public static final short waitbtend=266;
/** Save changes? Сохранить изменения?  */
  public static final short savechng=267;
/** Azimuth Азимут  */
  public static final short azimut=268;
/** Current position Текущее положение  */
  public static final short currpos=269;
/** Use azimuth Исполь. азимут  */
  public static final short useazimut=270;
/** What position Какое положение  */
  public static final short whatpos=271;
/** Code page Кодировка  */
  public static final short codepage=272;
/** Format Формат  */
  public static final short format=273;
/** My POI (gps-club.ru) Мои POI (gps-club.ru)  */
  public static final short mypoi=274;
/** Satellites Спутники  */
  public static final short satellites=275;
/** Check debug update Проверять отл. версии  */
  public static final short tellnewdebugv=276;
/** Saving Сохраняем  */
  public static final short saving=277;
/** Wait… Ждите…  */
  public static final short waitpls=278;
/** Hold add Удержание добавляет  */
  public static final short hold5add=279;
/** Add Minimize Добавить Свернуть  */
  public static final short addminimize=280;
/** Keys Клавиши  */
  public static final short keys=281;
/** Minimize Свернуть  */
  public static final short minimize=282;
/** External maps Внешние карты  */
  public static final short extmaps=283;
/** Add map Добавить карту  */
  public static final short addmap=284;
/** Use key # to change active map Используйте клавишу # для смены активной карты  */
  public static final short usekeycm=285;
/** Work Path Рабочая папка  */
  public static final short workpath=286;
/** Autocenter Автоцентрирование  */
  public static final short autocenter=287;
/** System Система  */
  public static final short system=288;
/** Total memory Всего памяти  */
  public static final short memtotal=289;
/** Used memory Использовано  */
  public static final short memused=290;
/** Vibration Вибрация  */
  public static final short vibration=291;
/** Reverse Обратить  */
  public static final short reverse=292;
/** Threads Потоков  */
  public static final short threads=293;
/** New track point Новая точка трека  */
  public static final short soundontp=294;
/** Cross Перекрестие  */
  public static final short cross=295;
/** Internal maps Внутренние карты  */
  public static final short intmaps=296;
/** 7 - GPS 7 - GPS  */
  public static final short gpsqm=297;
/** 1 - Track recording 1 - Трек пишется  */
  public static final short gpsqmtrrec=298;
/** 1 - Тrack stopped 1 - Трек остановлен  */
  public static final short gpsqmtrstop=299;
/** 2 - Relaxed data 2 - Усреднение данных  */
  public static final short gpsqmrelaxed=300;
/** 2 - Raw data 2 - Сырые данные  */
  public static final short gpsqmrawdata=301;
/** Track autosave Автосохранение трека  */
  public static final short trautosave=302;
/** Transparent pointer Прозрачный указатель  */
  public static final short transppointer=303;
/** Smooth scroll Плавное перемещение  */
  public static final short smoothscroll=304;
/** To next only На следующую только  */
  public static final short autowptnext=305;
/** To nearest next На ближайшую следующую  */
  public static final short autowptnear=306;
/** All Всех  */
  public static final short allexport=307;
/** Selected Выбранного  */
  public static final short selexport=308;
/** Map font Шрифт карты  */
  public static final short mapfont=309;
/** Shadow Тень  */
  public static final short shadow=310;
/** Appearance Внешний вид  */
  public static final short appearance=311;
/** View Вид  */
  public static final short view=312;
/** 2 - viewport 2 - окно  */
  public static final short splitview2=313;
/** Down Вниз  */
  public static final short down=314;
/** Recommend a friend Рассказать другу  */
  public static final short recfriend=315;
/** Friend's phone Телефон друга  */
  public static final short entfrtel=316;
/** Your name Ваше имя  */
  public static final short yourname=317;
/** Try MapNav - free mobile application for real GPS navigation! Попробуй MapNav - бесплатное приложение для GPS навигации!  */
  public static final short rectext=318;
/** Light on lock Свет при блокировке  */
  public static final short lightlock=319;
/** Opaque text Матовый текст  */
  public static final short mattetext=320;
/** Play Воспроизвести  */
  public static final short play=321;
/** Preload Прогрузить  */
  public static final short preload=322;
/** Messages Сообщения  */
  public static final short messages=323;
/** Message for Сообщение для  */
  public static final short messfor=324;
/** Send message Послать сообщение  */
  public static final short sendmes=325;
/** Reply Ответить  */
  public static final short reply=326;
/** Level Уровень  */
  public static final short level=327;
/** Metric Метрические  */
  public static final short metric=328;
/** Nautical Морские  */
  public static final short nautical=329;
/** Imperial Британские  */
  public static final short imperial=330;
/** Measure units Единицы измерения  */
  public static final short measureunits=331;
/** Landscape mode Альбомный режим  */
  public static final short landscape=332;
/** Zoom out Масштаб -  */
  public static final short zoomout=333;
/** Zoom in Масштаб +  */
  public static final short zoomin=334;
/** Map source Источник карт  */
  public static final short mapsrc=335;
/** Track cross Крест на треке  */
  public static final short trackcross=336;
/** Nearest WP Ближ. пут. точка  */
  public static final short nearestwpt=337;
/** Send Netradar Послать NetRadar  */
  public static final short sendnetradar=338;
/** Cache index Кешировать индекс  */
  public static final short cacheindex=339;
/** Key  Клавиша   */
  public static final short key=340;
/** Key hold  Удержание   */
  public static final short keyhold=341;
/** Merge Объединить  */
  public static final short merge=342;
/** Realtime В реальном времени  */
  public static final short realtimenr=343;
/** Show track Показывать трек  */
  public static final short showtrack=344;
/** Delay Опоздание  */
  public static final short delay=345;
/** Points to start new Точек для старта нового  */
  public static final short points2startnew=346;
/** Donate MapNav Поддержать MapNav  */
  public static final short donate=347;
/** Limit display when rotate Ограничить показ при вращении  */
  public static final short limitshowtrack=348;
/** Volume Громкость  */
  public static final short volume=349;
/** Search Поиск  */
  public static final short search=350;
/** Place Место  */
  public static final short place=351;
/** Results Результаты  */
  public static final short results=352;
/** Speed color Цвет по скорости  */
  public static final short coloredtrack=353;
/** Inverse map Ночная карта  */
  public static final short inversemap=354;
/** Altitude gain Набор высоты  */
  public static final short altgain=355;
/** Altitude lost Потеря высоты  */
  public static final short altlost=356;
/** Clear on reload Чистить при обновлении  */
  public static final short clearwithreload=357;
/** Use file cache Файловый кэш  */
  public static final short usefilecache=358;
/** File cache path Путь к кешу  */
  public static final short filecachepath=359;
/** Clock on map Часы на карте  */
  public static final short showclock=360;
/** Directions Направления  */
  public static final short directions=361;
/** Variometer Вариометр  */
  public static final short variometer=362;
/** Climb/Descent Подъем/спуск  */
  public static final short updownsound=363;
/** Climb max speed, m/s Max скорость подъема, м/с  */
  public static final short upwarnspeed=364;
/** Descent max speed, m/s Max скорость спуска, м/с  */
  public static final short downwarnspeed=365;
/** Screen Экран  */
  public static final short screen=366;
/** Record voice… Запись голоса…  */
  public static final short recordvoice=367;
/** Track backup to Work Path Сохранение трека в Рабочую папку  */
  public static final short trackbackup=368;
/** Sunrise Восход  */
  public static final short sunrise=369;
/** Sunset Заход  */
  public static final short sunset=370;
/** Sun Солнце  */
  public static final short sun=371;

  
}