Imports System.Collections
Imports System.Threading

Public Class TestApplication

    Protected m_DataSourceNames As ArrayList = New ArrayList()
    Protected techPacks As ArrayList = New ArrayList()
    Protected m_universes As SortedList = New SortedList()
    ' Private mythread As Thread = New Thread(AddressOf startTest)
    Private WithEvents TestWorker As System.ComponentModel.BackgroundWorker    

    '    Private techPacks As ArrayList
    Private create As Boolean
    Private Shadows update As Boolean
    Private reports As Boolean
    Private reference As Boolean
    Private createLinked As Boolean
    Private updateLinked As Boolean
    Private username As String
    Private password As String
    Private boServer As String
    Private baseTechPack As String
    Private outputDir As String
    Private boVersion As String
    Private authentication As String
    Private dwhrepConnection As String
    Private universes As SortedList

    Public Sub New()
        ' This call is required by the Windows Form Designer.
        InitializeComponent()

        ' Add any initialization after the InitializeComponent() call.
        getSystemDataSourceNames()
        getUserDataSourceNames()
        Me.DWHREPConCombo.Items.AddRange(m_DataSourceNames.ToArray())

        Me.Controls.Add(Button1)
        AddHandler Button1.Click, AddressOf getDataSources

        Me.Controls.Add(Select_All)
        AddHandler Me.Select_All.Click, AddressOf selectTechPacks

        Me.Controls.Add(Unselect_All)
        AddHandler Me.Unselect_All.Click, AddressOf unselectTechPacks

        Me.Controls.Add(Button2)
        ' AddHandler Me.Button2.Click, AddressOf startTest

        ' Dim listener As MyListener = New MyListener(Me.TextBox1)
        ' Trace.Listeners.Add(listener)

        ' Background worker:
        TestWorker = New System.ComponentModel.BackgroundWorker
        TestWorker.WorkerReportsProgress = True
        TestWorker.WorkerSupportsCancellation = True
    End Sub

    Private Sub Label1_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles Label1.Click

    End Sub

    Private Sub CheckBox1_CheckedChanged(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles CreateCheckBox.CheckedChanged

    End Sub

    Private Sub folderBrowserButton_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles folderBrowserButton.Click
        FolderBrowserDialog1.ShowDialog()
        outputDirTextBox.Text = FolderBrowserDialog1.SelectedPath
    End Sub

    Private Sub btnStart_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles Button2.Click
        Button2.Enabled = False
        create = Me.CreateCheckBox.Checked
        update = Me.UpdateCheckBox.Checked
        reports = Me.ReportsCheckBox.Checked
        username = Me.usernameTextBox.Text
        password = Me.passwordTextBox.Text
        boServer = Me.boServerComboBox.Text
        baseTechPack = Me.baseTechPackCombo.Text
        outputDir = Me.outputDirTextBox.Text
        boVersion = Me.boVersionComboBox.Text
        dwhrepConnection = Me.DWHREPConCombo.Text

        TestWorker.RunWorkerAsync()      
    End Sub

    Private Sub TestWorker_DoWork(ByVal sender As Object, ByVal e As System.ComponentModel.DoWorkEventArgs) Handles TestWorker.DoWork
        startTest(techPacks, create, update, reports, False, False, False, _
                          username, password, boServer, baseTechPack, outputDir, _
                          boVersion, "ENTERPRISE", dwhrepConnection, m_universes)
    End Sub

    Private Sub TestWorker_RunWorkerCompleted(ByVal sender As Object, ByVal e As System.ComponentModel.RunWorkerCompletedEventArgs) Handles TestWorker.RunWorkerCompleted
        Button2.Enabled = True
    End Sub

    Private Sub startTest(ByVal techPacks As ArrayList, ByVal create As Boolean, ByVal update As Boolean, ByVal reports As Boolean, ByVal reference As Boolean, _
                      ByVal createLinked As Boolean, ByVal updateLinked As Boolean, _
                      ByVal username As String, ByVal password As String, ByVal boServer As String, ByVal baseTechPack As String, ByVal outputDir As String, _
                      ByVal boVersion As String, _
                      ByVal authentication As String, _
                      ByVal dwhrepConnection As String, _
                      ByVal universes As SortedList)
        techPacks.Clear()
        For Each techpack As Object In CheckedListBox1.CheckedItems
            techPacks.Add(techpack.ToString)
        Next

        Dim regression As RegressionTest = New RegressionTest()
        regression.doTest(techPacks, create, update, reports, _
                          reference, createLinked, updateLinked, _
                          username, password, boServer, baseTechPack, outputDir, _
                          boVersion, "ENTERPRISE", dwhrepConnection, universes)
    End Sub

    Private Sub getDataSources()
        CheckedListBox1.Items.Clear()
        techPacks.Clear()
        m_universes.Clear()

        Dim selection As String = DWHREPConCombo.Text
        Dim tpConn As System.Data.Odbc.OdbcConnection
        If (selection <> "") Then

            Try
                Dim tpAdoConn As String = "DSN=" & selection & ";"
                ' tpAdoConn = tpAdoConn + selection + ";"
                Console.WriteLine(tpAdoConn + Environment.NewLine)

                Dim sqlStatement As String
                sqlStatement = "Select DISTINCT VERSIONID FROM DWHTechPacks where (TECHPACK_NAME LIKE 'DC_%')" _
                & "OR (TECHPACK_NAME LIKE 'Alarm%') OR (TECHPACK_NAME LIKE 'DIM_%')"
                tpConn = New System.Data.Odbc.OdbcConnection(tpAdoConn)
                tpConn.Open()

                Dim databaseProxy As DatabaseProxy = New DatabaseProxy()
                databaseProxy.setupDatabaseReader(sqlStatement, tpConn)

                Dim techPacks2 As ArrayList = databaseProxy.readSingleColumnFromDB(sqlStatement, False)

                sqlStatement = "Select DISTINCT VERSIONID FROM Versioning where VERSIONID LIKE 'TP_BASE%'"
                databaseProxy.setupDatabaseReader(sqlStatement, tpConn)

                Dim baseTechPacks As ArrayList = databaseProxy.readSingleColumnFromDB(sqlStatement, False)
                Me.baseTechPackCombo.Items.AddRange(baseTechPacks.ToArray())


                CheckedListBox1.Items.AddRange(techPacks2.ToArray())

                For Each techpack As String In techPacks2
                    Dim universes As ArrayList = New ArrayList()
                    sqlStatement = "Select DISTINCT VERSIONID FROM UniverseName where VERSIONID = '" & techpack & "'"

                    databaseProxy.setupDatabaseReader(sqlStatement, tpConn)

                    For Each result As String In (databaseProxy.readSingleColumnFromDB(sqlStatement, False))
                        universes.Add(result)
                    Next
                    m_universes.Add(techpack, universes)
                Next
            Catch ex As Exception
                Console.WriteLine("Error getting tech packs from database")
            Finally
                tpConn.Close()
            End Try
        End If
    End Sub

    Private Sub selectTechPacks()
        For i As Integer = 0 To Me.CheckedListBox1.Items.Count - 1
            Me.CheckedListBox1.SetItemChecked(i, True)
        Next i
    End Sub

    Private Sub unselectTechPacks()
        For i As Integer = 0 To Me.CheckedListBox1.Items.Count - 1
            Me.CheckedListBox1.SetItemChecked(i, False)
        Next i
    End Sub

    Public Function getDSNs()
        Return m_DataSourceNames
    End Function


    Public Sub getSystemDataSourceNames()
        ' get system dsns
        Dim reg As Microsoft.Win32.RegistryKey = (Microsoft.Win32.Registry.LocalMachine).OpenSubKey("Software")
        GetDataSourceNames(reg)
    End Sub

    Public Sub getUserDataSourceNames()
        ' get user's dsns
        Dim reg As Microsoft.Win32.RegistryKey = (Microsoft.Win32.Registry.CurrentUser).OpenSubKey("Software")
        GetDataSourceNames(reg)
    End Sub

    Public Sub GetDataSourceNames(ByVal reg As Microsoft.Win32.RegistryKey)
        Dim dsnList As SortedList = New System.Collections.SortedList()

        If Not (reg Is Nothing) Then
            reg = reg.OpenSubKey("ODBC")
            If Not (reg Is Nothing) Then
                reg = reg.OpenSubKey("ODBC.INI")
                If Not (reg Is Nothing) Then
                    reg = reg.OpenSubKey("ODBC Data Sources")
                    If Not (reg Is Nothing) Then
                        For Each sName As String In reg.GetValueNames()
                            If (m_DataSourceNames.Contains(sName) = False) Then
                                m_DataSourceNames.Add(sName)
                            End If
                        Next
                    End If
                    Try
                        reg.Close()
                    Catch ex As Exception
                        Console.WriteLine("Error closing registry")
                    End Try
                End If
            End If
        End If
    End Sub

    Private Class MyListener
        Inherits System.Diagnostics.TraceListener

        Private textbox As System.Windows.Forms.TextBox
        Private WithEvents TestWorker2 As System.ComponentModel.BackgroundWorker
        Private Message As String = ""

        Public Sub New()
            ' 
        End Sub

        Private Sub TestWorker2_DoWork(ByVal sender As Object, ByVal e As System.ComponentModel.DoWorkEventArgs) Handles TestWorker2.DoWork
            textbox.ForeColor = Drawing.Color.Blue
            textbox.AppendText(Message + Environment.NewLine)
        End Sub

        Public Sub New(ByVal TextBox1 As System.Windows.Forms.TextBox)
            Me.textbox = TextBox1
            TestWorker2 = New System.ComponentModel.BackgroundWorker
            TestWorker2.WorkerReportsProgress = True
            TestWorker2.WorkerSupportsCancellation = True
        End Sub

        Public Overloads Overrides Sub Write(ByVal Message As String)
            Me.Message = Message
            TestWorker2.RunWorkerAsync()
        End Sub

        Public Overloads Overrides Sub WriteLine(ByVal Message As String)
            ' textbox.AppendText(Message + Environment.NewLine)
        End Sub

    End Class


End Class