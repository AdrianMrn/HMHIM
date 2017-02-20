Type=Activity
Version=3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
	Dim Timer1 As Timer
	

	
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	
	Dim dblWage As Double
    Dim dblSec As Double

	Dim btnStart As Button
	Dim lblMade As Label
	Dim lblMoney As Label
	Dim lblWage As Label
	Dim txtWage As EditText
	Dim btnReset As Button
	
	Dim p As Phone
	
	
	Dim TimeStart As Long
	Dim TimePassedSinceStart As Long
	Dim TimeIdled As Long
	Dim TotalTime As Long
	
End Sub

Sub btnStart_Down
	
	Dim Time1 As Long
	Dim Time2 As Long
	Dim Timepassed As Long
	

	
	If txtWage.Text.length = 0 Then
	'DoNothing
	Else	
		dblWage = txtWage.Text
		dblSec = (dblWage / 3600)

		If Timer1.Enabled = False AND TimeStart = 0 Then
			TimeStart = (DateTime.now /1000)
			Timer1.Enabled = True
		Else If Timer1.enabled = False Then
			Time2 = DateTime.now
			Timepassed = ((Time2 - Time1)/1000)
			TimeIdled = TimeIdled + Timepassed
			Timer1.Enabled = True
		Else 
			Timer1.Enabled = False
			Time1 = DateTime.Now
			
		End If
	End If
	
	p.hidekeyboard(Activity)

End Sub


Sub btnReset_down
	
	Dim result As Int
	
	result = Msgbox2("Are you sure you want to reset your money made?", "Warning", "Yes", "Cancel", "", Null)
	
	If result = DialogResponse.positive Then
		lblMoney.Text = "0.000"
		TimePassedSinceStart = 0
		TimeIdled = 0
		TimeStart = 0
		TotalTime = 0
		Timer1.Enabled = False
	Else
	End If

End Sub

Sub Activity_KeyPress (KeyCode As Int) As Boolean 'ignore

	If KeyCode = KeyCodes.KEYCODE_BACK Then
		Dim result As Int
		result = Msgbox2("Are you sure you want to quit the application?", "Warning", "Yes", "Cancel", "", Null)
		If result = DialogResponse.POSITIVE Then
			ExitApplication
		Else
			Return True
		End If
	End If

End Sub

Sub Timer1_Tick

	TimePassedSinceStart = TimeStart - (DateTime.now/1000)

	TotalTime = TimePassedSinceStart - TimeIdled
	
	lblMoney.Text = NumberFormat((TotalTime*dblSec), 0, 3)

End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("lytMain")
	Timer1.Initialize("Timer1", 1000)
	
	TimeStart = 0
	TimePassedSinceStart = 0
	lblMoney.Gravity = Gravity.CENTER
	lblMoney.Text = "0.000"
	
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub
