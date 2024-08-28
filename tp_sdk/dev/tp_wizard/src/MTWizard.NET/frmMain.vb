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
        BaseTPTextBox.Text = aConfig.ReadSetting("BaseFile")
        InterfaceBox.Text = aConfig.ReadSetting("InterfaceFile")
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
    Public WithEvents Command1 As System.Windows.Forms.Button
    Public WithEvents Frame1 As System.Windows.Forms.GroupBox
    Public WithEvents Create_Metadata As System.Windows.Forms.MenuItem
    Public WithEvents Metadata_Wizards As System.Windows.Forms.MenuItem
    Public MainMenu1 As System.Windows.Forms.MainMenu
    'NOTE: The following procedure is required by the Windows Form Designer
    'It can be modified using the Windows Form Designer.
    'Do not modify it using the code editor.
    Public WithEvents Button1 As System.Windows.Forms.Button
    Friend WithEvents Label1 As System.Windows.Forms.Label
    Public WithEvents BaseTPTextBox As System.Windows.Forms.TextBox
    Public WithEvents Button2 As System.Windows.Forms.Button
    Public WithEvents OutputDir As System.Windows.Forms.TextBox
    Friend WithEvents FolderBrowserDialog1 As System.Windows.Forms.FolderBrowserDialog
    Friend WithEvents OpenFileDialog1 As System.Windows.Forms.OpenFileDialog
    Public WithEvents Label2 As System.Windows.Forms.Label
    Public WithEvents TPSpecBox As System.Windows.Forms.TextBox
    Friend WithEvents Label3 As System.Windows.Forms.Label
    Public WithEvents InterfaceBox As System.Windows.Forms.TextBox
    Public WithEvents Button3 As System.Windows.Forms.Button
    Public aConfig As ConfigSettings
    Friend WithEvents GroupBox1 As System.Windows.Forms.GroupBox
    Friend WithEvents LogText As System.Windows.Forms.RichTextBox
    Friend WithEvents MenuItem1 As System.Windows.Forms.MenuItem
    Friend WithEvents MenuItem2 As System.Windows.Forms.MenuItem
    Friend WithEvents MenuItem3 As System.Windows.Forms.MenuItem
    Friend WithEvents MenuItem4 As System.Windows.Forms.MenuItem
    <System.Diagnostics.DebuggerStepThrough()> Private Sub InitializeComponent()
        Dim resources As System.Resources.ResourceManager = New System.Resources.ResourceManager(GetType(frmMain))
        Me.Frame1 = New System.Windows.Forms.GroupBox
        Me.Label3 = New System.Windows.Forms.Label
        Me.InterfaceBox = New System.Windows.Forms.TextBox
        Me.Button3 = New System.Windows.Forms.Button
        Me.TPSpecBox = New System.Windows.Forms.TextBox
        Me.Label2 = New System.Windows.Forms.Label
        Me.OutputDir = New System.Windows.Forms.TextBox
        Me.Button2 = New System.Windows.Forms.Button
        Me.Label1 = New System.Windows.Forms.Label
        Me.BaseTPTextBox = New System.Windows.Forms.TextBox
        Me.Button1 = New System.Windows.Forms.Button
        Me.Command1 = New System.Windows.Forms.Button
        Me.MainMenu1 = New System.Windows.Forms.MainMenu
        Me.Metadata_Wizards = New System.Windows.Forms.MenuItem
        Me.Create_Metadata = New System.Windows.Forms.MenuItem
        Me.MenuItem2 = New System.Windows.Forms.MenuItem
        Me.MenuItem1 = New System.Windows.Forms.MenuItem
        Me.MenuItem3 = New System.Windows.Forms.MenuItem
        Me.FolderBrowserDialog1 = New System.Windows.Forms.FolderBrowserDialog
        Me.OpenFileDialog1 = New System.Windows.Forms.OpenFileDialog
        Me.GroupBox1 = New System.Windows.Forms.GroupBox
        Me.LogText = New System.Windows.Forms.RichTextBox
        Me.MenuItem4 = New System.Windows.Forms.MenuItem
        Me.Frame1.SuspendLayout()
        Me.GroupBox1.SuspendLayout()
        Me.SuspendLayout()
        '
        'Frame1
        '
        Me.Frame1.BackColor = System.Drawing.SystemColors.Control
        Me.Frame1.Controls.Add(Me.Label3)
        Me.Frame1.Controls.Add(Me.InterfaceBox)
        Me.Frame1.Controls.Add(Me.Button3)
        Me.Frame1.Controls.Add(Me.TPSpecBox)
        Me.Frame1.Controls.Add(Me.Label2)
        Me.Frame1.Controls.Add(Me.OutputDir)
        Me.Frame1.Controls.Add(Me.Button2)
        Me.Frame1.Controls.Add(Me.Label1)
        Me.Frame1.Controls.Add(Me.BaseTPTextBox)
        Me.Frame1.Controls.Add(Me.Button1)
        Me.Frame1.Controls.Add(Me.Command1)
        Me.Frame1.Font = New System.Drawing.Font("Arial", 8.0!, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.Frame1.ForeColor = System.Drawing.SystemColors.ControlText
        Me.Frame1.Location = New System.Drawing.Point(8, 8)
        Me.Frame1.Name = "Frame1"
        Me.Frame1.RightToLeft = System.Windows.Forms.RightToLeft.No
        Me.Frame1.Size = New System.Drawing.Size(481, 320)
        Me.Frame1.TabIndex = 1
        Me.Frame1.TabStop = False
        Me.Frame1.Text = "TP Definition"
        '
        'Label3
        '
        Me.Label3.Location = New System.Drawing.Point(8, 160)
        Me.Label3.Name = "Label3"
        Me.Label3.Size = New System.Drawing.Size(376, 23)
        Me.Label3.TabIndex = 14
        Me.Label3.Text = "TP Interface Definition"
        '
        'InterfaceBox
        '
        Me.InterfaceBox.AcceptsReturn = True
        Me.InterfaceBox.AutoSize = False
        Me.InterfaceBox.BackColor = System.Drawing.SystemColors.Window
        Me.InterfaceBox.Cursor = System.Windows.Forms.Cursors.IBeam
        Me.InterfaceBox.Font = New System.Drawing.Font("Arial", 8.0!, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.InterfaceBox.ForeColor = System.Drawing.SystemColors.WindowText
        Me.InterfaceBox.Location = New System.Drawing.Point(8, 184)
        Me.InterfaceBox.MaxLength = 0
        Me.InterfaceBox.Multiline = True
        Me.InterfaceBox.Name = "InterfaceBox"
        Me.InterfaceBox.RightToLeft = System.Windows.Forms.RightToLeft.No
        Me.InterfaceBox.Size = New System.Drawing.Size(377, 40)
        Me.InterfaceBox.TabIndex = 13
        Me.InterfaceBox.Text = "-"
        '
        'Button3
        '
        Me.Button3.BackColor = System.Drawing.SystemColors.Control
        Me.Button3.Cursor = System.Windows.Forms.Cursors.Default
        Me.Button3.Font = New System.Drawing.Font("Arial", 8.0!, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.Button3.ForeColor = System.Drawing.SystemColors.ControlText
        Me.Button3.Location = New System.Drawing.Point(392, 184)
        Me.Button3.Name = "Button3"
        Me.Button3.RightToLeft = System.Windows.Forms.RightToLeft.No
        Me.Button3.Size = New System.Drawing.Size(81, 40)
        Me.Button3.TabIndex = 12
        Me.Button3.Text = "Browse ..."
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
        Me.TPSpecBox.TabIndex = 11
        Me.TPSpecBox.Text = "-"
        '
        'Label2
        '
        Me.Label2.Location = New System.Drawing.Point(8, 248)
        Me.Label2.Name = "Label2"
        Me.Label2.Size = New System.Drawing.Size(376, 23)
        Me.Label2.TabIndex = 10
        Me.Label2.Text = "TP Directory"
        '
        'OutputDir
        '
        Me.OutputDir.AcceptsReturn = True
        Me.OutputDir.AutoSize = False
        Me.OutputDir.BackColor = System.Drawing.SystemColors.Window
        Me.OutputDir.Cursor = System.Windows.Forms.Cursors.IBeam
        Me.OutputDir.Font = New System.Drawing.Font("Arial", 8.0!, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.OutputDir.ForeColor = System.Drawing.SystemColors.WindowText
        Me.OutputDir.Location = New System.Drawing.Point(8, 272)
        Me.OutputDir.MaxLength = 0
        Me.OutputDir.Multiline = True
        Me.OutputDir.Name = "OutputDir"
        Me.OutputDir.RightToLeft = System.Windows.Forms.RightToLeft.No
        Me.OutputDir.Size = New System.Drawing.Size(377, 40)
        Me.OutputDir.TabIndex = 9
        Me.OutputDir.Text = ""
        '
        'Button2
        '
        Me.Button2.BackColor = System.Drawing.SystemColors.Control
        Me.Button2.Cursor = System.Windows.Forms.Cursors.Default
        Me.Button2.Font = New System.Drawing.Font("Arial", 8.0!, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.Button2.ForeColor = System.Drawing.SystemColors.ControlText
        Me.Button2.Location = New System.Drawing.Point(392, 272)
        Me.Button2.Name = "Button2"
        Me.Button2.RightToLeft = System.Windows.Forms.RightToLeft.No
        Me.Button2.Size = New System.Drawing.Size(81, 40)
        Me.Button2.TabIndex = 8
        Me.Button2.Text = "Browse ..."
        '
        'Label1
        '
        Me.Label1.Location = New System.Drawing.Point(8, 80)
        Me.Label1.Name = "Label1"
        Me.Label1.Size = New System.Drawing.Size(376, 23)
        Me.Label1.TabIndex = 7
        Me.Label1.Text = "Base TP Definition"
        '
        'BaseTPTextBox
        '
        Me.BaseTPTextBox.AcceptsReturn = True
        Me.BaseTPTextBox.AutoSize = False
        Me.BaseTPTextBox.BackColor = System.Drawing.SystemColors.Window
        Me.BaseTPTextBox.Cursor = System.Windows.Forms.Cursors.IBeam
        Me.BaseTPTextBox.Font = New System.Drawing.Font("Arial", 8.0!, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.BaseTPTextBox.ForeColor = System.Drawing.SystemColors.WindowText
        Me.BaseTPTextBox.Location = New System.Drawing.Point(8, 104)
        Me.BaseTPTextBox.MaxLength = 0
        Me.BaseTPTextBox.Multiline = True
        Me.BaseTPTextBox.Name = "BaseTPTextBox"
        Me.BaseTPTextBox.RightToLeft = System.Windows.Forms.RightToLeft.No
        Me.BaseTPTextBox.Size = New System.Drawing.Size(377, 40)
        Me.BaseTPTextBox.TabIndex = 6
        Me.BaseTPTextBox.Text = "-"
        '
        'Button1
        '
        Me.Button1.BackColor = System.Drawing.SystemColors.Control
        Me.Button1.Cursor = System.Windows.Forms.Cursors.Default
        Me.Button1.Font = New System.Drawing.Font("Arial", 8.0!, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.Button1.ForeColor = System.Drawing.SystemColors.ControlText
        Me.Button1.Location = New System.Drawing.Point(392, 104)
        Me.Button1.Name = "Button1"
        Me.Button1.RightToLeft = System.Windows.Forms.RightToLeft.No
        Me.Button1.Size = New System.Drawing.Size(81, 40)
        Me.Button1.TabIndex = 5
        Me.Button1.Text = "Browse ..."
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
        Me.Command1.Size = New System.Drawing.Size(81, 33)
        Me.Command1.TabIndex = 2
        Me.Command1.Text = "Browse ..."
        '
        'MainMenu1
        '
        Me.MainMenu1.MenuItems.AddRange(New System.Windows.Forms.MenuItem() {Me.Metadata_Wizards})
        '
        'Metadata_Wizards
        '
        Me.Metadata_Wizards.Index = 0
        Me.Metadata_Wizards.MenuItems.AddRange(New System.Windows.Forms.MenuItem() {Me.Create_Metadata, Me.MenuItem2, Me.MenuItem4, Me.MenuItem1, Me.MenuItem3})
        Me.Metadata_Wizards.Text = "File"
        '
        'Create_Metadata
        '
        Me.Create_Metadata.Index = 0
        Me.Create_Metadata.Text = "Create Metadata"
        '
        'MenuItem2
        '
        Me.MenuItem2.Index = 1
        Me.MenuItem2.Text = "Create Interface"
        '
        'MenuItem1
        '
        Me.MenuItem1.Index = 3
        Me.MenuItem1.Text = "-"
        '
        'MenuItem3
        '
        Me.MenuItem3.Index = 4
        Me.MenuItem3.Text = "Exit"
        '
        'FolderBrowserDialog1
        '
        Me.FolderBrowserDialog1.RootFolder = System.Environment.SpecialFolder.MyComputer
        '
        'OpenFileDialog1
        '
        Me.OpenFileDialog1.Filter = "Excel files|*.xls"
        '
        'GroupBox1
        '
        Me.GroupBox1.Controls.Add(Me.LogText)
        Me.GroupBox1.Location = New System.Drawing.Point(496, 8)
        Me.GroupBox1.Name = "GroupBox1"
        Me.GroupBox1.Size = New System.Drawing.Size(440, 320)
        Me.GroupBox1.TabIndex = 2
        Me.GroupBox1.TabStop = False
        Me.GroupBox1.Text = "Log"
        '
        'LogText
        '
        Me.LogText.Location = New System.Drawing.Point(8, 16)
        Me.LogText.Name = "LogText"
        Me.LogText.Size = New System.Drawing.Size(424, 296)
        Me.LogText.TabIndex = 3
        Me.LogText.Text = ""
        '
        'MenuItem4
        '
        Me.MenuItem4.Index = 2
        Me.MenuItem4.Text = "Create Tech Pack Description"
        '
        'frmMain
        '
        Me.AutoScaleBaseSize = New System.Drawing.Size(5, 13)
        Me.BackColor = System.Drawing.SystemColors.Control
        Me.ClientSize = New System.Drawing.Size(944, 337)
        Me.Controls.Add(Me.GroupBox1)
        Me.Controls.Add(Me.Frame1)
        Me.Cursor = System.Windows.Forms.Cursors.Default
        Me.Font = New System.Drawing.Font("Arial", 8.0!, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle
        Me.Icon = CType(resources.GetObject("$this.Icon"), System.Drawing.Icon)
        Me.Location = New System.Drawing.Point(10, 56)
        Me.MaximizeBox = False
        Me.Menu = Me.MainMenu1
        Me.Name = "frmMain"
        Me.RightToLeft = System.Windows.Forms.RightToLeft.No
        Me.Text = "Metadata Wizard"
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
    Public Sub Create_Metadata_Popup(ByVal eventSender As System.Object, ByVal eventArgs As System.EventArgs) Handles Create_Metadata.Popup
        Create_Metadata_Click(eventSender, eventArgs)
    End Sub
    Public Sub Create_Metadata_Click(ByVal eventSender As System.Object, ByVal eventArgs As System.EventArgs) Handles Create_Metadata.Click

        Dim mtClass As MetadataFunctions = New MetadataFunctions
        Dim logs As logMessages
        Dim message As logMessages.Message
        Dim count As Integer

        logs = New logMessages

        If TPSpecBox.Text = "" Then
            MsgBox("Select Tech Pack Definition.")
        ElseIf BaseTPTextBox.Text = "" Then
            MsgBox("Select Base Tech Pack Definition.")
        ElseIf InterfaceBox.Text = "" Then
            MsgBox("Select Interface Definition.")
        ElseIf OutputDir.Text = "" Then
            MsgBox("Select Tech Pack Directory.")
        Else
            logs = mtClass.MakeMetadata(TPSpecBox.Text, BaseTPTextBox.Text, InterfaceBox.Text, OutputDir.Text, False)
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

        'Application.Exit()
    End Sub
    Private Sub mnuFileExit_Click()
        'unload the form
        Me.Close()
    End Sub

    Private Sub Button1_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles Button1.Click
        aConfig = New ConfigSettings
        Me.OpenFileDialog1.FileName = aConfig.ReadSetting("BaseFile")
        If OpenFileDialog1.ShowDialog() = DialogResult.OK Then
            BaseTPTextBox.Text = OpenFileDialog1.FileName
            aConfig.WriteSetting("BaseFile", OpenFileDialog1.FileName)
        End If
    End Sub

    Private Sub Button2_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles Button2.Click
        aConfig = New ConfigSettings
        Me.FolderBrowserDialog1.SelectedPath = aConfig.ReadSetting("OutputFolder")
        If FolderBrowserDialog1.ShowDialog() = DialogResult.OK Then
            OutputDir.Text = FolderBrowserDialog1.SelectedPath
            aConfig.WriteSetting("OutputFolder", FolderBrowserDialog1.SelectedPath)
        End If
    End Sub

    Private Sub Button3_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles Button3.Click
        aConfig = New ConfigSettings
        Me.OpenFileDialog1.FileName = aConfig.ReadSetting("InterfaceFile")
        If OpenFileDialog1.ShowDialog() = DialogResult.OK Then
            InterfaceBox.Text = OpenFileDialog1.FileName
            aConfig.WriteSetting("InterfaceFile", OpenFileDialog1.FileName)
        End If
    End Sub


    Shared Function GetEXEVersion() As String
        With System.Diagnostics.FileVersionInfo.GetVersionInfo(System.Reflection.Assembly.GetExecutingAssembly.Location)
            Return .FileMajorPart & "." & .FileMinorPart & "." & .FileBuildPart & "." & .FilePrivatePart
        End With
    End Function

    Private Sub frmMain_Load(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles MyBase.Load
        Me.Text = "Ericsson Network IQ Metadata Wizard Build " & GetEXEVersion()
    End Sub

    Private Sub MenuItem2_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles MenuItem2.Click
        Dim mtClass As MetadataFunctions = New MetadataFunctions
        Dim logs As logMessages
        Dim message As logMessages.Message
        Dim count As Integer

        logs = New logMessages

        If TPSpecBox.Text = "" Then
            MsgBox("Select Tech Pack Definition.")
        ElseIf BaseTPTextBox.Text = "" Then
            MsgBox("Select Base Tech Pack Definition.")
        ElseIf InterfaceBox.Text = "" Then
            MsgBox("Select Interface Definition.")
        ElseIf OutputDir.Text = "" Then
            MsgBox("Select Tech Pack Directory.")
        Else
            logs = mtClass.MakeMetadata(TPSpecBox.Text, BaseTPTextBox.Text, InterfaceBox.Text, OutputDir.Text, True)
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
    End Sub

    Private Sub MenuItem3_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles MenuItem3.Click
        Application.Exit()
    End Sub

    Private Sub MenuItem4_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles MenuItem4.Click
        Dim mtClass As MetadataFunctions = New MetadataFunctions
        Dim logs As logMessages
        Dim message As logMessages.Message
        Dim count As Integer

        logs = New logMessages

        If TPSpecBox.Text = "" Then
            MsgBox("Select Tech Pack Definition.")
        ElseIf BaseTPTextBox.Text = "" Then
            MsgBox("Select Base Tech Pack Definition.")
        ElseIf InterfaceBox.Text = "" Then
            MsgBox("Select Interface Definition.")
        ElseIf OutputDir.Text = "" Then
            MsgBox("Select Tech Pack Directory.")
        Else
            logs = mtClass.MakeTPDescription(TPSpecBox.Text, BaseTPTextBox.Text, InterfaceBox.Text, OutputDir.Text, False)
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
    End Sub
End Class