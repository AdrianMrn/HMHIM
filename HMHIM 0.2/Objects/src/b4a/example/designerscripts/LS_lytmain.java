package b4a.example.designerscripts;
import anywheresoftware.b4a.objects.TextViewWrapper;
import anywheresoftware.b4a.objects.ImageViewWrapper;
import anywheresoftware.b4a.BA;


public class LS_lytmain{

public static void LS_general(java.util.HashMap<String, anywheresoftware.b4a.objects.ViewWrapper<?>> views, int width, int height, float scale) {
anywheresoftware.b4a.keywords.LayoutBuilder.setScaleRate(0.3);
//BA.debugLineNum = 2;BA.debugLine="AutoScaleAll 'uncomment to scale all views based on the device physical size."[lytmain/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleAll(views);
//BA.debugLineNum = 4;BA.debugLine="btnSettings.Width = 80dip"[lytmain/General script]
views.get("btnsettings").setWidth((int)((80d * scale)));
//BA.debugLineNum = 5;BA.debugLine="btnSettings.Height = 45dip"[lytmain/General script]
views.get("btnsettings").setHeight((int)((45d * scale)));
//BA.debugLineNum = 6;BA.debugLine="btnSettings.left = 0%x"[lytmain/General script]
views.get("btnsettings").setLeft((int)((0d / 100 * width)));
//BA.debugLineNum = 7;BA.debugLine="btnSettings.Top = 0%y"[lytmain/General script]
views.get("btnsettings").setTop((int)((0d / 100 * height)));
//BA.debugLineNum = 9;BA.debugLine="btnTip.Width = 80dip"[lytmain/General script]
views.get("btntip").setWidth((int)((80d * scale)));
//BA.debugLineNum = 10;BA.debugLine="btnTip.Height = 80dip"[lytmain/General script]
views.get("btntip").setHeight((int)((80d * scale)));
//BA.debugLineNum = 11;BA.debugLine="btnTip.Right = 100%x"[lytmain/General script]
views.get("btntip").setLeft((int)((100d / 100 * width) - (views.get("btntip").getWidth())));
//BA.debugLineNum = 12;BA.debugLine="btnTip.Top = 0%y"[lytmain/General script]
views.get("btntip").setTop((int)((0d / 100 * height)));
//BA.debugLineNum = 13;BA.debugLine="btnTip.Visible = false"[lytmain/General script]
views.get("btntip").setVisible(BA.parseBoolean("false"));
//BA.debugLineNum = 15;BA.debugLine="lblWage.Width = 100dip"[lytmain/General script]
views.get("lblwage").setWidth((int)((100d * scale)));
//BA.debugLineNum = 16;BA.debugLine="lblWage.Height = 60dip"[lytmain/General script]
views.get("lblwage").setHeight((int)((60d * scale)));
//BA.debugLineNum = 17;BA.debugLine="lblWage.HorizontalCenter = 50%x"[lytmain/General script]
views.get("lblwage").setLeft((int)((50d / 100 * width) - (views.get("lblwage").getWidth() / 2)));
//BA.debugLineNum = 18;BA.debugLine="lblWage.Top = 10%y"[lytmain/General script]
views.get("lblwage").setTop((int)((10d / 100 * height)));
//BA.debugLineNum = 20;BA.debugLine="btnStart.Width = 150%x"[lytmain/General script]
views.get("btnstart").setWidth((int)((150d / 100 * width)));
//BA.debugLineNum = 21;BA.debugLine="btnStart.Height = 150%y"[lytmain/General script]
views.get("btnstart").setHeight((int)((150d / 100 * height)));
//BA.debugLineNum = 22;BA.debugLine="btnStart.left = -25%x"[lytmain/General script]
views.get("btnstart").setLeft((int)(0-(25d / 100 * width)));
//BA.debugLineNum = 23;BA.debugLine="btnStart.Top = -45%y"[lytmain/General script]
views.get("btnstart").setTop((int)(0-(45d / 100 * height)));
//BA.debugLineNum = 25;BA.debugLine="lblMade.Width = 260dip"[lytmain/General script]
views.get("lblmade").setWidth((int)((260d * scale)));
//BA.debugLineNum = 26;BA.debugLine="lblMade.Height = 40dip"[lytmain/General script]
views.get("lblmade").setHeight((int)((40d * scale)));
//BA.debugLineNum = 27;BA.debugLine="lblMade.HorizontalCenter = 50%x"[lytmain/General script]
views.get("lblmade").setLeft((int)((50d / 100 * width) - (views.get("lblmade").getWidth() / 2)));
//BA.debugLineNum = 28;BA.debugLine="lblMade.Top = 25%y"[lytmain/General script]
views.get("lblmade").setTop((int)((25d / 100 * height)));
//BA.debugLineNum = 30;BA.debugLine="lblMoney.Width = 250dip"[lytmain/General script]
views.get("lblmoney").setWidth((int)((250d * scale)));
//BA.debugLineNum = 31;BA.debugLine="lblMoney.Height = 80dip"[lytmain/General script]
views.get("lblmoney").setHeight((int)((80d * scale)));
//BA.debugLineNum = 32;BA.debugLine="lblMoney.HorizontalCenter = 50%x"[lytmain/General script]
views.get("lblmoney").setLeft((int)((50d / 100 * width) - (views.get("lblmoney").getWidth() / 2)));
//BA.debugLineNum = 33;BA.debugLine="lblMoney.Top = lblMade.bottom"[lytmain/General script]
views.get("lblmoney").setTop((int)((views.get("lblmade").getTop() + views.get("lblmade").getHeight())));
//BA.debugLineNum = 35;BA.debugLine="lblTimepassed.Width = 140dip"[lytmain/General script]
views.get("lbltimepassed").setWidth((int)((140d * scale)));
//BA.debugLineNum = 36;BA.debugLine="lblTimepassed.Height = 24dip"[lytmain/General script]
views.get("lbltimepassed").setHeight((int)((24d * scale)));
//BA.debugLineNum = 37;BA.debugLine="lblTimepassed.HorizontalCenter = 50%x"[lytmain/General script]
views.get("lbltimepassed").setLeft((int)((50d / 100 * width) - (views.get("lbltimepassed").getWidth() / 2)));
//BA.debugLineNum = 38;BA.debugLine="lblTimepassed.Top = lblMoney.bottom + 2%y"[lytmain/General script]
views.get("lbltimepassed").setTop((int)((views.get("lblmoney").getTop() + views.get("lblmoney").getHeight())+(2d / 100 * height)));
//BA.debugLineNum = 40;BA.debugLine="lblRandom.Width = 100%x"[lytmain/General script]
views.get("lblrandom").setWidth((int)((100d / 100 * width)));
//BA.debugLineNum = 41;BA.debugLine="lblRandom.Height = 11%y"[lytmain/General script]
views.get("lblrandom").setHeight((int)((11d / 100 * height)));
//BA.debugLineNum = 42;BA.debugLine="lblRandom.HorizontalCenter = 50%x"[lytmain/General script]
views.get("lblrandom").setLeft((int)((50d / 100 * width) - (views.get("lblrandom").getWidth() / 2)));
//BA.debugLineNum = 43;BA.debugLine="lblRandom.Top = lblTimepassed.Bottom + 2%y"[lytmain/General script]
views.get("lblrandom").setTop((int)((views.get("lbltimepassed").getTop() + views.get("lbltimepassed").getHeight())+(2d / 100 * height)));
//BA.debugLineNum = 45;BA.debugLine="lblStartPause.bottom = 100%y"[lytmain/General script]
views.get("lblstartpause").setTop((int)((100d / 100 * height) - (views.get("lblstartpause").getHeight())));
//BA.debugLineNum = 46;BA.debugLine="lblStartPause.Left = 2%x"[lytmain/General script]
views.get("lblstartpause").setLeft((int)((2d / 100 * width)));
//BA.debugLineNum = 47;BA.debugLine="lblStartPause.Width = 98%x"[lytmain/General script]
views.get("lblstartpause").setWidth((int)((98d / 100 * width)));
//BA.debugLineNum = 49;BA.debugLine="btnReset.Width = 100dip"[lytmain/General script]
views.get("btnreset").setWidth((int)((100d * scale)));
//BA.debugLineNum = 50;BA.debugLine="btnReset.Height = 45dip"[lytmain/General script]
views.get("btnreset").setHeight((int)((45d * scale)));
//BA.debugLineNum = 51;BA.debugLine="btnReset.Right = 100%x"[lytmain/General script]
views.get("btnreset").setLeft((int)((100d / 100 * width) - (views.get("btnreset").getWidth())));
//BA.debugLineNum = 52;BA.debugLine="btnReset.Bottom = 100%y"[lytmain/General script]
views.get("btnreset").setTop((int)((100d / 100 * height) - (views.get("btnreset").getHeight())));

}
}