Type=Activity
Version=3
@EndOfDesignText@
#Region  Project Attributes 
	
	#VersionCode: 1
	#VersionName: 
	'SupportedOrientations possible values: unspecified, landscape or portrait.
	#SupportedOrientations: unspecified
	#CanInstallToExternalStorage: False
#End Region

#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
    Dim dblWage As Double
    Dim dblSec As Double
    Dim dblMade As Double
	
	Dim Timer1 As Timer
	
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Dim btnStart As Button
	Dim lblMade As Label
	Dim lblMoney As Label
	Dim lblWage As Label
	Dim txtWage As EditText
	Dim btnReset As Button
	
	Dim p As Phone
End Sub

Sub btnStart_Down

	If txtWage.Text.length = 0 Then
	'DoNothing
	Else
		dblWage = txtWage.Text
		dblSec = (dblWage / 60 / 60)

		If Timer1.enabled = False Then
			Timer1.Enabled = True
		Else 
			Timer1.Enabled = False
		End If
	End If
	
	p.hidekeyboard(Activity)

End Sub


Sub btnReset_down
	
	Dim now1 As Long
	Dim now2 As Long
	Dim timepassed As Double
	now1 = DateTime.now
	
	Dim result As Int
	
	result = Msgbox2("Are you sure you want to reset your money made?", "Warning", "Yes", "Cancel", "", Null)
	
	If result = DialogResponse.positive Then
		lblMoney.Text = "0.000"
		dblMade = 0
		Timer1.Enabled = False
	Else
		now2 = DateTime.now
		timepassed = Round((now2 - now1)/1000)
		dblMade = dblMade + (timepassed*dblSec)
	End If

End Sub

Sub Activity_KeyPress (KeyCode As Int) As Boolean 

	If KeyCode = KeyCodes.KEYCODE_BACK Then
		Dim now3 As Long
		Dim now4 As Long
		Dim timepassed As Double
		now3 = DateTime.now
		
		Dim result As Int
		result = Msgbox2("Are you sure you want to quit the application?", "Warning", "Yes", "Cancel", "", Null)
		If result = DialogResponse.POSITIVE Then
			ExitApplication
		Else
			now4 = DateTime.now
			timepassed = Round((now4 - now3)/1000)
			dblMade = dblMade + (timepassed*dblSec)
			Return True
		End If
	End If

End Sub

Sub Timer1_Tick

	dblMade = dblMade + dblSec
	
	lblMoney.Text = NumberFormat(dblMade, 0, 3)

End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("lytMain")
	Timer1.Initialize("Timer1", 1000)
	
	lblMoney.Gravity = Gravity.CENTER
	lblMoney.Text = "0.000"
	
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub
