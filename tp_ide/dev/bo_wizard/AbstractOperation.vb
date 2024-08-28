'''
'Abstract operation defines a generic type for the operations done by the BO interface.
'All operations should inherit/extend this class.
'Has common code for all operations.
''
Public MustInherit Class AbstractOperation

    ' The name of the operation
    Protected m_operationName As String

    ' The user name for the BO server
    Protected m_boUser As String

    ' The password for the BO server
    Protected m_bopass As String

    ' The repository string e.g. atrcps43:6400
    Protected m_borep As String

    ' The main tech pack identifier string
    Protected m_tpident As String

    ' Boolean to check if the tech pack is a CM tech pack
    Protected m_cmTechPack As Boolean

    ' The base tech pack identifier string
    Protected m_baseident As String

    ' The output folder where upgraded universes, reports etc are written
    Protected m_outputFolder As String

    ' The 
    Protected m_eniqConn As String

    ' The Business Objects version: either 6.5 or XI
    Protected m_boVersion As String

    ' BO authorisation (usually "Enterprise")
    Protected m_boAut As String

    ' The directory to use on the BO server
    Protected m_domain As String

    ' Name of the universe. Used when creating and updating linked universes.
    Protected m_universe As String

    ' The file name for the log file
    Private logFileName As String = "TP IDE BO Interface.log"

    Protected universeFunctions As UniverseFunctionsTPIde

    ' Gets/sets the operation name
    Public Property OperationName() As String
        Get
            OperationName = m_operationName
        End Get

        Set(ByVal newName As String)
            m_operationName = newName
        End Set
    End Property

    ' Default constructor
    Public Sub New()

    End Sub

    Public Sub New(ByVal operationName As String, ByVal bouser As String, ByVal bopass As String, ByVal borep As String, _
                   ByVal tpident As String, ByVal cmTechPack As String, ByVal baseident As String, ByVal outputFolder As String, _
                   ByVal eniqConn As String, ByVal boVersion As String, ByVal boAut As String, ByVal domain As String, ByVal universe As String)
        Me.m_operationName = operationName
        Me.m_boUser = bouser
        Me.m_bopass = bopass
        Me.m_borep = borep
        Me.m_tpident = tpident
        Me.m_cmTechPack = cmTechPack
        Me.m_baseident = baseident
        Me.m_outputFolder = outputFolder
        Me.m_eniqConn = eniqConn
        Me.m_boVersion = boVersion
        Me.m_boAut = boAut
        Me.m_domain = domain
        Me.m_universe = universe
        Me.logFileName = getLogFileName()
        Me.universeFunctions = createUniverseFunctions()
    End Sub

    'Execute method starts off an operation.
    'Error handling is common to all operations.
    Public Sub execute()
        Dim success As Boolean = False
        setupTracing()
        Try
            success = doOperation()
        Catch ex As System.NullReferenceException
            Trace.WriteLine("Error for operation: " & m_operationName & ": " & ex.ToString())
            MsgBox("Error for operation: " & m_operationName & ": " & ex.ToString(), MsgBoxStyle.Critical, m_operationName)
        Catch ex As Exception
            Trace.WriteLine("Error for operation: " & m_operationName & ": " & ex.ToString())
            MsgBox("Error for operation: " & m_operationName & ": " & ex.ToString(), MsgBoxStyle.Critical, m_operationName)
        Finally
            If (success = False) Then
                Trace.WriteLine("Error for operation: " & m_operationName)
                Console.WriteLine("Error for operation: " & m_operationName)
            End If
            Try
                cleanup()
                Trace.Flush()
                displayLogFileMessage()
            Catch ex As Exception
                Trace.WriteLine("Error while shutting down: " & ex.ToString())
            End Try
        End Try
    End Sub

    Protected Overridable Sub displayLogFileMessage()
        If (m_outputFolder Is Nothing Or m_outputFolder = "") Then
            MsgBox("Wrote log file to " & System.Environment.CurrentDirectory & "\" & logFileName, MsgBoxStyle.OkOnly, "Log file written")
        Else
            MsgBox("Wrote log file to " & m_outputFolder & "\" & logFileName, MsgBoxStyle.OkOnly, "Log file written")
        End If
    End Sub

    ' Calls the operation.
    ' Must be overridden by the operations.
    Public MustOverride Function doOperation() As Boolean

    ' Closes database connections and instances of Designer and Deski.
    Public MustOverride Function cleanup() As Boolean

    ''
    'Sets up tracing for the application.
    'Writes a log file to the output directory defined in the IDE.
    Protected Overridable Sub setupTracing()
        Dim tr1 As TextWriterTraceListener
        If (m_outputFolder Is Nothing Or m_outputFolder = "") Then
            tr1 = New TextWriterTraceListener(System.Environment.CurrentDirectory & "\" & logFileName)
        Else
            tr1 = New TextWriterTraceListener(m_outputFolder & "\" & logFileName)
        End If
        Trace.Listeners.Add(tr1)
        Trace.WriteLine("Ericsson Network IQ Tech Pack IDE - BO Interface")
        Trace.WriteLine(System.DateTime.Now)
        Trace.WriteLine("Starting BO Interface for operation: " & m_operationName)
    End Sub

    ' Gets the log file name.
    ' e.g. UpdateUniverse-9-8-2010-13-17.log
    Private Function getLogFileName() As String
        ' Get timestamp:
        Dim currentTime As System.DateTime = System.DateTime.Now
        Dim timestamp As String
        timestamp = "-" & currentTime.Day & "-" & currentTime.Month & "-" & currentTime.Year & "-" & currentTime.Hour & "-" & currentTime.Minute
        Return m_operationName.Replace(" ", "") & timestamp & ".log"
    End Function

    ' Creates a new instance of Designer.Application.
    Protected Function createNewDesignerApp() As Designer.Application
        Dim DesignerApp As Designer.Application = New Designer.Application
        DesignerApp.Visible = False
        DesignerApp.Interactive = False
        Return DesignerApp
    End Function

    ' Logs on to the Designer application.
    Protected Function logOnToDesignerApp() As Designer.Application
        Dim DesignerApp As Designer.Application = Nothing
        DesignerApp = createNewDesignerApp()
        If (m_boVersion = "6.5") Then
            DesignerApp.LoginAs(m_boUser, m_bopass, False, m_borep)
        ElseIf (m_boVersion = "XI") Then
            DesignerApp.Logon(m_boUser, m_bopass, m_borep, m_boAut)
        End If
        Return DesignerApp
    End Function

    '' Protected creator function to create a UniverseFunctionsTPIde object:
    Protected Overridable Function createUniverseFunctions() As UniverseFunctionsTPIde
        universeFunctions = New UniverseFunctionsTPIde()
        Return universeFunctions
    End Function

End Class
