package b4a.example;

import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
		BA.handler.postDelayed(new WaitForLayout(), 5);

	}
	private static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
		return true;
	}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
		this.setIntent(intent);
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.Timer _timer1 = null;
public static anywheresoftware.b4a.objects.Timer _timer2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblmade = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblmoney = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbltimepassed = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblwage = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblrandom = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtwage = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnstart = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnreset = null;
public static double _dblwage = 0;
public static double _dblsec = 0;
public anywheresoftware.b4a.objects.SpinnerWrapper _spncurrency = null;
public anywheresoftware.b4a.phone.Phone _p = null;
public static long _timestart = 0L;
public static long _timepassedsincestart = 0L;
public static long _timeidled = 0L;
public static long _totaltime = 0L;
public static long _moneymade = 0L;
public static int _sliderpos = 0;
public static long _time1 = 0L;
public static long _time2 = 0L;
public static long _timepassed = 0L;
public static double _dblval = 0;
public static String _strmoney = "";
public static String _strkey = "";
public static double _dblconvert = 0;
public static String _strcurrency = "";
public anywheresoftware.b4a.objects.collections.List _listkey = null;
public anywheresoftware.b4a.objects.collections.List _listval = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnsettings = null;
public de.amberhome.objects.preferenceactivity.PreferenceScreenWrapper _prefscreen = null;
public de.amberhome.objects.preferenceactivity.PreferenceManager _prefman = null;
public anywheresoftware.b4a.objects.collections.List _listmancurr = null;
public b4a.example.statemanager _statemanager = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 217;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 221;BA.debugLine="listManCurr.Initialize";
mostCurrent._listmancurr.Initialize();
 //BA.debugLineNum = 222;BA.debugLine="listManCurr.AddAll(Array As String(\"$\", \"€\", \"£\"))";
mostCurrent._listmancurr.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"$","€","£"}));
 //BA.debugLineNum = 224;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 225;BA.debugLine="SetDefaults";
_setdefaults();
 //BA.debugLineNum = 226;BA.debugLine="CreatePreferenceScreen";
_createpreferencescreen();
 };
 //BA.debugLineNum = 231;BA.debugLine="Activity.LoadLayout(\"lytMain\")";
mostCurrent._activity.LoadLayout("lytMain",mostCurrent.activityBA);
 //BA.debugLineNum = 232;BA.debugLine="Timer1.Initialize(\"Timer1\", 1000)";
_timer1.Initialize(processBA,"Timer1",(long) (1000));
 //BA.debugLineNum = 233;BA.debugLine="Timer2.Initialize(\"Timer2\", 30000)";
_timer2.Initialize(processBA,"Timer2",(long) (30000));
 //BA.debugLineNum = 235;BA.debugLine="ListKey.Initialize";
mostCurrent._listkey.Initialize();
 //BA.debugLineNum = 236;BA.debugLine="ListVal.Initialize";
mostCurrent._listval.Initialize();
 //BA.debugLineNum = 238;BA.debugLine="ListKey.AddAll(Array As String(\"Cristiano Ronaldo\", \"Gareth Bale\", \"Madonna\", \"Donald Trump\", \"Ralph Lauren\", \"Barrack Obama\", \"Michael Jordan\", \"Shaquille O'Neal\", \"Kobe Bryant\", \"LeBron James\", \"Queen Elizabeth\", \"Warren Buffett\", \"Tony Blair\", \"Lady Gaga\", \"Eminem\", \"Johnny Depp\", \"Will Smith\", \"Katy Perry\", \"Beyonce\", \"Leonardo DiCaprio\", \"Roger Federer\", \"Lionel Messi\", \"Shakira\", \"Rihanna\", \"Adele\", \"Nicolas Cage\", \"Nicki Minaj\", \"Tom Cruise\", \"Naomi Campbell\", \"George Clooney\", \"Suzanne Collins\", \"Taylor Swift\", \"Britney Spears\", \"Hugh Jackman\", \"Bill Gates\", \"Mark Zuckerberg\"))";
mostCurrent._listkey.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"Cristiano Ronaldo","Gareth Bale","Madonna","Donald Trump","Ralph Lauren","Barrack Obama","Michael Jordan","Shaquille O'Neal","Kobe Bryant","LeBron James","Queen Elizabeth","Warren Buffett","Tony Blair","Lady Gaga","Eminem","Johnny Depp","Will Smith","Katy Perry","Beyonce","Leonardo DiCaprio","Roger Federer","Lionel Messi","Shakira","Rihanna","Adele","Nicolas Cage","Nicki Minaj","Tom Cruise","Naomi Campbell","George Clooney","Suzanne Collins","Taylor Swift","Britney Spears","Hugh Jackman","Bill Gates","Mark Zuckerberg"}));
 //BA.debugLineNum = 239;BA.debugLine="ListVal.AddAll(Array As Int(1940, 2715, 15140, 6850, 3000, 50, 6640, 1660, 2530, 1580, 8300, 5140, 70, 9690, 4720, 3630, 2790, 4720, 6420, 4720, 8660, 5000, 5330, 5200, 3630, 2850, 3510, 190850, 4240, 100, 2300, 6660, 6660, 7020, 6660, 117000, 190850))";
mostCurrent._listval.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new int[]{(int) (1940),(int) (2715),(int) (15140),(int) (6850),(int) (3000),(int) (50),(int) (6640),(int) (1660),(int) (2530),(int) (1580),(int) (8300),(int) (5140),(int) (70),(int) (9690),(int) (4720),(int) (3630),(int) (2790),(int) (4720),(int) (6420),(int) (4720),(int) (8660),(int) (5000),(int) (5330),(int) (5200),(int) (3630),(int) (2850),(int) (3510),(int) (190850),(int) (4240),(int) (100),(int) (2300),(int) (6660),(int) (6660),(int) (7020),(int) (6660),(int) (117000),(int) (190850)}));
 //BA.debugLineNum = 241;BA.debugLine="spnCurrency.AddAll(Array As String(\"$\", \"€\", \"£\"))";
mostCurrent._spncurrency.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"$","€","£"}));
 //BA.debugLineNum = 242;BA.debugLine="spnCurrency.SelectedIndex = 0";
mostCurrent._spncurrency.setSelectedIndex((int) (0));
 //BA.debugLineNum = 244;BA.debugLine="txtWage.SingleLine = True";
mostCurrent._txtwage.setSingleLine(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 246;BA.debugLine="lblTimepassed.Gravity = Gravity.CENTER";
mostCurrent._lbltimepassed.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 247;BA.debugLine="lblTimepassed.Text = \"00:00:00\"";
mostCurrent._lbltimepassed.setText((Object)("00:00:00"));
 //BA.debugLineNum = 248;BA.debugLine="lblMoney.Gravity = Gravity.CENTER";
mostCurrent._lblmoney.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 249;BA.debugLine="lblRandom.Gravity = Gravity.CENTER";
mostCurrent._lblrandom.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 250;BA.debugLine="DateTime.SetTimeZone(0)";
anywheresoftware.b4a.keywords.Common.DateTime.SetTimeZone((int) (0));
 //BA.debugLineNum = 252;BA.debugLine="If StateManager.GetSetting(\"Wage\") = 0 OR StateManager.GetSetting(\"Wage\") = \"\" Then";
if ((mostCurrent._statemanager._getsetting(mostCurrent.activityBA,"Wage")).equals(BA.NumberToString(0)) || (mostCurrent._statemanager._getsetting(mostCurrent.activityBA,"Wage")).equals("")) { 
 //BA.debugLineNum = 253;BA.debugLine="strCurrency = \"$\"";
mostCurrent._strcurrency = "$";
 //BA.debugLineNum = 254;BA.debugLine="MoneyMade = 0";
_moneymade = (long) (0);
 }else {
 //BA.debugLineNum = 256;BA.debugLine="btnStart.Text = \"Press screen to pause\"";
mostCurrent._btnstart.setText((Object)("Press screen to pause"));
 //BA.debugLineNum = 257;BA.debugLine="TimeStart = StateManager.GetSetting2(\"TimeStart\", 0)";
_timestart = (long)(Double.parseDouble(mostCurrent._statemanager._getsetting2(mostCurrent.activityBA,"TimeStart",BA.NumberToString(0))));
 //BA.debugLineNum = 258;BA.debugLine="dblSec = StateManager.GetSetting2(\"Sec\", 0)";
_dblsec = (double)(Double.parseDouble(mostCurrent._statemanager._getsetting2(mostCurrent.activityBA,"Sec",BA.NumberToString(0))));
 //BA.debugLineNum = 259;BA.debugLine="dblWage = StateManager.GetSetting2(\"Wage\", 0)";
_dblwage = (double)(Double.parseDouble(mostCurrent._statemanager._getsetting2(mostCurrent.activityBA,"Wage",BA.NumberToString(0))));
 //BA.debugLineNum = 260;BA.debugLine="TimeIdled = StateManager.GetSetting2(\"TimeIdled\", 0)";
_timeidled = (long)(Double.parseDouble(mostCurrent._statemanager._getsetting2(mostCurrent.activityBA,"TimeIdled",BA.NumberToString(0))));
 //BA.debugLineNum = 261;BA.debugLine="Time1 = StateManager.GetSetting2(\"Time1\", 0)";
_time1 = (long)(Double.parseDouble(mostCurrent._statemanager._getsetting2(mostCurrent.activityBA,"Time1",BA.NumberToString(0))));
 //BA.debugLineNum = 262;BA.debugLine="TotalTime = StateManager.GetSetting2(\"TotalTime\", 0)";
_totaltime = (long)(Double.parseDouble(mostCurrent._statemanager._getsetting2(mostCurrent.activityBA,"TotalTime",BA.NumberToString(0))));
 //BA.debugLineNum = 263;BA.debugLine="MoneyMade = StateManager.GetSetting2(\"MoneyMade\", 0)";
_moneymade = (long)(Double.parseDouble(mostCurrent._statemanager._getsetting2(mostCurrent.activityBA,"MoneyMade",BA.NumberToString(0))));
 //BA.debugLineNum = 265;BA.debugLine="txtWage.Text = dblWage";
mostCurrent._txtwage.setText((Object)(_dblwage));
 //BA.debugLineNum = 267;BA.debugLine="If Time1 = 0 Then";
if (_time1==0) { 
 }else {
 //BA.debugLineNum = 270;BA.debugLine="Time2 = (DateTime.now/1000)";
_time2 = (long) ((anywheresoftware.b4a.keywords.Common.DateTime.getNow()/(double)1000));
 //BA.debugLineNum = 271;BA.debugLine="Timepassed = Time2 - Time1";
_timepassed = (long) (_time2-_time1);
 //BA.debugLineNum = 272;BA.debugLine="TimeIdled = TimeIdled + Timepassed";
_timeidled = (long) (_timeidled+_timepassed);
 //BA.debugLineNum = 273;BA.debugLine="StateManager.SetSetting(\"TimeIdled\", TimeIdled)";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"TimeIdled",BA.NumberToString(_timeidled));
 //BA.debugLineNum = 274;BA.debugLine="StateManager.SetSetting(\"Time1\", 0)";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"Time1",BA.NumberToString(0));
 };
 //BA.debugLineNum = 277;BA.debugLine="Timer1.Enabled = True";
_timer1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 278;BA.debugLine="Timer2.Enabled = True";
_timer2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 279;BA.debugLine="RandomPick";
_randompick();
 };
 //BA.debugLineNum = 282;BA.debugLine="dblConvert = StateManager.GetSetting2(\"Conversion\", 0.84)";
_dblconvert = (double)(Double.parseDouble(mostCurrent._statemanager._getsetting2(mostCurrent.activityBA,"Conversion",BA.NumberToString(0.84))));
 //BA.debugLineNum = 283;BA.debugLine="SliderPos = StateManager.GetSetting2(\"SlnPos\", 0)";
_sliderpos = (int)(Double.parseDouble(mostCurrent._statemanager._getsetting2(mostCurrent.activityBA,"SlnPos",BA.NumberToString(0))));
 //BA.debugLineNum = 284;BA.debugLine="strCurrency = StateManager.GetSetting2(\"Currency\", \"$\")";
mostCurrent._strcurrency = mostCurrent._statemanager._getsetting2(mostCurrent.activityBA,"Currency","$");
 //BA.debugLineNum = 286;BA.debugLine="spnCurrency.SelectedIndex = SliderPos";
mostCurrent._spncurrency.setSelectedIndex(_sliderpos);
 //BA.debugLineNum = 288;BA.debugLine="lblMoney.Text = strCurrency & MoneyMade";
mostCurrent._lblmoney.setText((Object)(mostCurrent._strcurrency+BA.NumberToString(_moneymade)));
 //BA.debugLineNum = 290;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
int _result = 0;
 //BA.debugLineNum = 150;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean 'ignore";
 //BA.debugLineNum = 152;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 153;BA.debugLine="Dim result As Int";
_result = 0;
 //BA.debugLineNum = 154;BA.debugLine="result = Msgbox2(\"Are you sure you want to quit the application?\", \"Warning\", \"Yes\", \"Cancel\", \"\", Null)";
_result = anywheresoftware.b4a.keywords.Common.Msgbox2("Are you sure you want to quit the application?","Warning","Yes","Cancel","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA);
 //BA.debugLineNum = 155;BA.debugLine="If result = DialogResponse.POSITIVE Then";
if (_result==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 156;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 }else {
 //BA.debugLineNum = 158;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 };
 };
 //BA.debugLineNum = 162;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 357;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 359;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 336;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 338;BA.debugLine="If PrefMan.GetString(\"wage\") = Null Then";
if (mostCurrent._prefman.GetString("wage")== null) { 
 //BA.debugLineNum = 339;BA.debugLine="dblWage = 0";
_dblwage = 0;
 }else {
 //BA.debugLineNum = 341;BA.debugLine="dblWage = PrefMan.GetString(\"wage\")";
_dblwage = (double)(Double.parseDouble(mostCurrent._prefman.GetString("wage")));
 //BA.debugLineNum = 342;BA.debugLine="dblSec = (dblWage / 3600)";
_dblsec = (_dblwage/(double)3600);
 };
 //BA.debugLineNum = 345;BA.debugLine="If MoneyMade = 0 Then";
if (_moneymade==0) { 
 }else {
 //BA.debugLineNum = 347;BA.debugLine="RandomPick";
_randompick();
 };
 //BA.debugLineNum = 350;BA.debugLine="StateManager.SetSetting(\"Wage\", dblWage)";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"Wage",BA.NumberToString(_dblwage));
 //BA.debugLineNum = 351;BA.debugLine="StateManager.SetSetting(\"Sec\", dblSec)";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"Sec",BA.NumberToString(_dblsec));
 //BA.debugLineNum = 354;BA.debugLine="End Sub";
return "";
}
public static String  _btnreset_down() throws Exception{
int _result = 0;
 //BA.debugLineNum = 115;BA.debugLine="Sub btnReset_down";
 //BA.debugLineNum = 117;BA.debugLine="Dim result As Int";
_result = 0;
 //BA.debugLineNum = 119;BA.debugLine="result = Msgbox2(\"Are you sure you want to reset your money made?\", \"Warning\", \"Yes\", \"Cancel\", \"\", Null)";
_result = anywheresoftware.b4a.keywords.Common.Msgbox2("Are you sure you want to reset your money made?","Warning","Yes","Cancel","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA);
 //BA.debugLineNum = 121;BA.debugLine="If result = DialogResponse.positive Then";
if (_result==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 122;BA.debugLine="MoneyMade = 0";
_moneymade = (long) (0);
 //BA.debugLineNum = 123;BA.debugLine="TimePassedSinceStart = 0";
_timepassedsincestart = (long) (0);
 //BA.debugLineNum = 124;BA.debugLine="TimeIdled = 0";
_timeidled = (long) (0);
 //BA.debugLineNum = 125;BA.debugLine="TimeStart = 0";
_timestart = (long) (0);
 //BA.debugLineNum = 126;BA.debugLine="TotalTime = 0";
_totaltime = (long) (0);
 //BA.debugLineNum = 127;BA.debugLine="Time1 = 0";
_time1 = (long) (0);
 //BA.debugLineNum = 128;BA.debugLine="Time2 = 0";
_time2 = (long) (0);
 //BA.debugLineNum = 129;BA.debugLine="lblTimepassed.Text = \"00:00:00\"";
mostCurrent._lbltimepassed.setText((Object)("00:00:00"));
 //BA.debugLineNum = 130;BA.debugLine="lblRandom.Text = \"\"";
mostCurrent._lblrandom.setText((Object)(""));
 //BA.debugLineNum = 131;BA.debugLine="btnStart.Text = \"Press screen to start\"";
mostCurrent._btnstart.setText((Object)("Press screen to start"));
 //BA.debugLineNum = 132;BA.debugLine="StateManager.SetSetting(\"TotalTime\", 0)";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"TotalTime",BA.NumberToString(0));
 //BA.debugLineNum = 133;BA.debugLine="StateManager.SetSetting(\"MoneyMade\", 0)";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"MoneyMade",BA.NumberToString(0));
 //BA.debugLineNum = 134;BA.debugLine="StateManager.SetSetting(\"TimeStart\", 0)";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"TimeStart",BA.NumberToString(0));
 //BA.debugLineNum = 135;BA.debugLine="StateManager.SetSetting(\"Sec\", 0)";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"Sec",BA.NumberToString(0));
 //BA.debugLineNum = 136;BA.debugLine="StateManager.SetSetting(\"Wage\", 0)";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"Wage",BA.NumberToString(0));
 //BA.debugLineNum = 137;BA.debugLine="StateManager.SetSetting(\"TimeIdled\", 0)";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"TimeIdled",BA.NumberToString(0));
 //BA.debugLineNum = 138;BA.debugLine="StateManager.SetSetting(\"Time1\", 0)";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"Time1",BA.NumberToString(0));
 //BA.debugLineNum = 139;BA.debugLine="StateManager.SaveSettings";
mostCurrent._statemanager._savesettings(mostCurrent.activityBA);
 //BA.debugLineNum = 140;BA.debugLine="lblMoney.Text = strCurrency & MoneyMade";
mostCurrent._lblmoney.setText((Object)(mostCurrent._strcurrency+BA.NumberToString(_moneymade)));
 //BA.debugLineNum = 141;BA.debugLine="Timer1.Enabled = False";
_timer1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 142;BA.debugLine="Timer2.Enabled = False";
_timer2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 }else {
 };
 //BA.debugLineNum = 147;BA.debugLine="End Sub";
return "";
}
public static String  _btnsettings_down() throws Exception{
 //BA.debugLineNum = 69;BA.debugLine="Sub btnSettings_Down";
 //BA.debugLineNum = 71;BA.debugLine="StartActivity(PrefScreen.CreateIntent)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._prefscreen.CreateIntent()));
 //BA.debugLineNum = 73;BA.debugLine="End Sub";
return "";
}
public static String  _btnstart_down() throws Exception{
 //BA.debugLineNum = 76;BA.debugLine="Sub btnStart_Down";
 //BA.debugLineNum = 82;BA.debugLine="If Timer1.Enabled = False AND TimeStart = 0 Then";
if (_timer1.getEnabled()==anywheresoftware.b4a.keywords.Common.False && _timestart==0) { 
 //BA.debugLineNum = 83;BA.debugLine="TimeStart = (DateTime.now /1000)";
_timestart = (long) ((anywheresoftware.b4a.keywords.Common.DateTime.getNow()/(double)1000));
 //BA.debugLineNum = 84;BA.debugLine="Timer1.Enabled = True";
_timer1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 85;BA.debugLine="Timer2.Enabled = True";
_timer2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 86;BA.debugLine="StateManager.SetSetting(\"TimeStart\", TimeStart)";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"TimeStart",BA.NumberToString(_timestart));
 //BA.debugLineNum = 89;BA.debugLine="btnStart.Text = \"Press screen to pause\"";
mostCurrent._btnstart.setText((Object)("Press screen to pause"));
 }else if(_timer1.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 91;BA.debugLine="Time2 = (DateTime.now/1000)";
_time2 = (long) ((anywheresoftware.b4a.keywords.Common.DateTime.getNow()/(double)1000));
 //BA.debugLineNum = 92;BA.debugLine="Time1 = StateManager.GetSetting2(\"Time1\", \"0\")";
_time1 = (long)(Double.parseDouble(mostCurrent._statemanager._getsetting2(mostCurrent.activityBA,"Time1","0")));
 //BA.debugLineNum = 93;BA.debugLine="Timepassed = Time2 - Time1";
_timepassed = (long) (_time2-_time1);
 //BA.debugLineNum = 94;BA.debugLine="StateManager.SetSetting(\"Time1\", 0)";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"Time1",BA.NumberToString(0));
 //BA.debugLineNum = 95;BA.debugLine="TimeIdled = TimeIdled + Timepassed";
_timeidled = (long) (_timeidled+_timepassed);
 //BA.debugLineNum = 96;BA.debugLine="StateManager.SetSetting(\"TimeIdled\", TimeIdled)";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"TimeIdled",BA.NumberToString(_timeidled));
 //BA.debugLineNum = 97;BA.debugLine="Timer1.Enabled = True";
_timer1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 98;BA.debugLine="Timer2.Enabled = True";
_timer2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 99;BA.debugLine="btnStart.Text = \"Press screen to pause\"";
mostCurrent._btnstart.setText((Object)("Press screen to pause"));
 }else {
 //BA.debugLineNum = 101;BA.debugLine="Timer1.Enabled = False";
_timer1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 102;BA.debugLine="Timer2.Enabled = False";
_timer2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 103;BA.debugLine="Time1 = (DateTime.Now/1000)";
_time1 = (long) ((anywheresoftware.b4a.keywords.Common.DateTime.getNow()/(double)1000));
 //BA.debugLineNum = 104;BA.debugLine="StateManager.SetSetting(\"Time1\", Time1)";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"Time1",BA.NumberToString(_time1));
 //BA.debugLineNum = 105;BA.debugLine="btnStart.Text = \"Press screen to start\"";
mostCurrent._btnstart.setText((Object)("Press screen to start"));
 //BA.debugLineNum = 106;BA.debugLine="StateManager.SaveSettings";
mostCurrent._statemanager._savesettings(mostCurrent.activityBA);
 };
 //BA.debugLineNum = 110;BA.debugLine="p.hidekeyboard(Activity)";
mostCurrent._p.HideKeyboard(mostCurrent._activity);
 //BA.debugLineNum = 112;BA.debugLine="End Sub";
return "";
}
public static String  _createpreferencescreen() throws Exception{
de.amberhome.objects.preferenceactivity.PreferenceCategoryWrapper _cat1 = null;
 //BA.debugLineNum = 293;BA.debugLine="Sub CreatePreferenceScreen";
 //BA.debugLineNum = 295;BA.debugLine="PrefScreen.Initialize(\"Settings\", \"\")";
mostCurrent._prefscreen.Initialize("Settings","");
 //BA.debugLineNum = 296;BA.debugLine="Dim cat1 As AHPreferenceCategory";
_cat1 = new de.amberhome.objects.preferenceactivity.PreferenceCategoryWrapper();
 //BA.debugLineNum = 297;BA.debugLine="cat1.Initialize(\"Settings\")";
_cat1.Initialize("Settings");
 //BA.debugLineNum = 299;BA.debugLine="cat1.AddEditText2(\"wage\", \"Hourly Wage\", \"\", \"10\", 12290, False, True, Null)";
_cat1.AddEditText2("wage","Hourly Wage","","10",(int) (12290),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.True,BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 300;BA.debugLine="cat1.AddList(\"currency\", \"Currency\", \"\", \"$\", Null, listManCurr)";
_cat1.AddList("currency","Currency","","$",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Null),mostCurrent._listmancurr);
 //BA.debugLineNum = 301;BA.debugLine="cat1.AddCheckBox(\"tips\", \"Enable tips\", \"\", \"\", False, Null)";
_cat1.AddCheckBox("tips","Enable tips","","",anywheresoftware.b4a.keywords.Common.False,BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 303;BA.debugLine="PrefScreen.AddPreferenceCategory(cat1)";
mostCurrent._prefscreen.AddPreferenceCategory(_cat1);
 //BA.debugLineNum = 307;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
statemanager._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _globals() throws Exception{
 //BA.debugLineNum = 22;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 26;BA.debugLine="Dim lblMade As Label";
mostCurrent._lblmade = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim lblMoney As Label";
mostCurrent._lblmoney = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim lblTimepassed As Label";
mostCurrent._lbltimepassed = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Dim lblWage As Label";
mostCurrent._lblwage = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Dim lblRandom As Label";
mostCurrent._lblrandom = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim txtWage As EditText";
mostCurrent._txtwage = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Dim btnStart As Button";
mostCurrent._btnstart = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Dim btnReset As Button";
mostCurrent._btnreset = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Dim dblWage As Double";
_dblwage = 0;
 //BA.debugLineNum = 35;BA.debugLine="Dim dblSec As Double";
_dblsec = 0;
 //BA.debugLineNum = 36;BA.debugLine="Dim spnCurrency As Spinner";
mostCurrent._spncurrency = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 37;BA.debugLine="Dim p As Phone";
mostCurrent._p = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 39;BA.debugLine="Dim TimeStart As Long";
_timestart = 0L;
 //BA.debugLineNum = 40;BA.debugLine="Dim TimePassedSinceStart As Long";
_timepassedsincestart = 0L;
 //BA.debugLineNum = 41;BA.debugLine="Dim TimeIdled As Long";
_timeidled = 0L;
 //BA.debugLineNum = 42;BA.debugLine="Dim TotalTime As Long";
_totaltime = 0L;
 //BA.debugLineNum = 43;BA.debugLine="Dim MoneyMade As Long";
_moneymade = 0L;
 //BA.debugLineNum = 44;BA.debugLine="Dim SliderPos As Int";
_sliderpos = 0;
 //BA.debugLineNum = 46;BA.debugLine="Dim Time1 As Long";
_time1 = 0L;
 //BA.debugLineNum = 47;BA.debugLine="Dim Time2 As Long";
_time2 = 0L;
 //BA.debugLineNum = 48;BA.debugLine="Dim Timepassed As Long";
_timepassed = 0L;
 //BA.debugLineNum = 50;BA.debugLine="Dim dblVal As Double";
_dblval = 0;
 //BA.debugLineNum = 51;BA.debugLine="Dim strMoney As String";
mostCurrent._strmoney = "";
 //BA.debugLineNum = 52;BA.debugLine="Dim strKey As String";
mostCurrent._strkey = "";
 //BA.debugLineNum = 54;BA.debugLine="Dim dblConvert As Double";
_dblconvert = 0;
 //BA.debugLineNum = 55;BA.debugLine="Dim strCurrency As String";
mostCurrent._strcurrency = "";
 //BA.debugLineNum = 57;BA.debugLine="Dim ListKey As List";
mostCurrent._listkey = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 58;BA.debugLine="Dim ListVal As List";
mostCurrent._listval = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 60;BA.debugLine="Dim btnSettings As Button";
mostCurrent._btnsettings = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 61;BA.debugLine="Dim PrefScreen As AHPreferenceScreen";
mostCurrent._prefscreen = new de.amberhome.objects.preferenceactivity.PreferenceScreenWrapper();
 //BA.debugLineNum = 62;BA.debugLine="Dim PrefMan As AHPreferenceManager";
mostCurrent._prefman = new de.amberhome.objects.preferenceactivity.PreferenceManager();
 //BA.debugLineNum = 64;BA.debugLine="Dim listManCurr As List";
mostCurrent._listmancurr = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 66;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 17;BA.debugLine="Dim Timer1 As Timer";
_timer1 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 18;BA.debugLine="Dim Timer2 As Timer";
_timer2 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 20;BA.debugLine="End Sub";
return "";
}
public static String  _randompick() throws Exception{
int _randomnum = 0;
 //BA.debugLineNum = 319;BA.debugLine="Sub RandomPick";
 //BA.debugLineNum = 321;BA.debugLine="Dim Randomnum As Int";
_randomnum = 0;
 //BA.debugLineNum = 323;BA.debugLine="Randomnum = Rnd(0, 36)";
_randomnum = anywheresoftware.b4a.keywords.Common.Rnd((int) (0),(int) (36));
 //BA.debugLineNum = 325;BA.debugLine="strKey = ListKey.Get(Randomnum)";
mostCurrent._strkey = BA.ObjectToString(mostCurrent._listkey.Get(_randomnum));
 //BA.debugLineNum = 326;BA.debugLine="dblVal = ListVal.Get(Randomnum)";
_dblval = (double)(BA.ObjectToNumber(mostCurrent._listval.Get(_randomnum)));
 //BA.debugLineNum = 328;BA.debugLine="strMoney = NumberFormat((dblVal/3600) * TotalTime * dblConvert, 1, 0)";
mostCurrent._strmoney = anywheresoftware.b4a.keywords.Common.NumberFormat((_dblval/(double)3600)*_totaltime*_dblconvert,(int) (1),(int) (0));
 //BA.debugLineNum = 330;BA.debugLine="lblRandom.text = (\"By now, \" & strKey & \" would have made \" & strCurrency & strMoney)";
mostCurrent._lblrandom.setText((Object)(("By now, "+mostCurrent._strkey+" would have made "+mostCurrent._strcurrency+mostCurrent._strmoney)));
 //BA.debugLineNum = 333;BA.debugLine="End Sub";
return "";
}
public static String  _setdefaults() throws Exception{
 //BA.debugLineNum = 310;BA.debugLine="Sub SetDefaults";
 //BA.debugLineNum = 312;BA.debugLine="PrefMan.SetString(\"wage\", \"10\")";
mostCurrent._prefman.SetString("wage","10");
 //BA.debugLineNum = 313;BA.debugLine="PrefMan.SetString(\"currency\", \"$\")";
mostCurrent._prefman.SetString("currency","$");
 //BA.debugLineNum = 314;BA.debugLine="PrefMan.setBoolean(\"tips\", False)";
mostCurrent._prefman.SetBoolean("tips",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 316;BA.debugLine="End Sub";
return "";
}
public static String  _spncurrency_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 188;BA.debugLine="Sub spnCurrency_ItemClick(Position As Int, Value As Object)";
 //BA.debugLineNum = 190;BA.debugLine="If Position = 0 Then";
if (_position==0) { 
 //BA.debugLineNum = 191;BA.debugLine="dblConvert = 1.37";
_dblconvert = 1.37;
 //BA.debugLineNum = 192;BA.debugLine="strCurrency = \"$\"";
mostCurrent._strcurrency = "$";
 }else if(_position==1) { 
 //BA.debugLineNum = 194;BA.debugLine="dblConvert = 1";
_dblconvert = 1;
 //BA.debugLineNum = 195;BA.debugLine="strCurrency = \"€\"";
mostCurrent._strcurrency = "€";
 }else {
 //BA.debugLineNum = 197;BA.debugLine="dblConvert = 0.84";
_dblconvert = 0.84;
 //BA.debugLineNum = 198;BA.debugLine="strCurrency = \"£\"";
mostCurrent._strcurrency = "£";
 };
 //BA.debugLineNum = 201;BA.debugLine="lblMoney.Text = strCurrency & NumberFormat((TotalTime*dblSec), 1, 2)";
mostCurrent._lblmoney.setText((Object)(mostCurrent._strcurrency+anywheresoftware.b4a.keywords.Common.NumberFormat((_totaltime*_dblsec),(int) (1),(int) (2))));
 //BA.debugLineNum = 203;BA.debugLine="StateManager.SetSetting(\"Conversion\", dblConvert)";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"Conversion",BA.NumberToString(_dblconvert));
 //BA.debugLineNum = 204;BA.debugLine="StateManager.SetSetting(\"SlnPos\", Position)";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"SlnPos",BA.NumberToString(_position));
 //BA.debugLineNum = 205;BA.debugLine="StateManager.SetSetting(\"Currency\", Value)";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"Currency",BA.ObjectToString(_value));
 //BA.debugLineNum = 206;BA.debugLine="StateManager.SaveSettings";
mostCurrent._statemanager._savesettings(mostCurrent.activityBA);
 //BA.debugLineNum = 208;BA.debugLine="If lblRandom.Text = \"\" Then";
if ((mostCurrent._lblrandom.getText()).equals("")) { 
 }else {
 //BA.debugLineNum = 210;BA.debugLine="strMoney = NumberFormat((dblVal/3600) * TotalTime * dblConvert, 1, 0)";
mostCurrent._strmoney = anywheresoftware.b4a.keywords.Common.NumberFormat((_dblval/(double)3600)*_totaltime*_dblconvert,(int) (1),(int) (0));
 //BA.debugLineNum = 211;BA.debugLine="lblRandom.text = (\"By now, \" & strKey & \" would have made \" & strCurrency & strMoney)";
mostCurrent._lblrandom.setText((Object)(("By now, "+mostCurrent._strkey+" would have made "+mostCurrent._strcurrency+mostCurrent._strmoney)));
 };
 //BA.debugLineNum = 214;BA.debugLine="End Sub";
return "";
}
public static String  _timer1_tick() throws Exception{
 //BA.debugLineNum = 165;BA.debugLine="Sub Timer1_Tick";
 //BA.debugLineNum = 168;BA.debugLine="TimePassedSinceStart = (DateTime.Now/1000) - TimeStart";
_timepassedsincestart = (long) ((anywheresoftware.b4a.keywords.Common.DateTime.getNow()/(double)1000)-_timestart);
 //BA.debugLineNum = 169;BA.debugLine="TotalTime = TimePassedSinceStart - TimeIdled";
_totaltime = (long) (_timepassedsincestart-_timeidled);
 //BA.debugLineNum = 170;BA.debugLine="MoneyMade = NumberFormat((TotalTime*dblSec), 1, 2)";
_moneymade = (long)(Double.parseDouble(anywheresoftware.b4a.keywords.Common.NumberFormat((_totaltime*_dblsec),(int) (1),(int) (2))));
 //BA.debugLineNum = 171;BA.debugLine="lblMoney.Text = strCurrency & NumberFormat((TotalTime*dblSec), 1, 2)";
mostCurrent._lblmoney.setText((Object)(mostCurrent._strcurrency+anywheresoftware.b4a.keywords.Common.NumberFormat((_totaltime*_dblsec),(int) (1),(int) (2))));
 //BA.debugLineNum = 172;BA.debugLine="lblTimepassed.Text = DateTime.Time(TotalTime * 1000)";
mostCurrent._lbltimepassed.setText((Object)(anywheresoftware.b4a.keywords.Common.DateTime.Time((long) (_totaltime*1000))));
 //BA.debugLineNum = 174;BA.debugLine="StateManager.SetSetting(\"TotalTime\", TotalTime)";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"TotalTime",BA.NumberToString(_totaltime));
 //BA.debugLineNum = 175;BA.debugLine="StateManager.SetSetting(\"MoneyMade\", MoneyMade)";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"MoneyMade",BA.NumberToString(_moneymade));
 //BA.debugLineNum = 176;BA.debugLine="StateManager.SaveSettings";
mostCurrent._statemanager._savesettings(mostCurrent.activityBA);
 //BA.debugLineNum = 178;BA.debugLine="End Sub";
return "";
}
public static String  _timer2_tick() throws Exception{
 //BA.debugLineNum = 181;BA.debugLine="Sub Timer2_Tick";
 //BA.debugLineNum = 183;BA.debugLine="RandomPick";
_randompick();
 //BA.debugLineNum = 185;BA.debugLine="End Sub";
return "";
}
}
