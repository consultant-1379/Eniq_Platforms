Option Strict Off
Option Explicit On
Friend Class frmMain
	Inherits System.Windows.Forms.Form
#Region "Windows Form Designer generated code "
	Public Sub New()
		MyBase.New()
        'This call is required by the Windows Form Designer.
        InitializeComponent()

        aConfig = New ConfigSettings
        TPSpecBox.Text = aConfig.ReadSetting("TechPackFile")
        TPBaseBox.Text = aConfig.ReadSetting("BaseFile")
        OutputDir.Text = aConfig.ReadSetting("OutputFolder")

	End Sub
	'Form overrides dispose to clean up the component list.
	Protected Overloads Overrides Sub Dispose(ByVal Disposing As Boolean)
		If Disposing Then
			If Not components Is Nothing Then
				components.Dispose()
			End If
		End If
		MyBase.Dispose(Disposing)
	End Sub
	'Required by the Windows Form Designer
	Private components As System.ComponentModel.IContainer
    Public WithEvents Frame2 As System.Windows.Forms.GroupBox
	Public WithEvents TPSpecBox As System.Windows.Forms.TextBox
	Public WithEvents Command1 As System.Windows.Forms.Button
	Public WithEvents Frame1 As System.Windows.Forms.GroupBox
    Public WithEvents Create_Universe As System.Windows.Forms.MenuItem
    Public WithEvents Update_Universe As System.Windows.Forms.MenuItem
    Public WithEvents Universe_Wizards As System.Windows.Forms.MenuItem
	Public MainMenu1 As System.Windows.Forms.MainMenu
	'NOTE: The following procedure is required by the Windows Form Designer
	'It can be modified using the Windows Form Designer.
	'Do not modify it using the code editor.
    Friend WithEvents CMCheckBox As System.Windows.Forms.CheckBox
    Public WithEvents TPBaseBox As System.Windows.Forms.TextBox
    Public WithEvents Button2 As System.Windows.Forms.Button
    Friend WithEvents Label1 As System.Windows.Forms.Label
    Friend WithEvents OpenFileDialog1 As System.Windows.Forms.OpenFileDialog
    Public aConfig As ConfigSettings
    Public WithEvents Label2 As System.Windows.Forms.Label
    Public WithEvents OutputDir As System.Windows.Forms.TextBox
    Public WithEvents Button1 As System.Windows.Forms.Button
    Friend WithEvents FolderBrowserDialog1 As System.Windows.Forms.FolderBrowserDialog
    Friend WithEvents GroupBox1 As System.Windows.Forms.GroupBox
    Friend WithEvents LogText As System.Windows.Forms.RichTextBox
    Friend WithEvents MenuItem1 As System.Windows.Forms.MenuItem
    Friend WithEvents MenuItem2 As System.Windows.Forms.MenuItem
    Friend WithEvents MenuItem3 As System.Windows.Forms.MenuItem
    Friend WithEvents MenuItem5 As System.Windows.Forms.MenuItem
    <System.Diagnostics.DebuggerStepThrough()> Private Sub InitializeComponent()
        Dim configurationAppSettings As System.Configuration.AppSettingsReader = New System.Configuration.AppSettingsReader
        Dim resources As System.Resources.ResourceManager = New System.Resources.ResourceManager(GetType(frmMain))
        Me.Frame2 = New System.Windows.Forms.GroupBox
        Me.CMCheckBox = New System.Windows.Forms.CheckBox
        Me.Frame1 = New System.Windows.Forms.GroupBox
        Me.Label2 = New System.Windows.Forms.Label
        Me.OutputDir = New System.Windows.Forms.TextBox
        Me.Button1 = New System.Windows.Forms.Button
        Me.Label1 = New System.Windows.Forms.Label
        Me.TPBaseBox = New System.Windows.Forms.TextBox
        Me.Button2 = New System.Windows.Forms.Button
        Me.TPSpecBox = New System.Windows.Forms.TextBox
        Me.Command1 = New System.Windows.Forms.Button
        Me.MainMenu1 = New System.Windows.Forms.MainMenu
        Me.Universe_Wizards = New System.Windows.Forms.MenuItem
        Me.Create_Universe = New System.Windows.Forms.MenuItem
        Me.Update_Universe = New System.Windows.Forms.MenuItem
        Me.MenuItem1 = New System.Windows.Forms.MenuItem
        Me.MenuItem2 = New System.Windows.Forms.MenuItem
        Me.MenuItem3 = New System.Windows.Forms.MenuItem
        Me.MenuItem5 = New System.Windows.Forms.MenuItem
        Me.OpenFileDialog1 = New System.Windows.Forms.OpenFileDialog
        Me.FolderBrowserDialog1 = New System.Windows.Forms.FolderBrowserDialog
        Me.GroupBox1 = New System.Windows.Forms.GroupBox
        Me.LogText = New System.Windows.Forms.RichTextBox
        Me.Frame2.SuspendLayout()
        Me.Frame1.SuspendLayout()
        Me.GroupBox1.SuspendLayout()
        Me.SuspendLayout()
        '
        'Frame2
        '
        Me.Frame2.BackColor = System.Drawing.SystemColors.Control
        Me.Frame2.Controls.Add(Me.CMCheckBox)
        Me.Frame2.Font = New System.Drawing.Font("Arial", 8.0!, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.Frame2.ForeColor = System.Drawing.SystemColors.ControlText
        Me.Frame2.Location = New System.Drawing.Point(8, 256)
        Me.Frame2.Name = "Frame2"
        Me.Frame2.RightToLeft = System.Windows.Forms.RightToLeft.No
        Me.Frame2.Size = New System.Drawing.Size(481, 56)
        Me.Frame2.TabIndex = 4
        Me.Frame2.TabStop = False
        Me.Frame2.Text = "Universe create/modify options"
        '
        'CMCheckBox
        '
        Me.CMCheckBox.Location = New System.Drawing.Point(16, 24)
        Me.CMCheckBox.Name = "CMCheckBox"
        Me.CMCheckBox.Size = New System.Drawing.Size(152, 24)
        Me.CMCheckBox.TabIndex = 0
        Me.CMCheckBox.Text = "CM Technology Package"
        '
        'Frame1
        '
        Me.Frame1.BackColor = System.Drawing.SystemColors.Control
        Me.Frame1.Controls.Add(Me.Label2)
        Me.Frame1.Controls.Add(Me.OutputDir)
        Me.Frame1.Controls.Add(Me.Button1)
        Me.Frame1.Controls.Add(Me.Label1)
        Me.Frame1.Controls.Add(Me.TPBaseBox)
        Me.Frame1.Controls.Add(Me.Button2)
        Me.Frame1.Controls.Add(Me.TPSpecBox)
        Me.Frame1.Controls.Add(Me.Command1)
        Me.Frame1.Font = New System.Drawing.Font("Arial", 8.0!, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.Frame1.ForeColor = System.Drawing.SystemColors.ControlText
        Me.Frame1.Location = New System.Drawing.Point(8, 8)
        Me.Frame1.Name = "Frame1"
        Me.Frame1.RightToLeft = System.Windows.Forms.RightToLeft.No
        Me.Frame1.Size = New System.Drawing.Size(481, 240)
        Me.Frame1.TabIndex = 1
        Me.Frame1.TabStop = False
        Me.Frame1.Text = CType(configurationAppSettings.GetValue("Frame1.Text", GetType(System.String)), String)
        '
        'Label2
        '
        Me.Label2.Location = New System.Drawing.Point(8, 160)
        Me.Label2.Name = "Label2"
        Me.Label2.Size = New System.Drawing.Size(376, 23)
        Me.Label2.TabIndex = 13
        Me.Label2.Text = "Output Directory"
        '
        'OutputDir
        '
        Me.OutputDir.AcceptsReturn = True
        Me.OutputDir.AutoSize = False
        Me.OutputDir.BackColor = System.Drawing.SystemColors.Window
        Me.OutputDir.Cursor = System.Windows.Forms.Cursors.IBeam
        Me.OutputDir.Font = New System.Drawing.Font("Arial", 8.0!, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.OutputDir.ForeColor = System.Drawing.SystemColors.WindowText
        Me.OutputDir.Location = New System.Drawing.Point(8, 184)
        Me.OutputDir.MaxLength = 0
        Me.OutputDir.Multiline = True
        Me.OutputDir.Name = "OutputDir"
        Me.OutputDir.RightToLeft = System.Windows.Forms.RightToLeft.No
        Me.OutputDir.Size = New System.Drawing.Size(377, 40)
        Me.OutputDir.TabIndex = 12
        Me.OutputDir.Text = ""
        '
        'Button1
        '
        Me.Button1.BackColor = System.Drawing.SystemColors.Control
        Me.Button1.Cursor = System.Windows.Forms.Cursors.Default
        Me.Button1.Font = New System.Drawing.Font("Arial", 8.0!, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.Button1.ForeColor = System.Drawing.SystemColors.ControlText
        Me.Button1.Location = New System.Drawing.Point(392, 184)
        Me.Button1.Name = "Button1"
        Me.Button1.RightToLeft = System.Windows.Forms.RightToLeft.No
        Me.Button1.Size = New System.Drawing.Size(81, 40)
        Me.Button1.TabIndex = 11
        Me.Button1.Text = "Browse ..."
        '
        'Label1
        '
        Me.Label1.Location = New System.Drawing.Point(8, 80)
        Me.Label1.Name = "Label1"
        Me.Label1.Size = New System.Drawing.Size(376, 23)
        Me.Label1.TabIndex = 6
        Me.Label1.Text = "Tech Pack Base Specification"
        '
        'TPBaseBox
        '
        Me.TPBaseBox.AcceptsReturn = True
        Me.TPBaseBox.AutoSize = False
        Me.TPBaseBox.BackColor = System.Drawing.SystemColors.Window
        Me.TPBaseBox.Cursor = System.Windows.Forms.Cursors.IBeam
        Me.TPBaseBox.Font = New System.Drawing.Font("Arial", 8.0!, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.TPBaseBox.ForeColor = System.Drawing.SystemColors.WindowText
        Me.TPBaseBox.Location = New System.Drawing.Point(8, 104)
        Me.TPBaseBox.MaxLength = 0
        Me.TPBaseBox.Multiline = True
        Me.TPBaseBox.Name = "TPBaseBox"
        Me.TPBaseBox.RightToLeft = System.Windows.Forms.RightToLeft.No
        Me.TPBaseBox.Size = New System.Drawing.Size(377, 40)
        Me.TPBaseBox.TabIndex = 5
        Me.TPBaseBox.Text = "-"
        '
        'Button2
        '
        Me.Button2.BackColor = System.Drawing.SystemColors.Control
        Me.Button2.Cursor = System.Windows.Forms.Cursors.Default
        Me.Button2.Font = New System.Drawing.Font("Arial", 8.0!, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.Button2.ForeColor = System.Drawing.SystemColors.ControlText
        Me.Button2.Location = New System.Drawing.Point(392, 104)
        Me.Button2.Name = "Button2"
        Me.Button2.RightToLeft = System.Windows.Forms.RightToLeft.No
        Me.Button2.Size = New System.Drawing.Size(81, 40)
        Me.Button2.TabIndex = 4
        Me.Button2.Text = "Browse ..."
        '
        'TPSpecBox
        '
        Me.TPSpecBox.AcceptsReturn = True
        Me.TPSpecBox.AutoSize = False
        Me.TPSpecBox.BackColor = System.Drawing.SystemColors.Window
        Me.TPSpecBox.Cursor = System.Windows.Forms.Cursors.IBeam
        Me.TPSpecBox.Font = New System.Drawing.Font("Arial", 8.0!, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.TPSpecBox.ForeColor = System.Drawing.SystemColors.WindowText
        Me.TPSpecBox.Location = New System.Drawing.Point(8, 24)
        Me.TPSpecBox.MaxLength = 0
        Me.TPSpecBox.Multiline = True
        Me.TPSpecBox.Name = "TPSpecBox"
        Me.TPSpecBox.RightToLeft = System.Windows.Forms.RightToLeft.No
        Me.TPSpecBox.Size = New System.Drawing.Size(377, 40)
        Me.TPSpecBox.TabIndex = 3
        Me.TPSpecBox.Text = ""
        '
        'Command1
        '
        Me.Command1.BackColor = System.Drawing.SystemColors.Control
        Me.Command1.Cursor = System.Windows.Forms.Cursors.Default
        Me.Command1.Font = New System.Drawing.Font("Arial", 8.0!, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.Command1.ForeColor = System.Drawing.SystemColors.ControlText
        Me.Command1.Location = New System.Drawing.Point(392, 24)
        Me.Command1.Name = "Command1"
        Me.Command1.RightToLeft = System.Windows.Forms.RightToLeft.No
        Me.Command1.Size = New System.Drawing.Size(81, 40)
        Me.Command1.TabIndex = 2
        Me.Command1.Text = "Browse ..."
        '
        'MainMenu1
        '
        Me.MainMenu1.MenuItems.AddRange(New System.Windows.Forms.MenuItem() {Me.Universe_Wizards, Me.MenuItem1, Me.MenuItem3})
        '
        'Universe_Wizards
        '
        Me.Universe_Wizards.Index = 0
        Me.Universe_Wizards.MenuItems.AddRange(New System.Windows.Forms.MenuItem() {Me.Create_Universe, Me.Update_Universe})
        Me.Universe_Wizards.Text = "Universe"
        '
        'Create_Universe
        '
        Me.Create_Universe.Index = 0
        Me.Create_Universe.Text = "Create Universe"
        '
        'Update_Universe
        '
        Me.Update_Universe.Index = 1
        Me.Update_Universe.Text = "Upgrade Universe"
        '
        'MenuItem1
        '
        Me.MenuItem1.Index = 1
        Me.MenuItem1.MenuItems.AddRange(New System.Windows.Forms.MenuItem() {Me.MenuItem2})
        Me.MenuItem1.Text = "Reports"
        '
        'MenuItem2
        '
        Me.MenuItem2.Index = 0
        Me.MenuItem2.Text = "Create Verification Reports"
        '
        'MenuItem3
        '
        Me.MenuItem3.Index = 2
        Me.MenuItem3.MenuItems.AddRange(New System.Windows.Forms.MenuItem() {Me.MenuItem5})
        Me.MenuItem3.Text = "Documents"
        '
        'MenuItem5
        '
        Me.MenuItem5.Index = 0
        Me.MenuItem5.Text = "Create Universe Reference"
        '
        'OpenFileDialog1
        '
        Me.OpenFileDialog1.Filter = "Excel files|*.xls|Word files|*.doc"
        '
        'GroupBox1
        '
        Me.GroupBox1.Controls.Add(Me.LogText)
        Me.GroupBox1.Location = New System.Drawing.Point(496, 8)
        Me.GroupBox1.Name = "GroupBox1"
        Me.GroupBox1.Size = New System.Drawing.Size(392, 304)
        Me.GroupBox1.TabIndex = 5
        Me.GroupBox1.TabStop = False
        Me.GroupBox1.Text = "Log"
        '
        'LogText
        '
        Me.LogText.Location = New System.Drawing.Point(8, 16)
        Me.LogText.Name = "LogText"
        Me.LogText.Size = New System.Drawing.Size(376, 280)
        Me.LogText.TabIndex = 0
        Me.LogText.Text = ""
        '
        'frmMain
        '
        Me.AutoScaleBaseSize = New System.Drawing.Size(5, 13)
        Me.BackColor = System.Drawing.SystemColors.Control
        Me.ClientSize = New System.Drawing.Size(894, 323)
        Me.Controls.Add(Me.GroupBox1)
        Me.Controls.Add(Me.Frame2)
        Me.Controls.Add(Me.Frame1)
        Me.Cursor = System.Windows.Forms.Cursors.Default
        Me.Font = New System.Drawing.Font("Arial", 8.0!, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle
        Me.Icon = CType(resources.GetObject("$this.Icon"), System.Drawing.Icon)
        Me.Location = New System.Drawing.Point(10, 56)
        Me.MaximizeBox = False
        Me.Menu = Me.MainMenu1
        Me.MinimizeBox = False
        Me.Name = "frmMain"
        Me.RightToLeft = System.Windows.Forms.RightToLeft.No
        Me.Text = "Reporting Wizard"
        Me.Frame2.ResumeLayout(False)
        Me.Frame1.ResumeLayout(False)
        Me.GroupBox1.ResumeLayout(False)
        Me.ResumeLayout(False)

    End Sub
#End Region 
    Private Sub Command1_Click(ByVal eventSender As System.Object, ByVal eventArgs As System.EventArgs) Handles Command1.Click
        aConfig = New ConfigSettings
        Me.OpenFileDialog1.FileName = aConfig.ReadSetting("TechPackFile")
        If OpenFileDialog1.ShowDialog() = DialogResult.OK Then
            TPSpecBox.Text = OpenFileDialog1.FileName
            aConfig.WriteSetting("TechPackFile", OpenFileDialog1.FileName)
        End If
    End Sub
    Public Sub Create_Universe_Popup(ByVal eventSender As System.Object, ByVal eventArgs As System.EventArgs) Handles Create_Universe.Popup
        Create_Universe_Click(eventSender, eventArgs)
    End Sub
    Public Sub Create_Universe_Click(ByVal eventSender As System.Object, ByVal eventArgs As System.EventArgs) Handles Create_Universe.Click
        Dim UniverseFunctionsClass As UniverseFunctions = New UniverseFunctions
        Dim logs As logMessages
        Dim message As logMessages.Message
        Dim count As Integer

        If TPSpecBox.Text = "" Then
            MsgBox("Select Tech Pack Definition.")
        ElseIf TPBaseBox.Text = "" Then
            MsgBox("Select Base Tech Pack Definition.")
        ElseIf OutputDir.Text = "" Then
            MsgBox("Select Tech Pack Directory.")
        Else
            If CMCheckBox.Checked = True Then
                logs = UniverseFunctionsClass.MakeUniverse(TPSpecBox.Text, True, TPBaseBox.Text, OutputDir.Text, "6.5")
            Else
                logs = UniverseFunctionsClass.MakeUniverse(TPSpecBox.Text, False, TPBaseBox.Text, OutputDir.Text, "6.5")
            End If
            LogText.Clear()
            If logs.Count = 0 Then
                LogText.AppendText("No log messages.")
            End If
            For count = 1 To logs.Count
                message = logs.Item(count)
                LogText.AppendText(message.Text)
                LogText.AppendText(Chr(10))
            Next count
        End If
        UniverseFunctionsClass = Nothing
    End Sub
    Public Sub Regen_joins_and_contexts_Popup(ByVal eventSender As System.Object, ByVal eventArgs As System.EventArgs)
        Regen_joins_and_contexts_Click(eventSender, eventArgs)
    End Sub
    Public Sub Regen_joins_and_contexts_Click(ByVal eventSender As System.Object, ByVal eventArgs As System.EventArgs)
        'Dim UniverseFunctionsClass As UniverseFunctions = New UniverseFunctions
        'UniverseFunctionsClass.UpdateJoins(TPSpecBox.Text, TPBaseBox.Text)
    End Sub

    Public Sub Update_Universe_Popup(ByVal eventSender As System.Object, ByVal eventArgs As System.EventArgs) Handles Update_Universe.Popup
        Update_Universe_Click(eventSender, eventArgs)
    End Sub
    Public Sub Update_Universe_Click(ByVal eventSender As System.Object, ByVal eventArgs As System.EventArgs) Handles Update_Universe.Click
        Dim logs As logMessages
        Dim message As logMessages.Message
        Dim count As Integer

        Dim UniverseFunctionsClass As UniverseFunctions = New UniverseFunctions

        If TPSpecBox.Text = "" Then
            MsgBox("Select Tech Pack Definition.")
        ElseIf TPBaseBox.Text = "" Then
            MsgBox("Select Base Tech Pack Definition.")
        ElseIf OutputDir.Text = "" Then
            MsgBox("Select Tech Pack Directory.")
        Else
            If CMCheckBox.Checked = True Then
                logs = UniverseFunctionsClass.UpdateUniverse(TPSpecBox.Text, True, TPBaseBox.Text, OutputDir.Text, "6.5")
            Else
                logs = UniverseFunctionsClass.UpdateUniverse(TPSpecBox.Text, False, TPBaseBox.Text, OutputDir.Text, "6.5")
            End If
            LogText.Clear()
            If logs.Count = 0 Then
                LogText.AppendText("No log messages.")
            End If
            For count = 1 To logs.Count
                message = logs.Item(count)
                LogText.AppendText(message.Text)
                LogText.AppendText(Chr(10))
            Next count
        End If
        UniverseFunctionsClass = Nothing


    End Sub
    Private Sub Button2_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles Button2.Click
        aConfig = New ConfigSettings
        Me.OpenFileDialog1.FileName = aConfig.ReadSetting("BaseFile")
        If OpenFileDialog1.ShowDialog() = DialogResult.OK Then
            TPBaseBox.Text = OpenFileDialog1.FileName
            aConfig.WriteSetting("BaseFile", OpenFileDialog1.FileName)
        End If
    End Sub

    Private Sub Button1_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles Button1.Click
        aConfig = New ConfigSettings
        Me.FolderBrowserDialog1.SelectedPath = aConfig.ReadSetting("OutputFolder")
        If FolderBrowserDialog1.ShowDialog() = DialogResult.OK Then
            OutputDir.Text = FolderBrowserDialog1.SelectedPath
            aConfig.WriteSetting("OutputFolder", FolderBrowserDialog1.SelectedPath)
        End If
    End Sub

    Private Sub MenuItem2_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles MenuItem2.Click
        Dim UniverseFunctionsClass As UniverseFunctions = New UniverseFunctions
        Dim logs As logMessages
        Dim message As logMessages.Message
        Dim count As Integer
        Dim BoApp As busobj.Application
        Dim doc As busobj.Document



        If TPSpecBox.Text = "" Then
            MsgBox("Select Tech Pack Definition.")
        ElseIf TPBaseBox.Text = "" Then
            MsgBox("Select Base Tech Pack Definition.")
        ElseIf OutputDir.Text = "" Then
            MsgBox("Select Tech Pack Directory.")
        Else
            BoApp = New busobj.Application
            BoApp.Visible = False
            If CMCheckBox.Checked = True Then
                logs = UniverseFunctionsClass.MakeVerificationReports(BoApp, TPSpecBox.Text, TPBaseBox.Text, True, OutputDir.Text, "6.5")
            Else
                logs = UniverseFunctionsClass.MakeVerificationReports(BoApp, TPSpecBox.Text, TPBaseBox.Text, False, OutputDir.Text, "6.5")
            End If
            BoApp.Quit()
            BoApp = Nothing
            LogText.Clear()
            If logs.Count = 0 Then
                LogText.AppendText("No log messages.")
            End If
            For count = 1 To logs.Count
                message = logs.Item(count)
                LogText.AppendText(message.Text)
                LogText.AppendText(Chr(10))
            Next count
        End If
        UniverseFunctionsClass = Nothing
    End Sub

    Shared Function GetEXEVersion() As String
        With System.Diagnostics.FileVersionInfo.GetVersionInfo(System.Reflection.Assembly.GetExecutingAssembly.Location)
            Return .FileMajorPart & "." & .FileMinorPart & "." & .FileBuildPart & "." & .FilePrivatePart
        End With
    End Function

    Private Sub MenuItem5_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles MenuItem5.Click
        Dim docFunctions As DocumentFunctions = New DocumentFunctions
        Dim logs As logMessages
        Dim count As Integer
        Dim message As logMessages.Message

        logs = New logMessages

        If TPSpecBox.Text = "" Then
            MsgBox("Select Tech Pack Definition.")
        ElseIf TPBaseBox.Text = "" Then
            MsgBox("Select Base Tech Pack Definition.")
        ElseIf OutputDir.Text = "" Then
            MsgBox("Select Tech Pack Directory.")
        Else
            If CMCheckBox.Checked = True Then
                logs = docFunctions.GenerateTechPackProductReference(TPSpecBox.Text, OutputDir.Text, True, "6.5")
            Else
                logs = docFunctions.GenerateTechPackProductReference(TPSpecBox.Text, OutputDir.Text, False, "6.5")
            End If
        End If
        LogText.Clear()
        If logs.Count = 0 Then
            LogText.AppendText("No log messages.")
        End If
        For count = 1 To logs.Count
            message = logs.Item(count)
            LogText.AppendText(message.Text)
            LogText.AppendText(Chr(10))
        Next count
        docFunctions = Nothing
    End Sub

    Private Sub frmMain_Load(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles MyBase.Load
        Me.Text = "Ericsson Network IQ Reporting Wizard Build " & GetEXEVersion()
    End Sub
End Class