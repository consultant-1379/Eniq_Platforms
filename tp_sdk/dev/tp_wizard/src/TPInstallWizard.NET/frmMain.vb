Option Strict Off
Option Explicit On 

Public Class frmMain
    Inherits System.Windows.Forms.Form

    Dim DesignerApp As Designer.Application
    Public DWHConnection As String
    Public UnvDomain As String
    Public ReportDomain As String
    Public UnvGroup As String


#Region "Windows Form Designer generated code "
    Public Sub New()
        MyBase.New()
        'This call is required by the Windows Form Designer.
        InitializeComponent()

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
    'NOTE: The following procedure is required by the Windows Form Designer
    'It can be modified using the Windows Form Designer.
    'Do not modify it using the code editor.
    Friend WithEvents MainMenu1 As System.Windows.Forms.MainMenu
    Friend WithEvents MenuItem1 As System.Windows.Forms.MenuItem
    Friend WithEvents MenuItem2 As System.Windows.Forms.MenuItem
    Friend WithEvents MenuItem3 As System.Windows.Forms.MenuItem
    Friend WithEvents MenuItem4 As System.Windows.Forms.MenuItem
    Friend WithEvents MenuItem5 As System.Windows.Forms.MenuItem
    Friend WithEvents GroupBox1 As System.Windows.Forms.GroupBox
    Friend WithEvents MenuItem10 As System.Windows.Forms.MenuItem
    Friend WithEvents GroupBox2 As System.Windows.Forms.GroupBox
    Friend WithEvents LogText As System.Windows.Forms.RichTextBox
    Friend WithEvents Label1 As System.Windows.Forms.Label
    Friend WithEvents UserText As System.Windows.Forms.TextBox
    Friend WithEvents PasswordText As System.Windows.Forms.TextBox
    Friend WithEvents Label5 As System.Windows.Forms.Label
    Friend WithEvents Label7 As System.Windows.Forms.Label
    Friend WithEvents MenuItem7 As System.Windows.Forms.MenuItem
    Friend WithEvents StatusBar1 As System.Windows.Forms.StatusBar
    Friend WithEvents DesignerPanel As System.Windows.Forms.StatusBarPanel
    Friend WithEvents Label2 As System.Windows.Forms.Label
    Friend WithEvents Label3 As System.Windows.Forms.Label
    Friend WithEvents DomainCombo As System.Windows.Forms.ComboBox
    Friend WithEvents UniverseCombo As System.Windows.Forms.ComboBox
    Friend WithEvents Label4 As System.Windows.Forms.Label
    Friend WithEvents Label6 As System.Windows.Forms.Label
    Friend WithEvents Label8 As System.Windows.Forms.Label
    Friend WithEvents GroupCombo As System.Windows.Forms.ComboBox
    Friend WithEvents RepositoryCombo As System.Windows.Forms.ComboBox
    Friend WithEvents MenuItem6 As System.Windows.Forms.MenuItem
    <System.Diagnostics.DebuggerStepThrough()> Private Sub InitializeComponent()
        Dim resources As System.Resources.ResourceManager = New System.Resources.ResourceManager(GetType(frmMain))
        Me.MainMenu1 = New System.Windows.Forms.MainMenu
        Me.MenuItem1 = New System.Windows.Forms.MenuItem
        Me.MenuItem3 = New System.Windows.Forms.MenuItem
        Me.MenuItem4 = New System.Windows.Forms.MenuItem
        Me.MenuItem2 = New System.Windows.Forms.MenuItem
        Me.MenuItem5 = New System.Windows.Forms.MenuItem
        Me.MenuItem6 = New System.Windows.Forms.MenuItem
        Me.MenuItem7 = New System.Windows.Forms.MenuItem
        Me.MenuItem10 = New System.Windows.Forms.MenuItem
        Me.GroupBox1 = New System.Windows.Forms.GroupBox
        Me.RepositoryCombo = New System.Windows.Forms.ComboBox
        Me.Label7 = New System.Windows.Forms.Label
        Me.PasswordText = New System.Windows.Forms.TextBox
        Me.Label5 = New System.Windows.Forms.Label
        Me.UserText = New System.Windows.Forms.TextBox
        Me.Label1 = New System.Windows.Forms.Label
        Me.GroupBox2 = New System.Windows.Forms.GroupBox
        Me.LogText = New System.Windows.Forms.RichTextBox
        Me.StatusBar1 = New System.Windows.Forms.StatusBar
        Me.DesignerPanel = New System.Windows.Forms.StatusBarPanel
        Me.DomainCombo = New System.Windows.Forms.ComboBox
        Me.Label2 = New System.Windows.Forms.Label
        Me.Label3 = New System.Windows.Forms.Label
        Me.UniverseCombo = New System.Windows.Forms.ComboBox
        Me.Label4 = New System.Windows.Forms.Label
        Me.GroupCombo = New System.Windows.Forms.ComboBox
        Me.Label6 = New System.Windows.Forms.Label
        Me.Label8 = New System.Windows.Forms.Label
        Me.GroupBox1.SuspendLayout()
        Me.GroupBox2.SuspendLayout()
        CType(Me.DesignerPanel, System.ComponentModel.ISupportInitialize).BeginInit()
        Me.SuspendLayout()
        '
        'MainMenu1
        '
        Me.MainMenu1.MenuItems.AddRange(New System.Windows.Forms.MenuItem() {Me.MenuItem1, Me.MenuItem5})
        '
        'MenuItem1
        '
        Me.MenuItem1.Index = 0
        Me.MenuItem1.MenuItems.AddRange(New System.Windows.Forms.MenuItem() {Me.MenuItem3, Me.MenuItem4, Me.MenuItem2})
        Me.MenuItem1.Text = "File"
        '
        'MenuItem3
        '
        Me.MenuItem3.Index = 0
        Me.MenuItem3.Text = "Connect"
        '
        'MenuItem4
        '
        Me.MenuItem4.Enabled = False
        Me.MenuItem4.Index = 1
        Me.MenuItem4.Text = "Disconnect"
        '
        'MenuItem2
        '
        Me.MenuItem2.Index = 2
        Me.MenuItem2.Text = "Exit"
        '
        'MenuItem5
        '
        Me.MenuItem5.Enabled = False
        Me.MenuItem5.Index = 1
        Me.MenuItem5.MenuItems.AddRange(New System.Windows.Forms.MenuItem() {Me.MenuItem6, Me.MenuItem7, Me.MenuItem10})
        Me.MenuItem5.Text = "Utility"
        '
        'MenuItem6
        '
        Me.MenuItem6.Index = 0
        Me.MenuItem6.Text = "Export Tech Pack Universe"
        '
        'MenuItem7
        '
        Me.MenuItem7.Index = 1
        Me.MenuItem7.Text = "Create Linked Universe"
        '
        'MenuItem10
        '
        Me.MenuItem10.Index = 2
        Me.MenuItem10.Text = "Update Linked Universe"
        '
        'GroupBox1
        '
        Me.GroupBox1.Controls.Add(Me.RepositoryCombo)
        Me.GroupBox1.Controls.Add(Me.Label7)
        Me.GroupBox1.Controls.Add(Me.PasswordText)
        Me.GroupBox1.Controls.Add(Me.Label5)
        Me.GroupBox1.Controls.Add(Me.UserText)
        Me.GroupBox1.Controls.Add(Me.Label1)
        Me.GroupBox1.Location = New System.Drawing.Point(8, 8)
        Me.GroupBox1.Name = "GroupBox1"
        Me.GroupBox1.Size = New System.Drawing.Size(192, 144)
        Me.GroupBox1.TabIndex = 25
        Me.GroupBox1.TabStop = False
        Me.GroupBox1.Text = "Connection Information"
        '
        'RepositoryCombo
        '
        Me.RepositoryCombo.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList
        Me.RepositoryCombo.Location = New System.Drawing.Point(8, 112)
        Me.RepositoryCombo.Name = "RepositoryCombo"
        Me.RepositoryCombo.Size = New System.Drawing.Size(176, 21)
        Me.RepositoryCombo.TabIndex = 2
        '
        'Label7
        '
        Me.Label7.Location = New System.Drawing.Point(8, 88)
        Me.Label7.Name = "Label7"
        Me.Label7.Size = New System.Drawing.Size(72, 23)
        Me.Label7.TabIndex = 29
        Me.Label7.Text = "Repository"
        '
        'PasswordText
        '
        Me.PasswordText.Location = New System.Drawing.Point(80, 56)
        Me.PasswordText.Name = "PasswordText"
        Me.PasswordText.PasswordChar = Microsoft.VisualBasic.ChrW(42)
        Me.PasswordText.TabIndex = 1
        Me.PasswordText.Text = ""
        '
        'Label5
        '
        Me.Label5.Location = New System.Drawing.Point(8, 56)
        Me.Label5.Name = "Label5"
        Me.Label5.Size = New System.Drawing.Size(72, 23)
        Me.Label5.TabIndex = 27
        Me.Label5.Text = "Password"
        '
        'UserText
        '
        Me.UserText.Location = New System.Drawing.Point(80, 24)
        Me.UserText.Name = "UserText"
        Me.UserText.TabIndex = 0
        Me.UserText.Text = ""
        '
        'Label1
        '
        Me.Label1.Location = New System.Drawing.Point(8, 24)
        Me.Label1.Name = "Label1"
        Me.Label1.Size = New System.Drawing.Size(72, 23)
        Me.Label1.TabIndex = 25
        Me.Label1.Text = "User Name"
        '
        'GroupBox2
        '
        Me.GroupBox2.Controls.Add(Me.LogText)
        Me.GroupBox2.Location = New System.Drawing.Point(8, 160)
        Me.GroupBox2.Name = "GroupBox2"
        Me.GroupBox2.Size = New System.Drawing.Size(568, 208)
        Me.GroupBox2.TabIndex = 26
        Me.GroupBox2.TabStop = False
        Me.GroupBox2.Text = "Log"
        '
        'LogText
        '
        Me.LogText.Location = New System.Drawing.Point(8, 16)
        Me.LogText.Name = "LogText"
        Me.LogText.ReadOnly = True
        Me.LogText.Size = New System.Drawing.Size(552, 184)
        Me.LogText.TabIndex = 0
        Me.LogText.Text = ""
        '
        'StatusBar1
        '
        Me.StatusBar1.Location = New System.Drawing.Point(0, 373)
        Me.StatusBar1.Name = "StatusBar1"
        Me.StatusBar1.Panels.AddRange(New System.Windows.Forms.StatusBarPanel() {Me.DesignerPanel})
        Me.StatusBar1.ShowPanels = True
        Me.StatusBar1.Size = New System.Drawing.Size(586, 22)
        Me.StatusBar1.TabIndex = 27
        Me.StatusBar1.Text = "StatusBar1"
        '
        'DesignerPanel
        '
        Me.DesignerPanel.Alignment = System.Windows.Forms.HorizontalAlignment.Center
        Me.DesignerPanel.AutoSize = System.Windows.Forms.StatusBarPanelAutoSize.Spring
        Me.DesignerPanel.BorderStyle = System.Windows.Forms.StatusBarPanelBorderStyle.None
        Me.DesignerPanel.Text = "Designer disconnected"
        Me.DesignerPanel.Width = 570
        '
        'DomainCombo
        '
        Me.DomainCombo.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList
        Me.DomainCombo.Location = New System.Drawing.Point(208, 40)
        Me.DomainCombo.Name = "DomainCombo"
        Me.DomainCombo.Size = New System.Drawing.Size(152, 21)
        Me.DomainCombo.TabIndex = 0
        Me.DomainCombo.Visible = False
        '
        'Label2
        '
        Me.Label2.Location = New System.Drawing.Point(208, 16)
        Me.Label2.Name = "Label2"
        Me.Label2.Size = New System.Drawing.Size(152, 16)
        Me.Label2.TabIndex = 29
        Me.Label2.Text = "Repository Domains"
        Me.Label2.Visible = False
        '
        'Label3
        '
        Me.Label3.Location = New System.Drawing.Point(400, 72)
        Me.Label3.Name = "Label3"
        Me.Label3.Size = New System.Drawing.Size(152, 16)
        Me.Label3.TabIndex = 31
        Me.Label3.Text = "Domain universes"
        Me.Label3.Visible = False
        '
        'UniverseCombo
        '
        Me.UniverseCombo.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList
        Me.UniverseCombo.Location = New System.Drawing.Point(400, 96)
        Me.UniverseCombo.Name = "UniverseCombo"
        Me.UniverseCombo.Size = New System.Drawing.Size(152, 21)
        Me.UniverseCombo.TabIndex = 2
        Me.UniverseCombo.Visible = False
        '
        'Label4
        '
        Me.Label4.Location = New System.Drawing.Point(208, 72)
        Me.Label4.Name = "Label4"
        Me.Label4.Size = New System.Drawing.Size(152, 16)
        Me.Label4.TabIndex = 33
        Me.Label4.Text = "Domain groups"
        Me.Label4.Visible = False
        '
        'GroupCombo
        '
        Me.GroupCombo.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList
        Me.GroupCombo.Location = New System.Drawing.Point(208, 96)
        Me.GroupCombo.Name = "GroupCombo"
        Me.GroupCombo.Size = New System.Drawing.Size(152, 21)
        Me.GroupCombo.TabIndex = 1
        Me.GroupCombo.Visible = False
        '
        'Label6
        '
        Me.Label6.Font = New System.Drawing.Font("Microsoft Sans Serif", 9.75!, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.Label6.Location = New System.Drawing.Point(368, 40)
        Me.Label6.Name = "Label6"
        Me.Label6.Size = New System.Drawing.Size(24, 24)
        Me.Label6.TabIndex = 34
        Me.Label6.Text = "-->"
        Me.Label6.TextAlign = System.Drawing.ContentAlignment.MiddleCenter
        Me.Label6.Visible = False
        '
        'Label8
        '
        Me.Label8.Font = New System.Drawing.Font("Microsoft Sans Serif", 9.75!, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.Label8.Location = New System.Drawing.Point(368, 96)
        Me.Label8.Name = "Label8"
        Me.Label8.Size = New System.Drawing.Size(24, 24)
        Me.Label8.TabIndex = 35
        Me.Label8.Text = "-->"
        Me.Label8.TextAlign = System.Drawing.ContentAlignment.MiddleCenter
        Me.Label8.Visible = False
        '
        'frmMain
        '
        Me.AutoScaleBaseSize = New System.Drawing.Size(5, 13)
        Me.ClientSize = New System.Drawing.Size(586, 395)
        Me.Controls.Add(Me.Label8)
        Me.Controls.Add(Me.Label6)
        Me.Controls.Add(Me.Label4)
        Me.Controls.Add(Me.GroupCombo)
        Me.Controls.Add(Me.Label3)
        Me.Controls.Add(Me.UniverseCombo)
        Me.Controls.Add(Me.Label2)
        Me.Controls.Add(Me.DomainCombo)
        Me.Controls.Add(Me.StatusBar1)
        Me.Controls.Add(Me.GroupBox2)
        Me.Controls.Add(Me.GroupBox1)
        Me.Cursor = System.Windows.Forms.Cursors.Default
        Me.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle
        Me.Icon = CType(resources.GetObject("$this.Icon"), System.Drawing.Icon)
        Me.MaximizeBox = False
        Me.Menu = Me.MainMenu1
        Me.MinimizeBox = False
        Me.Name = "frmMain"
        Me.RightToLeft = System.Windows.Forms.RightToLeft.No
        Me.GroupBox1.ResumeLayout(False)
        Me.GroupBox2.ResumeLayout(False)
        CType(Me.DesignerPanel, System.ComponentModel.ISupportInitialize).EndInit()
        Me.ResumeLayout(False)

    End Sub
#End Region

    Private Sub MenuItem2_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles MenuItem2.Click
        Try
            If Not DesignerApp Is Nothing Then
                quitDesigner()
            End If
            If DesignerApp Is Nothing Then
                DesignerPanel.BorderStyle = StatusBarPanelBorderStyle.None
                DesignerPanel.Text = "Designer disconnected"
            End If
        Catch ex As Exception
            MsgBox(ex.ToString)
            Exit Sub
        End Try

        Application.Exit()
    End Sub

    Private Sub MenuItem4_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles MenuItem4.Click

        Try
            If Not DesignerApp Is Nothing Then
                quitDesigner()
            End If
            If DesignerApp Is Nothing Then
                DesignerPanel.BorderStyle = StatusBarPanelBorderStyle.None
                DesignerPanel.Text = "Designer disconnected"
            End If
        Catch ex As Exception
            MsgBox(ex.ToString)
            Exit Sub
        End Try


        UserText.Enabled = True
        PasswordText.Enabled = True
        RepositoryCombo.Enabled = True

        MenuItem3.Enabled = True
        MenuItem2.Enabled = True
        MenuItem4.Enabled = False
        MenuItem5.Enabled = False

        DomainCombo.Items.Clear()
        UniverseCombo.Items.Clear()
        GroupCombo.Items.Clear()
        DomainCombo.Text = ""
        UniverseCombo.Text = ""
        GroupCombo.Text = ""

        DomainCombo.Visible = False
        UniverseCombo.Visible = False
        GroupCombo.Visible = False

        Label2.Visible = False
        Label3.Visible = False
        Label4.Visible = False
        Label6.Visible = False
        Label8.Visible = False

    End Sub

    Private Sub MenuItem3_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles MenuItem3.Click

        LogText.Clear()

        If DesignerApp Is Nothing Then
            Try
                DesignerApp = CreateObject("Designer.Application")
                ' DesignerApp = New Designer.Application
                DesignerApp.Visible = False
                DesignerApp.Interactive = False
            Catch ex As Exception
                LogText.Clear()
                LogText.AppendText("Designer instance is not started. Exception: " & ex.Message)
                Exit Sub
            End Try
        End If

        If UserText.Text <> "" AndAlso RepositoryCombo.Text <> "" Then
            Try
                'DesignerApp.LoginAs()
                DesignerApp.LoginAs(UserText.Text, PasswordText.Text, False, RepositoryCombo.Text)
                DesignerPanel.BorderStyle = StatusBarPanelBorderStyle.Sunken
                DesignerPanel.Text = "Designer connected"
            Catch ex As Exception
                LogText.Clear()
                LogText.AppendText("Login to BO repository failed. Exception: " & ex.Message)
                Exit Sub
            End Try

            MenuItem3.Enabled = False
            MenuItem4.Enabled = True
            MenuItem5.Enabled = True
            MenuItem2.Enabled = True

            UserText.Enabled = False
            PasswordText.Enabled = False
            RepositoryCombo.Enabled = False

            DomainCombo.Visible = True
            Label2.Visible = True

            DomainCombo.Items.Clear()
            Dim domain As Designer.UniverseDomain
            For Each domain In DesignerApp.UniverseDomains
                DomainCombo.Items.Add(domain.Name)
            Next
        End If



    End Sub

    Private Sub MenuItem10_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles MenuItem10.Click
        LogText.Clear()

        Dim TPInstallFunctionsClass As TPInstallFunctions = New TPInstallFunctions
        Dim createUniverse As Boolean
        Dim logs As logMessages

        Dim Result As MsgBoxResult

        Dim universeFullName As String
        Dim universeName As String


        LogText.Clear()
        If DomainCombo.Text <> "" AndAlso UniverseCombo.Text <> "" Then
            Me.SendToBack()
            logs = TPInstallFunctionsClass.UpdateLinkedUniverse(DesignerApp, UniverseCombo.Text, DomainCombo.Text, GroupCombo.Text, RepositoryCombo.Text, True)
            printLog("No log messages on updating custom universe.", logs)
            Me.BringToFront()
        End If

        DomainCombo.Items.Clear()
        GroupCombo.Items.Clear()
        UniverseCombo.Items.Clear()
        DomainCombo.Text = ""
        GroupCombo.Text = ""
        UniverseCombo.Text = ""

        Dim domain As Designer.UniverseDomain
        For Each domain In DesignerApp.UniverseDomains
            DomainCombo.Items.Add(domain.Name)
        Next
    End Sub

    Sub printLog(ByRef defaultMessage As String, ByRef logs As logMessages)
        Dim count As Integer
        Dim message As logMessages.Message

        If (logs Is Nothing = False) Then
            If logs.Count = 0 Then
                LogText.AppendText(defaultMessage & Chr(10))
            End If
            For count = 1 To logs.Count
                message = logs.Item(count)
                LogText.AppendText(message.Text)
                LogText.AppendText(Chr(10))
            Next count
        End If
    End Sub

    Private Sub MenuItem7_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles MenuItem7.Click
        LogText.Clear()

        Dim TPInstallFunctionsClass As TPInstallFunctions = New TPInstallFunctions
        Dim createUniverse As Boolean
        Dim logs As logMessages

        Dim Result As MsgBoxResult

        Dim universeFullName As String
        Dim universeName As String


        LogText.Clear()
        If DomainCombo.Text <> "" AndAlso UniverseCombo.Text <> "" Then
            Me.SendToBack()
            logs = TPInstallFunctionsClass.CreateLinkedUniverse(DesignerApp, UniverseCombo.Text, DomainCombo.Text, GroupCombo.Text, RepositoryCombo.Text, True)
            printLog("No log messages on creating custom universe.", logs)
            Me.BringToFront()
        End If

        DomainCombo.Items.Clear()
        GroupCombo.Items.Clear()
        UniverseCombo.Items.Clear()
        DomainCombo.Text = ""
        GroupCombo.Text = ""
        UniverseCombo.Text = ""

        Dim domain As Designer.UniverseDomain
        For Each domain In DesignerApp.UniverseDomains
            DomainCombo.Items.Add(domain.Name)
        Next

    End Sub

    Sub quitDesigner()
        Try
            DesignerApp.Quit()
            DesignerApp = Nothing
        Catch ex As Exception
            MsgBox(ex.ToString)
        End Try
    End Sub

    Shared Function GetEXEVersion() As String
        With System.Diagnostics.FileVersionInfo.GetVersionInfo(System.Reflection.Assembly.GetExecutingAssembly.Location)
            Return .FileMajorPart & "." & .FileMinorPart & "." & .FileBuildPart & "." & .FilePrivatePart
        End With
    End Function

    Private Sub frmMain_Load(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles MyBase.Load
        Me.Text = "ENIQ Universe Installer Build " & GetEXEVersion()

        Try
            DesignerApp = CreateObject("Designer.Application")
            ' DesignerApp = New Designer.Application
            DesignerApp.Visible = False
            DesignerApp.Interactive = False
        Catch ex As Exception
            LogText.Clear()
            LogText.AppendText("Designer instance is not started. Exception: " & ex.Message)
            Exit Sub
        End Try

        ' make a reference to a directory
        Dim di As New IO.DirectoryInfo(Replace(DesignerApp.GetInstallDirectory(Designer.DsDirectoryID.dsDesignerDirectory), "\bin", "\LocData"))
        Dim files As IO.FileInfo() = di.GetFiles()
        Dim file As IO.FileInfo

        RepositoryCombo.Items.Clear()
        For Each file In files
            RepositoryCombo.Items.Add(Replace(file.ToString, ".key", ""))
        Next

    End Sub

    Private Sub DomainCombo_SelectedIndexChanged(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles DomainCombo.SelectedIndexChanged
        Dim universe As Designer.StoredUniverse
        Dim group As Designer.User

        UniverseCombo.Items.Clear()
        If DomainCombo.Text <> "" Then
            For Each universe In DesignerApp.UniverseDomains(DomainCombo.Text).StoredUniverses
                UniverseCombo.Items.Add(universe.Name)
            Next
        End If
        GroupCombo.Items.Clear()
        If DomainCombo.Text <> "" Then
            For Each group In DesignerApp.UniverseDomains(DomainCombo.Text).Users
                GroupCombo.Items.Add(group.Name)
            Next
        End If

        GroupCombo.Visible = True
        Label4.Visible = True
        Label6.Visible = True

    End Sub

    Private Sub GroupCombo_SelectedIndexChanged(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles GroupCombo.SelectedIndexChanged
        UniverseCombo.Visible = True
        Label3.Visible = True
        Label8.Visible = True

    End Sub

    Private Sub MenuItem6_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles MenuItem6.Click
        LogText.Clear()

        Dim TPInstallFunctionsClass As TPInstallFunctions = New TPInstallFunctions
        Dim logs As logMessages

        Dim Result As MsgBoxResult

        Dim universeFullName As String
        Dim universeName As String

        LogText.Clear()
        If DomainCombo.Text <> "" Then
            Me.SendToBack()
            logs = TPInstallFunctionsClass.ExportUniverse(DesignerApp, DomainCombo.Text, GroupCombo.Text, RepositoryCombo.Text, True)
            printLog("No log messages on exporting universe.", logs)
            Me.BringToFront()
        End If

        DomainCombo.Items.Clear()
        GroupCombo.Items.Clear()
        UniverseCombo.Items.Clear()
        DomainCombo.Text = ""
        GroupCombo.Text = ""
        UniverseCombo.Text = ""

        Dim domain As Designer.UniverseDomain
        For Each domain In DesignerApp.UniverseDomains
            DomainCombo.Items.Add(domain.Name)
        Next
    End Sub

    Private Sub GroupBox1_Enter(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles GroupBox1.Enter

    End Sub
End Class